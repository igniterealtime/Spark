/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.spark.ui;

import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.component.WrappedLabel;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.SwingWorker;

import javax.swing.JPanel;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * RetryPanel is the UI/Function class to handle reconnection logic. This allows for a simple card layout to replace the current
 * roster when the connection has been lost.
 *
 * @author Derek DeMoro
 */
public class RetryPanel extends JPanel {
    private WrappedLabel descriptionLabel;
    private RolloverButton retryButton;

    /**
     * Construct the RetryPanel.
     */
    public RetryPanel() {
        setLayout(new GridBagLayout());

        // Init Components
        descriptionLabel = new WrappedLabel();

        retryButton = new RolloverButton(SparkRes.getImageIcon(SparkRes.SMALL_CHECK));

        layoutComponents();

        retryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                attemptReconnection();
            }
        });


        setBackground(Color.white);

        // Set Font
        descriptionLabel.setFont(new Font("Dialog", Font.PLAIN, 12));

        retryButton.setText("Reconnect");
    }

    private void attemptReconnection() {
        retryButton.setText("Attempting...");
        retryButton.setEnabled(false);

        SwingWorker worker = new SwingWorker() {
            public Object construct() {
                try {
                    SparkManager.getConnection().connect();
                    return true;
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }

            public void finished() {
                retryButton.setEnabled(true);

                if ((Boolean)get()) {
                    ContactList list = SparkManager.getWorkspace().getContactList();
                    list.clientReconnected();
                }
                else {
                    retryButton.setText("Reconnect");
                }
            }

        };

        worker.start();


    }

    /**
     * Sets the reason the user was disconnected from the server.
     *
     * @param reason the reason the user was disconnected from the server.
     */
    public void setDisconnectReason(String reason) {
        if (!ModelUtil.hasLength(reason)) {
            reason = Res.getString("message.generic.reconnect.message");
        }
        descriptionLabel.setText(reason);
    }

    private void layoutComponents() {
        add(descriptionLabel, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

        final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(retryButton);

        add(buttonPanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    }

    /**
     * Starts the countdown to the next retry attempt. The retry attemp is set for every 45 seconds or what is set
     * as the default in preferences.
     *
     * @param text the text to display on the reconnect button.
     */
    protected void setReconnectText(String text) {
        retryButton.setVisible(true);
        retryButton.setText(text);
    }

    /**
     * Changes the UI to handle when a conflict occurs on the server.
     */
    public void showConflict() {
        retryButton.setVisible(false);
    }
}
