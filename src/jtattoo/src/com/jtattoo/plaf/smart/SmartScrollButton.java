/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.smart;

import java.awt.*;

import com.jtattoo.plaf.*;

/**
 * @author  Michael Hagen
 */
public class SmartScrollButton extends BaseScrollButton {

    public SmartScrollButton(int direction, int width) {
        super(direction, width);
    }

    public void paint(Graphics g) {
        super.paint(g);
        if (getModel().isRollover()) {
            g.setColor(SmartLookAndFeel.getFocusColor());
            g.drawLine(1, 1, getWidth() - 2, 1);
            g.drawLine(1, 2, getWidth() - 2, 2);
        }
    }
}
