package com.foodordersystem.ui.common;

import javax.swing.*;
import java.awt.*;

public abstract class BaseFrame extends JFrame {

    public BaseFrame(String title, int width, int height) {
        setTitle(title);
        setSize(width, height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
    }

    protected abstract void initComponents();
}