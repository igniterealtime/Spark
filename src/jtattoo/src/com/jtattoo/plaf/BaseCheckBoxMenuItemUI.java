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
public class BaseCheckBoxMenuItemUI extends BaseMenuItemUI {

    public static ComponentUI createUI(JComponent c) {
        return new BaseCheckBoxMenuItemUI();
    }

    protected void installDefaults() {
        super.installDefaults();
        checkIcon = UIManager.getIcon("CheckBoxMenuItem.checkIcon");
    }

}
