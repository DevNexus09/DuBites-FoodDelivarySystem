package com.foodordersystem.ui.customer;

import com.foodordersystem.database.RestaurantDatabase;
import com.foodordersystem.model.entities.Restaurant;
import com.foodordersystem.model.entities.User;
import com.foodordersystem.ui.common.BaseFrame;
import com.foodordersystem.ui.common.RoleSelectionFrame;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class RestaurantSelectionFrame extends BaseFrame {
    private final List<Restaurant> restaurants;
    private final User customer;

    public RestaurantSelectionFrame(User customer) {
        super("Select a Restaurant", 800, 600);
        this.customer = customer;
        this.restaurants = new RestaurantDatabase().getAllRestaurants();
        initComponents();
    }

    protected void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Use ImagePanel for the background
        ImagePanel3 backgroundPanel = new ImagePanel3("/com/foodordersystem/Resources/LandingPageBg.png", 1.0f);
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);


        if (restaurants.isEmpty()) {
            JLabel noRestaurantsLabel = new JLabel("No restaurants available yet. Please check back later.", SwingConstants.CENTER);
            noRestaurantsLabel.setForeground(Color.WHITE); // Set text color to be visible
            add(noRestaurantsLabel, BorderLayout.CENTER);
        } else {
            JPanel restaurantPanel = new JPanel(new GridLayout(0, 3, 10, 10));
            restaurantPanel.setOpaque(false); // Make panel transparent
            for (Restaurant restaurant : restaurants) {
                JButton restaurantButton = new JButton(restaurant.getName());
                if (restaurant.getImagePath() != null && !restaurant.getImagePath().isEmpty()) {
                    ImageIcon icon = new ImageIcon(getClass().getResource(restaurant.getImagePath()));
                    Image img = icon.getImage().getScaledInstance(200, 150, Image.SCALE_SMOOTH);
                    restaurantButton.setIcon(new ImageIcon(img));
                    restaurantButton.setHorizontalTextPosition(JButton.CENTER);
                    restaurantButton.setVerticalTextPosition(JButton.CENTER);
                }
                restaurantButton.setPreferredSize(new Dimension(200, 150));
                restaurantButton.addActionListener(e -> {
                    new FoodOrderSystem(restaurant, customer).setVisible(true);
                    dispose();
                });
                restaurantPanel.add(restaurantButton);
            }
            JScrollPane scrollPane = new JScrollPane(restaurantPanel);
            scrollPane.setOpaque(false); // Make scroll pane transparent
            scrollPane.getViewport().setOpaque(false); // Make viewport transparent
            add(scrollPane, BorderLayout.CENTER);
        }

        JButton logoutButton = new JButton("Logout");
        styleButton(logoutButton, new Font("DialogInput", Font.BOLD, 12), new Dimension(100, 30), Color.BLACK, Color.WHITE);
        logoutButton.addActionListener(e -> {
            new RoleSelectionFrame().setVisible(true);
            dispose();
        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false); // Make panel transparent
        bottomPanel.add(logoutButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void styleButton(JButton button, Font font, Dimension size, Color bgColor, Color fgColor) {
        button.setFont(font);
        button.setPreferredSize(size);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}

class ImagePanel3 extends JPanel {
    private Image backgroundImage;
    private float opacity;

    public ImagePanel3(String imagePath, float opacity) {
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