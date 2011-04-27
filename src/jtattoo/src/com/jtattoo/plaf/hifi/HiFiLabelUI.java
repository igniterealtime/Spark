/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.hifi;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

import com.jtattoo.plaf.*;

/**
 * @author Michael Hagen
 */
public class HiFiLabelUI extends BasicLabelUI {

    private static HiFiLabelUI labelUI = null;

    public static ComponentUI createUI(JComponent c) {
        if (labelUI == null) {
            labelUI = new HiFiLabelUI();
        }
        return labelUI;
    }

    protected void paintEnabledText(JLabel l, Graphics g, String s, int textX, int textY) {
        int mnemIndex = -1;
        if (JTattooUtilities.getJavaVersion() >= 1.4) {
            mnemIndex = l.getDisplayedMnemonicIndex();
        } else {
            mnemIndex = JTattooUtilities.findDisplayedMnemonicIndex(l.getText(), l.getDisplayedMnemonic());
        }
        Color fc = l.getForeground();
        if (AbstractLookAndFeel.getTheme().isTextShadowOn() && ColorHelper.getGrayValue(fc) > 128) {
            g.setColor(Color.black);
            JTattooUtilities.drawStringUnderlineCharAt(l, g, s, mnemIndex, textX + 1, textY + 1);
        }
        g.setColor(fc);
        JTattooUtilities.drawStringUnderlineCharAt(l, g, s, mnemIndex, textX, textY);
    }

    protected void paintDisabledText(JLabel l, Graphics g, String s, int textX, int textY) {
        int mnemIndex = -1;
        if (JTattooUtilities.getJavaVersion() >= 1.4) {
            mnemIndex = l.getDisplayedMnemonicIndex();
        } else {
            mnemIndex = JTattooUtilities.findDisplayedMnemonicIndex(l.getText(), l.getDisplayedMnemonic());
        }
        g.setColor(Color.black);
        JTattooUtilities.drawStringUnderlineCharAt(l, g, s, mnemIndex, textX + 1, textY + 1);
        g.setColor(AbstractLookAndFeel.getDisabledForegroundColor());
        JTattooUtilities.drawStringUnderlineCharAt(l, g, s, mnemIndex, textX, textY);
    }
}

