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
 
package com.jtattoo.plaf.smart;

import com.jtattoo.plaf.AbstractLookAndFeel;
import com.jtattoo.plaf.BaseTabbedPaneUI;
import java.awt.*;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

/**
 * author Michael Hagen
 */
public class SmartTabbedPaneUI extends BaseTabbedPaneUI {

    public static ComponentUI createUI(JComponent c) {
        return new SmartTabbedPaneUI();
    }

    public void installDefaults() {
        super.installDefaults();
        tabAreaInsets = new Insets(2, 6, 2, 6);
        contentBorderInsets = new Insets(0, 0, 0, 0);
    }

    protected Color getGapColor(int tabIndex) {
        if (tabIndex == tabPane.getSelectedIndex()) {
            return tabPane.getBackgroundAt(tabIndex);
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

    protected boolean hasInnerBorder() {
        return true;
    }

    protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
        if (isSelected) {
            g.setColor(tabPane.getBackgroundAt(tabIndex));
            if (tabPlacement == TOP) {
                g.fillRect(x + 1, y + 1, w - 1, h + 2);
            } else if (tabPlacement == LEFT) {
                g.fillRect(x + 1, y + 1, w + 2, h - 1);
            } else if (tabPlacement == BOTTOM) {
                g.fillRect(x + 1, y - 2, w - 1, h + 1);
            } else {
                g.fillRect(x - 2, y + 1, w + 2, h - 1);
            }
        } else {
            super.paintTabBackground(g, tabPlacement, tabIndex, x, y, w, h, isSelected);
            if (!isSelected && tabIndex == rolloverIndex && tabPane.isEnabledAt(tabIndex)) {
                g.setColor(AbstractLookAndFeel.getFocusColor());
                if (tabPlacement == TOP) {
                    g.fillRect(x + 2, y + 1, w - 3, 2);
                } else if (tabPlacement == LEFT) {
                    g.fillRect(x, y + 1, w - 1, 2);
                } else if (tabPlacement == BOTTOM) {
                    g.fillRect(x + 2, y + h - 3, w - 3, 2);
                } else {
                    g.fillRect(x, y + 1, w - 1, 2);
                }
            }
        }
    }

}