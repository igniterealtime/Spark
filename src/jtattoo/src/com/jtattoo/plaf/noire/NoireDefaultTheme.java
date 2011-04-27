/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.noire;

import javax.swing.plaf.*;
import java.awt.*;

import com.jtattoo.plaf.*;

/**
 * @author Michael Hagen
 */
public class NoireDefaultTheme extends AbstractTheme {

    public NoireDefaultTheme() {
        super();
        // Setup theme with defaults
        setUpColor();
        // Overwrite defaults with user props
        loadProperties();
        // Setup the color arrays
        setUpColorArrs();
    }

    public String getPropertyFileName() {
        return "NoireTheme.properties";
    }

    public void setUpColor() {
        super.setUpColor();

        // Defaults for NoireLookAndFeel
        textShadow = true;
        foregroundColor = white;
        disabledForegroundColor = gray;
        disabledBackgroundColor = new ColorUIResource(48, 48, 48);

        backgroundColor = new ColorUIResource(24, 26, 28);
        backgroundColorLight = new ColorUIResource(24, 26, 28);
        backgroundColorDark = new ColorUIResource(4, 5, 6);
        alterBackgroundColor = new ColorUIResource(78, 84, 90);

        selectionForegroundColor = new ColorUIResource(255, 220, 120);
        selectionBackgroundColor = black;
        frameColor = black;
        gridColor = black;
        focusCellColor = orange;

        inputBackgroundColor = new ColorUIResource(52, 55, 59);
        inputForegroundColor = foregroundColor;

        rolloverColor = new ColorUIResource(240, 168, 0);
        rolloverColorLight = new ColorUIResource(240, 168, 0);
        rolloverColorDark = new ColorUIResource(196, 137, 0);

        buttonForegroundColor = black;
        buttonBackgroundColor = new ColorUIResource(196, 196, 196);
        buttonColorLight = new ColorUIResource(232, 238, 244);
        buttonColorDark = new ColorUIResource(196, 200, 208);

        controlForegroundColor = foregroundColor;
        controlBackgroundColor = new ColorUIResource(52, 55, 59); // netbeans use this for selected tab in the toolbar
        controlColorLight = new ColorUIResource(44, 47, 50);
        controlColorDark = new ColorUIResource(16, 18, 20);
        controlHighlightColor = new ColorUIResource(96, 96, 96);
        controlShadowColor = new ColorUIResource(32, 32, 32);
        controlDarkShadowColor = black;

        windowTitleForegroundColor = foregroundColor;
        windowTitleBackgroundColor = new ColorUIResource(144, 148, 149);//new ColorUIResource(124, 128, 129);
        windowTitleColorLight = new ColorUIResource(64, 67, 60);//new ColorUIResource(44, 47, 50);
        windowTitleColorDark = black;//new ColorUIResource(16, 18, 20);
        windowBorderColor = new ColorUIResource(16, 18, 20);//new ColorUIResource(26, 28, 30);
        windowIconColor = lightGray;
        windowIconShadowColor = black;
        windowIconRolloverColor = orange;

        windowInactiveTitleForegroundColor = new ColorUIResource(196, 196, 196);
        windowInactiveTitleBackgroundColor = new ColorUIResource(64, 64, 64);
        windowInactiveTitleColorLight = new ColorUIResource(64, 64, 64);
        windowInactiveTitleColorDark = new ColorUIResource(32, 32, 32);
        windowInactiveBorderColor = new ColorUIResource(48, 48, 48);

        menuForegroundColor = white;
        menuBackgroundColor = new ColorUIResource(24, 26, 28);
        menuSelectionForegroundColor = black;
        menuSelectionBackgroundColor = new ColorUIResource(196, 137, 0);
        menuColorLight = new ColorUIResource(96, 96, 96);
        menuColorDark = new ColorUIResource(32, 32, 32);

        toolbarBackgroundColor = new ColorUIResource(64, 64, 64);
        toolbarColorLight = new ColorUIResource(96, 96, 96);
        toolbarColorDark = new ColorUIResource(32, 32, 32);

        tabAreaBackgroundColor = backgroundColor;
        desktopColor = new ColorUIResource(52, 55, 59);

        controlFont = new FontUIResource("Dialog", Font.BOLD, 12);
        systemFont = new FontUIResource("Dialog", Font.BOLD, 12);
        userFont = new FontUIResource("Dialog", Font.BOLD, 12);
        menuFont = new FontUIResource("Dialog", Font.BOLD, 12);
        windowTitleFont = new FontUIResource("Dialog", Font.BOLD, 12);
        smallFont = new FontUIResource("Dialog", Font.PLAIN, 10);
    }

