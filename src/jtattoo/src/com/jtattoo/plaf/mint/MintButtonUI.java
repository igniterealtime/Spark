/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.mint;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;

import com.jtattoo.plaf.*;
import javax.swing.plaf.basic.BasicGraphicsUtils;

/**
 * @author Michael Hagen
 */
public class MintButtonUI extends BaseButtonUI {

    public static ComponentUI createUI(JComponent c) {
        return new MintButtonUI();
    }

    protected void paintBackground(Graphics g, AbstractButton b) {
        if (!b.isContentAreaFilled() || (b.getParent() instanceof JMenuBar)) {
            return;
        }

        if (!(b.isBorderPainted() && (b.getBorder() instanceof UIResource))) {
            super.paintBackground(g, b);
            return;
        }

        if ((b.getWidth() < 32) || (b.getHeight() < 16)) {
            ButtonModel model = b.getModel();
            Color color = MintLookAndFeel.getTheme().getButtonBackgroundColor();
            if (model.isPressed() && model.isArmed()) {
                color = MintLookAndFeel.getTheme().getSelectionBackgroundColor();
            } else if (model.isRollover()) {
                color = MintLookAndFeel.getTheme().getRolloverColor();
            }
            g.setColor(color);
            g.fillRect(0, 0, b.getWidth(), b.getHeight());
            JTattooUtilities.draw3DBorder(g, Color.white, Color.lightGray, 0, 0, b.getWidth(), b.getHeight());
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
            g2D.setColor(ColorHelper.darker(color, 40));
            Object savedRederingHint = g2D.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
            g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2D.drawRoundRect(0, 0, width - 1, height - 1, height, height);
            g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, savedRederingHint);
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
        int width = b.getWidth();
        int height = b.getHeight();
        if (!b.isContentAreaFilled()
                || ((width < 64) || (height < 16)) && ((b.getText() == null) || b.getText().equals(""))) {
            g.setColor(AbstractLookAndFeel.getFocusColor());
            BasicGraphicsUtils.drawDashedRect(g, 4, 3, width - 8, height - 6);
        } else {
            Object savedRenderingHint = g2D.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
            g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2D.setColor(AbstractLookAndFeel.getFocusColor());
            int d = height - 6;
            g2D.drawRoundRect(2, 2, width - 7, height - 7, d, d);
            g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, savedRenderingHint);
        }
    }
}
