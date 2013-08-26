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
 
package com.jtattoo.plaf.mcwin;

import com.jtattoo.plaf.AbstractBorderFactory;
import javax.swing.border.Border;

/**
 * @author Michael Hagen
 */
public class McWinBorderFactory implements AbstractBorderFactory {

    private static McWinBorderFactory instance = null;

    private McWinBorderFactory() {
    }

    public static synchronized McWinBorderFactory getInstance() {
        if (instance == null) {
            instance = new McWinBorderFactory();
        }
        return instance;
    }

    public Border getFocusFrameBorder() {
        return McWinBorders.getFocusFrameBorder();
    }

    public Border getButtonBorder() {
        return McWinBorders.getButtonBorder();
    }

    public Border getToggleButtonBorder() {
        return McWinBorders.getToggleButtonBorder();
    }

    public Border getTextBorder() {
        return McWinBorders.getTextBorder();
    }

    public Border getSpinnerBorder() {
        return McWinBorders.getSpinnerBorder();
    }

    public Border getTextFieldBorder() {
        return McWinBorders.getTextFieldBorder();
    }

    public Border getComboBoxBorder() {
        return McWinBorders.getComboBoxBorder();
    }

    public Border getTableHeaderBorder() {
        return McWinBorders.getTableHeaderBorder();
    }

    public Border getTableScrollPaneBorder() {
        return McWinBorders.getTableScrollPaneBorder();
    }

    public Border getScrollPaneBorder() {
        return McWinBorders.getScrollPaneBorder();
    }

    public Border getTabbedPaneBorder() {
        return McWinBorders.getTabbedPaneBorder();
    }

    public Border getMenuBarBorder() {
        return McWinBorders.getMenuBarBorder();
    }

    public Border getMenuItemBorder() {
        return McWinBorders.getMenuItemBorder();
    }

    public Border getPopupMenuBorder() {
        return McWinBorders.getPopupMenuBorder();
    }

    public Border getInternalFrameBorder() {
        return McWinBorders.getInternalFrameBorder();
    }

    public Border getPaletteBorder() {
        return McWinBorders.getPaletteBorder();
    }

    public Border getToolBarBorder() {
        return McWinBorders.getToolBarBorder();
    }

    public Border getProgressBarBorder() {
        return McWinBorders.getProgressBarBorder();
    }

    public Border getDesktopIconBorder() {
        return McWinBorders.getDesktopIconBorder();
    }
}

