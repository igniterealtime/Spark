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

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/**
 * Represent the TabUI within the <code>SparkTabbedPane</code>.
 *
 * @author Derek DeMoro
 */
public class SparkTab extends TabPanel {
    private JLabel iconLabel;
    private JLabel textLabel;

    private String actualText;

    private boolean boldWhenActive;

    private Font defaultFont;

    private Icon previousIcon;
    private Icon defaultIcon;

    /**
     * Creates a SparkTab.
     *
     * @param icon the icon to use.
     * @param text the text to display.
     */
    public SparkTab(Icon icon, String text) {
        setLayout(new GridBagLayout());

        this.defaultIcon = icon;

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

    /**
     * Adds a new component on the right side of the SparkTab.
     *
     * @param component the component to add.
     */
    public void addComponent(Component component) {
        // add Component
        add(component, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(3, 0, 3, 2), 0, 0));
    }

    public void addPop(JComponent comp) {
        add(comp, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(3, 2, 3, 2), 0, 0));
    }

    /**
     * Returns the actual text of the tab.
     *
     * @return the actual text.
     */
    public String getActualText() {
        return actualText;
    }

    /**
     * Change the color of the tab text.
     *
     * @param color the new color.
     */
    public void setTitleColor(Color color) {
        textLabel.setForeground(color);
        textLabel.validate();
        textLabel.repaint();
    }

    /**
     * Changes the tab to display as bold or plain.
     *
     * @param bold true if the tab should be displayed as bold, otherwise false.
     */
    public void setTabBold(boolean bold) {
        Font oldFont = textLabel.getFont();
        Font newFont;
        if (bold) {
            newFont = new Font(oldFont.getFontName(), Font.BOLD, oldFont.getSize());
        }
        else {
            newFont = new Font(oldFont.getFontName(), Font.PLAIN, oldFont.getSize());
        }

        textLabel.setFont(newFont);
        textLabel.validate();
        textLabel.repaint();
    }

    /**
     * Sets the font of the title text.
     *
     * @param font the font to use for the title text.
     */
    public void setTabFont(Font font) {
        textLabel.setFont(font);
    }

    /**
     * Validates the tab UI.
     */
    public void validateTab() {
        textLabel.validate();
        textLabel.repaint();
    }


    public void setSelected(boolean selected) {
        super.setSelected(selected);

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

    /**
     * Returns the initial font for this tab.
     *
     * @return the initial font used for this tab.
     */
    public Font getDefaultFont() {
        return defaultFont;
    }

    /**
     * Returns the label used to display text on the tab.
     *
     * @return the label.
     */
    public JLabel getTitleLabel() {
        return textLabel;
    }

    /**
     * Sets the text of the tab.
     *
     * @param title the title of the tab.
     */
    public void setTabTitle(String title) {
        textLabel.setText(title);
    }

    /**
     * Returns the text of the tab.
     *
     * @return the text of the tab.
     */
    public String getTabTitle() {
        return textLabel.getText();
    }

    /**
     * Sets the icon to be displayed on this tab.
     *
     * @param icon the icon to display.
     */
    public void setIcon(Icon icon) {
        if (iconLabel.getIcon() != null) {
            previousIcon = iconLabel.getIcon();
        }
        iconLabel.setIcon(icon);
    }

    /**
     * Specify if this tab should be bold when selected within the <code>SparkTabbedPane</code>
     *
     * @param boldWhenActive true to be bold when tab is active, otherwise false.
     */
    public void setBoldWhenActive(boolean boldWhenActive) {
        this.boldWhenActive = boldWhenActive;
    }

    /**
     * Returns if this tab should be bold when active.
     *
     * @return true if the tab should be active.
     */
    public boolean isBoldWhenActive() {
        return boldWhenActive;
    }

    /**
     * Returns the icon used before the latest icon.
     *
     * @return the previous icon used.
     */
    public Icon getPreviousIcon() {
        return previousIcon;
    }

    /**
     * Returns the default icon. The default icon is the icon used when the tab was initially created.
     *
     * @return the default icon.
     */
    public Icon getDefaultIcon() {
        return defaultIcon;
    }
}
