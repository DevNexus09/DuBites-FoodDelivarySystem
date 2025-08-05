package com.foodordersystem.database;

import com.foodordersystem.model.entities.Order;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderDatabase {
    private static final String ORDER_FILE = "ordersOnline.dat";
    private List<Order> orders;

    public OrderDatabase() {
        orders = new ArrayList<>();
        loadOrders();
    }

    @SuppressWarnings("unchecked")
    private void loadOrders() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ORDER_FILE))) {
            orders = (List<Order>) ois.readObject();
        } catch (FileNotFoundException e) {
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void saveOrders() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ORDER_FILE))) {
            oos.writeObject(orders);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addOrder(Order order) {
        orders.add(order);
        saveOrders();
    }

    public void updateOrder(Order orderToUpdate) {
        Optional<Order> existingOrderOpt = orders.stream()
                .filter(o -> o.getOrderId().equals(orderToUpdate.getOrderId()))
                .findFirst();

        if (existingOrderOpt.isPresent()) {
            orders.removeIf(o -> o.getOrderId().equals(orderToUpdate.getOrderId()));
            orders.add(orderToUpdate);
        } else {
            orders.add(orderToUpdate);
        }
        saveOrders();
    }


    public void removeOrder(String orderId) {
        orders.removeIf(order -> order.getOrderId().equals(orderId));
        saveOrders();
    }

    public Order findOrderById(String orderId) {
        for (Order order : orders) {
            if (order.getOrderId().equals(orderId)) {
                return order;
            }
        }
        return null;
    }

    public List<Order> getAllOrders() {
        return new ArrayList<>(orders);
    }
}