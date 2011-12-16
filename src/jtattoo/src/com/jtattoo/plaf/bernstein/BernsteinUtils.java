/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.bernstein;

import java.awt.*;
import javax.swing.*;

import com.jtattoo.plaf.*;

/**
 * @author  Michael Hagen
 */
public class BernsteinUtils {

    private static final Icon BG_IMAGE = new LazyImageIcon("bernstein/icons/background.jpg");
    private static final int IMAGE_WIDTH = BG_IMAGE.getIconWidth();
    private static final int IMAGE_HEIGHT = BG_IMAGE.getIconHeight();

    private BernsteinUtils() {
    }

    public static void fillComponent(Graphics g, Component c) {
        if (AbstractLookAndFeel.getTheme().isBackgroundPatternOn()) {
            int w = c.getWidth();
            int h = c.getHeight();
            Point p = JTattooUtilities.getRelLocation(c);
            int y = -p.y;
            while (y < h) {
                int x = -p.x;
                while (x < w) {
                    BG_IMAGE.paintIcon(c, g, x, y);
                    x += IMAGE_WIDTH;
                }
                y += IMAGE_HEIGHT;
            }
        } else {
            g.setColor(c.getBackground());
            g.fillRect(0, 0, c.getWidth(), c.getHeight());
        }

    }
}
