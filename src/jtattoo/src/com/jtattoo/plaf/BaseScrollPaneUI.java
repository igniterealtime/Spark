/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf;

import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

/**
 * @author Michael Hagen
 */
public class BaseScrollPaneUI extends BasicScrollPaneUI {

    public static ComponentUI createUI(JComponent c) {
        return new BaseScrollPaneUI();
    }

    public void installDefaults(JScrollPane p) {
        super.installDefaults(p);
        p.setFont(AbstractLookAndFeel.getTheme().getControlTextFont());
        p.setBackground(AbstractLookAndFeel.getTheme().getBackgroundColor());
        p.getViewport().setBackground(AbstractLookAndFeel.getTheme().getBackgroundColor());
    }
}
