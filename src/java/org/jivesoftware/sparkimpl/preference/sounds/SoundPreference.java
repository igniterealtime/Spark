/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkimpl.preference.sounds;

import com.thoughtworks.xstream.XStream;
import org.jivesoftware.Spark;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.spark.preference.Preference;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.WindowsFileSystemView;
import org.jivesoftware.spark.util.log.Log;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Preferences to handle Sounds played within Spark.
 *
 * @author Derek DeMoro
 */
public class SoundPreference implements Preference {

    private XStream xstream = new XStream();
    private SoundPreferences preferences;
    private SoundPanel soundPanel;

    public static String NAMESPACE = "Sounds";

    public SoundPreference() {
        xstream.alias("sounds", SoundPreferences.class);
    }


    public String getTitle() {
        return "Sound Preferences";
    }

    public Icon getIcon() {
        return SparkRes.getImageIcon(SparkRes.SOUND_PREFERENCES_IMAGE);
    }

    public String getTooltip() {
        return "Sounds";
    }

    public String getListName() {
        return "Sounds";
    }

    public String getNamespace() {
        return NAMESPACE;
    }

    public JComponent getGUI() {
        if (soundPanel == null) {
            soundPanel = new SoundPanel();
        }
        return soundPanel;
    }

    public void loadFromFile() {
        if (preferences != null) {
            return;
        }

        if (!getSoundSettingsFile().exists()) {
            preferences = new SoundPreferences();
        }
        else {

            // Do Initial Load from FileSystem.
            File settingsFile = getSoundSettingsFile();
            try {
                FileReader reader = new FileReader(settingsFile);
                preferences = (SoundPreferences)xstream.fromXML(reader);
            }
            catch (Exception e) {
                Log.error("Error loading Sound Preferences.", e);
                preferences = new SoundPreferences();
            }
        }
    }

    public void load() {
        if (soundPanel == null) {
            soundPanel = new SoundPanel();
        }

        SwingWorker worker = new SwingWorker() {
            public Object construct() {
                loadFromFile();
                return preferences;
            }

            public void finished() {
                // Set default settings
                soundPanel.setIncomingMessageSound(preferences.getIncomingSound());
                soundPanel.playIncomingSound(preferences.isPlayIncomingSound());

                soundPanel.setOutgoingMessageSound(preferences.getOutgoingSound());
                soundPanel.playOutgoingSound(preferences.isPlayOutgoingSound());

                soundPanel.setOfflineSound(preferences.getOfflineSound());
                soundPanel.playOfflineSound(preferences.isPlayOfflineSound());
            }
        };
        worker.start();
    }

    public void commit() {
        preferences.setIncomingSound(soundPanel.getIncomingSound());
        preferences.setOutgoingSound(soundPanel.getOutgoingSound());
        preferences.setOfflineSound(soundPanel.getOfflineSound());

        preferences.setPlayIncomingSound(soundPanel.playIncomingSound());
        preferences.setPlayOutgoingSound(soundPanel.playOutgoingSound());
        preferences.setPlayOfflineSound(soundPanel.playOfflineSound());

        saveSoundsFile();
    }

    public boolean isDataValid() {
        return true;
    }

    public String getErrorMessage() {
        return null;
    }

    public Object getData() {
        return null;
    }


    private class SoundPanel extends JPanel {
        final JCheckBox incomingMessageBox = new JCheckBox();
        final JTextField incomingMessageSound = new JTextField();
        final JButton incomingBrowseButton = new JButton();

        final JCheckBox outgoingMessageBox = new JCheckBox();
        final JTextField outgoingMessageSound = new JTextField();
        final JButton outgoingBrowseButton = new JButton();

        final JCheckBox userOfflineCheckbox = new JCheckBox();
        final JTextField userOfflineField = new JTextField();
        final JButton offlineBrowseButton = new JButton();
        private JFileChooser fc;


        public SoundPanel() {
            setLayout(new GridBagLayout());

            // Add ResourceUtils
            ResourceUtils.resButton(incomingMessageBox, "Play sound when new message &arrives");
            ResourceUtils.resButton(outgoingMessageBox, "Play sound when a message is &sent");
            ResourceUtils.resButton(userOfflineCheckbox, "Play sound when user goes &offline");
            ResourceUtils.resButton(incomingBrowseButton, "&Browse");
            ResourceUtils.resButton(outgoingBrowseButton, "B&rowse");
            ResourceUtils.resButton(offlineBrowseButton, "Br&owse");

            // Handle incoming sounds
            add(incomingMessageBox, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
            add(incomingMessageSound, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
            add(incomingBrowseButton, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

            // Handle sending sounds
            add(outgoingMessageBox, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
            add(outgoingMessageSound, new GridBagConstraints(0, 3, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
            add(outgoingBrowseButton, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

            // Handle User Online Sound
            add(userOfflineCheckbox, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
            add(userOfflineField, new GridBagConstraints(0, 5, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
            add(offlineBrowseButton, new GridBagConstraints(1, 5, 1, 1, 0.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

            incomingBrowseButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    pickFile("Choose Incoming Sound File", incomingMessageSound);
                }
            });


            outgoingBrowseButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    pickFile("Choose Outgoing Sound File", outgoingMessageSound);
                }
            });

            offlineBrowseButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    pickFile("Choose Offline Sound File", userOfflineField);
                }
            });


        }

        public void setIncomingMessageSound(String path) {
            incomingMessageSound.setText(path);
        }

        public void setOutgoingMessageSound(String path) {
            outgoingMessageSound.setText(path);
        }

        public void setOfflineSound(String path) {
            userOfflineField.setText(path);
        }

        public void playIncomingSound(boolean play) {
            incomingMessageBox.setSelected(play);
        }

        public void playOutgoingSound(boolean play) {
            outgoingMessageBox.setSelected(play);
        }

        public void playOfflineSound(boolean play) {
            userOfflineCheckbox.setSelected(play);
        }


        public String getIncomingSound() {
            return incomingMessageSound.getText();
        }

        public boolean playIncomingSound() {
            return incomingMessageBox.isSelected();
        }

        public boolean playOutgoingSound() {
            return outgoingMessageBox.isSelected();
        }

        public String getOutgoingSound() {
            return outgoingMessageSound.getText();
        }

        public boolean playOfflineSound() {
            return userOfflineCheckbox.isSelected();
        }

        public String getOfflineSound() {
            return userOfflineField.getText();
        }

        private void pickFile(String title, JTextField field) {
            if (fc == null) {
                fc = new JFileChooser();
                if (Spark.isWindows()) {
                    fc.setFileSystemView(new WindowsFileSystemView());
                }
            }
            fc.setDialogTitle(title);
            int returnVal = fc.showOpenDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                field.setText(file.getAbsolutePath());
            }
            else {

            }
        }

    }

    private File getSoundSettingsFile() {
        File file = new File(Spark.getUserHome(), "Spark");
        if (!file.exists()) {
            file.mkdirs();
        }
        return new File(file, "sound-settings.xml");
    }

    private void saveSoundsFile() {
        try {
            FileWriter writer = new FileWriter(getSoundSettingsFile());
            xstream.toXML(preferences, writer);
        }
        catch (Exception e) {
            Log.error("Error saving sound settings.", e);
        }
    }

    public SoundPreferences getPreferences() {
        if (preferences == null) {
            load();
        }
        return preferences;
    }

    public void shutdown() {

    }

}
