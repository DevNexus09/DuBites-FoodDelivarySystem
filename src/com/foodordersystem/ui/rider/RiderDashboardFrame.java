package com.foodordersystem.ui.rider;

import com.foodordersystem.database.OrderDatabase;
import com.foodordersystem.model.entities.Order;
import com.foodordersystem.model.history.RiderHistory;
import com.foodordersystem.model.entities.User;
import com.foodordersystem.ui.common.BaseFrame;
import com.foodordersystem.ui.common.ImagePanel;
import com.foodordersystem.ui.common.RoleSelectionFrame;
import com.foodordersystem.ui.common.RoundedButton;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class RiderDashboardFrame extends BaseFrame {
    private final User rider;
    private OrderDatabase orderDatabase;
    private JPanel ordersPanel;
    private final JLabel deliveryCountLabel;

    public RiderDashboardFrame(User rider) {
        super("Rider Dashboard - " + rider.getName(), 900, 700);
        this.rider = rider;
        this.orderDatabase = new OrderDatabase();
        this.deliveryCountLabel = new JLabel("Total Deliveries: 0");
        initComponents();
    }

    protected void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        ImagePanel backgroundPanel = new ImagePanel("/com/foodordersystem/Resources/FoodOrderSystemBg.png", 0.9f);
        backgroundPanel.setLayout(new BorderLayout(10, 10));
        setContentPane(backgroundPanel);

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout(20, 10));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Available Deliveries");
        titleLabel.setFont(new Font("DialogInput", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JPanel rightHeaderPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightHeaderPanel.setOpaque(false);

        deliveryCountLabel.setFont(new Font("Dialog", Font.BOLD, 16));
        deliveryCountLabel.setForeground(Color.WHITE);
        rightHeaderPanel.add(deliveryCountLabel);

        JButton refreshButton = new RoundedButton("Refresh");
        styleHeaderButton(refreshButton);
        refreshButton.addActionListener(e -> refreshOrderList());
        rightHeaderPanel.add(refreshButton);

        JButton logoutButton = new RoundedButton("Logout");
        styleHeaderButton(logoutButton);
        logoutButton.addActionListener(e -> {
            new RoleSelectionFrame().setVisible(true);
            dispose();
        });
        rightHeaderPanel.add(logoutButton);

        headerPanel.add(rightHeaderPanel, BorderLayout.EAST);
        backgroundPanel.add(headerPanel, BorderLayout.NORTH);

        // Orders Panel
        ordersPanel = new JPanel();
        ordersPanel.setOpaque(false);
        ordersPanel.setLayout(new BoxLayout(ordersPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(ordersPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        backgroundPanel.add(scrollPane, BorderLayout.CENTER);

        refreshOrderList();
        updateDeliveryCount();
    }

    public void refreshOrderList() {
        orderDatabase = new OrderDatabase();
        ordersPanel.removeAll();
        List<Order> placedOrders = orderDatabase.getAllOrders();

        if (placedOrders == null) {
            placedOrders = Collections.emptyList();
        }

        List<Order> filteredOrders = placedOrders.stream()
                .filter(Objects::nonNull)
                .filter(o -> "Placed".equalsIgnoreCase(o.getStatus()))
                .collect(Collectors.toList());

        if (filteredOrders.isEmpty()) {
            JLabel noOrdersLabel = new JLabel("No available orders at the moment.");
            noOrdersLabel.setFont(new Font("Dialog", Font.ITALIC, 18));
            noOrdersLabel.setForeground(Color.LIGHT_GRAY);
            noOrdersLabel.setHorizontalAlignment(SwingConstants.CENTER);
            ordersPanel.add(noOrdersLabel);
        } else {
            for (Order order : filteredOrders) {
                ordersPanel.add(new OrderCardPanel(order));
                ordersPanel.add(Box.createRigidArea(new Dimension(0, 15)));
            }
        }
        ordersPanel.revalidate();
        ordersPanel.repaint();
    }


    public void updateDeliveryCount() {
        RiderHistory riderHistory = new RiderHistory();
        int count = riderHistory.getDeliveryCount(rider.getUsername());
        deliveryCountLabel.setText("Total Deliveries: " + count);
    }

    private void styleHeaderButton(JButton button) {
        button.setFont(new Font("Dialog", Font.BOLD, 12));
        button.setBackground(new Color(255, 102, 0));
        button.setForeground(Color.WHITE);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(90, 35));
    }

    private class OrderCardPanel extends JPanel {
        public OrderCardPanel(Order order) {
            setLayout(new BorderLayout(15, 10));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

            // Custom painting for a semi-transparent rounded background
            setOpaque(false);

            Border defaultBorder = BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(255, 255, 255, 70), 2),
                    BorderFactory.createEmptyBorder(15, 20, 15, 20)
            );
            setBorder(defaultBorder);

            // Details Panel
            JPanel detailsPanel = new JPanel(new GridLayout(3, 1, 0, 5));
            detailsPanel.setOpaque(false);

            JLabel fromLabel = new JLabel(String.format("Pickup: %s, %s",
                    order.getRestaurantName() != null ? order.getRestaurantName() : "N/A",
                    order.getRestaurantLocation() != null ? order.getRestaurantLocation() : "N/A"));
            fromLabel.setFont(new Font("Dialog", Font.BOLD, 16));
            fromLabel.setForeground(Color.WHITE);

            JLabel toLabel = new JLabel("Deliver to: " + (order.getCustomerAddress() != null ? order.getCustomerAddress() : "N/A"));
            toLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
            toLabel.setForeground(Color.LIGHT_GRAY);

            JLabel idLabel = new JLabel("ID: " + (order.getOrderId() != null ? order.getOrderId().substring(0, 8).toUpperCase() : "N/A"));
            idLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
            idLabel.setForeground(Color.GRAY);

            detailsPanel.add(fromLabel);
            detailsPanel.add(toLabel);
            detailsPanel.add(idLabel);

            // Pickup Button
            JButton pickupButton = new RoundedButton("View Details");
            pickupButton.setFont(new Font("Dialog", Font.BOLD, 12));
            pickupButton.setBackground(new Color(45, 137, 45));
            pickupButton.setForeground(Color.WHITE);
            pickupButton.setPreferredSize(new Dimension(120, 35));

            pickupButton.addActionListener(e -> {
                new OrderPickupFrame(order, RiderDashboardFrame.this, rider).setVisible(true);
            });

            add(detailsPanel, BorderLayout.CENTER);
            add(pickupButton, BorderLayout.EAST);

        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0, 0, 0, 160));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}