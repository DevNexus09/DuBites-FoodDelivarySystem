package com.foodordersystem.model.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Restaurant implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String name;
    private final String ownerUsername;
    private final String location;
    private final String pin;
    private final int delivaryCharge;
    private List<MenuItem> menu;
    private String imagePath; // Added for restaurant image

    public Restaurant(String name, String ownerUsername, String location, String pin, String imagePath) {
        this.name = name;
        this.ownerUsername = ownerUsername;
        this.location = location;
        this.pin = pin;
        this.delivaryCharge = 15;
        this.menu = new ArrayList<>();
        this.imagePath = imagePath;
    }

    public String getName() { return name; }
    public String getOwnerUsername() { return ownerUsername; }
    public String getLocation() { return location; }
    public String getPin() { return pin; }
    public String getImagePath() { return imagePath; } // Getter for image path

    public int getDelivaryCharge() {
        return delivaryCharge;
    }

    public List<MenuItem> getMenu() { return menu; }
    public void setMenu(List<MenuItem> menu) { this.menu = menu; }
}