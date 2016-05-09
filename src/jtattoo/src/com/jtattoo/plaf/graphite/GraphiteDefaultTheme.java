/*
* Copyright (c) 2002 and later by MH Software-Entwicklung. All Rights Reserved.
*  
* JTattoo is multiple licensed. If your are an open source developer you can use
* it under the terms and conditions of the GNU General Public License version 2.0
* or later as published by the Free Software Foundation.
*  
* see: gpl-2.0.txt
* 
* If you pay for a license you will become a registered user who could use the
* software under the terms and conditions of the GNU Lesser General Public License
* version 2.0 or later with classpath exception as published by the Free Software
* Foundation.
* 
* see: lgpl-2.0.txt
* see: classpath-exception.txt
* 
* Registered users could also use JTattoo under the terms and conditions of the 
* Apache License, Version 2.0 as published by the Apache Software Foundation.
*  
* see: APACHE-LICENSE-2.0.txt
*/
 
package com.jtattoo.plaf.graphite;

import com.jtattoo.plaf.*;
import java.awt.Color;
import java.awt.Font;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;

public class GraphiteDefaultTheme extends AbstractTheme {

    public GraphiteDefaultTheme() {
        super();
        // Setup theme with defaults
        setUpColor();
        // Overwrite defaults with user props
        loadProperties();
        // Setup the color arrays
        setUpColorArrs();
    }

    public String getPropertyFileName() {
        return "GraphiteTheme.properties";
    }

    public void setUpColor() {
        super.setUpColor();

//        showFocusFrame = true;

        foregroundColor = black;
        backgroundColor = new ColorUIResource(224, 224, 224);
        backgroundColorLight = white;
        backgroundColorDark = lightGray;
        alterBackgroundColor = new ColorUIResource(220, 220, 220);
        disabledForegroundColor = new ColorUIResource(128, 128, 128);
        disabledBackgroundColor = new ColorUIResource(240, 240, 240);
        inputBackgroundColor = white;
        inputForegroundColor = black;

        selectionForegroundColor = white;
        selectionBackgroundColor = new ColorUIResource(80, 80, 80);
        selectionBackgroundColorLight = new ColorUIResource(96, 96, 96);
        selectionBackgroundColorDark = new ColorUIResource(64, 64, 64);

        focusColor = orange;
        focusCellColor = orange;
        frameColor = new ColorUIResource(144, 144, 144);
        gridColor = new ColorUIResource(200, 200, 200);

        rolloverColor = orange;
        rolloverColorLight = new ColorUIResource(255, 213, 113);
        rolloverColorDark = new ColorUIResource(240, 168, 0);

        buttonForegroundColor = black;
        buttonBackgroundColor = extraLightGray;
        buttonColorLight = white;
        buttonColorDark = extraLightGray;

        controlForegroundColor = black;
        controlBackgroundColor = extraLightGray;
        controlShadowColor = new ColorUIResource(164, 164, 164);
        controlDarkShadowColor = new ColorUIResource(64, 64, 64);
        controlColorLight = new ColorUIResource(96, 96, 96);
        controlColorDark = new ColorUIResource(40, 40, 40);

        windowTitleForegroundColor = white;
        windowTitleBackgroundColor = gray;
        windowTitleColorLight = new ColorUIResource(88, 88, 88);
        windowTitleColorDark = new ColorUIResource(24, 24, 24);
        windowBorderColor = black;
        windowIconColor = white;
        windowIconShadowColor = black;
        windowIconRolloverColor = orange;

        windowInactiveTitleForegroundColor = white;
        windowInactiveTitleBackgroundColor = new ColorUIResource(224, 224, 224);
        windowInactiveTitleColorLight = new ColorUIResource(64, 64, 64);
        windowInactiveTitleColorDark = new ColorUIResource(0, 0, 0);
        windowInactiveBorderColor = black;

        menuForegroundColor = black;
        menuBackgroundColor = new ColorUIResource(240, 240, 240);
        menuSelectionForegroundColor = black;
        menuSelectionBackgroundColor = new ColorUIResource(32, 32, 32);
        menuSelectionBackgroundColorLight = new ColorUIResource(255, 213, 113);
        menuSelectionBackgroundColorDark = new ColorUIResource(240, 168, 0);
        menuColorLight = new ColorUIResource(240, 240, 240);
        menuColorDark = new ColorUIResource(220, 220, 220);

        toolbarForegroundColor = black;
        toolbarBackgroundColor = backgroundColor;
        toolbarColorLight = menuColorLight;
        toolbarColorDark = menuColorDark;

        tabAreaBackgroundColor = backgroundColor;
        tabSelectionForegroundColor = white;
        desktopColor = backgroundColor;
        tooltipForegroundColor = black;
        tooltipBackgroundColor = yellow;

        controlFont = new FontUIResource("Dialog", Font.PLAIN, 13);
        systemFont = new FontUIResource("Dialog", Font.PLAIN, 13);
        userFont = new FontUIResource("Dialog", Font.PLAIN, 13);
        smallFont = new FontUIResource("Dialog", Font.PLAIN, 12);
        menuFont = new FontUIResource("Dialog", Font.PLAIN, 13);
        windowTitleFont = new FontUIResource("Dialog", Font.BOLD, 13);
    }

