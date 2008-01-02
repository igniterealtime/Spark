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
        private JTextArea txtBlockedTypes = new JTextArea(2, 0);

        BlockedTypesPanel() {
            txtBlockedTypes.setBorder(UIManager.getLookAndFeelDefaults().getBorder("TextField.border"));
            txtBlockedTypes.setToolTipText("Enter file extensions you wish to block (eg. '*.doc'), separated by commas");
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createCompoundBorder(new TitledBorder("Blocked File Extensions"),
                        new EmptyBorder(2,4,4,4)));
            add(txtBlockedTypes, BorderLayout.CENTER);
        }

        public void setBlockedTypes(List types) {
            txtBlockedTypes.setText(FileTransferSettings.convertSettingsListToString(types));
        }

        public List getBlockedTypes() {
            return FileTransferSettings.convertSettingsStringToList(txtBlockedTypes.getText());
        }
    }

    private class BlockedPeoplePanel extends JPanel {
        private JTextArea txtBlockedPeople = new JTextArea(2, 0);

        BlockedPeoplePanel() {
            txtBlockedPeople.setBorder(UIManager.getLookAndFeelDefaults().getBorder("TextField.border"));
            txtBlockedPeople.setToolTipText("Enter the JID of users from whom you want to block file transfers " +
                                            "(eg. 'loser@domain.com'), separated by commas");
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createCompoundBorder(new TitledBorder("Blocked Senders"),
                        new EmptyBorder(2,4,4,4)));
            add(txtBlockedPeople, BorderLayout.CENTER);
        }

        public void setBlockedPeople(List people) {
            txtBlockedPeople.setText(FileTransferSettings.convertSettingsListToString(people));
        }

        public List getBlockedPeople() {
            return FileTransferSettings.convertSettingsStringToList(txtBlockedPeople.getText());
        }
    }

    private class FileSizePanel extends JPanel {
        private JSpinner spinMaxSize = new JSpinner();
        private JCheckBox chkMaxEnabled = new JCheckBox("Limit File Size");

        FileSizePanel() {
            setLayout(new VerticalFlowLayout());
            setBorder(new TitledBorder("File Size"));
            add(chkMaxEnabled);

            JPanel pnlSpinner = new JPanel(new GridBagLayout());
            pnlSpinner.add(new JLabel("Maximum Size in KB:"),
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
        private JTextArea txtMessage = new JTextArea(2, 0);

        CannedResponsePanel() {
            txtMessage.setBorder(UIManager.getLookAndFeelDefaults().getBorder("TextField.border"));
            txtMessage.setToolTipText("Enter a message you would like to send to those whose files are blocked by your " +
                                      "settings. Leave this space blank if you don't wish to send them a message.");
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createCompoundBorder(new TitledBorder("Automated Rejection Response"),
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