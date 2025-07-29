package com.foodordersystem.ui.rider;

import com.foodordersystem.model.entities.MenuItem;
import com.foodordersystem.model.entities.Order;
import com.foodordersystem.model.entities.User;
import com.foodordersystem.model.history.OrderHistory;
import com.foodordersystem.model.history.RiderHistory;
import com.foodordersystem.ui.common.BaseFrame;
import com.foodordersystem.database.OrderDatabase;

import javax.swing.*;
import java.awt.*;

public class OrderPickupFrame extends BaseFrame {
    private final Order order;
    private final RiderDashboardFrame dashboard;
    private final User rider;

    public OrderPickupFrame(Order order, RiderDashboardFrame dashboard, User rider) {
        super("Order Details - " + order.getOrderId().substring(0,8), 500, 400);
        this.order = order;
        this.dashboard = dashboard;
        this.rider = rider;

        initComponents();
    }

    protected void initComponents() {
        setTitle("Order Details - " + order.getOrderId().substring(0,8));
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JTextArea detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        detailsArea.setText(getOrderDetails());
        add(new JScrollPane(detailsArea), BorderLayout.CENTER);

        JButton deliveredButton = new JButton("Order Delivery Done");
        deliveredButton.addActionListener(e -> deliveredOrder());

        add(deliveredButton, BorderLayout.SOUTH);
    }

    private String getOrderDetails() {
        StringBuilder sb = new StringBuilder();
        sb.append("PICKUP FROM:\n");
        sb.append("--------------------------------\n");
        sb.append("Restaurant: ").append(order.getRestaurantName()).append("\n");
        sb.append("Location:   ").append(order.getRestaurantLocation()).append("\n\n");

        sb.append("DELIVER TO:\n");
        sb.append("--------------------------------\n");
        sb.append("Customer:   ").append(order.getCustomerUsername()).append("\n");
        sb.append("Address:    ").append(order.getCustomerAddress()).append("\n\n");

        sb.append("ORDER ITEMS:\n");
        sb.append("--------------------------------\n");
        for (MenuItem item : order.getMenuItems()) {
            sb.append(String.format("- %-20s (Qty: %s)\n", item.getName(), "1")); // Assuming quantity is 1 for display
        }
        sb.append("\nTOTAL BILL: Rs ").append(String.format("%.2f", order.getTotal()));
        return sb.toString();
    }

    private void deliveredOrder() {
        order.setStatus("Delivered");
        new OrderDatabase().addOrder(order);
        OrderHistory.saveOrder(new OrderHistory(order.getRestaurantName(), order.getTotal()));
        new RiderHistory().saveDelivery(rider.getUsername(), order.getOrderId());

        JOptionPane.showMessageDialog(this, "Order delivery completed and recorded!");

        dashboard.updateDeliveryCount();
        dispose();
    }
}