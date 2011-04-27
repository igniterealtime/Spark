/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.BasicSeparatorUI;

/**
 * @author Michael Hagen
 */
public class BaseSeparatorUI extends BasicSeparatorUI {

    private static final Dimension size = new Dimension(2, 3);

    public static ComponentUI createUI(JComponent c) {
        return new BaseSeparatorUI();
    }

    public void paint(Graphics g, JComponent c) {
        boolean horizontal = true;
        if (c instanceof JSeparator) {
            horizontal = (((JSeparator) c).getOrientation() == JSeparator.HORIZONTAL);
        }
        if (horizontal) {
            int w = c.getWidth();
            g.setColor(AbstractLookAndFeel.getBackgroundColor());
            g.drawLine(0, 0, w, 0);
            g.setColor(ColorHelper.darker(AbstractLookAndFeel.getBackgroundColor(), 30));
            g.drawLine(0, 1, w, 1);
            g.setColor(ColorHelper.brighter(AbstractLookAndFeel.getBackgroundColor(), 50));
            g.drawLine(0, 2, w, 2);
        } else {
            int h = c.getHeight();
            g.setColor(ColorHelper.darker(AbstractLookAndFeel.getBackgroundColor(), 30));
            g.drawLine(0, 0, 0, h);
            g.setColor(ColorHelper.brighter(AbstractLookAndFeel.getBackgroundColor(), 50));
            g.drawLine(1, 0, 1, h);
        }
    }

    public Dimension getPreferredSize(JComponent c) {
        return size;
    }
}




