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

import javax.swing.*;

import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.util.SwingWorker;

/**
 * Used for silent reconnecting<br>
 * Displays a little Icon at first position in commandpanel
 * 
 * @author wolf.posdorfer
 * 
 */
public class ReconnectPanelIcon implements ConnectionListener {

    private static final long serialVersionUID = 437696141257704105L;
    private JLabel _icon;
    private JPanel _commandpanel;
    private boolean _closedOnError;

    /**
     * creates a new Panel
     */
    public ReconnectPanelIcon() {

	_commandpanel = SparkManager.getWorkspace().getCommandPanel();

	_icon = new JLabel(SparkRes.getImageIcon(SparkRes.BUSY_IMAGE));

	}

    public JPanel getPanel() {
	return _commandpanel;
    }

    public JLabel getButton() {
	return _icon;
    }


    public void setClosedOnError(boolean onError) {
	_closedOnError = onError;
    }

    public void setReconnectText(String text) {
	_icon.setToolTipText(text);
    }

    public void remove() {
	SwingWorker worker = new SwingWorker() {
	    @Override
	    public Object construct() {
		return 42;
	    }

	    @Override
	    public void finished() {
		_commandpanel.remove(_icon);
	    }
	};
	worker.start();

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
