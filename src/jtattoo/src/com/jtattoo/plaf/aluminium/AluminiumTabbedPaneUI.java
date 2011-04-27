/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.aluminium;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;

import com.jtattoo.plaf.*;

/**
 * @author Michael Hagen
 */
public class AluminiumTabbedPaneUI extends BaseTabbedPaneUI {

    private static final Color TOP_SELECTED_TAB_COLORS[] = ColorHelper.createColorArr(new Color(204, 206, 202), new Color(220, 222, 218), 20);
    private static final Color BOTTOM_SELECTED_TAB_COLORS[] = ColorHelper.createColorArr(new Color(220, 222, 218), new Color(204, 206, 202), 20);

    public static ComponentUI createUI(JComponent c) {
        return new AluminiumTabbedPaneUI();
    }

    public void installDefaults() {
        super.installDefaults();
        tabAreaInsets = new Insets(2, 6, 2, 6);
        contentBorderInsets = new Insets(1, 1, 0, 0);
    }

    protected Font getTabFont(boolean isSelected) {
        if (isSelected) {
            return super.getTabFont(isSelected).deriveFont(Font.BOLD);
        } else {
            return super.getTabFont(isSelected);
        }
    }

    protected Color[] getTabColors(int tabIndex, boolean isSelected) {
        if (isSelected) {
            if (tabPane.getTabPlacement() == BOTTOM) {
                return BOTTOM_SELECTED_TAB_COLORS;
            } else {
                return TOP_SELECTED_TAB_COLORS;
            }
        } else {
            return super.getTabColors(tabIndex, isSelected);
        }
    }

    protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {
        Color loColor = AbstractLookAndFeel.getControlDarkShadow();
        Color hiColor = AbstractLookAndFeel.getControlHighlight();
        g.setColor(loColor);
        switch (tabPlacement) {
            case TOP: {
                int tabAreaHeight = calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight);
                if (tabPane.getBorder() == null) {
                    g.drawLine(x + 1, y + tabAreaHeight - 2, w - 2, y + tabAreaHeight - 2);
                } else {
                    g.drawRect(x, y + tabAreaHeight - 1, w - 1, h - tabAreaHeight);
                    g.setColor(hiColor);
                    g.drawLine(x + 1, y + tabAreaHeight, x + w - 2, y + tabAreaHeight);
                    g.drawLine(x + 1, y + tabAreaHeight, x + 1, y + h - 2);
                }
                break;
            }
            case LEFT: {
                int tabAreaWidth = calculateTabAreaWidth(tabPlacement, runCount, maxTabWidth);
                if (tabPane.getBorder() == null) {
                    g.drawLine(x + tabAreaWidth - 2, y + 1, x + tabAreaWidth - 2, y + h - 2);
                } else {
                    g.drawRect(x + tabAreaWidth - 1, y, w - tabAreaWidth, h - 1);
                    g.setColor(hiColor);
                    g.drawLine(x + tabAreaWidth, y + 1, x + tabAreaWidth, y + h - 2);
                    g.drawLine(x + tabAreaWidth, y + 1, x + w - 2, y + 1);
                }
                break;
            }
            case BOTTOM: {
                int tabAreaHeight = calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight);
                if (tabPane.getBorder() == null) {
                    g.drawLine(x + 1, y + h - tabAreaHeight + 1, w - 2, y + h - tabAreaHeight + 1);
                } else {
                    g.drawRect(x, y, w - 1, h - tabAreaHeight);
                    g.setColor(hiColor);
                    g.drawLine(x + 1, y + 1, x + w - 2, y + 1);
                    g.drawLine(x + 1, y + 1, x + 1, y + h - tabAreaHeight - 1);
                }
                break;
            }
            case RIGHT: {
                int tabAreaWidth = calculateTabAreaWidth(tabPlacement, runCount, maxTabWidth);
                if (tabPane.getBorder() == null) {
                    g.drawLine(x + w - tabAreaWidth + 2, y + 1, x + w - tabAreaWidth + 2, y + h - 2);
                } else {
                    g.drawRect(x, y, w - tabAreaWidth, h - 1);
                    g.setColor(hiColor);
                    g.drawLine(x + 1, y + 1, x + 1, y + h - 2);
                    g.drawLine(x + 1, y + 1, x + w - tabAreaWidth - 1, y + 1);
                }
                break;
            }
        }
    }

    protected void paintScrollContentBorder(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {
        Insets bi = new Insets(0, 0, 0, 0);
        if (tabPane.getBorder() != null) {
            bi = tabPane.getBorder().getBorderInsets(tabPane);
        }
        Color loColor = AbstractLookAndFeel.getControlDarkShadow();
        Color hiColor = AbstractLookAndFeel.getControlHighlight();
        g.setColor(loColor);
        switch (tabPlacement) {
            case TOP: {
                int tabAreaHeight = calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight);
                if (tabPane.getBorder() == null) {
                    g.drawLine(x, y + tabAreaHeight - 2, w, y + tabAreaHeight - 2);
                } else {
                    g.drawLine(x, y + tabAreaHeight - 1 - bi.top, w, y + tabAreaHeight - 1 - bi.top);
                    g.setColor(hiColor);
                    g.drawLine(x, y + tabAreaHeight - bi.top, w - 1, y + tabAreaHeight - bi.top);
                }
                break;
            }
            case LEFT: {
                int tabAreaWidth = calculateTabAreaWidth(tabPlacement, runCount, maxTabWidth);
                if (tabPane.getBorder() == null) {
                    g.drawLine(x + tabAreaWidth - 2, y + 1, x + tabAreaWidth - 2, y + h - 2);
                } else {
                    g.drawLine(x + tabAreaWidth - 1 - bi.left, y, x + tabAreaWidth - 1 - bi.left, h);
                    g.setColor(hiColor);
                    g.drawLine(x + tabAreaWidth - bi.left, y, x + tabAreaWidth - bi.left, h);
                }
                break;
            }
            case BOTTOM: {
                int tabAreaHeight = calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight);
                if (tabPane.getBorder() == null) {
                    g.drawLine(x + 1, y + h - tabAreaHeight + 1, w - 2, y + h - tabAreaHeight + 1);
                } else {
                    g.drawLine(x, h - tabAreaHeight + bi.bottom, w, h - tabAreaHeight + bi.bottom);
                }
                break;
            }
            case RIGHT: {
                int tabAreaWidth = calculateTabAreaWidth(tabPlacement, runCount, maxTabWidth);
                if (tabPane.getBorder() == null) {
                    g.drawLine(x + w - tabAreaWidth + 2, y + 1, x + w - tabAreaWidth + 2, y + h - 2);
                } else {
                    g.drawLine(w - tabAreaWidth + bi.right, y, w - tabAreaWidth + bi.right, h);
                }
                break;
            }
        }
    }

    protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
        if (JTattooUtilities.isMac()) {
            if (isSelected) {
                Color colorArr[] = getTabColors(tabIndex, isSelected);
                switch (tabPlacement) {
                    case LEFT:
                        JTattooUtilities.fillHorGradient(g, colorArr, x + 1, y + 1, w + 1, h - 1);
                        break;
                    case RIGHT:
                        JTattooUtilities.fillHorGradient(g, colorArr, x - 1, y + 1, w + 1, h - 1);
                        break;
                    case BOTTOM:
                        JTattooUtilities.fillHorGradient(g, colorArr, x + 1, y - 1, w - 1, h);
                        break;
                    case TOP:
                    default:
                        JTattooUtilities.fillHorGradient(g, colorArr, x + 1, y + 1, w - 1, h + 1);
                        break;
                }
            } else {
                super.paintTabBackground(g, tabPlacement, tabIndex, x, y, w, h, isSelected);
            }
        } else {
            if (isSelected) {
                if (tabPane.getBackgroundAt(tabIndex) instanceof UIResource) {
                    g.setColor(AluminiumLookAndFeel.getBackgroundColor());
                    if (tabPlacement == TOP)
                        AluminiumUtils.fillComponent(g, tabPane, x + 1, y + 1, w - 1, h + 1);
                    else if (tabPlacement == LEFT)
                        AluminiumUtils.fillComponent(g, tabPane, x + 1, y + 1, w + 1, h - 1);
                    else if (tabPlacement == BOTTOM)
                        AluminiumUtils.fillComponent(g, tabPane, x + 1, y - 1, w - 1, h + 1);
                    else
                        AluminiumUtils.fillComponent(g, tabPane, x - 1, y + 1, w + 1, h - 1);
                }
                else
                    super.paintTabBackground(g, tabPlacement, tabIndex, x, y, w, h, isSelected);
            }
            else {
                super.paintTabBackground(g, tabPlacement, tabIndex, x, y, w, h, isSelected);
            }
        }
    }

}