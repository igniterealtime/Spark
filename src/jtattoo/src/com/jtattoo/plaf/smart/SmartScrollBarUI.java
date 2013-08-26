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
 
package com.jtattoo.plaf.smart;

import com.jtattoo.plaf.AbstractLookAndFeel;
import com.jtattoo.plaf.BaseScrollBarUI;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.*;
import javax.swing.plaf.ComponentUI;

/**
 *
 * @author  Michael Hagen
 */
public class SmartScrollBarUI extends BaseScrollBarUI {

    public static ComponentUI createUI(JComponent c) {
        return new SmartScrollBarUI();
    }

    protected JButton createDecreaseButton(int orientation) {
        return new SmartScrollButton(orientation, scrollBarWidth);
    }

    protected JButton createIncreaseButton(int orientation) {
        return new SmartScrollButton(orientation, scrollBarWidth);
    }

    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        super.paintThumb(g, c, thumbBounds);
        if (isRollover) {
            g.setColor(AbstractLookAndFeel.getFocusColor());
            if (scrollbar.getOrientation() == JScrollBar.VERTICAL) {
                g.drawLine(thumbBounds.x + 1, thumbBounds.y + 1, thumbBounds.x + thumbBounds.width - 2, thumbBounds.y + 1);
                g.drawLine(thumbBounds.x + 1, thumbBounds.y + 2, thumbBounds.x + thumbBounds.width - 2, thumbBounds.y + 2);
            } else {
                g.drawLine(thumbBounds.x + 1, thumbBounds.y + 1, thumbBounds.x + 1, thumbBounds.y + thumbBounds.height - 2);
                g.drawLine(thumbBounds.x + 2, thumbBounds.y + 1, thumbBounds.x + 2, thumbBounds.y + thumbBounds.height - 2);
            }
        }
    }
}
