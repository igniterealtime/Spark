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
package org.jivesoftware.spark.component.browser;

import java.awt.BorderLayout;
import java.net.MalformedURLException;

import javax.swing.JFrame;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.lobobrowser.gui.ContentEvent;
import org.lobobrowser.gui.ContentListener;
import org.lobobrowser.gui.FramePanel;
import org.lobobrowser.main.PlatformInit;

public class EmbeddedBrowserViewer extends BrowserViewer implements ContentListener {

	private static final long serialVersionUID = -8055149462713514766L;
	private FramePanel browser;
	
	/**
	 * Constructs a new LobobrowserViewer
	 */
	public EmbeddedBrowserViewer() {
		LookAndFeel laf = UIManager.getLookAndFeel();
		try {
			PlatformInit.getInstance().init(false, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		browser = new FramePanel();
		//substance look and feel 
		try {
				UIManager.setLookAndFeel(laf);
			}
			catch (UnsupportedLookAndFeelException e) {
				e.printStackTrace();
			}
		browser.addContentListener(this);
	 }
	
	/**
	 * Implementation of "Back"-button
	 */
	public void goBack() {
		browser.back();
	}

	/**
	 * Initialization of the BrowserViewer
	 */
	public void initializeBrowser() {
		this.setLayout(new BorderLayout());
		this.add(browser, BorderLayout.CENTER);
	}

	/**
	 * Load the given URL
	 */
	public void loadURL(String url) {
		try {
			browser.navigate(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * React to an event by updating the address bar 
	 */
	public void contentSet(ContentEvent event) {
		if (browser == null || browser.getCurrentNavigationEntry() == null) {
            return;
        }
        String url = browser.getCurrentNavigationEntry().getUrl().toExternalForm();
        documentLoaded(url);
	}
	
	public static void main(String[] args) {
		EmbeddedBrowserViewer  viewer = new EmbeddedBrowserViewer();
		viewer.initializeBrowser();
		JFrame frame = new JFrame("Test");

        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(viewer, BorderLayout.CENTER);
		frame.setVisible(true);
	    frame.pack();
        frame.setSize(600, 400);
		viewer.loadURL("http://igniterealtime.org");
	}
}