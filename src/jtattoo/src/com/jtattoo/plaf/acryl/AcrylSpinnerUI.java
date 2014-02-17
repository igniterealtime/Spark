/*
* Copyright (c) 2002 and later by MH Software-Entwicklung. All Rights Reserved.
*  
* JTattoo is multiple licensed. If your are an open source developer you can use
* it under the terms and conditions of the GNU General Public License version 2.0
* or later as published by the Free Software Foundation.
*  
* see: gpl-2.0.txt
* 
* If you pay for a license you will become a registered user who could use the
* software under the terms and conditions of the GNU Lesser General Public License
* version 2.0 or later with classpath exception as published by the Free Software
* Foundation.
* 
* see: lgpl-2.0.txt
* see: classpath-exception.txt
* 
* Registered users could also use JTattoo under the terms and conditions of the 
* Apache License, Version 2.0 as published by the Apache Software Foundation.
*  
* see: APACHE-LICENSE-2.0.txt
*/
 
package com.jtattoo.plaf.acryl;

import com.jtattoo.plaf.*;
import java.awt.Color;
import java.awt.Component;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;

/**
 *
 * @author Michael Hagen
 */
public class AcrylSpinnerUI extends BaseSpinnerUI {

    /**
     * Returns a new instance of AcrylSpinnerUI. SpinnerListUI
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
