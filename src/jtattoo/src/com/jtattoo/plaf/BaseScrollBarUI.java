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

package com.jtattoo.plaf;

import java.awt.*;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicScrollBarUI;

/**
 * @author Michael Hagen
 */
public class BaseScrollBarUI extends BasicScrollBarUI {

    protected int scrollBarWidth = 17;
    protected int incrGap = 0;
    protected int decrGap = 0;
    protected boolean isRollover = false;

    public static ComponentUI createUI(JComponent c) {
        return new BaseScrollBarUI();
    }

    protected void installDefaults() {
        super.installDefaults();
        
        scrollBarWidth = UIManager.getInt("ScrollBar.width");
        incrGap = UIManager.getInt("ScrollBar.incrementButtonGap");
        decrGap = UIManager.getInt("ScrollBar.decrementButtonGap");

        // TODO this can be removed when incrGap/decrGap become protected
        // handle scaling for sizeVarients for special case components. The
        // key "JComponent.sizeVariant" scales for large/small/mini
        // components are based on Apples LAF
        String scaleKey = (String)scrollbar.getClientProperty("JComponent.sizeVariant");
        if (scaleKey != null){
            if ("large".equals(scaleKey)){
                scrollBarWidth *= 1.15;
                incrGap *= 1.15;
                decrGap *= 1.15;
            } else if ("small".equals(scaleKey)){
                scrollBarWidth *= 0.857;
                incrGap *= 0.857;
                decrGap *= 0.857;
            } else if ("mini".equals(scaleKey)){
                scrollBarWidth *= 0.714;
                incrGap *= 0.714;
                decrGap *= 0.714;
            }
        }
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

        Graphics2D g2D = (Graphics2D) g;
        Composite savedComposite = g2D.getComposite();
        if (scrollbar.getOrientation() == JScrollBar.VERTICAL) {
            JTattooUtilities.fillVerGradient(g, colors, 1, 1, thumbBounds.width - 1, thumbBounds.height - 1);
            JTattooUtilities.draw3DBorder(g, frameColorLo, ColorHelper.darker(frameColorLo, 15), 0, 0, thumbBounds.width, thumbBounds.height);

            g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
            g.setColor(frameColorHi);
            g.drawLine(1, 1, thumbBounds.width - 2, 1);
            g.drawLine(1, 1, 1, thumbBounds.height - 2);

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
            g2D.setComposite(savedComposite);
        } else { // HORIZONTAL
            JTattooUtilities.fillHorGradient(g, colors, 1, 1, thumbBounds.width - 1, thumbBounds.height - 1);
            JTattooUtilities.draw3DBorder(g, frameColorLo, ColorHelper.darker(frameColorLo, 10), 0, 0, thumbBounds.width, thumbBounds.height);

            g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
            g.setColor(frameColorHi);
            g.drawLine(1, 1, thumbBounds.width - 2, 1);
            g.drawLine(1, 1, 1, thumbBounds.height - 2);

            int dx = thumbBounds.width / 2 - 3;
            int dy = 5;
            int dh = thumbBounds.height - 11;

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
        }
        g2D.setComposite(savedComposite);

        g.translate(-thumbBounds.x, -thumbBounds.y);
    }

