/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.luna;

import javax.swing.*;
import javax.swing.plaf.*;

import com.jtattoo.plaf.*;

/**
 * @author  Michael Hagen
 */
public class LunaRootPaneUI extends BaseRootPaneUI {

    public static ComponentUI createUI(JComponent c) {
        return new LunaRootPaneUI();
    }

    public BaseTitlePane createTitlePane(JRootPane root) {
        return new LunaTitlePane(root, this);
    }
}
