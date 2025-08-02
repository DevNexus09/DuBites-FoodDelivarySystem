package com.foodordersystem.model.entities;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class MenuItem implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String name;
    private final double price;
    private int quantity;

    private transient JCheckBox checkBox;
    private transient JTextField quantityField;

    public MenuItem(String name, double price) {
        this.name = name;
        this.price = price;
        this.quantity = 0;
        reinitializeUIComponents();
    }

    public void reinitializeUIComponents() {
        if (this.checkBox == null) {
            this.checkBox = new JCheckBox();
        }
        if (this.quantityField == null) {
            this.quantityField = new JTextField(String.valueOf(this.quantity));
        }
    }

    public void syncQuantityFromTextField() {
        try {
            this.quantity = Integer.parseInt(quantityField.getText());
        } catch (NumberFormatException e) {
            this.quantity = 0;
        }
    }

    public double getCost() {
        return price * quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public JCheckBox getCheckBox() { return checkBox; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public JTextField getQuantityField() { return quantityField; }
    public boolean isSelected() {
        return this.quantity > 0;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        reinitializeUIComponents();
    }
}