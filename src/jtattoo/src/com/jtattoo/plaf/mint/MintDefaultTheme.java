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
 
package com.jtattoo.plaf.mint;

import com.jtattoo.plaf.AbstractTheme;
import com.jtattoo.plaf.ColorHelper;
import java.awt.Color;
import java.awt.Font;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;

public class MintDefaultTheme extends AbstractTheme {

    public MintDefaultTheme() {
        super();
        // Setup theme with defaults
        setUpColor();
        // Overwrite defaults with user props
        loadProperties();
        // Setup the color arrays
        setUpColorArrs();
    }

    public String getPropertyFileName() {
        return "MintTheme.properties";
    }

    public void setUpColor() {
        super.setUpColor();

        // Defaults for MintLookAndFeel
        backgroundColor = new ColorUIResource(236, 242, 242);
        backgroundColorLight = new ColorUIResource(236, 242, 242);
        backgroundColorDark = new ColorUIResource(220, 228, 228);
        alterBackgroundColor = new ColorUIResource(220, 228, 228);
        selectionBackgroundColor = new ColorUIResource(220, 228, 228);
        selectionForegroundColor = black;
        frameColor = new ColorUIResource(140, 140, 140);
        focusCellColor = focusColor;

        rolloverColor = new ColorUIResource(255, 230, 170);
        rolloverColorLight = new ColorUIResource(255, 230, 170);
        rolloverColorDark = new ColorUIResource(255, 191, 43);

        buttonBackgroundColor = new ColorUIResource(240, 248, 248);
        buttonColorLight = white;
        buttonColorDark = new ColorUIResource(220, 228, 228);

        controlBackgroundColor = backgroundColor; // netbeans use this for selected tab in the toolbar
        controlColorLight = new ColorUIResource(223, 234, 234);
        controlColorDark = new ColorUIResource(180, 203, 203);
        controlHighlightColor = white;
        controlShadowColor = new ColorUIResource(172, 186, 186);
        controlDarkShadowColor = new ColorUIResource(160, 164, 164);

        windowTitleForegroundColor = black;
        windowTitleBackgroundColor = new ColorUIResource(180, 203, 203);//new ColorUIResource(180, 203, 203);
        windowTitleColorLight = new ColorUIResource(223, 234, 234);
        windowTitleColorDark = new ColorUIResource(180, 203, 203);
        windowBorderColor = new ColorUIResource(154, 186, 186);

        windowInactiveTitleBackgroundColor = new ColorUIResource(220, 228, 228);//new ColorUIResource(220, 220, 220);
        windowInactiveTitleColorLight = backgroundColor;
        windowInactiveTitleColorDark = new ColorUIResource(220, 228, 228);
        windowInactiveBorderColor = new ColorUIResource(192, 211, 211);

        menuBackgroundColor = white;//backgroundColor;
        menuSelectionBackgroundColor = selectionBackgroundColor;
        menuSelectionForegroundColor = selectionForegroundColor;
        menuColorLight = backgroundColor;
        menuColorDark = new ColorUIResource(220, 228, 228);

        toolbarBackgroundColor = backgroundColor;
        toolbarColorLight = menuColorLight;
        toolbarColorDark = menuColorDark;

        tabAreaBackgroundColor = backgroundColor;
        desktopColor = new ColorUIResource(220, 228, 228);
    }

    public void setUpColorArrs() {
        super.setUpColorArrs();

        // Generate the color arrays
        DEFAULT_COLORS = ColorHelper.createColorArr(controlColorLight, controlColorDark, 20);
        HIDEFAULT_COLORS = ColorHelper.createColorArr(ColorHelper.brighter(controlColorLight, 40), ColorHelper.brighter(controlColorDark, 40), 20);
        ACTIVE_COLORS = DEFAULT_COLORS;
        ROLLOVER_COLORS = ColorHelper.createColorArr(rolloverColorLight, rolloverColorDark, 20);
        SELECTED_COLORS = DEFAULT_COLORS;
        PRESSED_COLORS = ColorHelper.createColorArr(ColorHelper.darker(controlColorLight, 10), ColorHelper.darker(controlColorDark, 10), 20);
        DISABLED_COLORS = ColorHelper.createColorArr(new Color(248, 248, 248), new Color(224, 224, 224), 20);
        WINDOW_TITLE_COLORS = ColorHelper.createColorArr(windowTitleColorLight, windowTitleColorDark, 20);
        WINDOW_INACTIVE_TITLE_COLORS = ColorHelper.createColorArr(windowInactiveTitleColorLight, windowInactiveTitleColorDark, 20);
        MENUBAR_COLORS = ColorHelper.createColorArr(menuColorLight, menuColorDark, 20);
        TOOLBAR_COLORS = ColorHelper.createColorArr(toolbarColorLight, toolbarColorDark, 20);
        BUTTON_COLORS = new Color[]{
                    new Color(255, 255, 255),
                    new Color(254, 255, 254),
                    new Color(253, 255, 254),
                    new Color(252, 255, 254),
                    new Color(251, 254, 253),
                    new Color(250, 253, 252),
                    new Color(250, 253, 252),
                    new Color(248, 250, 249),
                    new Color(244, 248, 246),
                    new Color(240, 245, 243),
                    new Color(238, 242, 240),
                    new Color(232, 237, 235),
                    new Color(232, 237, 235),
                    new Color(224, 230, 227),
                    new Color(214, 221, 217),
                    new Color(204, 212, 208),
                    new Color(160, 164, 162),};
        TAB_COLORS = ColorHelper.createColorArr(buttonColorLight, buttonColorDark, 20);
        CHECKBOX_COLORS = TAB_COLORS;
        COL_HEADER_COLORS = ColorHelper.createColorArr(buttonColorLight, buttonColorDark, 20);
        TRACK_COLORS = ColorHelper.createColorArr(new Color(238, 238, 238), Color.white, 20);
        THUMB_COLORS = DEFAULT_COLORS;
        SLIDER_COLORS = DEFAULT_COLORS;
        PROGRESSBAR_COLORS = DEFAULT_COLORS;
        INACTIVE_COLORS = ColorHelper.createColorArr(buttonColorLight, buttonColorDark, 20);
    }

    public FontUIResource getControlTextFont() {
        if (controlFont == null) {
            controlFont = new FontUIResource("Dialog", Font.PLAIN, 12);
        }
        return controlFont;
    }

    public FontUIResource getSystemTextFont() {
        if (systemFont == null) {
            systemFont = new FontUIResource("Dialog", Font.PLAIN, 12);
        }
        return systemFont;
    }

    public FontUIResource getUserTextFont() {
        if (userFont == null) {
            userFont = new FontUIResource("Dialog", Font.PLAIN, 12);
        }
        return userFont;
    }

    public FontUIResource getMenuTextFont() {
        if (menuFont == null) {
            menuFont = new FontUIResource("Dialog", Font.PLAIN, 12);
        }
        return menuFont;
    }

    public FontUIResource getWindowTitleFont() {
        if (windowTitleFont == null) {
            windowTitleFont = new FontUIResource("Dialog", Font.BOLD, 12);
        }
        return windowTitleFont;
    }

    public FontUIResource getSubTextFont() {
        if (smallFont == null) {
            smallFont = new FontUIResource("Dialog", Font.PLAIN, 10);
        }
        return smallFont;
    }
}
