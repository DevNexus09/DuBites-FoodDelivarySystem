package com.foodordersystem.ui.owner;

import com.foodordersystem.model.entities.User;
import com.foodordersystem.ui.common.BaseFrame;
import com.foodordersystem.ui.common.ImagePanel;
import com.foodordersystem.ui.common.RoleSelectionFrame;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class OwnerDashboardFrame extends BaseFrame {
    private final User owner;

    public OwnerDashboardFrame(User owner) {
        super("Owner Dashboard", 800, 600);
        this.owner = owner;
        initComponents();
    }

    protected void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main background panel
        ImagePanel backgroundPanel = new ImagePanel("/com/foodordersystem/Resources/OwnerDashboardBg.png", 1.0f);
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);

        // Main content panel with GridBagLayout for centering
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        GridBagConstraints gbc = new GridBagConstraints();

        // Welcome Label
        JLabel welcomeLabel = new JLabel("Welcome, " + owner.getName() + "!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("DialogInput", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 0.1; // Pushes content down slightly
        gbc.anchor = GridBagConstraints.PAGE_START;
        mainPanel.add(welcomeLabel, gbc);

        // Card Panel
        JPanel cardPanel = new JPanel(new GridLayout(1, 2, 40, 0));
        cardPanel.setOpaque(false);
        gbc.gridy = 1;
        gbc.weighty = 0.8; // Takes up most of the space
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(cardPanel, gbc);


        // Create Restaurant Card
        JPanel createCard = createDashboardCard(
                "Create New Restaurant",
                "Set up your restaurant profile and menu.",
                "/com/foodordersystem/Resources/create_icon.png" // Example icon path
        );
        createCard.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                new RestaurantCreationFrame(owner).setVisible(true);
                dispose();
            }
        });

        // Manage Restaurant Card
        JPanel manageCard = createDashboardCard(
                "Manage Existing Restaurant",
                "Update your menu, view orders, and track sales.",
                "/com/foodordersystem/Resources/manage_icon.png" // Example icon path
        );
        manageCard.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                new RestaurantLoginFrame(owner).setVisible(true);
                dispose();
            }
        });

        cardPanel.add(createCard);
        cardPanel.add(manageCard);

        // Logout Button Panel
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logoutPanel.setOpaque(false);
        JButton logoutButton = new JButton("Logout");
        styleLogoutButton(logoutButton);
        logoutButton.addActionListener(e -> {
            new RoleSelectionFrame().setVisible(true);
            dispose();
        });
        logoutPanel.add(logoutButton);

        gbc.gridy = 2;
        gbc.weighty = 0.1;
        gbc.anchor = GridBagConstraints.PAGE_END;
        mainPanel.add(logoutPanel, gbc);

        backgroundPanel.add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createDashboardCard(String title, String description, String iconPath) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(0, 0, 0, 170));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2d.dispose();
            }
        };
        card.setLayout(new BorderLayout(10, 10));
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(280, 220));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        Border defaultBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 80), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );
        Border hoverBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(243, 96, 6), 2),
                BorderFactory.createEmptyBorder(19, 19, 19, 19)
        );
        card.setBorder(defaultBorder);

        // Title
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        card.add(titleLabel, BorderLayout.NORTH);

        // Description
        JTextArea descriptionArea = new JTextArea(description);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setLineWrap(true);
        descriptionArea.setOpaque(false);
        descriptionArea.setEditable(false);
        descriptionArea.setFont(new Font("Dialog", Font.PLAIN, 14));
        descriptionArea.setForeground(Color.LIGHT_GRAY);
        descriptionArea.setBorder(BorderFactory.createEmptyBorder(10, 5, 0, 5));
        card.add(descriptionArea, BorderLayout.CENTER);


        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBorder(hoverBorder);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBorder(defaultBorder);
            }
        });

        return card;
    }

    private void styleLogoutButton(JButton button) {
        button.setFont(new Font("Dialog", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(243, 96, 6));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(80, 30));
    }
}