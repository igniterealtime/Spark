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
import org.jivesoftware.resource.Res;
import org.jivesoftware.spark.ui.TranscriptWindow;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.WindowsFileSystemView;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.emoticons.Emoticon;
import org.jivesoftware.sparkimpl.plugin.emoticons.EmoticonManager;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * ThemePanel is used for the setting of TranscriptWindows and Emoticon packs.
 */
public class ThemePanel extends JPanel {
	private static final long serialVersionUID = 2943854311454590459L;

	private TranscriptWindow transcript;

    private JComboBox messageStyleBox;

    private JLabel emoticonsLabel;
    private JComboBox emoticonBox;

    private JButton addEmoticonButton;

    private JTextField contactListFontField;
    private JLabel contactListFontLabel;

    private JTextField chatRoomFontField;
    private JLabel chatRoomFontLabel;

    private JCheckBox emoticonCheckBox;
    private JFileChooser fc;

    private JCheckBox systemLookAndFeelBox;

    private JCheckBox showAvatarsBox;
    private JCheckBox showVCards;
    private JLabel avatarSizeLabel;
    private JComboBox avatarSizeField;

    /**
     * Construct UI
     */
    public ThemePanel() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createTitledBorder(Res.getString("title.appearance.preferences")));
        
        JLabel messageStyleLabel = new JLabel();
        messageStyleBox = new JComboBox();

        emoticonsLabel = new JLabel();
        emoticonBox = new JComboBox();

        emoticonCheckBox = new JCheckBox();

        JButton addThemeButton = new JButton();
        addEmoticonButton = new JButton();

        transcript = new TranscriptWindow();
        transcript.setForceEmoticons(true);

        systemLookAndFeelBox = new JCheckBox();

        showAvatarsBox = new JCheckBox();
        avatarSizeLabel = new JLabel();
        String[] sizeChoices = {"16x16", "24x24", "32x32"};
        avatarSizeField = new JComboBox(sizeChoices);

        contactListFontField = new JTextField();
        contactListFontLabel = new JLabel();

        chatRoomFontField = new JTextField();
        chatRoomFontLabel = new JLabel();
        
        showVCards = new JCheckBox(Res.getString("title.appearance.showVCards"));

        // Set ResourceUtils
        ResourceUtils.resLabel(messageStyleLabel, messageStyleBox, Res.getString("label.message.style") + ":");
        ResourceUtils.resLabel(emoticonsLabel, emoticonBox, Res.getString("label.emoticons") + ":");
        ResourceUtils.resButton(emoticonCheckBox, Res.getString("checkbox.enable.emoticons"));
        ResourceUtils.resButton(systemLookAndFeelBox, Res.getString("checkbox.use.system.look.and.feel"));

        ResourceUtils.resButton(addThemeButton, Res.getString("button.add"));
        ResourceUtils.resButton(addEmoticonButton, Res.getString("button.add2"));

        ResourceUtils.resLabel(contactListFontLabel, contactListFontField, Res.getString("label.contactlist.fontsize"));
        ResourceUtils.resLabel(chatRoomFontLabel, chatRoomFontField, Res.getString("label.chatroom.fontsize"));
        ResourceUtils.resButton(showAvatarsBox, Res.getString("checkbox.show.avatars.in.contactlist"));
        ResourceUtils.resLabel(avatarSizeLabel, avatarSizeField, Res.getString("label.contactlist.avatarsize"));

        // Build UI
        buildUI();
    }

    /**
     * Builds the UI.
     */
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
        add(emoticonCheckBox, new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

        if (Spark.isWindows()) {
            add(systemLookAndFeelBox, new GridBagConstraints(0, 4, 3, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        }

        add(chatRoomFontLabel, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(chatRoomFontField, new GridBagConstraints(1, 5, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 50, 0));
        add(contactListFontLabel, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(contactListFontField, new GridBagConstraints(1, 6, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 50, 0));
        add(showAvatarsBox, new GridBagConstraints(0, 7, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(avatarSizeLabel, new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(avatarSizeField, new GridBagConstraints(1, 8, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 50, 0));
        add(showVCards, new GridBagConstraints(0, 9, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 50, 0));


        // Activate live one.
        LocalPreferences pref = SettingsManager.getLocalPreferences();

        // messageStyleBox.setSelectedItem(theme);

        final EmoticonManager emoticonManager = EmoticonManager.getInstance();
        if (emoticonManager.getEmoticonPacks() != null)
        {
	        for (String pack : emoticonManager.getEmoticonPacks()) {
	            emoticonBox.addItem(pack);
	        }
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
                addEmoticonPack();
            }
        });

        showSelectedEmoticon();

        emoticonCheckBox.setSelected(pref.areEmoticonsEnabled());

        systemLookAndFeelBox.setSelected(pref.useSystemLookAndFeel());

        showVCards.setSelected(pref.areVCardsVisible());
        
        showAvatarsBox.setSelected(pref.areAvatarsVisible());
       

        if (pref.getContactListIconSize() == 16) {
            avatarSizeField.setSelectedIndex(0);
        }
        else if (pref.getContactListIconSize() == 24) {
            avatarSizeField.setSelectedIndex(1);
        }
        else if (pref.getContactListIconSize() == 32) {
            avatarSizeField.setSelectedIndex(2);
        }
        else {
            avatarSizeField.setSelectedIndex(1);
        }

        try {
            int chatRoomFontSize = pref.getChatRoomFontSize();
            int contactListFontSize = pref.getContactListFontSize();

            chatRoomFontField.setText(Integer.toString(chatRoomFontSize));
            contactListFontField.setText(Integer.toString(contactListFontSize));
        }
        catch (Exception e) {
            Log.error(e);
        }
    }
    
    /**
     * Displays the active emoticon pack.
     */
    protected void showSelectedEmoticon() {
        EmoticonManager emoticonManager = EmoticonManager.getInstance();
        String activeEmoticonName = emoticonManager.getActiveEmoticonSetName();

        transcript.clear();
        transcript.insertCustomText(activeEmoticonName + " Emoticons", true, true, Color.GRAY);
        try {
            transcript.insertText("\n");
        }
        catch (BadLocationException e) {
            Log.error(e);
        }

        StringBuilder builder = new StringBuilder();
        if (emoticonManager.getActiveEmoticonSet() != null)
        {
	        for (Emoticon emoticon : emoticonManager.getActiveEmoticonSet()) {
	            String eq = emoticon.getEquivalants().get(0);
	            builder.append(eq);
	            builder.append(" ");
	        }
        }
        
        try {
            transcript.insert(builder.toString());
        }
        catch (BadLocationException e) {
            Log.error(e);
        }
    }

    /**
     * Returns the name of the theme selected.
     *
     * @return the name of the selected theme.
     */
    public String getSelectedTheme() {
        return (String)messageStyleBox.getSelectedItem();
    }

    /**
     * Returns the name of the selected emoticon pack.
     *
     * @return the name of the emoticon pack.
     */
    public String getSelectedEmoticonPack() {
        return (String)emoticonBox.getSelectedItem();
    }

    public void setEmoticonsEnabled(boolean enabled) {
        emoticonCheckBox.setSelected(enabled);
    }

    public boolean areEmoticonsEnabled() {
        return emoticonCheckBox.isSelected();
    }

    public boolean useSystemLookAndFeel() {
        return systemLookAndFeelBox.isSelected();
    }

    /**
     * Adds a new Emoticon pack to Spark.
     */
    private void addEmoticonPack() {
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

                if (name == null) {
                    JOptionPane.showMessageDialog(this, "Not a valid emoticon pack.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // If the name does not exists, add it to the message box.
                for (int i = 0; i < emoticonBox.getItemCount(); i++) {
                    String n = (String)emoticonBox.getItemAt(i);
                    if (name.equals(n)) {
                        return;
                    }
                }

                emoticonBox.addItem(name);

                // Set Selected
                emoticonBox.setSelectedItem(name);
            }
            catch (Exception e) {
                Log.error(e);
            }
        }
    }

    /**
     * The ZipFilter class is used by the emoticon file picker to filter out all
     * other files besides *.zip files.
     */
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

    public String getChatRoomFontSize(){
        return chatRoomFontField.getText();
    }

    public String getContactListFontSize(){
        return contactListFontField.getText();
    }

    public int getContactListIconSize(){
        if (avatarSizeField.getSelectedIndex() == 0) {
            return 16;
        }
        else if (avatarSizeField.getSelectedIndex() == 1) {
            return 24;
        }
        else if (avatarSizeField.getSelectedIndex() == 2) {
            return 32;
        }
        else {
            return 24;
        }
    }

    public boolean areAvatarsVisible(){
        return showAvatarsBox.isSelected();
    }
    
    public boolean areVCardsVisible(){
       return showVCards.isSelected();
   }
}
