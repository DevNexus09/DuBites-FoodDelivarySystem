package com.foodordersystem.ui.customer;

import com.foodordersystem.core.UIManager;
import com.foodordersystem.database.OrderDatabase;
import com.foodordersystem.model.entities.MenuItem;
import com.foodordersystem.model.entities.Order;
import com.foodordersystem.model.entities.Restaurant;
import com.foodordersystem.model.entities.User;
import com.foodordersystem.ui.common.BaseFrame;
import com.foodordersystem.ui.common.ImagePanel;
import com.foodordersystem.ui.common.Receipt;
import com.foodordersystem.ui.common.RoundedButton;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.List;
import java.util.stream.Collectors;

public class FoodOrderSystem extends BaseFrame {

    private final Restaurant restaurant;
    private final User customer;
    private Order order;
    private UIManager uiManager;
    private JTextField jtxtDeliveryCharge, jtxtSub, jtxtTotal;
    private JTextArea jtxtReceipt;
    private JButton jbtnConfirmOrder;

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
            item.getQuantityField().setText("0");
            item.getCheckBox().setSelected(false);
        }
        order = new Order(menuItems, restaurant, customer);
        uiManager = new UIManager(menuItems);
    }

    @Override
    protected void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);

        ImagePanel backgroundPanel = new ImagePanel("/com/foodordersystem/Resources/SignUpFrameBg.png", 1.0f);
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.6;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 0, 0, 10);
        JScrollPane menuScrollPane = createMenuPanel();
        contentPanel.add(menuScrollPane, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.4;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 10, 0, 0);
        JPanel orderPanel = createOrderPanel();
        contentPanel.add(orderPanel, gbc);

        JPanel buttonPanel = createButtonPanel();

        backgroundPanel.add(contentPanel, BorderLayout.CENTER);
        backgroundPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    // A custom circular button for quantity controls
    class CircularButton extends JButton {
        public CircularButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setPreferredSize(new Dimension(30, 30));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (getModel().isArmed()) {
                g2.setColor(getBackground().darker());
            } else {
                g2.setColor(getBackground());
            }
            g2.fill(new Ellipse2D.Float(0, 0, getWidth(), getHeight()));

            // Draw the text in the center
            g2.setColor(getForeground());
            g2.setFont(getFont());
            FontMetrics metrics = g2.getFontMetrics(getFont());
            int x = (getWidth() - metrics.stringWidth(getText())) / 2;
            int y = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();
            g2.drawString(getText(), x, y);

            g2.dispose();
        }
    }

    // A custom panel for each menu item card
    class MenuItemCard extends JPanel {
        private final MenuItem menuItem;

        public MenuItemCard(MenuItem item) {
            this.menuItem = item;
            setLayout(new BorderLayout(10, 10));
            setOpaque(false);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(255, 255, 255, 80)),
                    BorderFactory.createEmptyBorder(10, 15, 10, 15)
            ));

            JLabel nameLabel = new JLabel(menuItem.getName());
            nameLabel.setFont(new Font("Dialog", Font.BOLD, 16));
            nameLabel.setForeground(Color.WHITE);

            JLabel priceLabel = new JLabel(String.format("Bdt %.2f", menuItem.getPrice()));
            priceLabel.setFont(new Font("Dialog", Font.ITALIC, 14));
            priceLabel.setForeground(Color.LIGHT_GRAY);

            JPanel infoPanel = new JPanel();
            infoPanel.setOpaque(false);
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
            infoPanel.add(nameLabel);
            infoPanel.add(priceLabel);

            // Quantity controls
            JPanel quantityPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            quantityPanel.setOpaque(false);
            JButton minusButton = new CircularButton("-");
            JButton plusButton = new CircularButton("+");
            JTextField quantityField = menuItem.getQuantityField();

            // Style the quantity field
            quantityField.setPreferredSize(new Dimension(40, 30));
            quantityField.setHorizontalAlignment(JTextField.CENTER);
            quantityField.setEditable(false);
            quantityField.setFont(new Font("Dialog", Font.BOLD, 16));
            quantityField.setForeground(Color.WHITE);
            quantityField.setBackground(new Color(0,0,0,0)); // Transparent background
            quantityField.setOpaque(false);
            quantityField.setBorder(null);

            styleQuantityButton(minusButton);
            styleQuantityButton(plusButton);

            quantityPanel.add(minusButton);
            quantityPanel.add(quantityField);
            quantityPanel.add(plusButton);

            minusButton.addActionListener(e -> {
                int quantity = Integer.parseInt(quantityField.getText());
                if (quantity > 0) {
                    quantity--;
                    quantityField.setText(String.valueOf(quantity));
                    menuItem.getCheckBox().setSelected(quantity > 0);
                }
            });

            plusButton.addActionListener(e -> {
                int quantity = Integer.parseInt(quantityField.getText());
                quantity++;
                quantityField.setText(String.valueOf(quantity));
                menuItem.getCheckBox().setSelected(true);
            });

            add(infoPanel, BorderLayout.CENTER);
            add(quantityPanel, BorderLayout.EAST);
        }

        private void styleQuantityButton(JButton button) {
            button.setFont(new Font("Dialog", Font.BOLD, 20));
            button.setBackground(new Color(50, 50, 50, 200));
            button.setForeground(Color.WHITE);
        }
    }

    private JScrollPane createMenuPanel() {
        JPanel menuPanel = createTitledPanel("Select Your Dish");
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));

        List<MenuItem> menuItems = restaurant.getMenu();
        for (MenuItem item : menuItems) {
            menuPanel.add(new MenuItemCard(item));
            menuPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        JScrollPane menuScrollPane = new JScrollPane(menuPanel);
        styleScrollPane(menuScrollPane);
        return menuScrollPane;
    }

    private JPanel createOrderPanel() {
        JPanel orderPanel = createTitledPanel("Your Order Summary");
        orderPanel.setLayout(new BorderLayout(10, 10));

        JPanel costPanel = new JPanel(new GridLayout(3, 2, 10, 15));
        costPanel.setOpaque(false);
        costPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        Font labelFont = new Font("DialogInput", Font.BOLD, 14);
        jtxtDeliveryCharge = styleTextField(new JTextField("0.00"));
        jtxtSub = styleTextField(new JTextField("0.00"));
        jtxtTotal = styleTextField(new JTextField("0.00"));

        costPanel.add(createLabel("Delivery Charge:", labelFont));
        costPanel.add(jtxtDeliveryCharge);
        costPanel.add(createLabel("SubTotal:", labelFont));
        costPanel.add(jtxtSub);
        costPanel.add(createLabel("Total:", labelFont));
        costPanel.add(jtxtTotal);
        orderPanel.add(costPanel, BorderLayout.NORTH);

        jtxtReceipt = new JTextArea();
        styleReceiptArea(jtxtReceipt);
        JScrollPane receiptScrollPane = new JScrollPane(jtxtReceipt);
        styleScrollPane(receiptScrollPane);
        receiptScrollPane.getViewport().setOpaque(true);
        receiptScrollPane.getViewport().setBackground(Color.BLACK);
        receiptScrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        orderPanel.add(receiptScrollPane, BorderLayout.CENTER);

        return orderPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JButton jbtnCheckout = new RoundedButton("Checkout");
        jbtnConfirmOrder = new RoundedButton("Confirm Order");
        JButton jbtnReset = new RoundedButton("Reset");
        JButton jbtnBack = new RoundedButton("Back");

        styleOrderButton(jbtnCheckout, new Color(0, 122, 204), new Dimension(150, 40));
        styleOrderButton(jbtnConfirmOrder, new Color(45, 137, 45), new Dimension(150, 40));
        styleOrderButton(jbtnReset, new Color(192, 57, 43), new Dimension(150, 40));
        styleOrderButton(jbtnBack, new Color(100, 100, 100), new Dimension(150, 40));

        jbtnConfirmOrder.setEnabled(false);

        jbtnCheckout.addActionListener(e -> updateTotal());
        jbtnConfirmOrder.addActionListener(e -> confirmOrder());
        jbtnReset.addActionListener(e -> resetOrder());
        jbtnBack.addActionListener(e -> backToRestaurants());

        buttonPanel.add(jbtnCheckout);
        buttonPanel.add(jbtnConfirmOrder);
        buttonPanel.add(jbtnReset);
        buttonPanel.add(jbtnBack);

        return buttonPanel;
    }

    private void updateTotal() {
        order.calculateTotals();
        jtxtDeliveryCharge.setText(String.format("%.2f", order.getDeliveryCharge()));
        jtxtSub.setText(String.format("%.2f", order.getSubTotal()));
        jtxtTotal.setText(String.format("%.2f", order.getTotal()));
        jtxtReceipt.setText(Receipt.generateReceipt(order));

        jbtnConfirmOrder.setEnabled(order.getTotal() > 0);
    }

    private void confirmOrder() {
        List<MenuItem> selectedItems = order.getMenuItems().stream()
                .filter(MenuItem::isSelected)
                .collect(Collectors.toList());

        if (selectedItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select at least one item.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        new OrderDatabase().addOrder(order);
        JOptionPane.showMessageDialog(this, "Your order has been placed!", "Order Confirmed", JOptionPane.INFORMATION_MESSAGE);
        backToRestaurants();
    }

    private void resetOrder() {
        for(MenuItem item : restaurant.getMenu()) {
            item.getQuantityField().setText("0");
            item.getCheckBox().setSelected(false);
        }
        updateTotal();
        jtxtReceipt.setText("");
        jbtnConfirmOrder.setEnabled(false);
    }

    private void backToRestaurants() {
        new RestaurantSelectionFrame(customer).setVisible(true);
        dispose();
    }

    private JPanel createTitledPanel(String title) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.WHITE, 1), title,
                TitledBorder.CENTER, TitledBorder.TOP,
                new Font("DialogInput", Font.BOLD, 20), Color.WHITE
        ));
        return panel;
    }

    private void styleScrollPane(JScrollPane scrollPane) {
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
    }

    private JLabel createLabel(String text, Font font) {
        return new JLabel(text) {{
            setFont(font);
            setForeground(Color.WHITE);
        }};
    }

    private JTextField styleTextField(JTextField textField) {
        textField.setEditable(false);
        textField.setFont(new Font("Monospaced", Font.BOLD, 16));
        textField.setForeground(new Color(200, 255, 200));
        textField.setBackground(Color.BLACK);
        textField.setOpaque(true);
        textField.setHorizontalAlignment(JTextField.RIGHT);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return textField;
    }

    private void styleReceiptArea(JTextArea textArea) {
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        textArea.setForeground(Color.WHITE);
        textArea.setBackground(Color.BLACK);
        textArea.setOpaque(true);
        textArea.setMargin(new Insets(10, 10, 10, 10));
    }

    private void styleOrderButton(JButton button, Color color, Dimension size) {
        button.setFont(new Font("DialogInput", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setPreferredSize(size);
    }
}