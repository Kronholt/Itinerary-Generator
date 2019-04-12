package com.Kegan;

import java.util.*;

//contains an inner class of Seat - which contain seatNumbers, price, and reserved
//when flight is created it implements createSeats() which populates the plan with Seats
//uses reserveSeats(String) to reserve a seat if it is not booked
// cancelSeat(String) cancels seat if it is reserved
// changeSeat(String) allows cancellation of seat and new seat Selection
//implements compareTo to sort by flightNumber
//Overrides toString() to print relevant flight info


public class Flight implements Comparable<Flight> {

    private String flightNumber;
    private String portOfDeparture;
    private String portOfArrival;
    private String departTime;
    private String arrivalTime;
    private String terminalAndGate;
    private double baseCost = 100;


    List<Seat> seats;

    public Flight(String flightNumber, String portOfDeparture, String portOfArrival, String departTime, String arrivalTime, String terminalAndGate, double baseCost) {
        this.flightNumber = flightNumber;
        this.portOfDeparture = portOfDeparture;
        this.portOfArrival = portOfArrival;
        this.departTime = departTime;
        this.arrivalTime = arrivalTime;
        this.terminalAndGate = terminalAndGate;
        this.baseCost = baseCost;


        this.seats = new ArrayList<>();
        createSeats();


    }//end of Constructor


    public Flight(String flightNumber, String portOfDeparture, String portOfArrival, String departTime, String arrivalTime) {
        this.flightNumber = flightNumber;
        this.portOfDeparture = portOfDeparture;
        this.portOfArrival = portOfArrival;
        this.departTime = departTime;
        this.arrivalTime = arrivalTime;

        this.seats = new ArrayList<>();

        createSeats();
    }

    public double getBaseCost() {
        return baseCost;
    }

    //creates Seat objects to populate the seats ArrayList
    public void createSeats() {
        double price;
        int totalRow = 45;
        int firstClass = 45 / 10;
        int businessClass = (totalRow * 4) / 10;
        int economyClass = totalRow - (firstClass + businessClass);


        for (int row = 1; row <= 45; row++) {
            price = 50.00;
            if (row <= firstClass) {
                int lastSeat = 'F';
                price = 200.00;
                for (char seatNum = 'A'; seatNum <= lastSeat; seatNum++) {
                    if (seatNum != 'B' && seatNum != 'E') {
                        Seat seat = new Seat(String.format("%02d", row) + seatNum, price);
                        //seats.add(seat);
                        seats.add(seat);
                    }
                }
            }// end of loop that creates first Class seats
            if (row > firstClass && row <= businessClass) {
                price = 120.00;
                int lastSeat = 'K';
                for (char seatNum = 'A'; seatNum <= lastSeat; seatNum++) {
                    if (seatNum != 'B' && seatNum != 'I' && seatNum != 'J') {
                        Seat seat = new Seat(String.format("%02d", row) + seatNum, price);
                        //seats.add(seat);
                        seats.add(seat);
                    }
                }

            }// end of statement that creates business class seats
            else if (row > businessClass) {
                int lastSeat = 'K';

                for (char seatNum = 'A'; seatNum <= lastSeat; seatNum++) {
                    if (seatNum != 'I') {
                        Seat seat = new Seat(String.format("%02d", row) + seatNum, price);
                        //seats.add(seat);
                        seats.add(seat);
                    }
                }
            }
        }//end of row creator

    }//end of createSeats()


    public void printSeats() {

        for (Seat seat : this.seats) {

            System.out.println(seat);
        }
    }

    public void printPlane() {


        for (int i = 0; i < 16; i++) {
            if (i % 4 == 0) {
                System.out.println();
                //System.out.print("            ");
            }
            if (i % 2 == 0) {
                System.out.print("  ");
            }
            System.out.print(seats.get(i) + " ");


        }
        System.out.println();
        for (int i = 16; i < 128; i++) {
            if (i % 8 == 0) {
                System.out.println();
                //System.out.print("            ");
            }
            if (i % 2 == 0) {
                System.out.print("  ");
            }
            System.out.print(seats.get(i) + " ");


        }
        System.out.println();
        for (int i = 128; i < seats.size(); i++) {
            if (i % 8 == 0) {
                System.out.println();
                //System.out.print("            ");
            }
            if (i % 2 == 0) {
                System.out.print("  ");
            }
            System.out.print(seats.get(i) + " ");


        }

    }






