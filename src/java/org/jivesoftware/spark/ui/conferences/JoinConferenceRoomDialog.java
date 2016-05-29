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
package org.jivesoftware.spark.ui.conferences;

import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.resource.Res;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.TitlePanel;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

final class JoinConferenceRoomDialog extends JPanel {
    private static final long serialVersionUID = -6400555509113588047L;
    private JLabel passwordLabel = new JLabel();
    private JPasswordField passwordField = new JPasswordField();
    private JTextField nicknameField = new JTextField();
    private JLabel roomNameDescription = new JLabel();

    public JoinConferenceRoomDialog() {
        GridBagLayout gridBagLayout1 = new GridBagLayout();
        setLayout( gridBagLayout1 );
        add(nicknameField, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        add(passwordField, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        add(passwordLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        JLabel nicknameLabel = new JLabel();
        add( nicknameLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        JLabel roomNameLabel = new JLabel();
        add( roomNameLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(roomNameDescription, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        add(new JLabel(), new GridBagConstraints(0, 3, 2, 1, 0.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.VERTICAL, new Insets(5, 5, 5, 5), 0, 0));

        // Add Resource Utils
        ResourceUtils.resLabel( nicknameLabel, nicknameField, Res.getString("label.nickname") + ":");
        ResourceUtils.resLabel(passwordLabel, passwordField, Res.getString("label.password") + ":");

        roomNameLabel.setText(Res.getString("room.name") +":");
    }

    public void joinRoom(final String roomJID, final String roomName) {
        final LocalPreferences pref = SettingsManager.getLocalPreferences();

        // Set default nickname
        nicknameField.setText(pref.getNickname());

        // Enable password field if a password is required
        passwordField.setVisible(false);
        passwordLabel.setVisible(false);


        roomNameDescription.setText(roomName);

        final JOptionPane pane;


        TitlePanel titlePanel;

        // Create the title panel for this dialog
        titlePanel = new TitlePanel(Res.getString("title.join.conference.room"), Res.getString("message.specify.information.for.conference"), SparkRes.getImageIcon(SparkRes.BLANK_IMAGE), true);

        // Construct main panel w/ layout.
        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // The user should only be able to close this dialog.
        Object[] options = {Res.getString("join"), Res.getString("cancel")};
        pane = new JOptionPane(this, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null, options, options[0]);

        mainPanel.add(pane, BorderLayout.CENTER);

        final JOptionPane p = new JOptionPane();

        final JDialog dlg = p.createDialog(SparkManager.getMainWindow(), Res.getString("title.conference.rooms"));
        dlg.setModal(false);

        dlg.pack();
        dlg.setSize(350, 250);
        dlg.setResizable(true);
        dlg.setContentPane(mainPanel);
        dlg.setLocationRelativeTo(SparkManager.getMainWindow());

        PropertyChangeListener changeListener = e -> {
            String value = (String)pane.getValue();
            if (Res.getString("cancel").equals(value)) {
                pane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                dlg.dispose();
            }
            else if (Res.getString("join").equals(value)) {
                pane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                dlg.dispose();
                ConferenceUtils.joinConferenceOnSeperateThread(roomName, roomJID, null);
            }
        };

        pane.addPropertyChangeListener(changeListener);

        dlg.setVisible(true);
        dlg.toFront();
        dlg.requestFocus();

        SwingWorker worker = new SwingWorker() {
            boolean requiresPassword;

            public Object construct() {
                requiresPassword = ConferenceUtils.isPasswordRequired(roomJID);
                return requiresPassword;
            }

            public void finished() {
                passwordField.setVisible(requiresPassword);
                passwordLabel.setVisible(requiresPassword);
            }
        };
        worker.start();
    }
}
