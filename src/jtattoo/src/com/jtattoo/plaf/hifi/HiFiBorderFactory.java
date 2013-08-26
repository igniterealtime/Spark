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
 
package com.jtattoo.plaf.hifi;

import com.jtattoo.plaf.AbstractBorderFactory;
import javax.swing.border.Border;

/**
 * @author Michael Hagen
 */
public class HiFiBorderFactory implements AbstractBorderFactory {

    private static HiFiBorderFactory instance = null;

    private HiFiBorderFactory() {
    }

    public static synchronized HiFiBorderFactory getInstance() {
        if (instance == null) {
            instance = new HiFiBorderFactory();
        }
        return instance;
    }

    public Border getFocusFrameBorder() {
        return HiFiBorders.getFocusFrameBorder();
    }

    public Border getButtonBorder() {
        return HiFiBorders.getButtonBorder();
    }

    public Border getToggleButtonBorder() {
        return HiFiBorders.getToggleButtonBorder();
    }

    public Border getTextBorder() {
        return HiFiBorders.getTextBorder();
    }

    public Border getSpinnerBorder() {
        return HiFiBorders.getSpinnerBorder();
    }

    public Border getTextFieldBorder() {
        return HiFiBorders.getTextFieldBorder();
    }

    public Border getComboBoxBorder() {
        return HiFiBorders.getComboBoxBorder();
    }

    public Border getTableHeaderBorder() {
        return HiFiBorders.getTableHeaderBorder();
    }

    public Border getTableScrollPaneBorder() {
        return HiFiBorders.getTableScrollPaneBorder();
    }

    public Border getScrollPaneBorder() {
        return HiFiBorders.getScrollPaneBorder();
    }

    public Border getTabbedPaneBorder() {
        return HiFiBorders.getTabbedPaneBorder();
    }

    public Border getMenuBarBorder() {
        return HiFiBorders.getMenuBarBorder();
    }

    public Border getMenuItemBorder() {
        return HiFiBorders.getMenuItemBorder();
    }

    public Border getPopupMenuBorder() {
        return HiFiBorders.getPopupMenuBorder();
    }

    public Border getInternalFrameBorder() {
        return HiFiBorders.getInternalFrameBorder();
    }

    public Border getPaletteBorder() {
        return HiFiBorders.getPaletteBorder();
    }

    public Border getToolBarBorder() {
        return HiFiBorders.getToolBarBorder();
    }

    public Border getProgressBarBorder() {
        return HiFiBorders.getProgressBarBorder();
    }

    public Border getDesktopIconBorder() {
        return HiFiBorders.getDesktopIconBorder();
    }
}

