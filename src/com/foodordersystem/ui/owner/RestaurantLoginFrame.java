package com.foodordersystem.ui.owner;

import com.foodordersystem.database.RestaurantDatabase;
import com.foodordersystem.model.entities.Restaurant;
import com.foodordersystem.model.entities.User;
import com.foodordersystem.ui.common.BaseFrame;
import com.foodordersystem.ui.common.ImagePanel;
import com.foodordersystem.ui.common.RoundedButton;

import javax.swing.*;
import java.awt.*;

public class RestaurantLoginFrame extends BaseFrame {
    private final User owner;
    private final Restaurant restaurant;

    public RestaurantLoginFrame(User owner) {
        super("Manage Restaurant Login", 450, 550);
        this.owner = owner;
        this.restaurant = new RestaurantDatabase().findRestaurantByOwner(owner.getUsername());
        initComponents();
    }

    protected void initComponents() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Use a background panel
        ImagePanel backgroundPanel = new ImagePanel("/com/foodordersystem/Resources/SignUpFrameBg.png", 1.0f);
        backgroundPanel.setLayout(new GridBagLayout());
        setContentPane(backgroundPanel);

        if (restaurant == null) {
            JOptionPane.showMessageDialog(this, "You do not own a restaurant yet. Please create one first.", "Error", JOptionPane.ERROR_MESSAGE);
            new OwnerDashboardFrame(owner).setVisible(true);
            dispose();
            return;
        }

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        // Restaurant Image
        JLabel imageLabel = new JLabel();
        if (restaurant.getImagePath() != null && !restaurant.getImagePath().isEmpty()) {
            try {
                ImageIcon icon = new ImageIcon(new ImageIcon(getClass().getResource(restaurant.getImagePath())).getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH));
                imageLabel.setIcon(icon);
            } catch (Exception e) {
                // If image fails to load, show placeholder text
                imageLabel.setText("Image not found");
                imageLabel.setForeground(Color.WHITE);
            }
        } else {
            imageLabel.setText("No Image");
            imageLabel.setForeground(Color.WHITE);
        }
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(imageLabel, gbc);

        // Restaurant Name
        JLabel nameLabel = new JLabel("Manage: " + restaurant.getName(), SwingConstants.CENTER);
        nameLabel.setFont(new Font("DialogInput", Font.BOLD, 22));
        nameLabel.setForeground(Color.WHITE);
        panel.add(nameLabel, gbc);

        // PIN Label and Field
        JLabel pinLabel = new JLabel("Enter Your 4-Digit Security PIN", SwingConstants.CENTER);
        pinLabel.setForeground(Color.LIGHT_GRAY);
        panel.add(pinLabel, gbc);

        JPasswordField pinField = new JPasswordField(4);
        pinField.setHorizontalAlignment(SwingConstants.CENTER);
        pinField.setFont(new Font("DialogInput", Font.BOLD, 28));
        gbc.insets = new Insets(5, 50, 20, 50);
        panel.add(pinField, gbc);
        gbc.insets = new Insets(10, 10, 10, 10);

        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        buttonPanel.setOpaque(false);
        JButton loginButton = new RoundedButton("Login to Manage");
        JButton backButton = new RoundedButton("Back");
        buttonPanel.add(loginButton);
        buttonPanel.add(backButton);

        gbc.gridwidth = GridBagConstraints.REMAINDER;
        panel.add(buttonPanel, gbc);

        add(panel);

        // Action Listeners
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
    }
}