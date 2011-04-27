/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.mint;

import javax.swing.border.*;
import com.jtattoo.plaf.*;

/**
 * @author Michael Hagen
 */
public class MintBorderFactory implements AbstractBorderFactory {

    private static MintBorderFactory instance = null;

    private MintBorderFactory() {
    }

    public static synchronized MintBorderFactory getInstance() {
        if (instance == null) {
            instance = new MintBorderFactory();
        }
        return instance;
    }

    public Border getFocusFrameBorder() {
        return MintBorders.getFocusFrameBorder();
    }

    public Border getButtonBorder() {
        return MintBorders.getButtonBorder();
    }

    public Border getToggleButtonBorder() {
        return MintBorders.getToggleButtonBorder();
    }

    public Border getTextBorder() {
        return MintBorders.getTextBorder();
    }

    public Border getSpinnerBorder() {
        return MintBorders.getSpinnerBorder();
    }

    public Border getTextFieldBorder() {
        return MintBorders.getTextFieldBorder();
    }

    public Border getComboBoxBorder() {
        return MintBorders.getComboBoxBorder();
    }

    public Border getTableHeaderBorder() {
        return MintBorders.getTableHeaderBorder();
    }

    public Border getTableScrollPaneBorder() {
        return MintBorders.getTableScrollPaneBorder();
    }

    public Border getScrollPaneBorder() {
        return MintBorders.getScrollPaneBorder();
    }

    public Border getTabbedPaneBorder() {
        return MintBorders.getTabbedPaneBorder();
    }

    public Border getMenuBarBorder() {
        return MintBorders.getMenuBarBorder();
    }

    public Border getMenuItemBorder() {
        return MintBorders.getMenuItemBorder();
    }

    public Border getPopupMenuBorder() {
        return MintBorders.getPopupMenuBorder();
    }

    public Border getInternalFrameBorder() {
        return MintBorders.getInternalFrameBorder();
    }

    public Border getPaletteBorder() {
        return MintBorders.getPaletteBorder();
    }

    public Border getToolBarBorder() {
        return MintBorders.getToolBarBorder();
    }

    public Border getProgressBarBorder() {
        return MintBorders.getProgressBarBorder();
    }

    public Border getDesktopIconBorder() {
        return MintBorders.getDesktopIconBorder();
    }
}

