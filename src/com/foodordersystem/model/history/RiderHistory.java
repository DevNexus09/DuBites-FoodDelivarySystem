package com.foodordersystem.model.history;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RiderHistory {
    private static final String RIDER_HISTORY_FILE = "rider_history.dat";
    private Map<String, List<String>> deliveryHistory;

    @SuppressWarnings("unchecked")
    public RiderHistory() {
        deliveryHistory = new HashMap<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(RIDER_HISTORY_FILE))) {
            deliveryHistory = (Map<String, List<String>>) ois.readObject();
        } catch (FileNotFoundException e) {
            // File does not exist, which is fine
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void saveDelivery(String riderUsername, String orderId) {
        deliveryHistory.computeIfAbsent(riderUsername, k -> new ArrayList<>()).add(orderId);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(RIDER_HISTORY_FILE))) {
            oos.writeObject(deliveryHistory);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getDeliveryCount(String riderUsername) {
        return deliveryHistory.getOrDefault(riderUsername, new ArrayList<>()).size();
    }
}