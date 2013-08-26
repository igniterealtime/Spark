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
 
package com.jtattoo.plaf.bernstein;

import com.jtattoo.plaf.*;
import java.awt.*;
import javax.swing.Icon;

/**
 * @author  Michael Hagen
 */
public class BernsteinUtils {

    private static final Icon BG_IMAGE = new LazyImageIcon("bernstein/icons/background.jpg");
    private static final int IMAGE_WIDTH = BG_IMAGE.getIconWidth();
    private static final int IMAGE_HEIGHT = BG_IMAGE.getIconHeight();

    private BernsteinUtils() {
    }

    public static void fillComponent(Graphics g, Component c) {
        if (AbstractLookAndFeel.getTheme().isBackgroundPatternOn()) {
            int w = c.getWidth();
            int h = c.getHeight();
            Point p = JTattooUtilities.getRelLocation(c);
            int y = -p.y;
            while (y < h) {
                int x = -p.x;
                while (x < w) {
                    BG_IMAGE.paintIcon(c, g, x, y);
                    x += IMAGE_WIDTH;
                }
                y += IMAGE_HEIGHT;
            }
        } else {
            g.setColor(c.getBackground());
            g.fillRect(0, 0, c.getWidth(), c.getHeight());
        }

    }
}
