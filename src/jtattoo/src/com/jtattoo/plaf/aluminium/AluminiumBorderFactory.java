/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.aluminium;

import javax.swing.border.*;
import com.jtattoo.plaf.*;

/**
 * @author Michael Hagen
 */
public class AluminiumBorderFactory implements AbstractBorderFactory {

    private static AluminiumBorderFactory instance = null;

    private AluminiumBorderFactory() {
    }

    public static synchronized AluminiumBorderFactory getInstance() {
        if (instance == null) {
            instance = new AluminiumBorderFactory();
        }
        return instance;
    }

    public Border getFocusFrameBorder() {
        return AluminiumBorders.getFocusFrameBorder();
    }

    public Border getButtonBorder() {
        return AluminiumBorders.getButtonBorder();
    }

    public Border getToggleButtonBorder() {
        return AluminiumBorders.getToggleButtonBorder();
    }

    public Border getTextBorder() {
        return AluminiumBorders.getTextBorder();
    }

    public Border getSpinnerBorder() {
        return AluminiumBorders.getSpinnerBorder();
    }

    public Border getTextFieldBorder() {
        return AluminiumBorders.getTextFieldBorder();
    }

    public Border getComboBoxBorder() {
        return AluminiumBorders.getComboBoxBorder();
    }

    public Border getTableHeaderBorder() {
        return AluminiumBorders.getTableHeaderBorder();
    }

    public Border getTableScrollPaneBorder() {
        return AluminiumBorders.getTableScrollPaneBorder();
    }

    public Border getScrollPaneBorder() {
        return AluminiumBorders.getScrollPaneBorder();
    }

    public Border getTabbedPaneBorder() {
        return AluminiumBorders.getTabbedPaneBorder();
    }

    public Border getMenuBarBorder() {
        return AluminiumBorders.getMenuBarBorder();
    }

    public Border getMenuItemBorder() {
        return AluminiumBorders.getMenuItemBorder();
    }

    public Border getPopupMenuBorder() {
        return AluminiumBorders.getPopupMenuBorder();
    }

    public Border getInternalFrameBorder() {
        return AluminiumBorders.getInternalFrameBorder();
    }

    public Border getPaletteBorder() {
        return AluminiumBorders.getPaletteBorder();
    }

    public Border getToolBarBorder() {
        return AluminiumBorders.getToolBarBorder();
    }

    public Border getProgressBarBorder() {
        return AluminiumBorders.getProgressBarBorder();
    }

    public Border getDesktopIconBorder() {
        return AluminiumBorders.getDesktopIconBorder();
    }
}

