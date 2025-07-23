package com.foodordersystem.model.entities;

import javax.swing.JCheckBox;
import javax.swing.JTextField;
import java.io.Serializable;

public class MenuItem implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String name;
    private final double price;

    private transient JCheckBox checkBox;
    private transient JTextField quantityField;

    public MenuItem(String name, double price) {
        this.name = name;
        this.price = price;
        reinitializeUIComponents();
    }

    public void reinitializeUIComponents() {
        if (this.checkBox == null) {
            // Checkbox is now just for state, not display
            this.checkBox = new JCheckBox();
        }
        if (this.quantityField == null) {
            this.quantityField = new JTextField("0");
        }
    }

    public double getCost() {
        try {
            int quantity = Integer.parseInt(quantityField.getText());
            return price * quantity;
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    public JCheckBox getCheckBox() { return checkBox; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public JTextField getQuantityField() { return quantityField; }
    public boolean isSelected() {
        return Integer.parseInt(quantityField.getText()) > 0;
    }
}