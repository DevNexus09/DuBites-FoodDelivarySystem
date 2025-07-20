package com.foodordersystem.core;

import com.foodordersystem.ui.common.LandingPage;
import com.foodordersystem.ui.common.LoginFrame;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LandingPage().setVisible(true);
        });
    }
}