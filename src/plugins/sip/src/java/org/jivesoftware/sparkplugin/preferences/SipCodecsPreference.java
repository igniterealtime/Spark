package org.jivesoftware.sparkplugin.preferences;

import javax.swing.Icon;
import javax.swing.JComponent;

import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.spark.plugin.phone.resource.PhoneRes;
import org.jivesoftware.spark.preference.Preference;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

public class SipCodecsPreference implements Preference {
    private SipCodecs panel = new SipCodecs();
    public static final String NAMESPACE = "http://www.jivesoftware.org/spark/codecs";

    @Override
    public void commit() {
	LocalPreferences pref = SettingsManager.getLocalPreferences();
	pref.setSelectedCodecs(panel.getSelected());
	pref.setAvailableCodecs(panel.getAvailable());

	SettingsManager.saveSettings();
    }

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
	return SparkRes.getImageIcon(SparkRes.DIAL_PHONE_IMAGE_24x24);
    }

    @Override
    public String getListName() {
	return PhoneRes.getIString("title.sip.codecs.title");
    }

    @Override
    public String getNamespace() {
	return NAMESPACE;
    }

    @Override
    public String getTitle() {
	return PhoneRes.getIString("title.sip.codecs.title");
    }

    @Override
    public String getTooltip() {
	return PhoneRes.getIString("title.sip.codecs.title");
    }

    @Override
    public boolean isDataValid() {
	return true;
    }

    @Override
    public void load() {
	SwingWorker thread = new SwingWorker() {
	    LocalPreferences localPreferences;

	    public Object construct() {
		localPreferences = SettingsManager.getLocalPreferences();
		return localPreferences;
	    }

	    public void finished() {
		String sel = localPreferences.getSelectedCodecs();
		String avail = localPreferences.getAvailableCodecs();
		panel.setAvailable(avail);
		panel.setSelected(sel);
	    }
	};

	thread.start();
    }

    @Override
    public void shutdown() {
	commit();
    }

}
