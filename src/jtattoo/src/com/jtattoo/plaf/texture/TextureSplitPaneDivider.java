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
import com.jtattoo.plaf.BaseSplitPaneDivider;
import java.awt.*;
import javax.swing.*;

/**
 * @author Michael Hagen
 */
public class TextureSplitPaneDivider extends BaseSplitPaneDivider {

    public TextureSplitPaneDivider(TextureSplitPaneUI ui) {
        super(ui);
    }

    public void paint(Graphics g) {
        if (!isFlatMode()) {
            TextureUtils.fillComponent(g, this, TextureUtils.getTextureType(splitPane));

            Graphics2D g2D = (Graphics2D) g;
            Composite savedComposite = g2D.getComposite();
            AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
            g2D.setComposite(alpha);

            int width = getSize().width;
            int height = getSize().height;
            int dx = 0;
            int dy = 0;
            if ((width % 2) == 1) {
                dx = 1;
            }
            if ((height % 2) == 1) {
                dy = 1;
            }

            Icon horBumps = null;
            Icon verBumps = null;
            if (UIManager.getLookAndFeel() instanceof AbstractLookAndFeel) {
                AbstractLookAndFeel laf = (AbstractLookAndFeel) UIManager.getLookAndFeel();
                horBumps = laf.getIconFactory().getSplitterHorBumpIcon();
                verBumps = laf.getIconFactory().getSplitterVerBumpIcon();
            }
            if (orientation == JSplitPane.HORIZONTAL_SPLIT) {
                if ((horBumps != null) && (width > horBumps.getIconWidth())) {
                    if (splitPane.isOneTouchExpandable() && centerOneTouchButtons) {
                        int centerY = height / 2;
                        int x = (width - horBumps.getIconWidth()) / 2 + dx;
                        int y = centerY - horBumps.getIconHeight() - 40;
                        horBumps.paintIcon(this, g, x, y);
                        y = centerY + 40;
                        horBumps.paintIcon(this, g, x, y);
                    } else {
                        int x = (width - horBumps.getIconWidth()) / 2 + dx;
                        int y = (height - horBumps.getIconHeight()) / 2;
                        horBumps.paintIcon(this, g, x, y);
                    }
                }
            } else {
                if ((verBumps != null) && (height > verBumps.getIconHeight())) {
                    if (splitPane.isOneTouchExpandable() && centerOneTouchButtons) {
                        int centerX = width / 2;
                        int x = centerX - verBumps.getIconWidth() - 40;
                        int y = (height - verBumps.getIconHeight()) / 2 + dy;
                        verBumps.paintIcon(this, g, x, y);
                        x = centerX + 40;
                        verBumps.paintIcon(this, g, x, y);
                    } else {
                        int x = (width - verBumps.getIconWidth()) / 2;
                        int y = (height - verBumps.getIconHeight()) / 2 + dy;
                        verBumps.paintIcon(this, g, x, y);
                    }
                }
            }
            g2D.setComposite(savedComposite);
        }
        paintComponents(g);
    }
}
