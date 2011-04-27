/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.smart;

import java.awt.*;
import javax.swing.*;

import com.jtattoo.plaf.*;

/**
 * @author  Michael Hagen
 */
public class SmartTitlePane extends BaseTitlePane {

    public SmartTitlePane(JRootPane root, BaseRootPaneUI ui) {
        super(root, ui);
    }

    public void createButtons() {
        iconifyButton = new BaseTitleButton(iconifyAction, ICONIFY, iconifyIcon, 1.0f);
        maxButton = new BaseTitleButton(restoreAction, MAXIMIZE, maximizeIcon, 1.0f);
        closeButton = new BaseTitleButton(closeAction, CLOSE, closeIcon, 1.0f);
    }

    public void paintBorder(Graphics g) {
        if (isActive()) {
            g.setColor(AbstractLookAndFeel.getTheme().getFrameColor());
        } else {
            g.setColor(ColorHelper.brighter(AbstractLookAndFeel.getTheme().getFrameColor(), 40));
        }
        g.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
    }

    public void paintComponent(Graphics g) {
        Graphics2D g2D = (Graphics2D) g;

        if (getFrame() != null) {
            setState(DecorationHelper.getExtendedState(getFrame()));
        }

        paintBackground(g);

        boolean leftToRight = (window == null) ? getRootPane().getComponentOrientation().isLeftToRight() : window.getComponentOrientation().isLeftToRight();
        boolean isSelected = (window == null) ? true : JTattooUtilities.isWindowActive(window);

        Color foreground = AbstractLookAndFeel.getWindowInactiveTitleForegroundColor();
        Color backColor = SmartLookAndFeel.getTheme().getWindowInactiveTitleColors()[10];
        if (isSelected) {
            foreground = AbstractLookAndFeel.getWindowTitleForegroundColor();
            backColor = SmartLookAndFeel.getTheme().getWindowTitleColors()[10];
        }

        int width = getWidth();
        int height = getHeight();
        int titleWidth = width - buttonsWidth - 4;
        int xOffset = leftToRight ? 2 : width - 2;
        if (getWindowDecorationStyle() == BaseRootPaneUI.FRAME) {
            int mw = menuBar.getWidth() + 2;
            xOffset += leftToRight ? mw : -mw;
            titleWidth -= height;
        }

        String theTitle = getTitle();
        if (theTitle != null) {
            FontMetrics fm = g.getFontMetrics();
            g.setColor(foreground);
            int yOffset = ((height - fm.getHeight()) / 2) + fm.getAscent() - 1;
            Rectangle rect = iconifyButton.getBounds();

            int titleW;
            if (leftToRight) {
                if (rect.x == 0) {
                    rect.x = window.getWidth() - window.getInsets().right - 2;
                }
                titleW = rect.x - xOffset - 4;
                theTitle = JTattooUtilities.getClippedText(theTitle, fm, titleW);
            } else {
                titleW = xOffset - rect.x - rect.width - 4;
                theTitle = JTattooUtilities.getClippedText(theTitle, fm, titleW);
                xOffset -= SwingUtilities.computeStringWidth(fm, theTitle);
            }
            int titleLength = SwingUtilities.computeStringWidth(fm, theTitle);

            if (ColorHelper.getGrayValue(foreground) > 164) {
                g.setColor(Color.black);
                JTattooUtilities.drawString(rootPane, g, theTitle, xOffset + 1, yOffset + 1);
            }
            g.setColor(foreground);
            JTattooUtilities.drawString(rootPane, g, theTitle, xOffset, yOffset);

            xOffset += leftToRight ? titleLength + 5 : -5;
        }
        int dx;
        int dw;
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
    }
}
