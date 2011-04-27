/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */

package com.jtattoo.plaf.graphite;

import javax.swing.border.*;

import com.jtattoo.plaf.*;

/**
 * @author Michael Hagen
 */
public class GraphiteBorderFactory implements AbstractBorderFactory {

    private static GraphiteBorderFactory instance = null;
    
    private GraphiteBorderFactory() {
    }
    
    public static synchronized GraphiteBorderFactory getInstance() {
        if (instance == null)
            instance = new GraphiteBorderFactory();
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

