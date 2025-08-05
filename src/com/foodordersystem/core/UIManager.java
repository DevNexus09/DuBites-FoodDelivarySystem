package com.foodordersystem.core;

import com.foodordersystem.model.entities.MenuItem;

import javax.swing.JCheckBox;
import javax.swing.JTextField;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;


public class UIManager {

    private final List<MenuItem> menuItems;

    public UIManager(List<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }


    public void setupEventListeners() {
        for (MenuItem item : menuItems) {
            // Add ActionListener to the checkbox
            item.getCheckBox().addActionListener(e -> {
                boolean selected = item.getCheckBox().isSelected();
                item.getQuantityField().setEnabled(selected);
                if (selected) {
                    item.getQuantityField().setText("1");
                    item.getQuantityField().requestFocus();
                } else {
                    item.getQuantityField().setText("0");
                }
            });

            item.getQuantityField().addKeyListener(new KeyAdapter() {
                public void keyTyped(KeyEvent evt) {
                    char iNumber = evt.getKeyChar();
                    if (!Character.isDigit(iNumber) && iNumber != KeyEvent.VK_BACK_SPACE && iNumber != KeyEvent.VK_DELETE) {
                        evt.consume();
                    }
                }
            });
        }
    }


    public void resetUI(JTextField taxField, JTextField subTotalField, JTextField totalField, javax.swing.JTextArea receiptArea) {
        for (MenuItem item : menuItems) {
            item.getCheckBox().setSelected(false);
            item.getQuantityField().setText("0");
            item.getQuantityField().setEnabled(false);
            item.getCheckBox().setEnabled(false);
        }
        taxField.setText("0");
        subTotalField.setText("0");
        totalField.setText("0");
        receiptArea.setText("");
        receiptArea.setEnabled(false);
    }


    public void enableCategory(List<JCheckBox> categoryItems) {
        for(JCheckBox checkBox : categoryItems) {
            checkBox.setEnabled(true);
        }
    }


    public void disableAll() {
        for (MenuItem item : menuItems) {
            item.getQuantityField().setEnabled(false);
            item.getCheckBox().setEnabled(false);
        }
    }
}