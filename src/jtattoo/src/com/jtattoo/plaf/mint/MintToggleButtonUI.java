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
 
package com.jtattoo.plaf.mint;

import com.jtattoo.plaf.*;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;

/**
 * @author Michael Hagen
 */
public class MintToggleButtonUI extends BaseToggleButtonUI {

    public static ComponentUI createUI(JComponent c) {
        return new MintToggleButtonUI();
    }

    protected void paintBackground(Graphics g, AbstractButton b) {
        if (!b.isContentAreaFilled() || (b.getParent() instanceof JMenuBar)) {
            return;
        }

        if (!(b.isBorderPainted() && (b.getBorder() instanceof UIResource))) {
            super.paintBackground(g, b);
            return;
        }
        Graphics2D g2D = (Graphics2D) g;
        int width = b.getWidth() - 2;
        int height = b.getHeight() - 2;
        ButtonModel model = b.getModel();
        if (model.isPressed() && model.isArmed()) {
            Color color = AbstractLookAndFeel.getTheme().getSelectionBackgroundColor();
            g2D.setColor(color);
            g2D.fillRoundRect(0, 0, width, height, height, height);
            g2D.setColor(AbstractLookAndFeel.getTheme().getFrameColor());
            Object savedRederingHint = g2D.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
            g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2D.drawRoundRect(0, 0, width - 1, height - 1, height, height);
            g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, savedRederingHint);
            return;
        } else if (model.isSelected()) {
            Color frameColor = b.getParent().getBackground();
            Color[] colors = ColorHelper.createColorArr(AbstractLookAndFeel.getTheme().getBackgroundColor(), Color.white, 20);
            Shape savedClip = g2D.getClip();
            Area area = new Area(new Area(new RoundRectangle2D.Double(0, 0, width, height, height, height)));
            g2D.setClip(area);
            JTattooUtilities.fillHorGradient(g, colors, 0, 0, width, height);
            g2D.setClip(savedClip);
            JTattooUtilities.drawRound3DBorder(g, ColorHelper.darker(frameColor, 5), ColorHelper.brighter(frameColor, 80), 0, 0, width, height);
            JTattooUtilities.drawRound3DBorder(g, ColorHelper.darker(frameColor, 20), ColorHelper.brighter(frameColor, 10), 1, 1, width - 2, height - 2);
            return;
        }

        Color colors[] = AbstractLookAndFeel.getTheme().getButtonColors();
        if (!model.isEnabled()) {
            colors = AbstractLookAndFeel.getTheme().getDisabledColors();
        } else if (b.isRolloverEnabled() && model.isRollover()) {
            Color[] src = AbstractLookAndFeel.getTheme().getRolloverColors();
            colors = new Color[src.length];
            System.arraycopy(src, 0, colors, 0, colors.length);
            colors[colors.length - 2] = ColorHelper.darker(colors[colors.length - 2], 15);
        }
        Object savedRederingHint = g2D.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Paint shadow
        Color color = b.getParent().getBackground();
        g2D.setColor(ColorHelper.darker(color, 6));
        g2D.drawRoundRect(2, 2, width - 1, height - 1, height, height);
        g2D.setColor(ColorHelper.darker(color, 18));
        g2D.drawRoundRect(1, 1, width - 1, height - 1, height, height);
        // paint background
        int x = 0;
        int y = 0;
        int w = width;
        int h = height;
        for (int i = colors.length - 1; i >= 0; i--) {
            g2D.setColor(colors[i]);
            g2D.fillRoundRect(x, y, w, h, h, h);
            h--;
            w--;
            if (((i + 1) % 4) == 0) {
                x++;
                y++;
            }
            if (h == 0) {
                break;
            }
        }
        g2D.setColor(Color.white);
        g2D.drawRoundRect(1, 1, width - 3, height - 3, height - 2, height - 2);
        g2D.drawRoundRect(1, 1, width - 3, height - 3, height - 2, height - 2);
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, savedRederingHint);
    }

    protected void paintFocus(Graphics g, AbstractButton b, Rectangle viewRect, Rectangle textRect, Rectangle iconRect) {
        Graphics2D g2D = (Graphics2D) g;
        Object savedRenderingHint = g2D.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2D.setColor(AbstractLookAndFeel.getFocusColor());
        int d = b.getHeight() - 6;
        g2D.drawRoundRect(2, 2, b.getWidth() - 7, b.getHeight() - 7, d, d);
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, savedRenderingHint);
    }
}
