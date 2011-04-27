/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import javax.swing.text.JTextComponent;

/**
 * @author Michael Hagen
 */
public class BaseEditorPaneUI extends BasicEditorPaneUI {

    private Border orgBorder = null;

    public static ComponentUI createUI(JComponent c) {
        return new BaseEditorPaneUI();
    }

    public void installDefaults() {
        super.installDefaults();
        updateBackground();
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


    private void updateBackground() {
        JTextComponent c = getComponent();
        if (c.getBackground() instanceof UIResource) {
            if (!c.isEnabled() || !c.isEditable()) {
                c.setBackground(AbstractLookAndFeel.getDisabledBackgroundColor());
            } else {
                c.setBackground(AbstractLookAndFeel.getInputBackgroundColor());
            }
        }
    }
}
