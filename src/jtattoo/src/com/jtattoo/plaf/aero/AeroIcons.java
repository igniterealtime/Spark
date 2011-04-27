/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */

package com.jtattoo.plaf.aero;

import java.awt.*;
import javax.swing.*;

import com.jtattoo.plaf.*;

/**
 * @author Michael Hagen
 */
public class AeroIcons extends BaseIcons {
    private static Icon iconIcon = null;
    private static Icon maxIcon = null;
    private static Icon minIcon = null;
    private static Icon closeIcon = null;
    
    public static Icon getIconIcon() {
        if (iconIcon == null)
            iconIcon = new BaseIcons.IconSymbol(Color.black, Color.white, null, new Insets(2, 2, 2, 2));
        return iconIcon;
    }
    
    public static Icon getMinIcon() {
        if (minIcon == null)
            minIcon = new BaseIcons.MinSymbol(Color.black, Color.white, null, new Insets(2, 2, 2, 2));
        return minIcon;
    }
    
    public static Icon getMaxIcon() {
        if (maxIcon == null)
            maxIcon = new BaseIcons.MaxSymbol(Color.black, Color.white, null, new Insets(2, 2, 2, 2));
        return maxIcon;
    }
    
    public static Icon getCloseIcon() {
        if (closeIcon == null)
            closeIcon = new BaseIcons.CloseSymbol(Color.black, Color.white, null, new Insets(2, 2, 2, 2));
        return closeIcon;
    }
    
}
