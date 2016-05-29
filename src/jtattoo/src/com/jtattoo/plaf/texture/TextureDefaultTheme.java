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
 
package com.jtattoo.plaf.texture;

import com.jtattoo.plaf.AbstractTheme;
import com.jtattoo.plaf.ColorHelper;
import java.awt.Color;
import java.awt.Font;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;

public class TextureDefaultTheme extends AbstractTheme {

    public TextureDefaultTheme() {
        super();
        // Setup theme with defaults
        setUpColor();
        // Overwrite defaults with user props
        loadProperties();
        // Setup the color arrays
        setUpColorArrs();
    }

    public String getPropertyFileName() {
        return "TextureTheme.properties";
    }

    public void setUpColor() {
        super.setUpColor();

        menuOpaque = false;
        menuAlpha = 1.0f;
        showFocusFrame = true;
        textShadow = true;

        foregroundColor = black;
        backgroundColor = new ColorUIResource(242, 232, 220);
        backgroundColorLight = new ColorUIResource(224, 215, 205);
        backgroundColorDark = new ColorUIResource(197, 184, 168);

        alterBackgroundColor = new ColorUIResource(219, 206, 189);
        disabledForegroundColor = new ColorUIResource(114, 94, 80);
        disabledBackgroundColor = new ColorUIResource(244, 242, 232);
        inputBackgroundColor = white;
        inputForegroundColor = black;

        selectionForegroundColor = black;
        selectionBackgroundColor = new ColorUIResource(200, 215, 240);
        selectionBackgroundColorLight = new ColorUIResource(128, 128, 128);
        selectionBackgroundColorDark = new ColorUIResource(64, 64, 64);

        focusColor = orange;
        focusCellColor = orange;
        frameColor = new ColorUIResource(172, 162, 145);
        gridColor = new ColorUIResource(204, 197, 183);

        rolloverColor = new ColorUIResource(176, 163, 138);
        rolloverColorLight = new ColorUIResource(226, 222, 214);
        rolloverColorDark = new ColorUIResource(208, 200, 185);

        buttonForegroundColor = black;
        buttonBackgroundColor = extraLightGray;
        buttonColorLight = new ColorUIResource(247, 244, 239);
        buttonColorDark = new ColorUIResource(219, 216, 211);

        controlForegroundColor = black;
        controlBackgroundColor = new ColorUIResource(240, 230, 217);
        controlHighlightColor = white;
        controlShadowColor = new ColorUIResource(172, 162, 145);
        controlDarkShadowColor = new ColorUIResource(100, 80, 60);
        controlColorLight = new ColorUIResource(239, 230, 221);
        controlColorDark = new ColorUIResource(220, 207, 192);

        windowTitleForegroundColor = white;
        windowTitleBackgroundColor = new ColorUIResource(200, 215, 240);
        windowTitleColorLight = new ColorUIResource(96, 96, 96);
        windowTitleColorDark = new ColorUIResource(48, 48, 48);
        windowBorderColor = black;
        windowIconColor = white;
        windowIconShadowColor = black;
        windowIconRolloverColor = orange;

        windowInactiveTitleForegroundColor = white;
        windowInactiveTitleBackgroundColor = new ColorUIResource(242, 232, 220);
        windowInactiveTitleColorLight = new ColorUIResource(96, 96, 96);
        windowInactiveTitleColorDark = new ColorUIResource(48, 48, 48);
        windowInactiveBorderColor = black;

        menuForegroundColor = white;
        menuBackgroundColor = new ColorUIResource(240, 240, 240);
        menuSelectionForegroundColor = black;
        menuSelectionBackgroundColor = new ColorUIResource(32, 32, 32);
        menuSelectionBackgroundColorLight = new ColorUIResource(255, 213, 113);
        menuSelectionBackgroundColorDark = new ColorUIResource(240, 168, 0);
        menuColorLight = new ColorUIResource(240, 240, 240);
        menuColorDark = new ColorUIResource(220, 220, 220);

        toolbarForegroundColor = white;
        toolbarBackgroundColor = new ColorUIResource(52, 52, 52);
        toolbarColorLight = menuColorLight;
        toolbarColorDark = menuColorDark;

        tabAreaBackgroundColor = backgroundColor;
        tabSelectionForegroundColor = white;
        
        desktopColor = new ColorUIResource(220, 207, 192);
        tooltipForegroundColor = black;
        tooltipBackgroundColor = new ColorUIResource(244, 235, 228);

        controlFont = new FontUIResource("Dialog", Font.PLAIN, 13);
        systemFont = new FontUIResource("Dialog", Font.PLAIN, 13);
        userFont = new FontUIResource("Dialog", Font.PLAIN, 13);
        smallFont = new FontUIResource("Dialog", Font.PLAIN, 12);
        menuFont = new FontUIResource("Dialog", Font.PLAIN, 13);
        windowTitleFont = new FontUIResource("Dialog", Font.BOLD, 13);
    }

