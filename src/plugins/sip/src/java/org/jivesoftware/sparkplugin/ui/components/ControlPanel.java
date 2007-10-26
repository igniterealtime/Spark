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
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * Base Panel for Controls.
 *
 * @author Derek DeMoro
 */
public class ControlPanel extends JPanel {

    public ControlPanel() {
        setBorder(BorderFactory.createLineBorder(new Color(197, 213, 230)));
    }

    public ControlPanel(LayoutManager layout) {
        super(layout);
        setBorder(BorderFactory.createLineBorder(new Color(197, 213, 230)));
    }

    public void paintComponent(Graphics g) {
        final BufferedImage bufferedImage = new BufferedImage(2, getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bufferedImage.createGraphics();

        GradientPaint paint = new GradientPaint(0, 0, Color.white, 0, getHeight(), new Color(235, 241, 246), true);

        g2d.setPaint(paint);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.dispose();

        g.drawImage(bufferedImage, 0, 0, getWidth(), getHeight(), null);
    }
}
