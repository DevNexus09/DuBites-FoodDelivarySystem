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

    public LoginFrame() {
        super("Login", 800, 600); // Changed size
        this.userDatabase = new UserDatabase();
        initComponents();
    }

    @Override
    protected void initComponents() {
        setTitle("DuBites Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout()); // Center the content

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Login"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Username:"), gbc);

        JTextField usernameField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Password:"), gbc);

        JPasswordField passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton loginButton = new JButton("Login");
        JButton backButton = new JButton("Back");
        buttonPanel.add(loginButton);
        buttonPanel.add(backButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(buttonPanel, gbc);

        add(panel); // Add the panel to the frame's content pane

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            User user = userDatabase.findUserByUsername(username);

            if (user != null && user.getPassword().equals(password)) {
                JOptionPane.showMessageDialog(this, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
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
                JOptionPane.showMessageDialog(this, "Invalid username or password", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        backButton.addActionListener(e -> {
            new RoleSelectionFrame().setVisible(true);
            dispose();
        });
    }
}