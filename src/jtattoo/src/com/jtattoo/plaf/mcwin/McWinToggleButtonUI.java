/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.mcwin;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;

import com.jtattoo.plaf.*;

/**
 * @author Michael Hagen
 */
public class McWinToggleButtonUI extends BaseToggleButtonUI {

    public static ComponentUI createUI(JComponent c) {
        return new McWinToggleButtonUI();
    }

    protected void paintBackground(Graphics g, AbstractButton b) {
        if (!b.isContentAreaFilled() || (b.getParent() instanceof JMenuBar)) {
            return;
        }
        super.paintBackground(g, b);
        int width = b.getWidth();
        int height = b.getHeight();
        Graphics2D g2D = (Graphics2D) g;
        Composite composite = g2D.getComposite();
        g2D.setColor(Color.lightGray);
        g2D.drawRect(0, 0, width - 2, height - 1);
        AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f);
        g2D.setComposite(alpha);
        g2D.setColor(Color.white);
        g2D.drawLine(width - 1, 0, width - 1, height - 1);
        g2D.setComposite(composite);
    }
}