    public void setUpColorArrs() {
        super.setUpColorArrs();
        BUTTON_COLORS = ColorHelper.createColorArr(buttonColorLight, buttonColorDark, 24);
        CHECKBOX_COLORS = BUTTON_COLORS;
        DEFAULT_COLORS = ColorHelper.createColorArr(controlColorLight, controlColorDark, 24);
        HIDEFAULT_COLORS = new Color[20];
        for (int i = 0; i < 20; i++) {
            HIDEFAULT_COLORS[i] = ColorHelper.brighter(DEFAULT_COLORS[i], 40);
        }
        SELECTED_COLORS = DEFAULT_COLORS;
        SELECTION_COLORS = ColorHelper.createColorArr(selectionBackgroundColorLight, selectionBackgroundColorDark, 20);
        MENU_SELECTION_COLORS = ColorHelper.createColorArr(menuSelectionBackgroundColorLight, menuSelectionBackgroundColorDark, 20);
        ROLLOVER_COLORS = ColorHelper.createColorArr(rolloverColorLight, rolloverColorDark, 24);
        PRESSED_COLORS = ColorHelper.createColorArr(menuColorDark, menuColorLight, 24);
        DISABLED_COLORS = ColorHelper.createColorArr(superLightGray, extraLightGray, 24);
        ACTIVE_COLORS = DEFAULT_COLORS;
        INACTIVE_COLORS = BUTTON_COLORS;

        WINDOW_TITLE_COLORS = ColorHelper.createColorArr(windowTitleColorLight, windowTitleColorDark, 24);
        WINDOW_INACTIVE_TITLE_COLORS = ColorHelper.createColorArr(windowInactiveTitleColorLight, windowInactiveTitleColorDark, 24);

        TOOLBAR_COLORS = ColorHelper.createColorArr(toolbarColorLight, toolbarColorDark, 24);
        MENUBAR_COLORS = ColorHelper.createColorArr(menuColorLight, menuColorDark, 24);

        TAB_COLORS = BUTTON_COLORS;
        COL_HEADER_COLORS = BUTTON_COLORS;
        Color hiColor = backgroundColorLight;
        Color loColor = ColorHelper.darker(backgroundColor, 4);
        TRACK_COLORS = ColorHelper.createColorArr(loColor, hiColor, 24);
        if ("true".equals(System.getProperty("graphiteBrightThumb"))) {
            hiColor = backgroundColorDark;
            loColor = ColorHelper.darker(backgroundColorDark, 10);
            THUMB_COLORS = ColorHelper.createColorArr(hiColor, loColor, 24);
        } else {
            THUMB_COLORS = DEFAULT_COLORS;
        }

        SLIDER_COLORS = DEFAULT_COLORS;
        PROGRESSBAR_COLORS = DEFAULT_COLORS;
    }


}
