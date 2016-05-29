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
 
package com.jtattoo.plaf.texture;

import com.jtattoo.plaf.*;
import java.awt.Color;
import java.awt.Insets;
import javax.swing.Icon;

/**
 * @author Michael Hagen
 */
public class TextureIcons extends BaseIcons {

    public static void setUp() {
        iconIcon = null;
        maxIcon = null;
        minIcon = null;
        closeIcon = null;
        splitterHorBumpIcon = null;
        splitterVerBumpIcon = null;
        thumbHorIcon = null;
        thumbVerIcon = null;
        thumbHorIconRollover = null;
        thumbVerIconRollover = null;
    }

    public static Icon getIconIcon() {
        if (iconIcon == null) {
            if (AbstractLookAndFeel.getTheme().isMacStyleWindowDecorationOn()) {
                iconIcon = new MacIconIcon();
            } else {
                Color iconColor = AbstractLookAndFeel.getTheme().getWindowIconColor();
                Color iconShadowColor = AbstractLookAndFeel.getTheme().getWindowIconShadowColor();
                Color iconRolloverColor = AbstractLookAndFeel.getTheme().getWindowIconRolloverColor();
                iconIcon = new BaseIcons.IconSymbol(iconColor, iconShadowColor, iconRolloverColor, new Insets(1, 1, 1, 1));
            }
        }
        return iconIcon;
    }

    public static Icon getMinIcon() {
        if (minIcon == null) {
            if (AbstractLookAndFeel.getTheme().isMacStyleWindowDecorationOn()) {
                minIcon = new MacMinIcon();
            } else {
                Color iconColor = AbstractLookAndFeel.getTheme().getWindowIconColor();
                Color iconShadowColor = AbstractLookAndFeel.getTheme().getWindowIconShadowColor();
                Color iconRolloverColor = AbstractLookAndFeel.getTheme().getWindowIconRolloverColor();
                minIcon = new BaseIcons.MinSymbol(iconColor, iconShadowColor, iconRolloverColor, new Insets(1, 1, 1, 1));
            }
        }
        return minIcon;
    }

    public static Icon getMaxIcon() {
        if (maxIcon == null) {
            if (AbstractLookAndFeel.getTheme().isMacStyleWindowDecorationOn()) {
                maxIcon = new MacMaxIcon();
            } else {
                Color iconColor = AbstractLookAndFeel.getTheme().getWindowIconColor();
                Color iconShadowColor = AbstractLookAndFeel.getTheme().getWindowIconShadowColor();
                Color iconRolloverColor = AbstractLookAndFeel.getTheme().getWindowIconRolloverColor();
                maxIcon = new BaseIcons.MaxSymbol(iconColor, iconShadowColor, iconRolloverColor, new Insets(1, 1, 1, 1));
            }
        }
        return maxIcon;
    }

    public static Icon getCloseIcon() {
        if (closeIcon == null) {
            if (AbstractLookAndFeel.getTheme().isMacStyleWindowDecorationOn()) {
                closeIcon = new MacCloseIcon();
            } else {
                Color iconColor = AbstractLookAndFeel.getTheme().getWindowIconColor();
                Color iconShadowColor = AbstractLookAndFeel.getTheme().getWindowIconShadowColor();
                Color iconRolloverColor = AbstractLookAndFeel.getTheme().getWindowIconRolloverColor();
                closeIcon = new BaseIcons.CloseSymbol(iconColor, iconShadowColor, iconRolloverColor, new Insets(1, 1, 1, 1));
            }
        }
        return closeIcon;
    }

    public static Icon getSplitterHorBumpIcon() {
        if (splitterHorBumpIcon == null) {
            splitterHorBumpIcon = new LazyImageIcon("texture/icons/SplitterHorBumps.gif");
        }
        return splitterHorBumpIcon;
    }

    public static Icon getSplitterVerBumpIcon() {
        if (splitterVerBumpIcon == null) {
            splitterVerBumpIcon = new LazyImageIcon("texture/icons/SplitterVerBumps.gif");
        }
        return splitterVerBumpIcon;
    }

    public static Icon getThumbHorIcon() {
        if ("Default".equals(AbstractLookAndFeel.getTheme().getName())) {
            if (thumbHorIcon == null) {
                thumbHorIcon = new LazyImageIcon("texture/icons/thumb_hor.gif");
            }
            return thumbHorIcon;
        } else {
            return BaseIcons.getThumbHorIcon();
        }
    }

    public static Icon getThumbVerIcon() {
        if ("Default".equals(AbstractLookAndFeel.getTheme().getName())) {
            if (thumbVerIcon == null) {
                thumbVerIcon = new LazyImageIcon("texture/icons/thumb_ver.gif");
            }
            return thumbVerIcon;
        } else {
            return BaseIcons.getThumbVerIcon();
        }
    }

    public static Icon getThumbHorIconRollover() {
        if ("Default".equals(AbstractLookAndFeel.getTheme().getName())) {
            if (thumbHorIconRollover == null) {
                thumbHorIconRollover = new LazyImageIcon("texture/icons/thumb_hor_rollover.gif");
            }
            return thumbHorIconRollover;
        } else {
            return BaseIcons.getThumbHorIconRollover();
        }
    }

    public static Icon getThumbVerIconRollover() {
        if ("Default".equals(AbstractLookAndFeel.getTheme().getName())) {
            if (thumbVerIconRollover == null) {
                thumbVerIconRollover = new LazyImageIcon("texture/icons/thumb_ver_rollover.gif");
            }
            return thumbVerIconRollover;
        } else {
            return BaseIcons.getThumbVerIconRollover();
        }
    }
}
