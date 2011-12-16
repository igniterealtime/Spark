/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.mcwin;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.border.*;
import com.jtattoo.plaf.*;

/**
 * @author Michael Hagen
 */
public class McWinComboBoxUI extends BaseComboBoxUI {

    public static ComponentUI createUI(JComponent c) {
        return new McWinComboBoxUI();
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
    static class ArrowButton extends NoFocusButton {

        public void paint(Graphics g) {
            Dimension size = getSize();
            if (isEnabled()) {
                if (getModel().isArmed() && getModel().isPressed()) {
                    JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme().getPressedColors(), 0, 0, size.width, size.height);
                } else if (getModel().isRollover()) {
                    JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme().getRolloverColors(), 0, 0, size.width, size.height);
                } else if (JTattooUtilities.isActive(this)) {
                    if (AbstractLookAndFeel.getTheme().isBrightMode()) {
                        JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme().getButtonColors(), 0, 0, size.width, size.height);
                    } else {
                        JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme().getDefaultColors(), 0, 0, size.width, size.height);
                    }
                } else {
                    JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme().getInActiveColors(), 0, 0, size.width, size.height);
                }
            } else {
                JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme().getDisabledColors(), 0, 0, size.width, size.height);
            }
            Icon icon = BaseIcons.getComboBoxIcon();
            int x = (size.width - icon.getIconWidth()) / 2;
            int y = (size.height - icon.getIconHeight()) / 2;
            if (getModel().isPressed() && getModel().isArmed()) {
                icon.paintIcon(this, g, x + 2, y + 1);
            } else {
                icon.paintIcon(this, g, x + 1, y);
            }
            paintBorder(g);
        }
    }
}