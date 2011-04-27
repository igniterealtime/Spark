/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.smart;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;

import com.jtattoo.plaf.*;

/**
 *
 * @author  Michael Hagen
 */
public class SmartScrollBarUI extends BaseScrollBarUI {

    public static ComponentUI createUI(JComponent c) {
        return new SmartScrollBarUI();
    }

    protected JButton createDecreaseButton(int orientation) {
        return new SmartScrollButton(orientation, scrollBarWidth);
    }

    protected JButton createIncreaseButton(int orientation) {
        return new SmartScrollButton(orientation, scrollBarWidth);
    }

    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        super.paintThumb(g, c, thumbBounds);
        if (isRollover) {
            g.setColor(SmartLookAndFeel.getFocusColor());
            if (scrollbar.getOrientation() == JScrollBar.VERTICAL) {
                g.drawLine(thumbBounds.x + 1, thumbBounds.y + 1, thumbBounds.x + thumbBounds.width - 2, thumbBounds.y + 1);
                g.drawLine(thumbBounds.x + 1, thumbBounds.y + 2, thumbBounds.x + thumbBounds.width - 2, thumbBounds.y + 2);
            } else {
                g.drawLine(thumbBounds.x + 1, thumbBounds.y + 1, thumbBounds.x + 1, thumbBounds.y + thumbBounds.height - 2);
                g.drawLine(thumbBounds.x + 2, thumbBounds.y + 1, thumbBounds.x + 2, thumbBounds.y + thumbBounds.height - 2);
            }
        }
    }
}
