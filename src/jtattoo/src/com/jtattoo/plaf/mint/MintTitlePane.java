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
 
package com.jtattoo.plaf.mint;

import com.jtattoo.plaf.*;
import java.awt.*;
import javax.swing.JRootPane;

/**
 * @author  Michael Hagen
 */
public class MintTitlePane extends BaseTitlePane {

    public MintTitlePane(JRootPane root, BaseRootPaneUI ui) {
        super(root, ui);
    }

    public void createButtons() {
        iconifyButton = new BaseTitleButton(iconifyAction, ICONIFY, iconifyIcon, 1.0f);
        maxButton = new BaseTitleButton(restoreAction, MAXIMIZE, maximizeIcon, 1.0f);
        closeButton = new BaseTitleButton(closeAction, CLOSE, closeIcon, 1.0f);
    }

    public void paintBackground(Graphics g) {
        if (isActive()) {
            Graphics2D g2D = (Graphics2D) g;
            Composite composite = g2D.getComposite();
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, null);
                AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaValue);
                g2D.setComposite(alpha);
            }
            JTattooUtilities.fillVerGradient(g, AbstractLookAndFeel.getTheme().getWindowTitleColors(), 0, 0, getWidth(), getHeight());
            g2D.setComposite(composite);
        } else {
            JTattooUtilities.fillVerGradient(g, AbstractLookAndFeel.getTheme().getWindowInactiveTitleColors(), 0, 0, getWidth(), getHeight());
        }
    }

    public void paintBorder(Graphics g) {
        g.setColor(ColorHelper.darker(AbstractLookAndFeel.getTheme().getWindowTitleColorDark(), 10));
        g.drawLine(0, getHeight() - 2, getWidth(), getHeight() - 2);
        g.setColor(Color.white);
        g.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
    }
}
