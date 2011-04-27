/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */

package com.jtattoo.plaf.aero;

import javax.swing.border.*;

import com.jtattoo.plaf.*;

/**
 * @author Michael Hagen
 */
public class AeroBorderFactory implements AbstractBorderFactory {
    private static AeroBorderFactory instance = null;
    
    private AeroBorderFactory() {
    }
    
    public static synchronized AeroBorderFactory getInstance() {
        if (instance == null)
            instance = new AeroBorderFactory();
        return instance;
    }
    
    public Border getFocusFrameBorder() {
        return AeroBorders.getFocusFrameBorder();
    }

    public Border getButtonBorder() {
        return AeroBorders.getButtonBorder(); 
    }
    
    public Border getToggleButtonBorder() {
        return AeroBorders.getToggleButtonBorder(); 
    }
    
    public Border getTextBorder() {
        return AeroBorders.getTextBorder(); 
    }
    
    public Border getSpinnerBorder() {
        return AeroBorders.getSpinnerBorder();
    }
    
    public Border getTextFieldBorder() {
        return AeroBorders.getTextFieldBorder(); 
    }
    
    public Border getComboBoxBorder() {
        return AeroBorders.getComboBoxBorder(); 
    }
    
    public Border getTableHeaderBorder() {
        return AeroBorders.getTableHeaderBorder(); 
    }
    
    public Border getTableScrollPaneBorder() {
        return AeroBorders.getTableScrollPaneBorder();
    }

    public Border getScrollPaneBorder() {
        return AeroBorders.getScrollPaneBorder(); 
    }
    
    public Border getTabbedPaneBorder() {
        return AeroBorders.getTabbedPaneBorder(); 
    }
    
    public Border getMenuBarBorder() {
        return AeroBorders.getMenuBarBorder(); 
    }
    
    public Border getMenuItemBorder() {
        return AeroBorders.getMenuItemBorder(); 
    }
    
    public Border getPopupMenuBorder() {
        return AeroBorders.getPopupMenuBorder(); 
    }
    
    public Border getInternalFrameBorder() {
        return AeroBorders.getInternalFrameBorder(); 
    }
    
    public Border getPaletteBorder() {
        return AeroBorders.getPaletteBorder(); 
    }
    
    public Border getToolBarBorder() {
        return AeroBorders.getToolBarBorder(); 
    }
    
    public Border getProgressBarBorder() {
        return AeroBorders.getProgressBarBorder(); 
    }
    
    public Border getDesktopIconBorder() {
        return AeroBorders.getDesktopIconBorder(); 
    }
}

