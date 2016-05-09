/*
* Copyright (c) 2002 and later by MH Software-Entwicklung. All Rights Reserved.
*  
* JTattoo is multiple licensed. If your are an open source developer you can use
* it under the terms and conditions of the GNU General Public License version 2.0
* or later as published by the Free Software Foundation.
*  
* see: gpl-2.0.txt
* 
* If you pay for a license you will become a registered user who could use the
* software under the terms and conditions of the GNU Lesser General Public License
* version 2.0 or later with classpath exception as published by the Free Software
* Foundation.
* 
* see: lgpl-2.0.txt
* see: classpath-exception.txt
* 
* Registered users could also use JTattoo under the terms and conditions of the 
* Apache License, Version 2.0 as published by the Apache Software Foundation.
*  
* see: APACHE-LICENSE-2.0.txt
*/
 
package com.jtattoo.plaf.smart;

import com.jtattoo.plaf.*;
import java.awt.*;
import javax.swing.JDialog;
import javax.swing.JRootPane;

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

    public void paintBackground(Graphics g) {
        Graphics2D g2D = (Graphics2D) g;
        int width = getWidth();
        int height = getHeight();
        Color backColor = null;
        Color frameColor = null;
        if (JTattooUtilities.isActive(this)) {
            backColor = AbstractLookAndFeel.getTheme().getWindowTitleColors()[10];
            frameColor = AbstractLookAndFeel.getTheme().getFrameColor();
            JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme().getWindowTitleColors(), 0, 0, width, height);
        } else {
            backColor = AbstractLookAndFeel.getTheme().getWindowInactiveTitleColors()[10];
            frameColor = ColorHelper.brighter(AbstractLookAndFeel.getTheme().getFrameColor(), 40);
            JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme().getWindowInactiveTitleColors(), 0, 0, width, height);
        }

        int iconWidth = 0;
        if (menuBar != null) {
            iconWidth = menuBar.getWidth() + 5;
        }

        int titleWidth = 0;
        String frameTitle = getTitle();
        if (frameTitle != null) {
            Font f = getFont();
            g.setFont(f);
            FontMetrics fm = g.getFontMetrics();
            titleWidth = fm.stringWidth(JTattooUtilities.getClippedText(getTitle(), fm, getWidth() - iconWidth - buttonsWidth - 15)) + 10;
            if (getWindow() instanceof JDialog) {
                Image image = getFrameIconImage();
                if (image != null) {
                    titleWidth += getHeight();
                }
            }
        }

        int dx;
        int dw;
        boolean leftToRight = isLeftToRight();
        int xOffset = leftToRight ? iconWidth + 10 + titleWidth : width - 10 - iconWidth - titleWidth;

        if (leftToRight) {
            dw = width - buttonsWidth - xOffset - 10;
            dx = xOffset;
        } else {
            dw = xOffset - buttonsWidth - 10;
            dx = buttonsWidth + 10;
        }
        int dy = 3;

        if (!AbstractLookAndFeel.getTheme().isMacStyleWindowDecorationOn() && (dw > 0)) {
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

    public void paintText(Graphics g, int x, int y, String title) {
        x += paintIcon(g, x, y);
        if (isActive()) {
            Color titleColor = AbstractLookAndFeel.getWindowTitleForegroundColor();
            if (ColorHelper.getGrayValue(titleColor) > 164) {
                g.setColor(Color.black);
                JTattooUtilities.drawString(rootPane, g, title, x + 1, y + 1);
            }
            g.setColor(titleColor);
            JTattooUtilities.drawString(rootPane, g, title, x, y);
        } else {
            g.setColor(AbstractLookAndFeel.getWindowInactiveTitleForegroundColor());
            JTattooUtilities.drawString(rootPane, g, title, x, y);
        }
    }

}
