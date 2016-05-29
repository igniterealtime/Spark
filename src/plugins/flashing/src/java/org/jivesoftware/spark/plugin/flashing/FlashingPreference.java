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

import java.awt.EventQueue;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;

import org.jivesoftware.spark.preference.Preference;
import org.jivesoftware.spark.util.log.Log;

public class FlashingPreference implements Preference {
	public static String NAMESPACE = "flashing";
	private FlashingPreferenceDialog dialog;
	private FlashingPreferences preferences;

	public FlashingPreference() {
	   	preferences = new FlashingPreferences();
	   	
		try {
            if (EventQueue.isDispatchThread()) {
                dialog = new FlashingPreferenceDialog();
            } else {
                EventQueue.invokeAndWait( () -> dialog = new FlashingPreferenceDialog() );
            }
		} catch (Exception e) {
            Log.error(e);
		}		
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
