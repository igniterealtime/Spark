/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.bernstein;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;

import com.jtattoo.plaf.*;

/**
 * @author Michael Hagen
 */
public class BernsteinToolBarUI extends AbstractToolBarUI {

    public static ComponentUI createUI(JComponent c) {
        return new BernsteinToolBarUI();
    }

    public Border getRolloverBorder() {
        return BernsteinBorders.getRolloverToolButtonBorder();
    }

    public Border getNonRolloverBorder() {
        return BernsteinBorders.getToolButtonBorder();
    }

    public boolean isButtonOpaque() {
        return true;
    }

    public void paint(Graphics g, JComponent c) {
        BernsteinUtils.fillComponent(g, c);
    }
}
