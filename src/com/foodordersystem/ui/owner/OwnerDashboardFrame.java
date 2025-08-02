package com.foodordersystem.ui.owner;

import com.foodordersystem.database.RestaurantDatabase;
import com.foodordersystem.model.entities.Restaurant;
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
    private final Restaurant existingRestaurant;

    public OwnerDashboardFrame(User owner) {
        super("Owner Dashboard", 800, 600);
        this.owner = owner;
        this.existingRestaurant = new RestaurantDatabase().findRestaurantByOwner(owner.getUsername());
        initComponents();
    }

    protected void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        ImagePanel backgroundPanel = new ImagePanel("/com/foodordersystem/Resources/OwnerDashboardBg.png", 1.0f);
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel welcomeLabel = new JLabel("Welcome, " + owner.getName() + "!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("DialogInput", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 0.1;
        gbc.anchor = GridBagConstraints.PAGE_START;
        mainPanel.add(welcomeLabel, gbc);

        JPanel cardPanel = new JPanel(new GridLayout(1, 2, 40, 0));
        cardPanel.setOpaque(false);
        gbc.gridy = 1;
        gbc.weighty = 0.8;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(cardPanel, gbc);

        JPanel createCard = createDashboardCard(
                "Create New Restaurant",
                "Set up your restaurant profile and menu.",
                "/com/foodordersystem/Resources/create_icon.png"
        );

        JPanel manageCard = createDashboardCard(
                "Manage Existing Restaurant",
                "Update your menu, view orders, and track sales.",
                "/com/foodordersystem/Resources/manage_icon.png"
        );

        if (existingRestaurant != null) {
            setCardEnabled(createCard, false);
            createCard.setToolTipText("You can only own one restaurant per account.");
            manageCard.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (manageCard.isEnabled()) {
                        new RestaurantLoginFrame(owner).setVisible(true);
                        dispose();
                    }
                }
            });
        } else {
            setCardEnabled(manageCard, false);
            manageCard.setToolTipText("You need to create a restaurant first.");
            createCard.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (createCard.isEnabled()) {
                        new RestaurantCreationFrame(owner).setVisible(true);
                        dispose();
                    }
                }
            });
        }

        cardPanel.add(createCard);
        cardPanel.add(manageCard);

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

    private void setCardEnabled(JPanel card, boolean enabled) {
        card.setEnabled(enabled);
        for (Component comp : card.getComponents()) {
            comp.setEnabled(enabled);
            if (comp instanceof JTextArea) {
                JTextArea area = (JTextArea) comp;
                area.setForeground(enabled ? Color.LIGHT_GRAY : Color.DARK_GRAY);
            } else {
                comp.setForeground(enabled ? Color.WHITE : Color.GRAY);
            }
        }
        if (!enabled) {
            card.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        } else {
            card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
    }

    private JPanel createDashboardCard(String title, String description, String iconPath) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(isEnabled() ? new Color(0, 0, 0, 170) : new Color(0, 0, 0, 100));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2d.dispose();
            }
        };
        card.setLayout(new BorderLayout(10, 10));
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(280, 220));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        Border defaultBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 80), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );
        Border hoverBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(243, 96, 6), 2),
                BorderFactory.createEmptyBorder(19, 19, 19, 19)
        );
        card.setBorder(defaultBorder);

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        card.add(titleLabel, BorderLayout.NORTH);

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
                if (card.isEnabled()) {
                    card.setBorder(hoverBorder);
                }
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
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(80, 30));
    }
}