    public void setUpColorArrs() {
        super.setUpColorArrs();

        Color topHi = white;
        Color topLo = buttonColorLight;
        Color bottomHi = ColorHelper.brighter(buttonColorDark, 40);
        Color bottomLo = buttonColorDark;
        Color topColors[] = ColorHelper.createColorArr(topHi, topLo, 11);
        Color bottomColors[] = ColorHelper.createColorArr(bottomHi, bottomLo, 13);
        BUTTON_COLORS = new Color[24];
        System.arraycopy(topColors, 0, BUTTON_COLORS, 0, 11);
        System.arraycopy(bottomColors, 0, BUTTON_COLORS, 11, 13);
        CHECKBOX_COLORS = BUTTON_COLORS;

        DEFAULT_COLORS = ColorHelper.createColorArr(controlColorLight, controlColorDark, 24);
        HIDEFAULT_COLORS = new Color[20];
        for (int i = 0; i < 20; i++) {
            HIDEFAULT_COLORS[i] = ColorHelper.brighter(DEFAULT_COLORS[i], 40);
        }
        SELECTED_COLORS = ColorHelper.createColorArr(selectionBackgroundColorLight, selectionBackgroundColorDark, 20);
        SELECTION_COLORS = SELECTED_COLORS;//ColorHelper.createColorArr(selectionBackgroundColorLight, selectionBackgroundColorDark, 20);
        MENU_SELECTION_COLORS = ColorHelper.createColorArr(menuSelectionBackgroundColorLight, menuSelectionBackgroundColorDark, 20);

        topHi = rolloverColorLight;
        topLo = rolloverColorDark;
        bottomHi = ColorHelper.darker(rolloverColorDark, 6);
        bottomLo = ColorHelper.darker(rolloverColorDark, 10);
        topColors = ColorHelper.createColorArr(topHi, topLo, 11);
        bottomColors = ColorHelper.createColorArr(bottomHi, bottomLo, 13);
        ROLLOVER_COLORS = new Color[24];
        System.arraycopy(topColors, 0, ROLLOVER_COLORS, 0, 11);
        System.arraycopy(bottomColors, 0, ROLLOVER_COLORS, 11, 13);
        PRESSED_COLORS = ColorHelper.createColorArr(buttonColorDark, buttonColorLight, 24);
        DISABLED_COLORS = ColorHelper.createColorArr(superLightGray, extraLightGray, 24);
        ACTIVE_COLORS = DEFAULT_COLORS;
        INACTIVE_COLORS = BUTTON_COLORS;

        WINDOW_TITLE_COLORS = ColorHelper.createColorArr(windowTitleColorLight, windowTitleColorDark, 24);
        WINDOW_INACTIVE_TITLE_COLORS = ColorHelper.createColorArr(windowInactiveTitleColorLight, windowInactiveTitleColorDark, 24);

        TOOLBAR_COLORS = ColorHelper.createColorArr(toolbarColorLight, toolbarColorDark, 24);
        MENUBAR_COLORS = ColorHelper.createColorArr(menuColorLight, menuColorDark, 24);

        TAB_COLORS = BUTTON_COLORS;//ColorHelper.createColorArr(controlColorLight, controlColorDark, 24);
        COL_HEADER_COLORS = BUTTON_COLORS;

        THUMB_COLORS = ColorHelper.createColorArr(backgroundColorLight, backgroundColorDark, 24);
        Color hiColor = ColorHelper.brighter(backgroundColor, 30);
        Color loColor = ColorHelper.brighter(backgroundColor, 10);
        TRACK_COLORS = ColorHelper.createColorArr(loColor, hiColor, 24);

        SLIDER_COLORS = THUMB_COLORS;
        PROGRESSBAR_COLORS = SLIDER_COLORS;
    }

}
