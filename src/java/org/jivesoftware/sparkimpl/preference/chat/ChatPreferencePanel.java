/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkimpl.preference.chat;

import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.VerticalFlowLayout;
import org.jivesoftware.spark.util.ResourceUtils;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

/**
 * The Preference UI used to handle changing of Chat Preferences.
 */
public class ChatPreferencePanel extends JPanel implements ActionListener {

    private JCheckBox showTimeBox = new JCheckBox();
    private JCheckBox spellCheckBox = new JCheckBox();
    private JCheckBox groupChatNotificationBox = new JCheckBox();
    private JPanel generalPanel = new JPanel();
    private JPanel chatWindowPanel = new JPanel();

    // Password changing
    private JPasswordField passwordField = new JPasswordField();
    private JPasswordField confirmationPasswordField = new JPasswordField();
    private JLabel passwordLabel = new JLabel();
    private JLabel confirmationPasswordLabel = new JLabel();
    private JCheckBox hideChatHistory = new JCheckBox();

    /**
     * Constructor invokes UI setup.
     */
    public ChatPreferencePanel() {
        // Build the UI
        createUI();
    }

    private void createUI() {
        setLayout(new VerticalFlowLayout());

        // Setup Mnemonics
        ResourceUtils.resButton(showTimeBox, "&Show time in chat window");
        ResourceUtils.resLabel(passwordLabel, passwordField, "&Change Password To:");
        ResourceUtils.resLabel(confirmationPasswordLabel, confirmationPasswordField, "Confirm &Password:");
        ResourceUtils.resButton(spellCheckBox, "&Perform spell checking in background");
        ResourceUtils.resButton(groupChatNotificationBox, "&Show notifications in conference rooms");
        ResourceUtils.resButton(hideChatHistory, "&Disable Chat History");

        generalPanel.setBorder(BorderFactory.createTitledBorder("General Information"));
        chatWindowPanel.setBorder(BorderFactory.createTitledBorder("Chat Window Information"));

        add(generalPanel);
        add(chatWindowPanel);

        generalPanel.setLayout(new GridBagLayout());
        chatWindowPanel.setLayout(new GridBagLayout());

        // Chat Window Panel settings
        chatWindowPanel.add(showTimeBox, new GridBagConstraints(0, 0, 2, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        chatWindowPanel.add(spellCheckBox, new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        chatWindowPanel.add(groupChatNotificationBox, new GridBagConstraints(0, 2, 2, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        chatWindowPanel.add(hideChatHistory, new GridBagConstraints(0, 3, 2, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));


        generalPanel.add(passwordLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        generalPanel.add(passwordField, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 100, 0));
        generalPanel.add(confirmationPasswordLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        generalPanel.add(confirmationPasswordField, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 100, 0));

        hideChatHistory.addActionListener(this);
    }

    /**
     * Set to true to have the ChatWindow show the timestamp of each message.
     *
     * @param showTime true to show timestamp of each message.
     */
    public void setShowTime(boolean showTime) {
        showTimeBox.setSelected(showTime);
    }

    /**
     * Returns true if the ChatWindow should show a timestamp of each message.
     *
     * @return true if the ChatWindow should show a timestamp of each message.
     */
    public boolean getShowTime() {
        return showTimeBox.isSelected();
    }


    /**
     * Returns the new password to use.
     *
     * @return the new password to use.
     */
    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    /**
     * Returns the confirmation password used to compare to the first password.
     *
     * @return the confirmation password used to compare to the first password.
     */
    public String getConfirmationPassword() {
        return new String(confirmationPasswordField.getPassword());
    }

    public void setSpellCheckerOn(boolean on) {
        spellCheckBox.setSelected(on);
    }

    public boolean isSpellCheckerOn() {
        return spellCheckBox.isSelected();
    }

    public void setGroupChatNotificationsOn(boolean on) {
        groupChatNotificationBox.setSelected(on);
    }

    public boolean isGroupChatNotificationsOn() {
        return groupChatNotificationBox.isSelected();
    }

    public void setChatHistoryHidden(boolean hide) {
        hideChatHistory.setSelected(hide);
    }

    public boolean isChatHistoryHidden() {
        return hideChatHistory.isSelected();
    }

    public void actionPerformed(ActionEvent actionEvent) {
        if (hideChatHistory.isSelected()) {
            int ok = JOptionPane.showConfirmDialog(this, "Delete all previous history?", "Delete Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (ok == JOptionPane.YES_OPTION) {
                File transcriptDir = new File(SparkManager.getUserDirectory(), "transcripts");
                File[] files = transcriptDir.listFiles();

                for (int i = 0; i < files.length; i++) {
                    File transcriptFile = files[i];
                    transcriptFile.delete();
                }
            }
        }
    }

}