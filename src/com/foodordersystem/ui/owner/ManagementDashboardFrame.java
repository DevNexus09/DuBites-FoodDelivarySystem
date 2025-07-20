package com.foodordersystem.ui.owner;

import com.foodordersystem.database.RestaurantDatabase;
import com.foodordersystem.database.UserDatabase;
import com.foodordersystem.model.entities.MenuItem;
import com.foodordersystem.model.history.OrderHistory;
import com.foodordersystem.model.entities.Restaurant;
import com.foodordersystem.model.entities.User;
import com.foodordersystem.ui.common.BaseFrame;
import com.foodordersystem.ui.common.RoleSelectionFrame;

import javax.swing.*;
import java.awt.*;

public class ManagementDashboardFrame extends BaseFrame {
    private final Restaurant restaurant;
    private final RestaurantDatabase restaurantDatabase;
    private DefaultListModel<String> menuListModel;
    private JList<String> menuJList;

    public ManagementDashboardFrame(Restaurant restaurant) {
        super("Management Dashboard: " + restaurant.getName(), 800, 600);
        this.restaurant = restaurant;
        this.restaurantDatabase = new RestaurantDatabase();
        initComponents();
        refreshMenuList();
    }

    protected void initComponents() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // --- Menu Management Panel ---
        JPanel menuManagementPanel = new JPanel(new BorderLayout());
        menuManagementPanel.setBorder(BorderFactory.createTitledBorder("Menu Management"));

        menuListModel = new DefaultListModel<>();
        menuJList = new JList<>(menuListModel);
        menuManagementPanel.add(new JScrollPane(menuJList), BorderLayout.CENTER);

        JPanel menuActionPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Add Item");
        JButton deleteButton = new JButton("Delete Selected Item");
        JButton updateButton = new JButton("Update Selected Item Price");
        JButton backButton = new JButton("Back to Dashboard");

        menuActionPanel.add(addButton);
        menuActionPanel.add(deleteButton);
        menuActionPanel.add(updateButton);
        menuActionPanel.add(backButton);
        menuManagementPanel.add(menuActionPanel, BorderLayout.SOUTH);

        // --- Sales History Panel ---
        JPanel salesHistoryPanel = new JPanel(new BorderLayout());
        salesHistoryPanel.setBorder(BorderFactory.createTitledBorder("Weekly Sales and Order History"));
        JTextArea salesHistoryArea = new JTextArea();
        salesHistoryArea.setEditable(false);
        salesHistoryPanel.add(new JScrollPane(salesHistoryArea), BorderLayout.CENTER);

        // Add action listeners
        addButton.addActionListener(e -> addItem());
        deleteButton.addActionListener(e -> deleteItem());
        updateButton.addActionListener(e -> updateItemPrice());
        backButton.addActionListener(e -> {
            User owner = new UserDatabase().findUserByUsername(restaurant.getOwnerUsername());
            if (owner != null) {
                new OwnerDashboardFrame(owner).setVisible(true);
                dispose();
            }
        });


        // Load and display sales history
        OrderHistory.loadOrderHistory(salesHistoryArea, restaurant.getName());

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, menuManagementPanel, salesHistoryPanel);
        splitPane.setResizeWeight(0.7);
        add(splitPane, BorderLayout.CENTER);
    }

    private void refreshMenuList() {
        menuListModel.clear();
        for (MenuItem item : restaurant.getMenu()) {
            menuListModel.addElement(item.getName() + " - Rs " + item.getPrice());
        }
    }

    private void addItem() {
        JTextField nameField = new JTextField();
        JTextField priceField = new JTextField();
        Object[] message = {
                "Item Name:", nameField,
                "Price:", priceField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Add New Item", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText();
                double price = Double.parseDouble(priceField.getText());
                restaurant.getMenu().add(new MenuItem(name, price));
                restaurantDatabase.addRestaurant(restaurant); // This will save the updated restaurant
                refreshMenuList();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid price format.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteItem() {
        int selectedIndex = menuJList.getSelectedIndex();
        if (selectedIndex != -1) {
            restaurant.getMenu().remove(selectedIndex);
            restaurantDatabase.addRestaurant(restaurant);
            refreshMenuList();
        } else {
            JOptionPane.showMessageDialog(this, "Please select an item to delete.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateItemPrice() {
        int selectedIndex = menuJList.getSelectedIndex();
        if (selectedIndex != -1) {
            MenuItem item = restaurant.getMenu().get(selectedIndex);
            String newPriceStr = JOptionPane.showInputDialog(this, "Enter new price for " + item.getName() + ":", item.getPrice());
            try {
                double newPrice = Double.parseDouble(newPriceStr);
                item = new MenuItem(item.getName(), newPrice); // Create a new item with the updated price
                restaurant.getMenu().set(selectedIndex, item);
                restaurantDatabase.addRestaurant(restaurant);
                refreshMenuList();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid price format.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an item to update.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}