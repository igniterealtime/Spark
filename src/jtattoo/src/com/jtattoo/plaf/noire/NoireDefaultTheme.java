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
 
package com.jtattoo.plaf.noire;

import com.jtattoo.plaf.AbstractTheme;
import com.jtattoo.plaf.ColorHelper;
import java.awt.Color;
import java.awt.Font;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;

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
        buttonBackgroundColor = new ColorUIResource(120, 129, 148);
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
        windowTitleBackgroundColor = new ColorUIResource(144, 148, 149);
        windowTitleColorLight = new ColorUIResource(64, 67, 60);
        windowTitleColorDark = black;
        windowBorderColor = black;
        windowIconColor = lightGray;
        windowIconShadowColor = black;
        windowIconRolloverColor = orange;

        windowInactiveTitleForegroundColor = new ColorUIResource(196, 196, 196);
        windowInactiveTitleBackgroundColor = new ColorUIResource(64, 64, 64);
        windowInactiveTitleColorLight = new ColorUIResource(64, 64, 64);
        windowInactiveTitleColorDark = new ColorUIResource(32, 32, 32);
        windowInactiveBorderColor = black;

        menuForegroundColor = white;
        menuBackgroundColor = new ColorUIResource(24, 26, 28);
        menuSelectionForegroundColor = black;
        menuSelectionBackgroundColor = new ColorUIResource(196, 137, 0);
        menuColorLight = new ColorUIResource(96, 96, 96);
        menuColorDark = new ColorUIResource(32, 32, 32);

        toolbarBackgroundColor = new ColorUIResource(24, 26, 28);
        toolbarColorLight = new ColorUIResource(96, 96, 96);
        toolbarColorDark = new ColorUIResource(32, 32, 32);

        tabAreaBackgroundColor = backgroundColor;
        desktopColor = new ColorUIResource(52, 55, 59);
        
        tooltipForegroundColor = white;
        tooltipBackgroundColor = black;//new ColorUIResource(16, 16, 16);

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
        Color bottomColors[] = ColorHelper.createColorArr(bottomHi, bottomLo, 12);
        BUTTON_COLORS = new Color[22];
        System.arraycopy(topColors, 0, BUTTON_COLORS, 0, 10);
        System.arraycopy(bottomColors, 0, BUTTON_COLORS, 10, 12);

        topHi = ColorHelper.brighter(controlColorLight, 40);
        topLo = ColorHelper.brighter(controlColorDark, 40);
        bottomHi = controlColorLight;
        bottomLo = controlColorDark;
        topColors = ColorHelper.createColorArr(topHi, topLo, 10);
        bottomColors = ColorHelper.createColorArr(bottomHi, bottomLo, 12);
        DEFAULT_COLORS = new Color[22];
        System.arraycopy(topColors, 0, DEFAULT_COLORS, 0, 10);
        System.arraycopy(bottomColors, 0, DEFAULT_COLORS, 10, 12);
        
        HIDEFAULT_COLORS = ColorHelper.createColorArr(ColorHelper.brighter(controlColorLight, 15), ColorHelper.brighter(controlColorDark, 15), 20);
        ACTIVE_COLORS = ColorHelper.createColorArr(controlColorLight, controlColorDark, 20);
        INACTIVE_COLORS = ColorHelper.createColorArr(new Color(64, 64, 64), new Color(32, 32, 32), 20);

        SELECTED_COLORS = BUTTON_COLORS;

        topHi = ColorHelper.brighter(rolloverColorLight, 40);
        topLo = rolloverColorLight;
        bottomHi = rolloverColorDark;
        bottomLo = ColorHelper.darker(rolloverColorDark, 20);
        topColors = ColorHelper.createColorArr(topHi, topLo, 10);
        bottomColors = ColorHelper.createColorArr(bottomHi, bottomLo, 12);
        ROLLOVER_COLORS = new Color[22];
        System.arraycopy(topColors, 0, ROLLOVER_COLORS, 0, 10);
        System.arraycopy(bottomColors, 0, ROLLOVER_COLORS, 10, 12);
        
        PRESSED_COLORS = ColorHelper.createColorArr(new Color(64, 64, 64), new Color(96, 96, 96), 20);
        DISABLED_COLORS = ColorHelper.createColorArr(new Color(80, 80, 80), new Color(64, 64, 64), 20);
        topHi = ColorHelper.brighter(windowTitleColorLight, 40);
        topLo = ColorHelper.brighter(windowTitleColorDark, 40);
        bottomHi = windowTitleColorLight;
        bottomLo = windowTitleColorDark;
        topColors = ColorHelper.createColorArr(topHi, topLo, 8);
        bottomColors = ColorHelper.createColorArr(bottomHi, bottomLo, 12);
        WINDOW_TITLE_COLORS = new Color[20];
        System.arraycopy(topColors, 0, WINDOW_TITLE_COLORS, 0, 8);
        System.arraycopy(bottomColors, 0, WINDOW_TITLE_COLORS, 8, 12);
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
