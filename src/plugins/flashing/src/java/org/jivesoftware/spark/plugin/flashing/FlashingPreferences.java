package org.jivesoftware.spark.plugin.flashing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.jivesoftware.Spark;

public class FlashingPreferences {
	public static final String TYPE_CONTINOUS = "continuous";
	public static final String TYPE_TEMPORARY = "temporary";

	private Properties props;
	private File configFile;

	public FlashingPreferences() {
		this.props = new Properties();

		try {
			props.load(new FileInputStream(getConfigFile()));
		} catch (IOException e) {
			// Can't load ConfigFile
		}

	}

	public File getConfigFile() {
		if (configFile == null)
			configFile = new File(Spark.getSparkUserHome(),
					"flashing.properties");

		return configFile;
	}

	public void save() {
		try {
			props.store(new FileOutputStream(getConfigFile()), "");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isFlashingEnabled() {
		return getBoolean("flashingEnabled", true);
	}
	
	public void setFlashingEnabled(boolean enabled) {
		setBoolean("flashingEnabled", enabled);
	}

	public String getFlashingType() {
		return props.getProperty("flashingType", TYPE_CONTINOUS);
	}

	public void setFlashingType(String type) {
		props.setProperty("flashingType", type);
	}

	private boolean getBoolean(String property, boolean defaultValue) {
		return Boolean.parseBoolean(props.getProperty(property, Boolean
				.toString(defaultValue)));
	}

	private void setBoolean(String property, boolean value) {
		props.setProperty(property, Boolean.toString(value));
	}
}
