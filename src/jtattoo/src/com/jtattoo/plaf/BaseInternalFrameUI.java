/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf;

import java.beans.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

public class BaseInternalFrameUI extends BasicInternalFrameUI {

    private static final PropertyChangeListener myPropertyChangeListener = new MyPropertyChangeHandler();

    public BaseInternalFrameUI(JInternalFrame b) {
        super(b);
    }

    public static ComponentUI createUI(JComponent c) {
        return new BaseInternalFrameUI((JInternalFrame) c);
    }

    public void installUI(JComponent c) {
        super.installUI(c);
        Object paletteProp = c.getClientProperty("JInternalFrame.isPalette");
        if (paletteProp != null) {
            setPalette(((Boolean) paletteProp).booleanValue());
        }
        stripContentBorder();
    }

    protected void installListeners() {
        super.installListeners();
        frame.addPropertyChangeListener(myPropertyChangeListener);
    }

    protected void uninstallListeners() {
        frame.removePropertyChangeListener(myPropertyChangeListener);
        super.uninstallListeners();
    }

    protected void uninstallComponents() {
        titlePane = null;
        super.uninstallComponents();
    }

    public void stripContentBorder() {
        Container content = frame.getContentPane();
        if (content instanceof JComponent) {
            JComponent contentPane = (JComponent) content;
            contentPane.setBorder(BorderFactory.createEmptyBorder());
        }
    }

    protected JComponent createNorthPane(JInternalFrame w) {
        titlePane = new BaseInternalFrameTitlePane(w);
        return titlePane;
    }

    public void setPalette(boolean isPalette) {
        if (isPalette) {
            frame.setBorder(UIManager.getBorder("InternalFrame.paletteBorder"));
        } else {
            frame.setBorder(UIManager.getBorder("InternalFrame.border"));
        }

        if (titlePane instanceof BaseInternalFrameTitlePane) {
            ((BaseInternalFrameTitlePane) titlePane).setPalette(isPalette);
        }
    }

    protected void activateFrame(JInternalFrame f) {
        getDesktopManager().activateFrame(f);
    }

    protected void deactivateFrame(JInternalFrame f) {
        getDesktopManager().deactivateFrame(f);
    }

    private static class MyPropertyChangeHandler implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent e) {
            JInternalFrame jif = (JInternalFrame) e.getSource();
            if (!(jif.getUI() instanceof BaseInternalFrameUI)) {
                return;
            }

            BaseInternalFrameUI ui = (BaseInternalFrameUI) jif.getUI();
            String name = e.getPropertyName();
            if (name.equals("JInternalFrame.frameType")) {
                if (e.getNewValue() instanceof String) {
                    if ("palette".equals(e.getNewValue())) {
                        LookAndFeel.installBorder(ui.frame, "InternalFrame.paletteBorder");
                        ui.setPalette(true);
                    } else {
                        LookAndFeel.installBorder(ui.frame, "InternalFrame.border");
                        ui.setPalette(false);
                    }
                }
            } else if (name.equals("JInternalFrame.isPalette")) {
                if (e.getNewValue() != null) {
                    ui.setPalette(((Boolean) e.getNewValue()).booleanValue());
                } else {
                    ui.setPalette(false);
                }
            } else if (name.equals(JInternalFrame.CONTENT_PANE_PROPERTY)) {
                ui.stripContentBorder();
            }
        }
    } // end class MyPropertyChangeHandler
}

