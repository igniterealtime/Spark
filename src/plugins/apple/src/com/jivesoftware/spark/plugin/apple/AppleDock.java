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
package com.jivesoftware.spark.plugin.apple;

import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import org.jivesoftware.resource.Res;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.spark.PresenceManager;
import org.jivesoftware.spark.SparkManager;
import com.apple.eawt.Application;

/**
 * @author Wolf.Posdorfer
 */
public class AppleDock implements ActionListener {

    public AppleDock() {

	PopupMenu menu = new PopupMenu();

	PopupMenu statusmenu = new PopupMenu(Res.getString("menuitem.status"));

	for (Presence p : PresenceManager.getPresences()) {
	    MenuItem dd = new MenuItem(p.getStatus());
	    dd.addActionListener(this);
	    statusmenu.add(dd);
	}

	menu.add(statusmenu);

	JFrame frame = SparkManager.getMainWindow();
	frame.add(menu);

	// set dock menu
	Application app = new Application();
	app.setDockMenu(menu);

    }

    @Override
    public void actionPerformed(ActionEvent e) {

	Presence presence = null;
	for (Presence p : PresenceManager.getPresences()) {
	    if (p.getStatus().equals(e.getActionCommand())) {
		presence = p;
		break;
	    }
	}

	SparkManager.getSessionManager().changePresence(presence);

    }

}
