/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.bernstein;

import java.awt.*;
import javax.swing.*;

import com.jtattoo.plaf.*;

/**
 * @author  Michael Hagen
 */
public class BernsteinTitlePane extends BaseTitlePane {

    public BernsteinTitlePane(JRootPane root, BaseRootPaneUI ui) {
        super(root, ui);
    }

    public void paintBackground(Graphics g) {
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, null);
        } else {
            BernsteinUtils.fillComponent(g, this);
        }
        Graphics2D g2D = (Graphics2D) g;
        Composite composite = g2D.getComposite();
        AlphaComposite alpha = null;
        if (backgroundImage != null) {
            alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaValue);
        } else {
            alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f);
        }
        g2D.setComposite(alpha);
        JTattooUtilities.fillHorGradient(g, BernsteinLookAndFeel.getTheme().getDefaultColors(), 0, 0, getWidth(), getHeight());
        g2D.setComposite(composite);
    }

    public void paintBorder(Graphics g) {
        if (isActive()) {
            g.setColor(AbstractLookAndFeel.getTheme().getWindowBorderColor());
        } else {
            g.setColor(AbstractLookAndFeel.getTheme().getWindowInactiveBorderColor());
        }
        g.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
    }
}
