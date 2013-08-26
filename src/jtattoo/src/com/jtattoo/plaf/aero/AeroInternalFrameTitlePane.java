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
 
package com.jtattoo.plaf.aero;

import com.jtattoo.plaf.*;
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.*;

/**
 * @author Michael Hagen
 */
public class AeroInternalFrameTitlePane extends BaseInternalFrameTitlePane {

    public AeroInternalFrameTitlePane(JInternalFrame f) {
        super(f);
    }

    protected int getHorSpacing() {
        return AbstractLookAndFeel.getTheme().isMacStyleWindowDecorationOn() ? 1 : 0;
    }

    protected int getVerSpacing() {
        return AbstractLookAndFeel.getTheme().isMacStyleWindowDecorationOn() ? 3 : 0;
    }

    protected void createButtons() {
        if (AbstractLookAndFeel.getTheme().isMacStyleWindowDecorationOn()) {
            super.createButtons();
        } else {
            iconButton = new TitleButton(iconifyAction, ICONIFY, iconIcon);
            maxButton = new TitleButton(maximizeAction, MAXIMIZE, maxIcon);
            closeButton = new TitleButton(closeAction, CLOSE, closeIcon);
            setButtonIcons();
        }
    }

    public void paintBorder(Graphics g) {
        if (isActive()) {
            g.setColor(ColorHelper.brighter(AbstractLookAndFeel.getWindowTitleColorDark(), 50));
        } else {
            g.setColor(ColorHelper.darker(AbstractLookAndFeel.getWindowInactiveTitleColorDark(), 10));
        }
        g.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
    }

    public void paintText(Graphics g, int x, int y, String title) {
        if (isMacStyleWindowDecoration()) {
            x += paintIcon(g, x, y) + 5;
        }
        if (isActive()) {
            Color titleColor = AbstractLookAndFeel.getWindowTitleForegroundColor();
            if (ColorHelper.getGrayValue(titleColor) > 164) {
                g.setColor(Color.black);
            } else {
                g.setColor(Color.white);
            }
            JTattooUtilities.drawString(frame, g, title, x + 1, y + 1);
            g.setColor(AbstractLookAndFeel.getWindowTitleForegroundColor());
            JTattooUtilities.drawString(frame, g, title, x, y);
        }
        else {
            g.setColor(AbstractLookAndFeel.getWindowInactiveTitleForegroundColor());
            JTattooUtilities.drawString(frame, g, title, x, y);
        }
    }

//------------------------------------------------------------------------------
    private class TitleButton extends BaseTitleButton {

        public TitleButton(Action action, String accessibleName, Icon icon) {
            super(action, accessibleName, icon, 1.0f);
        }

        public void paint(Graphics g) {
            boolean isPressed = getModel().isPressed();
            boolean isArmed = getModel().isArmed();
            boolean isRollover = getModel().isRollover();
            int width = getWidth();
            int height = getHeight();
            Color colors[] = AbstractLookAndFeel.getTheme().getButtonColors();
            if (isRollover) {
                colors = AbstractLookAndFeel.getTheme().getRolloverColors();
            }
            if (isPressed && isArmed) {
                colors = AbstractLookAndFeel.getTheme().getPressedColors();
            }
            JTattooUtilities.fillHorGradient(g, colors, 0, 0, width, height);
            g.setColor(Color.lightGray);
            g.drawLine(0, 0, 0, height);
            g.drawLine(0, height - 1, width, height - 1);
            g.setColor(Color.white);
            g.drawLine(1, 0, 1, height - 2);
            getIcon().paintIcon(this, g, 1, 0);
        }
    }

}
