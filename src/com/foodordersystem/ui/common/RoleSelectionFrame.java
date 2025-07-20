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

        // Use the custom ImagePanel as the content pane for the background effect
        ImagePanel backgroundPanel = new ImagePanel("/com/foodordersystem/Resources/Logo.png", 0.1f);
        backgroundPanel.setLayout(new GridBagLayout());
        setContentPane(backgroundPanel);

        // Panel to hold the buttons, made transparent
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        // Add the new label
        JLabel titleLabel = new JLabel("Let Us Know Who You Are !");
        titleLabel.setFont(new Font("DialogInput", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        buttonPanel.add(titleLabel, gbc);

        // Add some space after the title
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 20)), gbc);

        // Button styling properties
        Font buttonFont = new Font("DialogInput", Font.BOLD, 16);
        Dimension regularButtonSize = new Dimension(250, 50);
        Dimension backButtonSize = new Dimension(150, 40); // Smaller size for the back button
        Color buttonColor = new Color(255, 102, 0);
        Color backButtonColor = new Color(128, 128, 128);
        Color buttonTextColor = Color.WHITE;

        // Create and style the buttons
        JButton customerButton = new RoundedButton("Customer");
        styleButton(customerButton, buttonFont, regularButtonSize, buttonColor, buttonTextColor);
        customerButton.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        JButton riderButton = new RoundedButton("Rider");
        styleButton(riderButton, buttonFont, regularButtonSize, buttonColor, buttonTextColor);
        riderButton.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        JButton ownerButton = new RoundedButton("Restaurant Owner");
        styleButton(ownerButton, buttonFont, regularButtonSize, buttonColor, buttonTextColor);
        ownerButton.addActionListener(e -> {
            new LoginFrame().setVisible(true);
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

        // --- **FIX** ---
        // Modify constraints for the back button to prevent it from stretching
        gbc.insets = new Insets(30, 10, 10, 10);
        gbc.fill = GridBagConstraints.NONE; // This stops the button from filling horizontally
        gbc.anchor = GridBagConstraints.CENTER; // This centers the button in its cell
        buttonPanel.add(backButton, gbc);

        // Add the button panel to the main background panel
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

/**
 * A custom JButton class to create buttons with rounded corners.
 */
class RoundedButton extends JButton {
    public RoundedButton(String text) {
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
 * This class is added here to make the file self-contained.
 */
class ImagePanel1 extends JPanel {
    private Image backgroundImage;
    private float opacity;

    public ImagePanel1(String imagePath, float opacity) {
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