package com.foodordersystem.ui.common;

import com.foodordersystem.database.UserDatabase;
import com.foodordersystem.model.entities.User;
import com.foodordersystem.ui.customer.RestaurantSelectionFrame;
import com.foodordersystem.ui.owner.OwnerDashboardFrame;
import com.foodordersystem.ui.rider.RiderDashboardFrame;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends BaseFrame {
    private final UserDatabase userDatabase;
    private final String role;

    public LoginFrame(String role) {
        super("Login", 800, 600);
        this.userDatabase = new UserDatabase();
        this.role = role;
        initComponents();
    }

    @Override
    protected void initComponents() {
        setTitle("DuBites Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Set a custom panel with a background image as the content pane
        ImagePanel backgroundPanel = new ImagePanel("/com/foodordersystem/Resources/LoginFrameBg.png", 1.0f); // Opacity reduced
        backgroundPanel.setLayout(new GridBagLayout()); // Center the content
        setContentPane(backgroundPanel);

        // Main login panel, made transparent to show the background
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false); // Make panel transparent
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30)); // Add padding

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Title ---
        JLabel titleLabel = new JLabel("Login as " + role);
        titleLabel.setFont(new Font("DialogInput", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 20, 0);
        panel.add(titleLabel, gbc);
        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 10, 10, 10);


        // --- Form Fields ---
        Font labelFont = new Font("DialogInput", Font.BOLD, 14);
        Color labelColor = Color.WHITE;

        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(labelFont);
        userLabel.setForeground(labelColor);
        panel.add(userLabel, gbc);

        JTextField usernameField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(labelFont);
        passLabel.setForeground(labelColor);
        panel.add(passLabel, gbc);

        JPasswordField passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        // --- Buttons ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false); // Make button panel transparent

        JButton loginButton = new RoundedButton("Login");
        styleButton(loginButton, new Font("DialogInput", Font.BOLD, 14), new Dimension(120, 40), new Color(255, 102, 0), Color.WHITE);

        JButton backButton = new RoundedButton("Back");
        styleButton(backButton, new Font("DialogInput", Font.BOLD, 14), new Dimension(120, 40), new Color(100, 100, 100), Color.WHITE);

        buttonPanel.add(loginButton);
        buttonPanel.add(backButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 0, 0, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(buttonPanel, gbc);

        // Add the login panel to the main background panel
        add(panel);

        // --- Action Listeners ---
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            User user = userDatabase.findUser(username, password, this.role);

            if (user != null) {
                switch (user.getRole()) {
                    case "Customer":
                        new RestaurantSelectionFrame(user).setVisible(true);
                        break;
                    case "Restaurant Owner":
                        new OwnerDashboardFrame(user).setVisible(true);
                        break;
                    case "Rider":
                        new RiderDashboardFrame(user).setVisible(true);
                        break;
                }
                dispose();
            } else {
                showErrorDialog("Invalid credentials");
            }
        });

        backButton.addActionListener(e -> {
            new RoleSelectionFrame().setVisible(true);
            dispose();
        });
    }

    private void styleButton(JButton button, Font font, Dimension size, Color bgColor, Color fgColor) {
        button.setFont(font);
        button.setPreferredSize(size);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void showErrorDialog(String message) {
        UIManager.put("OptionPane.background", new Color(43, 43, 43));
        UIManager.put("Panel.background", new Color(43, 43, 43));
        UIManager.put("OptionPane.messageForeground", Color.WHITE);
        UIManager.put("Button.background", new Color(255, 102, 0));
        UIManager.put("Button.foreground", Color.WHITE);

        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);

        // Reset to default
        UIManager.put("OptionPane.background", null);
        UIManager.put("Panel.background", null);
        UIManager.put("OptionPane.messageForeground", null);
        UIManager.put("Button.background", null);
        UIManager.put("Button.foreground", null);
    }
}