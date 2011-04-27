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
public class McWinPanelUI extends BasePanelUI {

    private static McWinPanelUI panelUI = null;

    public static ComponentUI createUI(JComponent c) {
        if (panelUI == null) {
            panelUI = new McWinPanelUI();
        }
        return panelUI;
    }

    public void update(Graphics g, JComponent c) {
        if (c.getBackground() instanceof ColorUIResource) {
            if (c.isOpaque()) {
                McWinUtils.fillComponent(g, c);
            }
        } else {
            super.update(g, c);
        }
    }
}
