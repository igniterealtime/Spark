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

import com.jtattoo.plaf.AbstractBorderFactory;
import javax.swing.border.Border;

/**
 * @author Michael Hagen
 */
public class SmartBorderFactory implements AbstractBorderFactory {

    private static SmartBorderFactory instance = null;

    private SmartBorderFactory() {
    }

    public static synchronized SmartBorderFactory getInstance() {
        if (instance == null) {
            instance = new SmartBorderFactory();
        }
        return instance;
    }

    public Border getFocusFrameBorder() {
        return SmartBorders.getFocusFrameBorder();
    }

    public Border getButtonBorder() {
        return SmartBorders.getButtonBorder();
    }

    public Border getToggleButtonBorder() {
        return SmartBorders.getToggleButtonBorder();
    }

    public Border getTextBorder() {
        return SmartBorders.getTextBorder();
    }

    public Border getSpinnerBorder() {
        return SmartBorders.getSpinnerBorder();
    }

    public Border getTextFieldBorder() {
        return SmartBorders.getTextFieldBorder();
    }

    public Border getComboBoxBorder() {
        return SmartBorders.getComboBoxBorder();
    }

    public Border getTableHeaderBorder() {
        return SmartBorders.getTableHeaderBorder();
    }

    public Border getTableScrollPaneBorder() {
        return SmartBorders.getTableScrollPaneBorder();
    }

    public Border getScrollPaneBorder() {
        return SmartBorders.getScrollPaneBorder();
    }

    public Border getTabbedPaneBorder() {
        return SmartBorders.getTabbedPaneBorder();
    }

    public Border getMenuBarBorder() {
        return SmartBorders.getMenuBarBorder();
    }

    public Border getMenuItemBorder() {
        return SmartBorders.getMenuItemBorder();
    }

    public Border getPopupMenuBorder() {
        return SmartBorders.getPopupMenuBorder();
    }

    public Border getInternalFrameBorder() {
        return SmartBorders.getInternalFrameBorder();
    }

    public Border getPaletteBorder() {
        return SmartBorders.getPaletteBorder();
    }

    public Border getToolBarBorder() {
        return SmartBorders.getToolBarBorder();
    }

    public Border getProgressBarBorder() {
        return SmartBorders.getProgressBarBorder();
    }

    public Border getDesktopIconBorder() {
        return SmartBorders.getDesktopIconBorder();
    }
}

