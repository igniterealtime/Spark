/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.mcwin;

import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

import com.jtattoo.plaf.*;

/**
 * @author Michael Hagen
 */
public class McWinSplitPaneUI extends BaseSplitPaneUI {

    public static ComponentUI createUI(JComponent c) {
        return new McWinSplitPaneUI();
    }

    public BasicSplitPaneDivider createDefaultDivider() {
        return new McWinSplitPaneDivider(this);
    }
}
