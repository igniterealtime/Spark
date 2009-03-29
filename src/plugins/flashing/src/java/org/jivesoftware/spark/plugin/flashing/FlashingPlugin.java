package org.jivesoftware.spark.plugin.flashing;

import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.plugin.Plugin;

public class FlashingPlugin implements Plugin {
	
	@Override
	public boolean canShutDown() {
		return true;
	}

	@Override
	public void initialize() {
		FlashingPreference preference = new FlashingPreference();
		SparkManager.getPreferenceManager().addPreference(preference);
		SparkManager.getNativeManager().addNativeHandler(new FlashingHandler());
	}

	@Override
	public void shutdown() {
	}

	@Override
	public void uninstall() {
	}

}
