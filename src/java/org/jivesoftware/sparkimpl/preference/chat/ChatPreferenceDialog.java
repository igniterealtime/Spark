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


import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.TitlePanel;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * The Dialog UI to handle changing of ChatPreferences.
 */
public class ChatPreferenceDialog implements PropertyChangeListener {
    private ChatPreferencePanel chatPreferencePanel;
    private ChatPreferences chatPreferences;

    private JOptionPane optionPane;
    private JDialog preferenceDialog;

    private TitlePanel titlePanel;

    /**
     * Empty Constructor
     */
    public ChatPreferenceDialog() {
    }

    /**
     * Invoke the ChatPreference Dialog
     *
     * @param preferences the preferences to use with this dialog.
     */
    public void showDialog(ChatPreferences preferences) {
        chatPreferencePanel = new ChatPreferencePanel();

        if (preferences != null) {
            chatPreferences = preferences;
        }
        else {
            chatPreferences = new ChatPreferences();
            chatPreferences.showDatesInChat(true);
        }

        // Set default values
        chatPreferencePanel.setShowTime(chatPreferences.showDatesInChat());

        // Create the title chatPreferencePanel for this dialog
        titlePanel = new TitlePanel("Chat Window Preferences",
                "Preferences used by the Chat Window",
                SparkRes.getImageIcon(SparkRes.BLANK_24x24), false);

        // Construct main chatPreferencePanel w/ layout.
        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // The user should only be able to close this dialog.
        Object[] options = {"Ok", "Cancel"};
        optionPane = new JOptionPane(chatPreferencePanel, JOptionPane.PLAIN_MESSAGE,
                JOptionPane.OK_CANCEL_OPTION, null, options, options[0]);

        mainPanel.add(optionPane, BorderLayout.CENTER);

        preferenceDialog = new JDialog(SparkManager.getMainWindow(), "Chat Window", true);
        preferenceDialog.pack();
        preferenceDialog.setSize(400, 300);
        preferenceDialog.setContentPane(mainPanel);
        preferenceDialog.setLocationRelativeTo(SparkManager.getMainWindow());
        optionPane.addPropertyChangeListener(this);

        preferenceDialog.setVisible(true);
        preferenceDialog.toFront();
        preferenceDialog.requestFocus();
    }

    public void propertyChange(PropertyChangeEvent e) {
        String value = (String)optionPane.getValue();
        if ("Cancel".equals(value)) {
            preferenceDialog.setVisible(false);
        }
        else if ("Ok".equals(value)) {
            chatPreferences.showDatesInChat(chatPreferencePanel.getShowTime());
            preferenceDialog.setVisible(false);
        }
    }

    /**
     * Return the current modified preferences.
     *
     * @return the current modified preferences.
     */
    protected ChatPreferences getPreferences() {
        return chatPreferences;
    }


}