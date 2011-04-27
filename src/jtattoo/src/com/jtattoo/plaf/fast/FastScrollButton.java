/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.fast;

import java.awt.*;

import com.jtattoo.plaf.*;
import javax.swing.Icon;

public class FastScrollButton extends BaseScrollButton {

    public FastScrollButton(int direction, int width) {
        super(direction, width);
    }

    public void paint(Graphics g) {
        boolean isPressed = getModel().isPressed();

        int width = getWidth();
        int height = getHeight();

        Color backColor = FastLookAndFeel.getTheme().getControlBackgroundColor();
        if (!JTattooUtilities.isActive(this)) {
            backColor = ColorHelper.brighter(backColor, 50);
        }
        if (isPressed) {
            backColor = ColorHelper.darker(backColor, 10);
        }
        Color hiColor = ColorHelper.brighter(backColor, 40);
        Color loColor = ColorHelper.darker(backColor, 30);
        g.setColor(backColor);
        g.fillRect(0, 0, width, height);

        if (getDirection() == NORTH) {
            Icon upArrow = BaseIcons.getUpArrowIcon();
            int x = (width / 2) - (upArrow.getIconWidth() / 2);
            int y = (height / 2) - (upArrow.getIconHeight() / 2) - 1;
            upArrow.paintIcon(this, g, x, y);
        } else if (getDirection() == SOUTH) {
            Icon downArrow = BaseIcons.getDownArrowIcon();
            int x = (width / 2) - (downArrow.getIconWidth() / 2);
            int y = (height / 2) - (downArrow.getIconHeight() / 2);
            downArrow.paintIcon(this, g, x, y);
        } else if (getDirection() == WEST) {
            Icon leftArrow = BaseIcons.getLeftArrowIcon();
            int x = (width / 2) - (leftArrow.getIconWidth() / 2) - 1;
            int y = (height / 2) - (leftArrow.getIconHeight() / 2);
            leftArrow.paintIcon(this, g, x, y);
        } else {
            Icon rightArrow = BaseIcons.getRightArrowIcon();
            int x = (width / 2) - (rightArrow.getIconWidth() / 2);
            int y = (height / 2) - (rightArrow.getIconHeight() / 2);
            rightArrow.paintIcon(this, g, x, y);
        }

        JTattooUtilities.draw3DBorder(g, ColorHelper.brighter(loColor, 20), loColor, 0, 0, width, height);
        if (!isPressed) {
            g.setColor(hiColor);
            g.drawLine(1, 1, width - 2, 1);
            g.drawLine(1, 1, 1, height - 2);
        }
    }
}

