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
package org.jivesoftware.spark.plugin.flashing;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jivesoftware.spark.component.VerticalFlowLayout;
import org.jivesoftware.spark.util.ResourceUtils;

public class FlashingPreferenceDialog extends JPanel {
	private final JCheckBox flashingEnabled = new JCheckBox();
	private final JComboBox<String> flashingType = new JComboBox<>();
    private final JPanel flashingPanel = new JPanel();
    private final JLabel lTyps = new JLabel();

    public FlashingPreferenceDialog() {
        flashingPanel.setLayout(new GridBagLayout());
		
		flashingEnabled.addActionListener( e -> updateUI(flashingEnabled.isSelected()) );
		
		flashingType.addItem(FlashingResources.getString("flashing.type.continuous"));
		flashingType.addItem(FlashingResources.getString("flashing.type.temporary"));
		
		
		flashingPanel.add(flashingEnabled, new GridBagConstraints(0, 0, 3, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		flashingPanel.add(lTyps, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		flashingPanel.add(flashingType, new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		
		flashingPanel.setBorder(BorderFactory.createTitledBorder(FlashingResources.getString("title.flashing")));
		
		ResourceUtils.resButton(flashingEnabled, FlashingResources.getString("flashing.enable"));
		ResourceUtils.resLabel(lTyps, flashingType, FlashingResources.getString("flashing.type"));
		
		setLayout(new VerticalFlowLayout());
		add( flashingPanel );
	}
	
	public void updateUI(boolean enabled) {
		flashingType.setEnabled(enabled);
	}
	
    public void setFlashing(boolean enabled) {
    	flashingEnabled.setSelected(enabled);
    	updateUI(enabled);
    }

    public boolean getFlashing() {
        return flashingEnabled.isSelected();
    }
    
    public void setFlashingType(String type) {
        switch (type) {
            case FlashingPreferences.TYPE_CONTINUOUS:
                flashingType.setSelectedIndex(0);
                break;
            case FlashingPreferences.TYPE_TEMPORARY:
                flashingType.setSelectedIndex(1);
                break;
            default:
                flashingType.setSelectedIndex(0);
                break;
        }
    }
    
    public String getFlashingType() {
        switch (flashingType.getSelectedIndex()) {
            case 0:
                return FlashingPreferences.TYPE_CONTINUOUS;
            case 1:
                return FlashingPreferences.TYPE_TEMPORARY;
            default:
                return FlashingPreferences.TYPE_CONTINUOUS;
        }
    }
}
