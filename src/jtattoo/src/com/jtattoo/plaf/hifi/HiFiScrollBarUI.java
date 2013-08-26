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
 
package com.jtattoo.plaf.hifi;

import com.jtattoo.plaf.*;
import java.awt.Color;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

/**
 *
 * @author  Michael Hagen
 */
public class HiFiScrollBarUI extends XPScrollBarUI {

    public static ComponentUI createUI(JComponent c) {
        return new HiFiScrollBarUI();
    }

    protected void installDefaults() {
        super.installDefaults();
        Color colors[] = AbstractLookAndFeel.getTheme().getThumbColors();
        rolloverColors = new Color[colors.length];
        dragColors = new Color[colors.length];
        for (int i = 0; i < colors.length; i++) {
            rolloverColors[i] = ColorHelper.brighter(colors[i], 8);
            dragColors[i] = ColorHelper.darker(colors[i], 8);
        }
    }

    protected JButton createDecreaseButton(int orientation) {
        return new HiFiScrollButton(orientation, scrollBarWidth);
    }

    protected JButton createIncreaseButton(int orientation) {
        return new HiFiScrollButton(orientation, scrollBarWidth);
    }

    protected Color getFrameColor() {
        Color frameColor = ColorHelper.brighter(AbstractLookAndFeel.getTheme().getButtonBackgroundColor(), 8);
        if (isDragging) {
            return ColorHelper.darker(frameColor, 8);
        } else if (isRollover) {
            return ColorHelper.brighter(frameColor, 16);
        } else {
            return frameColor;
        }
    }

}
