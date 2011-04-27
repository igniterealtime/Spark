/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.mcwin;

import javax.swing.border.*;
import com.jtattoo.plaf.*;

/**
 * @author Michael Hagen
 */
public class McWinBorderFactory implements AbstractBorderFactory {

    private static McWinBorderFactory instance = null;

    private McWinBorderFactory() {
    }

    public static synchronized McWinBorderFactory getInstance() {
        if (instance == null) {
            instance = new McWinBorderFactory();
        }
        return instance;
    }

    public Border getFocusFrameBorder() {
        return McWinBorders.getFocusFrameBorder();
    }

    public Border getButtonBorder() {
        return McWinBorders.getButtonBorder();
    }

    public Border getToggleButtonBorder() {
        return McWinBorders.getToggleButtonBorder();
    }

    public Border getTextBorder() {
        return McWinBorders.getTextBorder();
    }

    public Border getSpinnerBorder() {
        return McWinBorders.getSpinnerBorder();
    }

    public Border getTextFieldBorder() {
        return McWinBorders.getTextFieldBorder();
    }

    public Border getComboBoxBorder() {
        return McWinBorders.getComboBoxBorder();
    }

    public Border getTableHeaderBorder() {
        return McWinBorders.getTableHeaderBorder();
    }

    public Border getTableScrollPaneBorder() {
        return McWinBorders.getTableScrollPaneBorder();
    }

    public Border getScrollPaneBorder() {
        return McWinBorders.getScrollPaneBorder();
    }

    public Border getTabbedPaneBorder() {
        return McWinBorders.getTabbedPaneBorder();
    }

    public Border getMenuBarBorder() {
        return McWinBorders.getMenuBarBorder();
    }

    public Border getMenuItemBorder() {
        return McWinBorders.getMenuItemBorder();
    }

    public Border getPopupMenuBorder() {
        return McWinBorders.getPopupMenuBorder();
    }

    public Border getInternalFrameBorder() {
        return McWinBorders.getInternalFrameBorder();
    }

    public Border getPaletteBorder() {
        return McWinBorders.getPaletteBorder();
    }

    public Border getToolBarBorder() {
        return McWinBorders.getToolBarBorder();
    }

    public Border getProgressBarBorder() {
        return McWinBorders.getProgressBarBorder();
    }

    public Border getDesktopIconBorder() {
        return McWinBorders.getDesktopIconBorder();
    }
}

