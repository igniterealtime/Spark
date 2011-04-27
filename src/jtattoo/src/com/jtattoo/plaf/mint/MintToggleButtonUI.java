/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.mint;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.plaf.*;

import com.jtattoo.plaf.*;

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
            Color color = MintLookAndFeel.getTheme().getSelectionBackgroundColor();
            g2D.setColor(color);
            g2D.fillRoundRect(0, 0, width, height, height, height);
            g2D.setColor(MintLookAndFeel.getTheme().getFrameColor());
            Object savedRederingHint = g2D.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
            g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2D.drawRoundRect(0, 0, width - 1, height - 1, height, height);
            g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, savedRederingHint);
            return;
        } else if (model.isSelected()) {
            Color frameColor = b.getParent().getBackground();
            Color[] colors = ColorHelper.createColorArr(MintLookAndFeel.getTheme().getBackgroundColor(), Color.white, 20);
            Shape savedClip = g2D.getClip();
            Area area = new Area(new Area(new RoundRectangle2D.Double(0, 0, width, height, height, height)));
            g2D.setClip(area);
            JTattooUtilities.fillHorGradient(g, colors, 0, 0, width, height);
            g2D.setClip(savedClip);
            //JTattooUtilities.drawRound3DBorder(g, Color.red, Color.blue, 0, 0, width, height, height / 2);
            JTattooUtilities.drawRound3DBorder(g, ColorHelper.darker(frameColor, 5), ColorHelper.brighter(frameColor, 80), 0, 0, width, height);
            JTattooUtilities.drawRound3DBorder(g, ColorHelper.darker(frameColor, 20), ColorHelper.brighter(frameColor, 10), 1, 1, width - 2, height - 2);
            return;
        }

        Color colors[] = MintLookAndFeel.getTheme().getButtonColors();
        if (!model.isEnabled()) {
            colors = MintLookAndFeel.getTheme().getDisabledColors();
        } else if (model.isRollover()) {
            Color[] src = MintLookAndFeel.getTheme().getRolloverColors();
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
