/**
 * Copyright (C) 2004-2011 Jive Software. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jivesoftware.spark.ui.themes;

import org.jivesoftware.Spark;
import org.jivesoftware.resource.Default;
import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.ui.transctipt.TranscriptWindow;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.WindowsFileSystemView;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.emoticons.Emoticon;
import org.jivesoftware.sparkimpl.plugin.emoticons.EmoticonManager;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.io.File;
import java.util.Objects;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.CENTER;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.NORTHWEST;
import static java.awt.GridBagConstraints.WEST;
import static org.jivesoftware.spark.util.GraphicUtils.addItemIfNotExists;
import static org.jivesoftware.spark.util.ResourceUtils.resButton;
import static org.jivesoftware.spark.util.ResourceUtils.resLabel;

/**
 * ThemePanel is used for the setting of TranscriptWindows and Emoticon packs.
 */
public class ThemePanel extends JPanel {
    private final JComboBox<String> messageStyleBox = new JComboBox<>();
    private final JComboBox<String> emoticonBox = new JComboBox<>();
    private final JButton addEmoticonButton = new JButton();

    private final JSpinner contactListFontField = new JSpinner(new SpinnerNumberModel(14, 9, 36, 1));
    private final JLabel contactListFontLabel = new JLabel();

    private final JSpinner chatRoomFontField = new JSpinner(new SpinnerNumberModel(16, 9, 36, 1));
    private final JLabel chatRoomFontLabel = new JLabel();

    private final JSpinner maxCurrentHistorySizeField = new JSpinner(new SpinnerNumberModel(20, 0, 100, 10));
    private final JLabel maxCurrentHistorySizeLabel = new JLabel();

    private final JCheckBox emoticonCheckBox = new JCheckBox();
    private final JButton addThemeButton = new JButton();
    private final JLabel messageStyleLabel = new JLabel();
    private JFileChooser fc;

    private final JCheckBox showAvatarsBox = new JCheckBox();
    private final JCheckBox showVCards = new JCheckBox();
    private final JLabel avatarSizeLabel = new JLabel();
    private final JComboBox<String> avatarSizeField;

    private final JCheckBox disableGrayingIdleContacts = new JCheckBox();
    private final JLabel _lookAndFeelLabel = new JLabel();
    private final JComboBox<String> _lookAndFeel;
    private final JButton _lookAndFeelPreview = new JButton();
    private final JCheckBox _useTabsForTransports = new JCheckBox();
    private final JCheckBox _useTabsForConference = new JCheckBox();
    private final JCheckBox useSingleWindowDocking = new JCheckBox();
    private final JCheckBox hideInTaskbar = new JCheckBox();
    private final JComboBox<String> _showReconnectBox;

    private final JScrollPane emoticonScrollPane;
    private JPanel emoticonsPanel;

    private final LocalPreferences pref = SettingsManager.getLocalPreferences();

