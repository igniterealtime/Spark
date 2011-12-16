/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.mcwin;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

import com.jtattoo.plaf.*;

/**
 * @author Michael Hagen
 */
public class McWinMenuBarUI extends BaseMenuBarUI {

    private static final Color shadowColors[] = ColorHelper.createColorArr(Color.white, new Color(240, 240, 240), 8);

    public static ComponentUI createUI(JComponent x) {
        return new McWinMenuBarUI();
    }

    public void installUI(JComponent c) {
        super.installUI(c);
        if ((c != null) && (c instanceof JMenuBar)) {
            ((JMenuBar) c).setBorder(McWinBorders.getMenuBarBorder());
            ((JMenuBar) c).setBorderPainted(true);
        }
    }

    public void paint(Graphics g, JComponent c) {
        if (AbstractLookAndFeel.getTheme().isBackgroundPatternOn()) {
            McWinUtils.fillComponent(g, c, shadowColors);
        } else {
            super.paint(g, c);
        }
    }
}