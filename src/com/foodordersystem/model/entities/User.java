package com.foodordersystem.model.entities;

import java.io.Serializable;

public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String dob;
    private String mobileNumber;
    private String username;
    private String password;
    private String address;
    private final String role;

    public User(String name, String dob, String mobileNumber, String username, String password, String address, String role) {
        this.name = name;
        this.dob = dob;
        this.mobileNumber = mobileNumber;
        this.username = username;
        this.password = password;
        this.address = address;
        this.role = role;
    }


    public String getName() { return name; }
    public String getDob() { return dob; }
    public String getMobileNumber() { return mobileNumber; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getAddress() { return address; }
    public String getRole() { return role; }
}