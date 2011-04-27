/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.fast;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;

import com.jtattoo.plaf.*;

/**
 * author Michael Hagen
 */
public class FastTabbedPaneUI extends BaseTabbedPaneUI {

    public static ComponentUI createUI(JComponent c) {
        return new FastTabbedPaneUI();
    }

    public void installDefaults() {
        super.installDefaults();
        roundedTabs = false;
        tabAreaInsets = new Insets(2, 6, 2, 6);
        contentBorderInsets = new Insets(1, 1, 0, 0);
    }

    protected Color getGapColor(int tabIndex) {
        if (tabIndex == tabPane.getSelectedIndex() && (tabPane.getBackgroundAt(tabIndex) instanceof UIResource)) {
             return AbstractLookAndFeel.getTheme().getBackgroundColor();
        } else {
            if ((tabIndex >= 0) && (tabIndex < tabPane.getTabCount())) {
                return tabPane.getBackgroundAt(tabIndex);
            }
        }
        return tabAreaBackground;
    }

    protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {
        g.setColor(tabAreaBackground);
        int tabAreaHeight = calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight);
        int tabAreaWidth = calculateTabAreaWidth(tabPlacement, runCount, maxTabWidth);
        if (tabPlacement == JTabbedPane.TOP || tabPlacement == JTabbedPane.LEFT) {
            g.fillRect(x, y, tabAreaWidth, tabAreaHeight);
        } else if (tabPlacement == JTabbedPane.BOTTOM) {
            g.fillRect(x, h - tabAreaHeight + 1, w, tabAreaHeight);
        } else {
            g.fillRect(w - tabAreaWidth + 1, y, tabAreaWidth, h);
        }
        Color loColor = AbstractLookAndFeel.getControlDarkShadow();
        Color hiColor = AbstractLookAndFeel.getControlHighlight();
        g.setColor(loColor);
        switch (tabPlacement) {
            case TOP: {
                g.drawRect(x, y + tabAreaHeight - 1, x + w - 1, h - tabAreaHeight);
                g.setColor(hiColor);
                g.drawLine(x + 1, y + tabAreaHeight, w - 2, y + tabAreaHeight);
                g.drawLine(x + 1, y + tabAreaHeight, x + 1, h - 2);
                break;
            }
            case LEFT: {
                g.drawRect(x + tabAreaWidth - 1, y, w - tabAreaWidth, y + h - 1);
                g.setColor(hiColor);
                g.drawLine(x + tabAreaWidth, y + 1, x + tabAreaWidth, h - 2);
                g.drawLine(x + tabAreaWidth, y + 1, w - 2, y + 1);
                break;
            }
            case BOTTOM: {
                g.drawRect(x, y, x + w - 1, h - tabAreaHeight);
                g.setColor(hiColor);
                g.drawLine(x + 1, y + 1, w - 2, y + 1);
                g.drawLine(x + 1, y + 1, x + 1, h - tabAreaHeight - 1);
                break;
            }
            case RIGHT: {
                g.drawRect(x, y, w - tabAreaWidth, y + h - 1);
                g.setColor(hiColor);
                g.drawLine(x + 1, y + 1, x + 1, h - 2);
                g.drawLine(x + 1, y + 1, w - tabAreaWidth - 1, y + 1);
                break;
            }
        }
    }

    protected void paintScrollContentBorder(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {
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
        Insets bi = tabPane.getBorder().getBorderInsets(tabPane);
        Color loColor = AbstractLookAndFeel.getControlDarkShadow();
        Color hiColor = AbstractLookAndFeel.getControlHighlight();
        g.setColor(loColor);
        switch (tabPlacement) {
            case TOP: {
                g.drawLine(x, y + tabAreaHeight - 1 - bi.top, w, y + tabAreaHeight - 1 - bi.top);
                g.setColor(hiColor);
                g.drawLine(x, y + tabAreaHeight - bi.top, w - 1, y + tabAreaHeight - bi.top);
                break;
            }
            case LEFT: {
                g.drawLine(x + tabAreaWidth - 1 - bi.left, y, x + tabAreaWidth - 1 - bi.left, h);
                g.setColor(hiColor);
                g.drawLine(x + tabAreaWidth - bi.left, y, x + tabAreaWidth - bi.left, h);
                break;
            }
            case BOTTOM: {
                g.drawLine(x, h - tabAreaHeight + bi.bottom, w, h - tabAreaHeight + bi.bottom);
                break;
            }
            case RIGHT: {
                g.drawLine(w - tabAreaWidth + bi.right, y, w - tabAreaWidth + bi.right, h);
                break;
            }
        }
    }

    protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
        if (isSelected && (tabPane.getBackgroundAt(tabIndex) instanceof UIResource)) {
            g.setColor(AbstractLookAndFeel.getTheme().getBackgroundColor());
        } else {
            g.setColor(tabPane.getBackgroundAt(tabIndex));
        }
        switch (tabPlacement) {
            case TOP:
                if (isSelected) {
                    g.fillRect(x + 1, y + 1, w - 1, h + 2);
                } else {
                    g.fillRect(x + 1, y + 1, w - 1, h - 1);
                }
                break;
            case LEFT:
                if (isSelected) {
                    g.fillRect(x + 1, y + 1, w + 2, h - 1);
                } else {
                    g.fillRect(x + 1, y + 1, w - 1, h - 1);
                }
                break;
            case BOTTOM:
                if (isSelected) {
                    g.fillRect(x + 1, y - 2, w - 1, h + 1);
                } else {
                    g.fillRect(x + 1, y, w - 1, h - 1);
                }
                break;
            case RIGHT:
                if (isSelected) {
                    g.fillRect(x - 2, y + 1, w + 2, h - 1);
                } else {
                    g.fillRect(x, y + 1, w, h - 1);
                }
                break;
        }
    }

}