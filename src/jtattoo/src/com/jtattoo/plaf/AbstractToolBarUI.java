/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf;

import java.beans.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.*;

public abstract class AbstractToolBarUI extends BasicToolBarUI {

    private final static String IS_ROLLOVER = "JToolBar.isRollover";
    private final static Insets BUTTON_MARGIN = new Insets(1, 1, 1, 1);
    private final static Border INNER_BORDER = BorderFactory.createEmptyBorder(2, 2, 2, 2);
    private boolean isRolloverEnabled = true;
    private MyPropertyChangeListener propertyChangeListener = null;
    private MyContainerListener containerListener = null;
    private Hashtable orgBorders = new Hashtable();
    private Hashtable orgMargins = new Hashtable();

    public abstract Border getRolloverBorder();

    public abstract Border getNonRolloverBorder();

    public abstract boolean isButtonOpaque();

    public void installUI(JComponent c) {
        super.installUI(c);
        Boolean isRollover = (Boolean) UIManager.get(IS_ROLLOVER);
        if (isRollover != null) {
            isRolloverEnabled = isRollover.booleanValue();
        }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                changeBorders();
            }
        });
    }

    public void uninstallUI(JComponent c) {
        restoreBorders();
        super.uninstallUI(c);
    }

    protected void installListeners() {
        super.installListeners();
        propertyChangeListener = new MyPropertyChangeListener();
        if (propertyChangeListener != null) {
            toolBar.addPropertyChangeListener(propertyChangeListener);
        }
        containerListener = new MyContainerListener();
        if (containerListener != null) {
            toolBar.addContainerListener(containerListener);
        }
    }

    protected void uninstallListeners() {
        if (propertyChangeListener != null) {
            toolBar.removePropertyChangeListener(propertyChangeListener);
        }
        propertyChangeListener = null;
        if (containerListener != null) {
            toolBar.removeContainerListener(containerListener);
        }
        containerListener = null;
        super.uninstallListeners();
    }

    protected void setBorderToNormal(Component c) {
    }

    protected void setBorderToRollover(Component c) {
    }

    protected void setBorderToNonRollover(Component c) {
    }

    protected void changeBorders() {
        Component[] components = toolBar.getComponents();
        for (int i = 0; i < components.length; ++i) {
            Component comp = components[i];
            if (comp instanceof AbstractButton) {
                changeButtonBorder((AbstractButton) comp);
            }
        }
    }

    protected void restoreBorders() {
        Component[] components = toolBar.getComponents();
        for (int i = 0; i < components.length; ++i) {
            Component comp = components[i];
            if (comp instanceof AbstractButton) {
                restoreButtonBorder((AbstractButton) comp);
            }
        }
    }

    protected void changeButtonBorder(AbstractButton b) {
        Object cp = b.getClientProperty("paintToolBarBorder");
        if ((cp != null) && (cp instanceof Boolean)) {
            Boolean changeBorder = (Boolean)cp;
            if (!changeBorder.booleanValue()) {
                return;
            }
        }
        if (!orgBorders.contains(b)) {
            if (b.getBorder() != null) {
                orgBorders.put(b, b.getBorder());
            } else {
                orgBorders.put(b, new NullBorder());
            }
        }

        if (!orgMargins.contains(b)) {
            orgMargins.put(b, b.getMargin());
        }

        if (b.getBorder() != null) {
            if (isRolloverEnabled) {
                b.setBorderPainted(true);
                b.setBorder(BorderFactory.createCompoundBorder(getRolloverBorder(), INNER_BORDER));
                b.setMargin(BUTTON_MARGIN);
                b.setRolloverEnabled(true);
                b.setOpaque(isButtonOpaque());
                b.setContentAreaFilled(isButtonOpaque());
            } else {
                b.setBorder(BorderFactory.createCompoundBorder(getNonRolloverBorder(), INNER_BORDER));
                b.setMargin(BUTTON_MARGIN);
                b.setRolloverEnabled(false);
                b.setOpaque(isButtonOpaque());
                b.setContentAreaFilled(isButtonOpaque());
            }
        }
    }

    protected void restoreButtonBorder(AbstractButton b) {
        Object cp = b.getClientProperty("paintToolBarBorder");
        if ((cp != null) && (cp instanceof Boolean)) {
            Boolean changeBorder = (Boolean)cp;
            if (!changeBorder.booleanValue()) {
                return;
            }
        }
        Border border = (Border) orgBorders.get(b);
        if (border != null) {
            if (border instanceof NullBorder) {
                b.setBorder(null);
            } else {
                b.setBorder(border);
            }
        }
        b.setMargin((Insets) orgMargins.get(b));
    }

    protected void updateToolbarBorder() {
        toolBar.revalidate();
        toolBar.repaint();
    }

    protected boolean isToolBarUnderMenubar() {
        if (toolBar != null && toolBar.getOrientation() == JToolBar.HORIZONTAL) {
            JRootPane rp = SwingUtilities.getRootPane(toolBar);
            JMenuBar mb = rp.getJMenuBar();
            if (mb != null) {
                Point mbPoint = new Point(0, 0);
                mbPoint = SwingUtilities.convertPoint(mb, mbPoint, rp);
                Point tbPoint = new Point(0, 0);
                tbPoint = SwingUtilities.convertPoint(toolBar, tbPoint, rp);
                tbPoint.y -= mb.getHeight() - 1;
                Rectangle rect = new Rectangle(mbPoint, mb.getSize());
                return rect.contains(tbPoint);
            }
        }
        return false;
    }

    protected class MyPropertyChangeListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent e) {
            if (e.getPropertyName().equals(IS_ROLLOVER)) {
                if (e.getNewValue() != null) {
                    isRolloverEnabled = ((Boolean) e.getNewValue()).booleanValue();
                    changeBorders();
                }
            } else if ("componentOrientation".equals(e.getPropertyName())) {
                updateToolbarBorder();
            }
        }
    }

    protected class MyContainerListener implements ContainerListener {

        public void componentAdded(ContainerEvent e) {
            Component c = e.getChild();
            if (c instanceof AbstractButton) {
                changeButtonBorder((AbstractButton) c);
            }
        }

        public void componentRemoved(ContainerEvent e) {
            Component c = e.getChild();
            if (c instanceof AbstractButton) {
                restoreButtonBorder((AbstractButton) c);
            }
        }
    }

    private static class NullBorder implements Border, UIResource {

        private static final Insets insets = new Insets(0, 0, 0, 0);

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
        }

        public Insets getBorderInsets(Component c) {
            return insets;
        }

        public boolean isBorderOpaque() {
            return true;
        }
    } // class NullBorder
}
