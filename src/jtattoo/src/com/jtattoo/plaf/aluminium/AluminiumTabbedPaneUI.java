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
 
package com.jtattoo.plaf.aluminium;

import com.jtattoo.plaf.*;
import java.awt.*;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;

/**
 * @author Michael Hagen
 */
public class AluminiumTabbedPaneUI extends BaseTabbedPaneUI {

    private static Color TOP_SELECTED_TAB_COLORS[] = null;
    private static Color BOTTOM_SELECTED_TAB_COLORS[] = null;
    
    public static ComponentUI createUI(JComponent c) {
        return new AluminiumTabbedPaneUI();
    }

    public void installDefaults() {
        super.installDefaults();
        tabAreaInsets = new Insets(2, 6, 2, 6);
        contentBorderInsets = new Insets(0, 0, 0, 0);
        Color c = AbstractLookAndFeel.getTheme().getBackgroundColor();
        Color cHi = ColorHelper.brighter(c, 20);
        Color cLo = ColorHelper.darker(c, 10);
        TOP_SELECTED_TAB_COLORS = ColorHelper.createColorArr(cHi, c, 20);
        BOTTOM_SELECTED_TAB_COLORS = ColorHelper.createColorArr(c, cLo, 20);
    }

    protected Font getTabFont(boolean isSelected) {
        if (isSelected) {
            return super.getTabFont(isSelected).deriveFont(Font.BOLD);
        } else {
            return super.getTabFont(isSelected);
        }
    }

    protected Color[] getTabColors(int tabIndex, boolean isSelected, boolean isRollover) {
        Color backColor = tabPane.getBackgroundAt(tabIndex);
        if ((backColor instanceof UIResource) && isSelected) {
            if (tabPane.getTabPlacement() == BOTTOM) {
                return BOTTOM_SELECTED_TAB_COLORS;
            } else {
                return TOP_SELECTED_TAB_COLORS;
            }
        }
        return super.getTabColors(tabIndex, isSelected, isRollover);
    }

    protected boolean hasInnerBorder() {
        return true;
    }

    protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
        Color backColor = tabPane.getBackgroundAt(tabIndex);
        if (!(backColor instanceof UIResource)) {
            super.paintTabBackground(g, tabPlacement, tabIndex, x, y, w, h, isSelected);
            return;
        }
        if (JTattooUtilities.isMac() || !AbstractLookAndFeel.getTheme().isBackgroundPatternOn()) {
            if (isSelected) {
                Color colorArr[] = getTabColors(tabIndex, isSelected, tabIndex == rolloverIndex);
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
                    g.setColor(AbstractLookAndFeel.getBackgroundColor());
                    if (tabPlacement == TOP) {
                        AluminiumUtils.fillComponent(g, tabPane, x + 1, y + 1, w - 1, h + 1);
                    } else if (tabPlacement == LEFT) {
                        AluminiumUtils.fillComponent(g, tabPane, x + 1, y + 1, w + 1, h - 1);
                    } else if (tabPlacement == BOTTOM) {
                        AluminiumUtils.fillComponent(g, tabPane, x + 1, y - 2, w - 1, h + 1);
                    } else {
                        AluminiumUtils.fillComponent(g, tabPane, x - 1, y + 1, w + 1, h - 1);
                    }
                } else {
                    super.paintTabBackground(g, tabPlacement, tabIndex, x, y, w, h, isSelected);
                }
            } else {
                super.paintTabBackground(g, tabPlacement, tabIndex, x, y, w, h, isSelected);
            }
        }
    }

}