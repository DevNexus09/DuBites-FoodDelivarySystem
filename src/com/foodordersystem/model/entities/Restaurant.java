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
    private final String cuisine;
    private final int delivaryCharge;
    private List<MenuItem> menu;
    private String imagePath;
    private List<Order> orders; // Added to track orders

    public Restaurant(String name, String ownerUsername, String location, String pin, String imagePath, String cuisine) {
        this.name = name;
        this.ownerUsername = ownerUsername;
        this.location = location;
        this.pin = pin;
        this.imagePath = imagePath;
        this.cuisine = cuisine;
        this.delivaryCharge = 15;
        this.menu = new ArrayList<>();
        this.orders = new ArrayList<>(); // Initialize the orders list
    }

    // Existing getters...
    public String getName() { return name; }
    public String getOwnerUsername() { return ownerUsername; }
    public String getLocation() { return location; }
    public String getPin() { return pin; }
    public String getImagePath() { return imagePath; }
    public String getCuisine() { return cuisine; }
    public int getDelivaryCharge() { return delivaryCharge; }
    public List<MenuItem> getMenu() { return menu; }

    // New methods for orders
    public List<Order> getOrders() {
        return orders;
    }

    public void addOrder(Order order) {
        if (this.orders == null) {
            this.orders = new ArrayList<>();
        }
        this.orders.add(order);
    }

    public void setMenu(List<MenuItem> menu) { this.menu = menu; }
}