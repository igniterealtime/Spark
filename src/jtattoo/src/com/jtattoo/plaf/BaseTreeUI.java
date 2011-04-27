/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

/**
 * author Michael Hagen
 */
public class BaseTreeUI extends BasicTreeUI {

    public static ComponentUI createUI(JComponent c) {
        return new BaseTreeUI();
    }

    protected void paintVerticalLine(Graphics g, JComponent c, int x, int top, int bottom) {
        drawDashedVerticalLine(g, x, top, bottom);
    }

    protected void paintHorizontalLine(Graphics g, JComponent c, int y, int left, int right) {
        drawDashedHorizontalLine(g, y, left, right);
    }
}


