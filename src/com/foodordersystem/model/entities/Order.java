package com.foodordersystem.model.entities;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class Order implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String orderId;
    private final List<MenuItem> menuItems;
    private double subTotal;
    private double deliveryCharge;
    private double total;
    private String status;
    private String riderUsername;
    private final String restaurantName;
    private final String restaurantLocation;
    private final String customerUsername;
    private  final String customerName;
    private final String customerAddress;
    private final String customerMobileNumber;
    private final LocalDateTime orderDate;

    public Order(List<MenuItem> menuItems, Restaurant restaurant, User customer) {
        this.orderId = UUID.randomUUID().toString();
        this.menuItems = menuItems;
        this.restaurantName = restaurant.getName();
        this.restaurantLocation = restaurant.getLocation();
        this.customerUsername = customer.getUsername();
        this.customerName = customer.getName();
        this.customerAddress = customer.getAddress();
        this.customerMobileNumber = customer.getMobileNumber(); // Set the mobile number
        this.status = "Placed";
        this.orderDate = LocalDateTime.now();
    }

    public void finalizeQuantities() {
        if (menuItems != null) {
            for (MenuItem item : menuItems) {
                item.syncQuantityFromTextField();
            }
        }
    }

    public void calculateTotals() {
        subTotal = 0.0;
        for (MenuItem item : menuItems) {
            item.syncQuantityFromTextField(); // Ensure quantity is up-to-date
            if (item.isSelected()) {
                subTotal += item.getCost();
            }
        }
        deliveryCharge = 15.00;
        total = subTotal + deliveryCharge;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public double getDeliveryCharge() {
        return deliveryCharge;
    }

    public double getTotal() {
        return total;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRiderUsername() {
        return riderUsername;
    }

    public void setRiderUsername(String riderUsername) {
        this.riderUsername = riderUsername;
    }

    public List<MenuItem> getMenuItems() {
        return menuItems;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public String getRestaurantLocation() {
        return restaurantLocation;
    }

    public String getCustomerUsername() {
        return customerUsername;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public String getCustomerMobileNumber() {
        return customerMobileNumber;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }
}