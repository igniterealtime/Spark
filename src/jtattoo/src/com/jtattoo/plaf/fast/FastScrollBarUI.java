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
public class FastScrollBarUI extends BaseScrollBarUI {

    public static ComponentUI createUI(JComponent c) {
        return new FastScrollBarUI();
    }

    protected JButton createDecreaseButton(int orientation) {
        return new FastScrollButton(orientation, scrollBarWidth);
    }

    protected JButton createIncreaseButton(int orientation) {
        return new FastScrollButton(orientation, scrollBarWidth);
    }

    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        g.setColor(FastLookAndFeel.getControlColorLight());
        g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
    }

    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        if (!c.isEnabled()) {
            return;
        }

        g.translate(thumbBounds.x, thumbBounds.y);

        Color backColor = FastLookAndFeel.getTheme().getControlBackgroundColor();
        if (!JTattooUtilities.isActive(c)) {
            backColor = ColorHelper.brighter(backColor, 50);
        }
        Color frameColorHi = ColorHelper.brighter(backColor, 40);
        Color frameColorLo = ColorHelper.darker(backColor, 30);
        g.setColor(backColor);
        g.fillRect(1, 1, thumbBounds.width - 1, thumbBounds.height - 1);
        g.setColor(frameColorLo);
        g.drawRect(0, 0, thumbBounds.width - 1, thumbBounds.height - 1);
        g.setColor(frameColorHi);
        g.drawLine(1, 1, thumbBounds.width - 2, 1);
        g.drawLine(1, 1, 1, thumbBounds.height - 2);
        g.translate(-thumbBounds.x, -thumbBounds.y);
    }
}