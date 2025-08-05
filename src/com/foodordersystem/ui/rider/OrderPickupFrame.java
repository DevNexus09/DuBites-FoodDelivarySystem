package com.foodordersystem.ui.rider;

import com.foodordersystem.database.OrderDatabase;
import com.foodordersystem.database.RestaurantDatabase;
import com.foodordersystem.model.entities.MenuItem;
import com.foodordersystem.model.entities.Order;
import com.foodordersystem.model.entities.Restaurant;
import com.foodordersystem.model.entities.User;
import com.foodordersystem.model.history.OrderHistory;
import com.foodordersystem.model.history.RiderHistory;
import com.foodordersystem.ui.common.BaseFrame;
import com.foodordersystem.ui.common.ImagePanel;
import com.foodordersystem.ui.common.RoundedButton;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;
import java.util.Optional;

public class OrderPickupFrame extends BaseFrame {
    private final Order order;
    private final RiderDashboardFrame dashboard;
    private final User rider;

    public OrderPickupFrame(Order order, RiderDashboardFrame dashboard, User rider) {
        super("Order Details - " + (order.getOrderId() != null ? order.getOrderId().substring(0, 8).toUpperCase() : "N/A"), 600, 700);
        this.order = order;
        this.dashboard = dashboard;
        this.rider = rider;
        initComponents();
    }

    protected void initComponents() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        ImagePanel backgroundPanel = new ImagePanel("/com/foodordersystem/Resources/CommonBg.png", 1.0f);
        backgroundPanel.setLayout(new BorderLayout(10, 10));
        backgroundPanel.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
        setContentPane(backgroundPanel);


        JPanel mainPanel = new JPanel();
        mainPanel.setOpaque(false);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // Pickup Panel
        mainPanel.add(createSectionPanel("Pickup From", new String[]{
                "Restaurant: " + (order.getRestaurantName() != null ? order.getRestaurantName() : "N/A"),
                "Location: " + (order.getRestaurantLocation() != null ? order.getRestaurantLocation() : "N/A")
        }));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Delivery Panel
        mainPanel.add(createSectionPanel("Deliver To", new String[]{
                "Customer: " + (order.getCustomerName() != null ? order.getCustomerName() : "N/A"),
                "Address: " + (order.getCustomerAddress() != null ? order.getCustomerAddress() : "N/A"),
                "Contact: " + (order.getCustomerMobileNumber() != null ? order.getCustomerMobileNumber() : "N/A")
        }));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Order Items Panel
        mainPanel.add(createOrderItemsPanel());
        mainPanel.add(Box.createVerticalGlue());

        // Total Bill Panel
        mainPanel.add(createTotalBillPanel());

        // Delivered Button
        JButton deliveredButton = new RoundedButton("Confirm Delivery");
        deliveredButton.setFont(new Font("Dialog", Font.BOLD, 16));
        deliveredButton.setBackground(new Color(45, 137, 45));
        deliveredButton.setForeground(Color.WHITE);
        deliveredButton.setPreferredSize(new Dimension(200, 50));
        deliveredButton.addActionListener(e -> deliveredOrder());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(deliveredButton);


        backgroundPanel.add(mainPanel, BorderLayout.CENTER);
        backgroundPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createSectionPanel(String title, String[] details) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(createTitledBorder(title));

