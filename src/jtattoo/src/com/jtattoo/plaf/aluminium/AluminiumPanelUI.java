/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.aluminium;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;
import com.jtattoo.plaf.*;

/**
 * @author Michael Hagen
 */
public class AluminiumPanelUI extends BasePanelUI {

    private static AluminiumPanelUI panelUI = null;

    public static ComponentUI createUI(JComponent c) {
        if (panelUI == null) {
            panelUI = new AluminiumPanelUI();
        }
        return panelUI;
    }

    public void update(Graphics g, JComponent c) {
        if (c.getBackground() instanceof ColorUIResource) {
            if (c.isOpaque()) {
                AluminiumUtils.fillComponent(g, c);
            }
        } else {
            super.update(g, c);
        }
    }
}
