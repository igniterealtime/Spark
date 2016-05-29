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

package com.jtattoo.plaf;

import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicEditorPaneUI;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;

/**
 * @author Michael Hagen
 */
public class BaseEditorPaneUI extends BasicEditorPaneUI {

    private Border orgBorder = null;
    private FocusListener focusListener = null;

    public static ComponentUI createUI(JComponent c) {
        return new BaseEditorPaneUI();
    }

    public void installDefaults() {
        super.installDefaults();
        updateBackground();
    }

    protected void installKeyboardActions() {
        super.installKeyboardActions();
        if (JTattooUtilities.isMac()) {
            InputMap im = (InputMap) UIManager.get("TextField.focusInputMap");
            int commandKey = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, commandKey), DefaultEditorKit.copyAction);
            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, commandKey), DefaultEditorKit.pasteAction);
            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_X, commandKey), DefaultEditorKit.cutAction);
            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, KeyEvent.ALT_DOWN_MASK), DefaultEditorKit.nextWordAction);
            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, KeyEvent.ALT_DOWN_MASK), DefaultEditorKit.previousWordAction);
        }
    }

    protected void installListeners() {
        super.installListeners();

        if (AbstractLookAndFeel.getTheme().doShowFocusFrame()) {
            focusListener = new FocusListener() {

                public void focusGained(FocusEvent e) {
                    if (getComponent() != null) {
                        orgBorder = getComponent().getBorder();
                        LookAndFeel laf = UIManager.getLookAndFeel();
                        if (laf instanceof AbstractLookAndFeel && orgBorder instanceof UIResource) {
                            Border focusBorder = ((AbstractLookAndFeel)laf).getBorderFactory().getFocusFrameBorder();
                            getComponent().setBorder(focusBorder);
                        }
                        getComponent().invalidate();
                        getComponent().repaint();
                    }
                }

                public void focusLost(FocusEvent e) {
                    if (getComponent() != null) {
                        if (orgBorder instanceof UIResource) {
                            getComponent().setBorder(orgBorder);
                        }
                        getComponent().invalidate();
                        getComponent().repaint();
                    }
                }
            };
            getComponent().addFocusListener(focusListener);
        }
    }

    protected void uninstallListeners() {
        getComponent().removeFocusListener(focusListener);
        focusListener = null;
        super.uninstallListeners();
    }

    protected void paintBackground(Graphics g) {
        g.setColor(getComponent().getBackground());
        if (AbstractLookAndFeel.getTheme().doShowFocusFrame()) {
            if (getComponent().hasFocus() && getComponent().isEditable()) {
                g.setColor(AbstractLookAndFeel.getTheme().getFocusBackgroundColor());
            }
        }
        g.fillRect(0, 0, getComponent().getWidth(), getComponent().getHeight());
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
