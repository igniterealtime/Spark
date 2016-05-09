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
import com.jtattoo.plaf.BaseButtonUI;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;

/**
 * @author Michael Hagen
 */
public class SmartButtonUI extends BaseButtonUI {

    public static ComponentUI createUI(JComponent c) {
        return new SmartButtonUI();
    }

    protected void paintBackground(Graphics g, AbstractButton b) {
        int w = b.getWidth();
        int h = b.getHeight();
        Graphics2D g2D = (Graphics2D) g;
        Shape savedClip = g.getClip();
        if ((b.getBorder() != null) && b.isBorderPainted() && (b.getBorder() instanceof UIResource)) {
            Area clipArea = new Area(new RoundRectangle2D.Double(0, 0, w - 1, h - 1, 6, 6));
            clipArea.intersect(new Area(savedClip));
            g2D.setClip(clipArea);
        }
        super.paintBackground(g, b);
        if (b.isContentAreaFilled() && b.isRolloverEnabled() && b.getModel().isRollover() && b.isBorderPainted() && (b.getBorder() != null)) {
            g.setColor(AbstractLookAndFeel.getTheme().getFocusColor());
            Insets ins = b.getBorder().getBorderInsets(b);
            if (ins.top == 0) {
                g.drawLine(1, 0, w - 1, 0);
                g.drawLine(1, 1, w - 1, 1);
                g.drawLine(1, 2, w - 1, 2);
            } else {
                g.drawLine(1, 1, w - 1, 1);
                g.drawLine(1, 2, w - 1, 2);
                g.drawLine(1, 3, w - 1, 3);
            }
        }
        g2D.setClip(savedClip);
    }
}


