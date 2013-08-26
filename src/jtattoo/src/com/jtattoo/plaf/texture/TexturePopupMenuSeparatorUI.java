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
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSeparatorUI;

/**
 * @author Michael Hagen
 */
public class TexturePopupMenuSeparatorUI extends BasicSeparatorUI {

    private static final Dimension size = new Dimension(8, 8);
    private static final Color colors[] = new Color[] { Color.black, new Color(164, 164, 164), new Color(48, 48, 48), new Color(128, 128, 128) };

    public static ComponentUI createUI(JComponent c) {
        return new TexturePopupMenuSeparatorUI();
    }

    public void paint(Graphics g, JComponent c) {
        TextureUtils.fillComponent(g, c, TextureUtils.MENUBAR_TEXTURE_TYPE);
        if (AbstractLookAndFeel.getTheme().getTextureSet().equals("Default")) {
            Graphics2D g2D = (Graphics2D) g;
            Composite savedComposite = g2D.getComposite();
            AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f);
            g2D.setComposite(alpha);
            int w = c.getWidth();
            drawLine(g, 2, w, colors[0]);
            drawLine(g, 3, w, colors[1]);
            drawLine(g, 4, w, colors[2]);
            drawLine(g, 5, w, colors[3]);
            g2D.setComposite(savedComposite);
        } else {
            Graphics2D g2D = (Graphics2D) g;
            Composite savedComposite = g2D.getComposite();
            AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f);
            g2D.setComposite(alpha);
            if (AbstractLookAndFeel.getTheme().isDarkTexture()) {
                g.setColor(Color.black);
            } else {
                g.setColor(Color.gray);
            }
            g.drawLine(1, 3, c.getWidth() - 2, 3);
            alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f);
            g2D.setComposite(alpha);
            g.setColor(Color.white);
            g.drawLine(1, 4, c.getWidth() - 2, 4);
            g2D.setComposite(savedComposite);
        }
    }

    private void drawLine(Graphics g, int y, int w, Color color) {
        g.setColor(color);
        int dw = 3;
        int dx = 2;
        int x = dx;
        while (x < w) {
            g.drawLine(x, y, x + dw - 1, y);
            x += dx + dw;
        }
    }

    public Dimension getPreferredSize(JComponent c) {
        return size;
    }
}




