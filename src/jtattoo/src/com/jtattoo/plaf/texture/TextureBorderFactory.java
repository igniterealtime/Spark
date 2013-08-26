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
 
package com.jtattoo.plaf.texture;

import com.jtattoo.plaf.AbstractBorderFactory;
import javax.swing.border.Border;

/**
 * @author Michael Hagen
 */
public class TextureBorderFactory implements AbstractBorderFactory {

    private static TextureBorderFactory instance = null;
    
    private TextureBorderFactory() {
    }
    
    public static synchronized TextureBorderFactory getInstance() {
        if (instance == null)
            instance = new TextureBorderFactory();
        return instance;
    }
    
    public Border getFocusFrameBorder() {
        return TextureBorders.getFocusFrameBorder();
    }

    public Border getButtonBorder() {
        return TextureBorders.getButtonBorder();
    }
    
    public Border getToggleButtonBorder() {
        return TextureBorders.getToggleButtonBorder();
    }
    
    public Border getTextBorder() {
        return TextureBorders.getTextBorder();
    }
    
    public Border getSpinnerBorder() {
        return TextureBorders.getSpinnerBorder();
    }
    
    public Border getTextFieldBorder() {
        return TextureBorders.getTextFieldBorder();
    }
    
    public Border getComboBoxBorder() {
        return TextureBorders.getComboBoxBorder();
    }
    
    public Border getTableHeaderBorder() {
        return TextureBorders.getTableHeaderBorder();
    }
    
    public Border getTableScrollPaneBorder() {
        return TextureBorders.getTableScrollPaneBorder();
    }

    public Border getScrollPaneBorder() {
        return TextureBorders.getScrollPaneBorder();
    }
    
    public Border getTabbedPaneBorder() {
        return TextureBorders.getTabbedPaneBorder();
    }
    
    public Border getMenuBarBorder() {
        return TextureBorders.getMenuBarBorder();
    }
    
    public Border getMenuItemBorder() {
        return TextureBorders.getMenuItemBorder();
    }
    
    public Border getPopupMenuBorder() {
        return TextureBorders.getPopupMenuBorder();
    }
    
    public Border getInternalFrameBorder() {
        return TextureBorders.getInternalFrameBorder();
    }
    
    public Border getPaletteBorder() {
        return TextureBorders.getPaletteBorder();
    }
    
    public Border getToolBarBorder() {
        return TextureBorders.getToolBarBorder();
    }
    
    public Border getProgressBarBorder() {
        return TextureBorders.getProgressBarBorder();
    }
    
    public Border getDesktopIconBorder() {
        return TextureBorders.getDesktopIconBorder();
    }
}

