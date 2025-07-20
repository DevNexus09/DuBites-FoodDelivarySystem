package com.foodordersystem.ui.owner;

import com.foodordersystem.database.RestaurantDatabase;
import com.foodordersystem.model.entities.Restaurant;
import com.foodordersystem.model.entities.User;
import com.foodordersystem.ui.common.BaseFrame;

import javax.swing.*;
import java.awt.*;

public class RestaurantLoginFrame extends BaseFrame {
    private final User owner;

    public RestaurantLoginFrame(User owner) {
        super("Manage Restaurant Login", 800, 600);
        this.owner = owner;
        initComponents();

    }

    protected void initComponents() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;


        RestaurantDatabase db = new RestaurantDatabase();
        Restaurant restaurant = db.findRestaurantByOwner(owner.getUsername());

        if (restaurant == null) {
            JOptionPane.showMessageDialog(this, "You do not own a restaurant yet. Please create one first.", "Error", JOptionPane.ERROR_MESSAGE);
            new OwnerDashboardFrame(owner).setVisible(true);
            dispose();
            return;
        }

        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Owner Username:"), gbc);
        JTextField usernameField = new JTextField(owner.getUsername(), 20);
        usernameField.setEditable(false);
        gbc.gridx = 1; panel.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("Restaurant PIN:"), gbc);
        JPasswordField pinField = new JPasswordField(20);
        gbc.gridx = 1; panel.add(pinField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton loginButton = new JButton("Login to Manage");
        JButton backButton = new JButton("Back");
        buttonPanel.add(loginButton);
        buttonPanel.add(backButton);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; panel.add(buttonPanel, gbc);


        loginButton.addActionListener(e -> {
            String enteredPin = new String(pinField.getPassword());
            if (restaurant.getPin().equals(enteredPin)) {
                JOptionPane.showMessageDialog(this, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                new ManagementDashboardFrame(restaurant).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid PIN.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        backButton.addActionListener(e -> {
            new OwnerDashboardFrame(owner).setVisible(true);
            dispose();
        });

        add(panel);
    }
}