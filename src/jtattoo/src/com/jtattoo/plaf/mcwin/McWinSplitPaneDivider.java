/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.mcwin;

import java.awt.*;

import com.jtattoo.plaf.*;

/**
 * @author Michael Hagen
 */
public class McWinSplitPaneDivider extends BaseSplitPaneDivider {

    public McWinSplitPaneDivider(McWinSplitPaneUI ui) {
        super(ui);
    }

    public void paint(Graphics g) {
        McWinUtils.fillComponent(g, this);
        paintComponents(g);
    }
}
