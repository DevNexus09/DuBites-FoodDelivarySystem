package com.foodordersystem.ui.customer;

import com.foodordersystem.database.RestaurantDatabase;
import com.foodordersystem.model.entities.Restaurant;
import com.foodordersystem.model.entities.User;
import com.foodordersystem.ui.common.BaseFrame;
import com.foodordersystem.ui.common.ImagePanel;
import com.foodordersystem.ui.common.RoleSelectionFrame;
import com.foodordersystem.ui.common.RoundedButton;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.stream.Collectors;

public class RestaurantSelectionFrame extends BaseFrame {
    private final List<Restaurant> allRestaurants;
    private List<Restaurant> filteredRestaurants;
    private final User customer;
    private JPanel restaurantPanel;
    private JTextField searchField;
    private JComboBox<String> cuisineFilter;

    public RestaurantSelectionFrame(User customer) {
        super("Select a Restaurant", 1100, 800);
        this.customer = customer;
        this.allRestaurants = new RestaurantDatabase().getAllRestaurants();
        this.filteredRestaurants = allRestaurants;
        initComponents();
    }

    protected void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main background
        ImagePanel backgroundPanel = new ImagePanel("/com/foodordersystem/Resources/SignUpFrameBg.png", 1.0f);
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);

        // Header Panel for Title and Controls
        JPanel headerPanel = new JPanel(new BorderLayout(20, 10));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel titleLabel = new JLabel("Explore Restaurants");
        titleLabel.setFont(new Font("DialogInput", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Panel for search and filter controls
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 5, 0, 5);
        gbc.anchor = GridBagConstraints.CENTER;

        // Search Label
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setForeground(Color.WHITE);
        searchLabel.setFont(new Font("Dialog", Font.BOLD, 14));
        topPanel.add(searchLabel, gbc);

        // Search Field
        searchField = new JTextField(15);
        styleTextField(searchField);
        topPanel.add(searchField, gbc);

        // Cuisine Label
        JLabel cuisineLabel = new JLabel("Cuisine:");
        cuisineLabel.setForeground(Color.WHITE);
        cuisineLabel.setFont(new Font("Dialog", Font.BOLD, 14));
        gbc.insets = new Insets(0, 15, 0, 5);
        topPanel.add(cuisineLabel, gbc);
        gbc.insets = new Insets(0, 5, 0, 5);

        // Cuisine Filter
        List<String> cuisines = allRestaurants.stream().map(Restaurant::getCuisine).distinct().collect(Collectors.toList());
        cuisines.add(0, "All Cuisines");
        cuisineFilter = new JComboBox<>(cuisines.toArray(new String[0]));
        styleComboBox(cuisineFilter);
        topPanel.add(cuisineFilter, gbc);

        // Search Button
        JButton searchButton = new RoundedButton("Search");
        styleHeaderButton(searchButton);
        searchButton.addActionListener(e -> filterRestaurants());
        gbc.insets = new Insets(0, 15, 0, 5);
        topPanel.add(searchButton, gbc);

        headerPanel.add(topPanel, BorderLayout.EAST);
        backgroundPanel.add(headerPanel, BorderLayout.NORTH);

        if (allRestaurants.isEmpty()) {
            JLabel noRestaurantsLabel = new JLabel("No restaurants available yet.", SwingConstants.CENTER);
            noRestaurantsLabel.setFont(new Font("Dialog", Font.BOLD, 18));
            noRestaurantsLabel.setForeground(Color.WHITE);
            add(noRestaurantsLabel, BorderLayout.CENTER);
        } else {
            restaurantPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 25));
            restaurantPanel.setOpaque(false);
            updateRestaurantPanel();

            JScrollPane scrollPane = new JScrollPane(restaurantPanel);
            scrollPane.setOpaque(false);
            scrollPane.getViewport().setOpaque(false);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            scrollPane.getVerticalScrollBar().setUnitIncrement(16);
            backgroundPanel.add(scrollPane, BorderLayout.CENTER);
        }

        JButton logoutButton = new RoundedButton("Logout");
        styleHeaderButton(logoutButton);
        logoutButton.addActionListener(e -> {
            new RoleSelectionFrame().setVisible(true);
            dispose();
        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 30));
        bottomPanel.add(logoutButton);
        backgroundPanel.add(bottomPanel, BorderLayout.SOUTH);
    }

    private void filterRestaurants() {
        String searchText = searchField.getText().toLowerCase();
        String selectedCuisine = (String) cuisineFilter.getSelectedItem();

        filteredRestaurants = allRestaurants.stream()
                .filter(r -> r.getName().toLowerCase().contains(searchText))
                .filter(r -> "All Cuisines".equals(selectedCuisine) || r.getCuisine().equals(selectedCuisine))
                .collect(Collectors.toList());

        updateRestaurantPanel();
    }

    private void updateRestaurantPanel() {
        restaurantPanel.removeAll();
        for (Restaurant restaurant : filteredRestaurants) {
            RestaurantCardPanel restaurantCard = new RestaurantCardPanel(restaurant);
            restaurantPanel.add(restaurantCard);
        }
        restaurantPanel.revalidate();
        restaurantPanel.repaint();
    }

    private void styleTextField(JTextField textField) {
        textField.setFont(new Font("Dialog", Font.PLAIN, 14));
        textField.setBackground(new Color(50, 50, 50));
        textField.setForeground(Color.WHITE);
        textField.setCaretColor(Color.WHITE);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 100)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
    }

    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setFont(new Font("Dialog", Font.PLAIN, 14));
        comboBox.setBackground(new Color(50, 50, 50));
        comboBox.setForeground(Color.WHITE);
        comboBox.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100)));
    }

    private void styleHeaderButton(JButton button) {
        button.setFont(new Font("Dialog", Font.BOLD, 12));
        button.setBackground(new Color(255, 102, 0));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(80, 30));
    }

    private class RestaurantCardPanel extends JPanel {
        public RestaurantCardPanel(Restaurant restaurant) {
            setLayout(new BorderLayout(0, 10));
            setPreferredSize(new Dimension(220, 250));
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            Border defaultBorder = BorderFactory.createLineBorder(new Color(255, 255, 255, 70), 2);
            Border hoverBorder = BorderFactory.createLineBorder(Color.ORANGE, 2);
            setBorder(BorderFactory.createCompoundBorder(defaultBorder, BorderFactory.createEmptyBorder(10, 10, 10, 10)));

            JLabel imageLabel = new JLabel();
            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            if (restaurant.getImagePath() != null && !restaurant.getImagePath().isEmpty()) {
                try {
                    ImageIcon icon = new ImageIcon(new ImageIcon(getClass().getResource(restaurant.getImagePath())).getImage().getScaledInstance(200, 150, Image.SCALE_SMOOTH));
                    imageLabel.setIcon(icon);
                } catch (Exception e) {
                    imageLabel.setText("Image not found");
                    imageLabel.setForeground(Color.WHITE);
                }
            } else {
                imageLabel.setText("No Image Available");
                imageLabel.setForeground(Color.WHITE);
            }
            add(imageLabel, BorderLayout.CENTER);

            JPanel detailsPanel = new JPanel(new GridLayout(2, 1));
            detailsPanel.setOpaque(false);

            JLabel nameLabel = new JLabel(restaurant.getName());
            nameLabel.setFont(new Font("Dialog", Font.BOLD, 16));
            nameLabel.setForeground(Color.WHITE);
            nameLabel.setHorizontalAlignment(SwingConstants.CENTER);

            JLabel locationLabel = new JLabel(restaurant.getLocation());
            locationLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
            locationLabel.setForeground(Color.LIGHT_GRAY);
            locationLabel.setHorizontalAlignment(SwingConstants.CENTER);

            detailsPanel.add(nameLabel);
            detailsPanel.add(locationLabel);
            add(detailsPanel, BorderLayout.SOUTH);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    setBorder(BorderFactory.createCompoundBorder(hoverBorder, BorderFactory.createEmptyBorder(10, 10, 10, 10)));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    setBorder(BorderFactory.createCompoundBorder(defaultBorder, BorderFactory.createEmptyBorder(10, 10, 10, 10)));
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    new FoodOrderSystem(restaurant, customer).setVisible(true);
                    dispose();
                }
            });
        }
    }
}