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
package com.jivesoftware.spark.plugin.growl;


import info.growl.Growl;
import info.growl.GrowlCallbackListener;
import info.growl.GrowlException;
import info.growl.GrowlUtils;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;

/**
 * Simple Test Class to test Growl behaviour
 * 
 * @author wolf.posdorfer
 * 
 */
public class GrowlTest {

    public static void main(String[] args) throws GrowlException {

	new GrowlTest();
    }

    final String appName = "Eclipse";

    public GrowlTest() throws GrowlException {

	JFrame f = new JFrame("test");
	f.setVisible(true);
	f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	Growl growl = GrowlUtils.getGrowlInstance(appName);
	growl.addNotification(appName, true);

	GrowlCallbackListener listener = clickContext -> System.out.println(clickContext + " was clicked");

	growl.addCallbackListener(listener);
	growl.register();

	Action action = new AbstractAction("CLICK") {
	    private static final long serialVersionUID = -7415179834807796108L;

	    public void actionPerformed(ActionEvent ae) {
		try {
		    Growl gu = GrowlUtils.getGrowlInstance(appName);
		    gu.sendNotification(appName, "title", "body", "callbackcontext");
		} catch (GrowlException ge) {
		    ge.printStackTrace();
		}
	    }
	};

	JButton button = new JButton(action);
	f.setSize(200, 200);

	f.add(button);

    }

}
