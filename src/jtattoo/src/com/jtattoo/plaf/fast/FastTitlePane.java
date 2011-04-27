/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.fast;

import java.awt.*;
import javax.swing.*;

import com.jtattoo.plaf.*;

/**
 * @author  Michael Hagen
 */
public class FastTitlePane extends BaseTitlePane {

    public FastTitlePane(JRootPane root, BaseRootPaneUI ui) {
        super(root, ui);
    }

    public void paintBackground(Graphics g) {
        if (isActive()) {
            g.setColor(AbstractLookAndFeel.getWindowTitleBackgroundColor());
            g.fillRect(0, 0, getWidth(), getHeight());
        } else {
            g.setColor(AbstractLookAndFeel.getWindowInactiveTitleBackgroundColor());
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    public void paintBorder(Graphics g) {
        Color borderColor = FastLookAndFeel.getWindowInactiveBorderColor();
        if (isActive()) {
            borderColor = FastLookAndFeel.getWindowBorderColor();
        }
        JTattooUtilities.draw3DBorder(g, ColorHelper.brighter(borderColor, 30), ColorHelper.darker(borderColor, 5), 0, 0, getWidth(), getHeight());
    }
}
