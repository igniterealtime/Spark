/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */

package com.jtattoo.plaf.custom.pulsar;

import java.awt.*;
import javax.swing.*;

import com.jtattoo.plaf.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

/**
 * @author Michael Hagen
 */
public class PulsarInternalFrameTitlePane extends BaseInternalFrameTitlePane {
    private static Color ALTERNATE_COLORS[] = new Color[] {
            new Color(44, 183, 236),
            new Color(50, 188, 241),
            new Color(56, 192, 245),
            new Color(62, 196, 251),
            new Color(66, 198, 253),
            new Color(67, 200, 255),
            new Color(70, 201, 255),
            new Color(72, 203, 255),
            new Color(68, 195, 254),
            new Color(68, 195, 254),
            new Color(59, 175, 242),
            new Color(57, 174, 241),
            new Color(57, 174, 241),
            new Color(56, 172, 239),
            new Color(52, 168, 236),
            new Color(47, 164, 231),
            new Color(41, 157, 224),
            new Color(41, 157, 224),
            new Color(33, 150, 218),
            new Color(24, 141, 210),
            new Color(17, 138, 205),
            new Color( 0, 124, 189),
        };
    
    public PulsarInternalFrameTitlePane(JInternalFrame f) { 
        super(f); 
    }
    
    protected int getHorSpacing() {
        return 0;
    }
    
    protected int getVerSpacing() {
        return 0;
    }

    public void paintBorder(Graphics g) {
    }

    public void paintBackground(Graphics g) {
        Graphics2D g2D = (Graphics2D) g;
        int w = getWidth();
        int h = getHeight();
        if (isActive()) {
            JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme().getWindowTitleColors(), 0, 0, w, h - 2);
        } else {
            JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme().getWindowInactiveTitleColors(), 0, 0, w, h - 2);
        }
        Shape savedClip = g.getClip();
        int dw = getHeight() * (getButtonCount() + 1);
        int ex = w - dw;
        int ey = -h;
        int ew = 80;
        int eh = 2 * h;
        Area clipArea = new Area(savedClip);
        Area ellipseArea = new Area(new Ellipse2D.Double(ex, ey, ew, eh));
        clipArea.intersect(ellipseArea);
        g2D.setClip(clipArea);
        JTattooUtilities.fillHorGradient(g, ALTERNATE_COLORS, Math.max(0, w - dw - ew), 0, w, h - 2);
        g2D.setClip(savedClip);
        JTattooUtilities.fillHorGradient(g, ALTERNATE_COLORS, Math.max(0, w - dw + (ew / 2)), 0, w, h - 2);

        Color fc1 = ColorHelper.darker(AbstractLookAndFeel.getWindowTitleColorDark(), 10);
        Color fc2 = ColorHelper.darker(AbstractLookAndFeel.getWindowTitleColorDark(), 30);
        g.setColor(fc1);
        g.drawLine(0, h - 2, w, h - 2);
        g.setColor(fc2);
        g.drawLine(0, h - 1, w, h - 1);

        fc1 = ColorHelper.darker(ALTERNATE_COLORS[ALTERNATE_COLORS.length - 1], 10);
        fc2 = ColorHelper.darker(ALTERNATE_COLORS[ALTERNATE_COLORS.length - 1], 30);
        Color fc3 = ColorHelper.brighter(ALTERNATE_COLORS[ALTERNATE_COLORS.length - 1], 30);
        g.setColor(fc1);
        g.drawLine(w - dw + (ew / 3) , h - 2, w, h - 2);
        g.setColor(fc2);
        g.drawLine(w - dw + (ew / 2), h - 1, w, h - 1);

        Object savedRenderingHint = g2D.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Composite savedComposite = g2D.getComposite();
        g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
        g.setColor(fc3);
        g.drawArc(ex + 2, ey - 1, ew + 2, eh, 180, 90);
        g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        g.setColor(fc1);
        g.drawArc(ex + 1, ey, ew + 1, eh, 180, 90);
        g2D.setComposite(savedComposite);
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, savedRenderingHint);
    }

    public void paintText(Graphics g, int x, int y, String title) {
        Graphics2D g2D = (Graphics2D)g;
        Color fc = AbstractLookAndFeel.getWindowTitleForegroundColor();
        Color bc = AbstractLookAndFeel.getWindowTitleBackgroundColor();
        if (fc.equals(Color.white)) {
            g2D.setColor(bc);
            JTattooUtilities.drawString(frame, g, title, x-1, y-1);
            g2D.setColor(ColorHelper.darker(bc, 30));
            JTattooUtilities.drawString(frame, g, title, x+1, y+1);
        }
        g.setColor(fc);
        JTattooUtilities.drawString(frame, g, title, x, y);
    }

    private int getButtonCount() {
        int buttonCount = 1;
        if (frame.isIconifiable()) {
            buttonCount++;
        }
        if (frame.isMaximizable()) {
            buttonCount++;
        }
        return buttonCount;
    }


}
