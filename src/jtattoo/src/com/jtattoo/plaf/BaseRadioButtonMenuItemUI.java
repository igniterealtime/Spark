/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf;

import javax.swing.*;
import javax.swing.plaf.*;

/**
 * @author Michael Hagen
 */
public class BaseRadioButtonMenuItemUI extends BaseMenuItemUI {

    public static ComponentUI createUI(JComponent b) {
        return new BaseRadioButtonMenuItemUI();
    }

    protected void installDefaults() {
        super.installDefaults();
        checkIcon = UIManager.getIcon("RadioButtonMenuItem.checkIcon");
    }

}
