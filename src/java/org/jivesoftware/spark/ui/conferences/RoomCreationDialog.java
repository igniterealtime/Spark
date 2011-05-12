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
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.TitlePanel;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.log.Log;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class RoomCreationDialog extends JPanel {
    private static final long serialVersionUID = -8391698290385575601L;
    private JLabel nameLabel = new JLabel();
    private JLabel topicLabel = new JLabel();
    private JLabel passwordLabel = new JLabel();
    private JLabel confirmPasswordLabel = new JLabel();
    private JCheckBox permanentCheckBox = new JCheckBox();
    private JCheckBox privateCheckbox = new JCheckBox();
    private JTextField nameField = new JTextField();
    private JTextField topicField = new JTextField();
    private JPasswordField passwordField = new JPasswordField();
    private JPasswordField confirmPasswordField = new JPasswordField();
    private GridBagLayout gridBagLayout1 = new GridBagLayout();
    private MultiUserChat groupChat = null;

    public RoomCreationDialog() {
        try {
            jbInit();
        }
        catch (Exception e) {
            Log.error(e);
        }
    }

    private void jbInit() throws Exception {
        this.setLayout(gridBagLayout1);
        this.add(confirmPasswordField, new GridBagConstraints(1, 4, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        this.add(passwordField, new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        this.add(topicField, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        this.add(nameField, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        this.add(privateCheckbox, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        this.add(permanentCheckBox, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        this.add(confirmPasswordLabel, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        this.add(passwordLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        this.add(topicLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 5, 0));
        this.add(nameLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

        ResourceUtils.resLabel(nameLabel, nameField, Res.getString("label.room.name"));
        ResourceUtils.resLabel(topicLabel, topicField, Res.getString("label.room.topic") + ":");
        ResourceUtils.resLabel(passwordLabel, passwordField, Res.getString("label.password") + ":");
        ResourceUtils.resLabel(confirmPasswordLabel, confirmPasswordField, Res.getString("label.confirm.password") + ":");
        ResourceUtils.resButton(permanentCheckBox, Res.getString("checkbox.permanent"));
        ResourceUtils.resButton(privateCheckbox, Res.getString("checkbox.private.room"));
    }

    public MultiUserChat createGroupChat(Component parent, final String serviceName) {
        final JOptionPane pane;
        final JDialog dlg;

        TitlePanel titlePanel;

        // Create the title panel for this dialog
        titlePanel = new TitlePanel(Res.getString("title.create.or.join"), Res.getString("message.create.or.join.room"), SparkRes.getImageIcon(SparkRes.BLANK_24x24), true);

        // Construct main panel w/ layout.
        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // The user should only be able to close this dialog.
        Object[] options = {Res.getString("create"), Res.getString("close")};
        pane = new JOptionPane(this, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null, options, options[0]);

        mainPanel.add(pane, BorderLayout.CENTER);

        JOptionPane p = new JOptionPane();
        dlg = p.createDialog(parent, Res.getString("title.conference.rooms"));
        dlg.pack();
        dlg.setSize(400, 350);
        dlg.setContentPane(mainPanel);
        dlg.setLocationRelativeTo(parent);


        PropertyChangeListener changeListener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                Object o = pane.getValue();
                if (o instanceof Integer) {
                    dlg.setVisible(false);
                    return;
                }

                String value = (String)pane.getValue();
                if (Res.getString("close").equals(value)) {
                    dlg.setVisible(false);
                }
                else if (Res.getString("create").equals(value)) {
                    boolean isValid = validatePanel();
                    if (isValid) {
                        String room = nameField.getText().replaceAll(" ", "_") + "@" + serviceName;
                        try {
                            MultiUserChat.getRoomInfo(SparkManager.getConnection(), room);
                            //JOptionPane.showMessageDialog(dlg, "Room already exists. Please specify a unique room name.", "Room Exists", JOptionPane.ERROR_MESSAGE);
                            //pane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                            pane.removePropertyChangeListener(this);
                            dlg.setVisible(false);
                            ConferenceUtils.joinConferenceRoom(room, room);
                            return;
                        }
                        catch (XMPPException e1) {
                            // Nothing to do
                        }

                        groupChat = createGroupChat(nameField.getText(), serviceName);
                        if (groupChat == null) {
                            showError("Could not join chat " + nameField.getText());
                            pane.setValue(JOptionPane.UNINITIALIZED_VALUE);

                        }
                        else {
                            pane.removePropertyChangeListener(this);
                            dlg.setVisible(false);
                        }
                    }
                    else {
                        pane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                    }
                }
            }
        };

        pane.addPropertyChangeListener(changeListener);
        nameField.requestFocusInWindow();

        dlg.setVisible(true);
        dlg.toFront();
        dlg.requestFocus();

        return groupChat;
    }

    private boolean validatePanel() {
        String roomName = nameField.getText();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        boolean isPrivate = privateCheckbox.isSelected();

        // Check for valid information
        if (!ModelUtil.hasLength(roomName)) {
            showError(Res.getString("message.specify.name.error"));
            nameField.requestFocus();
            return false;
        }

        if (isPrivate) {
            if (!ModelUtil.hasLength(password)) {
                showError(Res.getString("message.password.private.room.error"));
                passwordField.requestFocus();
                return false;
            }

            if (!ModelUtil.hasLength(confirmPassword)) {
                showError(Res.getString("message.confirmation.password.error"));
                confirmPasswordField.requestFocus();
                return false;
            }

            if (!ModelUtil.areEqual(password, confirmPassword)) {
                showError(Res.getString("message.passwords.no.match"));
                passwordField.requestFocus();
                return false;
            }
        }

        return true;
    }

    private MultiUserChat createGroupChat(String roomName, String serviceName) {
        String room = roomName.replaceAll(" ", "_") + "@" + serviceName;

        // Create a group chat with valid information
        return new MultiUserChat(SparkManager.getConnection(), room.toLowerCase());
    }


    private void showError(String errorMessage) {
        JOptionPane.showMessageDialog(this, errorMessage, Res.getString("title.error"), JOptionPane.ERROR_MESSAGE);
    }

    public boolean isPrivate() {
        return privateCheckbox.isSelected();
    }

    public boolean isPermanent() {
        return permanentCheckBox.isSelected();
    }

    public boolean isPasswordProtected() {
        String password = new String(passwordField.getPassword());
        if (password.length() > 0) {
            return true;
        }

        return false;
    }

    public String getPassword() {
        return new String(confirmPasswordField.getPassword());
    }

    /**
     * Returns the Room name of the RoomCreationDialog
     *
     * @return The Room name
     */
    public String getRoomName() {
        return nameField.getText();
    }
    /**
     * Returns the Topic of the RoomCreationDialog
     * @return The Rooms Topic
     */
    public String getRoomTopic() {
        return topicField.getText();
    }

}