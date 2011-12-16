/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.fast;

import javax.swing.plaf.*;

import com.jtattoo.plaf.*;
import java.awt.Color;

/**
 * @author Michael Hagen
 */
public class FastDefaultTheme extends AbstractTheme {

    public FastDefaultTheme() {
        super();
        // Setup theme with defaults
        setUpColor();
        // Overwrite defaults with user props
        loadProperties();
        // Setup the color arrays
        setUpColorArrs();
    }

    public String getPropertyFileName() {
        return "FastTheme.properties";
    }

    public void setUpColor() {
        super.setUpColor();
        // Defaults for FastLookAndFeel
        backgroundColor = new ColorUIResource(244, 244, 244);
        backgroundColorLight = new ColorUIResource(255, 255, 255);
        backgroundColorDark = new ColorUIResource(232, 232, 232);
        alterBackgroundColor = new ColorUIResource(232, 232, 232);
        selectionBackgroundColor = new ColorUIResource(210, 210, 210);
        frameColor = gray;
        focusColor = new ColorUIResource(160, 160, 200);
        focusCellColor = new ColorUIResource(160, 160, 200);
        buttonBackgroundColor = extraLightGray;
        controlBackgroundColor = new ColorUIResource(220, 220, 220);

        windowTitleBackgroundColor = new ColorUIResource(210, 210, 210);
        windowBorderColor = new ColorUIResource(210, 210, 210);

        windowInactiveTitleBackgroundColor = new ColorUIResource(230, 230, 230);
        windowInactiveBorderColor = new ColorUIResource(230, 230, 230);

        menuBackgroundColor = new ColorUIResource(240, 240, 240);
        menuSelectionBackgroundColor = lightGray;

        toolbarBackgroundColor = new ColorUIResource(240, 240, 240);

        tabAreaBackgroundColor = backgroundColor;
        desktopColor = new ColorUIResource(128, 128, 148);
    }

    public void setUpColorArrs() {
        super.setUpColorArrs();
        // Generate the color arrays
        DEFAULT_COLORS = ColorHelper.createColorArr(controlBackgroundColor, controlBackgroundColor, 2);
        HIDEFAULT_COLORS = ColorHelper.createColorArr(backgroundColor, backgroundColor, 2);

        ACTIVE_COLORS = DEFAULT_COLORS;
        INACTIVE_COLORS = HIDEFAULT_COLORS;

        ROLLOVER_COLORS = ColorHelper.createColorArr(buttonBackgroundColor, buttonBackgroundColor, 2);
        SELECTED_COLORS = ColorHelper.createColorArr(controlColorDark, controlColorDark, 2);
        PRESSED_COLORS = ColorHelper.createColorArr(controlColorDark, controlColorDark, 2);
        DISABLED_COLORS = ColorHelper.createColorArr(controlColorLight, controlColorLight, 2);

        BUTTON_COLORS = ColorHelper.createColorArr(buttonBackgroundColor, buttonBackgroundColor, 2);
        COL_HEADER_COLORS = ColorHelper.createColorArr(new Color(248, 248, 248), new Color(248, 248, 248), 2);
        CHECKBOX_COLORS = COL_HEADER_COLORS;

        TAB_COLORS = DEFAULT_COLORS;
    }
}
