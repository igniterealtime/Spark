/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.smart;

import javax.swing.border.*;

import com.jtattoo.plaf.*;

/**
 * @author Michael Hagen
 */
public class SmartBorderFactory implements AbstractBorderFactory {

    private static SmartBorderFactory instance = null;

    private SmartBorderFactory() {
    }

    public static synchronized SmartBorderFactory getInstance() {
        if (instance == null) {
            instance = new SmartBorderFactory();
        }
        return instance;
    }

    public Border getFocusFrameBorder() {
        return SmartBorders.getFocusFrameBorder();
    }

    public Border getButtonBorder() {
        return SmartBorders.getButtonBorder();
    }

    public Border getToggleButtonBorder() {
        return SmartBorders.getToggleButtonBorder();
    }

    public Border getTextBorder() {
        return SmartBorders.getTextBorder();
    }

    public Border getSpinnerBorder() {
        return SmartBorders.getSpinnerBorder();
    }

    public Border getTextFieldBorder() {
        return SmartBorders.getTextFieldBorder();
    }

    public Border getComboBoxBorder() {
        return SmartBorders.getComboBoxBorder();
    }

    public Border getTableHeaderBorder() {
        return SmartBorders.getTableHeaderBorder();
    }

    public Border getTableScrollPaneBorder() {
        return SmartBorders.getTableScrollPaneBorder();
    }

    public Border getScrollPaneBorder() {
        return SmartBorders.getScrollPaneBorder();
    }

    public Border getTabbedPaneBorder() {
        return SmartBorders.getTabbedPaneBorder();
    }

    public Border getMenuBarBorder() {
        return SmartBorders.getMenuBarBorder();
    }

    public Border getMenuItemBorder() {
        return SmartBorders.getMenuItemBorder();
    }

    public Border getPopupMenuBorder() {
        return SmartBorders.getPopupMenuBorder();
    }

    public Border getInternalFrameBorder() {
        return SmartBorders.getInternalFrameBorder();
    }

    public Border getPaletteBorder() {
        return SmartBorders.getPaletteBorder();
    }

    public Border getToolBarBorder() {
        return SmartBorders.getToolBarBorder();
    }

    public Border getProgressBarBorder() {
        return SmartBorders.getProgressBarBorder();
    }

    public Border getDesktopIconBorder() {
        return SmartBorders.getDesktopIconBorder();
    }
}

