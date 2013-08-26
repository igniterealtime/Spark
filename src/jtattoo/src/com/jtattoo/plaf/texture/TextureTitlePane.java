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

import com.jtattoo.plaf.*;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import javax.swing.JRootPane;

/**
 * @author  Michael Hagen
 */
public class TextureTitlePane extends BaseTitlePane {
    
    public TextureTitlePane(JRootPane root, BaseRootPaneUI ui) {
        super(root, ui); 
    }

    protected int getHorSpacing() {
        return AbstractLookAndFeel.getTheme().isMacStyleWindowDecorationOn() ? 1 : 0;
    }
    
    protected int getVerSpacing() {
        return AbstractLookAndFeel.getTheme().isMacStyleWindowDecorationOn() ? 3 : 0;
    }

    protected boolean centerButtons() {
        return false;
    }

    public void paintBorder(Graphics g) {
    }

    public void paintBackground(Graphics g) {
        TextureUtils.fillComponent(g, this, TextureUtils.WINDOW_TEXTURE_TYPE);
    }

    public void paintText(Graphics g, int x, int y, String title) {
        x += paintIcon(g, x, y);
        Graphics2D g2D = (Graphics2D)g;
        Shape savedClip = g2D.getClip();
        Color fc = AbstractLookAndFeel.getWindowTitleForegroundColor();
        if (fc.equals(Color.white)) {
            Color bc = AbstractLookAndFeel.getWindowTitleColorDark();
            g2D.setColor(bc);
            JTattooUtilities.drawString(rootPane, g, title, x-1, y-1);
            g2D.setColor(ColorHelper.darker(bc, 30));
            JTattooUtilities.drawString(rootPane, g, title, x+1, y+1);
        }
        g.setColor(fc);

        Area clipArea = new Area(new Rectangle2D.Double(x, 0, getWidth(), getHeight() / 2));
        clipArea.intersect(new Area(savedClip));
        g2D.setClip(clipArea);
        JTattooUtilities.drawString(rootPane, g, title, x, y);

        clipArea = new Area(new Rectangle2D.Double(x, (getHeight() / 2), getWidth(), getHeight()));
        clipArea.intersect(new Area(savedClip));
        g2D.setClip(clipArea);
        g.setColor(ColorHelper.darker(fc, 20));
        JTattooUtilities.drawString(rootPane, g, title, x, y);

        g2D.setClip(savedClip);
    }
    
}
