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
 
package com.jtattoo.plaf.mint;

import com.jtattoo.plaf.AbstractBorderFactory;
import javax.swing.border.Border;

/**
 * @author Michael Hagen
 */
public class MintBorderFactory implements AbstractBorderFactory {

    private static MintBorderFactory instance = null;

    private MintBorderFactory() {
    }

    public static synchronized MintBorderFactory getInstance() {
        if (instance == null) {
            instance = new MintBorderFactory();
        }
        return instance;
    }

    public Border getFocusFrameBorder() {
        return MintBorders.getFocusFrameBorder();
    }

    public Border getButtonBorder() {
        return MintBorders.getButtonBorder();
    }

    public Border getToggleButtonBorder() {
        return MintBorders.getToggleButtonBorder();
    }

    public Border getTextBorder() {
        return MintBorders.getTextBorder();
    }

    public Border getSpinnerBorder() {
        return MintBorders.getSpinnerBorder();
    }

    public Border getTextFieldBorder() {
        return MintBorders.getTextFieldBorder();
    }

    public Border getComboBoxBorder() {
        return MintBorders.getComboBoxBorder();
    }

    public Border getTableHeaderBorder() {
        return MintBorders.getTableHeaderBorder();
    }

    public Border getTableScrollPaneBorder() {
        return MintBorders.getTableScrollPaneBorder();
    }

    public Border getScrollPaneBorder() {
        return MintBorders.getScrollPaneBorder();
    }

    public Border getTabbedPaneBorder() {
        return MintBorders.getTabbedPaneBorder();
    }

    public Border getMenuBarBorder() {
        return MintBorders.getMenuBarBorder();
    }

    public Border getMenuItemBorder() {
        return MintBorders.getMenuItemBorder();
    }

    public Border getPopupMenuBorder() {
        return MintBorders.getPopupMenuBorder();
    }

    public Border getInternalFrameBorder() {
        return MintBorders.getInternalFrameBorder();
    }

    public Border getPaletteBorder() {
        return MintBorders.getPaletteBorder();
    }

    public Border getToolBarBorder() {
        return MintBorders.getToolBarBorder();
    }

    public Border getProgressBarBorder() {
        return MintBorders.getProgressBarBorder();
    }

    public Border getDesktopIconBorder() {
        return MintBorders.getDesktopIconBorder();
    }
}

