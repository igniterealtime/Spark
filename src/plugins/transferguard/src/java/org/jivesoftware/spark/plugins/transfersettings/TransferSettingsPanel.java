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
package org.jivesoftware.spark.plugins.transfersettings;

import org.jivesoftware.spark.component.VerticalFlowLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;

import java.util.List;

/**
 * UI for the file transfer preferences. It displays all the various preference settings for editing.
 */
public class TransferSettingsPanel extends JPanel {
    private static final long serialVersionUID = -2481011951921919518L;
    private BlockedTypesPanel pnlTypes = new BlockedTypesPanel();
    private BlockedPeoplePanel pnlPeople = new BlockedPeoplePanel();
    private FileSizePanel pnlSize = new FileSizePanel();
    private CannedResponsePanel pnlResponse = new CannedResponsePanel();

    public TransferSettingsPanel() {
        setLayout(new VerticalFlowLayout());
        add(pnlTypes);
        add(pnlPeople);
        add(pnlSize);
        add(pnlResponse);
    }

    /**
     * Populates all the gui controls with values from the supplied {@link FileTransferSettings}.
     * @param settings  the {@link FileTransferSettings} to populate the gui from.
     */
    public void applySettings(FileTransferSettings settings) {
        pnlTypes.setBlockedTypes(settings.getBlockedExtensions());
        pnlPeople.setBlockedPeople(settings.getBlockedJIDs());
        pnlSize.setMaxFileSize(settings.getMaxFileSize());
        pnlSize.setCheckFileSize(settings.getCheckFileSize());
        pnlResponse.setCannedResponse(settings.getCannedRejectionMessage());
    }

    /**
     * Populates the supplied {@link FileTransferSettings} from the values in the gui controls.
     * @param settings the {@link FileTransferSettings} to populate.
     */
    public void storeSettings(FileTransferSettings settings) {
        settings.setBlockedExtensions(pnlTypes.getBlockedTypes());
        settings.setBlockedJIDS(pnlPeople.getBlockedPeople());
        settings.setMaxFileSize(pnlSize.getMaxFileSize());
        settings.setCheckFileSize(pnlSize.getCheckFileSize());
        settings.setCannedRejectionMessage(pnlResponse.getCannedResponse());
    }

    private class BlockedTypesPanel extends JPanel {
	private static final long serialVersionUID = 6152402556852606706L;
	private JTextArea txtBlockedTypes = new JTextArea(2, 0);

        BlockedTypesPanel() {
            txtBlockedTypes.setBorder(UIManager.getLookAndFeelDefaults().getBorder("TextField.border"));
            
            txtBlockedTypes.setToolTipText(TGuardRes.getString("guard.settings.tooltips.blockedtypes"));
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createCompoundBorder(new TitledBorder(TGuardRes.getString("guard.settings.title.extensions")),
                        new EmptyBorder(2,4,4,4)));
            add(txtBlockedTypes, BorderLayout.CENTER);
        }

        public void setBlockedTypes(List<String> types) {
            txtBlockedTypes.setText(FileTransferSettings.convertSettingsListToString(types));
        }

        public List<String> getBlockedTypes() {
            return FileTransferSettings.convertSettingsStringToList(txtBlockedTypes.getText());
        }
    }

    private class BlockedPeoplePanel extends JPanel {
	private static final long serialVersionUID = -1069560705582838620L;
	private JTextArea txtBlockedPeople = new JTextArea(2, 0);

        BlockedPeoplePanel() {
            txtBlockedPeople.setBorder(UIManager.getLookAndFeelDefaults().getBorder("TextField.border"));
            txtBlockedPeople.setToolTipText(TGuardRes.getString("guard.settings.tooltips.blockedperson"));
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createCompoundBorder(new TitledBorder(TGuardRes.getString("guard.settings.title.person")),
                        new EmptyBorder(2,4,4,4)));
            add(txtBlockedPeople, BorderLayout.CENTER);
        }

        public void setBlockedPeople(List<String> people) {
            txtBlockedPeople.setText(FileTransferSettings.convertSettingsListToString(people));
        }

        public List<String> getBlockedPeople() {
            return FileTransferSettings.convertSettingsStringToList(txtBlockedPeople.getText());
        }
    }

    private class FileSizePanel extends JPanel {
	private static final long serialVersionUID = -8457074359832858639L;
	private JSpinner spinMaxSize = new JSpinner();
        private JCheckBox chkMaxEnabled = new JCheckBox(TGuardRes.getString("guard.settings.limitcheck"));

        FileSizePanel() {
            setLayout(new VerticalFlowLayout());
            setBorder(new TitledBorder(TGuardRes.getString("guard.settings.title.filesize")));
            add(chkMaxEnabled);

            JPanel pnlSpinner = new JPanel(new GridBagLayout());
            pnlSpinner.add(new JLabel(TGuardRes.getString(("guard.settings.label.maxsize"))),
                    new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,2), 0, 0));
            pnlSpinner.add(spinMaxSize,
                    new GridBagConstraints(1, 0, 1, 1, 0.25, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0, 0));
            pnlSpinner.add(new JPanel(),
                    new GridBagConstraints(2, 0, 1, 1, 0.75, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0, 0));
            add(pnlSpinner);

            chkMaxEnabled.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent evnt) {
                    if (spinMaxSize != null) {
                        spinMaxSize.setEnabled(chkMaxEnabled.isSelected());
                    }
                }
            });
        }

        public void setMaxFileSize(int kb) {
            spinMaxSize.setValue(kb);
        }

        public int getMaxFileSize() {
            return (Integer) spinMaxSize.getValue();
        }

        public void setCheckFileSize(boolean check) {
            chkMaxEnabled.setSelected(check);
        }

        public boolean getCheckFileSize() {
            return chkMaxEnabled.isSelected();
        }
    }

    private class CannedResponsePanel extends JPanel {
 	private static final long serialVersionUID = -5992704440953686488L;
	private JTextArea txtMessage = new JTextArea(2, 0);

        CannedResponsePanel() {
            txtMessage.setBorder(UIManager.getLookAndFeelDefaults().getBorder("TextField.border"));
            txtMessage.setToolTipText(TGuardRes.getString(("guard.settings.tooltips.textarea")));
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createCompoundBorder(new TitledBorder(TGuardRes.getString(("guard.settings.title.rejectresponse"))),
                        new EmptyBorder(2,4,4,4)));
            add(txtMessage, BorderLayout.CENTER);
        }

        public void setCannedResponse(String message) {
            txtMessage.setText(message);
        }

        public String getCannedResponse() {
            return txtMessage.getText().trim();
        }
    }
}