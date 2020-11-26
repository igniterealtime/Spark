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

import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;

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
    private final JLabel _reconnectionlabel = new JLabel(
	    Res.getString("message.reconnect.attempting"),
	    SparkRes.getImageIcon(SparkRes.BUSY_IMAGE), SwingConstants.CENTER);

    /**
     * creates a new Panel
     * 
     * @param groupName
     */
    public ReconnectPanelSmall(String groupName) {
	super(groupName);
	this.add(_reconnectionlabel);
	this.setIcon(SparkRes.getImageIcon(SparkRes.BUSY_IMAGE));
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

}
