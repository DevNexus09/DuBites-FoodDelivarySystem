package com.foodordersystem.ui.common;

import com.foodordersystem.database.UserDatabase;
import com.foodordersystem.model.entities.User;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
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

        // Custom panel with a gradient background
        GradientPanel backgroundPanel = new GradientPanel(new Color(255, 102, 0), Color.WHITE);
        backgroundPanel.setLayout(new GridBagLayout());
        setContentPane(backgroundPanel);

        // Form panel with a background image and border
        ImagePanel formPanel = new ImagePanel("/com/foodordersystem/Resources/Logo.png", 0.08f);
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1), "Create Your Account",
                TitledBorder.CENTER, TitledBorder.TOP, new Font("Arial", Font.BOLD, 18), new Color(50, 50, 50)));


        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Form fields styling
        Font labelFont = new Font("Arial", Font.BOLD, 14);
        Font fieldFont = new Font("Arial", Font.PLAIN, 14);
        Color textColor = new Color(50, 50, 50);

        // Form fields
        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(createLabel("Name:", labelFont, textColor), gbc);
        JTextField nameField = createTextField(fieldFont); gbc.gridx = 1; formPanel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(createLabel("Date of Birth (DD-MM-YYYY):", labelFont, textColor), gbc);
        JTextField dobField = createTextField(fieldFont); gbc.gridx = 1; formPanel.add(dobField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(createLabel("Mobile Number:", labelFont, textColor), gbc);
        JTextField mobileField = createTextField(fieldFont); gbc.gridx = 1; formPanel.add(mobileField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; formPanel.add(createLabel("Username:", labelFont, textColor), gbc);
        JTextField usernameField = createTextField(fieldFont); gbc.gridx = 1; formPanel.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 4; formPanel.add(createLabel("Password:", labelFont, textColor), gbc);
        JPasswordField passwordField = createPasswordField(fieldFont); gbc.gridx = 1; formPanel.add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = 5; formPanel.add(createLabel("Address:", labelFont, textColor), gbc);
        JTextField addressField = createTextField(fieldFont); gbc.gridx = 1; formPanel.add(addressField, gbc);

        gbc.gridx = 0; gbc.gridy = 6; formPanel.add(createLabel("Role:", labelFont, textColor), gbc);
        JComboBox<String> roleComboBox = new JComboBox<>(new String[]{"Customer", "Rider", "Restaurant Owner"});
        roleComboBox.setFont(fieldFont);
        gbc.gridx = 1; formPanel.add(roleComboBox, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);
        JButton signupButton = new JButton("Sign Up");
        JButton backButton = new JButton("Back");
        styleButton(signupButton, new Font("Arial", Font.BOLD, 16), new Dimension(120, 40), new Color(255, 102, 0), Color.WHITE);
        styleButton(backButton, new Font("Arial", Font.BOLD, 16), new Dimension(120, 40), new Color(128, 128, 128), Color.WHITE);

        buttonPanel.add(signupButton);
        buttonPanel.add(backButton);

        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(buttonPanel, gbc);

        backgroundPanel.add(formPanel, new GridBagConstraints());


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

    private JLabel createLabel(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        return label;
    }

    private JTextField createTextField(Font font) {
        JTextField textField = new JTextField(20);
        textField.setFont(font);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        return textField;
    }

    private JPasswordField createPasswordField(Font font) {
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setFont(font);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        return passwordField;
    }

    private void styleButton(JButton button, Font font, Dimension size, Color bgColor, Color fgColor) {
        button.setFont(font);
        button.setPreferredSize(size);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}

class GradientPanel extends JPanel {
    private final Color color1;
    private final Color color2;

    public GradientPanel(Color color1, Color color2) {
        this.color1 = color1;
        this.color2 = color2;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        int width = getWidth();
        int height = getHeight();
        GradientPaint gp = new GradientPaint(0, 0, color1, 0, height, color2);
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, width, height);
    }
}

class ImagePanel extends JPanel {
    private Image backgroundImage;
    private float opacity;

    public ImagePanel(String imagePath, float opacity) {
        try {
            this.backgroundImage = new ImageIcon(getClass().getResource(imagePath)).getImage();
            this.opacity = opacity;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            g2d.dispose();
        }
    }
}