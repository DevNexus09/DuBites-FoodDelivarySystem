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


        ImagePanel backgroundPanel = new ImagePanel("/com/foodordersystem/Resources/LandingPageBg.png", 1.0f);
        backgroundPanel.setLayout(new GridBagLayout());
        setContentPane(backgroundPanel);


        // Panel to hold the main content
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        buttonPanel.setOpaque(false);

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
        buttonPanel.add(loginButton);
        buttonPanel.add(signUpButton);

        mainPanel.add(buttonPanel, gbc);

        GridBagConstraints mainPanelGbc = new GridBagConstraints();
        mainPanelGbc.anchor = GridBagConstraints.CENTER;
        mainPanelGbc.insets = new Insets(250, 0, 0, 0);
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