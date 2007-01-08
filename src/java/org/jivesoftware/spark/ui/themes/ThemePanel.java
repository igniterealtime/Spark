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
import org.jivesoftware.spark.ui.TranscriptWindow;
import org.jivesoftware.spark.util.ResourceUtils;
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

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.BadLocationException;

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

        /*
        add(messageStyleLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(messageStyleBox, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        add(addThemeButton, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        */

        add(emoticonsLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(emoticonBox, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        add(addEmoticonButton, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        // Activate live one.
        LocalPreferences pref = SettingsManager.getLocalPreferences();
        String theme = pref.getTheme();

        // messageStyleBox.setSelectedItem(theme);

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
                showSelectedEmoticon();
            }
        });

        addEmoticonButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addEmoticon();
            }
        });

        showSelectedEmoticon();
    }

    private void addTheme(File dir) {
        messageStyleBox.addItem(dir.getName());
    }


    protected void showSelectedEmoticon() {
        EmoticonManager emoticonManager = EmoticonManager.getInstance();
        String activeEmoticonName = emoticonManager.getActiveEmoticonSetName();

        transcript.clear();
        transcript.insertTitle(activeEmoticonName + " Emoticons");
        try {
            transcript.insertText("\n");
        }
        catch (BadLocationException e) {
            e.printStackTrace();
        }

        StringBuilder builder = new StringBuilder();
        for (Emoticon emoticon : emoticonManager.getActiveEmoticonSet()) {
            String eq = emoticon.getEquivalants().get(0);
            builder.append(eq);
            builder.append(" ");
        }

        try {
            transcript.insert(builder.toString());
        }
        catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public String getSelectedTheme() {
        return (String)messageStyleBox.getSelectedItem();
    }

    public String getSelectedEmoticonPack() {
        return (String)emoticonBox.getSelectedItem();
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
