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
import org.jivesoftware.spark.util.ResourceUtils;

import javax.swing.JPanel;
import javax.swing.Timer;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * RetryPanel is the UI/Function class to handle reconnection logic. This allows for a simple card layout to replace the current
 * roster when the connection has been lost.
 *
 * @author Derek DeMoro
 */
public class RetryPanel extends JPanel {
    private WrappedLabel descriptionLabel;
    private RolloverButton retryButton;
    private RolloverButton cancelButton;

    private Timer timer;
    private int countdown;

    private List listeners = new ArrayList();

    /**
     * Construct the RetryPanel.
     */
    public RetryPanel() {
        setLayout(new GridBagLayout());

        countdown = 45;

        // Init Components
        descriptionLabel = new WrappedLabel();

        retryButton = new RolloverButton(SparkRes.getImageIcon(SparkRes.SMALL_CHECK));
        cancelButton = new RolloverButton("", SparkRes.getImageIcon(SparkRes.SMALL_CHECK));

        ResourceUtils.resButton(cancelButton, Res.getString("button.reconnect"));

        layoutComponents();

        retryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                //attemptReconnect();
            }
        });


        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                logout();
            }
        });

        setBackground(Color.white);

        // Set Font
        descriptionLabel.setFont(new Font("Dialog", Font.PLAIN, 13));
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
        buttonPanel.add(cancelButton);

        add(buttonPanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    }

    /**
     * Starts the countdown to the next retry attempt. The retry attemp is set for every 45 seconds or what is set
     * as the default in preferences.
     */
    protected void startTimer() {
        retryButton.setVisible(true);

        ActionListener updateTime = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (countdown > 0) {
                    retryButton.setText("Retry in " + countdown);
                    countdown--;
                }
                else {
                    //attemptReconnect();
                }
            }
        };

        timer = new Timer(1000, updateTime);
        timer.start();
    }

    /**
     * Changes the UI to handle when a conflict occurs on the server.
     */
    public void showConflict() {
        retryButton.setVisible(false);
    }

    /*
    private void attemptReconnect() {
        retryButton.setText("Attempting....");
        timer.stop();

        SwingWorker worker = new SwingWorker() {
            public Object construct() {
                try {
                    Thread.sleep(500);
                }
                catch (InterruptedException e) {
                    Log.error(e);
                }

                return timer;
            }

            public void finished() {
                XMPPConnection con;
                try {
                    con = SparkManager.getConnection().reconnect();
                    SparkManager.getSessionManager().setConnection(con);
                    SparkManager.getMessageEventManager().setConnection(con);
                    fireReconnection();
                }
                catch (XMPPException e) {
                    countdown = 45;
                    timer.start();
                }
            }
        };

        worker.start();


    }
    */

    /**
     * Adds a <code>ReconnectListener</code>.
     *
     * @param listener the listener to add.
     */
    public void addReconnectionListener(ReconnectListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a <code>ReconnectListener</code>.
     *
     * @param listener the listener to remove.
     */
    public void removeReconnectionListener(ReconnectListener listener) {
        listeners.remove(listener);
    }

    private void fireReconnection() {
        final Iterator iter = ModelUtil.reverseListIterator(listeners.listIterator());
        while (iter.hasNext()) {
            ReconnectListener listener = (ReconnectListener)iter.next();
            listener.reconnected();
        }
    }

    private void fireCancelled() {
        final Iterator iter = ModelUtil.reverseListIterator(listeners.listIterator());
        while (iter.hasNext()) {
            ReconnectListener listener = (ReconnectListener)iter.next();
            listener.cancelled();
        }
    }


    private void logout() {
        SparkManager.getMainWindow().setVisible(false);
        SparkManager.getMainWindow().logout(false);
    }

    /**
     * Implementation of this class if you wish to be notified of reconnection or cancelling events when Spark
     * loses it's connection to the server.
     */
    public interface ReconnectListener {

        /**
         * Spark has successfully reconnected.
         */
        void reconnected();

        /**
         * The user has decided to cancel the reconnection attempt.
         */
        void cancelled();
    }


}
