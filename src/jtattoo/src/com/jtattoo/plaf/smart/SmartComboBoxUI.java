/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.smart;

import com.jtattoo.plaf.mcwin.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.border.*;
import com.jtattoo.plaf.*;

/**
 * @author Michael Hagen
 */
public class SmartComboBoxUI extends BaseComboBoxUI {

    public static ComponentUI createUI(JComponent c) {
        return new SmartComboBoxUI();
    }

    public JButton createArrowButton() {
        ArrowButton button = new ArrowButton();
        if (JTattooUtilities.isLeftToRight(comboBox)) {
            Border border = BorderFactory.createMatteBorder(0, 1, 0, 0, McWinLookAndFeel.getFrameColor());
            button.setBorder(border);
        } else {
            Border border = BorderFactory.createMatteBorder(0, 0, 0, 1, McWinLookAndFeel.getFrameColor());
            button.setBorder(border);
        }
        return button;
    }

//--------------------------------------------------------------------------------------------------    
    static class ArrowButton extends BaseComboBoxUI.ArrowButton {

        public void paint(Graphics g) {
            super.paint(g);
            if (getModel().isRollover()) {
                g.setColor(SmartLookAndFeel.getFocusColor());
                g.drawLine(1, 0, getWidth() - 1, 0);
                g.drawLine(1, 1, getWidth() - 1, 1);
            }
        }
    }
}