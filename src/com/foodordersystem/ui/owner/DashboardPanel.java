package com.foodordersystem.ui.owner;

import com.foodordersystem.model.entities.Order;
import com.foodordersystem.model.entities.Restaurant;
import com.foodordersystem.model.entities.User;
import com.foodordersystem.database.RestaurantDatabase;
import com.foodordersystem.database.UserDatabase;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DashboardPanel extends JPanel {

    private Restaurant restaurant;
    private final JLabel totalOrdersValueLabel;
    private final JLabel totalSalesValueLabel;
    private final JLabel averageOrderValueLabel;
    private final JLabel topSellingItemValueLabel;
    private final DefaultListModel<String> liveOrderModel;

    public DashboardPanel(Restaurant restaurant) {
        this.restaurant = restaurant;
        setLayout(new BorderLayout(20, 20));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setOpaque(false);

        JPanel summaryPanel = new JPanel(new GridLayout(1, 4, 20, 20));
        summaryPanel.setOpaque(false);
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        totalOrdersValueLabel = new JLabel();
        totalSalesValueLabel = new JLabel();
        averageOrderValueLabel = new JLabel();
        topSellingItemValueLabel = new JLabel();

        summaryPanel.add(createSummaryCard("Total Orders", "0", "orders_icon.png", totalOrdersValueLabel));
        summaryPanel.add(createSummaryCard("Total Sales", "Bdt 0.00", "sales_icon.png", totalSalesValueLabel));
        summaryPanel.add(createSummaryCard("Average Order Value", "Bdt 0.00", "avg_order_icon.png", averageOrderValueLabel));
        summaryPanel.add(createSummaryCard("Top Selling Item", "N/A", "top_item_icon.png", topSellingItemValueLabel));

        mainPanel.add(summaryPanel);

        JPanel liveOrderPanel = new JPanel(new BorderLayout(10,10));
        liveOrderPanel.setOpaque(false);
        liveOrderPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.ORANGE),
                "Live Orders",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("DialogInput", Font.BOLD, 18),
                Color.ORANGE
        ));


        liveOrderModel = new DefaultListModel<>();
        JList<String> liveOrderList = new JList<>(liveOrderModel);
        styleOrderList(liveOrderList);

        liveOrderPanel.add(new JScrollPane(liveOrderList), BorderLayout.CENTER);
        mainPanel.add(liveOrderPanel);

        add(mainPanel, BorderLayout.CENTER);

        refreshData(); // Initial data load
    }

    public void refreshData() {
        // Reload the restaurant data from the database
        Restaurant updatedRestaurant = new RestaurantDatabase().findRestaurantByOwner(restaurant.getOwnerUsername());
        if (updatedRestaurant != null) {
            this.restaurant = updatedRestaurant;
        } else {
            // Handle case where restaurant is not found, maybe show an error or empty state
            return;
        }

        List<Order> deliveredOrders = restaurant.getOrders().stream()
                .filter(order -> "Delivered".equalsIgnoreCase(order.getStatus()))
                .collect(Collectors.toList());

        int totalOrders = deliveredOrders.size();
        double totalSales = deliveredOrders.stream().mapToDouble(Order::getTotal).sum();
        double averageOrderValue = totalOrders > 0 ? totalSales / totalOrders : 0;

        totalOrdersValueLabel.setText(String.valueOf(totalOrders));
        totalSalesValueLabel.setText(String.format("Bdt %.2f", totalSales));
        averageOrderValueLabel.setText(String.format("Bdt %.2f", averageOrderValue));
        topSellingItemValueLabel.setText(getTopSellingItem(deliveredOrders));

        updateLiveOrders(liveOrderModel, restaurant.getOrders());
    }

    private JPanel createSummaryCard(String title, String value, String iconName, JLabel valueLabel) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(10, 10));
        card.setBackground(new Color(255, 255, 255, 20));
        Border line = BorderFactory.createLineBorder(new Color(255,255,255,50));
        Border padding = BorderFactory.createEmptyBorder(20, 20, 20, 20);
        card.setBorder(BorderFactory.createCompoundBorder(line, padding));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        valueLabel.setText(value);
        valueLabel.setFont(new Font("Dialog", Font.BOLD, 24));
        valueLabel.setForeground(Color.ORANGE);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private void styleOrderList(JList<String> list) {
        list.setBackground(Color.BLACK);
        list.setForeground(Color.WHITE);
        list.setFont(new Font("Monospaced", Font.PLAIN, 14));
        list.setSelectionBackground(Color.ORANGE);
        list.setSelectionForeground(Color.WHITE);
    }

    private String getTopSellingItem(List<Order> orders) {
        if (orders == null || orders.isEmpty()) {
            return "N/A";
        }
        return orders.stream()
                .flatMap(order -> order.getMenuItems().stream())
                .collect(Collectors.groupingBy(com.foodordersystem.model.entities.MenuItem::getName, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");
    }

    private void updateLiveOrders(DefaultListModel<String> model, List<Order> orders) {
        model.clear();
        if (orders == null || orders.isEmpty()) {
            model.addElement("No live orders at the moment.");
            return;
        }
        UserDatabase userDatabase = new UserDatabase();
        List<Order> liveOrders = orders.stream()
                .filter(o -> !"Delivered".equalsIgnoreCase(o.getStatus()))
                .collect(Collectors.toList());

        if (liveOrders.isEmpty()) {
            model.addElement("No live orders at the moment.");
            return;
        }

        for (Order order : liveOrders) {
            User customer = userDatabase.findUserByUsername(order.getCustomerUsername());
            String customerName = (customer != null) ? customer.getName() : order.getCustomerUsername();
            model.addElement(String.format("ID: %-10s | Customer: %-15s | Total: Bdt %-8.2f | Status: %s",
                    order.getOrderId().substring(0, 8),
                    customerName,
                    order.getTotal(),
                    order.getStatus()));
        }
    }
}