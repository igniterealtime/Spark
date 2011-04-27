/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.hifi;

import java.awt.*;
import javax.swing.*;

import com.jtattoo.plaf.*;

/**
 * @author Michael Hagen
 */
public class HiFiInternalFrameTitlePane extends BaseInternalFrameTitlePane {

    public HiFiInternalFrameTitlePane(JInternalFrame f) {
        super(f);
    }

    public void paintText(Graphics g, int x, int y, String title) {
        g.setColor(Color.black);
        JTattooUtilities.drawString(frame, g, title, x + 1, y + 1);
        if (isActive()) {
            g.setColor(AbstractLookAndFeel.getWindowTitleForegroundColor());
        } else {
            g.setColor(AbstractLookAndFeel.getWindowInactiveTitleForegroundColor());
        }
        JTattooUtilities.drawString(frame, g, title, x, y);
    }

    public void paintBorder(Graphics g) {
    }
}
