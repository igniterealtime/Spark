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
 
package com.jtattoo.plaf.luna;

import com.jtattoo.plaf.AbstractTheme;
import com.jtattoo.plaf.ColorHelper;
import java.awt.Color;
import javax.swing.plaf.ColorUIResource;

public class LunaDefaultTheme extends AbstractTheme {

    public LunaDefaultTheme() {
        super();
        // Setup theme with defaults
        setUpColor();
        // Overwrite defaults with user props
        loadProperties();
        // Setup the color arrays
        setUpColorArrs();
    }

    public String getPropertyFileName() {
        return "LunaTheme.properties";
    }

    public void setUpColor() {
        super.setUpColor();

        // Defaults for AbstractLookAndFeel
        backgroundColor = new ColorUIResource(240, 238, 225);
        backgroundColorLight = new ColorUIResource(255, 255, 255);
        backgroundColorDark = new ColorUIResource(232, 228, 208);
        alterBackgroundColor = new ColorUIResource(232, 228, 208);

        selectionForegroundColor = black;
        selectionBackgroundColor = new ColorUIResource(194, 208, 243);//new ColorUIResource(200, 210, 240);

        frameColor = new ColorUIResource(0, 88, 168); // new ColorUIResource(0, 60, 116);
        focusCellColor = new ColorUIResource(0, 60, 116);

        buttonBackgroundColor = new ColorUIResource(236, 233, 216);
        buttonColorLight = white;
        buttonColorDark = new ColorUIResource(214, 208, 197);

        rolloverColor = lightOrange;

        controlForegroundColor = black;
        controlBackgroundColor = new ColorUIResource(236, 233, 216);
        controlColorLight = white;
        controlColorDark = new ColorUIResource(214, 208, 197);

        windowTitleForegroundColor = white;
        windowTitleBackgroundColor = new ColorUIResource(194, 208, 243); //new ColorUIResource(139, 185, 254);
        windowTitleColorLight = new ColorUIResource(139, 185, 254);
        windowTitleColorDark = new ColorUIResource(2, 80, 196);
        windowBorderColor = new ColorUIResource(2, 80, 196);

        windowInactiveTitleForegroundColor = white;
        windowInactiveTitleBackgroundColor = new ColorUIResource(240, 238, 225); // new ColorUIResource(141, 186, 253);
        windowInactiveTitleColorLight = new ColorUIResource(141, 186, 253);
        windowInactiveTitleColorDark = new ColorUIResource(39, 106, 204);
        windowInactiveBorderColor = new ColorUIResource(39, 106, 204);

        menuBackgroundColor = backgroundColor;
        menuSelectionForegroundColor = white;
        menuSelectionBackgroundColor = new ColorUIResource(49, 106, 197);
        menuColorLight = new ColorUIResource(248, 247, 241);
        menuColorDark = backgroundColor;

        toolbarBackgroundColor = backgroundColor;
        toolbarColorLight = menuColorLight;
        toolbarColorDark = backgroundColor;

        tabAreaBackgroundColor = backgroundColor;
        desktopColor = backgroundColor;
    }

    public void setUpColorArrs() {
        super.setUpColorArrs();

        // Generate the color arrays
        DEFAULT_COLORS = ColorHelper.createColorArr(controlColorLight, controlColorDark, 20);
        HIDEFAULT_COLORS = ColorHelper.createColorArr(ColorHelper.brighter(controlColorLight, 90), ColorHelper.brighter(controlColorDark, 30), 20);

        ACTIVE_COLORS = DEFAULT_COLORS;
        INACTIVE_COLORS = ColorHelper.createColorArr(new Color(248, 247, 241), backgroundColor, 20);

        ROLLOVER_COLORS = ColorHelper.createColorArr(ColorHelper.brighter(controlColorLight, 30), ColorHelper.brighter(controlColorDark, 20), 30);
        SELECTED_COLORS = DEFAULT_COLORS;
        PRESSED_COLORS = ColorHelper.createColorArr(controlColorDark, controlColorLight, 20);
        DISABLED_COLORS = ColorHelper.createColorArr(Color.white, Color.lightGray, 20);

        // Generate the color arrays
        Color topHi = windowTitleColorLight;
        Color topLo = ColorHelper.darker(windowTitleColorLight, 10);
        Color bottomHi = ColorHelper.brighter(windowTitleColorDark, 15);
        Color bottomLo = windowTitleColorDark;

        WINDOW_TITLE_COLORS = new Color[20];
        Color[] topColors = ColorHelper.createColorArr(topHi, topLo, 8);
        System.arraycopy(topColors, 0, WINDOW_TITLE_COLORS, 0, 8);
        Color[] bottomColors = ColorHelper.createColorArr(bottomHi, bottomLo, 12);
        System.arraycopy(bottomColors, 0, WINDOW_TITLE_COLORS, 8, 12);

        WINDOW_INACTIVE_TITLE_COLORS = new Color[WINDOW_TITLE_COLORS.length];
        for (int i = 0; i < WINDOW_INACTIVE_TITLE_COLORS.length; i++) {
            WINDOW_INACTIVE_TITLE_COLORS[i] = ColorHelper.brighter(WINDOW_TITLE_COLORS[i], 20);
        }

        MENUBAR_COLORS = ColorHelper.createColorArr(menuColorLight, menuColorDark, 20);
        TOOLBAR_COLORS = ColorHelper.createColorArr(toolbarColorLight, toolbarColorDark, 20);

        BUTTON_COLORS = new Color[]{
                    new Color(255, 255, 255),
                    new Color(254, 254, 254),
                    new Color(252, 252, 251),
                    new Color(251, 251, 249),
                    new Color(250, 250, 248),
                    new Color(249, 249, 246),
                    new Color(248, 248, 244),
                    new Color(247, 247, 243),
                    new Color(246, 246, 242),
                    new Color(245, 245, 240),
                    new Color(244, 244, 239),
                    new Color(243, 243, 238),
                    new Color(242, 242, 236),
                    new Color(241, 241, 235),
                    new Color(240, 240, 234),
                    new Color(236, 235, 230),
                    new Color(226, 223, 214),
                    new Color(214, 208, 197),};
        TAB_COLORS = ColorHelper.createColorArr(Color.white, new Color(236, 235, 230), 20);
        COL_HEADER_COLORS = TAB_COLORS;
        CHECKBOX_COLORS = TAB_COLORS;
        TRACK_COLORS = ColorHelper.createColorArr(new Color(243, 241, 236), new Color(254, 254, 251), 20);
        THUMB_COLORS = ColorHelper.createColorArr(new Color(218, 230, 254), new Color(180, 197, 240), 20);
        //SLIDER_COLORS = ColorHelper.createColorArr(new Color(218, 230, 254), new Color(180, 197, 240), 20);
        SLIDER_COLORS = THUMB_COLORS;//ColorHelper.createColorArr(new Color(243, 241, 236), new Color(254, 254, 251), 20);
        PROGRESSBAR_COLORS = THUMB_COLORS;
    }
}
