package com.foodordersystem.ui.common;

import javax.swing.*;
import javax.swing.border.Border;
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

        // Set a custom panel with a background image as the content pane
        ImagePanel backgroundPanel = new ImagePanel("/com/foodordersystem/Resources/LandingPageBg.png", 1.0f);
        backgroundPanel.setLayout(new GridBagLayout()); // Center the content
        setContentPane(backgroundPanel);


        // Panel to hold the main content
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        // Make the panel's background transparent to show the frame's background
        mainPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;

        // Welcome Label
        JLabel welcomeLabel = new JLabel("Welcome to DuBites", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("DialogInput", Font.BOLD, 24));
        // Set the text color to white to be visible on the dark background
        welcomeLabel.setForeground(Color.WHITE);
        mainPanel.add(welcomeLabel, gbc);

        // Add some space between the welcome label and buttons
        mainPanel.add(Box.createRigidArea(new Dimension(0, 8)), gbc);

        // --- Button Panel ---
        // Create a new panel for the buttons to place them side-by-side
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false); // Make the panel transparent

        // Button styling
        Font buttonFont = new Font("DialogInput", Font.BOLD, 18);
        Dimension buttonSize = new Dimension(150, 50); // Adjusted size for side-by-side layout
        Color buttonBgColor = Color.BLACK;
        Color buttonFgColor = Color.WHITE;

        // Buttons
        JButton loginButton = new JButton("Login");
        styleButton(loginButton, buttonFont, buttonSize, buttonBgColor, buttonFgColor, 1);
        loginButton.addActionListener(e -> {
            new RoleSelectionFrame().setVisible(true);
            dispose();
        });

        JButton signUpButton = new JButton("Sign Up");
        styleButton(signUpButton, buttonFont, buttonSize, buttonBgColor, buttonFgColor, 1);
        signUpButton.addActionListener(e -> {
            new SignupFrame().setVisible(true);
            dispose();
        });

        // Add buttons to the button panel
        buttonPanel.add(loginButton);
        buttonPanel.add(signUpButton);

        // Add the button panel to the main panel
        mainPanel.add(buttonPanel, gbc);

        // Add the main panel to the frame, positioned at the bottom center
        GridBagConstraints mainPanelGbc = new GridBagConstraints();
        mainPanelGbc.anchor = GridBagConstraints.SOUTH;
        mainPanelGbc.weighty = 1.0; // Push the panel to the bottom
        mainPanelGbc.insets = new Insets(0, 0, 50, 0); // Add some padding from the bottom
        add(mainPanel, mainPanelGbc);
    }

    private void styleButton(JButton button, Font font, Dimension size, Color bgColor, Color fgColor, int thickness) {
        button.setFont(font);
        button.setPreferredSize(size);
        button.setForeground(fgColor);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, thickness));
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}