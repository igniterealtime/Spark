/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf;

import javax.swing.*;

/**
 * @author  Michael Hagen
 */
public class NoFocusButton extends JButton {

    public NoFocusButton() {
        super();
        init();
    }

    public NoFocusButton(Icon ico) {
        super(ico);
        init();
    }

    private void init() {
        setFocusPainted(false);
        setRolloverEnabled(true);
        if (JTattooUtilities.getJavaVersion() >= 1.4) {
            setFocusable(false);
        }
    }

    public boolean isFocusTraversable() {
        return false;
    }

    public void requestFocus() {
    }
}
