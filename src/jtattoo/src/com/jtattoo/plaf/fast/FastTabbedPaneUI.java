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
 
package com.jtattoo.plaf.fast;

import com.jtattoo.plaf.*;
import java.awt.*;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;

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
        contentBorderInsets = new Insets(0, 0, 0, 0);
    }

    protected boolean hasInnerBorder() {
        return true;
    }

    protected Color[] getTabColors(int tabIndex, boolean isSelected, boolean isRollover) {
        Color colorArr[] = AbstractLookAndFeel.getTheme().getTabColors();
        if ((tabIndex >= 0) && (tabIndex < tabPane.getTabCount())) {
            Color backColor = tabPane.getBackgroundAt(tabIndex);
            if ((backColor instanceof UIResource)) {
                if (isSelected) {
                    colorArr = AbstractLookAndFeel.getTheme().getSelectedColors();
                } else {
                    if (JTattooUtilities.isFrameActive(tabPane)) {
                        colorArr = AbstractLookAndFeel.getTheme().getTabColors();
                    } else {
                        colorArr = AbstractLookAndFeel.getTheme().getInActiveColors();
                    }
                }
            } else {
                colorArr = ColorHelper.createColorArr(backColor, backColor, 2);
            }
        }
        return colorArr;
    }
    
    protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
        if (isTabOpaque() || isSelected) {
            Color colorArr[] = getTabColors(tabIndex, isSelected, false);
            g.setColor(colorArr[0]);
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

}