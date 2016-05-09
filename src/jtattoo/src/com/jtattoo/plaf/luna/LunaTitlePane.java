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
 
package com.jtattoo.plaf.luna;

import com.jtattoo.plaf.*;
import java.awt.*;
import javax.swing.JRootPane;

/**
 * @author  Michael Hagen
 */
public class LunaTitlePane extends BaseTitlePane {

    public LunaTitlePane(JRootPane root, BaseRootPaneUI ui) {
        super(root, ui);
    }

    protected int getHorSpacing() {
        return 2;
    }

    protected int getVerSpacing() {
        return 5;
    }

    protected boolean centerButtons() {
        return false;
    }

    public void paintBorder(Graphics g) {
        if (isActive()) {
            g.setColor(AbstractLookAndFeel.getTheme().getFrameColor());
        } else {
            g.setColor(ColorHelper.brighter(AbstractLookAndFeel.getTheme().getFrameColor(), 40));
        }
        g.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
    }

    public void paintText(Graphics g, int x, int y, String title) {
        x += paintIcon(g, x, y);
        if (isActive()) {
            g.setColor(ColorHelper.brighter(AbstractLookAndFeel.getTheme().getWindowBorderColor(), 10));
            JTattooUtilities.drawString(rootPane, g, title, x - 1, y - 2);
            g.setColor(ColorHelper.darker(AbstractLookAndFeel.getTheme().getWindowBorderColor(), 25));
            JTattooUtilities.drawString(rootPane, g, title, x + 1, y);
            g.setColor(AbstractLookAndFeel.getWindowTitleForegroundColor());
        } else {
            g.setColor(AbstractLookAndFeel.getWindowInactiveTitleForegroundColor());
        }
        JTattooUtilities.drawString(rootPane, g, title, x, y - 1);
    }

}
