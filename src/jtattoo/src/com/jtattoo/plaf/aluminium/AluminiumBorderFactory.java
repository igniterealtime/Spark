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

import com.jtattoo.plaf.AbstractBorderFactory;
import javax.swing.border.Border;

/**
 * @author Michael Hagen
 */
public class AluminiumBorderFactory implements AbstractBorderFactory {

    private static AluminiumBorderFactory instance = null;

    private AluminiumBorderFactory() {
    }

    public static synchronized AluminiumBorderFactory getInstance() {
        if (instance == null) {
            instance = new AluminiumBorderFactory();
        }
        return instance;
    }

    public Border getFocusFrameBorder() {
        return AluminiumBorders.getFocusFrameBorder();
    }

    public Border getButtonBorder() {
        return AluminiumBorders.getButtonBorder();
    }

    public Border getToggleButtonBorder() {
        return AluminiumBorders.getToggleButtonBorder();
    }

    public Border getTextBorder() {
        return AluminiumBorders.getTextBorder();
    }

    public Border getSpinnerBorder() {
        return AluminiumBorders.getSpinnerBorder();
    }

    public Border getTextFieldBorder() {
        return AluminiumBorders.getTextFieldBorder();
    }

    public Border getComboBoxBorder() {
        return AluminiumBorders.getComboBoxBorder();
    }

    public Border getTableHeaderBorder() {
        return AluminiumBorders.getTableHeaderBorder();
    }

    public Border getTableScrollPaneBorder() {
        return AluminiumBorders.getTableScrollPaneBorder();
    }

    public Border getScrollPaneBorder() {
        return AluminiumBorders.getScrollPaneBorder();
    }

    public Border getTabbedPaneBorder() {
        return AluminiumBorders.getTabbedPaneBorder();
    }

    public Border getMenuBarBorder() {
        return AluminiumBorders.getMenuBarBorder();
    }

    public Border getMenuItemBorder() {
        return AluminiumBorders.getMenuItemBorder();
    }

    public Border getPopupMenuBorder() {
        return AluminiumBorders.getPopupMenuBorder();
    }

    public Border getInternalFrameBorder() {
        return AluminiumBorders.getInternalFrameBorder();
    }

    public Border getPaletteBorder() {
        return AluminiumBorders.getPaletteBorder();
    }

    public Border getToolBarBorder() {
        return AluminiumBorders.getToolBarBorder();
    }

    public Border getProgressBarBorder() {
        return AluminiumBorders.getProgressBarBorder();
    }

    public Border getDesktopIconBorder() {
        return AluminiumBorders.getDesktopIconBorder();
    }
}

