package com.batcodes.pharmaapplication.model;

public class User {
    private int id;
    private String name;
    private String emailAddress;
    private String mobileNumber;
    private String password;

    public User(String name, String emailAddress, String mobileNumber, String password) {
        this.name = name;
        this.emailAddress = emailAddress;
        this.mobileNumber = mobileNumber;
        this.password = password;
    }

    public User(int id, String name, String emailAddress, String mobileNumber, String password) {
        this.id = id;
        this.name = name;
        this.emailAddress = emailAddress;
        this.mobileNumber = mobileNumber;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
