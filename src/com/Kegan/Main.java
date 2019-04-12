package com.Kegan;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    //This program allows the user to create a new account or login to an existing account - this information is read in from a txt file
    //Once logged in the user can search for and select trip options

    public static Scanner scanner = new Scanner(System.in);
    public static CustomerDatabase customerInfo = new CustomerDatabase();


    public static ArrayList<Airline> airlines = new ArrayList<>();
    public static Customer currentUser;

    public static void main(String[] args) {

        //loads in the flights
        fillDeltaFlights();
        fillSouthwestFlights();
        fillUnited();


        while(currentUser == null) {
            System.out.println("Welcome to Flight Finder.");
            System.out.println("Are you a returning customer or a first time user?");
            System.out.println("1. Returning");
            System.out.println("2. New Customer");
            System.out.println("Please enter choice: ");
            String choice = scanner.nextLine();

            switch (choice) {

                case "1":
                    //calls the returningCustomer method which reads in the customers.txt file and checks if the customer already exists
                    returningCustomer();
                    if (currentUser == null) {
                        newCustomer();
                    }
                    break;

                case "2":
                    //calls the newCustomer method which allows the user to create a new profile
                    newCustomer();
                    break;

                default:
                    System.out.println("Incorrect entry. Please try again.");
            }
        }

        //flight menu system
        //allows user to book trips, show their balance, change seats, delete every trip, delete individual trips based on confirmation numbers
        boolean quit = false;
        while(!quit) {
            System.out.println();
            System.out.println("Welcome " + currentUser.getFullName());
            System.out.println();
            System.out.println("1. Book new trip");
            System.out.println("2. Display current trips");
            System.out.println("3. Display current balance");
            System.out.println("4. Change seats for selected flight");
            System.out.println("5. Delete all trips.");
            System.out.println("6. Delete single trip.");
            System.out.println("7. Exit program");
            System.out.println();
            System.out.println("Enter choice: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    //gets user input and sends the portOfDeparture and portOfArrival to the tripFinder to build a trip
                    System.out.println("Enter port of departure: ");
                    String portOfDeparture = scanner.nextLine();
                    System.out.println("Enter desired location: ");
                    String portOfArrival = scanner.nextLine();
                    tripFinder(portOfDeparture, portOfArrival);
                    System.out.println();
                    System.out.println("Would you like to make this a roundtrip ticket? Y/N"); //allows the user to order round trip
                    choice = scanner.nextLine();
                    if(choice.toUpperCase().equals("YES") || choice.toUpperCase().equals("Y")){
                        tripFinder(portOfArrival,portOfDeparture); //calls the tripFinder with the locations reversed to build a trip back
                    }
                    break;
                case "2":
                    ArrayList<Trip> currentTrips = currentUser.getTrips(); //prints out all trips that the user has selected
                    if (!currentTrips.isEmpty()) {
                        for (Trip trip : currentTrips) {
                            System.out.println("*********************");
                            trip.printBookedItinerary();
                            System.out.println("*********************");
                        }
                    } else {
                        System.out.println("You have no trips booked!");
                    }
                    break;
                case "3":
                    System.out.println("Current balance: $" + currentUser.getBalance());
                    break;
                case "4":

                    double changeSeatDifference = changeSeats();
                    currentUser.addCost(changeSeatDifference);

                    break;
                case "5":

                    for (Trip trip : currentUser.getTrips()) {
                        for (Trip.flightLeg leg : trip.getLegs()) {
                            String flightNumber = leg.getFlightNumber();
                            String seatNumber = leg.getSeatNum();
                            for (Airline airline : airlines) {
                                airline.cancelSeat(seatNumber, flightNumber);
                            }
                        }
                    }
                    currentUser.deleteTrips();
                    break;
                case "6":
                    System.out.println("Enter confirmation number to delete trip: ");
                    String confirmationNumber = scanner.nextLine();

                    for (Trip trip : currentUser.getTrips()) {
                        if (trip.getConfirmationNumber().equals(confirmationNumber)) {
                            for (Trip.flightLeg leg : trip.getLegs()) {
                                String flightNumber = leg.getFlightNumber();
                                String seatNumber = leg.getSeatNum();
                                for (Airline airline : airlines) {
                                    airline.cancelSeat(seatNumber, flightNumber);


                                }
                            }
                        }
                    }
                    if (currentUser.deleteSingleTrip(confirmationNumber)) {

                        System.out.println("Trip deleted.");

                    } else {
                        System.out.println("Trip not found.");
                    }
                    break;

                case "7":

                    quit = true;
                    customerInfo.writeFile();


                    break;

                default:
                    System.out.println("Incorrect entry. Please try again.");

            }
        }

    }//end of main()




    public static double reserveSeats(Trip trip){
        double cost = 0.0;


            for (Trip.flightLeg leg : trip.getLegs()) {

                while(leg.getSeatNum().equals("")) {
                    System.out.println("Enter desired Seat for flight: " + leg.getFlightNumber());
                    String seatNum = scanner.nextLine();
                    for (Airline airline : airlines) {

                        double tempCost = airline.reserveSeats(seatNum, leg.getFlightNumber());
                        if (tempCost >= 0) {
                            leg.setSeatNum(seatNum);
                            cost += tempCost;

                        }


                    }
                }

            }


        return cost;


    }

    public static double changeSeats(){

        double cost = 0.0;
        System.out.println("Please enter confirmation number: ");
        String confirmationNumber = scanner.nextLine();
        System.out.println("Please enter flight number: ");
        String flightNumber = scanner.nextLine();
        System.out.println("Please enter new seat number: ");
        String seatNumber = scanner.nextLine();
        for(Trip trip: currentUser.getTrips()){
            if(trip.getConfirmationNumber().equals(confirmationNumber)){
                for(Trip.flightLeg leg: trip.getLegs()){
                    if(leg.getFlightNumber().equals(flightNumber)){


                        for(Airline airline: airlines){
                            double temp = airline.changeSeat(seatNumber, leg.getSeatNum(), flightNumber);
                            if(temp != -1){
                                cost += temp;
                                leg.setSeatNum(seatNumber);
                                trip.addCost(cost);
                            }

                        }
                    }

                }
            }

        }
        return cost;
    }

    //this takes in basic info from the customer and checks if there is an existing customer in the data file
    //if a match is found, the stored customer is assigned to currentUser
    //if a match is not found, the user is given the option to try again, or create a new account
    public static void returningCustomer(){
        boolean quit = false;
        while(!quit) {

            System.out.println("Please enter first name");
            String firstName = scanner.nextLine();
            System.out.println("Please enter last name");
            String lastName = scanner.nextLine();
            System.out.println("Please enter password");
            String password = scanner.nextLine();

            Customer tempCustomer = customerInfo.searchCustomer(firstName.toUpperCase() + " " + lastName.toUpperCase(), password);
            if (tempCustomer != null) {
                currentUser = tempCustomer;
                quit = true;

            } else {
                System.out.println("Incorrect name or password.");
                System.out.println();
                System.out.println("Press enter to try again or type 'exit' to create account");
                String choice = scanner.nextLine();
                if(choice.toUpperCase().equals("EXIT")){
                    quit = true;
                }

            }
        }


    }


    //this method gathers basic information from the user and creates a new user
    //when the info is gathered, the customer is assigned to currentUser, and is written into the file
    public static void newCustomer(){
        System.out.println("Please enter first name");
        String firstName = scanner.nextLine();
        System.out.println("Please enter last name");
        String lastName = scanner.nextLine();
        System.out.println("Please enter password");
        String password = scanner.nextLine();
        System.out.println("Please enter passport number");
        String passportNumber = scanner.nextLine();
        System.out.println("Please enter address");
        String address = scanner.nextLine();



        currentUser = new Customer(firstName.toUpperCase() + " " + lastName.toUpperCase(), password, passportNumber, address);
        customerInfo.addCustomer(currentUser);
        //System.out.println(currentUser.getFullName());
    }





    //tripFinder allows the user to enter a port of departure and port of Arrival
    //uses the tripGenerator to create a list of trips options for the user to select

    public static void tripFinder(String portOfDeparture, String portOfArrival){



        ArrayList<Trip> tripOptions = tripGenerator(portOfDeparture,portOfArrival); //sends locations to tripGenerator which provides options
        for(int i = 0; i < tripOptions.size(); i++){
//
            if(i < 10) {
                System.out.println();
                System.out.println("              Option " + (i + 1));
                tripOptions.get(i).printItinerary();
                System.out.println();
                System.out.println("************************************");
            }
        }

        System.out.println();
        System.out.println();
        System.out.println("Choose trip option: ");
        String optionChoice = scanner.nextLine();

        switch(optionChoice){

            case "1":
                if(tripOptions.size() > 0) {
                    double price = reserveSeats(tripOptions.get(0));
                    tripOptions.get(0).addCost(price);

                    currentUser.addTrip(tripOptions.get(0));

                }
                break;
            case "2":
                if(tripOptions.size() > 1) {
                    double price = reserveSeats(tripOptions.get(1));
                    tripOptions.get(1).addCost(price);
                    currentUser.addTrip(tripOptions.get(1));
                }
                break;
            case "3":

                if(tripOptions.size() > 2) {
                    double price = reserveSeats(tripOptions.get(2));
                    tripOptions.get(2).addCost(price);
                    currentUser.addTrip(tripOptions.get(2));
                }
                break;
            case "4":
                if(tripOptions.size() > 3) {
                    double price = reserveSeats(tripOptions.get(3));
                    tripOptions.get(3).addCost(price);
                    currentUser.addTrip(tripOptions.get(3));
                }
                break;
            case "5":
                if(tripOptions.size() > 4) {
                    double price = reserveSeats(tripOptions.get(4));
                    tripOptions.get(4).addCost(price);
                    currentUser.addTrip(tripOptions.get(4));
                }
                break;
            case "6":
                if(tripOptions.size() > 5) {
                    double price = reserveSeats(tripOptions.get(5));
                    tripOptions.get(5).addCost(price);
                    currentUser.addTrip(tripOptions.get(5));
                }
                break;
            case "7":

                if(tripOptions.size() > 6) {
                    double price = reserveSeats(tripOptions.get(6));
                    tripOptions.get(6).addCost(price);
                    currentUser.addTrip(tripOptions.get(6));
                }
                break;
            case "8":
                if(tripOptions.size() > 7) {
                    double price = reserveSeats(tripOptions.get(7));
                    tripOptions.get(7).addCost(price);
                    currentUser.addTrip(tripOptions.get(7));
                }
                break;
            case "9":
                if(tripOptions.size() > 8) {
                    double price = reserveSeats(tripOptions.get(8));
                    tripOptions.get(8).addCost(price);
                    currentUser.addTrip(tripOptions.get(8));
                }
                break;
            case "10":
                if(tripOptions.size() > 9) {
                    double price = reserveSeats(tripOptions.get(9));
                    tripOptions.get(9).addCost(price);
                    currentUser.addTrip(tripOptions.get(9));
                }
                break;

            default:
                System.out.println("Incorrect entry");

        }
    }





    //cycles through the list of flights looking first for a direct flight
    //if not direct flight is found, chains flights backwards based on shared portOfDeparture
    //uses for loops to work backwards and build a connecting flight trip to desiredDestination
    public static ArrayList<Trip> tripGenerator(String portOfDeparture, String desiredDestination){

        ArrayList<Trip> tripOptions = new ArrayList<>();
        ArrayList<Flight> flights = new ArrayList<>();

        for (Airline airline: airlines) {
            for(Flight flight: airline.flights){
                flights.add(flight);
        }


            for(Flight flight: flights){
                //if a direct flight is found from port of departure and desiredlocation
                if(flight.getPortOfDeparture().equals(portOfDeparture) && flight.getPortOfArrival().equals(desiredDestination)){
                    Trip newTrip = new Trip();
                    newTrip.addFlight(flight);
                    tripOptions.add(newTrip);

                }
                //a flight is found that goes to the desiredLocation, but doesn't leave from the desire portOfDeparture.
                //initiated another search through flights to find a flight with an arrival port  matches first flights departure port,
                // and the desired port of Departure


                //uses for loops to chain flights together, builds them into trips, and sends the trip options back to let the user choose a desired trip
                else if(flight.getPortOfArrival().equals(desiredDestination) && !flight.getPortOfDeparture().equals(portOfDeparture)) {



                        for (Flight connectingFlight : flights) {

                            if (flight.getPortOfDeparture().equals(connectingFlight.getPortOfArrival())
                                    && connectingFlight.getPortOfDeparture().equals(portOfDeparture)) {
                                Trip newTrip = new Trip();
                                newTrip.addFlight(connectingFlight);
                                newTrip.addFlight(flight);
                                if (newTrip.layoverTime(newTrip.getLegs().get(0), newTrip.getLegs().get(1)) != null) {
                                    tripOptions.add(newTrip);
                                }


                            }
                            if (flight.getPortOfDeparture().equals(connectingFlight.getPortOfArrival())
                                    && !connectingFlight.getPortOfDeparture().equals(portOfDeparture)) {


                                for (Flight anotherConnectingFlight : flights) {

                                    if (connectingFlight.getPortOfDeparture().equals(anotherConnectingFlight.getPortOfArrival())
                                            && anotherConnectingFlight.getPortOfDeparture().equals(portOfDeparture)) {

                                        Trip newTrip = new Trip();
                                        newTrip.addFlight(anotherConnectingFlight);
                                        newTrip.addFlight(connectingFlight);
                                        newTrip.addFlight(flight);
                                        if ((newTrip.layoverTime(newTrip.getLegs().get(0), newTrip.getLegs().get(1))) != null
                                                && (newTrip.layoverTime(newTrip.getLegs().get(1), newTrip.getLegs().get(2)) != null)) {

                                            tripOptions.add(newTrip);
                                        }


                                    }

                                }
                            }


                        }


                    }


            }

        }

        return tripOptions;
    }





    //Ideally I would put these flights into a database, right now I am just loading sample flights to test functionality.
    //Loads random flights into the delta Airline object
    public static void fillDeltaFlights(){

        Airline delta = new Airline("Delta Airlines");



        delta.addFlight(new Flight("DL7475","LAX","SEA","01/22/19 09:00AM","01/22/19 2:00PM"));
        delta.addFlight( new Flight("DL408","SEA","JFK","01/23/19 02:45PM","01/23/19 6:00PM"));
        delta.addFlight( new Flight("DL0034","JFK","CAN","01/23/19 10:00PM","01/23/19 1:00AM"));
        delta.addFlight( new Flight("DL0541","GEG","JFK","01/23/19 10:00PM","01/23/19 1:00AM"));
        delta.addFlight( new Flight("DL9971","SEA","LAX","01/23/19 10:00PM","01/23/19 1:00AM"));
        delta.addFlight( new Flight("DL9971","SEA","LAX","01/23/19 10:00PM","01/23/19 1:00AM"));

        delta.addFlight(new Flight("DL1004","JAX","JFK","01/22/19 11:39AM","01/22/19 1:22PM"));
        delta.addFlight( new Flight("DL1056","RDU","FLL","01/23/19 12:12PM","01/23/19 1:55PM"));
        delta.addFlight( new Flight("DL107","FRA","JFK","01/23/19 10:55AM","01/23/19 12:42PM"));
        delta.addFlight( new Flight("DL1083","ATL","MCO","01/23/19 12:02PM","01/23/19 1:02PM"));
        delta.addFlight( new Flight("DL1087","MAD","ATL","01/23/19 1:20PM","01/23/19 4:42PM"));
        delta.addFlight( new Flight("DL1094","JFK","FLL","01/23/19 11:46AM","01/23/19 2:17PM"));

        delta.addFlight(new Flight("DL1097","JFK","AUS","01/22/19 09:41AM","01/22/19 12:06PM"));
        delta.addFlight( new Flight("DL408","LHR","MSP","01/23/19 11:29PM","01/23/19 3:24PM"));
        delta.addFlight( new Flight("DL1102","FLL","ATL","01/23/19 11:03AM","01/23/19 12:25AM"));
        delta.addFlight( new Flight("DL1115","SLC","BWI","01/23/19 10:02AM","01/23/19 3:38PM"));
        delta.addFlight( new Flight("DL1118","ATL","FLL","01/23/19 11:28AM","01/23/19 12:54PM"));
        delta.addFlight( new Flight("DL1127","FLL","ATL","01/23/19 12:10PM","01/23/19 1:41PM"));

        delta.addFlight(new Flight("DL117","STR","ATL","01/22/19 10:48AM","01/22/19 2:19PM"));
        delta.addFlight( new Flight("DL1175","SLC","ORD","01/23/19 10:15AM","01/23/19 1:47PM"));
        delta.addFlight( new Flight("DL1189","SLC","LAX","01/23/19 9:40AM","01/23/19 9:58AM"));
        delta.addFlight( new Flight("DL1192","SEA","CUN","01/23/19 8:41AM","01/23/19 3:23AM"));
        delta.addFlight( new Flight("DL1194","SLC","PDX","01/23/19 8:40AM","01/23/19 9:17AM"));
        delta.addFlight( new Flight("DL1196","LAX","OGG","01/23/19 8:31AM","01/23/19 10:51AM"));

//
//
//

        airlines.add(delta);
    }

    public static void fillSouthwestFlights(){

        Airline southWest = new Airline("Southwest Airlines");



        southWest.addFlight(new Flight("SW1002","PEK","CAN","01/22/19 09:00AM","01/22/19 2:00PM"));
        southWest.addFlight( new Flight("SW1003","PEK","JFK","01/23/19 02:45PM","01/23/19 6:00PM"));
        southWest.addFlight( new Flight("SW1004","PEK","SEA","01/23/19 10:00PM","01/23/19 1:00AM"));
        southWest.addFlight( new Flight("SW1005","PEK","BKK","01/23/19 10:00PM","01/23/19 1:00AM"));
        southWest.addFlight( new Flight("SW1006","SEA","ORD","01/23/19 10:00PM","01/23/19 1:00AM"));
        southWest.addFlight( new Flight("SW1007","ATL","ORD","01/23/19 10:00PM","01/23/19 1:00AM"));

        southWest.addFlight(new Flight("SW1008","SEA","HNL","01/22/19 11:39AM","01/22/19 1:22PM"));
        southWest.addFlight( new Flight("SW1009","BOS","FLL","01/23/19 12:12PM","01/23/19 1:55PM"));
        southWest.addFlight( new Flight("SW1010","PHX","ATL","01/23/19 10:55AM","01/23/19 12:42PM"));
        southWest.addFlight( new Flight("SW1011","BOS","MSP","01/23/19 12:02PM","01/23/19 1:02PM"));
        southWest.addFlight( new Flight("SW1012","SEA","MSP","01/23/19 1:20PM","01/23/19 4:42PM"));
        southWest.addFlight( new Flight("SW1013","SEA","PHX","01/23/19 11:46AM","01/23/19 2:17PM"));

        southWest.addFlight(new Flight("SW1014","HNL","SEA","01/22/19 09:41AM","01/22/19 12:06PM"));
        southWest.addFlight( new Flight("SW1015","FLL","BOS","01/23/19 11:29PM","01/23/19 3:24PM"));
        southWest.addFlight( new Flight("SW1016","ATL","PHX","01/23/19 11:03AM","01/23/19 12:25AM"));
        southWest.addFlight( new Flight("SW1017","MSP","BOS","01/23/19 10:02AM","01/23/19 3:38PM"));
        southWest.addFlight( new Flight("SW1018","MSP","SEA","01/23/19 11:28AM","01/23/19 12:54PM"));
        southWest.addFlight( new Flight("SW1019","PHX","SEA","01/23/19 12:10PM","01/23/19 1:41PM"));

        southWest.addFlight(new Flight("SW1020","CAN","PEK","01/22/19 10:48AM","01/22/19 2:19PM"));
        southWest.addFlight( new Flight("SW1021","SEA","PEK","01/23/19 10:15AM","01/23/19 1:47PM"));
        southWest.addFlight( new Flight("SW1022","JFK","PEK","01/23/19 9:40AM","01/23/19 9:58AM"));
        southWest.addFlight( new Flight("SW1023","BKK","PEK","01/23/19 8:41AM","01/23/19 3:23AM"));
        southWest.addFlight( new Flight("SW1024","ORD","SEA","01/23/19 8:40AM","01/23/19 9:17AM"));
        southWest.addFlight( new Flight("SW1025","ORD","ATL","01/23/19 8:31AM","01/23/19 10:51AM"));

//
//
//

        airlines.add(southWest);
    }




    //Ideally I would put these flights into a database, right now I am just loading sample flights to test functionality.
    //loads flights into the united Airline object
    public static void fillUnited(){


        Airline united = new Airline("United Airlines");

        united.addFlight(new Flight("UN7475","LAX","ATL","01/22/19 09:00AM","01/22/19 2:00PM"));
        united.addFlight( new Flight("UN408","SEA","CUN","01/23/19 02:45PM","01/23/19 6:00PM"));
        united.addFlight( new Flight("UN0034","JFK","ATL","01/23/19 10:00PM","01/23/19 1:00AM"));
        united.addFlight( new Flight("UN0541","ATL","SEA","01/23/19 10:00PM","01/23/19 1:00AM"));
        united.addFlight( new Flight("UN9971","SEA","LAX","01/23/19 10:00PM","01/23/19 1:00AM"));

        united.addFlight(new Flight("UN7475","LAX","SEA","01/22/19 09:00AM","01/22/19 2:00PM"));
        united.addFlight( new Flight("UN408","SEA","JFK","01/23/19 02:45PM","01/23/19 6:00PM"));
        united.addFlight( new Flight("UN0034","JFK","CAN","01/23/19 10:00PM","01/23/19 1:00AM"));
        united.addFlight( new Flight("UN0541","GEG","JFK","01/23/19 10:00PM","01/23/19 1:00AM"));
        united.addFlight( new Flight("UN9971","SEA","LAX","01/23/19 10:00PM","01/23/19 1:00AM"));
        united.addFlight( new Flight("UN9971","SEA","LAX","01/23/19 10:00PM","01/23/19 1:00AM"));

        united.addFlight(new Flight("UN1004","JAX","JFK","01/22/19 11:39AM","01/22/19 1:22PM"));
        united.addFlight( new Flight("UN1056","RDU","FLL","01/23/19 12:12PM","01/23/19 1:55PM"));
        united.addFlight( new Flight("UN107","FRA","JFK","01/23/19 10:55AM","01/23/19 12:42PM"));
        united.addFlight( new Flight("UN1083","ATL","MCO","01/23/19 12:02PM","01/23/19 1:02PM"));
        united.addFlight( new Flight("UN1087","SEA","GEG","01/23/19 1:20PM","01/23/19 4:42PM"));
        united.addFlight( new Flight("UN1094","JFK","FLL","01/23/19 11:46AM","01/23/19 2:17PM"));

        united.addFlight(new Flight("UN1097","JFK","AUS","01/22/19 09:41AM","01/22/19 12:06PM"));
        united.addFlight( new Flight("UN408","LHR","MSP","01/23/19 11:29PM","01/23/19 3:24PM"));
        united.addFlight( new Flight("UN1102","FLL","ATL","01/23/19 11:03AM","01/23/19 12:25AM"));
        united.addFlight( new Flight("UN1115","SLC","BWI","01/23/19 10:02AM","01/23/19 3:38PM"));
        united.addFlight( new Flight("UN1118","ATL","FLL","01/23/19 11:28AM","01/23/19 12:54PM"));
        united.addFlight( new Flight("UN1127","FLL","ATL","01/23/19 12:10PM","01/23/19 1:41PM"));

        united.addFlight(new Flight("UN117","STR","ATL","01/22/19 10:48AM","01/22/19 2:19PM"));
        united.addFlight( new Flight("UN1175","SLC","ORD","01/23/19 10:15AM","01/23/19 1:47PM"));
        united.addFlight( new Flight("UN1189","CUN","SEA","01/23/19 9:40AM","01/23/19 9:58AM"));
        united.addFlight( new Flight("UN1192","SEA","CUN","01/23/19 8:41AM","01/23/19 3:23AM"));
        united.addFlight( new Flight("UN1194","GEG","SEA","01/23/19 8:40AM","01/23/19 9:17AM"));
        united.addFlight( new Flight("UN1196","LAX","OGG","01/23/19 8:31AM","01/23/19 10:51AM"));

        airlines.add(united);
    }


}
