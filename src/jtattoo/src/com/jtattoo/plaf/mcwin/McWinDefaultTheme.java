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
 
package com.jtattoo.plaf.mcwin;

import com.jtattoo.plaf.AbstractTheme;
import com.jtattoo.plaf.ColorHelper;
import java.awt.Color;
import javax.swing.plaf.ColorUIResource;

/**
 * @author Michael Hagen
 */
public class McWinDefaultTheme extends AbstractTheme {

    public McWinDefaultTheme() {
        super();
        // Setup theme with defaults
        setUpColor();
        // Overwrite defaults with user props
        loadProperties();
        // Setup the color arrays
        setUpColorArrs();
    }

    public String getPropertyFileName() {
        return "McWinTheme.properties";
    }

    public void setUpColor() {
        super.setUpColor();

        // Defaults for McWinLookAndFeel
        menuOpaque = false;
        menuAlpha = 0.85f;

        backgroundColor = superLightGray;
        backgroundColorLight = white;
        backgroundColorDark = new ColorUIResource(240, 240, 240);
        alterBackgroundColor = new ColorUIResource(240, 240, 240);

        selectionBackgroundColor = new ColorUIResource(212, 224, 243);

        frameColor = new ColorUIResource(140, 144, 148);
        focusCellColor = orange;

        buttonBackgroundColor = superLightGray;

        controlBackgroundColor = superLightGray;
        controlColorLight = new ColorUIResource(106, 150, 192);
        controlColorDark = lightGray;

        rolloverColor = new ColorUIResource(164, 217, 190);
        rolloverColorLight = new ColorUIResource(182, 224, 203);
        rolloverColorDark = new ColorUIResource(106, 192, 150);

        windowTitleForegroundColor = new ColorUIResource(22, 34, 44);
        windowTitleBackgroundColor = new ColorUIResource(212, 224, 243);
        windowTitleColorLight = new ColorUIResource(231, 235, 248);
        windowTitleColorDark = new ColorUIResource(193, 211, 236);
        windowBorderColor = new ColorUIResource(154, 168, 182);

        windowInactiveTitleForegroundColor = extraDarkGray;
        windowInactiveTitleBackgroundColor = backgroundColor;
        windowInactiveTitleColorLight = white;
        windowInactiveTitleColorDark = new ColorUIResource(236, 236, 236);
        windowInactiveBorderColor = lightGray;

        menuBackgroundColor = white;
        menuSelectionBackgroundColor = selectionBackgroundColor;

        toolbarBackgroundColor = white;

        tabAreaBackgroundColor = backgroundColor;
        desktopColor = new ColorUIResource(232, 232, 232);
    }

