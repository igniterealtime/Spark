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
 
package com.jtattoo.plaf.aero;

import com.jtattoo.plaf.AbstractLookAndFeel;
import com.jtattoo.plaf.BaseIcons;
import java.awt.Color;
import java.awt.Insets;
import javax.swing.Icon;

/**
 * @author Michael Hagen
 */
public class AeroIcons extends BaseIcons {
    
    public static Icon getIconIcon() {
        if (iconIcon == null) {
            if (AbstractLookAndFeel.getTheme().isMacStyleWindowDecorationOn()) {
                iconIcon = new MacIconIcon();
            } else {
                iconIcon = new BaseIcons.IconSymbol(Color.black, Color.white, null, new Insets(2, 2, 2, 2));
            }
        }
        return iconIcon;
    }
    
    public static Icon getMinIcon() {
        if (minIcon == null) {
            if (AbstractLookAndFeel.getTheme().isMacStyleWindowDecorationOn()) {
                minIcon = new MacMinIcon();
            } else {
                minIcon = new BaseIcons.MinSymbol(Color.black, Color.white, null, new Insets(2, 2, 2, 2));
            }
        }
        return minIcon;
    }
    
    public static Icon getMaxIcon() {
        if (maxIcon == null) {
            if (AbstractLookAndFeel.getTheme().isMacStyleWindowDecorationOn()) {
                maxIcon = new MacMaxIcon();
            } else {
                maxIcon = new BaseIcons.MaxSymbol(Color.black, Color.white, null, new Insets(2, 2, 2, 2));
            }
        }
        return maxIcon;
    }
    
    public static Icon getCloseIcon() {
        if (closeIcon == null) {
            if (AbstractLookAndFeel.getTheme().isMacStyleWindowDecorationOn()) {
                closeIcon = new MacCloseIcon();
            } else {
                closeIcon = new BaseIcons.CloseSymbol(Color.black, Color.white, null, new Insets(2, 2, 2, 2));
            }
        }
        return closeIcon;
    }
    
}
