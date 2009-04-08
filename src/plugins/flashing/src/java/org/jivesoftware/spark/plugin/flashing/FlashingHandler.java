package org.jivesoftware.spark.plugin.flashing;

import java.awt.Window;

import javax.swing.JFrame;

import org.jivesoftware.spark.NativeHandler;
import org.jivesoftware.spark.SparkManager;

public class FlashingHandler implements NativeHandler {
	private FlashWindow flasher;

	public FlashingHandler() {
		flasher = new FlashWindow();
	}

	@Override
	public void flashWindow(Window window) {
		FlashingPreference preference = (FlashingPreference) SparkManager.getPreferenceManager().getPreference(FlashingPreference.NAMESPACE);

		if (preference.getPreferences().isFlashingEnabled()) {
			if (preference.getPreferences().getFlashingType().equals(FlashingPreferences.TYPE_CONTINOUS)) {
				flasher.startFlashing(window);
			}
			else if (preference.getPreferences().getFlashingType().equals(FlashingPreferences.TYPE_TEMPORARY)) {
				flasher.flash(window, 750, 1500, 5);
			}
		}
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
		flasher.stopFlashing((JFrame) window);
	}

}
