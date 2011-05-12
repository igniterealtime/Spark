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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jivesoftware.spark.component.VerticalFlowLayout;
import org.jivesoftware.spark.util.ResourceUtils;

public class FlashingPreferenceDialog extends JPanel {

	private static final long serialVersionUID = -5274539572483246530L;

	private JPanel flashingPanel;

	private JCheckBox flashingEnabled;
	private JComboBox flashingType;
	
	public FlashingPreferenceDialog() {
		flashingPanel = new JPanel();
		flashingEnabled = new JCheckBox();
		flashingType = new JComboBox();
		JLabel lTyps = new JLabel();
		flashingPanel.setLayout(new GridBagLayout());
		
		flashingEnabled.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				updateUI(flashingEnabled.isSelected());	
			}
			
		});
		
		flashingType.addItem(FlashingResources.getString("flashing.type.continuous"));
		flashingType.addItem(FlashingResources.getString("flashing.type.temporary"));
		
		
		flashingPanel.add(flashingEnabled, new GridBagConstraints(0, 0, 3, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		flashingPanel.add(lTyps, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		flashingPanel.add(flashingType, new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		
		flashingPanel.setBorder(BorderFactory.createTitledBorder(FlashingResources.getString("title.flashing")));
		
		// Setup MNEMORICS
		ResourceUtils.resButton(flashingEnabled, FlashingResources.getString("flashing.enable"));
		ResourceUtils.resLabel(lTyps, flashingType, FlashingResources.getString("flashing.type"));
		
		setLayout(new VerticalFlowLayout());
		add(flashingPanel);
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
    	if (FlashingPreferences.TYPE_CONTINOUS.equals(type)) {
    		flashingType.setSelectedIndex(0);
    	}
    	else if (FlashingPreferences.TYPE_TEMPORARY.equals(type)) {
    		flashingType.setSelectedIndex(1);
    	}
    	else {
    		flashingType.setSelectedIndex(0);
    	}
    }
    
    public String getFlashingType() {
    	if (flashingType.getSelectedIndex() == 0) {
    		return FlashingPreferences.TYPE_CONTINOUS;
    	}
    	else if (flashingType.getSelectedIndex() == 1) {
    		return FlashingPreferences.TYPE_TEMPORARY;
    	}
    	
    	return "continuous";
    }
}
