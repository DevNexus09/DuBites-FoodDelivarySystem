package com.foodordersystem.ui.customer;

import com.foodordersystem.database.RestaurantDatabase;
import com.foodordersystem.model.entities.Restaurant;
import com.foodordersystem.model.entities.User;
import com.foodordersystem.ui.common.BaseFrame;
import com.foodordersystem.ui.common.RoleSelectionFrame;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class RestaurantSelectionFrame extends BaseFrame {
    private final List<Restaurant> restaurants;
    private final User customer; // Add customer field

    // Update constructor to accept a User
    public RestaurantSelectionFrame(User customer) {
        super("Select a Restaurant", 800, 600);
        this.customer = customer;
        this.restaurants = new RestaurantDatabase().getAllRestaurants();
        initComponents();
    }

    protected void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        if (restaurants.isEmpty()) {
            JLabel noRestaurantsLabel = new JLabel("No restaurants available yet. Please check back later.", SwingConstants.CENTER);
            add(noRestaurantsLabel, BorderLayout.CENTER);
        } else {
            JPanel restaurantPanel = new JPanel(new GridLayout(0, 3, 10, 10));
            for (Restaurant restaurant : restaurants) {
                JButton restaurantButton = new JButton(restaurant.getName());
                restaurantButton.setPreferredSize(new Dimension(200, 50));
                restaurantButton.addActionListener(e -> {
                    // Pass both restaurant and customer to the order system
                    new FoodOrderSystem(restaurant, customer).setVisible(true);
                    dispose();
                });
                restaurantPanel.add(restaurantButton);
            }
            add(new JScrollPane(restaurantPanel), BorderLayout.CENTER);
        }

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            new RoleSelectionFrame().setVisible(true);
            dispose();
        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(logoutButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }
}