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

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.TimerTask;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.text.html.HTMLEditorKit;

import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.SwingTimerTask;
import org.jivesoftware.spark.util.TaskEngine;

/**
 * RetryPanel is the UI/Function class to handle reconnection logic. This allows for a simple card layout to replace the current
 * roster when the connection has been lost.
 *
 * @author Derek DeMoro
 */
public class RetryPanel extends JPanel implements ConnectionListener {
	private static final long serialVersionUID = -7099075581561760774L;
	private JEditorPane pane;
    private RolloverButton retryButton;
    private boolean closedOnError;

    /**
     * Construct the RetryPanel.
     */
    public RetryPanel() {
        setLayout(new GridBagLayout());

        // Init Components
        pane = new JEditorPane();
        pane.setBackground(Color.white);
        pane.setEditorKit(new HTMLEditorKit());
        pane.setEditable(false);

        retryButton = new RolloverButton(SparkRes.getImageIcon(SparkRes.SMALL_CHECK));

        layoutComponents();

        retryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                attemptReconnection();
            }
        });


        setBackground(Color.white);

        retryButton.setText(Res.getString("button.reconnect2"));

        SparkManager.getConnection().addConnectionListener(this);
    }

    private void attemptReconnection() {
        retryButton.setText(Res.getString("message.reconnect.attempting"));
        retryButton.setEnabled(false);

        TimerTask task = new SwingTimerTask() {
            public void doRun() {
                reconnect();
            }
        };

        TaskEngine.getInstance().schedule(task, 100);
    }

    private void reconnect() {
        try {
            if (closedOnError) {
            	SparkManager.getConnection().connect();
            }
            else {
                SparkManager.getMainWindow().logout(false);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
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

        StringBuilder builder = new StringBuilder();
        builder.append("<html><body><table height=100% width=100%><tr><td align=center>");
        builder.append("<b><u>");
        builder.append(reason);
        builder.append("</u></b>");
        builder.append("</td></tr></table></body></html>");

        pane.setText(builder.toString());
    }

    private void layoutComponents() {
        add(pane, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

        add(retryButton, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
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

    public void setClosedOnError(boolean onError) {
        closedOnError = onError;
    }


    public void connectionClosed() {
        retryButton.setVisible(true);
        retryButton.setEnabled(true);
    }

    public void connectionClosedOnError(Exception e) {
        retryButton.setVisible(true);
        retryButton.setEnabled(true);
    }

    public void reconnectingIn(int seconds) {
    }

    public void reconnectionSuccessful() {
        retryButton.setVisible(false);
        retryButton.setEnabled(true);
    }

    public void reconnectionFailed(Exception e) {
        retryButton.setVisible(true);
        retryButton.setEnabled(true);
    }
}
