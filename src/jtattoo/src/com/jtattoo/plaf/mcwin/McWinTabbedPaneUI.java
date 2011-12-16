/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.mcwin;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;

import com.jtattoo.plaf.*;

/**
 * @author Michael Hagen
 */
public class McWinTabbedPaneUI extends BaseTabbedPaneUI {

    private Color sepColors[] = null;
    private Color altSepColors[] = null;

    public static ComponentUI createUI(JComponent c) {
        return new McWinTabbedPaneUI();
    }

    public void installDefaults() {
        super.installDefaults();
    }

    protected Color[] getContentBorderColors(int tabPlacement) {
        Color controlColorLight = AbstractLookAndFeel.getTheme().getControlColorLight();
        if (!controlColorLight.equals(new ColorUIResource(106, 150, 192))) {
            controlColorLight = ColorHelper.brighter(controlColorLight, 6);
            Color controlColorDark = AbstractLookAndFeel.getTheme().getControlColorDark();
            if (sepColors == null) {
                sepColors = new Color[5];
                sepColors[0] = controlColorDark;
                sepColors[1] = controlColorLight;
                sepColors[2] = controlColorLight;
                sepColors[3] = controlColorLight;
                sepColors[4] = controlColorDark;
            }
            return sepColors;
        } else {
            if (tabPlacement == TOP || tabPlacement == LEFT) {
                if (sepColors == null) {
                    int len = AbstractLookAndFeel.getTheme().getDefaultColors().length;
                    sepColors = new Color[5];
                    sepColors[0] = AbstractLookAndFeel.getTheme().getDefaultColors()[0];
                    sepColors[1] = AbstractLookAndFeel.getTheme().getDefaultColors()[len - 6];
                    sepColors[2] = AbstractLookAndFeel.getTheme().getDefaultColors()[2];
                    sepColors[3] = AbstractLookAndFeel.getTheme().getDefaultColors()[1];
                    sepColors[4] = AbstractLookAndFeel.getTheme().getDefaultColors()[0];
                }
                return sepColors;
            } else {
                if (altSepColors == null) {
                    altSepColors = new Color[5];
                    altSepColors[0] = AbstractLookAndFeel.getTheme().getDefaultColors()[9];
                    altSepColors[1] = AbstractLookAndFeel.getTheme().getDefaultColors()[8];
                    altSepColors[2] = AbstractLookAndFeel.getTheme().getDefaultColors()[7];
                    altSepColors[3] = AbstractLookAndFeel.getTheme().getDefaultColors()[6];
                    altSepColors[4] = AbstractLookAndFeel.getTheme().getDefaultColors()[0];
                }
                return altSepColors;
            }
        }
    }

}