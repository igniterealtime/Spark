/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.bernstein;

import java.awt.*;

import com.jtattoo.plaf.*;

/**
 * @author Michael Hagen
 */
public class BernsteinSplitPaneDivider extends BaseSplitPaneDivider {

    public BernsteinSplitPaneDivider(BernsteinSplitPaneUI ui) {
        super(ui);
    }

    public void paint(Graphics g) {
        BernsteinUtils.fillComponent(g, this);
        paintComponents(g);
    }
}
