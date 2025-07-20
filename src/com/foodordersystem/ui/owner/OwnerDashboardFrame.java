package com.foodordersystem.ui.owner;

import com.foodordersystem.model.entities.User;
import com.foodordersystem.ui.common.BaseFrame;
import com.foodordersystem.ui.common.RoleSelectionFrame;

import javax.swing.*;
import java.awt.*;

public class OwnerDashboardFrame extends BaseFrame {
    private final User owner;

    public OwnerDashboardFrame(User owner) {
        super("Owner Dashboard", 800, 600);
        this.owner = owner;
        initComponents();
    }

    protected void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel welcomeLabel = new JLabel("Welcome, " + owner.getName() + "!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JButton createButton = new JButton("Create a New Restaurant");
        createButton.setFont(new Font("Arial", Font.PLAIN, 16));
        createButton.setPreferredSize(new Dimension(300, 50));
        createButton.addActionListener(e -> {
            new RestaurantCreationFrame(owner).setVisible(true);
            dispose();
        });

        JButton manageButton = new JButton("Manage an Existing Restaurant");
        manageButton.setFont(new Font("Arial", Font.PLAIN, 16));
        manageButton.setPreferredSize(new Dimension(300, 50));
        manageButton.addActionListener(e -> {
            new RestaurantLoginFrame(owner).setVisible(true);
            dispose();
        });

        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.PLAIN, 16));
        logoutButton.setPreferredSize(new Dimension(300, 50));
        logoutButton.addActionListener(e -> {
            new RoleSelectionFrame().setVisible(true);
            dispose();
        });

        panel.add(welcomeLabel, gbc);
        panel.add(createButton, gbc);
        panel.add(manageButton, gbc);
        panel.add(logoutButton, gbc);

        add(panel);
    }
}