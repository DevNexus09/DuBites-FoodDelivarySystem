package com.foodordersystem.model.history;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RiderHistory {
    private static final String RIDER_HISTORY_FILE = "rider_history.dat";
    private Map<String, List<DeliveryRecord>> deliveryHistory;

    @SuppressWarnings("unchecked")
    public RiderHistory() {
        deliveryHistory = new HashMap<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(RIDER_HISTORY_FILE))) {
            deliveryHistory = (Map<String, List<DeliveryRecord>>) ois.readObject();
        } catch (FileNotFoundException e) {
            // File does not exist, which is fine
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            // If the file is in an old format or corrupted, start with a fresh history
            System.err.println("Could not load rider history, starting fresh. Error: " + e.getMessage());
            deliveryHistory = new HashMap<>();
        }
    }

    public void saveDelivery(String riderUsername, DeliveryRecord record) {
        deliveryHistory.computeIfAbsent(riderUsername, k -> new ArrayList<>()).add(record);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(RIDER_HISTORY_FILE))) {
            oos.writeObject(deliveryHistory);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<DeliveryRecord> getHistory(String riderUsername) {
        return deliveryHistory.getOrDefault(riderUsername, new ArrayList<>());
    }

    public int getDeliveryCount(String riderUsername) {
        return deliveryHistory.getOrDefault(riderUsername, new ArrayList<>()).size();
    }

    // Inner class to store detailed delivery information
    public static class DeliveryRecord implements Serializable {
        private static final long serialVersionUID = 1L;
        private final String orderId;
        private final String restaurantName;
        private final LocalDateTime deliveryDate;
        private final double earnings;

        public DeliveryRecord(String orderId, String restaurantName, double earnings) {
            this.orderId = orderId;
            this.restaurantName = restaurantName;
            this.deliveryDate = LocalDateTime.now();
            this.earnings = earnings;
        }

        public String getOrderId() { return orderId; }
        public String getRestaurantName() { return restaurantName; }
        public LocalDateTime getDeliveryDate() { return deliveryDate; }
        public double getEarnings() { return earnings; }
    }
}