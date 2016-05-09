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
 
package com.jtattoo.plaf.fast;

import com.jtattoo.plaf.AbstractTheme;
import com.jtattoo.plaf.ColorHelper;
import java.awt.Color;
import javax.swing.plaf.ColorUIResource;

/**
 * @author Michael Hagen
 */
public class FastDefaultTheme extends AbstractTheme {

    public FastDefaultTheme() {
        super();
        // Setup theme with defaults
        setUpColor();
        // Overwrite defaults with user props
        loadProperties();
        // Setup the color arrays
        setUpColorArrs();
    }

    public String getPropertyFileName() {
        return "FastTheme.properties";
    }

    public void setUpColor() {
        super.setUpColor();
        // Defaults for FastLookAndFeel
        backgroundColor = new ColorUIResource(244, 244, 244);
        backgroundColorLight = new ColorUIResource(255, 255, 255);
        backgroundColorDark = new ColorUIResource(232, 232, 232);
        alterBackgroundColor = new ColorUIResource(232, 232, 232);
        selectionBackgroundColor = new ColorUIResource(210, 210, 210);
        frameColor = gray;
        focusColor = new ColorUIResource(160, 160, 200);
        focusCellColor = new ColorUIResource(160, 160, 200);
        buttonBackgroundColor = extraLightGray;
        controlBackgroundColor = new ColorUIResource(220, 220, 220);

        windowTitleBackgroundColor = new ColorUIResource(210, 210, 210);
        windowBorderColor = new ColorUIResource(210, 210, 210);

        windowInactiveTitleBackgroundColor = new ColorUIResource(230, 230, 230);
        windowInactiveBorderColor = new ColorUIResource(230, 230, 230);

        menuBackgroundColor = new ColorUIResource(240, 240, 240);
        menuSelectionBackgroundColor = lightGray;

        toolbarBackgroundColor = new ColorUIResource(240, 240, 240);

        tabAreaBackgroundColor = backgroundColor;
        desktopColor = new ColorUIResource(128, 128, 148);
    }

    public void setUpColorArrs() {
        super.setUpColorArrs();
        // Generate the color arrays
        DEFAULT_COLORS = ColorHelper.createColorArr(controlBackgroundColor, controlBackgroundColor, 2);
        HIDEFAULT_COLORS = ColorHelper.createColorArr(backgroundColor, backgroundColor, 2);

        ACTIVE_COLORS = DEFAULT_COLORS;
        INACTIVE_COLORS = HIDEFAULT_COLORS;

        ROLLOVER_COLORS = ColorHelper.createColorArr(buttonBackgroundColor, buttonBackgroundColor, 2);
        SELECTED_COLORS = ColorHelper.createColorArr(backgroundColor, backgroundColor, 2);
        PRESSED_COLORS = ColorHelper.createColorArr(controlColorDark, controlColorDark, 2);
        DISABLED_COLORS = ColorHelper.createColorArr(controlColorLight, controlColorLight, 2);

        BUTTON_COLORS = ColorHelper.createColorArr(buttonBackgroundColor, buttonBackgroundColor, 2);
        COL_HEADER_COLORS = ColorHelper.createColorArr(new Color(248, 248, 248), new Color(248, 248, 248), 2);
        CHECKBOX_COLORS = COL_HEADER_COLORS;

        TAB_COLORS = DEFAULT_COLORS;
        
    }
}
