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
 
package com.jtattoo.plaf.texture;

import com.jtattoo.plaf.AbstractLookAndFeel;
import java.awt.*;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuBarUI;


/**
 * @author Michael Hagen
 */
public class TextureMenuBarUI extends BasicMenuBarUI {

    public static ComponentUI createUI(JComponent x) {
        return new TextureMenuBarUI();
    }

    public void installUI(JComponent c) {
        super.installUI(c);
        if ((c != null) && (c instanceof JMenuBar)) {
            ((JMenuBar) c).setBorder(TextureBorders.getMenuBarBorder());
            ((JMenuBar) c).setBorderPainted(true);
        }
    }

    public void paint(Graphics g, JComponent c) {
        TextureUtils.fillComponent(g, c, TextureUtils.MENUBAR_TEXTURE_TYPE);

        if (AbstractLookAndFeel.getTheme().isDarkTexture()) {
            Graphics2D g2D = (Graphics2D) g;
            Composite savedComposite = g2D.getComposite();
            AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f);
            g2D.setComposite(alpha);
            g2D.setColor(Color.black);
            g2D.drawLine(0, 0, c.getWidth() - 1, 0);
            alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f);
            g2D.setComposite(alpha);
            g2D.setColor(Color.white);
            g2D.drawLine(0, c.getHeight() - 1, c.getWidth() - 1, c.getHeight() - 1);
            g2D.setComposite(savedComposite);
        }
    }
}