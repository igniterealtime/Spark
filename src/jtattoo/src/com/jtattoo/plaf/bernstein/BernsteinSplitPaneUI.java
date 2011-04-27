/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.bernstein;

import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.BasicSplitPaneDivider;

import com.jtattoo.plaf.*;

/**
 * @author Michael Hagen
 */
public class BernsteinSplitPaneUI extends BaseSplitPaneUI {

    public static ComponentUI createUI(JComponent c) {
        return new BernsteinSplitPaneUI();
    }

    public BasicSplitPaneDivider createDefaultDivider() {
        return new BernsteinSplitPaneDivider(this);
    }
}
