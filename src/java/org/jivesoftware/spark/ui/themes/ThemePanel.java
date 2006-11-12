/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.spark.ui.themes;

import org.jdesktop.jdic.browser.WebBrowser;
import org.jivesoftware.spark.util.ResourceUtils;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 */
public class ThemePanel extends JPanel {
    private WebBrowser viewer;

    private JLabel messageStyleLabel;
    private JComboBox messageStyleBox;

    private JLabel emoticonsLabel;
    private JComboBox emoticonBox;

    private JButton addThemeButton;
    private JButton addEmoticonButton;

    public ThemePanel() {
        setLayout(new GridBagLayout());

        messageStyleLabel = new JLabel();
        messageStyleBox = new JComboBox();

        emoticonsLabel = new JLabel();
        emoticonBox = new JComboBox();

        addThemeButton = new JButton();
        addEmoticonButton = new JButton();

        viewer = new WebBrowser();

        // Set ResourceUtils
        ResourceUtils.resLabel(messageStyleLabel, messageStyleBox, "&Message Style:");
        ResourceUtils.resLabel(emoticonsLabel, emoticonBox, "&Emoticons:");

        ResourceUtils.resButton(addThemeButton, "&Add...");
        ResourceUtils.resButton(addEmoticonButton, "A&dd...");

        // Build UI
        buildUI();
    }

    private void buildUI() {
        // Add Viewer
        add(new JScrollPane(viewer), new GridBagConstraints(0, 0, 3, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

        add(messageStyleLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(messageStyleBox, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        add(addThemeButton, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

        add(emoticonsLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(emoticonBox, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        add(addEmoticonButton, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    }

}
