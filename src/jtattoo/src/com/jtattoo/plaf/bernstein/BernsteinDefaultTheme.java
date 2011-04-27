/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.bernstein;

import javax.swing.plaf.*;
import java.awt.*;

import com.jtattoo.plaf.*;

/**
 * @author Michael Hagen
 */
public class BernsteinDefaultTheme extends AbstractTheme {

    public BernsteinDefaultTheme() {
        super();
        // Setup theme with defaults
        setUpColor();
        // Overwrite defaults with user props
        loadProperties();
        // Setup the color arrays
        setUpColorArrs();
    }

    public String getPropertyFileName() {
        return "BernsteinTheme.properties";
    }

    public void setUpColor() {
        super.setUpColor();
        // Defaults for BernsteinLookAndFeel
        menuOpaque = false;
        menuAlpha = 0.85f;

        backgroundColor = new ColorUIResource(253, 249, 204);
        backgroundColorLight = white;
        backgroundColorDark = new ColorUIResource(253, 241, 176);
        alterBackgroundColor = new ColorUIResource(253, 241, 176);
        
        selectionBackgroundColor = new ColorUIResource(250, 216, 32);
        disabledForegroundColor = new ColorUIResource(164, 164, 164);
        frameColor = new ColorUIResource(233, 199, 5);
        focusCellColor = new ColorUIResource(139, 92, 0);
        rolloverColor = new ColorUIResource(251, 220, 64);
        
        buttonBackgroundColor = new ColorUIResource(253, 249, 204);

        controlBackgroundColor = new ColorUIResource(253, 249, 204);
        controlHighlightColor = white;
        controlShadowColor = new ColorUIResource(247, 231, 34);
        controlDarkShadowColor = frameColor;
        controlColorLight = new ColorUIResource(252, 218, 0);
        controlColorDark = new ColorUIResource(183, 142, 0);

        windowTitleBackgroundColor = selectionBackgroundColor;
        windowTitleColorLight = new ColorUIResource(253, 249, 204);
        windowTitleColorDark = new ColorUIResource(251, 241, 153);
        windowBorderColor = new ColorUIResource(254, 240, 0);

        windowInactiveTitleBackgroundColor = backgroundColor;
        windowInactiveTitleColorLight = white;
        windowInactiveTitleColorDark = new ColorUIResource(236, 236, 236);
        windowInactiveBorderColor = new ColorUIResource(254, 240, 0);

        menuBackgroundColor = backgroundColor;
        menuSelectionBackgroundColor = selectionBackgroundColor;

        tabAreaBackgroundColor = backgroundColor;
        desktopColor = new ColorUIResource(253, 249, 204);
    }

    public void setUpColorArrs() {
        super.setUpColorArrs();
        // Generate the color arrays
        DEFAULT_COLORS = new Color[]{
                    new Color(247, 225, 0),
                    new Color(251, 232, 0),
                    new Color(243, 216, 0),
                    new Color(237, 204, 0),
                    new Color(239, 209, 0),
                    new Color(242, 215, 0),
                    new Color(243, 216, 0),
                    new Color(245, 221, 0),
                    new Color(246, 222, 0),
                    new Color(247, 225, 0),
                    new Color(248, 227, 0),
                    new Color(249, 230, 0),
                    new Color(251, 232, 0),
                    new Color(252, 235, 0),
                    new Color(253, 237, 0),
                    new Color(253, 237, 0),
                    new Color(254, 240, 0),};
        HIDEFAULT_COLORS = new Color[DEFAULT_COLORS.length];
        for (int i = 0; i < DEFAULT_COLORS.length; i++) {
            HIDEFAULT_COLORS[i] = ColorHelper.brighter(DEFAULT_COLORS[i], 50.0);
        }

        ACTIVE_COLORS = DEFAULT_COLORS;
        INACTIVE_COLORS = HIDEFAULT_COLORS;
        SELECTED_COLORS = DEFAULT_COLORS;
        PRESSED_COLORS = new Color[DEFAULT_COLORS.length];
        for (int i = 0; i < DEFAULT_COLORS.length; i++) {
            PRESSED_COLORS[i] = backgroundColor;
        }

        ROLLOVER_COLORS = new Color[DEFAULT_COLORS.length];
        for (int i = 0; i < DEFAULT_COLORS.length; i++) {
            ROLLOVER_COLORS[i] = ColorHelper.brighter(DEFAULT_COLORS[i], 70.0);
        }

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
        COL_HEADER_COLORS = HIDEFAULT_COLORS;
        THUMB_COLORS = HIDEFAULT_COLORS;
        TRACK_COLORS = ColorHelper.createColorArr(new Color(255, 245, 200), Color.white, 20);
        SLIDER_COLORS = DEFAULT_COLORS;
        PROGRESSBAR_COLORS = DEFAULT_COLORS;
    }
}
