/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.spark.ui.conferences;

import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.TitlePanel;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.sparkimpl.preference.chat.ChatPreference;
import org.jivesoftware.sparkimpl.preference.chat.ChatPreferences;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

final class JoinConferenceRoomDialog extends JPanel {
    private JLabel roomNameLabel = new JLabel();
    private JLabel nicknameLabel = new JLabel();
    private JLabel passwordLabel = new JLabel();
    private JPasswordField passwordField = new JPasswordField();
    private JTextField nicknameField = new JTextField();
    private GridBagLayout gridBagLayout1 = new GridBagLayout();
    private JLabel roomNameDescription = new JLabel();

    public JoinConferenceRoomDialog() {
        setLayout(gridBagLayout1);
        add(nicknameField, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        add(passwordField, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        add(passwordLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(nicknameLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(roomNameLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(roomNameDescription, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        add(new JLabel(), new GridBagConstraints(0, 3, 2, 1, 0.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.VERTICAL, new Insets(5, 5, 5, 5), 0, 0));

        // Add Resource Utils
        ResourceUtils.resLabel(nicknameLabel, nicknameField, "&Nickname:");
        ResourceUtils.resLabel(passwordLabel, passwordField, "&Password:");

        roomNameLabel.setText("Room Name:");
    }

    public void joinRoom(final String roomJID, final String roomName) {
        final ChatPreferences pref = (ChatPreferences)SparkManager.getPreferenceManager().getPreferenceData(ChatPreference.NAMESPACE);

        // Set default nickname
        nicknameField.setText(pref.getNickname());

        // Enable password field if a password is required
        passwordField.setVisible(false);
        passwordLabel.setVisible(false);


        roomNameDescription.setText(roomName);

        final JOptionPane pane;


        TitlePanel titlePanel;

        // Create the title panel for this dialog
        titlePanel = new TitlePanel("Join Conference Room", "Specify information for conference room.", SparkRes.getImageIcon(SparkRes.BLANK_IMAGE), true);

        // Construct main panel w/ layout.
        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // The user should only be able to close this dialog.
        Object[] options = {"Join", "Cancel"};
        pane = new JOptionPane(this, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null, options, options[0]);

        mainPanel.add(pane, BorderLayout.CENTER);

        final JOptionPane p = new JOptionPane();

        final JDialog dlg = p.createDialog(SparkManager.getMainWindow(), "Conference Rooms");
        dlg.setModal(false);

        dlg.pack();
        dlg.setSize(350, 250);
        dlg.setResizable(true);
        dlg.setContentPane(mainPanel);
        dlg.setLocationRelativeTo(SparkManager.getMainWindow());

        PropertyChangeListener changeListener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                String value = (String)pane.getValue();
                if ("Cancel".equals(value)) {
                    pane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                    dlg.dispose();
                }
                else if ("Join".equals(value)) {
                    pane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                    dlg.dispose();
                    ConferenceUtils.autoJoinConferenceRoom(roomName, roomJID, null);
                }
            }
        };

        pane.addPropertyChangeListener(changeListener);

        dlg.setVisible(true);
        dlg.toFront();
        dlg.requestFocus();

        SwingWorker worker = new SwingWorker() {
            boolean requiresPassword;

            public Object construct() {
                requiresPassword = ConferenceUtils.requiresPassword(roomJID);
                return new Boolean(requiresPassword);
            }

            public void finished() {
                passwordField.setVisible(requiresPassword);
                passwordLabel.setVisible(requiresPassword);
            }
        };
        worker.start();
    }
}
