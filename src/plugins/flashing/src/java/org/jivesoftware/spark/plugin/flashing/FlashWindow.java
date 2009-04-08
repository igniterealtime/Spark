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
		System.load(PluginManager.PLUGINS_DIRECTORY + File.separator
				+ "flashing" + File.separator + "lib" + File.separator
				+ "FlashWindow.dll");
		// System.loadLibrary("FlashWindow");
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
			final int intertime, final int count) {
		new Thread(new Runnable() {
			public void run() {
				try {
					if (window instanceof JFrame)
					{
						// flash on and off each time
						for (int i = 0; i < count; i++) {
							
							flash(((JFrame) window).getTitle(), true);
							Thread.sleep(intratime);
							flash(((JFrame) window).getTitle(), true);
							Thread.sleep(intertime);
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
							Thread.sleep(1000);
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
		frame.getContentPane().setLayout(new FlowLayout());
		JButton button = new JButton("Temp Flashing");
		frame.getContentPane().add(button);
		final FlashWindow winutil = new FlashWindow();
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				winutil.flash(frame, 750, 1500, 5);
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
