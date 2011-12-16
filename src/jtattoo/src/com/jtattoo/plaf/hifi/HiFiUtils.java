/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.hifi;

import java.awt.*;

import com.jtattoo.plaf.*;

/**
 * @author  Michael Hagen
 */
public class HiFiUtils {

    private HiFiUtils() {
    }

    public static void fillComponent(Graphics g, Component c) {
        if (AbstractLookAndFeel.getTheme().isBackgroundPatternOn()) {
            int w = c.getWidth();
            int h = c.getHeight();
            Point p = JTattooUtilities.getRelLocation(c);
            int y = 2 - (p.y % 3);
            g.setColor(AbstractLookAndFeel.getTheme().getBackgroundColorLight());
            g.fillRect(0, 0, w, h);
            g.setColor(AbstractLookAndFeel.getTheme().getBackgroundColorDark());
            while (y < h) {
                g.drawLine(0, y, w, y);
                y += 3;
            }
        } else {
            g.setColor(c.getBackground());
            g.fillRect(0, 0, c.getWidth(), c.getHeight());
        }
    }
}