    public ThemePanel() {
        setLayout(new GridBagLayout());
        _lookAndFeel = new JComboBox<>(LookAndFeelManager.getLookAndFeelNames());
        if (Default.getBoolean(Default.LOOK_AND_FEEL_DISABLED)) {
            _lookAndFeel.setEnabled(false);
        }
        resLabel(_lookAndFeelLabel, _lookAndFeelPreview, Res.getString("lookandfeel.select"));
        resButton(_lookAndFeelPreview, Res.getString("lookandfeel.change.now"));

        _lookAndFeel.addActionListener(e -> {
            final boolean requiresRestart = LookAndFeelManager.requiresRestart(getSelectedLookAndFeelName());
            _lookAndFeelPreview.setEnabled(!requiresRestart);
            _lookAndFeelPreview.setToolTipText(requiresRestart ? Res.getString("lookandfeel.tooltip.restart.yes") : Res.getString("lookandfeel.tooltip.restart.no"));
            _lookAndFeelPreview.revalidate();
        });

        _lookAndFeelPreview.addActionListener(e -> {
            SwingWorker worker = new SwingWorker() {
                @Override
                public Object construct() {
                    return 42;
                }

                private void setNewLaF() {
                    String selectedName = getSelectedLookAndFeelName();
                    try {
                        final String className = LookAndFeelManager.getClassName(selectedName);
                        UIManager.setLookAndFeel(className);
                    } catch (Exception e) {
                        Log.error("An unexpected exception occurred while trying to update Look and Feel to '" + selectedName + "'.", e);
                    }
                }

                private void updateAllComponentsLaF(final Window window) {
                    for (Window childWindow : window.getOwnedWindows()) {
                        updateAllComponentsLaF(childWindow);
                    }
                    SwingUtilities.updateComponentTreeUI(window);
                }

                @Override
                public void finished() {
                    // substance is a PITA! If the current laf is substance, and the new laf is not, we need to
                    // refresh all components, but since substance is very stubborn, we must restart.
                    final String currentName = UIManager.getLookAndFeel().getName().toLowerCase();
                    final String selectedName = getSelectedLookAndFeelName();
                    final String selectedClass = LookAndFeelManager.getClassName(selectedName);
                    if (currentName.contains("substance") && !selectedName.toLowerCase().contains("substance")) {
                        final int selectedOption = JOptionPane.showConfirmDialog(SparkManager.getPreferenceManager().getPreferenceDialog(),
                                Res.getString("message.restart.required"),
                                Res.getString("title.alert"),
                                JOptionPane.YES_NO_OPTION);
                        if (selectedOption == JOptionPane.YES_OPTION) {
                            setNewLaF();
                            pref.setLookAndFeel(selectedClass);
                            SparkManager.getMainWindow().logout(false);
                        }
                    } else {
                        // otherwise we're ok to just refresh all components
                        setNewLaF();
                        for (Frame frame : Frame.getFrames()) {
                            updateAllComponentsLaF(frame);
                        }
                        JFrame.setDefaultLookAndFeelDecorated(true);
                        JDialog.setDefaultLookAndFeelDecorated(true);
                        pref.setLookAndFeel(selectedClass);
                    }
                }
            };
            worker.start();
        });

        emoticonsPanel = new EmoticonPanel(10);
        emoticonScrollPane = new JScrollPane(emoticonsPanel);
        emoticonScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        emoticonScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        TranscriptWindow emoticonPreviewTranscript = new TranscriptWindow();
        emoticonPreviewTranscript.setForceEmoticons(true);

        String[] sizeChoices = {"16", "24", "32", "48", "96", "120"};
        avatarSizeField = new JComboBox<>(sizeChoices);

        String[] r = {
            Res.getString("checkbox.reconnect.panel.big"),
            Res.getString("checkbox.reconnect.panel.small"),
            Res.getString("checkbox.reconnect.panel.icon")
        };
        _showReconnectBox = new JComboBox<>(r);
        _showReconnectBox.setSelectedIndex(pref.getReconnectPanelType());
        _showReconnectBox.addActionListener(e -> {
            if (_showReconnectBox.getSelectedIndex() != -1) {
                setShowReconnectPanel(_showReconnectBox.getSelectedIndex());
            }
        });

        resButton(disableGrayingIdleContacts, Res.getString("checkbox.graying.out"));

        // Set ResourceUtils
        resLabel(messageStyleLabel, messageStyleBox, Res.getString("label.message.style") + ":");
//        ResourceUtils.resLabel(emoticonsLabel, emoticonBox, Res.getString("label.emoticons") + ":");
        resButton(emoticonCheckBox, Res.getString("checkbox.enable.emoticons"));

        resButton(addThemeButton, Res.getString("button.add"));
        resButton(addEmoticonButton, Res.getString("button.add"));

        resLabel(contactListFontLabel, contactListFontField, Res.getString("label.contactlist.fontsize"));
        resLabel(chatRoomFontLabel, chatRoomFontField, Res.getString("label.chatroom.fontsize"));
        resLabel(maxCurrentHistorySizeLabel, maxCurrentHistorySizeField, Res.getString("label.chatroom.maxcurrenthistorysize"));
        resButton(showAvatarsBox, Res.getString("checkbox.show.avatars.in.contactlist"));
        resLabel(avatarSizeLabel, avatarSizeField, Res.getString("label.contactlist.avatarsize"));
        resButton(showVCards, Res.getString("title.appearance.showVCards"));
        _useTabsForTransports.setText(Res.getString("checkbox.transport.tab.setting"));
        _useTabsForConference.setText(Res.getString("checkbox.conference.tab.setting"));
        useSingleWindowDocking.setText(Res.getString("checkbox.singleWindowDocking"));
        hideInTaskbar.setText(Res.getString("checkbox.hideInTaskbar"));

        // Build UI
        buildUI();
    }

    public String getSelectedLookAndFeelName() {
        return (String) this._lookAndFeel.getSelectedItem();
    }

    public String getSelectedLookAndFeelClassName() {
        return LookAndFeelManager.getClassName(getSelectedLookAndFeelName());
    }

