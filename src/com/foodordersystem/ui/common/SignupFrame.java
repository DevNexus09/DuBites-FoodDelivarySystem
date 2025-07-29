package com.foodordersystem.ui.common;

import com.foodordersystem.database.UserDatabase;
import com.foodordersystem.model.entities.User;

import javax.swing.*;
import java.awt.*;

public class SignupFrame extends BaseFrame {
    private final UserDatabase userDatabase;

    public SignupFrame() {
        super("Sign Up", 800, 600);
        this.userDatabase = new UserDatabase();
        initComponents();
    }

    @Override
    protected void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Use ImagePanel as the content pane for the background
        ImagePanel backgroundPanel = new ImagePanel("/com/foodordersystem/Resources/SignUpFrameBg.png", 1.0f);
        backgroundPanel.setLayout(new GridBagLayout()); // To center the form
        setContentPane(backgroundPanel);

        // Panel to hold the form components, made transparent
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30)); // Add padding

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Title ---
        JLabel titleLabel = new JLabel("Create Your Account");
        titleLabel.setFont(new Font("DialogInput", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 20, 0);
        formPanel.add(titleLabel, gbc);
        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 10, 10, 10);

        // --- Form Fields ---
        Font labelFont = new Font("Arial", Font.BOLD, 14);
        Color labelColor = Color.WHITE;

        // All fields are created and added directly
        JTextField nameField = new JTextField(20);
        JTextField dobField = new JTextField(20);
        JTextField mobileField = new JTextField(20);
        JTextField usernameField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JTextField addressField = new JTextField(20);
        JComboBox<String> roleComboBox = new JComboBox<>(new String[]{"Customer", "Rider", "Restaurant Owner"});

        // Add labels and fields to the panel
        addField(formPanel, gbc, 1, "Name:", nameField, labelFont, labelColor);
        addField(formPanel, gbc, 2, "Date of Birth (DD-MM-YYYY):", dobField, labelFont, labelColor);
        addField(formPanel, gbc, 3, "Mobile Number:", mobileField, labelFont, labelColor);
        addField(formPanel, gbc, 4, "Username:", usernameField, labelFont, labelColor);
        addField(formPanel, gbc, 5, "Password:", passwordField, labelFont, labelColor);
        addField(formPanel, gbc, 6, "Address:", addressField, labelFont, labelColor);
        addField(formPanel, gbc, 7, "Role:", roleComboBox, labelFont, labelColor);

        // --- Buttons ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);
        JButton signupButton = new RoundedButton("Sign Up");
        JButton backButton = new RoundedButton("Back");
        styleButton(signupButton, new Font("DialogInput", Font.BOLD, 14), new Dimension(120, 40), new Color(255, 102, 0), Color.WHITE);
        styleButton(backButton, new Font("DialogInput", Font.BOLD, 14), new Dimension(120, 40), new Color(100, 100, 100), Color.WHITE);
        buttonPanel.add(signupButton);
        buttonPanel.add(backButton);

        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 0, 0, 0);
        formPanel.add(buttonPanel, gbc);

        // Add the transparent form panel to the background
        add(formPanel, new GridBagConstraints());

        // --- Action Listeners ---
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
                new LoginFrame(role).setVisible(true);
                dispose();
            }
        });

        backButton.addActionListener(e -> {
            new LandingPage().setVisible(true);
            dispose();
        });
    }

    /**
     * Helper method to add a labeled component to the form.
     */
    private void addField(JPanel panel, GridBagConstraints gbc, int yPos, String labelText, Component component, Font font, Color color) {
        JLabel label = new JLabel(labelText);
        label.setFont(font);
        label.setForeground(color);
        gbc.gridx = 0;
        gbc.gridy = yPos;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(label, gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(component, gbc);
    }

    private void styleButton(JButton button, Font font, Dimension size, Color bgColor, Color fgColor) {
        button.setFont(font);
        button.setPreferredSize(size);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}