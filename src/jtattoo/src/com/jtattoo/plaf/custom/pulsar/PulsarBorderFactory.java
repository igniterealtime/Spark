/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */

package com.jtattoo.plaf.custom.pulsar;

import javax.swing.border.*;

import com.jtattoo.plaf.*;

/**
 * @author Michael Hagen
 */
public class PulsarBorderFactory implements AbstractBorderFactory {
    private static PulsarBorderFactory instance = null;
    
    private PulsarBorderFactory() {
    }
    
    public static synchronized PulsarBorderFactory getInstance() {
        if (instance == null)
            instance = new PulsarBorderFactory();
        return instance;
    }
    
    public Border getFocusFrameBorder() {
        return PulsarBorders.getFocusFrameBorder();
    }

    public Border getButtonBorder() {
        return PulsarBorders.getButtonBorder();
    }
    
    public Border getToggleButtonBorder() {
        return PulsarBorders.getToggleButtonBorder();
    }
    
    public Border getTextBorder() {
        return PulsarBorders.getTextBorder();
    }
    
    public Border getSpinnerBorder() {
        return PulsarBorders.getSpinnerBorder();
    }
    
    public Border getTextFieldBorder() {
        return PulsarBorders.getTextFieldBorder();
    }
    
    public Border getComboBoxBorder() {
        return PulsarBorders.getComboBoxBorder();
    }
    
    public Border getTableHeaderBorder() {
        return PulsarBorders.getTableHeaderBorder();
    }
    
    public Border getTableScrollPaneBorder() {
        return PulsarBorders.getTableScrollPaneBorder();
    }

    public Border getScrollPaneBorder() {
        return PulsarBorders.getScrollPaneBorder();
    }
    
    public Border getTabbedPaneBorder() {
        return PulsarBorders.getTabbedPaneBorder();
    }
    
    public Border getMenuBarBorder() {
        return PulsarBorders.getMenuBarBorder();
    }
    
    public Border getMenuItemBorder() {
        return PulsarBorders.getMenuItemBorder();
    }
    
    public Border getPopupMenuBorder() {
        return PulsarBorders.getPopupMenuBorder();
    }
    
    public Border getInternalFrameBorder() {
        return PulsarBorders.getInternalFrameBorder();
    }
    
    public Border getPaletteBorder() {
        return PulsarBorders.getPaletteBorder();
    }
    
    public Border getToolBarBorder() {
        return PulsarBorders.getToolBarBorder();
    }
    
    public Border getProgressBarBorder() {
        return PulsarBorders.getProgressBarBorder();
    }
    
    public Border getDesktopIconBorder() {
        return PulsarBorders.getDesktopIconBorder();
    }
}

