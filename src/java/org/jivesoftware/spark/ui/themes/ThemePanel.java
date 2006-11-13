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

import org.jivesoftware.Spark;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.spark.ui.TranscriptWindow;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.URLFileSystem;
import org.jivesoftware.spark.util.WindowsFileSystemView;
import org.jivesoftware.sparkimpl.plugin.emoticons.Emoticon;
import org.jivesoftware.sparkimpl.plugin.emoticons.EmoticonManager;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
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

    private JFileChooser fc;
    private ThemeManager themeManager;

    public ThemePanel() {
        setLayout(new GridBagLayout());

        themeManager = ThemeManager.getInstance();

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


        addThemeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addTheme();
            }
        });


        final EmoticonManager emoticonManager = EmoticonManager.getInstance();
        for (String pack : emoticonManager.getEmoticonPacks()) {
            emoticonBox.addItem(pack);
        }

        final String activePack = pref.getEmoticonPack();
        emoticonBox.setSelectedItem(activePack);

        emoticonBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                emoticonManager.addEmoticonPack((String)emoticonBox.getSelectedItem());
                emoticonManager.setActivePack((String)emoticonBox.getSelectedItem());
                showSelectedTheme();
            }
        });

        addEmoticonButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addEmoticon();
            }
        });

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

        StringBuilder builder = new StringBuilder();

        EmoticonManager emoticonManager = EmoticonManager.getInstance();
        for (Emoticon emoticon : emoticonManager.getActiveEmoticonSet()) {
            String eq = emoticon.getEquivalants().get(0);
            builder.append(eq);
            builder.append(" ");
        }


        transcript.insertCustomMessage("Emoticon Man:", builder.toString());
    }

    public String getSelectedTheme() {
        return (String)messageStyleBox.getSelectedItem();
    }

    public String getSelectedEmoticonPack() {
        return (String)emoticonBox.getSelectedItem();
    }

    private void addTheme() {
        if (fc == null) {
            fc = new JFileChooser();
            if (Spark.isWindows()) {
                fc.setFileSystemView(new WindowsFileSystemView());
            }
        }
        fc.setDialogTitle("Add Theme");

        fc.addChoosableFileFilter(new ZipFilter());

        int returnVal = fc.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File theme = fc.getSelectedFile();
            themeManager.installTheme(theme);
            try {
                String name = URLFileSystem.getName(theme.toURL());

                // If the name does not exists, add it to the message box.
                for (int i = 0; i < messageStyleBox.getItemCount(); i++) {
                    String n = (String)messageStyleBox.getItemAt(i);
                    if (name.equals(n)) {
                        return;
                    }
                }

                messageStyleBox.addItem(name);

                // Set Selected
                messageStyleBox.setSelectedItem(name);
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    private void addEmoticon() {
        if (fc == null) {
            fc = new JFileChooser();
            if (Spark.isWindows()) {
                fc.setFileSystemView(new WindowsFileSystemView());
            }
        }
        fc.setDialogTitle("Add Emoticon Pack");

        fc.addChoosableFileFilter(new ZipFilter());

        int returnVal = fc.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File pack = fc.getSelectedFile();
            try {
                EmoticonManager emoticonManager = EmoticonManager.getInstance();
                String name = emoticonManager.installPack(pack);

                // If the name does not exists, add it to the message box.
                for (int i = 0; i < emoticonBox.getItemCount(); i++) {
                    String n = (String)messageStyleBox.getItemAt(i);
                    if (name.equals(n)) {
                        return;
                    }
                }

                emoticonBox.addItem(name);

                // Set Selected
                emoticonBox.setSelectedItem(name);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class ZipFilter extends javax.swing.filechooser.FileFilter {
        public boolean accept(File file) {
            String filename = file.getName();
            if (file.isDirectory()) {
                return true;
            }
            return filename.endsWith(".zip");
        }

        public String getDescription() {
            return "*.zip";
        }
    }

}
