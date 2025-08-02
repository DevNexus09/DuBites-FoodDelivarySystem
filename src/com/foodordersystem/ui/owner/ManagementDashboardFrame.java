package com.foodordersystem.ui.owner;

import com.foodordersystem.database.RestaurantDatabase;
import com.foodordersystem.database.UserDatabase;
import com.foodordersystem.model.entities.MenuItem;
import com.foodordersystem.model.entities.Restaurant;
import com.foodordersystem.model.entities.User;
import com.foodordersystem.ui.common.BaseFrame;
import com.foodordersystem.ui.common.ImagePanel;
import com.foodordersystem.ui.common.RoundedButton;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class ManagementDashboardFrame extends BaseFrame {
    private final Restaurant restaurant;
    private final RestaurantDatabase restaurantDatabase;
    private DefaultTableModel menuTableModel;
    private JTable menuTable;
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private final List<JButton> navButtons = new ArrayList<>();
    private DashboardPanel dashboardPanel; // Field to hold the dashboard panel

    public ManagementDashboardFrame(Restaurant restaurant) {
        super("Management Dashboard: " + restaurant.getName(), 1200, 750);
        this.restaurant = restaurant;
        this.restaurantDatabase = new RestaurantDatabase();
        initComponents();
        refreshMenuTable();
    }

    protected void initComponents() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        ImagePanel backgroundPanel = new ImagePanel("/com/foodordersystem/Resources/ManagementDashboardFrameBg.png", 1.0f);
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);

        JPanel navPanel = createNavPanel();
        backgroundPanel.add(navPanel, BorderLayout.WEST);

        contentPanel = new JPanel();
        cardLayout = new CardLayout(20, 20);
        contentPanel.setLayout(cardLayout);
        contentPanel.setOpaque(false);

        dashboardPanel = new DashboardPanel(restaurant); // Use the field
        dashboardPanel.setOpaque(false);

        JPanel menuManagementPage = createMenuManagementPanel();
        menuManagementPage.setOpaque(false);

        contentPanel.add(dashboardPanel, "Dashboard");
        contentPanel.add(menuManagementPage, "Menu Management");

        backgroundPanel.add(contentPanel, BorderLayout.CENTER);

        cardLayout.show(contentPanel, "Dashboard");
        updateNavButtonSelection(navButtons.get(0));

        // Add a timer to refresh the dashboard every 5 seconds
        Timer timer = new Timer(5000, e -> {
            if (dashboardPanel.isShowing()) {
                dashboardPanel.refreshData();
            }
        });
        timer.start();
    }

    private JPanel createNavPanel() {
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new GridBagLayout());
        navPanel.setOpaque(false);
        navPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        navPanel.setBackground(new Color(0, 0, 0, 180));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.weightx = 1.0;

        String[] navItems = {"Dashboard", "Menu Management"};
        for (String item : navItems) {
            JButton navButton = createNavButton(item);
            navButtons.add(navButton);
            navButton.addActionListener(e -> {
                cardLayout.show(contentPanel, item);
                updateNavButtonSelection(navButton);
            });
            navPanel.add(navButton, gbc);
        }

        gbc.weighty = 1.0;
        navPanel.add(new JLabel(), gbc);
        gbc.weighty = 0;

        JButton backButton = new RoundedButton("Back to Owner Hub");
        styleButton(backButton);
        backButton.addActionListener(e -> {
            User owner = new UserDatabase().findUserByUsername(restaurant.getOwnerUsername());
            if (owner != null) {
                new OwnerDashboardFrame(owner).setVisible(true);
                dispose();
            }
        });
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        navPanel.add(backButton, gbc);

        return navPanel;
    }

    private JPanel createMenuManagementPanel() {
        JPanel menuManagementPanel = new JPanel(new BorderLayout(20, 20));
        menuManagementPanel.setOpaque(false);
        menuManagementPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Menu Management", SwingConstants.CENTER);
        title.setFont(new Font("DialogInput", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        menuManagementPanel.add(title, BorderLayout.NORTH);

        menuTableModel = new DefaultTableModel(new Object[]{"Item Name", "Price (Bdt)"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        menuTable = new JTable(menuTableModel);
        styleTable(menuTable);

        JScrollPane scrollPane = new JScrollPane(menuTable);
        styleScrollPane(scrollPane);
        menuManagementPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel menuActionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        menuActionPanel.setOpaque(false);
        JButton addButton = new RoundedButton("Add New Item");
        JButton deleteButton = new RoundedButton("Delete Selected Item");
        JButton updateButton = new RoundedButton("Update Item Price");
        styleButton(addButton);
        styleButton(deleteButton);
        styleButton(updateButton);

        menuActionPanel.add(addButton);
        menuActionPanel.add(deleteButton);
        menuActionPanel.add(updateButton);
        menuManagementPanel.add(menuActionPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> addItem());
        deleteButton.addActionListener(e -> deleteItem());
        updateButton.addActionListener(e -> updateItemPrice());

        return menuManagementPanel;
    }

    private JButton createNavButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.setFont(new Font("Dialog", Font.BOLD, 14));
        button.setForeground(Color.LIGHT_GRAY);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                if (!button.isSelected()) {
                    button.setForeground(Color.WHITE);
                }
            }
            public void mouseExited(MouseEvent evt) {
                if (!button.isSelected()) {
                    button.setForeground(Color.LIGHT_GRAY);
                }
            }
        });
        return button;
    }

    private void updateNavButtonSelection(JButton selectedButton) {
        for (JButton button : navButtons) {
            button.setSelected(false);
            button.setForeground(Color.LIGHT_GRAY);
            button.setFont(new Font("Dialog", Font.BOLD, 14));
        }
        selectedButton.setSelected(true);
        selectedButton.setForeground(Color.ORANGE);
        selectedButton.setFont(new Font("Dialog", Font.BOLD, 16));
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Dialog", Font.BOLD, 12));
        button.setBackground(new Color(80, 80, 80));
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(180, 35));
    }

    private void styleTable(JTable table) {
        table.getTableHeader().setFont(new Font("Dialog", Font.BOLD, 16));
        table.getTableHeader().setBackground(new Color(30, 30, 30));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setFont(new Font("Dialog", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.setBackground(new Color(50, 50, 50));
        table.setForeground(Color.WHITE);
        table.setSelectionBackground(Color.ORANGE);
        table.setSelectionForeground(Color.BLACK);
        table.setFillsViewportHeight(true);
    }

    private void styleScrollPane(JScrollPane scrollPane) {
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(80,80,80)));
    }

    private void refreshMenuTable() {
        menuTableModel.setRowCount(0);
        for (MenuItem item : restaurant.getMenu()) {
            menuTableModel.addRow(new Object[]{item.getName(), String.format("%.2f", item.getPrice())});
        }
    }

    private void addItem() {
        JTextField nameField = new JTextField();
        JTextField priceField = new JTextField();
        Object[] message = {"Item Name:", nameField, "Price (Bdt):", priceField};
        int option = showCustomConfirmDialog(message, "Add New Menu Item", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText();
                double price = Double.parseDouble(priceField.getText());
                if (name.isEmpty()){
                    showErrorDialog("Item name cannot be empty.");
                    return;
                }
                restaurant.getMenu().add(new MenuItem(name, price));
                restaurantDatabase.addRestaurant(restaurant);
                refreshMenuTable();
            } catch (NumberFormatException ex) {
                showErrorDialog("Invalid price format. Please enter a number.");
            }
        }
    }

    private void deleteItem() {
        int selectedRow = menuTable.getSelectedRow();
        if (selectedRow != -1) {
            int confirm = showCustomConfirmDialog("Are you sure you want to delete this item?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                restaurant.getMenu().remove(selectedRow);
                restaurantDatabase.addRestaurant(restaurant);
                refreshMenuTable();
            }
        } else {
            showErrorDialog("Please select an item from the table to delete.");
        }
    }

    private void updateItemPrice() {
        int selectedRow = menuTable.getSelectedRow();
        if (selectedRow != -1) {
            MenuItem item = restaurant.getMenu().get(selectedRow);
            String newPriceStr = showCustomInputDialog("Enter new price for " + item.getName() + ":", String.valueOf(item.getPrice()));
            if(newPriceStr != null && !newPriceStr.isEmpty()) {
                try {
                    double newPrice = Double.parseDouble(newPriceStr);
                    item = new MenuItem(item.getName(), newPrice);
                    restaurant.getMenu().set(selectedRow, item);
                    restaurantDatabase.addRestaurant(restaurant);
                    refreshMenuTable();
                } catch (NumberFormatException ex) {
                    showErrorDialog("Invalid price format. Please enter a number.");
                }
            }
        } else {
            showErrorDialog("Please select an item from the table to update.");
        }
    }

    private void showErrorDialog(String message) {
        UIManager.put("OptionPane.background", new Color(43, 43, 43));
        UIManager.put("Panel.background", new Color(43, 43, 43));
        UIManager.put("OptionPane.messageForeground", Color.WHITE);
        UIManager.put("Button.background", new Color(255, 102, 0));
        UIManager.put("Button.foreground", Color.WHITE);

        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);

        // Reset to default
        UIManager.put("OptionPane.background", null);
        UIManager.put("Panel.background", null);
        UIManager.put("OptionPane.messageForeground", null);
        UIManager.put("Button.background", null);
        UIManager.put("Button.foreground", null);
    }

    private int showCustomConfirmDialog(Object message, String title, int optionType) {
        UIManager.put("OptionPane.background", new Color(43, 43, 43));
        UIManager.put("Panel.background", new Color(43, 43, 43));
        UIManager.put("OptionPane.messageForeground", Color.WHITE);
        UIManager.put("Button.background", new Color(255, 102, 0));
        UIManager.put("Button.foreground", Color.WHITE);

        int result = JOptionPane.showConfirmDialog(this, message, title, optionType);

        // Reset to default
        UIManager.put("OptionPane.background", null);
        UIManager.put("Panel.background", null);
        UIManager.put("OptionPane.messageForeground", null);
        UIManager.put("Button.background", null);
        UIManager.put("Button.foreground", null);

        return result;
    }

    private String showCustomInputDialog(String message, String initialValue) {
        UIManager.put("OptionPane.background", new Color(43, 43, 43));
        UIManager.put("Panel.background", new Color(43, 43, 43));
        UIManager.put("OptionPane.messageForeground", Color.WHITE);
        UIManager.put("Button.background", new Color(255, 102, 0));
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("TextField.background", new Color(60, 63, 65));
        UIManager.put("TextField.foreground", Color.WHITE);
        UIManager.put("TextField.caretForeground", Color.WHITE);


        String result = JOptionPane.showInputDialog(this, message, initialValue);

        // Reset to default
        UIManager.put("OptionPane.background", null);
        UIManager.put("Panel.background", null);
        UIManager.put("OptionPane.messageForeground", null);
        UIManager.put("Button.background", null);
        UIManager.put("Button.foreground", null);
        UIManager.put("TextField.background", null);
        UIManager.put("TextField.foreground", null);
        UIManager.put("TextField.caretForeground", null);


        return result;
    }
}