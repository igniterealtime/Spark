/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.bernstein;

import javax.swing.border.*;

import com.jtattoo.plaf.*;

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

