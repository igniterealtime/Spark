/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.mint;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;

import com.jtattoo.plaf.*;

/**
 * @author Michael Hagen
 */
public class MintToolBarUI extends AbstractToolBarUI {

    public static ComponentUI createUI(JComponent c) {
        return new MintToolBarUI();
    }

    public Border getRolloverBorder() {
        return MintBorders.getRolloverToolButtonBorder();
    }

    public Border getNonRolloverBorder() {
        return MintBorders.getToolButtonBorder();
    }

    public boolean isButtonOpaque() {
        return false;
    }

    public void paint(Graphics g, JComponent c) {
        JTattooUtilities.fillVerGradient(g, MintLookAndFeel.getTheme().getToolBarColors(), 0, 0, c.getWidth(), c.getHeight());
    }
}

