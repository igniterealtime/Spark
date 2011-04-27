/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.luna;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;

import com.jtattoo.plaf.*;

/**
 * author Michael Hagen
 */
public class LunaTabbedPaneUI extends BaseTabbedPaneUI {

    private static Color[] selectedTabColors = new Color[]{AbstractLookAndFeel.getBackgroundColor()};
    private static Color sepColors[] = {AbstractLookAndFeel.getControlDarkShadow()};

    public static ComponentUI createUI(JComponent c) {
        return new LunaTabbedPaneUI();
    }

    public void installDefaults() {
        super.installDefaults();
        selectedTabColors = new Color[]{AbstractLookAndFeel.getBackgroundColor()};
        tabAreaInsets = new Insets(2, 6, 2, 6);
        contentBorderInsets = new Insets(1, 1, 0, 0);
    }

    protected void installComponents() {
        simpleButtonBorder = true;
        super.installComponents();
    }

    protected Font getTabFont(boolean isSelected) {
        if (isSelected) {
            return super.getTabFont(isSelected).deriveFont(Font.BOLD);
        } else {
            return super.getTabFont(isSelected);
        }
    }

    protected Color getGapColor(int tabIndex) {
        if (tabIndex == tabPane.getSelectedIndex() && (tabPane.getBackgroundAt(tabIndex) instanceof UIResource)) {
             return AbstractLookAndFeel.getTheme().getBackgroundColor();
        }
        return super.getGapColor(tabIndex);
    }

    protected Color[] getTabColors(int tabIndex, boolean isSelected) {
        if (isSelected && (tabPane.getBackgroundAt(tabIndex) instanceof UIResource)) {
            return selectedTabColors;
        } else {
            return super.getTabColors(tabIndex, isSelected);
        }
    }

    protected Color[] getContentBorderColors(int tabPlacement) {
        return sepColors;
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
                if (tabPane.getBorder() == null) {
                    g.drawLine(x + 1, y + tabAreaHeight - 2, w - 2, y + tabAreaHeight - 2);
                } else {
                    g.drawRect(x, y + tabAreaHeight - 1, x + w - 1, h - tabAreaHeight);
                    g.setColor(hiColor);
                    g.drawLine(x + 1, y + tabAreaHeight, w - 2, y + tabAreaHeight);
                    g.drawLine(x + 1, y + tabAreaHeight, x + 1, h - 2);
                }
                break;
            }
            case LEFT: {
                if (tabPane.getBorder() == null) {
                    g.drawLine(x + tabAreaWidth - 2, y + 1, x + tabAreaWidth - 2, y + h - 2);
                } else {
                    g.drawRect(x + tabAreaWidth - 1, y, w - tabAreaWidth, y + h - 1);
                    g.setColor(hiColor);
                    g.drawLine(x + tabAreaWidth, y + 1, x + tabAreaWidth, h - 2);
                    g.drawLine(x + tabAreaWidth, y + 1, w - 2, y + 1);
                }
                break;
            }
            case BOTTOM: {
                if (tabPane.getBorder() == null) {
                    g.drawLine(x + 1, y + h - tabAreaHeight + 1, w - 2, y + h - tabAreaHeight + 1);
                } else {
                    g.drawRect(x, y, x + w - 1, h - tabAreaHeight);
                    g.setColor(hiColor);
                    g.drawLine(x + 1, y + 1, w - 2, y + 1);
                    g.drawLine(x + 1, y + 1, x + 1, h - tabAreaHeight - 1);
                }
                break;
            }
            case RIGHT: {
                if (tabPane.getBorder() == null) {
                    g.drawLine(x + w - tabAreaWidth + 2, y + 1, x + w - tabAreaWidth + 2, y + h - 2);
                } else {
                    g.drawRect(x, y, w - tabAreaWidth, y + h - 1);
                    g.setColor(hiColor);
                    g.drawLine(x + 1, y + 1, x + 1, h - 2);
                    g.drawLine(x + 1, y + 1, w - tabAreaWidth - 1, y + 1);
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
        Color loColor = AbstractLookAndFeel.getControlDarkShadow();
        Color hiColor = AbstractLookAndFeel.getControlHighlight();
        g.setColor(loColor);
        switch (tabPlacement) {
            case TOP: {
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
                if (tabPane.getBorder() == null) {
                    g.drawLine(x + 1, y + h - tabAreaHeight + 1, w - 2, y + h - tabAreaHeight + 1);
                } else {
                    g.drawLine(x, h - tabAreaHeight + bi.bottom, w, h - tabAreaHeight + bi.bottom);
                }
                break;
            }
            case RIGHT: {
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
        if (isSelected) {
            if (tabPane.getBackgroundAt(tabIndex) instanceof UIResource) {
                g.setColor(AbstractLookAndFeel.getBackgroundColor());
            } else {
                g.setColor(tabPane.getBackgroundAt(tabIndex));
            }
            if (tabPlacement == TOP) {
                g.fillRect(x + 1, y + 1, w - 1, h + 2);
            } else if (tabPlacement == LEFT) {
                g.fillRect(x + 1, y + 1, w + 2, h - 1);
            } else if (tabPlacement == BOTTOM) {
                g.fillRect(x + 1, y - 2, w - 1, h + 2);
            } else {
                g.fillRect(x - 2, y + 1, w + 2, h - 1);
            }
        } else {
            super.paintTabBackground(g, tabPlacement, tabIndex, x, y, w, h, isSelected);
            switch (tabPlacement) {
                case TOP: {
                    if (tabIndex == rolloverIndex) {
                        g.setColor(AbstractLookAndFeel.getFocusColor());
                        g.fillRect(x + 2, y + 1, w - 3, 2);
                    }
                    break;
                }
                case LEFT: {
                    if (tabIndex == rolloverIndex) {
                        g.setColor(AbstractLookAndFeel.getFocusColor());
                        g.fillRect(x, y + 2, w - 1, 2);
                    }
                    break;
                }
                case RIGHT: {
                    if (tabIndex == rolloverIndex) {
                        g.setColor(AbstractLookAndFeel.getFocusColor());
                        g.fillRect(x, y + 2, w - 1, 2);
                    }
                    break;
                }
                case BOTTOM: {
                    if (tabIndex == rolloverIndex) {
                        g.setColor(AbstractLookAndFeel.getFocusColor());
                        g.fillRect(x + 2, y + h - 3, w - 3, 2);
                    }
                    break;
                }
            }
        }
    }

}