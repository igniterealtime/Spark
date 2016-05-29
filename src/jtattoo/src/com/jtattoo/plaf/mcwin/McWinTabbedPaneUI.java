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

import com.jtattoo.plaf.*;
import java.awt.Color;
import javax.swing.JComponent;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.ComponentUI;

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
        tabAreaInsets.bottom = 5;
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