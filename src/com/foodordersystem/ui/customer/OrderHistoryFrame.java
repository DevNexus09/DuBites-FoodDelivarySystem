package com.foodordersystem.ui.customer;

import com.foodordersystem.database.OrderDatabase;
import com.foodordersystem.model.entities.Order;
import com.foodordersystem.model.entities.User;
import com.foodordersystem.ui.common.BaseFrame;
import com.foodordersystem.ui.common.ImagePanel;
import com.foodordersystem.ui.common.RoundedButton;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class OrderHistoryFrame extends BaseFrame {
    private final User customer;

    public OrderHistoryFrame(User customer) {
        super("Order History", 800, 600);
        this.customer = customer;
        initComponents();
    }

    protected void initComponents() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        ImagePanel backgroundPanel = new ImagePanel("/com/foodordersystem/Resources/ManagementDashboardFrameBg.png", 1.0f);
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);

        JLabel titleLabel = new JLabel("Your Past Orders", SwingConstants.CENTER);
        titleLabel.setFont(new Font("DialogInput", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        backgroundPanel.add(titleLabel, BorderLayout.NORTH);

        List<Order> customerOrders = new OrderDatabase().getAllOrders().stream()
                .filter(order -> order.getCustomerUsername().equals(customer.getUsername()))
                .collect(Collectors.toList());

        if (customerOrders.isEmpty()) {
            JLabel noOrdersLabel = new JLabel("You haven't placed any orders yet.", SwingConstants.CENTER);
            noOrdersLabel.setFont(new Font("Dialog", Font.BOLD, 18));
            noOrdersLabel.setForeground(Color.WHITE);
            backgroundPanel.add(noOrdersLabel, BorderLayout.CENTER);
        } else {
            JPanel ordersPanel = new JPanel();
            ordersPanel.setOpaque(false);
            ordersPanel.setLayout(new BoxLayout(ordersPanel, BoxLayout.Y_AXIS));

            for (Order order : customerOrders) {
                ordersPanel.add(createOrderCard(order));
                ordersPanel.add(Box.createRigidArea(new Dimension(0, 15)));
            }

            JScrollPane scrollPane = new JScrollPane(ordersPanel);
            scrollPane.setOpaque(false);
            scrollPane.getViewport().setOpaque(false);
            scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            backgroundPanel.add(scrollPane, BorderLayout.CENTER);
        }

        JButton backButton = new RoundedButton("Back");
        backButton.addActionListener(e -> {
            new RestaurantSelectionFrame(customer).setVisible(true);
            dispose();
        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        bottomPanel.add(backButton);
        backgroundPanel.add(bottomPanel, BorderLayout.SOUTH);

    }

    private JPanel createOrderCard(Order order) {
        JPanel card = new JPanel(new BorderLayout(15, 10));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 70), 2),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        JLabel restaurantLabel = new JLabel(order.getRestaurantName());
        restaurantLabel.setFont(new Font("Dialog", Font.BOLD, 18));
        restaurantLabel.setForeground(Color.WHITE);

        JLabel totalLabel = new JLabel(String.format("Total: Bdt %.2f", order.getTotal()));
        totalLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
        totalLabel.setForeground(Color.LIGHT_GRAY);

        JPanel detailsPanel = new JPanel();
        detailsPanel.setOpaque(false);
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.add(restaurantLabel);
        detailsPanel.add(totalLabel);

        JButton reorderButton = new RoundedButton("Reorder");
        reorderButton.setBackground(new Color(5, 73, 36));
        reorderButton.setForeground(Color.WHITE);
        reorderButton.addActionListener(e -> {
            new FoodOrderSystem(order).setVisible(true);
            dispose();
        });

        card.add(detailsPanel, BorderLayout.CENTER);
        card.add(reorderButton, BorderLayout.EAST);

        return card;
    }
}