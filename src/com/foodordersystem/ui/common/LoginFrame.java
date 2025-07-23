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
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.WHITE, 2), "Login as " + role,
                javax.swing.border.TitledBorder.CENTER,
                javax.swing.border.TitledBorder.TOP,
                new Font("DialogInput", Font.BOLD, 20), Color.WHITE));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Form Fields ---
        Font labelFont = new Font("DialogInput", Font.BOLD, 14);
        Color labelColor = Color.WHITE;

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(labelFont);
        userLabel.setForeground(labelColor);
        panel.add(userLabel, gbc);

        JTextField usernameField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(labelFont);
        passLabel.setForeground(labelColor);
        panel.add(passLabel, gbc);

        JPasswordField passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        // --- Buttons ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false); // Make button panel transparent

        JButton loginButton = new RoundedButton("Login");
        styleButton(loginButton, new Font("DialogInput", Font.BOLD, 14), new Dimension(100, 40), Color.DARK_GRAY, Color.WHITE);

        JButton backButton = new RoundedButton("Back");
        styleButton(backButton, new Font("DialogInput", Font.BOLD, 14), new Dimension(100, 40), Color.DARK_GRAY, Color.WHITE);

        buttonPanel.add(loginButton);
        buttonPanel.add(backButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
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
                JOptionPane.showMessageDialog(this, "Invalid credentials", "Error", JOptionPane.ERROR_MESSAGE);
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
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}

