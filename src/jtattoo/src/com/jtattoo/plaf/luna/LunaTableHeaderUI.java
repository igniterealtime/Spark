/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.luna;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;

import com.jtattoo.plaf.*;

/**
 *
 * @author  Michael Hagen
 */
public class LunaTableHeaderUI extends BaseTableHeaderUI {

    public static ComponentUI createUI(JComponent c) {
        return new LunaTableHeaderUI();
    }

    protected void paintBackground(Graphics g, Rectangle cellRect, int col) {
        int x = cellRect.x;
        int y = cellRect.y;
        int w = cellRect.width;
        int h = cellRect.height;
        if (col == rolloverCol) {
            JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme().getRolloverColors(), x, y, w, h);
            g.setColor(AbstractLookAndFeel.getFocusColor());
            g.drawLine(x, y + 1, x + w - 1, y + 1);
            g.drawLine(x, y + 2, x + w - 1, y + 2);
        } else if (JTattooUtilities.isFrameActive(header)) {
            JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme().getColHeaderColors(), x, y, w, h);
        } else {
            JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme().getInActiveColors(), x, y, w, h);
        }
    }
}
