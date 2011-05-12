/**
 * $RCSfile: ,v $
 * $Revision: $
 * $Date: $
 * 
 * Copyright (C) 2004-2011 Jive Software. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jtattoo.plaf.luna;

import javax.swing.plaf.*;
import java.awt.*;

import com.jtattoo.plaf.*;

public class LunaSparkTheme extends AbstractTheme {

    public LunaSparkTheme() {
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

        // Defaults for LunaLookAndFeel
        backgroundColor = new ColorUIResource(235,239,254);
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
        Color topLo = ColorHelper.darker(windowTitleColorLight, 10);//new Color(81, 150, 253);
        Color bottomHi = ColorHelper.brighter(windowTitleColorDark, 15);//new Color(3, 101, 241);
        Color bottomLo = windowTitleColorDark;

        WINDOW_TITLE_COLORS = new Color[20];
        Color[] topColors = ColorHelper.createColorArr(topHi, topLo, 8);
        for (int i = 0; i < 8; i++) {
            WINDOW_TITLE_COLORS[i] = topColors[i];
        }
        Color[] bottomColors = ColorHelper.createColorArr(bottomHi, bottomLo, 12);
        for (int i = 0; i < 12; i++) {
            WINDOW_TITLE_COLORS[i + 8] = bottomColors[i];
        }

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
        TRACK_COLORS = ColorHelper.createColorArr(new Color(243, 241, 236), new Color(254, 254, 251), 20);
        THUMB_COLORS = ColorHelper.createColorArr(new Color(218, 230, 254), new Color(180, 197, 240), 20);
        //SLIDER_COLORS = ColorHelper.createColorArr(new Color(218, 230, 254), new Color(180, 197, 240), 20);
        SLIDER_COLORS = THUMB_COLORS;//ColorHelper.createColorArr(new Color(243, 241, 236), new Color(254, 254, 251), 20);
        PROGRESSBAR_COLORS = THUMB_COLORS;
    }
}
