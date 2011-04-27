/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */

package com.jtattoo.plaf.custom.pulsar;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.text.*;

import com.jtattoo.plaf.*;
import javax.swing.plaf.basic.BasicGraphicsUtils;

/**
 * @author Michael Hagen
 */
public class PulsarTabbedPaneUI extends BaseTabbedPaneUI {
    private static Color TAB_COLORS[] = { new Color(232, 232, 232) };
    private static Color SELECTED_TAB_COLORS[] = { new Color(0, 128, 255) };
    private static Color FRAME_COLOR = new Color(128, 128, 128);
    private static Color SELECTED_FRAME_COLOR = new Color(96, 112, 128);
    private static Color HI_FRAME_COLOR = new Color(80, 208, 255);
    private static Color CONTENT_FRAME_COLOR = new Color(118, 146, 185);

    public static ComponentUI createUI(JComponent c) { 
        return new PulsarTabbedPaneUI(); 
    }
    
    public void installDefaults() {
        super.installDefaults();
        tabAreaInsets = new Insets(2, 6, 2, 6);
        contentBorderInsets = new Insets(0, 0, 0, 0);
        tabPane.setBorder(BorderFactory.createLineBorder(AbstractLookAndFeel.getBackgroundColor()));
    }

    protected void installComponents() {
        simpleButtonBorder = true;
        super.installComponents();
    }

    protected Color getSelectedBorderColor(int tabIndex) {
        return SELECTED_FRAME_COLOR;
    }

    protected Color getLoBorderColor(int tabIndex) {
        return FRAME_COLOR;
    }

    protected Color getHiBorderColor(int tabIndex) {
        if (tabIndex == tabPane.getSelectedIndex()) {
            return HI_FRAME_COLOR;
        } else {
            return super.getHiBorderColor(tabIndex);
        }
    }

    protected Color[] getTabColors(int tabIndex, boolean isSelected) {
        boolean isEnabled = tabPane.isEnabledAt(tabIndex);
        if (isSelected) {
            return SELECTED_TAB_COLORS;
        } else if (tabIndex == rolloverIndex && isEnabled) {
            return AbstractLookAndFeel.getTheme().getRolloverColors();
        } else {
            if (tabPane.getBackgroundAt(tabIndex) instanceof ColorUIResource) {
                return TAB_COLORS;
            } else {
                return new Color[] {tabPane.getBackgroundAt(tabIndex)};
            }

        }
    }

    protected Font getTabFont(boolean isSelected) {
        if (isSelected)
            return super.getTabFont(isSelected).deriveFont(Font.BOLD);
        else
            return super.getTabFont(isSelected);
    }
    
    protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {
        g.setColor(tabAreaBackground);
        g.fillRect(0, 0, tabPane.getWidth(), tabPane.getHeight());
        g.setColor(CONTENT_FRAME_COLOR);
        switch (tabPlacement) {
            case TOP: {
                int tabAreaHeight = calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight);
                g.drawLine(x, y + tabAreaHeight - 1, w, y + tabAreaHeight - 1);
                break;
            }
            case LEFT: {
                int tabAreaWidth = calculateTabAreaWidth(tabPlacement, runCount, maxTabWidth);
                g.drawLine(x + tabAreaWidth, y, x + tabAreaWidth, h);
                break;
            }
            case BOTTOM: {
                int tabAreaHeight = calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight);
                g.drawLine(x, y + h - tabAreaHeight, w, y + h - tabAreaHeight);
                break;
            }
            case RIGHT: {
                int tabAreaWidth = calculateTabAreaWidth(tabPlacement, runCount, maxTabWidth);
                g.drawLine(x + w - tabAreaWidth, y, x + w - tabAreaWidth, h);
                break;
            }
        }
    }

    protected void paintText(Graphics g, int tabPlacement, Font font, FontMetrics metrics, int tabIndex, String title, Rectangle textRect, boolean isSelected) {
        g.setFont(font);
        View v = getTextViewForTab(tabIndex);
        if (v != null) {
            // html
            Graphics2D g2D = (Graphics2D)g;
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
            if (JTattooUtilities.getJavaVersion() >= 1.4) 
                mnemIndex = tabPane.getDisplayedMnemonicIndexAt(tabIndex);

            if (tabPane.isEnabled() && tabPane.isEnabledAt(tabIndex)) {
                if (isSelected) {
                    Color titleColor = AbstractLookAndFeel.getWindowTitleForegroundColor();
                    g.setColor(titleColor);
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

    protected void paintFocusIndicator(Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex, Rectangle iconRect, Rectangle textRect, boolean isSelected) {
        if (tabPane.hasFocus() && isSelected) {
            g.setColor(AbstractLookAndFeel.getTheme().getFocusColor());
            BasicGraphicsUtils.drawDashedRect(g, textRect.x - 4, textRect.y + textRect.height - 2, textRect.width + 8, 2);
        }
    }
    
}