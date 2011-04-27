/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.fast;

import javax.swing.border.*;
import com.jtattoo.plaf.*;

/**
 * @author Michael Hagen
 */
public class FastBorderFactory implements AbstractBorderFactory {

    private static FastBorderFactory instance = null;

    private FastBorderFactory() {
    }

    public static synchronized FastBorderFactory getInstance() {
        if (instance == null) {
            instance = new FastBorderFactory();
        }
        return instance;
    }

    public Border getFocusFrameBorder() {
        return FastBorders.getFocusFrameBorder();
    }

    public Border getButtonBorder() {
        return FastBorders.getButtonBorder();
    }

    public Border getToggleButtonBorder() {
        return FastBorders.getToggleButtonBorder();
    }

    public Border getTextBorder() {
        return FastBorders.getTextBorder();
    }

    public Border getSpinnerBorder() {
        return FastBorders.getSpinnerBorder();
    }

    public Border getTextFieldBorder() {
        return FastBorders.getTextFieldBorder();
    }

    public Border getComboBoxBorder() {
        return FastBorders.getComboBoxBorder();
    }

    public Border getTableHeaderBorder() {
        return FastBorders.getTableHeaderBorder();
    }

    public Border getTableScrollPaneBorder() {
        return FastBorders.getTableScrollPaneBorder();
    }

    public Border getScrollPaneBorder() {
        return FastBorders.getScrollPaneBorder();
    }

    public Border getTabbedPaneBorder() {
        return FastBorders.getTabbedPaneBorder();
    }

    public Border getMenuBarBorder() {
        return FastBorders.getMenuBarBorder();
    }

    public Border getMenuItemBorder() {
        return FastBorders.getMenuItemBorder();
    }

    public Border getPopupMenuBorder() {
        return FastBorders.getPopupMenuBorder();
    }

    public Border getInternalFrameBorder() {
        return FastBorders.getInternalFrameBorder();
    }

    public Border getPaletteBorder() {
        return FastBorders.getPaletteBorder();
    }

    public Border getToolBarBorder() {
        return FastBorders.getToolBarBorder();
    }

    public Border getProgressBarBorder() {
        return FastBorders.getProgressBarBorder();
    }

    public Border getDesktopIconBorder() {
        return FastBorders.getDesktopIconBorder();
    }
}

