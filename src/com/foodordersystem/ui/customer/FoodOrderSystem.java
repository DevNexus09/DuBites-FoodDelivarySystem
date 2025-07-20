package com.foodordersystem.ui.customer;

import com.foodordersystem.core.UIManager;
import com.foodordersystem.database.OrderDatabase;
import com.foodordersystem.model.entities.MenuItem;
import com.foodordersystem.model.entities.Order;
import com.foodordersystem.model.entities.Restaurant;
import com.foodordersystem.model.entities.User;
import com.foodordersystem.ui.common.BaseFrame;
import com.foodordersystem.ui.common.Receipt;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class FoodOrderSystem extends BaseFrame {

    private final Restaurant restaurant;
    private final User customer;
    private Order order;
    private com.foodordersystem.core.UIManager uiManager;
    private JTextField jtxtTax, jtxtSub, jtxtTotal;
    private JTextArea jtxtReceipt;

    public FoodOrderSystem(Restaurant restaurant, User customer) {
        super(restaurant.getName() + " - Food Ordering System", 1000, 700);
        this.restaurant = restaurant;
        this.customer = customer;
        initializeModels();
        initComponents();
    }

    private void initializeModels() {
        List<MenuItem> menuItems = restaurant.getMenu();
        for (MenuItem item : menuItems) {
            item.reinitializeUIComponents();
        }
        order = new Order(menuItems, restaurant, customer);
        uiManager = new UIManager(menuItems);
        uiManager.setupEventListeners();
        for (MenuItem item : menuItems) {
            item.getCheckBox().setEnabled(true);
        }
    }

    protected void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // --- West Panel for Menu Items ---
        JPanel menuPanel = new JPanel();
        menuPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2),
                "Select Dish",
                javax.swing.border.TitledBorder.CENTER,
                javax.swing.border.TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 24)
        ));
        menuPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        List<MenuItem> menuItems = restaurant.getMenu();
        int gridY = 0;
        for (MenuItem item : menuItems) {
            gbc.gridx = 0;
            gbc.gridy = gridY;
            gbc.weightx = 1.0;
            menuPanel.add(item.getCheckBox(), gbc);
            gbc.gridx = 1;
            gbc.weightx = 0.1;
            item.getQuantityField().setHorizontalAlignment(JTextField.CENTER);
            menuPanel.add(item.getQuantityField(), gbc);
            gridY++;
        }
        JScrollPane menuScrollPane = new JScrollPane(menuPanel);
        add(menuScrollPane, BorderLayout.CENTER);

        // --- East Panel for Totals and Receipt ---
        JPanel eastPanel = new JPanel();
        eastPanel.setLayout(new BorderLayout());
        eastPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2),
                "Cost Of Item And Receipt",
                javax.swing.border.TitledBorder.CENTER,
                javax.swing.border.TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 24)
        ));

        JPanel costPanel = new JPanel(new GridLayout(3, 2, 5, 10));
        costPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jtxtTax = new JTextField("0", 10);
        jtxtSub = new JTextField("0", 10);
        jtxtTotal = new JTextField("0", 10);
        jtxtTax.setEditable(false);
        jtxtSub.setEditable(false);
        jtxtTotal.setEditable(false);
        costPanel.add(new JLabel("Tax:"));
        costPanel.add(jtxtTax);
        costPanel.add(new JLabel("SubTotal:"));
        costPanel.add(jtxtSub);
        costPanel.add(new JLabel("Total:"));
        costPanel.add(jtxtTotal);

        jtxtReceipt = new JTextArea(15, 30);
        jtxtReceipt.setEditable(false);
        jtxtReceipt.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane receiptScrollPane = new JScrollPane(jtxtReceipt);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton jbtnTotal = new JButton("Total");
        JButton jbtnConfirmOrder = new JButton("Confirm Order");
        JButton jbtnReset = new JButton("Reset");
        JButton jbtnBack = new JButton("Back to Restaurants");
        buttonPanel.add(jbtnTotal);
        buttonPanel.add(jbtnConfirmOrder);
        buttonPanel.add(jbtnReset);
        buttonPanel.add(jbtnBack);
        eastPanel.add(costPanel, BorderLayout.NORTH);
        eastPanel.add(receiptScrollPane, BorderLayout.CENTER);
        eastPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(eastPanel, BorderLayout.EAST);

        // --- Action Listeners for Buttons ---
        jbtnTotal.addActionListener(e -> {
            order.calculateTotals();
            jtxtTax.setText(String.format("Rs %.2f", order.getDelivaryCharge()));
            jtxtSub.setText(String.format("Rs %.2f", order.getSubTotal()));
            jtxtTotal.setText(String.format("Rs %.2f", order.getTotal()));
            jtxtReceipt.setText(Receipt.generateReceipt(order));
        });

        jbtnConfirmOrder.addActionListener(e -> {
            order.calculateTotals();
            List<MenuItem> selectedItems = order.getMenuItems().stream()
                    .filter(MenuItem::isSelected)
                    .collect(Collectors.toList());

            if (selectedItems.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select at least one item.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Order finalOrder = new Order(selectedItems, restaurant, customer);
            finalOrder.calculateTotals();

            new OrderDatabase().addOrder(finalOrder);

            JOptionPane.showMessageDialog(this, "Your order has been placed and is waiting for a rider!", "Order Confirmed", JOptionPane.INFORMATION_MESSAGE);

            new RestaurantSelectionFrame(customer).setVisible(true);
            dispose();
        });

        jbtnReset.addActionListener(e -> {
            uiManager.resetUI(jtxtTax, jtxtSub, jtxtTotal, jtxtReceipt);
            for (MenuItem item : restaurant.getMenu()) {
                item.getCheckBox().setEnabled(true);
            }
        });

        jbtnBack.addActionListener(e -> {
            new RestaurantSelectionFrame(customer).setVisible(true);
            dispose();
        });

        pack();
        setLocationRelativeTo(null);
    }
}