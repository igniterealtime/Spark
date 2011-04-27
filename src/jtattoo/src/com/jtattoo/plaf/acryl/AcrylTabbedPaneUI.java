/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.acryl;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.text.*;

import com.jtattoo.plaf.*;

/**
 * author Michael Hagen
 */
public class AcrylTabbedPaneUI extends BaseTabbedPaneUI {

    public static ComponentUI createUI(JComponent c) {
        return new AcrylTabbedPaneUI();
    }

    public void installDefaults() {
        super.installDefaults();
        tabAreaInsets.bottom = 5;
    }

    protected Color[] getTabColors(int tabIndex, boolean isSelected) {
        if ((tabIndex >= 0) && (tabIndex < tabPane.getTabCount())) {
            boolean isEnabled = tabPane.isEnabledAt(tabIndex);
            Color backColor = tabPane.getBackgroundAt(tabIndex);
            Color colorArr[] = AbstractLookAndFeel.getTheme().getTabColors();
            if ((backColor instanceof UIResource)) {
                if (isSelected) {
                    colorArr = AbstractLookAndFeel.getTheme().getDefaultColors();
                } else if (tabIndex == rolloverIndex && isEnabled) {
                    colorArr = AbstractLookAndFeel.getTheme().getRolloverColors();
                } else {
                    colorArr = AbstractLookAndFeel.getTheme().getTabColors();
                }
            } else {
                if (isSelected) {
                    colorArr = AbstractLookAndFeel.getTheme().getDefaultColors();
                } else if (tabIndex == rolloverIndex && isEnabled) {
                    colorArr = AbstractLookAndFeel.getTheme().getRolloverColors();
                } else {
                    colorArr = ColorHelper.createColorArr(ColorHelper.brighter(backColor, 40), ColorHelper.darker(backColor, 10), 20);
                }
            }
            return colorArr;
        }
        return AbstractLookAndFeel.getTheme().getTabColors();
    }

    protected Color[] getContentBorderColors(int tabPlacement) {
        Color SEP_COLORS[] = {
            ColorHelper.brighter(AcrylLookAndFeel.getControlColorLight(), 20),
            AcrylLookAndFeel.getControlColorLight(),
            ColorHelper.brighter(AcrylLookAndFeel.getControlColorDark(), 20),
            AcrylLookAndFeel.getControlColorDark(),
            ColorHelper.darker(AcrylLookAndFeel.getControlColorDark(), 20)};
        return SEP_COLORS;
    }

    protected Color getContentBorderColor() {
        return ColorHelper.brighter(AbstractLookAndFeel.getTheme().getFrameColor(), 50);
    }

    protected Color getSelectedBorderColor(int tabIndex) {
        return AbstractLookAndFeel.getFrameColor();
    }

    protected Color getLoBorderColor(int tabIndex) {
        return ColorHelper.brighter(AbstractLookAndFeel.getFrameColor(), 50);
    }

    protected Color getHiBorderColor(int tabIndex) {
        if (tabIndex == tabPane.getSelectedIndex()) {
            return ColorHelper.darker(super.getHiBorderColor(tabIndex), 50);
        } else {
            return super.getHiBorderColor(tabIndex);
        }
    }

    protected Color getGapColor(int tabIndex) {
        if (tabIndex == tabPane.getSelectedIndex()) {
            Color colors[] = AbstractLookAndFeel.getTheme().getDefaultColors();
            return colors[colors.length - 1];
        }
        return super.getGapColor(tabIndex);
    }

    protected Font getTabFont(boolean isSelected) {
        if (isSelected) {
            return super.getTabFont(isSelected).deriveFont(Font.BOLD);
        } else {
            return super.getTabFont(isSelected);
        }
    }

    protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {
        g.setColor(AbstractLookAndFeel.getTabAreaBackgroundColor());
        int tabAreaHeight = calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight);
        int tabAreaWidth = calculateTabAreaWidth(tabPlacement, runCount, maxTabWidth);
        if (tabPlacement == JTabbedPane.TOP || tabPlacement == JTabbedPane.LEFT) {
            g.fillRect(x, y, tabAreaWidth, tabAreaHeight);
        } else if (tabPlacement == JTabbedPane.BOTTOM) {
            g.fillRect(x, h - tabAreaHeight + 1, w, tabAreaHeight);
        } else {
            g.fillRect(w - tabAreaWidth + 1, y, tabAreaWidth, h);
        }
        super.paintContentBorder(g, tabPlacement, selectedIndex, x, y, w, h);
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

            if (tabPane.isEnabled() && tabPane.isEnabledAt(tabIndex)) {
                if (isSelected) {
                    Color shadowColor = ColorHelper.darker(AcrylLookAndFeel.getWindowTitleColorDark(), 30);
                    g.setColor(shadowColor);
                    JTattooUtilities.drawStringUnderlineCharAt(tabPane, g, title, mnemIndex, textRect.x - 1, textRect.y - 1 + metrics.getAscent());
                    JTattooUtilities.drawStringUnderlineCharAt(tabPane, g, title, mnemIndex, textRect.x - 1, textRect.y + 1 + metrics.getAscent());
                    JTattooUtilities.drawStringUnderlineCharAt(tabPane, g, title, mnemIndex, textRect.x + 1, textRect.y - 1 + metrics.getAscent());
                    JTattooUtilities.drawStringUnderlineCharAt(tabPane, g, title, mnemIndex, textRect.x + 1, textRect.y + 1 + metrics.getAscent());
                    g.setColor(AbstractLookAndFeel.getTheme().getWindowTitleForegroundColor());
                } else {
                    g.setColor(tabPane.getForegroundAt(tabIndex));
                }
                JTattooUtilities.drawStringUnderlineCharAt(tabPane, g, title, mnemIndex, textRect.x, textRect.y + metrics.getAscent());

            } else { // tab disabled
                g.setColor(tabPane.getBackgroundAt(tabIndex).brighter());
                JTattooUtilities.drawStringUnderlineCharAt(tabPane, g, title, mnemIndex, textRect.x, textRect.y + metrics.getAscent());
                g.setColor(tabPane.getBackgroundAt(tabIndex).darker());
                JTattooUtilities.drawStringUnderlineCharAt(tabPane, g, title, mnemIndex, textRect.x - 1, textRect.y + metrics.getAscent() - 1);
            }
        }
    }
}