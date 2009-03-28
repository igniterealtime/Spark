package org.jivesoftware.spark.plugin.flashing;

import java.awt.Window;

import javax.swing.JFrame;

import org.jivesoftware.spark.NativeHandler;

public class FlashingHandler implements NativeHandler
{
	private FlashWindow flasher;
	
	public FlashingHandler() {
		flasher = new FlashWindow();
	}

	@Override
	public void flashWindow(Window window) {
		flasher.startFlashing(window);
	}

	@Override
	public void flashWindowStopWhenFocused(Window window) {
		flasher.stopFlashing(window);
	}

	@Override
	public boolean handleNotification() {
		return true;
	}

	@Override
	public void stopFlashing(Window window) {
		flasher.stopFlashing((JFrame)window);
	}

}
