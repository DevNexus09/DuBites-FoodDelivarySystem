package com.foodordersystem.ui.owner;

import com.foodordersystem.database.RestaurantDatabase;
import com.foodordersystem.model.entities.MenuItem;
import com.foodordersystem.model.entities.Restaurant;
import com.foodordersystem.model.entities.User;
import com.foodordersystem.ui.common.BaseFrame;

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

    public RestaurantCreationFrame(User owner) {
        super("Create a New Restaurant", 800, 600);
        this.owner = owner;
        initComponents();
    }

    protected void initComponents() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Restaurant Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameField = new JTextField(20);
        JTextField locationField = new JTextField(20);
        JTextField ownerUsernameField = new JTextField(owner.getUsername(), 20);
        ownerUsernameField.setEditable(false);
        JPasswordField pinField = new JPasswordField(20);
        JButton chooseImageButton = new JButton("Choose Image");
        JLabel selectedImageLabel = new JLabel("No image selected.");

        gbc.gridx = 0; gbc.gridy = 0; detailsPanel.add(new JLabel("Restaurant Name:"), gbc);
        gbc.gridx = 1; detailsPanel.add(nameField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; detailsPanel.add(new JLabel("Location:"), gbc);
        gbc.gridx = 1; detailsPanel.add(locationField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; detailsPanel.add(new JLabel("Owner Username:"), gbc);
        gbc.gridx = 1; detailsPanel.add(ownerUsernameField, gbc);
        gbc.gridx = 0; gbc.gridy = 3; detailsPanel.add(new JLabel("Set a Unique 4-Digit PIN:"), gbc);
        gbc.gridx = 1; detailsPanel.add(pinField, gbc);
        gbc.gridx = 0; gbc.gridy = 4; detailsPanel.add(chooseImageButton, gbc);
        gbc.gridx = 1; detailsPanel.add(selectedImageLabel, gbc);


        JPanel menuPanel = new JPanel(new BorderLayout());
        menuPanel.setBorder(BorderFactory.createTitledBorder("Add Initial Menu Items"));
        JList<String> menuJList = new JList<>(menuListModel);
        menuPanel.add(new JScrollPane(menuJList), BorderLayout.CENTER);

        JPanel menuItemInputPanel = new JPanel(new FlowLayout());
        JTextField itemNameField = new JTextField(15);
        JTextField itemPriceField = new JTextField(5);
        JButton addItemButton = new JButton("Add Item");
        menuItemInputPanel.add(new JLabel("Item Name:"));
        menuItemInputPanel.add(itemNameField);
        menuItemInputPanel.add(new JLabel("Price:"));
        menuItemInputPanel.add(itemPriceField);
        menuItemInputPanel.add(addItemButton);
        menuPanel.add(menuItemInputPanel, BorderLayout.SOUTH);

        chooseImageButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                try {
                    File destDir = new File("src/com/foodordersystem/Resources");
                    if (!destDir.exists()) {
                        destDir.mkdirs();
                    }
                    Files.copy(selectedFile.toPath(), new File(destDir, selectedFile.getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
                    selectedImagePath = "/com/foodordersystem/Resources/" + selectedFile.getName();
                    selectedImageLabel.setText(selectedFile.getName());
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error saving image.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        addItemButton.addActionListener(e -> {
            try {
                String name = itemNameField.getText();
                double price = Double.parseDouble(itemPriceField.getText());
                if (!name.isEmpty()) {
                    initialMenu.add(new MenuItem(name, price));
                    menuListModel.addElement(name + " - Rs " + price);
                    itemNameField.setText("");
                    itemPriceField.setText("");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid price.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton createRestaurantButton = new JButton("Create Restaurant");
        JButton backButton = new JButton("Back");
        bottomPanel.add(createRestaurantButton);
        bottomPanel.add(backButton);

        createRestaurantButton.addActionListener(e -> {
            String rName = nameField.getText();
            String rLocation = locationField.getText();
            String rPin = new String(pinField.getPassword());

            if (rName.isEmpty() || rLocation.isEmpty() || rPin.length() != 4) {
                JOptionPane.showMessageDialog(this, "Please fill all fields and use a 4-digit PIN.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            RestaurantDatabase db = new RestaurantDatabase();
            Restaurant newRestaurant = new Restaurant(rName, owner.getUsername(), rLocation, rPin, selectedImagePath);
            newRestaurant.setMenu(initialMenu);
            db.addRestaurant(newRestaurant);

            JOptionPane.showMessageDialog(this, "Restaurant created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            new OwnerDashboardFrame(owner).setVisible(true);
            dispose();
        });

        backButton.addActionListener(e -> {
            new OwnerDashboardFrame(owner).setVisible(true);
            dispose();
        });

        add(detailsPanel, BorderLayout.NORTH);
        add(menuPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }
}