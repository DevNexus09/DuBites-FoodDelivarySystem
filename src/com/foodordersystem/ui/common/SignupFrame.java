package com.foodordersystem.ui.common;

import com.foodordersystem.database.UserDatabase;
import com.foodordersystem.model.entities.User;

import javax.swing.*;
import java.awt.*;

public class SignupFrame extends BaseFrame {
    private final UserDatabase userDatabase;

    public SignupFrame() {
        super("Sign Up", 800, 600); // Changed size
        this.userDatabase = new UserDatabase();
        initComponents();
    }

    @Override
    protected void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Use GridBagLayout for a more organized form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Form fields
        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Name:"), gbc);
        JTextField nameField = new JTextField(20); gbc.gridx = 1; formPanel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Date of Birth (DD-MM-YYYY):"), gbc);
        JTextField dobField = new JTextField(20); gbc.gridx = 1; formPanel.add(dobField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Mobile Number:"), gbc);
        JTextField mobileField = new JTextField(20); gbc.gridx = 1; formPanel.add(mobileField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; formPanel.add(new JLabel("Username:"), gbc);
        JTextField usernameField = new JTextField(20); gbc.gridx = 1; formPanel.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 4; formPanel.add(new JLabel("Password:"), gbc);
        JPasswordField passwordField = new JPasswordField(20); gbc.gridx = 1; formPanel.add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = 5; formPanel.add(new JLabel("Address:"), gbc);
        JTextField addressField = new JTextField(20); gbc.gridx = 1; formPanel.add(addressField, gbc);

        gbc.gridx = 0; gbc.gridy = 6; formPanel.add(new JLabel("Role:"), gbc);
        JComboBox<String> roleComboBox = new JComboBox<>(new String[]{"Customer", "Rider", "Restaurant Owner"});
        gbc.gridx = 1; formPanel.add(roleComboBox, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton signupButton = new JButton("Sign Up");
        JButton backButton = new JButton("Back");
        buttonPanel.add(signupButton);
        buttonPanel.add(backButton);

        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(buttonPanel, gbc);

        // Main content panel to center the form
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.add(formPanel);
        add(mainPanel, BorderLayout.CENTER);

        signupButton.addActionListener(e -> {
            String name = nameField.getText();
            String dob = dobField.getText();
            String mobile = mobileField.getText();
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String address = addressField.getText();
            String role = (String) roleComboBox.getSelectedItem();

            if (name.isEmpty() || username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name, Username, and Password cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (userDatabase.isUsernameTaken(username)) {
                JOptionPane.showMessageDialog(this, "Username is already taken!", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                userDatabase.addUser(new User(name, dob, mobile, username, password, address, role));
                JOptionPane.showMessageDialog(this, "Signup successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                new LoginFrame().setVisible(true);
                dispose();
            }
        });

        backButton.addActionListener(e -> {
            new LandingPage().setVisible(true);
            dispose();
        });
    }
}