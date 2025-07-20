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
        setLayout(new GridBagLayout());
        getContentPane().setBackground(Color.WHITE); // Set a white background

        // Panel to hold the main content
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE); // Match frame background
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        // Add and resize the logo
        ImageIcon logoIcon = new ImageIcon(getClass().getResource("/com/foodordersystem/Resources/Logo.png"));
        Image image = logoIcon.getImage();
        Image newimg = image.getScaledInstance(265,265,  java.awt.Image.SCALE_SMOOTH);
        logoIcon = new ImageIcon(newimg);
        JLabel logoLabel = new JLabel(logoIcon);
        mainPanel.add(logoLabel, gbc);


        // Welcome Label
        JLabel welcomeLabel = new JLabel("Welcome to DuBites", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("DialogInput", Font.BOLD, 24));
        mainPanel.add(welcomeLabel, gbc);

        // Add some space between the welcome label and buttons
        mainPanel.add(Box.createRigidArea(new Dimension(0, 8)), gbc);

        // Button styling
        Font buttonFont = new Font("DialogInput", Font.BOLD, 18);
        Dimension buttonSize = new Dimension(220, 50);
        Color buttonBorderColor = new Color(255, 102, 0); // Orange color for the border
        Color buttonTextColor = new Color(255, 102, 0); // Orange color for the text


        // Buttons
        JButton loginButton = new JButton("Login");
        styleButton(loginButton, buttonFont, buttonSize, buttonBorderColor, buttonTextColor, 3); // Added thickness
        loginButton.addActionListener(e -> {
            new RoleSelectionFrame().setVisible(true);
            dispose();
        });

        JButton signUpButton = new JButton("Sign Up");
        styleButton(signUpButton, buttonFont, buttonSize, buttonBorderColor, buttonTextColor, 3); // Added thickness
        signUpButton.addActionListener(e -> {
            new SignupFrame().setVisible(true);
            dispose();
        });

        mainPanel.add(loginButton, gbc);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 0)), gbc); // Space between buttons
        mainPanel.add(signUpButton, gbc);

        // Add the main panel to the frame
        add(mainPanel);
    }

    private void styleButton(JButton button, Font font, Dimension size, Color borderColor, Color fgColor, int thickness) {
        button.setFont(font);
        button.setPreferredSize(size);
        button.setForeground(fgColor);
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(new RoundEdgedBorder(borderColor, 20, thickness)); // Use a custom rounded border with thickness
        button.setContentAreaFilled(false);
        button.setOpaque(true);
    }
}

class RoundEdgedBorder implements Border {
    private final Color color;
    private final int radius;
    private final int thickness;

    public RoundEdgedBorder(Color color, int radius, int thickness) {
        this.color = color;
        this.radius = radius;
        this.thickness = thickness;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(thickness)); // Set the border thickness
        g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        g2d.dispose();
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(this.radius + thickness, this.radius + thickness, this.radius + thickness, this.radius + thickness);
    }

    @Override
    public boolean isBorderOpaque() {
        return true;
    }
}