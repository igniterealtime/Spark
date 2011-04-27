/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.mint;

import javax.swing.*;
import javax.swing.plaf.*;

import com.jtattoo.plaf.*;
import java.awt.Graphics;

/**
 * @author Michael Hagen
 */
public class MintTabbedPaneUI extends BaseTabbedPaneUI {

    public static ComponentUI createUI(JComponent c) {
        return new MintTabbedPaneUI();
    }

    public void installDefaults() {
        super.installDefaults();
        tabAreaInsets.bottom = 6;
    }

    protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {
        g.setColor(AbstractLookAndFeel.getTabAreaBackgroundColor());
        int tabAreaHeight = calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight);
        int tabAreaWidth = calculateTabAreaWidth(tabPlacement, runCount, maxTabWidth);
        if (tabPlacement == JTabbedPane.TOP || tabPlacement == JTabbedPane.LEFT) {
            g.fillRect(x, y, tabAreaWidth, tabAreaHeight);
        } else if (tabPlacement == JTabbedPane.BOTTOM) {
            g.fillRect(x, h - tabAreaHeight + 1, w, tabAreaHeight);
        } else {
            g.fillRect(w - tabAreaWidth + 1, y, tabAreaWidth, h);
        }
        super.paintContentBorder(g, tabPlacement, selectedIndex, x, y, w, h);
    }

}