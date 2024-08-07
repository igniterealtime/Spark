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
package org.jivesoftware.spark.ui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;

import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.ReconnectionListener;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.util.ModelUtil;

/**
 * RetryPanel is the UI/Function class to handle reconnection logic. This allows for a simple card layout to replace the current
 * roster when the connection has been lost.
 *
 * @author Derek DeMoro
 */
public class ReconnectPanel extends JPanel implements ConnectionListener, ReconnectionListener {
	private static final long serialVersionUID = -7099075581561760774L;
	private final JEditorPane pane;
    private final JLabel _icon;

    private final JButton _button;

    /**
     * Construct the RetryPanel.
     */
    public ReconnectPanel() {
        setLayout(new GridBagLayout());

        // Init Components
        pane = new JEditorPane();
        pane.setBackground(Color.white);
        pane.setEditorKit(new HTMLEditorKit());
        pane.setEditable(false);

        _icon = new JLabel(SparkRes.getImageIcon(SparkRes.BUSY_IMAGE));
        _button = new JButton(Res.getString("button.reconnect2"));
        _button.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                final int selectedOption = JOptionPane.showConfirmDialog(null,
                    Res.getString("message.restart.required"),
                    Res.getString("title.alert"),
                    JOptionPane.YES_NO_OPTION);
                if (selectedOption == JOptionPane.YES_OPTION) {
                    SparkManager.getMainWindow().logout(false);
                }
            }
        });
        layoutComponents();

        setBackground(Color.white);

        _icon.setText(Res.getString("button.reconnect2"));

        SparkManager.getConnection().addConnectionListener(this);

        ReconnectionManager.getInstanceFor(SparkManager.getConnection()).addReconnectionListener(this);
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

        String builder = "<html><body><table height=100% width=100%><tr><td align=center>" +
                "<b><u>" +
                reason +
                "</u></b>" +
                "</td></tr></table></body></html>";

        pane.setText( builder );
    }

    private void layoutComponents() {
        add(pane, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
        add(_icon, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(_button, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    }

    /**
     * Starts the countdown to the next retry attempt. The retry attemp is set for every 45 seconds or what is set
     * as the default in preferences.
     *
     * @param text the text to display on the reconnect button.
     */
    protected void setReconnectText(String text) {
        _icon.setVisible(true);
        _icon.setText(text);
    }

    /**
     * Changes the UI to handle when a conflict occurs on the server.
     */
    public void showConflict() {
        _icon.setVisible(false);
    }

    @Override
    public void connected( XMPPConnection xmppConnection )
    {
        _icon.setVisible(false);
        _icon.setEnabled(true);
    }

    @Override
    public void authenticated( XMPPConnection xmppConnection, boolean b )
    {
        _icon.setVisible(false);
        _icon.setEnabled(true);
    }

    @Override
	public void connectionClosed() {
        _icon.setVisible(true);
        _icon.setEnabled(true);
    }

    @Override
	public void connectionClosedOnError(Exception e) {
        _icon.setVisible(true);
        _icon.setEnabled(true);
    }

    @Override
    public void reconnectingIn(int seconds) {
    }

    @Override
    public void reconnectionFailed(Exception e) {
        _icon.setVisible(true);
        _icon.setEnabled(true);
    }
}
