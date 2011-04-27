/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf;

import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.metal.MetalToolTipUI;

/**
 * @author Michael Hagen
 */
public class BaseToolTipUI extends MetalToolTipUI {

    public static ComponentUI createUI(JComponent c) {
        return new BaseToolTipUI();
    }
}
