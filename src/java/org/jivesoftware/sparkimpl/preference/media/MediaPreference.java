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
package org.jivesoftware.sparkimpl.preference.media;

import javax.swing.Icon;
import javax.swing.JComponent;

import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.spark.preference.Preference;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

public class MediaPreference implements Preference {

	private MediaPreferencePanel panel = new MediaPreferencePanel();
	public static final String NAMESPACE = "http://www.jivesoftware.org/spark/media";
	
	@Override
	public Object getData() {
		return null;
	}

	@Override
	public String getErrorMessage() {
		return null;
	}

	@Override
	public JComponent getGUI() {
		return panel;
	}

	@Override
	public Icon getIcon() {
		return SparkRes.getImageIcon(SparkRes.HEADSET_IMAGE);
	}

	@Override
	public String getListName() {
		return Res.getString("title.general.media");
	}

	@Override
	public String getNamespace() {
		return NAMESPACE;
	}

	@Override
	public String getTitle() {
		return Res.getString("title.general.media");
	}

	@Override
	public String getTooltip() {
		return Res.getString("title.general.media");
	}

	@Override
	public boolean isDataValid() {
		return true;
	}

	@Override
	public void load() {
		 LocalPreferences localPreferences = SettingsManager.getLocalPreferences();
		 panel.setVideoDevice(localPreferences.getVideoDevice());
		 panel.setAudioDevice(localPreferences.getAudioDevice());
		 panel.setStunServer(localPreferences.getStunFallbackHost());
		 panel.setStunPort(localPreferences.getStunFallbackPort());
	}

	@Override
	public void commit() {
		LocalPreferences pref = SettingsManager.getLocalPreferences();
		pref.setAudioDevice(panel.getAudioDevice());
		pref.setVideoDevice(panel.getVideoDevice());
		pref.setStunFallbackHost(panel.getStunServer());
		pref.setStunFallbackPort(panel.getStunPort());
		SettingsManager.saveSettings();
	}
	
	@Override
	public void shutdown() {
		commit();
	}

}
