/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.mcwin;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;

import com.jtattoo.plaf.*;

/**
 * @author Michael Hagen
 */
public class McWinToolBarUI extends AbstractToolBarUI {

    public static ComponentUI createUI(JComponent c) {
        return new McWinToolBarUI();
    }

    public Border getRolloverBorder() {
        return McWinBorders.getRolloverToolButtonBorder();
    }

    public Border getNonRolloverBorder() {
        return McWinBorders.getToolButtonBorder();
    }

    public boolean isButtonOpaque() {
        return true;
    }

    public void paint(Graphics g, JComponent c) {
        McWinUtils.fillComponent(g, c);
    }
}
