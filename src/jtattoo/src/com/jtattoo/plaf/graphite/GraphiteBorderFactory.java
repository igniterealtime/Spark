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
 
package com.jtattoo.plaf.graphite;

import com.jtattoo.plaf.AbstractBorderFactory;
import javax.swing.border.Border;

/**
 * @author Michael Hagen
 */
public class GraphiteBorderFactory implements AbstractBorderFactory {

    private static GraphiteBorderFactory instance = null;

    private GraphiteBorderFactory() {
    }

    public static synchronized GraphiteBorderFactory getInstance() {
        if (instance == null) {
            instance = new GraphiteBorderFactory();
        }
        return instance;
    }

    public Border getFocusFrameBorder() {
        return GraphiteBorders.getFocusFrameBorder();
    }

    public Border getButtonBorder() {
        return GraphiteBorders.getButtonBorder();
    }

    public Border getToggleButtonBorder() {
        return GraphiteBorders.getToggleButtonBorder();
    }

    public Border getTextBorder() {
        return GraphiteBorders.getTextBorder();
    }

    public Border getSpinnerBorder() {
        return GraphiteBorders.getSpinnerBorder();
    }

    public Border getTextFieldBorder() {
        return GraphiteBorders.getTextFieldBorder();
    }

    public Border getComboBoxBorder() {
        return GraphiteBorders.getComboBoxBorder();
    }

    public Border getTableHeaderBorder() {
        return GraphiteBorders.getTableHeaderBorder();
    }

    public Border getTableScrollPaneBorder() {
        return GraphiteBorders.getTableScrollPaneBorder();
    }

    public Border getScrollPaneBorder() {
        return GraphiteBorders.getScrollPaneBorder();
    }

    public Border getTabbedPaneBorder() {
        return GraphiteBorders.getTabbedPaneBorder();
    }

    public Border getMenuBarBorder() {
        return GraphiteBorders.getMenuBarBorder();
    }

    public Border getMenuItemBorder() {
        return GraphiteBorders.getMenuItemBorder();
    }

    public Border getPopupMenuBorder() {
        return GraphiteBorders.getPopupMenuBorder();
    }

    public Border getInternalFrameBorder() {
        return GraphiteBorders.getInternalFrameBorder();
    }

    public Border getPaletteBorder() {
        return GraphiteBorders.getPaletteBorder();
    }

    public Border getToolBarBorder() {
        return GraphiteBorders.getToolBarBorder();
    }

    public Border getProgressBarBorder() {
        return GraphiteBorders.getProgressBarBorder();
    }

    public Border getDesktopIconBorder() {
        return GraphiteBorders.getDesktopIconBorder();
    }
}

