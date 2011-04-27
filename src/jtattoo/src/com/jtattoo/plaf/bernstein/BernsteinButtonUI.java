/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.bernstein;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;

import com.jtattoo.plaf.*;

/**
 * @author Michael Hagen
 */
public class BernsteinButtonUI extends BaseButtonUI {

    public static ComponentUI createUI(JComponent c) {
        return new BernsteinButtonUI();
    }

    protected void paintBackground(Graphics g, AbstractButton b) {
        if (b.isContentAreaFilled() && !(b.getParent() instanceof JMenuBar)) {
            int width = b.getWidth();
            int height = b.getHeight();
            Color colors[] = null;
            ButtonModel model = b.getModel();
            if (b.isEnabled()) {
                if (b.getBackground() instanceof ColorUIResource) {
                    if (model.isPressed() && model.isArmed()) {
                        colors = BernsteinLookAndFeel.getTheme().getPressedColors();
                    } else if (model.isRollover()) {
                        colors = BernsteinLookAndFeel.getTheme().getRolloverColors();
                    } else {
                        if (JTattooUtilities.isFrameActive(b)) {
                            if (b.equals(b.getRootPane().getDefaultButton())) {
                                colors = BernsteinLookAndFeel.getTheme().getSelectedColors();
                            } else {
                                colors = BernsteinLookAndFeel.getTheme().getButtonColors();
                            }
                        } else {
                            colors = BernsteinLookAndFeel.getTheme().getInActiveColors();
                        }
                    }
                } else {
                    if (model.isPressed() && model.isArmed()) {
                        colors = ColorHelper.createColorArr(b.getBackground(), ColorHelper.darker(b.getBackground(), 50), 20);
                    } else {
                        if (model.isRollover()) {
                            colors = ColorHelper.createColorArr(ColorHelper.brighter(b.getBackground(), 80), ColorHelper.brighter(b.getBackground(), 20), 20);
                        } else {
                            colors = ColorHelper.createColorArr(ColorHelper.brighter(b.getBackground(), 40), ColorHelper.darker(b.getBackground(), 20), 20);
                        }
                    }
                }
            } else {
                colors = BernsteinLookAndFeel.getTheme().getDisabledColors();
            }
            JTattooUtilities.fillHorGradient(g, colors, 0, 0, width, height);
        }
    }
}


