/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.acryl;

import javax.swing.*;
import javax.swing.plaf.*;

import com.jtattoo.plaf.*;

/**
 * @author  Michael Hagen
 */
public class AcrylRootPaneUI extends BaseRootPaneUI {

    public static ComponentUI createUI(JComponent c) {
        return new AcrylRootPaneUI();
    }

    public BaseTitlePane createTitlePane(JRootPane root) {
        return new AcrylTitlePane(root, this);
    }
}
