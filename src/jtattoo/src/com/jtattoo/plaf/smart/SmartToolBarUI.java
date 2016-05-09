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

import com.jtattoo.plaf.*;
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JToolBar;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;

public class SmartToolBarUI extends AbstractToolBarUI {

    public static ComponentUI createUI(JComponent c) {
        return new SmartToolBarUI();
    }

    public Border getRolloverBorder() {
        return SmartBorders.getRolloverToolButtonBorder();
    }

    public Border getNonRolloverBorder() {
        return SmartBorders.getToolButtonBorder();
    }

    public boolean isButtonOpaque() {
        return false;
    }

    public void paint(Graphics g, JComponent c) {
        int w = c.getWidth();
        int h = c.getHeight();
        JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme().getToolBarColors(), 0, 0, w, h - 2);
        if ((toolBar.getOrientation() == JToolBar.HORIZONTAL) && isToolbarDecorated() && isToolBarUnderMenubar()) {
            g.setColor(Color.white);
            g.drawLine(0, 0, w, 0);
            g.drawLine(0, h - 2, w, h - 2);
            g.setColor(ColorHelper.darker(AbstractLookAndFeel.getToolbarColorDark(), 10));
            g.drawLine(0, 1, w, 1);
            g.drawLine(0, h - 1, w, h - 1);
        }
    }
}
