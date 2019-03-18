package com.elec390coen.alcoroam.Models;

public class User {
    //basic information
    String id;
    String name;
    String email;
    String password;
    //emergency contact information
    String emergencyContactName;
    String emergencyContactEmail;
    String emergencyContactNumber;

    //**Do not delete this default constructor**//
    //**It is necessary for Database**//
    public User()
    {}

    //constructor - when creating a new user, his emergency contact info is initialized to empty
    public User(String id, String name, String email, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        emergencyContactName="";
        emergencyContactEmail="";
        emergencyContactNumber="";
    }

    //----------------------------------------------------
    //Getter and Setters - used for database operation
    //----------------------------------------------------
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmergencyContactName() {
        return emergencyContactName;
    }

    public void setEmergencyContactName(String emergencyContactName) {
        this.emergencyContactName = emergencyContactName;
    }

    public String getEmergencyContactEmail() {
        return emergencyContactEmail;
    }

    public void setEmergencyContactEmail(String emergencyContactEmail) {
        this.emergencyContactEmail = emergencyContactEmail;
    }

    public String getEmergencyContactNumber() {
        return emergencyContactNumber;
    }

    public void setEmergencyContactNumber(String emergencyContactNumber) {
        this.emergencyContactNumber = emergencyContactNumber;
    }
}
