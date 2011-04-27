/*
 * Copyright 2010 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.custom.pulsar;

import java.awt.*;
import javax.swing.plaf.*;

import com.jtattoo.plaf.*;

public class PulsarDefaultTheme extends AbstractTheme {

    public PulsarDefaultTheme() {
        super();
        // Setup theme with defaults
        setUpColor();
        // Overwrite defaults with user props
        loadProperties();
        // Setup the color arrays
        setUpColorArrs();
    }

    public String getPropertyFileName() {
        return "PulsarTheme.properties";
    }

    public void setUpColor() {
        super.setUpColor();
        logoString = "Pulsar Multimedia";

        foregroundColor = new ColorUIResource(0, 32, 64);
        backgroundColor = new ColorUIResource(251, 250, 247);
        backgroundColorLight = white;
        backgroundColorDark = new ColorUIResource(244, 242, 232);
        alterBackgroundColor = new ColorUIResource(220, 220, 220);
        disabledForegroundColor = new ColorUIResource(96, 96, 96);
        disabledBackgroundColor = new ColorUIResource(244, 242, 232);
        inputBackgroundColor = white;
        inputForegroundColor = new ColorUIResource(0, 32, 64);

        selectionForegroundColor = new ColorUIResource(0, 32, 64);
        selectionBackgroundColor = new ColorUIResource(215, 228, 255);
        selectionBackgroundColorLight = new ColorUIResource(195, 227, 255);
        selectionBackgroundColorDark = new ColorUIResource(164, 195, 255);

        focusColor = orange;
        focusCellColor = orange;
        frameColor = new ColorUIResource(164, 164, 164);
        gridColor = new ColorUIResource(200, 200, 200);

        rolloverColor = orange;
        rolloverColorLight = new ColorUIResource(255, 240, 204);
        rolloverColorDark = new ColorUIResource(255, 213, 113);

        buttonForegroundColor = new ColorUIResource(0, 32, 64);
        buttonBackgroundColor = new ColorUIResource(244, 242, 232);
        buttonColorLight = white;
        buttonColorDark = new ColorUIResource(244, 242, 232);

        controlForegroundColor = new ColorUIResource(0, 32, 64);
        controlBackgroundColor = new ColorUIResource(244, 242, 232);
        controlShadowColor = new ColorUIResource(196, 196, 196);
        controlDarkShadowColor = new ColorUIResource(164, 164, 164);
        controlColorLight = new ColorUIResource(195, 227, 255);
        controlColorDark = new ColorUIResource(145, 183, 255);

        windowTitleForegroundColor = new ColorUIResource(0, 32, 64);
        windowTitleBackgroundColor = new ColorUIResource(164, 195, 255);
        windowTitleColorLight = new ColorUIResource(207, 238, 255);
        windowTitleColorDark = new ColorUIResource(168, 200, 255);
        windowBorderColor = new ColorUIResource(119, 157, 255);

        windowInactiveTitleForegroundColor = new ColorUIResource(ColorHelper.brighter(windowTitleForegroundColor, 20));
        windowInactiveTitleBackgroundColor = new ColorUIResource(ColorHelper.brighter(windowTitleBackgroundColor, 20));
        windowInactiveTitleColorLight = new ColorUIResource(ColorHelper.brighter(windowTitleColorLight, 20));
        windowInactiveTitleColorDark = new ColorUIResource(ColorHelper.brighter(windowTitleColorDark, 20));
        windowInactiveBorderColor = new ColorUIResource(ColorHelper.brighter(windowBorderColor, 20));

        menuForegroundColor = new ColorUIResource(0, 32, 64);
        menuBackgroundColor = white;
        menuSelectionForegroundColor = new ColorUIResource(0, 32, 64);
        menuSelectionBackgroundColor = new ColorUIResource(145, 179, 255);
        menuSelectionBackgroundColorLight = new ColorUIResource(195, 227, 255);
        menuSelectionBackgroundColorDark = new ColorUIResource(164, 195, 255);
        menuColorLight = white;
        menuColorDark = new ColorUIResource(251, 250, 247);

        toolbarForegroundColor = black;
        toolbarBackgroundColor = backgroundColor;
        toolbarColorLight = menuColorLight;
        toolbarColorDark = menuColorDark;

        tabAreaBackgroundColor = backgroundColor;
        desktopColor = backgroundColor;
        tooltipForegroundColor = black;
        tooltipBackgroundColor = yellow;

        controlFont = new FontUIResource("Dialog", Font.PLAIN, 12);
        systemFont = new FontUIResource("Dialog", Font.PLAIN, 12);
        userFont = new FontUIResource("Dialog", Font.PLAIN, 12);
        smallFont = new FontUIResource("Dialog", Font.PLAIN, 10);
        menuFont = new FontUIResource("Dialog", Font.PLAIN, 12);
        windowTitleFont = new FontUIResource("Dialog", Font.BOLD, 12);
    }

    public void setUpColorArrs() {
        super.setUpColorArrs();
        BUTTON_COLORS = ColorHelper.createColorArr(buttonColorLight, buttonColorDark, 24);
        DEFAULT_COLORS = ColorHelper.createColorArr(controlColorLight, controlColorDark, 24);
        HIDEFAULT_COLORS = new Color[20];
        for (int i = 0; i < 20; i++) {
            HIDEFAULT_COLORS[i] = ColorHelper.brighter(DEFAULT_COLORS[i], 40);
        }
        SELECTED_COLORS = DEFAULT_COLORS;
        SELECTION_COLORS = ColorHelper.createColorArr(selectionBackgroundColorLight, selectionBackgroundColorDark, 20);
        MENU_SELECTION_COLORS = ColorHelper.createColorArr(menuSelectionBackgroundColorLight, menuSelectionBackgroundColorDark, 20);

        ROLLOVER_COLORS = ColorHelper.createColorArr(rolloverColorLight, rolloverColorDark, 24);
        PRESSED_COLORS = new Color[] {new Color( 220, 220, 220) };//ColorHelper.createColorArr(menuColorDark, menuColorLight, 24);
        DISABLED_COLORS = ColorHelper.createColorArr(superLightGray, extraLightGray, 24);
        ACTIVE_COLORS = DEFAULT_COLORS;
        INACTIVE_COLORS = BUTTON_COLORS;

        WINDOW_TITLE_COLORS = new Color[] {
            new Color(168, 200, 255),
            new Color(172, 204, 255),
            new Color(175, 208, 255),
            new Color(180, 211, 255),
            new Color(184, 216, 255),
            new Color(188, 219, 255),
            new Color(191, 223, 255),
            new Color(195, 226, 255),
            new Color(199, 230, 255),
            new Color(203, 234, 255),
            new Color(207, 238, 255),

            new Color(207, 238, 255),
            new Color(203, 234, 255),
            new Color(199, 230, 255),
            new Color(195, 226, 255),
            new Color(191, 223, 255),
            new Color(188, 219, 255),
            new Color(184, 216, 255),
            new Color(180, 211, 255),
            new Color(175, 208, 255),
            new Color(172, 204, 255),
            new Color(168, 200, 255),
        };
        WINDOW_INACTIVE_TITLE_COLORS = new Color[WINDOW_TITLE_COLORS.length];
        for (int i = 0; i < WINDOW_INACTIVE_TITLE_COLORS.length; i++) {
            WINDOW_INACTIVE_TITLE_COLORS[i] = ColorHelper.brighter(WINDOW_TITLE_COLORS[i], 20);
        }

        TOOLBAR_COLORS = ColorHelper.createColorArr(toolbarColorLight, toolbarColorDark, 24);
        MENUBAR_COLORS = ColorHelper.createColorArr(menuColorLight, menuColorDark, 24);

        TAB_COLORS = BUTTON_COLORS;
        COL_HEADER_COLORS = BUTTON_COLORS;
        Color hiColor = backgroundColorLight;
        Color loColor = ColorHelper.darker(backgroundColor, 4);
        TRACK_COLORS = ColorHelper.createColorArr(loColor, hiColor, 24);
        THUMB_COLORS = DEFAULT_COLORS;

        SLIDER_COLORS = DEFAULT_COLORS;
        PROGRESSBAR_COLORS = DEFAULT_COLORS;
    }
}
