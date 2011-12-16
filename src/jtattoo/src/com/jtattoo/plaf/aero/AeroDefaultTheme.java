/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */

package com.jtattoo.plaf.aero;

import java.awt.*;
import javax.swing.plaf.*;

import com.jtattoo.plaf.*;

public class AeroDefaultTheme extends AbstractTheme {
    
    public AeroDefaultTheme() {
        super();
        // Setup theme with defaults
        setUpColor();
        // Overwrite defaults with user props
        loadProperties();
        // Setup the color arrays
        setUpColorArrs();
    }
    
    public String getPropertyFileName() { 
        return "AeroTheme.properties"; 
    }
    
    public void setUpColor() {
        super.setUpColor();
        backgroundColor               = new ColorUIResource(236, 236, 236);
        backgroundColorLight          = new ColorUIResource(255, 255, 255);
        backgroundColorDark           = new ColorUIResource(228, 228, 228);
        alterBackgroundColor          = new ColorUIResource(228, 228, 228);
        frameColor                    = new ColorUIResource(160, 164, 168);
        
        selectionForegroundColor      = black;
        selectionBackgroundColor      = new ColorUIResource(176, 196, 222);
        rolloverColor                 = new ColorUIResource(192, 212, 230);
        
        buttonBackgroundColor         = new ColorUIResource(220, 220, 220);
        buttonColorLight              = new ColorUIResource(240, 240, 240);
        buttonColorDark               = new ColorUIResource(120, 120, 120);
        
        controlBackgroundColor        = backgroundColor;
        controlColorLight             = new ColorUIResource(150, 176, 211);
        controlColorDark              = new ColorUIResource(60, 95, 142);
        controlHighlightColor         = white;
        controlShadowColor            = new ColorUIResource(180, 186, 190);
        controlDarkShadowColor        = frameColor;
        
        windowTitleForegroundColor    = white;
        windowTitleBackgroundColor    = new ColorUIResource(176, 196, 222); // controlBackgroundColor;
        windowTitleColorLight         = controlColorLight;
        windowTitleColorDark          = controlColorDark;
        windowBorderColor             = controlColorDark;
        
        windowInactiveTitleBackgroundColor  = new ColorUIResource(236, 236, 236); // new ColorUIResource(210, 210, 210);
        windowInactiveTitleColorLight       = new ColorUIResource(240, 240, 240);
        windowInactiveTitleColorDark        = new ColorUIResource(220, 220, 220);
        windowInactiveBorderColor           = new ColorUIResource(210, 210, 210);
        
        menuBackgroundColor           = backgroundColor;
        menuSelectionForegroundColor  = selectionForegroundColor;
        menuSelectionBackgroundColor  = selectionBackgroundColor;
        menuColorLight                = controlColorLight;
        menuColorDark                 = controlColorDark;
        
        toolbarBackgroundColor        = backgroundColor;
        toolbarColorLight             = new ColorUIResource(240, 240, 240);
        toolbarColorDark              = new ColorUIResource(200, 200, 200);
        
        desktopColor                  = new ColorUIResource(240, 240, 240);
    }
    
    public void setUpColorArrs() {
        super.setUpColorArrs();
        Color color1[] = ColorHelper.createColorArr(controlColorLight, controlColorDark, 6);
        Color color2[] = ColorHelper.createColorArr(ColorHelper.brighter(controlColorDark, 10), controlColorLight, 15);
        for (int i = 0; i < 6; i++) {
            DEFAULT_COLORS[i] = color1[i];
        }
        for (int i = 5; i < 20; i++) {
            DEFAULT_COLORS[i] = color2[i - 5];
        }
        for (int i = 0; i < 20; i++) {
            HIDEFAULT_COLORS[i] = ColorHelper.brighter(DEFAULT_COLORS[i], 60);
        }
        
        ROLLOVER_COLORS = HIDEFAULT_COLORS;
        ACTIVE_COLORS = DEFAULT_COLORS;
        
        PRESSED_COLORS = new Color[20];
        for (int i = 0; i < 20; i++) {
            PRESSED_COLORS[i] = ColorHelper.brighter(DEFAULT_COLORS[i], 40);
        }

        DISABLED_COLORS = ColorHelper.createColorArr(new Color(240,240,240), new Color(220, 220, 220), 20);
        
        WINDOW_TITLE_COLORS = new Color[20];
        for (int i = 0; i < 20; i++)
            WINDOW_TITLE_COLORS[i] = ColorHelper.brighter(DEFAULT_COLORS[i], 20);
        
        MENUBAR_COLORS = ColorHelper.createColorArr(menuColorLight, menuColorDark, 20);
        TOOLBAR_COLORS = ColorHelper.createColorArr(toolbarColorLight, toolbarColorDark, 20);
        
        BUTTON_COLORS = new Color[] {
            new Color(247, 247, 247),
            new Color(243, 243, 243),
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
            new Color(255, 255, 255),
        };
        CHECKBOX_COLORS = BUTTON_COLORS;
        
        SELECTED_COLORS = new Color[20];
        for (int i = 0; i < 20; i++) {
            SELECTED_COLORS[i] = ColorHelper.brighter(DEFAULT_COLORS[i], 40);
        }
        TAB_COLORS = BUTTON_COLORS;
        COL_HEADER_COLORS = BUTTON_COLORS;
        THUMB_COLORS = SELECTED_COLORS;
        SLIDER_COLORS = THUMB_COLORS;
        PROGRESSBAR_COLORS = THUMB_COLORS;
        INACTIVE_COLORS = BUTTON_COLORS;
        
        WINDOW_INACTIVE_TITLE_COLORS = INACTIVE_COLORS;
    }
    
}
