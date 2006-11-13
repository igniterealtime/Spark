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

import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.spark.ui.TranscriptWindow;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 */
public class ThemePanel extends JPanel {
    private TranscriptWindow transcript;

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

        transcript = new TranscriptWindow();

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
        add(new JScrollPane(transcript), new GridBagConstraints(0, 0, 3, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

        add(messageStyleLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(messageStyleBox, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        add(addThemeButton, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

        add(emoticonsLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(emoticonBox, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        add(addEmoticonButton, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));


        final ThemeManager manager = ThemeManager.getInstance();
        File themeDir = ThemeManager.THEMES_DIRECTORY;
        File[] dirs = themeDir.listFiles();
        for (int i = 0; i < dirs.length; i++) {
            File file = dirs[i];
            if (file.isDirectory()) {
                addTheme(file);
            }
        }

        messageStyleBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showSelectedTheme();
            }
        });

        // Activate live one.
        LocalPreferences pref = SettingsManager.getLocalPreferences();
        String theme = pref.getTheme();

        messageStyleBox.setSelectedItem(theme);
    }

    private void addTheme(File dir) {
        messageStyleBox.addItem(dir.getName());
    }


    protected void showSelectedTheme() {
        final ThemeManager manager = ThemeManager.getInstance();

        String themeName = (String)messageStyleBox.getSelectedItem();

        File themeDir = new File(ThemeManager.THEMES_DIRECTORY, themeName);
        try {
            manager.setTheme(themeDir);
        }
        catch (Exception e) {
            transcript.insertCustomMessage("", "Unable to view theme.");
        }

        transcript.setURL(manager.getTemplateURL());

        String status = manager.getStatusMessage("Welcome to this theme.", "7 a.m.");
        String message1 = manager.getOutgoingMessage("DrunkMan", "7 a.m.", "Hey, any idea where to drink early in the morning?", SparkRes.getURL(SparkRes.DUMMY_CONTACT_IMAGE));
        String message2 = manager.getIncomingMessage("Mr. Responsible", "7 a.m.", "I would go ahead and ask one of the Clearspace guys.", SparkRes.getURL(SparkRes.DUMMY_CONTACT_IMAGE));

        transcript.setInnerHTML("chatName", "Template Chat");
        transcript.setInnerHTML("timeOpened", "Conversation started at 7 on the noise.");
        transcript.setInnerHTML("incomingIconPath", "<img src=\"" + SparkRes.getURL(SparkRes.DUMMY_CONTACT_IMAGE).toExternalForm() + "\">");

        transcript.insertNotificationMessage("Welcome to this theme.");
        transcript.executeScript(("appendMessage('" + message1 + "')"));
        transcript.executeScript("appendMessage('" + message2 + "')");
    }

    public String getSelectedTheme() {
        return (String)messageStyleBox.getSelectedItem();
    }

}
