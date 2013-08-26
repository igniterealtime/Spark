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
import javax.swing.Icon;

/**
 * @author  Michael Hagen
 */
public abstract class XPScrollButton extends BaseScrollButton {

    public XPScrollButton(int direction, int width) {
        super(direction, width);
    }

    public abstract Icon getUpArrowIcon();
    public abstract Icon getDownArrowIcon();
    public abstract Icon getLeftArrowIcon();
    public abstract Icon getRightArrowIcon();

    public Color getFrameColor() {
        return Color.white;
    }

    public void paint(Graphics g) {
        Graphics2D g2D = (Graphics2D) g;
        Composite savedComposite = g2D.getComposite();
        Paint savedPaint = g2D.getPaint();

        boolean isPressed = getModel().isPressed();
        boolean isRollover = getModel().isRollover();

        int width = getWidth();
        int height = getHeight();

        Color[] tc = AbstractLookAndFeel.getTheme().getThumbColors();
        Color c1 = tc[0];
        Color c2 = tc[tc.length - 1];
        if (isPressed) {
            c1 = ColorHelper.darker(c1, 5);
            c2 = ColorHelper.darker(c2, 5);
        } else if (isRollover) {
            c1 = ColorHelper.brighter(c1, 20);
            c2 = ColorHelper.brighter(c2, 20);
        }

        g2D.setPaint(new GradientPaint(0, 0, c1, width, height, c2));
        g.fillRect(0, 0, width, height);
        g2D.setPaint(savedPaint);

        g.setColor(getFrameColor());
        g.drawLine(1, 1, width - 2, 1);
        g.drawLine(1, 1, 1, height - 3);
        g.drawLine(width - 2, 1, width - 2, height - 3);
        g.drawLine(2, height - 2, width - 3, height - 2);

        AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
        g2D.setComposite(alpha);
        g2D.setColor(c2);
        g.drawLine(2, 2, width - 3, 2);
        g.drawLine(2, 3, 2, height - 3);

        g.setColor(ColorHelper.darker(c2, 40));
        g.drawLine(width - 1, 2, width - 1, height - 3);
        g.drawLine(3, height - 1, width - 3, height - 1);
        alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f);
        g2D.setComposite(alpha);
        g.drawLine(1, height - 2, 2, height - 1);
        g.drawLine(width - 1, height - 2, width - 2, height - 1);

        g2D.setComposite(savedComposite);

        // paint the icon
        if (getDirection() == NORTH) {
            int x = (width / 2) - (getUpArrowIcon().getIconWidth() / 2);
            int y = (height / 2) - (getUpArrowIcon().getIconHeight() / 2);
            getUpArrowIcon().paintIcon(this, g, x, y);
        } else if (getDirection() == SOUTH) {
            int x = (width / 2) - (getDownArrowIcon().getIconWidth() / 2);
            int y = (height / 2) - (getDownArrowIcon().getIconHeight() / 2) + 1;
            getDownArrowIcon().paintIcon(this, g, x, y);
        } else if (getDirection() == WEST) {
            int x = (width / 2) - (getLeftArrowIcon().getIconWidth() / 2);
            int y = (height / 2) - (getLeftArrowIcon().getIconHeight() / 2);
            getLeftArrowIcon().paintIcon(this, g, x, y);
        } else {
            int x = (width / 2) - (getRightArrowIcon().getIconWidth() / 2) + 1;
            int y = (height / 2) - (getRightArrowIcon().getIconHeight() / 2);
            getRightArrowIcon().paintIcon(this, g, x, y);
        }
    }

    public Dimension getPreferredSize() {
        if (getDirection() == NORTH) {
            return new Dimension(buttonWidth, buttonWidth);
        } else if (getDirection() == SOUTH) {
            return new Dimension(buttonWidth, buttonWidth);
        } else if (getDirection() == EAST) {
            return new Dimension(buttonWidth, buttonWidth);
        } else if (getDirection() == WEST) {
            return new Dimension(buttonWidth, buttonWidth);
        } else {
            return new Dimension(0, 0);
        }
    }
}
