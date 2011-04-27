/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.fast;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;

import com.jtattoo.plaf.*;

/**
 * @author Michael Hagen
 */
public class FastToolBarUI extends AbstractToolBarUI {

    public static ComponentUI createUI(JComponent c) {
        return new FastToolBarUI();
    }

    public Border getRolloverBorder() {
        return FastBorders.getRolloverToolButtonBorder();
    }

    public Border getNonRolloverBorder() {
        return FastBorders.getToolButtonBorder();
    }

    public boolean isButtonOpaque() {
        return false;
    }
}

