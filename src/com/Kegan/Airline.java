package com.Kegan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Airline {

    List<Flight> flights = new ArrayList<>();

    private String name;


    public Airline(String name) {
        this.name = name;
    }

    public boolean addFlight(Flight flight){

        return flights.add(flight);

    }



    public double reserveSeats(String seatNumber, String flightNumber){
        int index = flightIndex(flightNumber);
        if(index >= 0) {
            return flights.get(index).reserveSeat(seatNumber);
        }
        return -1;
    }

    public boolean cancelSeat(String seatNumber, String flightNumber){
        int index = flightIndex(flightNumber);
        if(index >= 0) {

            return flights.get(index).cancelSeat(seatNumber);
        }
        return false;
    }

    public double changeSeat(String newSeatNumberChoice, String oldSeatNumber, String flightNumber){
        int index = flightIndex(flightNumber);

        if(index >= 0){return flights.get(index).changeSeat(oldSeatNumber, newSeatNumberChoice);}
        return -1;
    }

    public void printFlight(String flightName){
        int index = flightIndex(flightName);
        flights.get(index).printPlane();
    }

    public Flight searchFlight(String flightName){
        int found = flightIndex(flightName);
        if(found >= 0){
            return flights.get(found);
        }
        return null;
    }

    private int flightIndex(String flightName){
        Collections.sort(flights);
        Flight flight = new Flight(flightName, "", "", "","","", 0.0);
        int found = Collections.binarySearch(flights, flight, null);
        if (found >= 0){
            return found;
        }
        return -1;
    }





    public String getName() {
        return name;
    }
}
