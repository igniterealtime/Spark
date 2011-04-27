/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */

package com.jtattoo.plaf.custom.pulsar;

import java.awt.*;
import javax.swing.*;

import com.jtattoo.plaf.*;

/**
 * @author Michael Hagen
 */
public class PulsarIcons extends BaseIcons {
    private static final Color foreColor = new Color(0, 118, 183);
    private static final Color shadowColor = new Color(196, 242, 255);
    private static final Color rolloverColor = Color.red;

    private static Icon iconIcon = null;
    private static Icon maxIcon = null;
    private static Icon minIcon = null;
    private static Icon closeIcon = null;
    
    public static Icon getIconIcon() {
        if (iconIcon == null) {
            iconIcon = new BaseIcons.IconSymbol(foreColor, ColorHelper.darker(shadowColor, 10), rolloverColor, new Insets(1, 1, 1, 1));
        }
        return iconIcon;
    }

    public static Icon getMinIcon() {
        if (minIcon == null) {
            minIcon = new BaseIcons.MinSymbol(foreColor, ColorHelper.darker(shadowColor, 10), rolloverColor, new Insets(1, 1, 1, 1));
        }
        return minIcon;
    }

    public static Icon getMaxIcon() {
        if (maxIcon == null) {
            maxIcon = new BaseIcons.MaxSymbol(foreColor, ColorHelper.darker(shadowColor, 10), rolloverColor, new Insets(1, 1, 1, 1));
        }
        return maxIcon;
    }

    public static Icon getCloseIcon() {
        if (closeIcon == null) {
            closeIcon = new BaseIcons.CloseSymbol(foreColor, shadowColor, rolloverColor, new Insets(1, 1, 1, 1));
        }
        return closeIcon;
    }
    
    //--------------------------------------------------------------------------------------------------------
}
