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
package org.jivesoftware.spark.ui;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.TimerTask;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.util.SwingTimerTask;
import org.jivesoftware.spark.util.TaskEngine;

/**
 * Used for silent reconnecting <br>
 * Displays a reconnection dialog as a ContactGroup at the Top
 * 
 * @author wolf.posdorfer
 * 
 */
public class ReconnectPanelSmall extends ContactGroup implements
	ConnectionListener {

    private static final long serialVersionUID = 437696141257704105L;
    private JLabel _reconnectionlabel = new JLabel(
	    Res.getString("message.reconnect.attempting"),
	    SparkRes.getImageIcon(SparkRes.BUSY_IMAGE), 0);
    private Component thiscomp;
    private boolean _closedOnError;

    /**
     * creates a new Panel
     * 
     * @param groupName
     */
    public ReconnectPanelSmall(String groupName) {
	super(groupName);
	this.add(_reconnectionlabel);
	this.setIcon(SparkRes.getImageIcon(SparkRes.BUSY_IMAGE));
	thiscomp = this;

	_reconnectionlabel.addMouseListener(new MouseListener() {

	    @Override
	    public void mouseReleased(MouseEvent e) {
	    }

	    @Override
	    public void mousePressed(MouseEvent e) {
	    }

	    @Override
	    public void mouseExited(MouseEvent e) {
	    }

	    @Override
	    public void mouseEntered(MouseEvent e) {
	    }

	    @Override
	    public void mouseClicked(MouseEvent e) {

		if (SwingUtilities.isLeftMouseButton(e)) {
		    reconnect();
		} else if (SwingUtilities.isRightMouseButton(e)) {

		    int x = e.getX();
		    int y = e.getY();

		    final JPopupMenu popupmenu = new JPopupMenu();
		    final JMenuItem reconnect = new JMenuItem(Res.getString(
			    "button.reconnect").replace("&", ""));
		    reconnect.setIcon(SparkRes
			    .getImageIcon(SparkRes.SMALL_CHECK));
		    popupmenu.add(reconnect);

		    reconnect.addActionListener( e1 -> reconnect() );

		    popupmenu.show(thiscomp, x, y);
		}
	    }
	});
    }

    /**
     * Starts the Timer for no-panel-reconnection
     */
    public void startReconnecting() {

	if (!SparkManager.getConnection().isConnected()) {
	    TimerTask task = new SwingTimerTask() {
		public void doRun() {
		    reconnect();
		}
	    };
	    TaskEngine.getInstance().schedule(task, 100);
	}
    }

    public void setClosedOnError(boolean onError) {
	_closedOnError = onError;
    }

    /**
     * Reconnect Thread
     */
    private void reconnect() {
	try {
	    if (_closedOnError) {
		((AbstractXMPPConnection) SparkManager.getConnection()).connect();
	    } else {
		SparkManager.getMainWindow().logout(false);
	    }
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }

    public void setReconnectText(String text) {
	String s = "<HTML><BODY>" + text + "</BODY></HTML>";
	_reconnectionlabel.setText(s);
    }

	@Override
	public void connected( XMPPConnection xmppConnection ) {
	}

	@Override
	public void authenticated( XMPPConnection xmppConnection, boolean b ) {
	}

	@Override
    public void connectionClosed() {
    }

    @Override
    public void connectionClosedOnError(Exception e) {
    }

    @Override
    public void reconnectingIn(int seconds) {
    }

    @Override
    public void reconnectionSuccessful() {
    }

    @Override
    public void reconnectionFailed(Exception e) {
    }

}
