/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.smart;

import javax.swing.*;
import javax.swing.plaf.*;

import com.jtattoo.plaf.*;

/**
 *
 * @author  Michael Hagen
 */
public class SmartTableHeaderUI extends BaseTableHeaderUI {

    public static ComponentUI createUI(JComponent c) {
        return new SmartTableHeaderUI();
    }

    protected boolean drawRolloverBar() {
        return true;
    }

}
