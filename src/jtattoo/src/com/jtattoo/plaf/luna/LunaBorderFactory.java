/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.luna;

import javax.swing.border.*;
import com.jtattoo.plaf.*;

/**
 * @author Michael Hagen
 */
public class LunaBorderFactory implements AbstractBorderFactory {

    private static LunaBorderFactory instance = null;

    private LunaBorderFactory() {
    }

    public static synchronized LunaBorderFactory getInstance() {
        if (instance == null) {
            instance = new LunaBorderFactory();
        }
        return instance;
    }

    public Border getFocusFrameBorder() {
        return LunaBorders.getFocusFrameBorder();
    }

    public Border getButtonBorder() {
        return LunaBorders.getButtonBorder();
    }

    public Border getToggleButtonBorder() {
        return LunaBorders.getToggleButtonBorder();
    }

    public Border getTextBorder() {
        return LunaBorders.getTextBorder();
    }

    public Border getSpinnerBorder() {
        return LunaBorders.getSpinnerBorder();
    }

    public Border getTextFieldBorder() {
        return LunaBorders.getTextFieldBorder();
    }

    public Border getComboBoxBorder() {
        return LunaBorders.getComboBoxBorder();
    }

    public Border getTableHeaderBorder() {
        return LunaBorders.getTableHeaderBorder();
    }

    public Border getTableScrollPaneBorder() {
        return LunaBorders.getTableScrollPaneBorder();
    }

    public Border getScrollPaneBorder() {
        return LunaBorders.getScrollPaneBorder();
    }

    public Border getTabbedPaneBorder() {
        return LunaBorders.getTabbedPaneBorder();
    }

    public Border getMenuBarBorder() {
        return LunaBorders.getMenuBarBorder();
    }

    public Border getMenuItemBorder() {
        return LunaBorders.getMenuItemBorder();
    }

    public Border getPopupMenuBorder() {
        return LunaBorders.getPopupMenuBorder();
    }

    public Border getInternalFrameBorder() {
        return LunaBorders.getInternalFrameBorder();
    }

    public Border getPaletteBorder() {
        return LunaBorders.getPaletteBorder();
    }

    public Border getToolBarBorder() {
        return LunaBorders.getToolBarBorder();
    }

    public Border getProgressBarBorder() {
        return LunaBorders.getProgressBarBorder();
    }

    public Border getDesktopIconBorder() {
        return LunaBorders.getDesktopIconBorder();
    }
}

