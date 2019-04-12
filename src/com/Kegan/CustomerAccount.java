package com.Kegan;



import java.util.ArrayList;
import java.util.List;

public class CustomerAccount {


    //right now this is an unused account
    //eventually want to change the main() to implement a login to a CustomerAccount



    public static List<Customer> customers = new ArrayList<>();
    public String accountName;
    public String accountPassword;

    public CustomerAccount(String accountName, String accountPassword) {
        this.accountName = accountName;
        this.accountPassword = accountPassword;
    }

    public boolean changePassword(String oldPassword, String newPassword){

        if(oldPassword.equals(this.accountPassword)){
            System.out.println("Password changed");
            this.accountPassword = newPassword;
            return true;
        }
        else{
            System.out.println("Incorrect password");
            return false;
        }
    }



    public boolean addCustomer(Customer customer){
        return customers.add(customer);
    }


}
