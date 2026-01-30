/**
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
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.TitlePanel;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.log.Log;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import static java.awt.GridBagConstraints.*;

public class RoomCreationDialog extends JPanel {
    private static final long serialVersionUID = -8391698290385575601L;
    private final GridBagLayout gridBagLayout1 = new GridBagLayout();
    private final JLabel nameLabel = new JLabel();
    private final JTextField nameField = new JTextField();
    private final JLabel topicLabel = new JLabel();
    private final JTextField topicField = new JTextField();
    private final JCheckBox permanentCheckBox = new JCheckBox();
    private final JCheckBox hasPasswordCheckbox = new JCheckBox();
    private final JLabel passwordLabel = new JLabel();
    private final JPasswordField passwordField = new JPasswordField();
    private final JLabel confirmPasswordLabel = new JLabel();
    private final JPasswordField confirmPasswordField = new JPasswordField();
    private MultiUserChat groupChat = null;

    public RoomCreationDialog() {
        try {
            jbInit();
        } catch (Exception e) {
            Log.error(e);
        }
    }

    private void jbInit() {
        this.setLayout(gridBagLayout1);
        Insets insets = new Insets(5, 5, 5, 5);
        this.add(nameLabel, new GridBagConstraints(0, 0, 1, 1, 0, 0, WEST, NONE, insets, 0, 0));
        this.add(nameField, new GridBagConstraints(1, 0, 1, 1, 1, 0, WEST, HORIZONTAL, insets, 0, 0));
        this.add(topicLabel, new GridBagConstraints(0, 1, 1, 1, 0, 0, WEST, NONE, insets, 5, 0));
        this.add(topicField, new GridBagConstraints(1, 1, 1, 1, 1, 0, WEST, HORIZONTAL, insets, 0, 0));
        this.add(permanentCheckBox, new GridBagConstraints(0, 2, 1, 1, 0, 0, WEST, NONE, insets, 0, 0));
        this.add(hasPasswordCheckbox, new GridBagConstraints(0, 3, 1, 1, 0, 1, NORTHWEST, NONE, insets, 0, 0));
        this.add(passwordLabel, new GridBagConstraints(0, 4, 1, 1, 0, 0, WEST, NONE, insets, 0, 0));
        this.add(passwordField, new GridBagConstraints(1, 4, 1, 1, 1, 0, WEST, HORIZONTAL, insets, 0, 0));
        this.add(confirmPasswordLabel, new GridBagConstraints(0, 5, 1, 1, 0, 0, WEST, NONE, insets, 0, 0));
        this.add(confirmPasswordField, new GridBagConstraints(1, 5, 1, 1, 1, 1, WEST, HORIZONTAL, insets, 0, 0));

        ResourceUtils.resLabel(nameLabel, nameField, Res.getString("label.room.name"));
        ResourceUtils.resLabel(topicLabel, topicField, Res.getString("label.room.topic") + ":");
        ResourceUtils.resButton(permanentCheckBox, Res.getString("checkbox.permanent"));
        ResourceUtils.resButton(hasPasswordCheckbox, Res.getString("checkbox.private.room"));
        ResourceUtils.resLabel(passwordLabel, passwordField, Res.getString("label.password") + ":");
        ResourceUtils.resLabel(confirmPasswordLabel, confirmPasswordField, Res.getString("label.confirm.password") + ":");

        passwordLabel.setVisible(false);
        passwordField.setVisible(false);
        confirmPasswordLabel.setVisible(false);
        confirmPasswordField.setVisible(false);

        hasPasswordCheckbox.addActionListener(changeEvent -> {
            boolean hasPassword = hasPasswordCheckbox.isSelected();
            passwordLabel.setVisible(hasPassword);
            passwordField.setVisible(hasPassword);
            confirmPasswordLabel.setVisible(hasPassword);
            confirmPasswordField.setVisible(hasPassword);
        });
    }

    public MultiUserChat createGroupChat(Component parent, final DomainBareJid serviceName) {
        final JOptionPane pane;
        final JDialog dlg;

        TitlePanel titlePanel;

        // Create the title panel for this dialog
        titlePanel = new TitlePanel(Res.getString("title.create.room"), Res.getString("message.create.or.join.room"), SparkRes.getImageIcon(SparkRes.BLANK_24x24), true);

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
            @Override
            public void propertyChange(PropertyChangeEvent e) {
                Object o = pane.getValue();
                if (o instanceof Integer) {
                    dlg.setVisible(false);
                    return;
                }

                String value = (String) pane.getValue();
                // Closes dialog or creates group chat based on input
                if (Res.getString("close").equals(value)) {
                    dlg.setVisible(false);
                } else if (Res.getString("create").equals(value)) {
                    boolean isValid = validatePanel();
                    if (isValid) {
                        String roomJidString = nameField.getText().replaceAll(" ", "_") + "@" + serviceName;
                        EntityBareJid room;
                        try {
                            room = JidCreate.entityBareFrom(roomJidString);
                        } catch (XmppStringprepException ex) {
                            throw new IllegalStateException(ex);
                        }
                        try {
                            MultiUserChatManager.getInstanceFor(SparkManager.getConnection()).getRoomInfo(room);
                            //JOptionPane.showMessageDialog(dlg, "Room already exists. Please specify a unique room name.", "Room Exists", JOptionPane.ERROR_MESSAGE);
                            //pane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                            pane.removePropertyChangeListener(this);
                            dlg.setVisible(false);
                            ConferenceUtils.joinConferenceRoom(room.toString(), room);
                            return;
                        } catch (XMPPException | SmackException | InterruptedException ignored) {
                        }

                        groupChat = createGroupChat(nameField.getText(), serviceName);
                        if (groupChat == null) {
                            showError("Could not join chat " + nameField.getText());
                            pane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                        } else {
                            pane.removePropertyChangeListener(this);
                            dlg.setVisible(false);
                        }
                    } else {
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
        boolean hasPassword = hasPasswordCheckbox.isSelected();

        // Check for valid information
        if (!ModelUtil.hasLength(roomName)) {
            showError(Res.getString("message.specify.name.error"));
            nameField.requestFocus();
            return false;
        }

        if (hasPassword) {
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
            if (!password.equals(confirmPassword)) {
                showError(Res.getString("message.passwords.no.match"));
                passwordField.requestFocus();
                return false;
            }
        }
        return true;
    }

    private MultiUserChat createGroupChat(String roomName, DomainBareJid serviceName) {
        String roomString = roomName.replaceAll(" ", "_") + "@" + serviceName;
        EntityBareJid room = JidCreate.entityBareFromOrThrowUnchecked(roomString);
        // Create a group chat with valid information
        return MultiUserChatManager.getInstanceFor(SparkManager.getConnection()).getMultiUserChat(room);
    }

    private void showError(String errorMessage) {
        UIManager.put("OptionPane.okButtonText", Res.getString("ok"));
        JOptionPane.showMessageDialog(this, errorMessage, Res.getString("title.error"), JOptionPane.ERROR_MESSAGE);
    }

    public boolean hasPassword() {
        return hasPasswordCheckbox.isSelected();
    }

    public boolean isPermanent() {
        return permanentCheckBox.isSelected();
    }

    public boolean isPasswordProtected() {
        return passwordField.getPassword().length > 0;
    }

    public String getPassword() {
        return new String(confirmPasswordField.getPassword());
    }

    /**
     * Returns the Room name of the RoomCreationDialog
     */
    public String getRoomName() {
        return nameField.getText();
    }

    /**
     * Returns the Rooms Topic of the RoomCreationDialog
     */
    public String getRoomTopic() {
        return topicField.getText();
    }

}
