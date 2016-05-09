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

import com.jtattoo.plaf.AbstractBorderFactory;
import javax.swing.border.Border;

/**
 * @author Michael Hagen
 */
public class FastBorderFactory implements AbstractBorderFactory {

    private static FastBorderFactory instance = null;

    private FastBorderFactory() {
    }

    public static synchronized FastBorderFactory getInstance() {
        if (instance == null) {
            instance = new FastBorderFactory();
        }
        return instance;
    }

    public Border getFocusFrameBorder() {
        return FastBorders.getFocusFrameBorder();
    }

    public Border getButtonBorder() {
        return FastBorders.getButtonBorder();
    }

    public Border getToggleButtonBorder() {
        return FastBorders.getToggleButtonBorder();
    }

    public Border getTextBorder() {
        return FastBorders.getTextBorder();
    }

    public Border getSpinnerBorder() {
        return FastBorders.getSpinnerBorder();
    }

    public Border getTextFieldBorder() {
        return FastBorders.getTextFieldBorder();
    }

    public Border getComboBoxBorder() {
        return FastBorders.getComboBoxBorder();
    }

    public Border getTableHeaderBorder() {
        return FastBorders.getTableHeaderBorder();
    }

    public Border getTableScrollPaneBorder() {
        return FastBorders.getTableScrollPaneBorder();
    }

    public Border getScrollPaneBorder() {
        return FastBorders.getScrollPaneBorder();
    }

    public Border getTabbedPaneBorder() {
        return FastBorders.getTabbedPaneBorder();
    }

    public Border getMenuBarBorder() {
        return FastBorders.getMenuBarBorder();
    }

    public Border getMenuItemBorder() {
        return FastBorders.getMenuItemBorder();
    }

    public Border getPopupMenuBorder() {
        return FastBorders.getPopupMenuBorder();
    }

    public Border getInternalFrameBorder() {
        return FastBorders.getInternalFrameBorder();
    }

    public Border getPaletteBorder() {
        return FastBorders.getPaletteBorder();
    }

    public Border getToolBarBorder() {
        return FastBorders.getToolBarBorder();
    }

    public Border getProgressBarBorder() {
        return FastBorders.getProgressBarBorder();
    }

    public Border getDesktopIconBorder() {
        return FastBorders.getDesktopIconBorder();
    }
}

