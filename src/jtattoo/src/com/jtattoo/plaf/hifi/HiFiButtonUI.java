/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.hifi;

import java.awt.*;

import javax.swing.*;
import javax.swing.plaf.*;

import com.jtattoo.plaf.*;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

/**
 * @author Michael Hagen
 */
public class HiFiButtonUI extends BaseButtonUI {

    public static ComponentUI createUI(JComponent c) {
        return new HiFiButtonUI();
    }

    protected void paintBackground(Graphics g, AbstractButton b) {
        if (!b.isContentAreaFilled() || (b.getParent() instanceof JMenuBar)) {
            return;
        }
        int width = b.getWidth();
        int height = b.getHeight();
        Graphics2D g2D = (Graphics2D) g;
        Shape savedClip = g.getClip();
        if ((b.getBorder() != null) && b.isBorderPainted() && (b.getBorder() instanceof UIResource)) {
            Area clipArea = new Area(savedClip);
            Area rectArea = new Area(new Rectangle2D.Double(1, 1, width - 2, height - 2));
            rectArea.intersect(clipArea);
            g2D.setClip(rectArea);
        }
        super.paintBackground(g, b);
        g2D.setClip(savedClip);
    }

    protected void paintText(Graphics g, AbstractButton b, Rectangle textRect) {
        ButtonModel model = b.getModel();
        FontMetrics fm = g.getFontMetrics();
        int mnemIndex = -1;
        if (JTattooUtilities.getJavaVersion() >= 1.4) {
            mnemIndex = b.getDisplayedMnemonicIndex();
        } else {
            mnemIndex = JTattooUtilities.findDisplayedMnemonicIndex(b.getText(), model.getMnemonic());
        }
        int offs = 0;
        if (model.isArmed() && model.isPressed()) {
            offs = 1;
        }

        Graphics2D g2D = (Graphics2D) g;
        Composite composite = g2D.getComposite();
        AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f);
        g2D.setComposite(alpha);
        Color fc = b.getForeground();
        if (model.isPressed() && model.isArmed()) {
            fc = AbstractLookAndFeel.getTheme().getSelectionForegroundColor();
        }
        if (!model.isEnabled()) {
            fc = AbstractLookAndFeel.getTheme().getDisabledForegroundColor();
        }
        if (ColorHelper.getGrayValue(fc) > 64) {
            g2D.setColor(Color.black);
        } else {
            g2D.setColor(Color.white);
        }
        JTattooUtilities.drawStringUnderlineCharAt(b, g, b.getText(), mnemIndex, textRect.x + offs + 1, textRect.y + offs + fm.getAscent() + 1);
        g2D.setComposite(composite);
        g2D.setColor(fc);
        JTattooUtilities.drawStringUnderlineCharAt(b, g, b.getText(), mnemIndex, textRect.x + offs, textRect.y + offs + fm.getAscent());
    }
}
