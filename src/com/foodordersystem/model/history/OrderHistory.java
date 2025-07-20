package com.foodordersystem.model.history;

import javax.swing.*;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class OrderHistory implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String HISTORY_FILE = "order_history.dat";

    private String restaurantName;
    private String date;
    private double total;

    public OrderHistory(String restaurantName, double total) {
        this.restaurantName = restaurantName;
        this.total = total;
        this.date = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    @SuppressWarnings("unchecked")
    public static void saveOrder(OrderHistory order) {
        List<OrderHistory> history = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(HISTORY_FILE))) {
            history = (List<OrderHistory>) ois.readObject();
        } catch (FileNotFoundException e) {
            // File does not exist, which is fine
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        history.add(order);

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(HISTORY_FILE))) {
            oos.writeObject(history);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static void loadOrderHistory(JTextArea textArea, String restaurantName) {
        List<OrderHistory> history = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(HISTORY_FILE))) {
            history = (List<OrderHistory>) ois.readObject();
        } catch (FileNotFoundException e) {
            // No history yet
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        int weeklyOrderCount = 0;
        double weeklySales = 0.0;
        LocalDate oneWeekAgo = LocalDate.now().minusWeeks(1);

        StringBuilder sb = new StringBuilder();
        sb.append("Weekly Sales and Order History:\n\n");

        for (OrderHistory order : history) {
            if (order.restaurantName.equals(restaurantName)) {
                LocalDate orderDate = LocalDate.parse(order.date);
                if (orderDate.isAfter(oneWeekAgo)) {
                    weeklyOrderCount++;
                    weeklySales += order.total;
                    sb.append(String.format("Date: %s, Sale: Rs %.2f\n", order.date, order.total));
                }
            }
        }

        sb.append("\n------------------------------------\n");
        sb.append("Total Weekly Orders: ").append(weeklyOrderCount).append("\n");
        sb.append(String.format("Total Weekly Sales: Rs %.2f\n", weeklySales));

        textArea.setText(sb.toString());
    }
}
