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
public class FastToggleButtonUI extends BaseToggleButtonUI {

    public static ComponentUI createUI(JComponent b) {
        return new FastToggleButtonUI();
    }

    protected void paintBackground(Graphics g, AbstractButton b) {
        if (b.isContentAreaFilled() && !(b.getParent() instanceof JMenuBar)) {
            Color backColor = b.getBackground();
            ButtonModel model = b.getModel();
            if (model.isEnabled()) {
                if ((model.isPressed() && model.isArmed()) || model.isSelected()) {
                    backColor = ColorHelper.darker(backColor, 10);
                }
            } else {
                backColor = ColorHelper.brighter(FastLookAndFeel.getDisabledForegroundColor(), 80);
            }
            g.setColor(backColor);
            g.fillRect(1, 1, b.getWidth() - 2, b.getHeight() - 2);
        }
    }
}
