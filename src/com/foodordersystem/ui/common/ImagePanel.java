package com.foodordersystem.ui.common;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

/**
 * A reusable public JPanel that draws a background image.
 */
public class ImagePanel extends JPanel {
    private Image backgroundImage;
    private float opacity;

    public ImagePanel(String imagePath, float opacity) {
        try {
            URL imageUrl = getClass().getResource(imagePath);
            if (imageUrl != null) {
                this.backgroundImage = new ImageIcon(imageUrl).getImage();
            } else {
                System.err.println("Error loading background image: " + imagePath + " (Resource not found)");
            }
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

            // Set rendering hints for better image quality
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Set opacity and draw the image
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            g2d.dispose();
        }
    }
}