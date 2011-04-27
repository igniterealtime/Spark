/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.mint;

import java.awt.*;
import javax.swing.*;

import com.jtattoo.plaf.*;

/**
 * @author  Michael Hagen
 */
public class MintTitlePane extends BaseTitlePane {

    public MintTitlePane(JRootPane root, BaseRootPaneUI ui) {
        super(root, ui);
    }

    public void createButtons() {
        iconifyButton = new BaseTitleButton(iconifyAction, ICONIFY, iconifyIcon, 1.0f);
        maxButton = new BaseTitleButton(restoreAction, MAXIMIZE, maximizeIcon, 1.0f);
        closeButton = new BaseTitleButton(closeAction, CLOSE, closeIcon, 1.0f);
    }

    public void paintBackground(Graphics g) {
        if (isActive()) {
            Graphics2D g2D = (Graphics2D) g;
            Composite composite = g2D.getComposite();
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, null);
                AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaValue);
                g2D.setComposite(alpha);
            }
            JTattooUtilities.fillVerGradient(g, MintLookAndFeel.getTheme().getWindowTitleColors(), 0, 0, getWidth(), getHeight());
            g2D.setComposite(composite);
        } else {
            JTattooUtilities.fillVerGradient(g, MintLookAndFeel.getTheme().getWindowInactiveTitleColors(), 0, 0, getWidth(), getHeight());
        }
    }

    public void paintBorder(Graphics g) {
        g.setColor(ColorHelper.darker(MintLookAndFeel.getTheme().getWindowTitleColorDark(), 10));
        g.drawLine(0, getHeight() - 2, getWidth(), getHeight() - 2);
        g.setColor(Color.white);
        g.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
    }
}
