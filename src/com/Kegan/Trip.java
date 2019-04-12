package com.Kegan;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Trip {

    private double totalCost = 0.0;
    private String confirmationNumber;
    private List<flightLeg> legs = new ArrayList<>();



    //constructor - generates a confirmation code at creation of trip
    public Trip() {
        this.confirmationNumber = generateID();
    }



    //generates a random comfirmation code to be used to identify the trip
    public String generateID() {

        int leftLimit = 1;
        int rightLimit = 9;
        int targetStringLength = 4;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));


                buffer.append(randomLimitedInt);

        }
        String generatedString = buffer.toString();

        return generatedString;
    }



    //allows a flight object to be sent and added as a leg of the trip
    public void addFlight(Flight flight){
         addLeg(flight);

    }

    public void addCost(double cost){
        this.totalCost += cost;

    }


    //takes a flight and adds the required information to make it into a leg object
    private void addLeg(Flight flight){
        flightLeg newLeg = new flightLeg(flight.getFlightNumber(),flight.getPortOfDeparture(),
                flight.getPortOfArrival(), flight.getDepartTime(), flight.getArrivalTime(),flight.getBaseCost());
        //System.out.println(flight.getBaseCost());
        this.totalCost += newLeg.getPrice();
        legs.add(newLeg);
    }







    //used by the tripGenerator
    //generates general itineraries for the user to select from
    public void printItinerary(){

        for(int i = 0; i < legs.size(); i ++){
            System.out.println();
            System.out.println(legs.get(i));
            if(i < legs.size() - 1){
                String layover = layoverTime(legs.get(i), legs.get(i + 1));
                if(layover != null){System.out.println(layover);}
                else{
                    legs.get(i).setFlightNumber(null);
                }
            }



        }
        System.out.println();
        System.out.println("Total trip cost: " + this.totalCost);
    }




    //prints the itinerary with confirmation code after the user has booked the trip.
    public void printBookedItinerary(){

        for(int i = 0; i < legs.size(); i ++){
            System.out.println();
            System.out.println(legs.get(i));
            if(i < legs.size() - 1){
                String layover = layoverTime(legs.get(i), legs.get(i + 1));
                if(layover != null){System.out.println(layover);}
                else{
                    legs.get(i).setFlightNumber(null);
                }
            }
            if(!legs.isEmpty()) {
                System.out.println("Confirmation number: " + this.confirmationNumber);

            }



        }
        System.out.println();
       System.out.println("Total trip cost: " + this.totalCost);
    }



    //calculates the layover time between two flights
    public String layoverTime(flightLeg firstLeg, flightLeg secondLeg){

        String startTime = firstLeg.getArrivalTime();
        String endTime = secondLeg.getDepartTime();

        SimpleDateFormat sdf = new SimpleDateFormat("mm/dd/yy hh:mma");
        try {

            Date d1 = sdf.parse(startTime);

            Date d2 = sdf.parse(endTime);

            long elapsed = d2.getTime() - d1.getTime();
            //System.out.println(elapsed);

            int days = (int) Math.floor(elapsed / (24 * 60 * 60 * 1000));

            int hours = (int) Math.floor(elapsed / 3600000);


            int minutes = (int) Math.floor((elapsed - hours * 3600000) / 60000);


            if(elapsed <= 0){
                return null;
            }
            if(days == 0) {

                String layover = "Layover Time: %d hour(s) %d minute(s)\n";
                return String.format(layover, hours, minutes);
            }
            else {
                String layoverWithDays = "Layover Time: %d day(s) %d hour(s) %d minute(s)\n";
                return String.format(layoverWithDays, days, hours, minutes);
            }


        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;

    }//end of layoverTime


    //getters

    public double getTotalCost() {
        return totalCost;
    }

    public List<flightLeg> getLegs() {
        return legs;
    }
    public String getConfirmationNumber() {
        return confirmationNumber;
    }








    public class flightLeg {
        private String flightNumber;
        private String portOfDeparture;
        private String portOfArrival;
        private String departTime;
        private String arrivalTime;
        private String seatNum = "";
        private double price;
        private String customerName = "";
        private String tripID = "";





        public void setFlightNumber(String flightNumber) {
            this.flightNumber = flightNumber;
        }

        public flightLeg(String flightNumber, String portOfDeparture,
                         String portOfArrival, String departTime, String arrivalTime,
                          double price){

            this.flightNumber = flightNumber;
            this.portOfDeparture = portOfDeparture;
            this.portOfArrival = portOfArrival;
            this.departTime = departTime;
            this.arrivalTime = arrivalTime;
            this.price = price;

        }


        public void setSeatNum(String seatNum) {
            this.seatNum = seatNum;
        }

        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }

        public String getFlightNumber() {
            return flightNumber;
        }

        public String getPortOfDeparture() {
            return portOfDeparture;
        }

        public String getPortOfArrival() {
            return portOfArrival;
        }

        public String getDepartTime() {
            return departTime;
        }

        public String getArrivalTime() {
            return arrivalTime;
        }


        public String getSeatNum() {
            return seatNum;
        }

        public double getPrice() {
            return price;
        }

        public String getCustomerName() {
            return customerName;
        }

        public String getTripID() {
            return tripID;
        }

        @Override
        public String toString() {

            if(!customerName.equals("")) {
                return "Flight - " + this.flightNumber + "    " + this.customerName + " seat number: " + this.seatNum + "\n" + this.getPortOfDeparture() + " ---> " + this.getPortOfArrival() + "\nDeparture Time: " + this.departTime +
                        " Arrival Time: " + this.arrivalTime;
            }
            else {
                return "Flight - " + this.flightNumber + "\n" + this.getPortOfDeparture() + " ---> " + this.getPortOfArrival() + "\nDeparture Time: " + this.departTime +
                        " Arrival Time: " + this.arrivalTime;
            }
        }
    } //end of flightLeg




}





