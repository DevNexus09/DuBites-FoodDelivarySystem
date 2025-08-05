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
    private List<Order> orders;
    private List<Review> reviews;
    private int deliveryTime;
    private String priceRange;

    public Restaurant(String name, String ownerUsername, String location, String pin, String imagePath, String cuisine, int deliveryTime, String priceRange) {
        this.name = name;
        this.ownerUsername = ownerUsername;
        this.location = location;
        this.pin = pin;
        this.imagePath = imagePath;
        this.cuisine = cuisine;
        this.delivaryCharge = 15;
        this.menu = new ArrayList<>();
        this.orders = new ArrayList<>();
        this.reviews = new ArrayList<>();
        this.deliveryTime = deliveryTime;
        this.priceRange = priceRange;
    }


    public String getName() { return name; }
    public String getOwnerUsername() { return ownerUsername; }
    public String getLocation() { return location; }
    public String getPin() { return pin; }
    public String getImagePath() { return imagePath; }
    public String getCuisine() { return cuisine; }
    public int getDelivaryCharge() { return delivaryCharge; }
    public List<MenuItem> getMenu() { return menu; }
    public int getDeliveryTime() { return deliveryTime; }
    public String getPriceRange() { return priceRange; }


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

    public List<Review> getReviews() {
        return reviews;
    }

    public void addReview(Review review) {
        if (this.reviews == null) {
            this.reviews = new ArrayList<>();
        }
        this.reviews.add(review);
    }

    public double getAverageRating() {
        if (reviews == null || reviews.isEmpty()) {
            return 0.0;
        }
        double sum = 0;
        for (Review review : reviews) {
            sum += review.getRating();
        }
        return sum / reviews.size();
    }
}