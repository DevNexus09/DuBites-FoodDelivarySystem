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

public class RestaurantCreationFrame extends BaseFrame {
    private final User owner;
    private final List<MenuItem> initialMenu = new ArrayList<>();
    private final DefaultListModel<String> menuListModel = new DefaultListModel<>();
    private String selectedImagePath;
    private JLabel imagePreviewLabel;

    public RestaurantCreationFrame(User owner) {
        super("Create a New Restaurant", 900, 700);
        this.owner = owner;
        initComponents();
    }

    protected void initComponents() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Use a background panel
        ImagePanel backgroundPanel = new ImagePanel("/com/foodordersystem/Resources/RestaurantCreationFrameBg.png", 1.0f);
        backgroundPanel.setLayout(new BorderLayout(10, 10));
        backgroundPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setContentPane(backgroundPanel);

        // Form Panel
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

        // --- Image Upload ---
        JButton chooseImageButton = new RoundedButton("Choose Restaurant Image");
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(chooseImageButton, gbc);

        imagePreviewLabel = new JLabel("Image Preview", SwingConstants.CENTER);
        imagePreviewLabel.setPreferredSize(new Dimension(150, 150));
        imagePreviewLabel.setBorder(BorderFactory.createDashedBorder(Color.WHITE));
        imagePreviewLabel.setForeground(Color.WHITE);
        gbc.gridx = 2; gbc.gridy = 0; gbc.gridheight = 5; gbc.insets = new Insets(10, 30, 10, 10);
        formPanel.add(imagePreviewLabel, gbc);
        gbc.gridheight = 1; gbc.insets = new Insets(10, 10, 10, 10); // Reset insets

        // --- Menu Section ---
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 3; gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(new JSeparator(), gbc);

        gbc.gridy = 6;
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
        gbc.gridy = 7; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1.0;
        formPanel.add(menuScrollPane, gbc);
        gbc.weighty = 0; gbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel menuItemInputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        menuItemInputPanel.setOpaque(false);
        JTextField itemNameField = new JTextField(15);
        JTextField itemPriceField = new JTextField(5);
        JButton addItemButton = new RoundedButton("Add Item");

        JLabel itemNameLabel = new JLabel("Item Name:");
        itemNameLabel.setForeground(Color.WHITE);
        menuItemInputPanel.add(itemNameLabel);

        menuItemInputPanel.add(itemNameField);

        JLabel priceLabel = new JLabel("Price:");
        priceLabel.setForeground(Color.WHITE);
        menuItemInputPanel.add(priceLabel);

        menuItemInputPanel.add(itemPriceField);
        menuItemInputPanel.add(addItemButton);
        gbc.gridy = 8;
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
        addItemButton.addActionListener(e -> addItemToMenu(itemNameField, itemPriceField));
        createRestaurantButton.addActionListener(e -> createRestaurant(nameField, locationField, cuisineField, pinField));
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
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
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
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error saving or loading image.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void addItemToMenu(JTextField nameField, JTextField priceField) {
        try {
            String name = nameField.getText();
            double price = Double.parseDouble(priceField.getText());
            if (!name.isEmpty()) {
                initialMenu.add(new MenuItem(name, price));
                menuListModel.addElement(String.format("%s - Bdt %.2f", name, price));
                nameField.setText("");
                priceField.setText("");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid price.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createRestaurant(JTextField nameField, JTextField locationField, JTextField cuisineField, JPasswordField pinField) {
        String rName = nameField.getText();
        String rLocation = locationField.getText();
        String rCuisine = cuisineField.getText();
        String rPin = new String(pinField.getPassword());

        if (rName.isEmpty() || rLocation.isEmpty() || rCuisine.isEmpty() || rPin.length() != 4 || selectedImagePath == null) {
            JOptionPane.showMessageDialog(this, "Please fill all fields, choose an image, and use a 4-digit PIN.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        RestaurantDatabase db = new RestaurantDatabase();
        Restaurant newRestaurant = new Restaurant(rName, owner.getUsername(), rLocation, rPin, selectedImagePath, rCuisine);
        newRestaurant.setMenu(initialMenu);
        db.addRestaurant(newRestaurant);

        JOptionPane.showMessageDialog(this, "Restaurant created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        new OwnerDashboardFrame(owner).setVisible(true);
        dispose();
    }
}