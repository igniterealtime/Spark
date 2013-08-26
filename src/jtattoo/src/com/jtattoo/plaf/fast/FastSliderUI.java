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
 
package com.jtattoo.plaf.fast;

import com.jtattoo.plaf.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.ComponentUI;

/**
 * @author Michael Hagen
 */
public class FastSliderUI extends BaseSliderUI {

    private static ThumbHorIcon thumbHorIcon = new ThumbHorIcon();
    private static ThumbVerIcon thumbVerIcon = new ThumbVerIcon();

    public FastSliderUI(JSlider slider) {
        super(slider);
    }

    public static ComponentUI createUI(JComponent c) {
        return new FastSliderUI((JSlider) c);
    }

    public Icon getThumbHorIcon() {
        return thumbHorIcon;
    }

    public Icon getThumbHorIconRollover() {
        return thumbHorIcon;
    }

    public Icon getThumbVerIcon() {
        return thumbVerIcon;
    }

    public Icon getThumbVerIconRollover() {
        return thumbVerIcon;
    }

    public void paintTrack(Graphics g) {
        boolean leftToRight = JTattooUtilities.isLeftToRight(slider);

        g.translate(trackRect.x, trackRect.y);
        int overhang = 5;
        int trackLeft = 0;
        int trackTop = 0;
        int trackRight = 0;
        int trackBottom = 0;

        // Draw the track
        if (slider.getOrientation() == JSlider.HORIZONTAL) {
            trackBottom = (trackRect.height - 1) - overhang;
            trackTop = trackBottom - (getTrackWidth() - 1);
            trackRight = trackRect.width - 1;
        } else {
            if (leftToRight) {
                trackLeft = (trackRect.width - overhang) -
                        getTrackWidth();
                trackRight = (trackRect.width - overhang) - 1;
            } else {
                trackLeft = overhang;
                trackRight = overhang + getTrackWidth() - 1;
            }
            trackBottom = trackRect.height - 1;
        }

        g.setColor(Color.gray);
        g.drawRect(trackLeft, trackTop, (trackRight - trackLeft) - 1, (trackBottom - trackTop) - 1);

        int middleOfThumb = 0;
        int fillTop = 0;
        int fillLeft = 0;
        int fillBottom = 0;
        int fillRight = 0;

        if (slider.getOrientation() == JSlider.HORIZONTAL) {
            middleOfThumb = thumbRect.x + (thumbRect.width / 2);
            middleOfThumb -= trackRect.x; // To compensate for the g.translate()
            fillTop = trackTop + 1;
            fillBottom = trackBottom - 2;

            if (!drawInverted()) {
                fillLeft = trackLeft + 1;
                fillRight = middleOfThumb;
            } else {
                fillLeft = middleOfThumb;
                fillRight = trackRight - 2;
            }
//            if (slider.isEnabled()) {
                g.setColor(AbstractLookAndFeel.getControlBackgroundColor());
                g.fillRect(fillLeft, fillTop, fillRight - fillLeft, fillBottom - fillTop + 1);
//            } else {
//                g.setColor(slider.getBackground());
//                g.fillRect(fillLeft, fillTop, fillRight - fillLeft, fillBottom - fillTop);
//            }
        } else {
            middleOfThumb = thumbRect.y + (thumbRect.height / 2);
            middleOfThumb -= trackRect.y; // To compensate for the g.translate()
            fillLeft = trackLeft + 1;
            fillRight = trackRight - 2;

            if (!drawInverted()) {
                fillTop = middleOfThumb;
                fillBottom = trackBottom - 2;
            } else {
                fillTop = trackTop + 1;
                fillBottom = middleOfThumb;
            }
//            if (slider.isEnabled()) {
                g.setColor(AbstractLookAndFeel.getControlBackgroundColor());
                g.fillRect(fillLeft, fillTop, fillRight - fillLeft + 1, fillBottom - fillTop + 1);
//            } else {
//                g.setColor(slider.getBackground());
//                g.fillRect(fillLeft, fillTop, fillRight - fillLeft + 1, fillBottom - fillTop + 1);
//            }
        }

        g.translate(-trackRect.x, -trackRect.y);
    }

    private static class ThumbHorIcon implements Icon {

        private static final int WIDTH = 11;
        private static final int HEIGHT = 18;

        public void paintIcon(Component c, Graphics g, int x, int y) {
            int w = WIDTH - 1;
            int h = HEIGHT - 1;
            int dw = WIDTH / 2;
            Color backColor = AbstractLookAndFeel.getControlBackgroundColor();
            Color loColor = AbstractLookAndFeel.getFrameColor();
            Color hiColor = ColorHelper.brighter(backColor, 40);
            Polygon poly = new Polygon();
            poly.addPoint(x, y);
            poly.addPoint(x + w, y);
            poly.addPoint(x + w, y + h - dw);
            poly.addPoint(x + dw, y + h);
            poly.addPoint(x, y + h - dw);
            g.setColor(backColor);
            g.fillPolygon(poly);
            g.setColor(loColor);
            g.drawPolygon(poly);
            g.setColor(hiColor);
            g.drawLine(x + 1, y + 1, x + w - 1, y + 1);
            g.drawLine(x + 1, y + 1, x + 1, y + h - dw);
        }

        public int getIconWidth() {
            return WIDTH;
        }

        public int getIconHeight() {
            return HEIGHT;
        }
    }

    private static class ThumbVerIcon implements Icon {

        private static final int WIDTH = 18;
        private static final int HEIGHT = 11;

        public void paintIcon(Component c, Graphics g, int x, int y) {
            int w = WIDTH - 1;
            int h = HEIGHT - 1;
            int dh = HEIGHT / 2;
            Color backColor = AbstractLookAndFeel.getControlBackgroundColor();
            Color loColor = AbstractLookAndFeel.getFrameColor();
            Color hiColor = ColorHelper.brighter(backColor, 40);
            Polygon poly = new Polygon();
            poly.addPoint(x, y);
            poly.addPoint(x + w - dh, y);
            poly.addPoint(x + w, y + dh);
            poly.addPoint(x + w - dh, y + h);
            poly.addPoint(x, y + h);
            g.setColor(backColor);
            g.fillPolygon(poly);
            g.setColor(loColor);
            g.drawPolygon(poly);
            g.setColor(hiColor);
            g.drawLine(x + 1, y + 1, x + w - dh, y + 1);
            g.drawLine(x + 1, y + 1, x + 1, y + h - 1);
        }

        public int getIconWidth() {
            return WIDTH;
        }

        public int getIconHeight() {
            return HEIGHT;
        }
    }
}
