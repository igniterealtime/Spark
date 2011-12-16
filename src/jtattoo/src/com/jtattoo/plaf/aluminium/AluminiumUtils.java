/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.aluminium;

import java.awt.*;
import com.jtattoo.plaf.*;

/**
 * @author  Michael Hagen
 */
public class AluminiumUtils {

    private AluminiumUtils() {
    }

    public static void fillComponent(Graphics g, Component c) {
        if (!JTattooUtilities.isMac() && AbstractLookAndFeel.getTheme().isBackgroundPatternOn()) {
            Point offset = JTattooUtilities.getRelLocation(c);
            Dimension size = JTattooUtilities.getFrameSize(c);
            Graphics2D g2D = (Graphics2D) g;
            g2D.setPaint(new AluminiumGradientPaint(offset, size));
            g2D.fillRect(0, 0, c.getWidth(), c.getHeight());
            g2D.setPaint(null);
        } else {
            g.setColor(c.getBackground());
            g.fillRect(0, 0, c.getWidth(), c.getHeight());
        }
    }

    public static void fillComponent(Graphics g, Component c, int x, int y, int w, int h) {
        Shape savedClip = g.getClip();
        g.setClip(x, y, w, h);
        fillComponent(g, c);
        g.setClip(savedClip);
    }
}
