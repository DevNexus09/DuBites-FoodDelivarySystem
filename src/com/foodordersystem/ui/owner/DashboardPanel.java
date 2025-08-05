package com.foodordersystem.ui.owner;

import com.foodordersystem.database.RestaurantDatabase;
import com.foodordersystem.database.UserDatabase;
import com.foodordersystem.model.entities.Order;
import com.foodordersystem.model.entities.Restaurant;
import com.foodordersystem.model.entities.User;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
    private JComboBox<String> dateFilterComboBox;
    private List<Order> allDeliveredOrders;

    public DashboardPanel(Restaurant restaurant) {
        this.restaurant = restaurant;
        setLayout(new BorderLayout(20, 20));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setOpaque(false);

        // Header with date filter
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        headerPanel.setOpaque(false);
        JLabel filterLabel = new JLabel("Filter Data:");
        filterLabel.setForeground(Color.WHITE);
        dateFilterComboBox = new JComboBox<>(new String[]{"All Time", "Today", "This Week", "This Month"});
        dateFilterComboBox.addActionListener(e -> refreshData());
        headerPanel.add(filterLabel);
        headerPanel.add(dateFilterComboBox);
        mainPanel.add(headerPanel);

        JPanel summaryPanel = new JPanel(new GridLayout(1, 4, 20, 20));
        summaryPanel.setOpaque(false);
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        totalOrdersValueLabel = new JLabel();
        totalSalesValueLabel = new JLabel();
        averageOrderValueLabel = new JLabel();
        topSellingItemValueLabel = new JLabel();

        JPanel totalOrdersCard = createSummaryCard("Total Orders", totalOrdersValueLabel);
        totalOrdersCard.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showDetailsDialog("Order Details (" + dateFilterComboBox.getSelectedItem() + ")", getFilteredOrders());
            }
        });

        JPanel totalSalesCard = createSummaryCard("Total Sales", totalSalesValueLabel);
        totalSalesCard.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showDetailsDialog("Sales Details (" + dateFilterComboBox.getSelectedItem() + ")", getFilteredOrders());
            }
        });


        summaryPanel.add(totalOrdersCard);
        summaryPanel.add(totalSalesCard);
        summaryPanel.add(createSummaryCard("Average Order Value", averageOrderValueLabel));
        summaryPanel.add(createSummaryCard("Top Selling Item", topSellingItemValueLabel));

        mainPanel.add(summaryPanel);

        JPanel liveOrderPanel = new JPanel(new BorderLayout(10, 10));
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

        refreshData();
    }

    private List<Order> getFilteredOrders() {
        String filter = (String) dateFilterComboBox.getSelectedItem();
        LocalDate now = LocalDate.now();

        if (allDeliveredOrders == null) {
            return new ArrayList<>();
        }

        return allDeliveredOrders.stream()
                .filter(order -> {
                    if (order.getOrderDate() == null) return "All Time".equals(filter);
                    LocalDate orderDate = order.getOrderDate().toLocalDate();
                    switch (filter) {
                        case "Today":
                            return orderDate.isEqual(now);
                        case "This Week":
                            return !orderDate.isBefore(now.minusWeeks(1));
                        case "This Month":
                            return orderDate.getMonth() == now.getMonth() && orderDate.getYear() == now.getYear();
                        case "All Time":
                        default:
                            return true;
                    }
                })
                .collect(Collectors.toList());
    }

    public void refreshData() {
        Restaurant updatedRestaurant = new RestaurantDatabase().findRestaurantByOwner(restaurant.getOwnerUsername());
        if (updatedRestaurant != null) {
            this.restaurant = updatedRestaurant;
        } else {
            return;
        }

        allDeliveredOrders = restaurant.getOrders().stream()
                .filter(order -> "Delivered".equalsIgnoreCase(order.getStatus()))
                .collect(Collectors.toList());

        List<Order> filteredOrders = getFilteredOrders();

        int totalOrders = filteredOrders.size();
        double totalSales = filteredOrders.stream().mapToDouble(Order::getTotal).sum();
        double averageOrderValue = totalOrders > 0 ? totalSales / totalOrders : 0;

        totalOrdersValueLabel.setText(String.valueOf(totalOrders));
        totalSalesValueLabel.setText(String.format("Bdt %.2f", totalSales));
        averageOrderValueLabel.setText(String.format("Bdt %.2f", averageOrderValue));
        topSellingItemValueLabel.setText(getTopSellingItem(filteredOrders));

        updateLiveOrders(liveOrderModel, restaurant.getOrders());
    }

    private JPanel createSummaryCard(String title, JLabel valueLabel) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(10, 10));
        card.setBackground(new Color(255, 255, 255, 20));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        Border line = BorderFactory.createLineBorder(new Color(255, 255, 255, 50));
        Border padding = BorderFactory.createEmptyBorder(20, 20, 20, 20);
        card.setBorder(BorderFactory.createCompoundBorder(line, padding));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        valueLabel.setFont(new Font("Dialog", Font.BOLD, 24));
        valueLabel.setForeground(Color.ORANGE);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private void showDetailsDialog(String title, List<Order> orders) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), title, true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);

        String[] columnNames = {"Order ID", "Customer", "Total (Bdt)", "Date"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (Order order : orders) {
            Object[] row = {
                    order.getOrderId().substring(0, 8).toUpperCase(),
                    order.getCustomerName(),
                    String.format("%.2f", order.getTotal()),
                    order.getOrderDate() != null ? order.getOrderDate().format(formatter) : "N/A"
            };
            model.addRow(row);
        }

        JTable table = new JTable(model);
        dialog.add(new JScrollPane(table));
        dialog.setVisible(true);
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