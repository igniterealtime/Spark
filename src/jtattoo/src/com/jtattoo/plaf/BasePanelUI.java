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
import java.lang.reflect.Field;
import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPanelUI;

/**
 * @author Michael Hagen
 */
public class BasePanelUI extends BasicPanelUI {

    private static BasePanelUI panelUI = null;

    public static ComponentUI createUI(JComponent c) {
        if (panelUI == null) {
            panelUI = new BasePanelUI();
        }
        return panelUI;
    }

    protected void installDefaults(JPanel p) {
        super.installDefaults(p);
        p.setFont(AbstractLookAndFeel.getTheme().getControlTextFont());
        
        // We don't want medium weight popups for tool tips, so we try to force heavy weight popups.
        try {
            Field field;
            if (JTattooUtilities.getJavaVersion() < 1.7) {
                Class clazz = Class.forName("javax.swing.PopupFactory");
                field = clazz.getDeclaredField("forceHeavyWeightPopupKey");
            } else { //1.7.0, 1.8.0
                Class clazz = Class.forName("javax.swing.ClientPropertyKey");
                field = clazz.getDeclaredField("PopupFactory_FORCE_HEAVYWEIGHT_POPUP");
            }
            field.setAccessible(true);
            p.putClientProperty(field.get(null), Boolean.TRUE);
        } catch(Exception ex) {
        }        
    }

    public void update(Graphics g, JComponent c) {
        if (c.isOpaque()) {
            Object backgroundTexture = c.getClientProperty("backgroundTexture");
            if (backgroundTexture instanceof Icon) {
                JTattooUtilities.fillComponent(g, c, (Icon)backgroundTexture);
            } else {
                g.setColor(c.getBackground());
                g.fillRect(0, 0, c.getWidth(), c.getHeight());
            }
        }
    }

    public void paint(Graphics g, JComponent c) {
        Graphics2D g2D = (Graphics2D) g;
        Object savedRenderingHint = null;
        if (AbstractLookAndFeel.getTheme().isTextAntiAliasingOn()) {
            savedRenderingHint = g2D.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
            g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, AbstractLookAndFeel.getTheme().getTextAntiAliasingHint());
        }
        super.paint(g, c);
        if (AbstractLookAndFeel.getTheme().isTextAntiAliasingOn()) {
            g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, savedRenderingHint);
        }
    }
}
