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
 
package com.jtattoo.plaf.aluminium;

import com.jtattoo.plaf.*;
import java.awt.Graphics;
import javax.swing.JInternalFrame;

/**
 * @author Michael Hagen
 */
public class AluminiumInternalFrameTitlePane extends BaseInternalFrameTitlePane {

    public AluminiumInternalFrameTitlePane(JInternalFrame f) {
        super(f);
    }

    protected boolean centerButtons() {
        return false;
    }
    
    public void paintBorder(Graphics g) {
        if (isActive()) {
            g.setColor(AbstractLookAndFeel.getTheme().getWindowBorderColor());
        } else {
            g.setColor(AbstractLookAndFeel.getTheme().getWindowInactiveBorderColor());
        }
        g.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
    }

    public void paintText(Graphics g, int x, int y, String title) {
        if (isMacStyleWindowDecoration()) {
            x += paintIcon(g, x, y) + 5;
        }
        if (isActive()) {
            g.setColor(AbstractLookAndFeel.getWindowTitleBackgroundColor());
            JTattooUtilities.drawString(frame, g, title, x + 1, y - 1);
            g.setColor(AbstractLookAndFeel.getWindowTitleForegroundColor());
            JTattooUtilities.drawString(frame, g, title, x, y - 2);
        } else {
            g.setColor(AbstractLookAndFeel.getWindowTitleBackgroundColor());
            JTattooUtilities.drawString(frame, g, title, x + 1, y - 1);
            g.setColor(AbstractLookAndFeel.getWindowInactiveTitleForegroundColor());
            JTattooUtilities.drawString(frame, g, title, x, y - 2);
        }
    }
}