    /**
     * Builds the UI.
     */
    private void buildUI() {
        // Add Viewer
        Insets insets = new Insets(5, 5, 5, 5);
//        add(new JScrollPane(transcript), new GridBagConstraints(0, 0, 3, 1, 0, 0, CENTER, BOTH, insets, 0, 0));
        add(emoticonScrollPane, new GridBagConstraints(0, 1, 3, 1, 1, 1, CENTER, BOTH, insets, 0, 0));

        add(emoticonBox, new GridBagConstraints(1, 2, 1, 1, 1, 0, WEST, HORIZONTAL, insets, 0, 0));
        add(addEmoticonButton, new GridBagConstraints(2, 2, 1, 1, 0, 0, WEST, NONE, insets, 0, 0));
        add(emoticonCheckBox, new GridBagConstraints(0, 2, 3, 1, 0, 0, WEST, NONE, insets, 0, 0));

        add(_lookAndFeelLabel, new GridBagConstraints(0, 4, 3, 1, 1, 0, WEST, NONE, insets, 50, 0));
        add(_lookAndFeel, new GridBagConstraints(1, 4, 1, 1, 1, 0, WEST, HORIZONTAL, insets, 50, 0));
        add(_lookAndFeelPreview, new GridBagConstraints(2, 4, 1, 1, 0, 0, WEST, NONE, insets, 0, 0));
        add(chatRoomFontLabel, new GridBagConstraints(0, 5, 1, 1, 0, 0, WEST, NONE, insets, 0, 0));
        add(chatRoomFontField, new GridBagConstraints(1, 5, 2, 1, 1, 0, WEST, NONE, insets, 50, 0));
        add(contactListFontLabel, new GridBagConstraints(0, 6, 1, 1, 0, 0, WEST, NONE, insets, 0, 0));
        add(contactListFontField, new GridBagConstraints(1, 6, 2, 1, 1, 0, WEST, NONE, insets, 50, 0));
        add(maxCurrentHistorySizeLabel, new GridBagConstraints(0, 7, 1, 1, 0, 0, WEST, NONE, insets, 0, 0));
        add(maxCurrentHistorySizeField, new GridBagConstraints(1, 7, 2, 1, 1, 0, WEST, NONE, insets, 50, 0));
        add(showAvatarsBox, new GridBagConstraints(0, 8, 2, 1, 1, 0, WEST, NONE, insets, 0, 0));
        add(avatarSizeLabel, new GridBagConstraints(0, 9, 1, 1, 0, 0, WEST, NONE, insets, 0, 0));
        add(avatarSizeField, new GridBagConstraints(1, 9, 2, 1, 1, 0, WEST, NONE, insets, 50, 0));
        add(showVCards, new GridBagConstraints(0, 10, 2, 1, 1, 0, WEST, NONE, insets, 50, 0));
        add(disableGrayingIdleContacts, new GridBagConstraints(0, 11, 2, 1, 1, 0, WEST, NONE, insets, 50, 0));
        add(_useTabsForTransports, new GridBagConstraints(0, 12, 3, 1, 1, 0, WEST, NONE, insets, 50, 0));
        add(_useTabsForConference, new GridBagConstraints(0, 13, 3, 1, 1, 0, WEST, NONE, insets, 50, 0));
        add(useSingleWindowDocking, new GridBagConstraints(0, 14, 3, 1, 1, 0, WEST, NONE, insets, 50, 0));
        add(hideInTaskbar, new GridBagConstraints(0, 15, 3, 1, 1, 0, WEST, NONE, insets, 50, 0));

        JLabel reconnectionLabel = new JLabel(Res.getString("checkbox.reconnect.info"));
        add(reconnectionLabel, new GridBagConstraints(0, 16, 1, 1, 0, 0, NORTHWEST, HORIZONTAL, insets, 50, 0));
        add(_showReconnectBox, new GridBagConstraints(1, 16, 3, 1, 1, 0, WEST, NONE, insets, 50, 0));

        // Activate live one.
        _useTabsForTransports.setSelected(pref.isShowTransportTab());
        _useTabsForConference.setSelected(pref.isShowConferenceTab());
        useSingleWindowDocking.setSelected(pref.isDockingEnabled());
        hideInTaskbar.setSelected(pref.isHideInTaskbar());

        final EmoticonManager emoticonManager = EmoticonManager.getInstance();
        if (emoticonManager.getEmoticonPacks() != null) {
            for (String pack : emoticonManager.getEmoticonPacks()) {
                emoticonBox.addItem(pack);
            }
        }

        final String activePack = pref.getEmoticonPack();
        emoticonBox.setSelectedItem(activePack);
        emoticonBox.addActionListener(e -> {
            String packName = getSelectedEmoticonPack();
            emoticonManager.addEmoticonPack(packName);
            emoticonManager.setActivePack(packName);
            showSelectedEmoticon();
        });

        addEmoticonButton.addActionListener(e -> installEmoticonPack());

        showSelectedEmoticon();
        setEmoticonsEnabled(pref.areEmoticonsEnabled());

        final String className = pref.getLookAndFeel();
        final String name = LookAndFeelManager.getName(className);
        _lookAndFeel.setSelectedItem(name);

        showVCards.setSelected(pref.areVCardsVisible());
        showAvatarsBox.setSelected(pref.areAvatarsVisible());
        disableGrayingIdleContacts.setSelected(pref.isGrayingOutEnabled());

        String contactListIconSizeItem = String.valueOf(pref.getContactListIconSize());
        avatarSizeField.setSelectedItem(contactListIconSizeItem);
        // if there wasn't such size then add and select it
        if (!Objects.equals(avatarSizeField.getSelectedItem(), contactListIconSizeItem)) {
            addItemIfNotExists(avatarSizeField, contactListIconSizeItem);
            avatarSizeField.setSelectedItem(contactListIconSizeItem);
        }

        chatRoomFontField.setValue(pref.getChatRoomFontSize());
        contactListFontField.setValue(pref.getContactListFontSize());
        maxCurrentHistorySizeField.setValue(pref.getMaxCurrentHistorySize());
    }

