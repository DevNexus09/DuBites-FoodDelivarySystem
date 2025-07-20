package com.foodordersystem.ui.common;

import javax.swing.*;
import java.awt.*;

public class LandingPage extends BaseFrame {

    public LandingPage() {
        super("DuBites", 800, 600);
        initComponents();
    }

    @Override
    protected void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        // Panel to hold the main content
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridwidth = GridBagConstraints.REMAINDER; // Each component on a new line
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Welcome Label
        JLabel welcomeLabel = new JLabel("Welcome to DuBites", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 36));
        mainPanel.add(welcomeLabel, gbc);

        // Buttons
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.PLAIN, 18));
        loginButton.setPreferredSize(new Dimension(200, 50)); // Set a precise size
        loginButton.addActionListener(e -> {
            new RoleSelectionFrame().setVisible(true);
            dispose();
        });

        JButton signUpButton = new JButton("Sign Up");
        signUpButton.setFont(new Font("Arial", Font.PLAIN, 18));
        signUpButton.setPreferredSize(new Dimension(200, 50)); // Set a precise size
        signUpButton.addActionListener(e -> {
            new SignupFrame().setVisible(true);
            dispose();
        });

        mainPanel.add(loginButton, gbc);
        mainPanel.add(signUpButton, gbc);

        // Add the main panel to the frame
        add(mainPanel);
    }
}