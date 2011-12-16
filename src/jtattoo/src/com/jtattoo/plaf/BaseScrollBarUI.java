/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

/**
 * @author Michael Hagen
 */
public class BaseScrollBarUI extends BasicScrollBarUI {

    protected int scrollBarWidth = 17;
    protected boolean isRollover = false;

    public static ComponentUI createUI(JComponent c) {
        return new BaseScrollBarUI();
    }

    protected void installDefaults() {
        scrollBarWidth = UIManager.getInt("ScrollBar.width");
        super.installDefaults();
    }

    protected JButton createDecreaseButton(int orientation) {
        return new BaseScrollButton(orientation, scrollBarWidth);
    }

    protected JButton createIncreaseButton(int orientation) {
        return new BaseScrollButton(orientation, scrollBarWidth);
    }

    public TrackListener createTrackListener() {
        return new MyTrackListener();
    }

    public Dimension getPreferredSize(JComponent c) {
        if (scrollbar.getOrientation() == JScrollBar.VERTICAL) {
            return new Dimension(scrollBarWidth, scrollBarWidth * 3 + 16);
        } else {
            return new Dimension(scrollBarWidth * 3 + 16, scrollBarWidth);
        }
    }

    protected Dimension getMinimumThumbSize() {
        return new Dimension(scrollBarWidth, scrollBarWidth);
    }

    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        int w = c.getWidth();
        int h = c.getHeight();
        if (scrollbar.getOrientation() == JScrollBar.VERTICAL) {
            JTattooUtilities.fillVerGradient(g, AbstractLookAndFeel.getTheme().getTrackColors(), 0, 0, w, h);
        } else {
            JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme().getTrackColors(), 0, 0, w, h);
        }
    }

    protected Color[] getThumbColors() {
        if (isRollover || isDragging) {
            return AbstractLookAndFeel.getTheme().getRolloverColors();
        } else if (!JTattooUtilities.isActive(scrollbar)) {
            return AbstractLookAndFeel.getTheme().getInActiveColors();
        } else {
            return AbstractLookAndFeel.getTheme().getThumbColors();
        }
    }

    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        if (!c.isEnabled()) {
            return;
        }

        g.translate(thumbBounds.x, thumbBounds.y);

        Color colors[] = getThumbColors();

        Color frameColorHi = ColorHelper.brighter(colors[1], 20);
        Color frameColorLo = ColorHelper.darker(colors[colors.length - 1], 10);

        if (scrollbar.getOrientation() == JScrollBar.VERTICAL) {
            JTattooUtilities.fillVerGradient(g, colors, 1, 1, thumbBounds.width - 1, thumbBounds.height - 1);
            JTattooUtilities.draw3DBorder(g, frameColorLo, ColorHelper.darker(frameColorLo, 15), 0, 0, thumbBounds.width, thumbBounds.height);
            g.setColor(frameColorHi);
            g.drawLine(1, 1, thumbBounds.width - 2, 1);
            g.drawLine(1, 1, 1, thumbBounds.height - 2);

            Graphics2D g2D = (Graphics2D) g;
            Composite composite = g2D.getComposite();
            AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f);
            g2D.setComposite(alpha);

            int dx = 5;
            int dy = thumbBounds.height / 2 - 3;
            int dw = thumbBounds.width - 11;

            Color c1 = Color.white;
            Color c2 = Color.darkGray;

            for (int i = 0; i < 4; i++) {
                g.setColor(c1);
                g.drawLine(dx, dy, dx + dw, dy);
                dy++;
                g.setColor(c2);
                g.drawLine(dx, dy, dx + dw, dy);
                dy++;
            }
            g2D.setComposite(composite);
        } else // HORIZONTAL
        {
            JTattooUtilities.fillHorGradient(g, colors, 1, 1, thumbBounds.width - 1, thumbBounds.height - 1);
            JTattooUtilities.draw3DBorder(g, frameColorLo, ColorHelper.darker(frameColorLo, 10), 0, 0, thumbBounds.width, thumbBounds.height);
            g.setColor(frameColorHi);
            g.drawLine(1, 1, thumbBounds.width - 2, 1);
            g.drawLine(1, 1, 1, thumbBounds.height - 2);

            int dx = thumbBounds.width / 2 - 3;
            int dy = 5;
            int dh = thumbBounds.height - 11;

            Graphics2D g2D = (Graphics2D) g;
            Composite composite = g2D.getComposite();
            AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f);
            g2D.setComposite(alpha);

            Color c1 = Color.white;
            Color c2 = Color.darkGray;

            for (int i = 0; i < 4; i++) {
                g.setColor(c1);
                g.drawLine(dx, dy, dx, dy + dh);
                dx++;
                g.setColor(c2);
                g.drawLine(dx, dy, dx, dy + dh);
                dx++;
            }
            g2D.setComposite(composite);
        }

        g.translate(-thumbBounds.x, -thumbBounds.y);
    }

    protected class MyTrackListener extends TrackListener {

        public void mouseEntered(MouseEvent e) {
            super.mouseEntered(e);
            isRollover = true;
            Rectangle r = getTrackBounds();
            scrollbar.repaint(r.x, r.y, r.width, r.height);
        }

        public void mouseExited(MouseEvent e) {
            super.mouseExited(e);
            isRollover = false;
            Rectangle r = getTrackBounds();
            scrollbar.repaint(r.x, r.y, r.width, r.height);
        }

        public void mousePressed(MouseEvent e) {
            super.mousePressed(e);
            Rectangle r = getTrackBounds();
            scrollbar.repaint(r.x, r.y, r.width, r.height);
        }

        public void mouseReleased(MouseEvent e) {
            super.mouseReleased(e);
            Rectangle r = getTrackBounds();
            scrollbar.repaint(r.x, r.y, r.width, r.height);
        }
    }
}
