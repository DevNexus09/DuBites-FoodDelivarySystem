package com.foodordersystem.ui.common;

import javax.swing.*;
import java.awt.*;

public class RoleSelectionFrame extends BaseFrame {

    public RoleSelectionFrame() {
        super("Select Your Role", 800, 600);
        initComponents();
    }

    @Override
    protected void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JButton customerButton = new JButton("Customer");
        customerButton.setPreferredSize(new Dimension(200, 50));
        customerButton.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        JButton riderButton = new JButton("Rider");
        riderButton.setPreferredSize(new Dimension(200, 50));
        riderButton.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        JButton ownerButton = new JButton("Restaurant Owner");
        ownerButton.setPreferredSize(new Dimension(200, 50));
        ownerButton.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        JButton backButton = new JButton("Back");
        backButton.setPreferredSize(new Dimension(200, 50));
        backButton.addActionListener(e -> {
            new LandingPage().setVisible(true);
            dispose();
        });

        buttonPanel.add(customerButton, gbc);
        buttonPanel.add(riderButton, gbc);
        buttonPanel.add(ownerButton, gbc);
        buttonPanel.add(backButton, gbc);

        add(buttonPanel);
    }
}