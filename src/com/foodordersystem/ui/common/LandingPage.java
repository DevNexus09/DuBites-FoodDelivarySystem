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

        // --- Button Panel ---
        // Create a new panel for the buttons to place them side-by-side
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        buttonPanel.setOpaque(false); // Make the panel transparent

        // Button styling
        Font buttonFont = new Font("DialogInput", Font.BOLD, 18);
        Dimension buttonSize = new Dimension(250, 50);
        Color buttonBgColor = Color.WHITE;
        Color buttonFgColor = Color.BLACK;

        // Buttons
        JButton loginButton = new RoundedButton("Login");
        styleButton(loginButton, buttonFont, buttonSize, buttonBgColor, buttonFgColor);
        loginButton.addActionListener(e -> {
            new RoleSelectionFrame().setVisible(true);
            dispose();
        });

        JButton signUpButton = new RoundedButton("Sign Up");
        styleButton(signUpButton, buttonFont, buttonSize, buttonBgColor, buttonFgColor);
        signUpButton.addActionListener(e -> {
            new SignupFrame().setVisible(true);
            dispose();
        });

        // Add buttons to the button panel
        buttonPanel.add(loginButton);
        buttonPanel.add(signUpButton);

        // Add the button panel to the main panel
        mainPanel.add(buttonPanel, gbc);

        // Add the main panel to the frame, positioned to appear underneath the logo
        GridBagConstraints mainPanelGbc = new GridBagConstraints();
        mainPanelGbc.anchor = GridBagConstraints.CENTER;
        mainPanelGbc.insets = new Insets(250, 0, 0, 0); // Adjust top inset to position below logo
        add(mainPanel, mainPanelGbc);
    }

    private void styleButton(JButton button, Font font, Dimension size, Color bgColor, Color fgColor) {
        button.setFont(font);
        button.setPreferredSize(size);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}