    protected void layoutVScrollbar(JScrollBar sb) {
        if (AbstractLookAndFeel.getTheme().isLinuxStyleScrollBarOn()) {
            Dimension sbSize = sb.getSize();
            Insets sbInsets = sb.getInsets();
            int sizeH = sbSize.height - sbInsets.top - sbInsets.bottom;

            /*
             * Width and left edge of the buttons and thumb.
             */
            int itemX = sbInsets.left;
            int itemW = sbSize.width - (sbInsets.left + sbInsets.right);
            int itemH = Math.min(itemW, sizeH / 2);
            
            /* Nominal locations of the buttons, assuming their preferred
             * size will fit.
             */
            int decrButtonY = sbSize.height - sbInsets.bottom - itemH - itemH + 1;
            int incrButtonY = sbSize.height - sbInsets.bottom - itemH;

            /* Compute the height and origin of the thumb. The case
             * where the thumb is at the bottom edge is handled specially 
             * to avoid numerical problems in computing thumbY.  Enforce
             * the thumbs min/max dimensions. If the thumb doesn't
             * fit in the track (trackH) we'll hide it later.
             */
            float trackH = sbSize.height - sbInsets.top - sbInsets.bottom - itemW - itemW + 1;
            float min = sb.getMinimum();
            float max = sb.getMaximum();
            float extent = sb.getVisibleAmount();
            float range = max - min;
            float value = sb.getValue();
            
            int maxThumbH = getMaximumThumbSize().height;
            int minThumbH = getMinimumThumbSize().height;
            int thumbH = (range <= 0) ? maxThumbH : (int) (trackH * (extent / range));
            thumbH = Math.max(thumbH, minThumbH);
            thumbH = Math.min(thumbH, maxThumbH);

            int thumbY = decrButtonY - thumbH;
            if (value < (max - extent)) {
                float thumbRange = trackH - thumbH;
                thumbY = (int) (0.5f + (thumbRange * ((value - min) / (range - extent))));
            }
            
            /* If the thumb isn't going to fit, zero it's bounds.  Otherwise
             * make sure it fits between the buttons.  Note that setting the
             * thumbs bounds will cause a repaint.
             */
            if (thumbH > trackH) {
                setThumbBounds(0, 0, 0, 0);
            } else {
                setThumbBounds(itemX, thumbY, itemW, thumbH);
            }
            decrButton.setBounds(itemX, decrButtonY, itemW, itemH);
            incrButton.setBounds(itemX, incrButtonY, itemW, itemH);
            
            /* Update the trackRect field.
             */
            trackRect.setBounds(itemX, 0, itemW, (int)trackH);
            
        } else {
            super.layoutVScrollbar(sb);
        }
    }

    protected void layoutHScrollbar(JScrollBar sb) {
        if (AbstractLookAndFeel.getTheme().isLinuxStyleScrollBarOn()) {
            Dimension sbSize = sb.getSize();
            Insets sbInsets = sb.getInsets();
            int sizeW = sbSize.width - sbInsets.left - sbInsets.right;

            /*
             * Height and top edge of the buttons and thumb.
             */
            int itemY = sbInsets.top;
            int itemH = sbSize.height - (sbInsets.top + sbInsets.bottom);//Math.min(itemW, sizeH / 2);
            int itemW = Math.min(itemH, sizeW / 2);//sbSize.width - (sbInsets.left + sbInsets.right);
            
            /* Nominal locations of the buttons, assuming their preferred
             * size will fit.
             */
            int decrButtonX = sbSize.width - sbInsets.right - itemW - itemW + 1;
            int incrButtonX = sbSize.width - sbInsets.right - itemW;

            /* Compute the width and origin of the thumb. The case
             * where the thumb is at the right edge is handled specially 
             * to avoid numerical problems in computing thumbX.  Enforce
             * the thumbs min/max dimensions. If the thumb doesn't
             * fit in the track (trackW) we'll hide it later.
             */
            float trackW = sbSize.width - sbInsets.left - sbInsets.right - itemH - itemH + 1;
            float min = sb.getMinimum();
            float max = sb.getMaximum();
            float extent = sb.getVisibleAmount();
            float range = max - min;
            float value = sb.getValue();
            
            int maxThumbW = getMaximumThumbSize().width;
            int minThumbW = getMinimumThumbSize().width;
            int thumbW = (range <= 0) ? maxThumbW : (int) (trackW * (extent / range));
            thumbW = Math.max(thumbW, minThumbW);
            thumbW = Math.min(thumbW, maxThumbW);

            int thumbX = decrButtonX - thumbW;
            if (value < (max - extent)) {
                float thumbRange = trackW - thumbW;
                thumbX = (int) (0.5f + (thumbRange * ((value - min) / (range - extent))));
            }
            
            /* If the thumb isn't going to fit, zero it's bounds.  Otherwise
             * make sure it fits between the buttons.  Note that setting the
             * thumbs bounds will cause a repaint.
             */
            if (thumbW > trackW) {
                setThumbBounds(0, 0, 0, 0);
            } else {
                setThumbBounds(thumbX, itemY, thumbW, itemH);
            }
            decrButton.setBounds(decrButtonX, itemY,  itemW, itemH);
            incrButton.setBounds(incrButtonX, itemY, itemW, itemH);
            
            /* Update the trackRect field.
             */
            trackRect.setBounds(0, itemY, (int)trackW, itemH);
            
        } else {
            super.layoutHScrollbar(sb);
        }
    }
    
//-----------------------------------------------------------------------------
// inner classes    
//-----------------------------------------------------------------------------
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