    // receives a String of the desired Seat name, looks for the Seat in ArrayList and attempts to reserve.
    public double reserveSeat(String seatChoice){

        Seat newSeat = new Seat(seatChoice, 0);
        int foundSeat = Collections.binarySearch(seats, newSeat, null);
        if(foundSeat >= 0){

            return seats.get(foundSeat).reserveSeat();
        }
        System.out.println("Seat selection does not exist.");
        return -1;

    }

    // receives a String of desired Seat name to cancel, looks for Seat in ArrayList and attempts to cancel
    public boolean cancelSeat(String seatChoice){
        Seat newSeat = new Seat(seatChoice, 0);
        int foundSeat = Collections.binarySearch(seats, newSeat, null);
        if(foundSeat >= 0){
            return seats.get(foundSeat).cancelSeat();
        }
        System.out.println("Seat selection does not exist.");
        return false;
    }

    // Will allow user to cancel previously held seat, and book new Seat
    public double changeSeat(String oldSeat, String newSeatChoice){
        boolean quit = false;

            Scanner scanner = new Scanner(System.in);
            Seat newSeat = new Seat(oldSeat, 0);
            int foundSeat = Collections.binarySearch(seats, newSeat, null);
            if (foundSeat >= 0) {
                if (seats.get(foundSeat).cancelSeat()) {

                    return reserveSeat(newSeatChoice) - seats.get(foundSeat).getPrice();
                }
                else {
                    System.out.println("Desired seat is unavailable");
                    return -1;
                }

            }
            System.out.println("Seat selection does not exist.");

        return -1;
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

    public String getTerminalAndGate() {
        return terminalAndGate;
    }

    @Override
    public int compareTo(Flight flight) {
        return this.getFlightNumber().compareTo(flight.getFlightNumber());
    }

    @Override
    public String toString() {
        return "Flight - " + this.flightNumber + "\n" + this.getPortOfDeparture() + " ---> " + this.getPortOfArrival() + "\nDeparture Time: " + this.departTime +
                " Arrival Time: " + this.arrivalTime;
    }





    //inner class creates the Seat objects that are stored in a list in Flight
    //Flight Outer Class will reserve, and cancel Seat objects. Comparable implemented in order to sort Seat() by seatNumber
    public class Seat implements Comparable<Seat>{

        private String seatNumber;
        private boolean isReserved = false;
        private double price;

        public Seat(String seatNumber, double price) {
            this.seatNumber = seatNumber;
            this.price = price;
        }

        //allows the Flight class to reserve a seat from the list. returns false if seat is already booked.
        public double reserveSeat(){
            if(!this.isReserved){
                this.isReserved = true;
                System.out.println();
                System.out.println("Seat reserved.");
                return this.price;
            }
            else {
                System.out.println("Seat unavailable.");
                return -1;
            }
        }

        //allows the seat to be cancelled from the Outer Flight class - if already reserved, cancels seat
        public boolean cancelSeat(){

            if(this.isReserved){
                this.isReserved = false;
                System.out.println("Seat reservation cancelled.");
                return true;
            }
            else{
                System.out.println("No reservation found.");
                return false;
            }

        }

        //implements Comparable to compare the names of seats in order to sort them in Flight
        @Override
        public int compareTo(Seat seat) {
            return this.seatNumber.compareTo(seat.getSeatNumber());
        }

        public String getSeatNumber() {
            return seatNumber;
        }

        public boolean isReserved() {
            return isReserved;
        }

        public double getPrice() {
            return price;
        }

        @Override
        public String toString() {
            if(isReserved){
                return "N/A";
            }
            else {
                return seatNumber;
            }
        }
    }//end of Seat class

}//end of Flight class

