package com.foodordersystem.ui.common;

import javax.swing.*;
import java.awt.*;

public class RoleSelectionFrame extends BaseFrame {

    public RoleSelectionFrame() {
        super("Select Your Role", 800, 600); // Set frame size to 600x600
        initComponents();
    }

    @Override
    protected void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        ImagePanel backgroundPanel = new ImagePanel("/com/foodordersystem/Resources/CommonBg.png", 1.0f);
        backgroundPanel.setLayout(new GridBagLayout());
        setContentPane(backgroundPanel);


        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;


        JLabel titleLabel = new JLabel("Let Us Know Who You Are !");
        titleLabel.setFont(new Font("DialogInput", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        buttonPanel.add(titleLabel, gbc);


        buttonPanel.add(Box.createRigidArea(new Dimension(0, 20)), gbc);

        // Button styling properties
        Font buttonFont = new Font("DialogInput", Font.BOLD, 16);
        Dimension regularButtonSize = new Dimension(120, 50);
        Dimension backButtonSize = new Dimension(100, 40);
        Color buttonColor = new Color(255, 102, 0);
        Color backButtonColor = new Color(77, 76, 76);
        Color buttonTextColor = Color.WHITE;


        JButton customerButton = new RoundedButton("Customer");
        styleButton(customerButton, buttonFont, regularButtonSize, buttonColor, buttonTextColor);
        customerButton.addActionListener(e -> {
            new LoginFrame("Customer").setVisible(true);
            dispose();
        });

        JButton riderButton = new RoundedButton("Rider");
        styleButton(riderButton, buttonFont, regularButtonSize, buttonColor, buttonTextColor);
        riderButton.addActionListener(e -> {
            new LoginFrame("Rider").setVisible(true);
            dispose();
        });

        JButton ownerButton = new RoundedButton("Restaurant Owner");
        styleButton(ownerButton, buttonFont, regularButtonSize, buttonColor, buttonTextColor);
        ownerButton.addActionListener(e -> {
            new LoginFrame("Restaurant Owner").setVisible(true);
            dispose();
        });

        JButton backButton = new RoundedButton("Back");
        styleButton(backButton, buttonFont, backButtonSize, backButtonColor, buttonTextColor);
        backButton.addActionListener(e -> {
            new LandingPage().setVisible(true);
            dispose();
        });

        // Add role buttons to the panel
        buttonPanel.add(customerButton, gbc);
        buttonPanel.add(riderButton, gbc);
        buttonPanel.add(ownerButton, gbc);


        gbc.insets = new Insets(30, 10, 10, 10);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        buttonPanel.add(backButton, gbc);

        add(buttonPanel);
    }

    private void styleButton(JButton button, Font font, Dimension size, Color bgColor, Color fgColor) {
        button.setFont(font);
        button.setPreferredSize(size);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}

