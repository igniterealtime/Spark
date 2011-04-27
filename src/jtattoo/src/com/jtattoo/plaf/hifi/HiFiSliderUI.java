/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.hifi;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;

import com.jtattoo.plaf.*;

/**
 * @author Michael Hagen
 */
public class HiFiSliderUI extends BaseSliderUI {

    public HiFiSliderUI(JSlider slider) {
        super(slider);
    }

    public static ComponentUI createUI(JComponent c) {
        return new HiFiSliderUI((JSlider) c);
    }

    public void paintBackground(Graphics g, JComponent c) {
        if (c.isOpaque()) {
            Component parent = c.getParent();
            if ((parent != null) && (parent.getBackground() instanceof ColorUIResource)) {
                HiFiUtils.fillComponent(g, c);
            } else {
                if (parent != null) {
                    g.setColor(parent.getBackground());
                } else {
                    g.setColor(c.getBackground());
                }
                g.fillRect(0, 0, c.getWidth(), c.getHeight());
            }
        }
    }
}
