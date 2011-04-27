/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.acryl;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.border.*;

import com.jtattoo.plaf.*;

/**
 *
 * @author Michael Hagen
 */
public class AcrylSpinnerUI extends BaseSpinnerUI {

    /**
     * Returns a new instance of BaseSpinnerUI.  SpinnerListUI 
     * delegates are allocated one per JSpinner.  
     * 
     * @param c the JSpinner (not used)
     * @see ComponentUI#createUI
     * @return a new BasicSpinnerUI object
     */
    public static ComponentUI createUI(JComponent c) {
        return new AcrylSpinnerUI();
    }

    protected Component createNextButton() {
        JButton button = (JButton) super.createNextButton();
        Color frameColor = ColorHelper.brighter(AbstractLookAndFeel.getFrameColor(), 50);
        if (JTattooUtilities.isLeftToRight(spinner)) {
            Border border = BorderFactory.createMatteBorder(0, 1, 1, 0, frameColor);
            button.setBorder(border);
        } else {
            Border border = BorderFactory.createMatteBorder(0, 0, 1, 1, frameColor);
            button.setBorder(border);
        }
        return button;
    }

    protected Component createPreviousButton() {
        JButton button = (JButton) super.createPreviousButton();
        Color frameColor = ColorHelper.brighter(AbstractLookAndFeel.getFrameColor(), 50);
        if (JTattooUtilities.isLeftToRight(spinner)) {
            Border border = BorderFactory.createMatteBorder(0, 1, 0, 0, frameColor);
            button.setBorder(border);
        } else {
            Border border = BorderFactory.createMatteBorder(0, 0, 0, 1, frameColor);
            button.setBorder(border);
        }
        return button;
    }
}
