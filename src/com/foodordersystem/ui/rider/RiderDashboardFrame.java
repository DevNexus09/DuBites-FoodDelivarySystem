package com.foodordersystem.ui.rider;

import com.foodordersystem.database.OrderDatabase;
import com.foodordersystem.model.entities.Order;
import com.foodordersystem.model.history.RiderHistory;
import com.foodordersystem.model.entities.User;
import com.foodordersystem.ui.common.BaseFrame;
import com.foodordersystem.ui.common.RoleSelectionFrame;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class RiderDashboardFrame extends BaseFrame {
    private final User rider;
    private OrderDatabase orderDatabase;
    private final DefaultListModel<String> orderListModel;
    private final JList<String> orderJList;
    private final JLabel deliveryCountLabel;

    public RiderDashboardFrame(User rider) {
        super("Rider Dashboard - " + rider.getName(), 800, 600);
        this.rider = rider;
        this.orderDatabase = new OrderDatabase();
        this.orderListModel = new DefaultListModel<>();
        this.orderJList = new JList<>(orderListModel);
        this.deliveryCountLabel = new JLabel("Total Deliveries: 0");

        refreshOrderList();
        updateDeliveryCount();
        initComponents();
    }

    protected void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createTitledBorder("Available Online Orders"));

        mainPanel.add(new JScrollPane(orderJList), BorderLayout.CENTER);

        JButton pickupButton = new JButton("Pick Up Selected Order");
        pickupButton.addActionListener(e -> pickupOrder());

        JButton refreshButton = new JButton("Refresh List");
        refreshButton.addActionListener(e -> refreshOrderList());

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            new RoleSelectionFrame().setVisible(true);
            dispose();
        });

        JPanel bottomPanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(pickupButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(logoutButton);


        bottomPanel.add(buttonPanel, BorderLayout.WEST);
        bottomPanel.add(deliveryCountLabel, BorderLayout.EAST);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void refreshOrderList() {
        orderDatabase = new OrderDatabase();
        orderListModel.clear();
        List<Order> placedOrders = orderDatabase.getAllOrders();
        for (Order order : placedOrders) {
            String listEntry = String.format("ID: %s | Pickup: %s (%s) | Deliver to: %s",
                    order.getOrderId().substring(0, 8),
                    order.getRestaurantName(),
                    order.getRestaurantLocation(),
                    order.getCustomerAddress()
            );
            orderListModel.addElement(listEntry);
        }
    }

    public void updateDeliveryCount() {
        RiderHistory riderHistory = new RiderHistory();
        int count = riderHistory.getDeliveryCount(rider.getUsername());
        deliveryCountLabel.setText("Total Deliveries: " + count);
    }

    private void pickupOrder() {
        String selectedValue = orderJList.getSelectedValue();
        if (selectedValue != null) {
            String orderIdPrefix = selectedValue.split(" ")[1];

            Order orderToPick = null;
            for(Order order : orderDatabase.getAllOrders()){
                if(order.getOrderId().startsWith(orderIdPrefix)){
                    orderToPick = order;
                    break;
                }
            }

            if (orderToPick != null) {
                orderDatabase.removeOrder(orderToPick.getOrderId());
                new OrderPickupFrame(orderToPick, this, rider).setVisible(true);
                refreshOrderList();
            } else {
                JOptionPane.showMessageDialog(this, "Could not find the selected order.", "Error", JOptionPane.ERROR_MESSAGE);
                refreshOrderList();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an order to pick up.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}