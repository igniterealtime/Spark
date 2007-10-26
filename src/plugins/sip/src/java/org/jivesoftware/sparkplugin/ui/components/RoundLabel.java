/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2007 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkplugin.ui.components;


import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * RoundLabel
 * <p/>
 *
 * @author Derek DeMoro
 */
public class RoundLabel extends JPanel {
    public static float[] BLUR = {0.10f, 0.10f, 0.10f, 0.10f, 0.30f, 0.10f, 0.10f, 0.10f, 0.10f};

    private int inset = 0;
    private Color buttonColor;
    private String text = "Current Call";
    private Color foregroundColor = Color.white;

    /**
     * Creates a round label
     *
     * @param text
     * @param foreground
     * @param buttonColor
     */
    public RoundLabel(String text, Color foreground, Color buttonColor) {
        this.buttonColor = buttonColor;
        this.text = text;
        this.foregroundColor = foreground;
        setBorder(BorderFactory.createLineBorder(Color.white));
        setOpaque(false);
    }

    public RoundLabel() {
        // empty Constructor
        setBorder(BorderFactory.createLineBorder(Color.white));
        setOpaque(false);
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int vWidth = getWidth();
        int vHeight = getHeight();

        // Calculate the size of the button
        int vButtonHeight = vHeight - (inset * 2);
        int vButtonWidth = vWidth - (inset * 2);
        int vArcSize = vButtonHeight;

        BufferedImage vBuffer = new BufferedImage(vWidth, vHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D bg = vBuffer.createGraphics();
        bg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Paint the background of the button
        bg.setColor(Color.white);
        bg.fillRect(0, 0, vWidth, vHeight);

        // Create the gradient paint for the first layer of the button
        Color vGradientStartColor = buttonColor;
        Color vGradientEndColor = buttonColor;
        Paint vPaint = new GradientPaint(0, inset, vGradientStartColor, 0, vButtonHeight, vGradientEndColor, false);
        bg.setPaint(vPaint);

        // Paint the first layer of the button
        bg.fillRect(inset, inset, vButtonWidth, vButtonHeight);//, vArcSize, vArcSize);

        // Calulate the size of the second layer of the button
        int vHighlightInset = 2;
        int vButtonHighlightHeight = vButtonHeight - (vHighlightInset * 2);
        int vButtonHighlightWidth = vButtonWidth - (vHighlightInset * 2);
        int vHighlightArcSize = vButtonHighlightHeight;

        bg.setClip(new RoundRectangle2D.Float(inset + vHighlightInset, inset + vHighlightInset, vButtonHighlightWidth, vButtonHighlightHeight / 2, vButtonHighlightHeight / 3, vButtonHighlightHeight / 3));
       // bg.fillRoundRect(inset + vHighlightInset, inset + vHighlightInset, vButtonHighlightWidth, vButtonHighlightHeight, vHighlightArcSize, vHighlightArcSize);

        // Blur the button
        ConvolveOp vBlurOp = new ConvolveOp(new Kernel(3, 3, BLUR));
        BufferedImage vBlurredBase = vBlurOp.filter(vBuffer, null);

        // Draw our aqua button
        g2.drawImage(vBlurredBase, 0, 0, null);

        // Draw the text (if any)
        if (text != null) {
            g2.setColor(foregroundColor);

            Font vFont = g2.getFont().deriveFont((float)(((float)vButtonHeight) * .6));
            g2.setFont(vFont);

            FontMetrics vMetrics = g2.getFontMetrics();
            Rectangle2D vStringBounds = vMetrics.getStringBounds(text, g2);

            float x = (float)((vWidth / 2) - (vStringBounds.getWidth() / 2));
            float y = (float)((vHeight / 2) + (vStringBounds.getHeight() / 2)) - vMetrics.getDescent();

            g2.drawString(text, x, y);
        }
    }

    // --------------------------------------------------------------------------
    // Utility Methods
    // --------------------------------------------------------------------------

    /**
     * A main method to test the panel.
     *
     * @param args
     */
    public static void main(String[] args) {
        JFrame vFrame = new JFrame(RoundLabel.class.getName());
        vFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        vFrame.setSize(300, 100);

        vFrame.setLayout(new GridBagLayout());

        RoundLabel label = new RoundLabel("Hello", Color.white, new Color(198, 211, 247));

        vFrame.add(label, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 10));

        vFrame.setTitle("Aqua Button");
        vFrame.show();
    }
}