        for (String detail : details) {
            JLabel label = new JLabel(detail);
            label.setFont(new Font("Dialog", Font.PLAIN, 16));
            label.setForeground(Color.WHITE);
            label.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
            panel.add(label);
        }
        return panel;
    }

    private JPanel createOrderItemsPanel() {
        JPanel panel = createSectionPanel("Ordered Items", new String[]{});
        DefaultListModel<String> listModel = new DefaultListModel<>();
        if (order.getMenuItems() != null) {
            for (MenuItem item : order.getMenuItems()) {
                if (item != null && item.isSelected()) {
                    listModel.addElement(String.format("%s - [ x%d ]", item.getName(), item.getQuantity()));
                }
            }
        }

        JList<String> itemList = new JList<>(listModel);
        itemList.setOpaque(false);
        ((JComponent)itemList.getCellRenderer()).setOpaque(false);
        itemList.setForeground(Color.WHITE);
        itemList.setFont(new Font("Dialog", Font.PLAIN, 14));
        itemList.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(itemList);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);

        panel.add(scrollPane);
        return panel;
    }


    private JPanel createTotalBillPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        JLabel totalLabel = new JLabel(String.format("Total Bill: Bdt %.2f", order.getTotal()));
        totalLabel.setFont(new Font("Dialog", Font.BOLD, 18));
        totalLabel.setForeground(Color.ORANGE);
        panel.add(totalLabel);
        return panel;
    }

    private TitledBorder createTitledBorder(String title) {
        return BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 100)),
                title,
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("DialogInput", Font.BOLD, 18),
                Color.ORANGE
        );
    }


    private void deliveredOrder() {
        int confirmation = showCustomConfirmDialog(
                "Are you sure you want to mark this order as delivered?",
                "Confirm Delivery",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirmation == JOptionPane.YES_OPTION) {
            order.setStatus("Delivered");
            new OrderDatabase().updateOrder(order);

            // Update the restaurant's order list as well
            RestaurantDatabase restaurantDb = new RestaurantDatabase();
            Restaurant restaurant = restaurantDb.findRestaurantByName(order.getRestaurantName());
            if (restaurant != null) {
                List<Order> orders = restaurant.getOrders();
                Optional<Order> orderToUpdate = orders.stream().filter(o -> o.getOrderId().equals(order.getOrderId())).findFirst();
                orderToUpdate.ifPresent(o -> o.setStatus("Delivered"));
                restaurantDb.addRestaurant(restaurant);
            }

            RiderHistory.DeliveryRecord record = new RiderHistory.DeliveryRecord(order.getOrderId(), order.getRestaurantName(), order.getDeliveryCharge());
            new RiderHistory().saveDelivery(rider.getUsername(), record);


            OrderHistory.saveOrder(new OrderHistory(order.getRestaurantName(), order.getTotal()));

            showCustomMessageDialog(
                    "Order delivery completed and recorded! Great job!",
                    "Delivery Confirmed",
                    JOptionPane.INFORMATION_MESSAGE
            );

            dashboard.refreshOrderList();
            dashboard.updateDeliveryCount();
            dispose();
        }
    }

    private int showCustomConfirmDialog(String message, String title, int optionType, int messageType) {
        UIManager.put("OptionPane.background", new Color(43, 43, 43));
        UIManager.put("Panel.background", new Color(43, 43, 43));
        UIManager.put("OptionPane.messageForeground", Color.WHITE);
        UIManager.put("Button.background", new Color(255, 102, 0));
        UIManager.put("Button.foreground", Color.WHITE);

        int result = JOptionPane.showConfirmDialog(this, message, title, optionType, messageType);

        // Reset to default
        UIManager.put("OptionPane.background", null);
        UIManager.put("Panel.background", null);
        UIManager.put("OptionPane.messageForeground", null);
        UIManager.put("Button.background", null);
        UIManager.put("Button.foreground", null);

        return result;
    }

    private void showCustomMessageDialog(String message, String title, int messageType) {
        UIManager.put("OptionPane.background", new Color(43, 43, 43));
        UIManager.put("Panel.background", new Color(43, 43, 43));
        UIManager.put("OptionPane.messageForeground", Color.WHITE);
        UIManager.put("Button.background", new Color(255, 102, 0));
        UIManager.put("Button.foreground", Color.WHITE);

        JOptionPane.showMessageDialog(this, message, title, messageType);

        // Reset to default
        UIManager.put("OptionPane.background", null);
        UIManager.put("Panel.background", null);
        UIManager.put("OptionPane.messageForeground", null);
        UIManager.put("Button.background", null);
        UIManager.put("Button.foreground", null);
    }
}