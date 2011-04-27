/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf;

import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.BasicPasswordFieldUI;

/**
 * @author Michael Hagen
 */
public class BasePasswordFieldUI extends BasicPasswordFieldUI {

    private Border orgBorder = null;

    public static ComponentUI createUI(JComponent c) {
        return new BasePasswordFieldUI();
    }

    protected void installListeners() {
        super.installListeners();

        if (AbstractLookAndFeel.getTheme().doShowFocusFrame()) {
            getComponent().addFocusListener(new FocusListener() {

                public void focusGained(FocusEvent e) {
                    if (getComponent() != null) {
                        orgBorder = getComponent().getBorder();
                        LookAndFeel laf = UIManager.getLookAndFeel();
                        if (laf instanceof AbstractLookAndFeel) {
                            Border focusBorder = ((AbstractLookAndFeel)laf).getBorderFactory().getFocusFrameBorder();
                            getComponent().setBorder(focusBorder);
                        }
                    }
                }

                public void focusLost(FocusEvent e) {
                    if (getComponent() != null) {
                        getComponent().setBorder(orgBorder);
                    }
                }
            });
        }
    }

    protected void paintBackground(Graphics g) {
        Color orgBackgroundColor = getComponent().getBackground();
        if (AbstractLookAndFeel.getTheme().doShowFocusFrame()) {
            if (getComponent().hasFocus() && getComponent().isEditable()) {
                getComponent().setBackground(AbstractLookAndFeel.getTheme().getFocusBackgroundColor());
            }
        }
        super.paintBackground(g);
        getComponent().setBackground(orgBackgroundColor);
    }

    protected void paintSafely(Graphics g) {
        Graphics2D g2D = (Graphics2D) g;
        Object savedRenderingHint = null;
        if (AbstractLookAndFeel.getTheme().isTextAntiAliasingOn()) {
            savedRenderingHint = g2D.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
            g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, AbstractLookAndFeel.getTheme().getTextAntiAliasingHint());
        }
        super.paintSafely(g);
        if (AbstractLookAndFeel.getTheme().isTextAntiAliasingOn()) {
            g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, savedRenderingHint);
        }
    }
}
