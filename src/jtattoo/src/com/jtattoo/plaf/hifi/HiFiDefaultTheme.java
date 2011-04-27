/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.hifi;

import javax.swing.plaf.*;
import java.awt.*;

import com.jtattoo.plaf.*;

/**
 * @author Michael Hagen
 */
public class HiFiDefaultTheme extends AbstractTheme {

    public HiFiDefaultTheme() {
        super();
        // Setup theme with defaults
        setUpColor();
        // Overwrite defaults with user props
        loadProperties();
        // Setup the color arrays
        setUpColorArrs();
    }

    public String getPropertyFileName() {
        return "HiFiTheme.properties";
    }

    public void setUpColor() {
        super.setUpColor();

        // Defaults for HiFiLookAndFeel
        textShadow = true;
        foregroundColor = extraLightGray;
        disabledForegroundColor = gray;//extraDarkGray;
        disabledBackgroundColor = new ColorUIResource(48, 48, 48);

        backgroundColor = new ColorUIResource(48, 48, 48);
        backgroundColorLight = new ColorUIResource(48, 48, 48);
        backgroundColorDark = new ColorUIResource(16, 16, 16);
        alterBackgroundColor = new ColorUIResource(64, 64, 64);
        selectionForegroundColor = white;
        selectionBackgroundColor = new ColorUIResource(40, 40, 40);
        frameColor = black;
        gridColor = black;
        focusCellColor = orange;

        inputBackgroundColor = new ColorUIResource(80, 80, 80);
        inputForegroundColor = foregroundColor;

        rolloverColor = new ColorUIResource(112, 112, 112);
        rolloverColorLight = new ColorUIResource(128, 128, 128);
        rolloverColorDark = new ColorUIResource(96, 96, 96);

        buttonForegroundColor = foregroundColor;
        buttonBackgroundColor = new ColorUIResource(96, 96, 96);
        buttonColorLight = new ColorUIResource(96, 96, 96);
        buttonColorDark = new ColorUIResource(32, 32, 32);

        controlForegroundColor = foregroundColor;
        controlBackgroundColor = new ColorUIResource(64, 64, 64); // netbeans use this for selected tab in the toolbar
        controlColorLight = new ColorUIResource(96, 96, 96);
        controlColorDark = new ColorUIResource(32, 32, 32);
        controlHighlightColor = new ColorUIResource(96, 96, 96);
        controlShadowColor = new ColorUIResource(32, 32, 32);
        controlDarkShadowColor = black;


        windowTitleForegroundColor = foregroundColor;
        windowTitleBackgroundColor = new ColorUIResource(96, 96, 96);
        windowTitleColorLight = new ColorUIResource(96, 96, 96);
        windowTitleColorDark = new ColorUIResource(16, 16, 16);
        windowBorderColor = new ColorUIResource(36, 36, 36);
        windowIconColor = lightGray;
        windowIconShadowColor = black;
        windowIconRolloverColor = orange;

        windowInactiveTitleForegroundColor = new ColorUIResource(196, 196, 196);
        windowInactiveTitleBackgroundColor = new ColorUIResource(64, 64, 64);
        windowInactiveTitleColorLight = new ColorUIResource(64, 64, 64);
        windowInactiveTitleColorDark = new ColorUIResource(32, 32, 32);
        windowInactiveBorderColor = new ColorUIResource(32, 32, 32);

        menuForegroundColor = foregroundColor;
        menuBackgroundColor = new ColorUIResource(32, 32, 32);
        menuSelectionForegroundColor = white;
        menuSelectionBackgroundColor = new ColorUIResource(96, 96, 96);
        menuColorLight = new ColorUIResource(96, 96, 96);
        menuColorDark = new ColorUIResource(32, 32, 32);

        toolbarBackgroundColor = new ColorUIResource(64, 64, 64);
        toolbarColorLight = new ColorUIResource(96, 96, 96);
        toolbarColorDark = new ColorUIResource(32, 32, 32);

        tabAreaBackgroundColor = backgroundColor;
        desktopColor = new ColorUIResource(64, 64, 64);

        controlFont = new FontUIResource("Dialog", Font.BOLD, 12);
        systemFont = new FontUIResource("Dialog", Font.BOLD, 12);
        userFont = new FontUIResource("Dialog", Font.BOLD, 12);
        menuFont = new FontUIResource("Dialog", Font.BOLD, 12);
        windowTitleFont = new FontUIResource("Dialog", Font.BOLD, 12);
        smallFont = new FontUIResource("Dialog", Font.PLAIN, 10);
    }

    public void setUpColorArrs() {
        super.setUpColorArrs();
        DEFAULT_COLORS = ColorHelper.createColorArr(controlColorLight, controlColorDark, 20);
        HIDEFAULT_COLORS = ColorHelper.createColorArr(ColorHelper.brighter(controlColorLight, 15), ColorHelper.brighter(controlColorDark, 15), 20);
        ACTIVE_COLORS = DEFAULT_COLORS;
        INACTIVE_COLORS = ColorHelper.createColorArr(new Color(64, 64, 64), new Color(32, 32, 32), 20);
        SELECTED_COLORS = HIDEFAULT_COLORS;
        BUTTON_COLORS = ColorHelper.createColorArr(buttonColorLight, buttonColorDark, 20);
        ROLLOVER_COLORS = HIDEFAULT_COLORS;
        PRESSED_COLORS = ColorHelper.createColorArr(black, controlColorDark, 20);
        DISABLED_COLORS = ColorHelper.createColorArr(ColorHelper.darker(controlColorLight, 10), ColorHelper.darker(controlColorDark, 10), 20);
        WINDOW_TITLE_COLORS = ColorHelper.createColorArr(windowTitleColorLight, windowTitleColorDark, 20);
        WINDOW_INACTIVE_TITLE_COLORS = ColorHelper.createColorArr(windowInactiveTitleColorLight, windowInactiveTitleColorDark, 20);
        MENUBAR_COLORS = DEFAULT_COLORS;
        TOOLBAR_COLORS = MENUBAR_COLORS;
        TRACK_COLORS = ColorHelper.createColorArr(ColorHelper.darker(backgroundColor, 10), ColorHelper.brighter(backgroundColor, 5), 20);
        SLIDER_COLORS = DEFAULT_COLORS;
        PROGRESSBAR_COLORS = DEFAULT_COLORS;
        THUMB_COLORS = DEFAULT_COLORS;
        TAB_COLORS = DEFAULT_COLORS;
        COL_HEADER_COLORS = DEFAULT_COLORS;
    }
}
