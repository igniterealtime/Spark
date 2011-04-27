/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.hifi;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;

import com.jtattoo.plaf.*;

/**
 * @author Michael Hagen
 */
public class HiFiToolBarUI extends AbstractToolBarUI {

    public static ComponentUI createUI(JComponent c) {
        return new HiFiToolBarUI();
    }

    public Border getRolloverBorder() {
        return HiFiBorders.getRolloverToolButtonBorder();
    }

    public Border getNonRolloverBorder() {
        return HiFiBorders.getToolButtonBorder();
    }

    public boolean isButtonOpaque() {
        return true;
    }

    public void paint(Graphics g, JComponent c) {
        HiFiUtils.fillComponent(g, c);
    }
}

