/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.hifi;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.text.*;

import com.jtattoo.plaf.*;

/**
 * @author Michael Hagen
 */
public class HiFiTabbedPaneUI extends BaseTabbedPaneUI {

    public static ComponentUI createUI(JComponent c) {
        return new HiFiTabbedPaneUI();
    }

    protected Color[] getContentBorderColors(int tabPlacement) {
        Color SEP_COLORS[] = {
            ColorHelper.darker(AbstractLookAndFeel.getBackgroundColor(), 40),
            ColorHelper.brighter(AbstractLookAndFeel.getBackgroundColor(), 20),
            ColorHelper.darker(AbstractLookAndFeel.getBackgroundColor(), 20),
            ColorHelper.darker(AbstractLookAndFeel.getBackgroundColor(), 40),
        };
        return SEP_COLORS;
    }

    protected void paintText(Graphics g, int tabPlacement, Font font, FontMetrics metrics, int tabIndex, String title, Rectangle textRect, boolean isSelected) {
        g.setFont(font);
        View v = getTextViewForTab(tabIndex);
        if (v != null) {
            // html
            Graphics2D g2D = (Graphics2D) g;
            Object savedRenderingHint = null;
            if (AbstractLookAndFeel.getTheme().isTextAntiAliasingOn()) {
                savedRenderingHint = g2D.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
                g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, AbstractLookAndFeel.getTheme().getTextAntiAliasingHint());
            }
            v.paint(g, textRect);
            if (AbstractLookAndFeel.getTheme().isTextAntiAliasingOn()) {
                g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, savedRenderingHint);
            }
        } else {
            // plain text
            int mnemIndex = -1;
            if (JTattooUtilities.getJavaVersion() >= 1.4) {
                mnemIndex = tabPane.getDisplayedMnemonicIndexAt(tabIndex);
            }

            Graphics2D g2D = (Graphics2D) g;
            Composite composite = g2D.getComposite();
            AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f);
            g2D.setComposite(alpha);
            Color fc = tabPane.getForegroundAt(tabIndex);
            if (isSelected) {
                fc = AbstractLookAndFeel.getTheme().getButtonForegroundColor();
            }
            if (!tabPane.isEnabled() || !tabPane.isEnabledAt(tabIndex)) {
                fc = AbstractLookAndFeel.getTheme().getDisabledForegroundColor();
            }
            if (ColorHelper.getGrayValue(fc) > 128) {
                g2D.setColor(Color.black);
            } else {
                g2D.setColor(Color.white);
            }
            JTattooUtilities.drawStringUnderlineCharAt(tabPane, g, title, mnemIndex, textRect.x + 1, textRect.y + 1 + metrics.getAscent());
            g2D.setComposite(composite);
            g2D.setColor(fc);
            JTattooUtilities.drawStringUnderlineCharAt(tabPane, g, title, mnemIndex, textRect.x, textRect.y + metrics.getAscent());
        }
    }

    protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {
        HiFiUtils.fillComponent(g, tabPane);
        super.paintContentBorder(g, tabPlacement, selectedIndex, x, y, w, h);
    }

    protected void paintRoundedTopTabBorder(int tabIndex, Graphics g, int x1, int y1, int x2, int y2, boolean isSelected) {
        super.paintRoundedTopTabBorder(tabIndex, g, x1, y1, x2, y2, isSelected);
        g.setColor(tabAreaBackground);
        g.drawLine(x1 + 1, y1 + 1, x1 + 1, y1 + 1);
        g.drawLine(x2 - 1, y1 + 1, x2 - 1, y1 + 1);
    }
}