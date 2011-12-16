/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.hifi;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import javax.swing.text.View;

import com.jtattoo.plaf.*;

/**
 * @author Michael Hagen
 */
public class HiFiRadioButtonUI extends BaseRadioButtonUI {

    private static HiFiRadioButtonUI radioButtonUI = null;

    public static ComponentUI createUI(JComponent c) {
        if (radioButtonUI == null) {
            radioButtonUI = new HiFiRadioButtonUI();
        }
        return radioButtonUI;
    }

    protected void paintText(Graphics g, JComponent c, String text, Rectangle textRect) {
        View v = (View) c.getClientProperty(BasicHTML.propertyKey);
        if (v != null) {
            v.paint(g, textRect);
        } else {
            AbstractButton b = (AbstractButton) c;
            ButtonModel model = b.getModel();
            int mnemIndex = -1;
            if (JTattooUtilities.getJavaVersion() >= 1.4) {
                mnemIndex = b.getDisplayedMnemonicIndex();
            } else {
                mnemIndex = JTattooUtilities.findDisplayedMnemonicIndex(b.getText(), model.getMnemonic());
            }
            Font f = c.getFont();
            g.setFont(f);
            FontMetrics fm = g.getFontMetrics();
            if (model.isEnabled()) {
                Color fc = b.getForeground();
                if (AbstractLookAndFeel.getTheme().isTextShadowOn() && ColorHelper.getGrayValue(fc) > 128) {
                    g.setColor(Color.black);
                    JTattooUtilities.drawStringUnderlineCharAt(c, g, text, mnemIndex, textRect.x + 1, textRect.y + 1 + fm.getAscent());
                }
                g.setColor(fc);
                JTattooUtilities.drawStringUnderlineCharAt(c, g, text, mnemIndex, textRect.x, textRect.y + fm.getAscent());
            } else {
                g.setColor(Color.black);
                JTattooUtilities.drawStringUnderlineCharAt(c, g, text, mnemIndex, textRect.x + 1, textRect.y + 1 + fm.getAscent());
                g.setColor(AbstractLookAndFeel.getDisabledForegroundColor());
                JTattooUtilities.drawStringUnderlineCharAt(c, g, text, mnemIndex, textRect.x, textRect.y + fm.getAscent());
            }
        }
    }

    public void paintBackground(Graphics g, JComponent c) {
        if (c.isOpaque()) {
            if ((c.getBackground().equals(AbstractLookAndFeel.getBackgroundColor())) && (c.getBackground() instanceof ColorUIResource)) {
                HiFiUtils.fillComponent(g, c);
            } else {
                g.setColor(c.getBackground());
                g.fillRect(0, 0, c.getWidth(), c.getHeight());
            }
        }
    }
}
