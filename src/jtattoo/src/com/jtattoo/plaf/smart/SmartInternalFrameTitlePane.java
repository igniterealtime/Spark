/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.smart;

import java.awt.*;
import javax.swing.*;

import com.jtattoo.plaf.*;

/**
 * @author Michael Hagen
 */
public class SmartInternalFrameTitlePane extends BaseInternalFrameTitlePane {

    public SmartInternalFrameTitlePane(JInternalFrame f) {
        super(f);
    }

    public void paintPalette(Graphics g) {
        int width = getWidth();
        int height = getHeight();

        Color backColor = null;
        Color frameColor = null;
        if (JTattooUtilities.isFrameActive(this)) {
            JTattooUtilities.fillHorGradient(g, SmartLookAndFeel.getTheme().getWindowTitleColors(), 0, 0, width, height);
            backColor = SmartLookAndFeel.getTheme().getWindowTitleColors()[10];
            frameColor = ColorHelper.darker(SmartLookAndFeel.getWindowTitleColorDark(), 15);
        } else {
            JTattooUtilities.fillHorGradient(g, SmartLookAndFeel.getTheme().getWindowInactiveTitleColors(), 0, 0, width, height);
            backColor = SmartLookAndFeel.getTheme().getWindowInactiveTitleColors()[10];
            frameColor = ColorHelper.darker(SmartLookAndFeel.getWindowInactiveTitleColorDark(), 15);
        }
        g.setColor(frameColor);
        g.drawLine(0, height - 1, width, height - 1);

        int dx = 8;
        int dy = 2;
        int dw = width - buttonsWidth - 2 * 8;
        Graphics2D g2D = (Graphics2D) g;
        Composite composite = g2D.getComposite();
        AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f);
        g2D.setComposite(alpha);

        float dc1 = 50.0f;
        float dc2 = 5.0f;
        Color c1 = ColorHelper.brighter(backColor, dc1);
        Color c2 = null;
        while ((dy + 2) < height) {
            c2 = ColorHelper.darker(backColor, dc2);
            dc2 += 5.0f;
            g.setColor(c1);
            g.drawLine(dx, dy, dx + dw, dy);
            dy++;
            g.setColor(c2);
            g.drawLine(dx, dy, dx + dw, dy);
            dy += 2;
        }
        g2D.setComposite(composite);
    }

    public void paintBackground(Graphics g) {
        Graphics2D g2D = (Graphics2D) g;
        int width = getWidth();
        int height = getHeight();
        Color backColor = null;
        Color frameColor = null;
        if (JTattooUtilities.isActive(this)) {
            backColor = SmartLookAndFeel.getTheme().getWindowTitleColors()[10];
            frameColor = SmartLookAndFeel.getTheme().getFrameColor();
            JTattooUtilities.fillHorGradient(g, SmartLookAndFeel.getTheme().getWindowTitleColors(), 0, 0, width, height);
        } else {
            backColor = SmartLookAndFeel.getTheme().getWindowInactiveTitleColors()[10];
            frameColor = ColorHelper.brighter(SmartLookAndFeel.getTheme().getFrameColor(), 40);
            JTattooUtilities.fillHorGradient(g, SmartLookAndFeel.getTheme().getWindowInactiveTitleColors(), 0, 0, width, height);
        }

        int iconWidth = 0;
        Icon icon = frame.getFrameIcon();
        if (icon != null) {
            iconWidth = icon.getIconWidth() + 5;
        }

        int titleWidth = 0;
        String frameTitle = frame.getTitle();
        if (frameTitle != null) {
            Font f = getFont();
            g.setFont(f);
            FontMetrics fm = g.getFontMetrics();
            titleWidth = fm.stringWidth(JTattooUtilities.getClippedText(frame.getTitle(), fm, getWidth() - iconWidth - buttonsWidth - 15)) + 10;
        }

        int dx;
        int dw;
        boolean leftToRight = JTattooUtilities.isLeftToRight(frame);
        int xOffset = leftToRight ? iconWidth + 10 + titleWidth : width - 10 - iconWidth - titleWidth;

        if (leftToRight) {
            dw = width - buttonsWidth - xOffset - 10;
            dx = xOffset;
        } else {
            dw = xOffset - buttonsWidth - 10;
            dx = buttonsWidth + 10;
        }
        int dy = 3;

        if (dw > 0) {
            Composite composite = g2D.getComposite();
            AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f);
            g2D.setComposite(alpha);

            float dc1 = 50.0f;
            float dc2 = 5.0f;

            Color c1 = ColorHelper.brighter(backColor, dc1);
            Color c2 = null;
            while ((dy + 5) < height) {
                c2 = ColorHelper.darker(backColor, dc2);
                dc2 += 5.0f;
                g.setColor(c1);
                g.drawLine(dx, dy, dx + dw, dy);
                dy++;
                g.setColor(c2);
                g.drawLine(dx, dy, dx + dw, dy);
                dy += 3;
            }
            g2D.setComposite(composite);
        }
        g.setColor(frameColor);
        g.drawLine(0, height - 1, width, height - 1);
    }

    public void paintBorder(Graphics g) {
    }

    public void paintText(Graphics g, int x, int y, String title) {
        if (isActive()) {
            Color titleColor = AbstractLookAndFeel.getWindowTitleForegroundColor();
            if (ColorHelper.getGrayValue(titleColor) > 164) {
                g.setColor(Color.black);
                JTattooUtilities.drawString(frame, g, title, x + 1, y + 1);
            }
            g.setColor(titleColor);
            JTattooUtilities.drawString(frame, g, title, x, y);
        } else {
            g.setColor(AbstractLookAndFeel.getWindowInactiveTitleForegroundColor());
            JTattooUtilities.drawString(frame, g, title, x, y);
        }
    }
}