    public void setUpColorArrs() {
        super.setUpColorArrs();
        Color topHi = ColorHelper.brighter(buttonColorLight, 50);
        Color topLo = buttonColorLight;
        Color bottomHi = buttonColorDark;
        Color bottomLo = ColorHelper.darker(buttonColorDark, 40);
        Color topColors[] = ColorHelper.createColorArr(topHi, topLo, 10);
        Color bottomColors[] = ColorHelper.createColorArr(bottomHi, bottomLo, 10);
        BUTTON_COLORS = new Color[20];
        for (int i = 0; i < 10; i++) {
            BUTTON_COLORS[i] = topColors[i];
            BUTTON_COLORS[i + 10] = bottomColors[i];
        }

        topHi = ColorHelper.brighter(controlColorLight, 40);
        topLo = ColorHelper.brighter(controlColorDark, 40);
        bottomHi = controlColorLight;
        bottomLo = controlColorDark;
        topColors = ColorHelper.createColorArr(topHi, topLo, 10);
        bottomColors = ColorHelper.createColorArr(bottomHi, bottomLo, 10);
        DEFAULT_COLORS = new Color[20];
        for (int i = 0; i < 10; i++) {
            DEFAULT_COLORS[i] = topColors[i];
            DEFAULT_COLORS[i + 10] = bottomColors[i];
        }
        HIDEFAULT_COLORS = ColorHelper.createColorArr(ColorHelper.brighter(controlColorLight, 15), ColorHelper.brighter(controlColorDark, 15), 20);
        ACTIVE_COLORS = ColorHelper.createColorArr(controlColorLight, controlColorDark, 20);
        INACTIVE_COLORS = ColorHelper.createColorArr(new Color(64, 64, 64), new Color(32, 32, 32), 20);

        SELECTED_COLORS = BUTTON_COLORS;
        //SELECTED_COLORS = ColorHelper.createColorArr(new Color(255, 0, 0), new Color(128, 0, 0), 20);

        topHi = ColorHelper.brighter(rolloverColorLight, 40);
        topLo = rolloverColorLight;
        bottomHi = rolloverColorDark;
        bottomLo = ColorHelper.darker(rolloverColorDark, 20);
        topColors = ColorHelper.createColorArr(topHi, topLo, 10);
        bottomColors = ColorHelper.createColorArr(bottomHi, bottomLo, 10);
        ROLLOVER_COLORS = new Color[20];
        for (int i = 0; i < 10; i++) {
            ROLLOVER_COLORS[i] = topColors[i];
            ROLLOVER_COLORS[i + 10] = bottomColors[i];
        }
        PRESSED_COLORS = ColorHelper.createColorArr(new Color(64, 64, 64), new Color(96, 96, 96), 20);
        DISABLED_COLORS = ColorHelper.createColorArr(new Color(80, 80, 80), new Color(64, 64, 64), 20);
        topHi = ColorHelper.brighter(windowTitleColorLight, 40);
        topLo = ColorHelper.brighter(windowTitleColorDark, 40);
        bottomHi = windowTitleColorLight;
        bottomLo = windowTitleColorDark;
        topColors = ColorHelper.createColorArr(topHi, topLo, 8);
        bottomColors = ColorHelper.createColorArr(bottomHi, bottomLo, 12);
        WINDOW_TITLE_COLORS = new Color[20];
        for (int i = 0; i < 8; i++) {
            WINDOW_TITLE_COLORS[i] = topColors[i];
        }
        for (int i = 0; i < 12; i++) {
            WINDOW_TITLE_COLORS[i + 8] = bottomColors[i];
        }
        WINDOW_INACTIVE_TITLE_COLORS = ColorHelper.createColorArr(windowInactiveTitleColorLight, windowInactiveTitleColorDark, 20);
        MENUBAR_COLORS = DEFAULT_COLORS;
        TOOLBAR_COLORS = MENUBAR_COLORS;
        SLIDER_COLORS = BUTTON_COLORS;
        PROGRESSBAR_COLORS = DEFAULT_COLORS;
        //THUMB_COLORS = ColorHelper.createColorArr(controlColorLight, controlColorDark, 20);
        THUMB_COLORS = DEFAULT_COLORS;
        //TRACK_COLORS = ColorHelper.createColorArr(ColorHelper.darker(backgroundColor, 10), ColorHelper.brighter(backgroundColor, 5), 20);
        TRACK_COLORS = ColorHelper.createColorArr(ColorHelper.darker(inputBackgroundColor, 5), ColorHelper.brighter(inputBackgroundColor, 10), 20);

        TAB_COLORS = DEFAULT_COLORS;
        COL_HEADER_COLORS = DEFAULT_COLORS;
    }
}
