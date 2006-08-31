/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.spark.component.tabbedPane;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;

/**
 * Represent one tab in a SparkTabbedPane.
 *
 * @author Derek DeMoro
 */
public class SparkTab extends TabPanel {
    private JLabel iconLabel;
    private JLabel textLabel;

    private String actualText;

    private Color backgroundColor;
    private Color selectedBorderColor;
    private boolean selected;
    private boolean boldWhenActive;

    private Font defaultFont;

    public SparkTab(Icon icon, String text) {
        setLayout(new GridBagLayout());


        selectedBorderColor = new Color(173, 0, 0);

        //  setBackground(backgroundColor);

        this.actualText = text;

        iconLabel = new JLabel(icon);
        iconLabel.setOpaque(false);
        textLabel = new JLabel(text);

        // add Label
        add(iconLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(3, 2, 3, 2), 0, 0));

        // add text label
        add(textLabel, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(3, 2, 3, 10), 0, 0));

        // Set fonts
        defaultFont = new Font("Dialog", Font.PLAIN, 11);
        textLabel.setFont(defaultFont);
    }

    public void addComponent(Component component) {
        // add Component
        add(component, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(3, 0, 3, 2), 0, 0));
    }

    public void addPop(JComponent comp) {
        add(comp, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(3, 2, 3, 2), 0, 0));
    }

    public String getActualText() {
        return actualText;
    }


    public void setSelected(boolean selected) {
        super.setSelected(selected);
        this.selected = selected;

        if (boldWhenActive && selected) {
            textLabel.setFont(textLabel.getFont().deriveFont(Font.BOLD));
        }
        else if (boldWhenActive && !selected) {
            textLabel.setFont(defaultFont);
        }


        invalidate();
        validate();
        repaint();

        textLabel.invalidate();
        textLabel.validate();
        textLabel.repaint();
    }

    public Font getDefaultFont() {
        return defaultFont;
    }

    public JLabel getTitleLabel() {
        return textLabel;
    }

    public void setIcon(Icon icon) {
        iconLabel.setIcon(icon);
    }

    public void setBoldWhenActive(boolean boldWhenActive) {
        this.boldWhenActive = boldWhenActive;
    }


}
