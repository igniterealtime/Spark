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
public class BaseSplitPaneUI extends BasicSplitPaneUI {

    public static ComponentUI createUI(JComponent c) {
        return new BaseSplitPaneUI();
    }

    public BasicSplitPaneDivider createDefaultDivider() {
        return new BaseSplitPaneDivider(this);
    }
}
