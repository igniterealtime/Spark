/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.spark.component.borders;

import javax.swing.border.AbstractBorder;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;

/**
 * Renders edge borders for a concave appearance.
 *
 * @author Derek DeMoro
 */
public class PartialLineBorder extends AbstractBorder {
    private Color color;
    private int thickness;

    boolean top,
            left,
            bottom,
            right;

    public PartialLineBorder(Color color, int thickness) {
        top = true;
        left = true;
        bottom = true;
        right = true;

        this.color = color;
        this.thickness = thickness;


    }

    public boolean isBorderOpaque() {
        return true;
    }

    public Insets getBorderInsets(Component component) {
        return new Insets(2, 2, 2, 2);
    }

    public int getThickness() {
        return thickness;
    }

    public void paintBorder(Component component, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D)g;
        g2.setStroke(new BasicStroke(1.0f));
        g2.setColor(color);


        if (top) {
            g2.drawLine(x, y, x + width, y);
        }
        if (left) {
            g2.drawLine(x, y, x, y + height);
        }
        if (bottom) {
            g2.drawLine(x, y + height, x + width, y + height);
        }
        if (right) {
            g2.drawLine(x + width, y, x + width, y + height);
        }
    }
}