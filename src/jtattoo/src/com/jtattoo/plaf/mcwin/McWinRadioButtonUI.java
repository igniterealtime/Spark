/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.mcwin;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;

import com.jtattoo.plaf.*;

/**
 * @author Michael Hagen
 */
public class McWinRadioButtonUI extends BaseRadioButtonUI {

    private static McWinRadioButtonUI radioButtonUI = null;

    public static ComponentUI createUI(JComponent c) {
        if (radioButtonUI == null) {
            radioButtonUI = new McWinRadioButtonUI();
        }
        return radioButtonUI;
    }

    public void paintBackground(Graphics g, JComponent c) {
        if (c.isOpaque()) {
            if ((c.getBackground().equals(AbstractLookAndFeel.getBackgroundColor())) && (c.getBackground() instanceof ColorUIResource)) {
                McWinUtils.fillComponent(g, c);
            } else {
                g.setColor(c.getBackground());
                g.fillRect(0, 0, c.getWidth(), c.getHeight());
            }
        }
    }
}
