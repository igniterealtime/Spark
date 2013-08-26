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
 
package com.jtattoo.plaf.acryl;

import com.jtattoo.plaf.AbstractBorderFactory;
import javax.swing.border.Border;

/**
 * @author Michael Hagen
 */
public class AcrylBorderFactory implements AbstractBorderFactory {

    private static AcrylBorderFactory instance = null;

    private AcrylBorderFactory() {
    }

    public static synchronized AcrylBorderFactory getInstance() {
        if (instance == null) {
            instance = new AcrylBorderFactory();
        }
        return instance;
    }

    public Border getFocusFrameBorder() {
        return AcrylBorders.getFocusFrameBorder();
    }

    public Border getButtonBorder() {
        return AcrylBorders.getButtonBorder();
    }

    public Border getToggleButtonBorder() {
        return AcrylBorders.getToggleButtonBorder();
    }

    public Border getTextBorder() {
        return AcrylBorders.getTextBorder();
    }

    public Border getSpinnerBorder() {
        return AcrylBorders.getSpinnerBorder();
    }

    public Border getTextFieldBorder() {
        return AcrylBorders.getTextFieldBorder();
    }

    public Border getComboBoxBorder() {
        return AcrylBorders.getComboBoxBorder();
    }

    public Border getTableHeaderBorder() {
        return AcrylBorders.getTableHeaderBorder();
    }

    public Border getTableScrollPaneBorder() {
        return AcrylBorders.getTableScrollPaneBorder();
    }

    public Border getScrollPaneBorder() {
        return AcrylBorders.getScrollPaneBorder();
    }

    public Border getTabbedPaneBorder() {
        return AcrylBorders.getTabbedPaneBorder();
    }

    public Border getMenuBarBorder() {
        return AcrylBorders.getMenuBarBorder();
    }

    public Border getMenuItemBorder() {
        return AcrylBorders.getMenuItemBorder();
    }

    public Border getPopupMenuBorder() {
        return AcrylBorders.getPopupMenuBorder();
    }

    public Border getInternalFrameBorder() {
        return AcrylBorders.getInternalFrameBorder();
    }

    public Border getPaletteBorder() {
        return AcrylBorders.getPaletteBorder();
    }

    public Border getToolBarBorder() {
        return AcrylBorders.getToolBarBorder();
    }

    public Border getProgressBarBorder() {
        return AcrylBorders.getProgressBarBorder();
    }

    public Border getDesktopIconBorder() {
        return AcrylBorders.getDesktopIconBorder();
    }
}

