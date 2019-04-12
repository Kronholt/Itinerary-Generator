package com.Kegan;

import java.io.*;
import java.util.HashSet;


public class CustomerDatabase { //this class allows the reading and writing of the customers.txt file which stores user information

    private HashSet<Customer> customerList;


    public CustomerDatabase() {
        customerList = new HashSet<>();
        readFile();


    }



    public void addCustomer(Customer customer) {
        customerList.add(customer);
        //writeFile();
    }



    public Customer searchCustomer(String fullName, String password){
       for(Customer customer: customerList){
           if(customer.getFullName().equals(fullName) && customer.getPassword().equals(password)){
               return customer;
           }
       }
       return null;
    }


    //reads in the customers.txt file and creates customer objects with the data
    //loads all customers into an ArrayList of type Customer
    public void readFile() {

        try (BufferedReader locFile = new BufferedReader(new FileReader("/Users/Skip/Documents/javaPractice/ItineraryGenerator/customers.txt"))) {
            String input;
            while ((input = locFile.readLine()) != null) {
                String[] customerData = input.split(",");

                String name = customerData[0];
                String password = customerData[1];
                String passport = customerData[2];
                String address = customerData[3];

                Customer returningCustomer = new Customer(name, password, passport, address);
                customerList.add(returningCustomer);


            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    public void writeFile() {

        try (BufferedWriter localFile = new BufferedWriter(new FileWriter("/Users/Skip/Documents/javaPractice/ItineraryGenerator/customers.txt"))) {
            for (Customer customer : customerList) {

                String customerInfo = (customer.getFullName() + "," + customer.getPassword() + "," + customer.getPassport() + ","
                        + customer.getAddress() + "\n");

                localFile.write(customerInfo);


            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}











