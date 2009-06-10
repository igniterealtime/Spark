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
	}

	@Override
	public void commit() {
		LocalPreferences pref = SettingsManager.getLocalPreferences();
		pref.setAudioDevice(panel.getAudioDevice());
		pref.setVideoDevice(panel.getVideoDevice());
		SettingsManager.saveSettings();
	}
	
	@Override
	public void shutdown() {
		commit();
	}

}
