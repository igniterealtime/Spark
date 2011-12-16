/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.hifi;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;

import com.jtattoo.plaf.*;

/**
 *
 * @author  Michael Hagen
 */
public class HiFiScrollBarUI extends BaseScrollBarUI {

    private static final Color frameHiColor = new Color(128, 128, 128);
    private static final Color frameLoColor = new Color(96, 96, 96);

    public static ComponentUI createUI(JComponent c) {
        return new HiFiScrollBarUI();
    }

    protected JButton createDecreaseButton(int orientation) {
        return new HiFiScrollButton(orientation, scrollBarWidth);
    }

    protected JButton createIncreaseButton(int orientation) {
        return new HiFiScrollButton(orientation, scrollBarWidth);
    }

    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        if (!c.isEnabled()) {
            return;
        }

        Graphics2D g2D = (Graphics2D) g;
        Composite composite = g2D.getComposite();

        int x = thumbBounds.x;
        int y = thumbBounds.y;
        int width = thumbBounds.width;
        int height = thumbBounds.height;

        g.translate(x, y);

        Color[] colors = getThumbColors();
        if (scrollbar.getOrientation() == JScrollBar.VERTICAL) {
            JTattooUtilities.fillVerGradient(g, colors, 1, 1, width - 1, height - 1);
            AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f);
            g2D.setComposite(alpha);
            int dx = 5;
            int dy = height / 2 - 3;
            int dw = width - 12;
            Color c1 = ColorHelper.brighter(colors[0], 60);
            Color c2 = ColorHelper.darker(colors[0], 30);
            for (int i = 0; i < 4; i++) {
                g.setColor(c1);
                g.drawLine(dx, dy, dx + dw, dy);
                dy++;
                g.setColor(c2);
                g.drawLine(dx, dy, dx + dw, dy);
                dy++;
            }
        } else {
            // HORIZONTAL
            JTattooUtilities.fillHorGradient(g, colors, 1, 1, width - 1, height - 1);
            int dx = width / 2 - 3;
            int dy = 5;
            int dh = height - 12;
            AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f);
            g2D.setComposite(alpha);
            Color c1 = ColorHelper.brighter(colors[0], 60);
            Color c2 = ColorHelper.darker(colors[0], 30);
            for (int i = 0; i < 4; i++) {
                g.setColor(c1);
                g.drawLine(dx, dy, dx, dy + dh);
                dx++;
                g.setColor(c2);
                g.drawLine(dx, dy, dx, dy + dh);
                dx++;
            }
        }

        g2D.setComposite(composite);
        JTattooUtilities.draw3DBorder(g, Color.darkGray, Color.black, 0, 0, width, height);
        g.setColor(frameHiColor);
        g.drawLine(1, 1, width - 2, 1);
        g.drawLine(1, 1, 1, height - 3);
        g.setColor(frameLoColor);
        g.drawLine(width - 2, 1, width - 2, height - 3);
        g.drawLine(2, height - 2, width - 3, height - 2);

        AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
        g2D.setComposite(alpha);
        Color fc = colors[colors.length - 1];
        g2D.setColor(fc);
        g.drawLine(3, 2, width - 4, 2);
        g.drawLine(2, 3, 2, height - 4);

        g.setColor(ColorHelper.darker(fc, 30));
        g.drawLine(width - 1, 1, width - 1, height - 3);
        g.drawLine(3, height - 1, width - 3, height - 1);
        alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f);
        g2D.setComposite(alpha);
        g.drawLine(1, height - 2, 2, height - 1);
        g.drawLine(width - 1, height - 2, width - 2, height - 1);

        g.translate(-x, -y);
        g2D.setComposite(composite);
    }
}
