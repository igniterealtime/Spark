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
 
package com.jtattoo.plaf.smart;

import com.jtattoo.plaf.AbstractTheme;
import com.jtattoo.plaf.ColorHelper;
import java.awt.Color;
import javax.swing.plaf.ColorUIResource;

public class SmartDefaultTheme extends AbstractTheme {

    public SmartDefaultTheme() {
        super();
        // Setup theme with defaults
        setUpColor();
        // Overwrite defaults with user props
        loadProperties();
        // Setup the color arrays
        setUpColorArrs();
    }

    public String getPropertyFileName() {
        return "SmartTheme.properties";
    }

    public void setUpColor() {
        super.setUpColor();

        // Defaults for SmartLookAndFeel
        backgroundColor = new ColorUIResource(244, 242, 232);
        backgroundColorLight = new ColorUIResource(255, 255, 255);
        backgroundColorDark = new ColorUIResource(236, 233, 215);
        alterBackgroundColor = new ColorUIResource(236, 233, 215);

        selectionBackgroundColor = new ColorUIResource(201, 218, 254);
        selectionBackgroundColorLight = new ColorUIResource(201, 218, 254);
        selectionBackgroundColorDark = new ColorUIResource(201, 218, 254);
        frameColor = new ColorUIResource(128, 124, 112);
        focusCellColor = new ColorUIResource(255, 230, 120);

        buttonBackgroundColor = new ColorUIResource(218, 230, 254);
        buttonColorLight = white;
        buttonColorDark = backgroundColor;

        rolloverColor = new ColorUIResource(218, 230, 254);
        rolloverColorLight = new ColorUIResource(236, 242, 255);
        rolloverColorDark = new ColorUIResource(191, 211, 253);

        controlForegroundColor = black;
        controlBackgroundColor = backgroundColor;
        controlColorLight = new ColorUIResource(218, 230, 254);
        controlColorDark = new ColorUIResource(180, 197, 240);
        controlShadowColor = new ColorUIResource(164, 164, 164);
        controlDarkShadowColor = new ColorUIResource(148, 148, 148);

        windowTitleBackgroundColor = new ColorUIResource(180, 197, 240);
        windowTitleColorLight = new ColorUIResource(218, 230, 254);
        windowTitleColorDark = new ColorUIResource(180, 197, 240);
        windowBorderColor = new ColorUIResource(128, 129, 132);

        windowInactiveTitleBackgroundColor = backgroundColor;
        windowInactiveTitleColorLight = new ColorUIResource(244, 242, 234);
        windowInactiveTitleColorDark = new ColorUIResource(230, 224, 202);
        windowInactiveBorderColor = new ColorUIResource(164, 165, 169);

        menuBackgroundColor = new ColorUIResource(248, 247, 239);
        menuSelectionForegroundColor = black;
        menuSelectionBackgroundColor = selectionBackgroundColor;
        menuColorLight = new ColorUIResource(248, 245, 235);
        menuColorDark = backgroundColor;

        toolbarBackgroundColor = backgroundColor;
        toolbarColorLight = menuColorLight;
        toolbarColorDark = menuColorDark;

        tabAreaBackgroundColor = backgroundColor;
        desktopColor = backgroundColor;
    }

    public void setUpColorArrs() {
        super.setUpColorArrs();

        // Generate the color arrays
        DEFAULT_COLORS = ColorHelper.createColorArr(controlColorLight, controlColorDark, 20);
        HIDEFAULT_COLORS = ColorHelper.createColorArr(ColorHelper.brighter(controlColorLight, 90), ColorHelper.brighter(controlColorDark, 30), 20);

        ACTIVE_COLORS = DEFAULT_COLORS;
        INACTIVE_COLORS = ColorHelper.createColorArr(Color.white, backgroundColor, 20);

        ROLLOVER_COLORS = ColorHelper.createColorArr(rolloverColorLight, rolloverColorDark, 20);
        SELECTED_COLORS = DEFAULT_COLORS;
        PRESSED_COLORS = ColorHelper.createColorArr(controlColorDark, controlColorLight, 20);
        DISABLED_COLORS = ColorHelper.createColorArr(Color.white, new Color(230, 230, 230), 20);

        WINDOW_TITLE_COLORS = ColorHelper.createColorArr(windowTitleColorLight, windowTitleColorDark, 20);
        WINDOW_INACTIVE_TITLE_COLORS = ColorHelper.createColorArr(windowInactiveTitleColorLight, windowInactiveTitleColorDark, 20);
        MENUBAR_COLORS = ColorHelper.createColorArr(menuColorLight, menuColorDark, 20);
        TOOLBAR_COLORS = ColorHelper.createColorArr(toolbarColorLight, toolbarColorDark, 20);

        BUTTON_COLORS = ColorHelper.createColorArr(buttonColorLight, buttonColorDark, 20);
        TAB_COLORS = ColorHelper.createColorArr(Color.white, backgroundColor, 20);
        CHECKBOX_COLORS = TAB_COLORS;
        COL_HEADER_COLORS = MENUBAR_COLORS;
        TRACK_COLORS = ColorHelper.createColorArr(backgroundColor, Color.white, 16);
        THUMB_COLORS = DEFAULT_COLORS;
        SLIDER_COLORS = DEFAULT_COLORS;
        PROGRESSBAR_COLORS = DEFAULT_COLORS;
    }
}
