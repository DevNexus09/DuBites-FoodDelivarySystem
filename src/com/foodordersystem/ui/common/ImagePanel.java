package com.foodordersystem.ui.common;

import javax.swing.*;
import java.awt.*;

/**
 * A reusable public JPanel that draws a background image.
 */
public class ImagePanel extends JPanel {
    private Image backgroundImage;
    private float opacity;

    public ImagePanel(String imagePath, float opacity) {
        try {
            this.backgroundImage = new ImageIcon(getClass().getResource(imagePath)).getImage();
            this.opacity = opacity;
        } catch (Exception e) {
            System.err.println("Error loading background image: " + imagePath);
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            g2d.dispose();
        }
    }
}