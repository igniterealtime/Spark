/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.fast;

import java.awt.*;
import javax.swing.*;

import com.jtattoo.plaf.*;

/**
 * @author Michael Hagen
 */
public class FastInternalFrameTitlePane extends BaseInternalFrameTitlePane {

    public FastInternalFrameTitlePane(JInternalFrame f) {
        super(f);
    }

    public void paintPalette(Graphics g) {
        if (JTattooUtilities.isFrameActive(this)) {
            g.setColor(AbstractLookAndFeel.getWindowTitleBackgroundColor());
        } else {
            g.setColor(AbstractLookAndFeel.getWindowInactiveTitleBackgroundColor());
        }
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    public void paintBackground(Graphics g) {
        if (isActive()) {
            g.setColor(AbstractLookAndFeel.getWindowTitleBackgroundColor());
        } else {
            g.setColor(AbstractLookAndFeel.getWindowInactiveTitleBackgroundColor());
        }
        g.fillRect(0, 0, getWidth(), getHeight());
    }
}
