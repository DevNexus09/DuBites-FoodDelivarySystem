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
        super("Login", 800, 600);
        this.userDatabase = new UserDatabase();
        initComponents();
    }

    @Override
    protected void initComponents() {
        setTitle("DuBites Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Set a custom panel with a background image as the content pane
        ImagePanel backgroundPanel = new ImagePanel("/com/foodordersystem/Resources/LoginFrameBg.jpg", 0.7f); // Opacity reduced
        backgroundPanel.setLayout(new GridBagLayout()); // Center the content
        setContentPane(backgroundPanel);

        // Main login panel, made transparent to show the background
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false); // Make panel transparent
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2), "Login",
                javax.swing.border.TitledBorder.CENTER,
                javax.swing.border.TitledBorder.TOP,
                new Font("DialogInput", Font.BOLD, 20), Color.BLACK));

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
        styleButton(loginButton, new Font("DialogInput", Font.BOLD, 14), new Dimension(100, 40), new Color(255, 255, 255), Color.BLACK);

        JButton backButton = new RoundedButton("Back");
        styleButton(backButton, new Font("DialogInput", Font.BOLD, 14), new Dimension(100, 40), new Color(255, 255, 255), Color.BLACK);

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

    private void styleButton(JButton button, Font font, Dimension size, Color bgColor, Color fgColor) {
        button.setFont(font);
        button.setPreferredSize(size);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}

/**
 * A custom JButton class to create buttons with rounded corners.
 */
class RoundedButton1 extends JButton {
    public RoundedButton1(String text) {
        super(text);
        setContentAreaFilled(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (getModel().isArmed()) {
            g.setColor(getBackground().darker());
        } else {
            g.setColor(getBackground());
        }
        g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {
        // No border is painted to keep the rounded look clean
    }
}

/**
 * A custom JPanel that draws a background image with a specified opacity.
 */
class ImagePanel2 extends JPanel {
    private Image backgroundImage;
    private float opacity;

    public ImagePanel2(String imagePath, float opacity) {
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