    public void setUpColorArrs() {
        super.setUpColorArrs();

        if (controlColorLight.equals(new ColorUIResource(106, 150, 192))) {
            // Generate the color arrays
            DEFAULT_COLORS = new Color[]{
                        new Color(106, 150, 192),
                        new Color(154, 190, 209),
                        new Color(182, 208, 231),
                        new Color(200, 223, 255),
                        new Color(189, 218, 246),
                        new Color(167, 204, 231),
                        new Color(148, 191, 226),
                        new Color(144, 181, 225),
                        new Color(145, 182, 226),
                        new Color(151, 188, 230),
                        new Color(160, 198, 235),
                        new Color(168, 206, 242),
                        new Color(174, 213, 244),
                        new Color(183, 222, 251),
                        new Color(191, 230, 255),
                        new Color(202, 237, 255),
                        new Color(206, 247, 253),
                        new Color(211, 255, 254),
                        new Color(208, 255, 254),
                        new Color(206, 249, 255),
                        new Color(202, 237, 255),};
        } else {
            Color color1[] = ColorHelper.createColorArr(controlColorLight, controlColorDark, 6);
            color1[0] = controlColorDark;
            Color color2[] = ColorHelper.createColorArr(ColorHelper.brighter(controlColorDark, 10), controlColorLight, 15);
            System.arraycopy(color1, 0, DEFAULT_COLORS, 0, 6);
            for (int i = 5; i < 20; i++) {
                DEFAULT_COLORS[i] = color2[i - 5];
            }
        }
        if (rolloverColorDark.equals(new ColorUIResource(106, 192, 150))) {
            ROLLOVER_COLORS = new Color[]{
                        new Color(106, 192, 150),
                        new Color(154, 209, 190),
                        new Color(173, 220, 198),
                        new Color(182, 232, 203),
                        new Color(180, 234, 207),
                        new Color(167, 231, 204),
                        new Color(148, 226, 191),
                        new Color(144, 225, 181),
                        new Color(145, 226, 182),
                        new Color(151, 230, 188),
                        new Color(160, 235, 198),
                        new Color(168, 242, 206),
                        new Color(174, 244, 213),
                        new Color(183, 251, 222),
                        new Color(191, 255, 230),
                        new Color(202, 255, 237),
                        new Color(206, 253, 247),
                        new Color(211, 254, 255),
                        new Color(208, 254, 255),
                        new Color(206, 249, 255),
                        new Color(196, 247, 227),};
        } else {
            Color color1[] = ColorHelper.createColorArr(rolloverColorLight, rolloverColorDark, 6);
            color1[0] = rolloverColorDark;
            Color color2[] = ColorHelper.createColorArr(ColorHelper.brighter(rolloverColorDark, 10), rolloverColorLight, 15);
            System.arraycopy(color1, 0, ROLLOVER_COLORS, 0, 6);
            for (int i = 5; i < 20; i++) {
                ROLLOVER_COLORS[i] = color2[i - 5];
            }
        }

        HIDEFAULT_COLORS = new Color[]{
                    new Color(250, 250, 250),
                    new Color(250, 250, 250),
                    new Color(240, 240, 240),
                    new Color(230, 230, 230),
                    new Color(220, 220, 220),
                    new Color(214, 214, 214),
                    new Color(218, 218, 218),
                    new Color(222, 222, 222),
                    new Color(226, 226, 226),
                    new Color(230, 230, 230),
                    new Color(234, 234, 234),
                    new Color(237, 237, 237),
                    new Color(240, 240, 240),
                    new Color(242, 242, 242),
                    new Color(244, 244, 244),
                    new Color(246, 246, 246),
                    new Color(248, 248, 248),
                    new Color(250, 250, 250),
                    new Color(252, 252, 252),
                    new Color(254, 254, 254),
                    new Color(255, 255, 255),};

        ACTIVE_COLORS = DEFAULT_COLORS;
        INACTIVE_COLORS = HIDEFAULT_COLORS;
        SELECTED_COLORS = DEFAULT_COLORS;

        PRESSED_COLORS = ColorHelper.createColorArr(lightGray, extraLightGray, 20);
        DISABLED_COLORS = new Color[HIDEFAULT_COLORS.length];
        for (int i = 0; i < HIDEFAULT_COLORS.length; i++) {
            DISABLED_COLORS[i] = ColorHelper.brighter(HIDEFAULT_COLORS[i], 40.0);
        }

        WINDOW_TITLE_COLORS = ColorHelper.createColorArr(windowTitleColorLight, windowTitleColorDark, 20);
        WINDOW_INACTIVE_TITLE_COLORS = ColorHelper.createColorArr(windowInactiveTitleColorLight, windowInactiveTitleColorDark, 20);
        MENUBAR_COLORS = ColorHelper.createColorArr(menuColorLight, menuColorDark, 20);
        TOOLBAR_COLORS = MENUBAR_COLORS;

        BUTTON_COLORS = HIDEFAULT_COLORS;
        TAB_COLORS = BUTTON_COLORS;
        if (isBrightMode()) {
            COL_HEADER_COLORS = HIDEFAULT_COLORS;
        } else {
            COL_HEADER_COLORS = DEFAULT_COLORS;
        }
        if (isBrightMode()) {
            THUMB_COLORS = HIDEFAULT_COLORS;
        } else {
            THUMB_COLORS = DEFAULT_COLORS;
        }
        TRACK_COLORS = ColorHelper.createColorArr(new Color(220, 220, 220), Color.white, 20);
        SLIDER_COLORS = DEFAULT_COLORS;
        PROGRESSBAR_COLORS = DEFAULT_COLORS;
    }
}
