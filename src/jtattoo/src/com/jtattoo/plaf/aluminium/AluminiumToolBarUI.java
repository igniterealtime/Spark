/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.aluminium;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;

import com.jtattoo.plaf.*;

/**
 * @author Michael Hagen
 */
public class AluminiumToolBarUI extends AbstractToolBarUI {

    public static ComponentUI createUI(JComponent c) {
        return new AluminiumToolBarUI();
    }

    public Border getRolloverBorder() {
        return AluminiumBorders.getRolloverToolButtonBorder();
    }

    public Border getNonRolloverBorder() {
        return AluminiumBorders.getToolButtonBorder();
    }

    public boolean isButtonOpaque() {
        return false;
    }

    public void paint(Graphics g, JComponent c) {
        AluminiumUtils.fillComponent(g, c);
    }
}

