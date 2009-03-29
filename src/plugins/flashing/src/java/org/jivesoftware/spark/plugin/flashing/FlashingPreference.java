package org.jivesoftware.spark.plugin.flashing;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;

import org.jivesoftware.spark.preference.Preference;

public class FlashingPreference implements Preference {
	public static String NAMESPACE = "flashing";
	private FlashingPreferenceDialog dialog;
	private FlashingPreferences preferences;

	public FlashingPreference() {
		dialog = new FlashingPreferenceDialog();
		preferences = new FlashingPreferences();
	}

	public FlashingPreferences getPreferences() {
		return preferences;
	}

	@Override
	public void commit() {
		preferences.setFlashingEnabled(dialog.getFlashing());
		preferences.setFlashingType(dialog.getFlashingType());
		preferences.save();
	}

	@Override
	public Object getData() {
		return preferences;
	}

	@Override
	public String getErrorMessage() {
		return null;
	}

	@Override
	public JComponent getGUI() {
		return dialog;
	}

	@Override
	public Icon getIcon() {
		ClassLoader cl = getClass().getClassLoader();
		return new ImageIcon(cl.getResource("lightning16.png"));
	}

	@Override
	public String getListName() {
		return FlashingResources.getString("title.flashing");
	}

	@Override
	public String getNamespace() {
		return NAMESPACE;
	}

	@Override
	public String getTitle() {
		return FlashingResources.getString("title.flashing");
	}

	@Override
	public String getTooltip() {
		return FlashingResources.getString("title.flashing");
	}

	@Override
	public boolean isDataValid() {
		return true;
	}

	@Override
	public void load() {
		dialog.setFlashing(preferences.isFlashingEnabled());
		dialog.setFlashingType(preferences.getFlashingType());
	}

	@Override
	public void shutdown() {

	}

}
