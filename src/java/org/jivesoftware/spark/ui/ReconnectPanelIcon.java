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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.util.SwingTimerTask;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.TaskEngine;

/**
 * Used for silent reconnecting<br>
 * Displays a little Icon at first position in commandpanel
 * 
 * @author wolf.posdorfer
 * 
 */
public class ReconnectPanelIcon implements ConnectionListener {

    private static final long serialVersionUID = 437696141257704105L;
    private RolloverButton _button;
    private JPanel _commandpanel;
    private boolean _closedOnError;

    /**
     * creates a new Panel
     * 
     * @param groupName
     */
    public ReconnectPanelIcon() {

	_commandpanel = SparkManager.getWorkspace().getCommandPanel();

	_button = new RolloverButton(SparkRes.getImageIcon(SparkRes.BUSY_IMAGE));

	_button.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		startReconnecting();
	    }
	});
    }

    public JPanel getPanel() {
	return _commandpanel;
    }

    public JButton getButton() {
	return _button;
    }

    /**
     * Starts the Timer for Icon-reconnection
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

	SparkManager.getPreferenceManager();

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
		SparkManager.getConnection().connect();
	    } else {
		SparkManager.getMainWindow().logout(false);
	    }
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }

    public void setReconnectText(String text) {
	_button.setToolTipText(text);
    }

    public void remove() {
	SwingWorker worker = new SwingWorker() {
	    @Override
	    public Object construct() {
		return 42;
	    }

	    @Override
	    public void finished() {
		_commandpanel.remove(_button);
	    }
	};
	worker.start();

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
