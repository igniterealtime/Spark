/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import javax.swing.border.*;

public class BaseComboBoxUI extends BasicComboBoxUI {

    private PropertyChangeListener propertyChangeListener = null;
    private FocusListener focusListener = null;
    private Border orgBorder = null;
    private Color orgBackgroundColor = null;

    public static ComponentUI createUI(JComponent c) {
        return new BaseComboBoxUI();
    }

    public void installUI(JComponent c) {
        super.installUI(c);
        comboBox.setRequestFocusEnabled(true);
        if (comboBox.getEditor() != null) {
            if (comboBox.getEditor().getEditorComponent() instanceof JTextField) {
                ((JTextField) (comboBox.getEditor().getEditorComponent())).setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
            }
        }
    }

    protected void installListeners() {
        super.installListeners();
        propertyChangeListener = new PropertyChangeHandler();
        comboBox.addPropertyChangeListener(propertyChangeListener);

        if (AbstractLookAndFeel.getTheme().doShowFocusFrame()) {
            focusListener = new FocusListener() {

                public void focusGained(FocusEvent e) {
                    if (comboBox != null) {
                        orgBorder = comboBox.getBorder();
                        orgBackgroundColor = comboBox.getBackground();
                        LookAndFeel laf = UIManager.getLookAndFeel();
                        if (laf instanceof AbstractLookAndFeel) {
                            if (orgBorder instanceof UIResource) {
                                Border focusBorder = ((AbstractLookAndFeel)laf).getBorderFactory().getFocusFrameBorder();
                                comboBox.setBorder(focusBorder);
                            }
                            Color backgroundColor = AbstractLookAndFeel.getTheme().getFocusBackgroundColor();
                            comboBox.setBackground(backgroundColor);
                        }
                    }
                }

                public void focusLost(FocusEvent e) {
                    if (comboBox != null) {
                        comboBox.setBorder(orgBorder);
                        comboBox.setBackground(orgBackgroundColor);
                    }
                }
            };
            comboBox.addFocusListener(focusListener);
        }
    }

    protected void uninstallListeners() {
        comboBox.removePropertyChangeListener(propertyChangeListener);
        comboBox.removeFocusListener(focusListener);
        propertyChangeListener = null;
        focusListener = null;
        super.uninstallListeners();
    }

    public Dimension getPreferredSize(JComponent c) {
        Dimension size = super.getPreferredSize(c);
        return new Dimension(size.width + 2, size.height + 2);
    }

    public JButton createArrowButton() {
        JButton button = new ArrowButton();
        if (JTattooUtilities.isLeftToRight(comboBox)) {
            Border border = BorderFactory.createMatteBorder(0, 1, 0, 0, AbstractLookAndFeel.getFrameColor());
            button.setBorder(border);
        } else {
            Border border = BorderFactory.createMatteBorder(0, 0, 0, 1, AbstractLookAndFeel.getFrameColor());
            button.setBorder(border);
        }
        return button;
    }

    protected void setButtonBorder() {
        if (JTattooUtilities.isLeftToRight(comboBox)) {
            Border border = BorderFactory.createMatteBorder(0, 1, 0, 0, AbstractLookAndFeel.getFrameColor());
            arrowButton.setBorder(border);
        } else {
            Border border = BorderFactory.createMatteBorder(0, 0, 0, 1, AbstractLookAndFeel.getFrameColor());
            arrowButton.setBorder(border);
        }
    }

    public class PropertyChangeHandler implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent e) {
            String name = e.getPropertyName();
            if (name.equals("componentOrientation")) {
                setButtonBorder();
            }
        }
    }
//-----------------------------------------------------------------------------    

    public static class ArrowButton extends NoFocusButton {

        public void paint(Graphics g) {
            Dimension size = getSize();
            if (isEnabled()) {
                if (getModel().isArmed() && getModel().isPressed()) {
                    JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme().getPressedColors(), 0, 0, size.width, size.height);
                } else if (getModel().isRollover()) {
                    JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme().getRolloverColors(), 0, 0, size.width, size.height);
                } else {
                    JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme().getButtonColors(), 0, 0, size.width, size.height);
                }
            } else {
                JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme().getDisabledColors(), 0, 0, size.width, size.height);
            }
            Icon icon = BaseIcons.getComboBoxIcon();
            int x = (size.width - icon.getIconWidth()) / 2;
            int y = (size.height - icon.getIconHeight()) / 2;
            if (getModel().isPressed() && getModel().isArmed()) {
                icon.paintIcon(this, g, x + 2, y + 1);
            } else {
                icon.paintIcon(this, g, x + 1, y);
            }
            paintBorder(g);
        }
    }
}
