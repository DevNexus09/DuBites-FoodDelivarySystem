package com.foodordersystem.ui.customer;

import com.foodordersystem.core.UIManager;
import com.foodordersystem.database.OrderDatabase;
import com.foodordersystem.database.RestaurantDatabase;
import com.foodordersystem.database.UserDatabase;
import com.foodordersystem.model.entities.MenuItem;
import com.foodordersystem.model.entities.Order;
import com.foodordersystem.model.entities.Restaurant;
import com.foodordersystem.model.entities.User;
import com.foodordersystem.model.entities.Review;
import com.foodordersystem.ui.common.BaseFrame;
import com.foodordersystem.ui.common.ImagePanel;
import com.foodordersystem.ui.common.Receipt;
import com.foodordersystem.ui.common.RoundedButton;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FoodOrderSystem extends BaseFrame {

    private final Restaurant restaurant;
    private final User customer;
    private Order order;
    private UIManager uiManager;
    private JTextField jtxtDeliveryCharge, jtxtSub, jtxtTotal;
    private JTextArea jtxtReceipt;
    private JButton jbtnConfirmOrder;
    private JButton jbtnReview;

    public FoodOrderSystem(Restaurant restaurant, User customer) {
        super(restaurant.getName() + " - Food Ordering System", 1000, 700);
        this.restaurant = restaurant;
        this.customer = customer;
        initializeModels();
        initComponents();
    }

    public FoodOrderSystem(Order order) {
        super(order.getRestaurantName() + " - Reordering", 1000, 700);
        this.order = order;
        this.restaurant = new RestaurantDatabase().findRestaurantByName(order.getRestaurantName());
        this.customer = new UserDatabase().findUserByUsername(order.getCustomerUsername());
        initializeModels();
        initComponents();
        repopulateOrder();
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

    // repopulates quantities for reorder system
    private void repopulateOrder() {
        for (MenuItem pastItem : order.getMenuItems()) {
            for (MenuItem currentItem : restaurant.getMenu()) {
                if (pastItem.getName().equals(currentItem.getName())) {
                    currentItem.getQuantityField().setText(String.valueOf(pastItem.getQuantity()));
                    currentItem.getCheckBox().setSelected(true);
                }
            }
        }
        updateTotal();
    }


    @Override
    protected void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);

        ImagePanel backgroundPanel = new ImagePanel("/com/foodordersystem/Resources/ManagementDashboardFrameBg.png", 1.0f);
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
        JComponent menuComponent = createMenuPanel();
        contentPanel.add(menuComponent, gbc);

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
            setOpaque(false); // Make transparent to allow custom painting
            setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

            JLabel nameLabel = new JLabel(menuItem.getName());
            nameLabel.setFont(new Font("Dialog", Font.BOLD, 16));
            nameLabel.setForeground(Color.WHITE);

            JLabel priceLabel = new JLabel(String.format("Bdt %.2f", menuItem.getPrice()));
            priceLabel.setFont(new Font("Dialog", Font.ITALIC, 14));
            priceLabel.setForeground(Color.ORANGE);

            JPanel infoPanel = new JPanel();
            infoPanel.setOpaque(false);
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
            infoPanel.add(nameLabel);
            infoPanel.add(priceLabel);

            JPanel quantityPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            quantityPanel.setOpaque(false);
            JButton minusButton = new CircularButton("-");
            JButton plusButton = new CircularButton("+");
            JTextField quantityField = menuItem.getQuantityField();
            plusButton.setForeground(Color.LIGHT_GRAY);
            minusButton.setForeground(Color.LIGHT_GRAY);

            quantityField.setPreferredSize(new Dimension(40, 30));
            quantityField.setHorizontalAlignment(JTextField.CENTER);
            quantityField.setEditable(false);
            quantityField.setFont(new Font("Dialog", Font.BOLD, 16));
            quantityField.setForeground(Color.WHITE);
            quantityField.setBackground(new Color(0,0,0,0));
            quantityField.setOpaque(false);
            quantityField.setBorder(null);

            styleQuantityButton(minusButton);
            styleQuantityButton(plusButton);

            quantityPanel.add(minusButton);
            quantityPanel.add(quantityField);
            quantityPanel.add(plusButton);

            //Defining quantity by + - button
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

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(255, 255, 255, 20)); // Semi-transparent white background
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15); // Rounded corners
            g2.setColor(new Color(255, 255, 255, 80)); // Semi-transparent border
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
            g2.dispose();
            super.paintComponent(g);
        }

        private void styleQuantityButton(JButton button) {
            button.setFont(new Font("Dialog", Font.BOLD, 20));
            button.setBackground(new Color(50, 50, 50, 200));
            button.setForeground(Color.WHITE);
        }
    }

    // Making tabs for different category food items
    private JComponent createMenuPanel() {
        javax.swing.UIManager.put("TabbedPane.contentOpaque", false);
        javax.swing.UIManager.put("TabbedPane.background", new Color(0, 0, 0, 120));
        javax.swing.UIManager.put("TabbedPane.foreground", Color.WHITE);
        javax.swing.UIManager.put("TabbedPane.selected", new Color(255, 102, 0));
        javax.swing.UIManager.put("TabbedPane.focus", new Color(255, 102, 0, 50));
        javax.swing.UIManager.put("TabbedPane.borderHightlightColor", Color.DARK_GRAY);
        javax.swing.UIManager.put("TabbedPane.darkShadow", Color.DARK_GRAY);
        javax.swing.UIManager.put("TabbedPane.light", Color.DARK_GRAY);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setOpaque(false);
        tabbedPane.setFont(new Font("Dialog", Font.BOLD, 14));


        // Sorting items category wise
        Map<String, List<MenuItem>> groupedMenu = restaurant.getMenu().stream()
                .filter(MenuItem::isAvailable)
                .collect(Collectors.groupingBy(MenuItem::getCategory));

        for (Map.Entry<String, List<MenuItem>> entry : groupedMenu.entrySet()) {
            JPanel categoryPanel = new JPanel();
            categoryPanel.setLayout(new BoxLayout(categoryPanel, BoxLayout.Y_AXIS));
            categoryPanel.setOpaque(false);
            categoryPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

            for (MenuItem item : entry.getValue()) {
                categoryPanel.add(new MenuItemCard(item));
                categoryPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
            categoryPanel.add(Box.createVerticalGlue());

            JScrollPane scrollPane = new JScrollPane(categoryPanel);
            styleScrollPane(scrollPane);
            tabbedPane.addTab(entry.getKey(), scrollPane);
        }

        // Reset UIManager properties to avoid affecting other components
        javax.swing.UIManager.put("TabbedPane.contentOpaque", true);
        javax.swing.UIManager.put("TabbedPane.background", null);
        javax.swing.UIManager.put("TabbedPane.foreground", null);
        javax.swing.UIManager.put("TabbedPane.selected", null);
        javax.swing.UIManager.put("TabbedPane.focus", null);


        return tabbedPane;
    }

    // Order Bill Generation
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

    // Buttons
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JButton jbtnCheckout = new RoundedButton("Checkout");
        jbtnConfirmOrder = new RoundedButton("Confirm Order");
        jbtnReview = new RoundedButton("Review");
        JButton jbtnReset = new RoundedButton("Reset");
        JButton jbtnBack = new RoundedButton("Back");

        styleOrderButton(jbtnCheckout, new Color(0, 122, 204), new Dimension(150, 40));
        styleOrderButton(jbtnConfirmOrder, new Color(45, 137, 45), new Dimension(150, 40));
        styleOrderButton(jbtnReview, new Color(255, 153, 0), new Dimension(150, 40));
        styleOrderButton(jbtnReset, new Color(192, 57, 43), new Dimension(150, 40));
        styleOrderButton(jbtnBack, new Color(100, 100, 100), new Dimension(150, 40));

        jbtnConfirmOrder.setEnabled(false);
        jbtnReview.setEnabled(false);

        jbtnCheckout.addActionListener(e -> updateTotal());
        jbtnConfirmOrder.addActionListener(e -> confirmOrder());
        jbtnReview.addActionListener(e -> showReviewDialog());
        jbtnReset.addActionListener(e -> resetOrder());
        jbtnBack.addActionListener(e -> backToRestaurants());

        buttonPanel.add(jbtnCheckout);
        buttonPanel.add(jbtnConfirmOrder);
        buttonPanel.add(jbtnReview);
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

        if (order.getTotal() == 0) {
            showErrorDialog("Please select at least one item to proceed to checkout.");
            jbtnConfirmOrder.setEnabled(false);
        } else {
            jbtnConfirmOrder.setEnabled(true);
        }
    }

    private void confirmOrder() {
        order.finalizeQuantities();
        List<MenuItem> selectedItems = order.getMenuItems().stream()
                .filter(MenuItem::isSelected)
                .collect(Collectors.toList());

        if (selectedItems.isEmpty()) {
            showErrorDialog("Please select at least one item.");
            return;
        }

        new OrderDatabase().addOrder(order);
        restaurant.addOrder(order);
        new RestaurantDatabase().addRestaurant(restaurant);

        showSuccessDialog("Your order has been placed successfully! Thank you for choosing "+ restaurant.getName());
        jbtnReview.setEnabled(true);
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

    private void showReviewDialog() {
        JDialog reviewDialog = new JDialog(this, "Leave a Review", true);
        reviewDialog.setLayout(new BorderLayout(10, 10));
        reviewDialog.setSize(400, 300);
        reviewDialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Rating:"), gbc);

        gbc.gridx = 1;
        JComboBox<Integer> ratingComboBox = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
        panel.add(ratingComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.NORTH;
        panel.add(new JLabel("Comment:"), gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        JTextArea commentArea = new JTextArea(5, 20);
        JScrollPane scrollPane = new JScrollPane(commentArea);
        panel.add(scrollPane, gbc);

        JButton submitButton = new RoundedButton("Submit");
        submitButton.addActionListener(e -> {
            int rating = (int) ratingComboBox.getSelectedItem();
            String comment = commentArea.getText();
            Review review = new Review(customer.getUsername(), rating, comment);
            restaurant.addReview(review);
            new RestaurantDatabase().addRestaurant(restaurant);
            reviewDialog.dispose();
            showSuccessDialog("Thank you for your review!");
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(submitButton);

        reviewDialog.add(panel, BorderLayout.CENTER);
        reviewDialog.add(buttonPanel, BorderLayout.SOUTH);
        reviewDialog.setVisible(true);
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

    private void showErrorDialog(String message) {
        javax.swing.UIManager.put("OptionPane.background", new Color(43, 43, 43));
        javax.swing.UIManager.put("Panel.background", new Color(43, 43, 43));
        javax.swing.UIManager.put("OptionPane.messageForeground", Color.WHITE);
        javax.swing.UIManager.put("Button.background", new Color(255, 102, 0));
        javax.swing.UIManager.put("Button.foreground", Color.WHITE);
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
        javax.swing.UIManager.put("OptionPane.background", null);
        javax.swing.UIManager.put("Panel.background", null);
        javax.swing.UIManager.put("OptionPane.messageForeground", null);
        javax.swing.UIManager.put("Button.background", null);
        javax.swing.UIManager.put("Button.foreground", null);
    }

    private void showSuccessDialog(String message) {
        javax.swing.UIManager.put("OptionPane.background", new Color(43, 43, 43));
        javax.swing.UIManager.put("Panel.background", new Color(43, 43, 43));
        javax.swing.UIManager.put("OptionPane.messageForeground", Color.WHITE);
        javax.swing.UIManager.put("Button.background", new Color(45, 137, 45));
        javax.swing.UIManager.put("Button.foreground", Color.WHITE);
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
        javax.swing.UIManager.put("OptionPane.background", null);
        javax.swing.UIManager.put("Panel.background", null);
        javax.swing.UIManager.put("OptionPane.messageForeground", null);
        javax.swing.UIManager.put("Button.background", null);
        javax.swing.UIManager.put("Button.foreground", null);
    }
}