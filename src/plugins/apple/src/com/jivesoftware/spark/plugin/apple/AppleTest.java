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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.spark.PresenceManager;

public class AppleTest implements ActionListener {

    public static void main(String[] args) throws InterruptedException {
	new AppleTest();
    }

    public AppleTest() throws InterruptedException {

	for (LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels()) {
	    System.out.println(laf.getClassName() + " " + laf.getName());
	}

	System.out.println(UIManager.getLookAndFeel().getName());
	//	
	// JFrame f = new JFrame("Test");
	// JButton b = new JButton("B");
	// f.add(b);
	// f.setVisible(true);
	//	
	// Application app = new Application();
	// System.out.println("go to background now");
	// Thread.sleep(1500);
	//	
	// System.out.println("request attention");
	// app.requestUserAttention(false);
	//	
	// Thread.sleep(3000);
	// System.out.println("bounce again");
	//	
	// app.requestUserAttention(false);
	//    

    }

    //
    // MenuItem online = new MenuItem(Res.getString("status.online"));
    // online.addActionListener(this);
    //
    // MenuItem away = new MenuItem(Res.getString("status.away"));
    // away.addActionListener(this);
    //
    // MenuItem extaway = new MenuItem(Res.getString("status.extended.away"));
    // extaway.addActionListener(this);
    // MenuItem dnd = new MenuItem(Res.getString("status.do.not.disturb"));
    // dnd.addActionListener(this);
    // MenuItem onpho = new MenuItem(Res.getString("status.on.phone"));
    // onpho.addActionListener(this);
    // MenuItem ftc = new MenuItem(Res.getString("status.free.to.chat"));
    // ftc.addActionListener(this);
    // statusmenu.add(online);
    // statusmenu.add(away);
    // statusmenu.add(extaway);
    // statusmenu.add(dnd);
    // statusmenu.add(onpho);
    // statusmenu.add(ftc);

    // menu.add(statusmenu);
    //
    // JFrame frame = new JFrame();
    // frame.setVisible(true);
    // frame.add(menu);
    //
    // // set dock menu
    // app.setDockMenu(menu);
    // }

    @Override
    public void actionPerformed(ActionEvent e) {

	System.out.println(e.getActionCommand());

	Presence presence = null;
	for (Presence p : PresenceManager.getPresences()) {
	    if (p.getStatus().equals(e.getActionCommand())) {
		presence = p;
		break;
	    }
	}

	System.out.println(presence);

    }

}
