/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.hifi;

import java.awt.*;
import javax.swing.*;

import com.jtattoo.plaf.*;

/**
 * @author  Michael Hagen
 */
public class HiFiScrollButton extends BaseScrollButton {

    protected static Icon upArrowIcon = null;
    protected static Icon downArrowIcon = null;
    protected static Icon leftArrowIcon = null;
    protected static Icon rightArrowIcon = null;

    private static final Color frameHiColor = new Color(128, 128, 128);
    private static final Color frameLoColor = new Color(96, 96, 96);

    public HiFiScrollButton(int direction, int width) {
        super(direction, width);
    }

    public Icon getUpArrowIcon() {
        if (upArrowIcon == null) {
            upArrowIcon = new LazyImageIcon("hifi/icons/UpArrow.gif");
        }
        return upArrowIcon;
    }

    public Icon getDownArrowIcon() {
        if (downArrowIcon == null) {
            downArrowIcon = new LazyImageIcon("hifi/icons/DownArrow.gif");
        }
        return downArrowIcon;
    }

    public Icon getLeftArrowIcon() {
        if (leftArrowIcon == null) {
            leftArrowIcon = new LazyImageIcon("hifi/icons/LeftArrow.gif");
        }
        return leftArrowIcon;
    }

    public Icon getRightArrowIcon() {
        if (rightArrowIcon == null) {
            rightArrowIcon = new LazyImageIcon("hifi/icons/RightArrow.gif");
        }
        return rightArrowIcon;
    }

    public void paint(Graphics g) {
        Graphics2D g2D = (Graphics2D) g;

        boolean isPressed = getModel().isPressed();
        boolean isRollover = getModel().isRollover();

        int width = getWidth();
        int height = getHeight();

        Color[] tc = AbstractLookAndFeel.getTheme().getThumbColors();
        Color c1 = tc[0];
        Color c2 = tc[tc.length - 1];

        if (isPressed) {
            c1 = ColorHelper.darker(c1, 10);
            c2 = ColorHelper.darker(c2, 10);
        } else if (isRollover) {
            c1 = AbstractLookAndFeel.getTheme().getRolloverColorLight();
            c2 = AbstractLookAndFeel.getTheme().getRolloverColorDark();
        }

        g2D.setPaint(new GradientPaint(0, 0, c1, width, height, c2));
        g.fillRect(1, 1, width - 1, height - 1);
        JTattooUtilities.draw3DBorder(g, Color.darkGray, Color.black, 0, 0, width, height);
        g.setColor(frameHiColor);
        g.drawLine(1, 1, width - 2, 1);
        g.drawLine(1, 1, 1, height - 3);
        g.setColor(frameLoColor);
        g.drawLine(width - 2, 1, width - 2, height - 3);
        g.drawLine(2, height - 2, width - 3, height - 2);
        g2D.setPaint(null);

        Composite composite = g2D.getComposite();
        AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
        g2D.setComposite(alpha);
        g2D.setColor(c2);
        g.drawLine(3, 2, width - 4, 2);
        g.drawLine(2, 3, 2, height - 4);
        c2 = ColorHelper.darker(c2, 30);
        g.setColor(c2);
        g.drawLine(width - 1, 1, width - 1, height - 3);
        g.drawLine(3, height - 1, width - 3, height - 1);
        alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f);
        g2D.setComposite(alpha);
        g.drawLine(1, height - 2, 2, height - 1);
        g.drawLine(width - 1, height - 2, width - 2, height - 1);
        g2D.setComposite(composite);

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
