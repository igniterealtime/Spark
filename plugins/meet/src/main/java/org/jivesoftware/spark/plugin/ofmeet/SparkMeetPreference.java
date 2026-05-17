/**
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
package org.jivesoftware.spark.plugin.ofmeet;

import javax.swing.Icon;
import javax.swing.JComponent;

import org.jivesoftware.Spark;
import org.jivesoftware.spark.preference.Preference;
import org.jivesoftware.spark.util.log.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class SparkMeetPreference implements Preference {
	static final String NAMESPACE = "ofmeet";
	private SparkMeetPlugin plugin;
    private MeetingPanel panel;

    private final File pluginSettingsFile = new File(Spark.getSparkUserHome(), "ofmeet.properties");
    public Properties props = new Properties();

	public SparkMeetPreference(SparkMeetPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public void commit() {
        String meetingsBaseUrl = panel.getMeetingsBaseUrl();
        props = new Properties();
        props.setProperty("url", meetingsBaseUrl);
        savePreferences();
        // notify plugin about the change
        plugin.commit(meetingsBaseUrl);
	}

	@Override
	public Object getData() {
		return props;
	}

	@Override
	public String getErrorMessage() {
		return null;
	}

	@Override
	public JComponent getGUI() {
        panel = new MeetingPanel();
		panel.setMeetingsBaseUrl(props.getProperty("url"));
		return panel;
	}

	@Override
	public Icon getIcon() {
        return SparkMeetResource.PLUGIN_ICON;
	}

	@Override
	public String getListName() {
		return SparkMeetResource.getString("name");
	}

	@Override
	public String getNamespace() {
		return NAMESPACE;
	}

	@Override
	public String getTitle() {
		return SparkMeetResource.getString("name");
	}

	@Override
	public String getTooltip() {
		return SparkMeetResource.getString("name");
	}

	@Override
	public boolean isDataValid() {
		return true;
	}

	@Override
	public void load() {
        loadFromFile();
    }

    private void loadFromFile() {
        if (!pluginSettingsFile.exists()) {
            Log.debug("Meet plugin settings file does not exist= " + pluginSettingsFile.getPath() + ", using default");
            return;
        }
        Log.debug("Meet plugin: load settings from " + pluginSettingsFile.getPath());
        try {
            props.load(new FileInputStream(pluginSettingsFile));
        } catch (IOException ioe) {
            Log.error("Unable to load Meet plugin settings:", ioe);
        }
    }

    private void savePreferences() {
        try {
            FileOutputStream outputStream = new FileOutputStream(pluginSettingsFile);
            props.store(outputStream, null);
            outputStream.close();
        } catch (Exception e) {
            Log.error("Meet plugin: unable to save settings", e);
        }
    }
}