    /**
     * Displays the active emoticon pack.
     */
    protected void showSelectedEmoticon() {
        EmoticonManager emoticonManager = EmoticonManager.getInstance();
        int i = emoticonManager.getActiveEmoticonSet().size();
        if (i == 0) {
            emoticonsPanel = new EmoticonPanel(1);
            JLabel label = new JLabel(SparkRes.getImageIcon(SparkRes.Icon.SMALL_DELETE));
            emoticonsPanel.add(label);
        } else if (i < 25) {
            emoticonsPanel = new EmoticonPanel(i);
        } else {
            emoticonsPanel = new EmoticonPanel(10);
        }
        for (Emoticon emoticon : emoticonManager.getActiveEmoticonSet()) {
            ImageIcon ico = new ImageIcon(emoticonManager.getEmoticonURL(emoticon));
            JLabel label = new JLabel(ico);
            emoticonsPanel.add(label);
        }

        int rows = Math.min(((EmoticonPanel) emoticonsPanel).getNumRows() * 45, 300);
        emoticonScrollPane.setPreferredSize(new Dimension(300, rows));
        emoticonScrollPane.setViewportView(emoticonsPanel);
        this.revalidate();
    }

    /**
     * Returns the name of the theme selected.
     */
    public String getSelectedTheme() {
        return messageStyleBox.getItemAt(messageStyleBox.getSelectedIndex());
    }

    /**
     * Returns the name of the selected emoticon pack.
     */
    public String getSelectedEmoticonPack() {
        return (String) emoticonBox.getSelectedItem();
    }

    public void setEmoticonsEnabled(boolean enabled) {
        emoticonCheckBox.setSelected(enabled);
    }

    public boolean areEmoticonsEnabled() {
        return emoticonCheckBox.isSelected();
    }

    public boolean isGrayingOutEnabled() {
        return disableGrayingIdleContacts.isSelected();
    }

    /**
     * Adds a new Emoticon pack to Spark.
     */
    private void installEmoticonPack() {
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
                    JOptionPane.showMessageDialog(this, Res.getString("emoticons.notValidPack"), Res.getString("title.error"), JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // If the name does not exist, add it to the message box.
                addItemIfNotExists(emoticonBox, name);
                // Set Selected
                emoticonBox.setSelectedItem(name);
            } catch (Exception e) {
                Log.error(e);
            }
        }
    }

    /**
     * The ZipFilter class is used by the emoticon file picker to filter out all
     * other files besides *.zip files.
     */
    private static class ZipFilter extends javax.swing.filechooser.FileFilter {
        @Override
        public boolean accept(File file) {
            String filename = file.getName();
            if (file.isDirectory()) {
                return true;
            }
            return filename.endsWith(".zip");
        }

        @Override
        public String getDescription() {
            return "*.zip";
        }
    }

    public int getChatRoomFontSize() {
        return (int) chatRoomFontField.getValue();
    }

    public int getContactListFontSize() {
        return (int) contactListFontField.getValue();
    }

    public int getMaxCurrentHistorySize() {
        return (int) maxCurrentHistorySizeField.getValue();
    }

    public int getContactListIconSize() {
        String selectedSize = (String) avatarSizeField.getSelectedItem();
        if (selectedSize == null) {
            return 32;
        }
        return Integer.parseInt(selectedSize);
    }

    public boolean areAvatarsVisible() {
        return showAvatarsBox.isSelected();
    }

    public boolean areVCardsVisible() {
        return showVCards.isSelected();
    }

    public boolean isShowTransportTab() {
        return _useTabsForTransports.isSelected();
    }

    public boolean isShowConferenceTab() {
        return _useTabsForConference.isSelected();
    }

    public boolean isDockingEnabled() {
        return useSingleWindowDocking.isSelected();
    }

    public boolean getHideInTaskbar() {
        return hideInTaskbar.isSelected();
    }

    /**
     * Return 0,1,2
     */
    public int getReconnectPanelType() {
        return _showReconnectBox.getSelectedIndex();
    }

    public void setShowReconnectPanel(int reconnect) {
        _showReconnectBox.setSelectedIndex(reconnect);
    }
}
