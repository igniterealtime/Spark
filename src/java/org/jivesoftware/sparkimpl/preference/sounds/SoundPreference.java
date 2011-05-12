/**
 * $RCSfile: ,v $
 * $Revision: $
 * $Date: $
 * 
 * Copyright (C) 2004-2011 Jive Software. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jivesoftware.sparkimpl.preference.sounds;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jivesoftware.Spark;
import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.spark.preference.Preference;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.WindowsFileSystemView;
import org.jivesoftware.spark.util.log.Log;

import com.thoughtworks.xstream.XStream;

/**
 * Preferences to handle Sounds played within Spark.
 *
 * @author Derek DeMoro
 */
public class SoundPreference implements Preference {

    private XStream xstream;
    private SoundPreferences preferences;
    private SoundPanel soundPanel;

    public static String NAMESPACE = "Sounds";

    public SoundPreference() {

    }


    public String getTitle() {
        return Res.getString("title.sound.preferences");
    }

    public Icon getIcon() {
        return SparkRes.getImageIcon(SparkRes.SOUND_PREFERENCES_IMAGE);
    }

    public String getTooltip() {
        return Res.getString("title.sounds");
    }

    public String getListName() {
        return Res.getString("title.sounds");
    }

    public String getNamespace() {
        return NAMESPACE;
    }

    public JComponent getGUI() {
        if (soundPanel == null) {
            try {
		EventQueue.invokeAndWait(new Runnable() {
		public void run()
		{
		    soundPanel = new SoundPanel();
		}
		});
	    } catch (Exception e) {
		e.printStackTrace();
	    }
            
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
                preferences = (SoundPreferences)getXStream().fromXML(reader);
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

                soundPanel.setInvitationSound(preferences.getIncomingInvitationSoundFile());
                soundPanel.setPlayInvitationSound(preferences.playIncomingInvitationSound());
            }
        };
        worker.start();
    }

    public void commit() {
        preferences.setIncomingSound(soundPanel.getIncomingSound());
        preferences.setOutgoingSound(soundPanel.getOutgoingSound());

        preferences.setOfflineSound(soundPanel.getOfflineSound());
        preferences.setPlayOfflineSound(soundPanel.playOfflineSound());

        preferences.setPlayIncomingSound(soundPanel.playIncomingSound());
        preferences.setPlayOutgoingSound(soundPanel.playOutgoingSound());

        preferences.setIncomingInvitationSoundFile(soundPanel.getInvitationSound());
        preferences.setPlayIncomingInvitationSound(soundPanel.playInvitationSound());

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
	private static final long serialVersionUID = 4332294589601051699L;
	private final JCheckBox incomingMessageBox = new JCheckBox();
	private final JTextField incomingMessageSound = new JTextField();
	private final JButton incomingBrowseButton = new JButton("..");

	private final JCheckBox outgoingMessageBox = new JCheckBox();
	private final JTextField outgoingMessageSound = new JTextField();
	private final JButton outgoingBrowseButton = new JButton("..");

	private final JCheckBox userOfflineCheckbox = new JCheckBox();
	private final JTextField userOfflineField = new JTextField();
	private final JButton offlineBrowseButton = new JButton("..");

	private final JCheckBox incomingInvitationBox = new JCheckBox();
	private final JTextField incomingInvitationField = new JTextField();
	private final JButton incomingInvitationBrowseButton = new JButton("..");
        private JFileChooser fc;


        public SoundPanel() {
            setLayout(new GridBagLayout());

            setBorder(BorderFactory.createTitledBorder(Res.getString("title.sound.preferences")));
            // Add ResourceUtils
            ResourceUtils.resButton(incomingMessageBox, Res.getString("checkbox.play.sound.on.new.message"));
            ResourceUtils.resButton(outgoingMessageBox, Res.getString("checkbox.play.sound.on.outgoing.message"));
            ResourceUtils.resButton(userOfflineCheckbox, Res.getString("checkbox.play.sound.when.offline"));
            ResourceUtils.resButton(incomingInvitationBox, Res.getString("checkbox.play.sound.on.invitation"));

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
            add(offlineBrowseButton, new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

            // Handle Invitation Sound
            add(incomingInvitationBox, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
            add(incomingInvitationField, new GridBagConstraints(0, 7, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
            add(incomingInvitationBrowseButton, new GridBagConstraints(1, 7, 1, 1, 0.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

            incomingBrowseButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    pickFile(Res.getString("title.choose.incoming.sound"), incomingMessageSound);
                }
            });


            outgoingBrowseButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    pickFile(Res.getString("title.choose.outgoing.sound"), outgoingMessageSound);
                }
            });

            offlineBrowseButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    pickFile(Res.getString("title.choose.offline.sound"), userOfflineField);
                }
            });

            incomingInvitationBrowseButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    pickFile(Res.getString("title.choose.incoming.sound"), incomingInvitationField);
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

        public void setInvitationSound(String invitationSound) {
            incomingInvitationField.setText(invitationSound);
        }

        public String getInvitationSound() {
            return incomingInvitationField.getText();
        }

        public void setPlayInvitationSound(boolean play) {
            incomingInvitationBox.setSelected(play);
        }

        public boolean playInvitationSound() {
            return incomingInvitationBox.isSelected();
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
                try {
                    field.setText(file.getCanonicalPath());
                }
                catch (IOException e) {
                    Log.error(e);
                }
            }
            else {

            }
        }

    }

    private File getSoundSettingsFile() {
        File file = new File(Spark.getSparkUserHome());
        if (!file.exists()) {
            file.mkdirs();
        }
        return new File(file, "sound-settings.xml");
    }

    private void saveSoundsFile() {
        try {
            FileWriter writer = new FileWriter(getSoundSettingsFile());
            getXStream().toXML(preferences, writer);
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

    private XStream getXStream() {
        if (xstream == null) {
            xstream = new XStream();
            xstream.alias("sounds", SoundPreferences.class);
        }
        return xstream;
    }

}
