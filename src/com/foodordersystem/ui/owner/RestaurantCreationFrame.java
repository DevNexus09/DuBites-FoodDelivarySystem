package com.foodordersystem.ui.owner;

import com.foodordersystem.database.RestaurantDatabase;
import com.foodordersystem.model.entities.MenuItem;
import com.foodordersystem.model.entities.Restaurant;
import com.foodordersystem.model.entities.User;
import com.foodordersystem.ui.common.BaseFrame;
import com.foodordersystem.ui.common.ImagePanel;
import com.foodordersystem.ui.common.RoundedButton;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class RestaurantCreationFrame extends BaseFrame {
    private final User owner;
    private final List<MenuItem> initialMenu = new ArrayList<>();
    private final DefaultListModel<String> menuListModel = new DefaultListModel<>();
    private String selectedImagePath;
    private JLabel imagePreviewLabel;

    public RestaurantCreationFrame(User owner) {
        super("Create a New Restaurant", 900, 700);
        this.owner = owner;

        if (new RestaurantDatabase().findRestaurantByOwner(owner.getUsername()) != null) {
            SwingUtilities.invokeLater(() -> {
                showErrorDialog("You can only create one restaurant per account.");
                new OwnerDashboardFrame(owner).setVisible(true);
                dispose();
            });
        } else {
            initComponents();
        }
    }

    protected void initComponents() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        ImagePanel backgroundPanel = new ImagePanel("/com/foodordersystem/Resources/RestaurantCreationFrameBg.png", 1.0f);
        backgroundPanel.setLayout(new BorderLayout(10, 10));
        backgroundPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setContentPane(backgroundPanel);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Restaurant Details ---
        gbc.gridx = 0; gbc.gridy = 0; addField(formPanel, gbc, "Restaurant Name:");
        JTextField nameField = new JTextField(20);
        gbc.gridx = 1; formPanel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; addField(formPanel, gbc, "Location:");
        JTextField locationField = new JTextField(20);
        gbc.gridx = 1; formPanel.add(locationField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; addField(formPanel, gbc, "Cuisine (e.g., Italian):");
        JTextField cuisineField = new JTextField(20);
        gbc.gridx = 1; formPanel.add(cuisineField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; addField(formPanel, gbc, "4-Digit Security PIN:");
        JPasswordField pinField = new JPasswordField(20);
        gbc.gridx = 1; formPanel.add(pinField, gbc);

        gbc.gridx = 0; gbc.gridy = 4; addField(formPanel, gbc, "Delivery Time (in minutes):");
        JTextField deliveryTimeField = new JTextField(20);
        gbc.gridx = 1; formPanel.add(deliveryTimeField, gbc);

        gbc.gridx = 0; gbc.gridy = 5; addField(formPanel, gbc, "Price Range (e.g., $, $$, $$$):");
        JTextField priceRangeField = new JTextField(20);
        gbc.gridx = 1; formPanel.add(priceRangeField, gbc);


        // --- Image Upload ---
        JButton chooseImageButton = new RoundedButton("Choose Restaurant Image");
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(chooseImageButton, gbc);

        imagePreviewLabel = new JLabel("Image Preview", SwingConstants.CENTER);
        imagePreviewLabel.setPreferredSize(new Dimension(150, 150));
        imagePreviewLabel.setBorder(BorderFactory.createDashedBorder(Color.WHITE));
        imagePreviewLabel.setForeground(Color.WHITE);
        gbc.gridx = 2; gbc.gridy = 0; gbc.gridheight = 7; gbc.insets = new Insets(10, 30, 10, 10);
        formPanel.add(imagePreviewLabel, gbc);
        gbc.gridheight = 1; gbc.insets = new Insets(10, 10, 10, 10);

        // --- Menu Section ---
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 3; gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(new JSeparator(), gbc);

        gbc.gridy = 8;
        JLabel menuTitle = new JLabel("Initial Menu");
        menuTitle.setFont(new Font("DialogInput", Font.BOLD, 16));
        menuTitle.setForeground(Color.WHITE);
        formPanel.add(menuTitle, gbc);

        JList<String> menuJList = new JList<>(menuListModel);
        menuJList.setOpaque(false);
        ((JComponent)menuJList.getCellRenderer()).setOpaque(false);
        menuJList.setForeground(Color.WHITE);
        JScrollPane menuScrollPane = new JScrollPane(menuJList);
        menuScrollPane.setOpaque(false);
        menuScrollPane.getViewport().setOpaque(false);
        gbc.gridy = 9; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1.0;
        formPanel.add(menuScrollPane, gbc);
        gbc.weighty = 0; gbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel menuItemInputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        menuItemInputPanel.setOpaque(false);
        JTextField itemNameField = new JTextField(15);
        JTextField itemPriceField = new JTextField(5);
        JTextField itemCategoryField = new JTextField(10);
        JButton addItemButton = new RoundedButton("Add Item");

        menuItemInputPanel.add(new JLabel("Item Name:") {{ setForeground(Color.WHITE); }});
        menuItemInputPanel.add(itemNameField);
        menuItemInputPanel.add(new JLabel("Price:") {{ setForeground(Color.WHITE); }});
        menuItemInputPanel.add(itemPriceField);
        menuItemInputPanel.add(new JLabel("Category:") {{ setForeground(Color.WHITE); }});
        menuItemInputPanel.add(itemCategoryField);
        menuItemInputPanel.add(addItemButton);
        gbc.gridy = 10;
        formPanel.add(menuItemInputPanel, gbc);

        // --- Action Buttons ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        JButton createRestaurantButton = new RoundedButton("Create Restaurant");
        JButton backButton = new RoundedButton("Back to Dashboard");
        bottomPanel.add(createRestaurantButton);
        bottomPanel.add(backButton);

        backgroundPanel.add(formPanel, BorderLayout.CENTER);
        backgroundPanel.add(bottomPanel, BorderLayout.SOUTH);

        // --- Action Listeners ---
        chooseImageButton.addActionListener(e -> chooseImage());
        addItemButton.addActionListener(e -> addItemToMenu(itemNameField, itemPriceField, itemCategoryField));
        createRestaurantButton.addActionListener(e -> createRestaurant(nameField, locationField, cuisineField, pinField, deliveryTimeField, priceRangeField));
        backButton.addActionListener(e -> {
            new OwnerDashboardFrame(owner).setVisible(true);
            dispose();
        });
    }

    private void addField(JPanel panel, GridBagConstraints gbc, String labelText) {
        JLabel label = new JLabel(labelText);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Dialog", Font.BOLD, 14));
        panel.add(label, gbc);
    }

    private void chooseImage() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                File destDir = new File("out/production/foodorder/com/foodordersystem/Resources");
                if (!destDir.exists()) destDir.mkdirs();
                Files.copy(selectedFile.toPath(), new File(destDir, selectedFile.getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
                selectedImagePath = "/com/foodordersystem/Resources/" + selectedFile.getName();
                ImageIcon imageIcon = new ImageIcon(new ImageIcon(getClass().getResource(selectedImagePath)).getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH));
                imagePreviewLabel.setIcon(imageIcon);
                imagePreviewLabel.setText("");
                imagePreviewLabel.setBorder(null);
            } catch (Exception ex) {
                showErrorDialog("Error saving or loading image.");
            }
        }
    }

    private void addItemToMenu(JTextField nameField, JTextField priceField, JTextField categoryField) {
        try {
            String name = nameField.getText();
            String category = categoryField.getText();
            if (name.isEmpty() || category.isEmpty()) {
                showErrorDialog("Item name and category cannot be empty.");
                return;
            }
            double price = Double.parseDouble(priceField.getText());
            initialMenu.add(new MenuItem(name, price, category));
            menuListModel.addElement(String.format("[%s] %s - Bdt %.2f", category, name, price));
            nameField.setText("");
            priceField.setText("");
            categoryField.setText("");
        } catch (NumberFormatException ex) {
            showErrorDialog("Please enter a valid price.");
        }
    }

    private void createRestaurant(JTextField nameField, JTextField locationField, JTextField cuisineField, JPasswordField pinField, JTextField deliveryTimeField, JTextField priceRangeField) {
        String rName = nameField.getText();
        String rLocation = locationField.getText();
        String rCuisine = cuisineField.getText();
        String rPin = new String(pinField.getPassword());
        String deliveryTimeText = deliveryTimeField.getText();
        String rPriceRange = priceRangeField.getText();

        if (rName.isEmpty() || rLocation.isEmpty() || rCuisine.isEmpty() || selectedImagePath == null || !Pattern.matches("\\d{4}", rPin) || deliveryTimeText.isEmpty() || rPriceRange.isEmpty()) {
            showErrorDialog("Please fill all fields, choose an image, and use a 4-digit PIN.");
            return;
        }

        int deliveryTime;
        try {
            deliveryTime = Integer.parseInt(deliveryTimeText);
        } catch (NumberFormatException e) {
            showErrorDialog("Please enter a valid number for delivery time.");
            return;
        }


        RestaurantDatabase db = new RestaurantDatabase();
        Restaurant newRestaurant = new Restaurant(rName, owner.getUsername(), rLocation, rPin, selectedImagePath, rCuisine, deliveryTime, rPriceRange);
        newRestaurant.setMenu(initialMenu);
        db.addRestaurant(newRestaurant);

        showSuccessDialog("Restaurant created successfully!");
        new OwnerDashboardFrame(owner).setVisible(true);
        dispose();
    }

    private void showErrorDialog(String message) {
        UIManager.put("OptionPane.background", new Color(43, 43, 43));
        UIManager.put("Panel.background", new Color(43, 43, 43));
        UIManager.put("OptionPane.messageForeground", Color.WHITE);
        UIManager.put("Button.background", new Color(255, 102, 0));
        UIManager.put("Button.foreground", Color.WHITE);
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
        UIManager.put("OptionPane.background", null);
        UIManager.put("Panel.background", null);
        UIManager.put("OptionPane.messageForeground", null);
        UIManager.put("Button.background", null);
        UIManager.put("Button.foreground", null);
    }

    private void showSuccessDialog(String message) {
        UIManager.put("OptionPane.background", new Color(43, 43, 43));
        UIManager.put("Panel.background", new Color(43, 43, 43));
        UIManager.put("OptionPane.messageForeground", Color.WHITE);
        UIManager.put("Button.background", new Color(45, 137, 45));
        UIManager.put("Button.foreground", Color.WHITE);
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
        UIManager.put("OptionPane.background", null);
        UIManager.put("Panel.background", null);
        UIManager.put("OptionPane.messageForeground", null);
        UIManager.put("Button.background", null);
        UIManager.put("Button.foreground", null);
    }
}