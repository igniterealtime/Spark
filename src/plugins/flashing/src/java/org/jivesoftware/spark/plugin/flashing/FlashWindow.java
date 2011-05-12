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
package org.jivesoftware.spark.plugin.flashing;

import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;

import org.jivesoftware.spark.PluginManager;

public class FlashWindow {
	private HashMap<Window, Thread> flashings = new HashMap<Window, Thread>();

	static {
	    
	boolean is64bit = System.getProperty("sun.arch.data.model").equals("64");
	String arch = "";
	if (is64bit) {
	    arch = "64";
	}
	
	try {
	    System.load(PluginManager.PLUGINS_DIRECTORY + File.separator + "flashing" + File.separator + "lib"
		    + File.separator + "FlashWindow" + arch + ".dll");
	} catch (UnsatisfiedLinkError e) {
	    // So, we are on 64bit using 64bit java and you rather wand a 32bit.dll ?? suuuureeee....
	    if (e.getMessage().contains("Can't load AMD 64-bit .dll on a IA 32-bit platform")) {
		System.load(PluginManager.PLUGINS_DIRECTORY + File.separator + "flashing" + File.separator + "lib"
			+ File.separator + "FlashWindow.dll");
	    } else {
		e.printStackTrace();
	    }
	}
		// System.load("C:\\PATH\FlashWindow"+s+".dll");
	}

	public native void flash(String name, boolean bool);

	/*
	 * @param frame The JFrame to be flashed
	 * @param intratime The amount of time between the on and off states of a
	 * single flash
	 * @param intertime The amount of time between different flashes
	 * @param count The number of times to flash the window
	 */
	public void flash(final Window window, final int intratime,
			final int count) {
		new Thread(new Runnable() {
			public void run() {
				try {
					if (window instanceof JFrame)
					{
						// flash on and off each time
						for (int i = 0; i < count; i++) {
							flash(((JFrame) window).getTitle(), true);
							Thread.sleep(intratime);
						}
						// turn the flash off
						flash(((JFrame) window).getTitle(), false);
					}
				} catch (Exception ex) {
					// System.out.println(ex.getMessage());
				}
			}
		}).start();
	}

	public void startFlashing(final Window window) {
		if (flashings.get(window) == null) {
			Thread t = new Thread() {
				public void run() {
					try {
						while (true) {
							Thread.sleep(1500);
							// System.out.println("Flash Window");
							if (window instanceof JFrame)
								flash(((JFrame) window).getTitle(), true);
						}
					} catch (Exception ex) {
						flash(((JFrame) window).getTitle(), false);
						// System.out.println(ex.getMessage());
					}

				}
			};
			t.start();
			flashings.put(window, t);
		}
	}

	public void stopFlashing(final Window window) {
		if (flashings.get(window) != null) {
			flashings.get(window).interrupt();
			flashings.remove(window);
		}
	}

	public static void main(String[] args) throws Exception {
		final JFrame frame = new JFrame();
		frame.setTitle("Test");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new FlowLayout());
		JButton button = new JButton("Temp Flashing");
		frame.getContentPane().add(button);
		final FlashWindow winutil = new FlashWindow();
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				winutil.flash(frame, 750, 5);
			}
		});

		JButton startButton = new JButton("Start Flashing");
		frame.getContentPane().add(startButton);
		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				winutil.startFlashing(frame);
			}
		});

		JButton stopButton = new JButton("Stop Flashing");
		frame.getContentPane().add(stopButton);
		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				// winutil.flash(frame,750,1500,5);
				winutil.stopFlashing(frame);
			}
		});
		frame.pack();
		frame.setVisible(true);
	}

}
