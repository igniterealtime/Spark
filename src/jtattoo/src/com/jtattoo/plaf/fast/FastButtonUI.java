/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.fast;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;

import com.jtattoo.plaf.*;

/**
 * @author Michael Hagen
 */
public class FastButtonUI extends BaseButtonUI {

    public static ComponentUI createUI(JComponent c) {
        return new FastButtonUI();
    }

    protected void paintBackground(Graphics g, AbstractButton b) {
        if (b.isContentAreaFilled() && !(b.getParent() instanceof JMenuBar)) {
            Color backColor = b.getBackground();
            ButtonModel model = b.getModel();
            if (model.isEnabled()) {
                if (model.isPressed() && model.isArmed()) {
                    backColor = ColorHelper.darker(backColor, 30);
                }
            } else {
                backColor = ColorHelper.brighter(FastLookAndFeel.getDisabledForegroundColor(), 80);
            }
            g.setColor(backColor);
            g.fillRect(0, 0, b.getWidth(), b.getHeight());
        }
    }
}

