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
 
package com.jtattoo.plaf.bernstein;

import com.jtattoo.plaf.AbstractBorderFactory;
import javax.swing.border.Border;

/**
 * @author Michael Hagen
 */
public class BernsteinBorderFactory implements AbstractBorderFactory {

    private static BernsteinBorderFactory instance = null;

    private BernsteinBorderFactory() {
    }

    public static synchronized BernsteinBorderFactory getInstance() {
        if (instance == null) {
            instance = new BernsteinBorderFactory();
        }
        return instance;
    }

    public Border getFocusFrameBorder() {
        return BernsteinBorders.getFocusFrameBorder();
    }

    public Border getButtonBorder() {
        return BernsteinBorders.getButtonBorder();
    }

    public Border getToggleButtonBorder() {
        return BernsteinBorders.getToggleButtonBorder();
    }

    public Border getTextBorder() {
        return BernsteinBorders.getTextBorder();
    }

    public Border getSpinnerBorder() {
        return BernsteinBorders.getSpinnerBorder();
    }

    public Border getTextFieldBorder() {
        return BernsteinBorders.getTextFieldBorder();
    }

    public Border getComboBoxBorder() {
        return BernsteinBorders.getComboBoxBorder();
    }

    public Border getTableHeaderBorder() {
        return BernsteinBorders.getTableHeaderBorder();
    }

    public Border getTableScrollPaneBorder() {
        return BernsteinBorders.getTableScrollPaneBorder();
    }

    public Border getScrollPaneBorder() {
        return BernsteinBorders.getScrollPaneBorder();
    }

    public Border getTabbedPaneBorder() {
        return BernsteinBorders.getTabbedPaneBorder();
    }

    public Border getMenuBarBorder() {
        return BernsteinBorders.getMenuBarBorder();
    }

    public Border getMenuItemBorder() {
        return BernsteinBorders.getMenuItemBorder();
    }

    public Border getPopupMenuBorder() {
        return BernsteinBorders.getPopupMenuBorder();
    }

    public Border getInternalFrameBorder() {
        return BernsteinBorders.getInternalFrameBorder();
    }

    public Border getPaletteBorder() {
        return BernsteinBorders.getPaletteBorder();
    }

    public Border getToolBarBorder() {
        return BernsteinBorders.getToolBarBorder();
    }

    public Border getProgressBarBorder() {
        return BernsteinBorders.getProgressBarBorder();
    }

    public Border getDesktopIconBorder() {
        return BernsteinBorders.getDesktopIconBorder();
    }
}

