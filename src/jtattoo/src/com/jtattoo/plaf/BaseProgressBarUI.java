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
public class BaseProgressBarUI extends BasicProgressBarUI {

    public static ComponentUI createUI(JComponent c) {
        return new BaseProgressBarUI();
    }

    public void installUI(JComponent c) {
        super.installUI(c);
        c.setBorder(UIManager.getBorder("ProgressBar.border"));
    }

    protected void paintIndeterminate(Graphics g, JComponent c) {
        if (!(g instanceof Graphics2D)) {
            return;
        }
        Graphics2D g2D = (Graphics2D) g;

        Insets b = progressBar.getInsets(); // area for border
        int barRectWidth = progressBar.getWidth() - (b.right + b.left);
        int barRectHeight = progressBar.getHeight() - (b.top + b.bottom);

        Color colors[] = null;
        if (!JTattooUtilities.isActive(c)) {
            colors = AbstractLookAndFeel.getTheme().getInActiveColors();
        } else if (c.isEnabled()) {
            colors = AbstractLookAndFeel.getTheme().getProgressBarColors();
        } else {
            colors = AbstractLookAndFeel.getTheme().getDisabledColors();
        }

        Color cHi = ColorHelper.darker(colors[colors.length - 1], 5);
        Color cLo = ColorHelper.darker(colors[colors.length - 1], 10);

        // Paint the bouncing box.
        Rectangle boxRect = getBox(null);
        if (boxRect != null) {
            g2D.setColor(progressBar.getForeground());
            JTattooUtilities.draw3DBorder(g, cHi, cLo, boxRect.x + 1, boxRect.y + 1, boxRect.width - 2, boxRect.height - 2);
            JTattooUtilities.fillHorGradient(g, colors, boxRect.x + 2, boxRect.y + 2, boxRect.width - 4, boxRect.height - 4);
        }

        // Deal with possible text painting
        if (progressBar.isStringPainted()) {
            Object savedRenderingHint = null;
            if (AbstractLookAndFeel.getTheme().isTextAntiAliasingOn()) {
                savedRenderingHint = g2D.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
                g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, AbstractLookAndFeel.getTheme().getTextAntiAliasingHint());
            }
            if (progressBar.getOrientation() == JProgressBar.HORIZONTAL) {
                paintString(g2D, b.left, b.top, barRectWidth, barRectHeight, boxRect.width, b);
            } else {
                paintString(g2D, b.left, b.top, barRectWidth, barRectHeight, boxRect.height, b);
            }
            if (AbstractLookAndFeel.getTheme().isTextAntiAliasingOn()) {
                g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, savedRenderingHint);
            }
        }
    }

    protected void paintDeterminate(Graphics g, JComponent c) {
        if (!(g instanceof Graphics2D)) {
            return;
        }

        Graphics2D g2D = (Graphics2D) g;
        Insets b = progressBar.getInsets(); // area for border
        int w = progressBar.getWidth() - (b.right + b.left);
        int h = progressBar.getHeight() - (b.top + b.bottom);

        // amount of progress to draw
        int amountFull = getAmountFull(b, w, h);
        Color colors[] = null;
        if (!JTattooUtilities.isActive(c)) {
            colors = AbstractLookAndFeel.getTheme().getInActiveColors();
        } else if (c.isEnabled()) {
            colors = AbstractLookAndFeel.getTheme().getProgressBarColors();
        } else {
            colors = AbstractLookAndFeel.getTheme().getDisabledColors();
        }
        Color cHi = ColorHelper.darker(colors[colors.length - 1], 5);
        Color cLo = ColorHelper.darker(colors[colors.length - 1], 10);
        if (progressBar.getOrientation() == JProgressBar.HORIZONTAL) {
            if (JTattooUtilities.isLeftToRight(progressBar)) {
                JTattooUtilities.draw3DBorder(g, cHi, cLo, 2, 2, amountFull - 2, h - 2);
                JTattooUtilities.fillHorGradient(g, colors, 3, 3, amountFull - 4, h - 4);
            } else {
                JTattooUtilities.draw3DBorder(g, cHi, cLo, w - amountFull + 2, 2, w - 2, h - 2);
                JTattooUtilities.fillHorGradient(g, colors, w - amountFull + 3, 3, w - 4, h - 4);
            }
        } else { // VERTICAL
            JTattooUtilities.draw3DBorder(g, cHi, cLo, 2, 2, w - 2, amountFull - 2);
            JTattooUtilities.fillVerGradient(g, colors, 3, 3, w - 4, amountFull - 4);
        }

        // Deal with possible text painting
        if (progressBar.isStringPainted()) {
            Object savedRenderingHint = null;
            if (AbstractLookAndFeel.getTheme().isTextAntiAliasingOn()) {
                savedRenderingHint = g2D.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
                g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, AbstractLookAndFeel.getTheme().getTextAntiAliasingHint());
            }
            paintString(g, b.left, b.top, w, h, amountFull, b);
            if (AbstractLookAndFeel.getTheme().isTextAntiAliasingOn()) {
                g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, savedRenderingHint);
            }
        }
    }

    public void paint(Graphics g, JComponent c) {
        if (JTattooUtilities.getJavaVersion() >= 1.4) {
            if (progressBar.isIndeterminate()) {
                paintIndeterminate(g, c);
            } else {
                paintDeterminate(g, c);
            }
        } else {
            paintDeterminate(g, c);
        }
    }
}
