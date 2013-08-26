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

package com.jtattoo.plaf;

import java.awt.*;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 *
 * @author Michael Hagen
 */
public class LazyMenuArrowImageIcon implements Icon {

    private String leftToRightName = null;
    private String rightToLefttName = null;
    private Icon leftToRightIcon = null;
    private Icon rightToLeftIcon = null;

    public LazyMenuArrowImageIcon(String leftToRightName, String rightToLefttName) {
        this.leftToRightName = leftToRightName;
        this.rightToLefttName = rightToLefttName;
    }

    private Icon getLeftToRightIcon() {
        if (leftToRightIcon == null) {
            try {
                leftToRightIcon = new ImageIcon(LazyMenuArrowImageIcon.class.getResource(leftToRightName));
            } catch (Throwable t) {
                System.out.println("ERROR: loading image " + leftToRightName + " failed!");
            }
        }
        return leftToRightIcon;
    }

    private Icon getRightToLeftIcon() {
        if (rightToLeftIcon == null) {
            try {
                rightToLeftIcon = new ImageIcon(LazyMenuArrowImageIcon.class.getResource(rightToLefttName));
            } catch (Throwable t) {
                System.out.println("ERROR: loading image " + rightToLefttName + " failed!");
            }
        }
        return rightToLeftIcon;
    }
    
    private Icon getIcon(Component c) {
       if (JTattooUtilities.isLeftToRight(c)) {
           return getLeftToRightIcon();
       } else {
           return getRightToLeftIcon();
       }
    }
    
    public int getIconHeight() {
        Icon ico = getIcon(null);
        if (ico != null) {
            return ico.getIconHeight();
        } else {
            return 16;
        }
    }

    public int getIconWidth() {
        Icon ico = getIcon(null);
        if (ico != null) {
            return ico.getIconWidth();
        } else {
            return 16;
        }
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
        Icon ico = getIcon(c);
        if (ico != null) {
            ico.paintIcon(c, g, x, y);
        } else {
            g.setColor(Color.red);
            g.fillRect(x, y, 16, 16);
            g.setColor(Color.white);
            g.drawLine(x, y, x + 15, y + 15);
            g.drawLine(x + 15, y, x, y + 15);
        }
    }

}
