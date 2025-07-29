package com.foodordersystem.ui.owner;

import com.foodordersystem.model.entities.Order;
import com.foodordersystem.model.entities.Restaurant;
import com.foodordersystem.model.entities.User;
import com.foodordersystem.database.UserDatabase;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DashboardPanel extends JPanel {

    public DashboardPanel(Restaurant restaurant) {
        setLayout(new BorderLayout(20, 20));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setOpaque(false);

        JPanel summaryPanel = new JPanel(new GridLayout(1, 4, 20, 20));
        summaryPanel.setOpaque(false);
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        List<Order> deliveredOrders = restaurant.getOrders().stream()
                .filter(order -> "Delivered".equalsIgnoreCase(order.getStatus()))
                .collect(Collectors.toList());

        int totalOrders = deliveredOrders.size();
        double totalSales = deliveredOrders.stream().mapToDouble(Order::getTotal).sum();
        double averageOrderValue = totalOrders > 0 ? totalSales / totalOrders : 0;

        summaryPanel.add(createSummaryCard("Total Orders", String.valueOf(totalOrders), "orders_icon.png"));
        summaryPanel.add(createSummaryCard("Total Sales", String.format("Bdt %.2f", totalSales), "sales_icon.png"));
        summaryPanel.add(createSummaryCard("Average Order Value", String.format("Bdt %.2f", averageOrderValue), "avg_order_icon.png"));
        summaryPanel.add(createSummaryCard("Top Selling Item", getTopSellingItem(deliveredOrders), "top_item_icon.png"));

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


        DefaultListModel<String> liveOrderModel = new DefaultListModel<>();
        JList<String> liveOrderList = new JList<>(liveOrderModel);
        styleOrderList(liveOrderList);
        updateLiveOrders(liveOrderModel, restaurant.getOrders());

        liveOrderPanel.add(new JScrollPane(liveOrderList), BorderLayout.CENTER);
        mainPanel.add(liveOrderPanel);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createSummaryCard(String title, String value, String iconName) {
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

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Dialog", Font.BOLD, 24));
        valueLabel.setForeground(Color.ORANGE);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private void styleOrderList(JList<String> list) {
        list.setBackground(new Color(255,255,255,220));
        list.setForeground(Color.BLACK);
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
        for (Order order : orders) {
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