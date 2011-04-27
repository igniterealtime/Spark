/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

/**
 * @author Michael Hagen
 */
public class BaseLabelUI extends BasicLabelUI {

    private static BaseLabelUI baseLabelUI = null;

    public static ComponentUI createUI(JComponent c) {
        if (baseLabelUI == null) {
            baseLabelUI = new BaseLabelUI();
        }
        return baseLabelUI;
    }

    protected void paintEnabledText(JLabel l, Graphics g, String s, int textX, int textY) {
        int mnemIndex = -1;
        if (JTattooUtilities.getJavaVersion() >= 1.4) {
            mnemIndex = l.getDisplayedMnemonicIndex();
        } else {
            mnemIndex = JTattooUtilities.findDisplayedMnemonicIndex(l.getText(), l.getDisplayedMnemonic());
        }
        g.setColor(l.getForeground());
        JTattooUtilities.drawStringUnderlineCharAt(l, g, s, mnemIndex, textX, textY);
    }

    protected void paintDisabledText(JLabel l, Graphics g, String s, int textX, int textY) {
        int mnemIndex = -1;
        if (JTattooUtilities.getJavaVersion() >= 1.4) {
            mnemIndex = l.getDisplayedMnemonicIndex();
        } else {
            mnemIndex = JTattooUtilities.findDisplayedMnemonicIndex(l.getText(), l.getDisplayedMnemonic());
        }
        g.setColor(Color.white);
        JTattooUtilities.drawStringUnderlineCharAt(l, g, s, mnemIndex, textX + 1, textY + 1);
        g.setColor(AbstractLookAndFeel.getDisabledForegroundColor());
        JTattooUtilities.drawStringUnderlineCharAt(l, g, s, mnemIndex, textX, textY);
    }
}

