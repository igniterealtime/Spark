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
