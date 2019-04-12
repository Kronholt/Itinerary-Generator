package com.Kegan;

import java.util.ArrayList;

public class Customer implements Comparable<Customer> {

   private String fullName;
   private String passport;
   private String address;
   private String password;
   private double balance;
   private ArrayList<Trip> trips = new ArrayList<>();

    public Customer(String fullName, String password, String passport, String address) {
        this.fullName = fullName;
        this.password = password;
        this.passport = passport;
        this.address = address;
    }

    public boolean addTrip(Trip trip){
        for(Trip.flightLeg leg: trip.getLegs()){
            leg.setCustomerName(this.getFullName());
        }
        balance += trip.getTotalCost();
        return trips.add(trip);

    }
    //clears all trips that the user has stored
    public void deleteTrips(){

        trips.removeAll(trips);
        this.balance = 0;
    }

    //allows user to delete a single trip
    public boolean deleteSingleTrip(String confirmationNumber){
        for(Trip trip: trips){
           if(trip.getConfirmationNumber().equals(confirmationNumber)){
               this.balance -= trip.getTotalCost();
               return trips.remove(trip);
           }
        }
        return false;

    }
    //allows the user to add the price of seats to their balance
    public void addCost(double cost){
        this.balance += cost;
    }


    public String getPassword() {
        return password;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPassport() {
        return passport;
    }

    public String getAddress() {
        return address;
    }

    public double getBalance() {
        return balance;
    }

    public ArrayList<Trip> getTrips() {
        return trips;
    }

    @Override
    public int compareTo(Customer o) {
        return this.getFullName().compareTo(o.getFullName());
    }
}
