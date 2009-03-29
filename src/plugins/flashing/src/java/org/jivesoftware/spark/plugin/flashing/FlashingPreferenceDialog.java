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

public class FlashingPreferenceDialog extends JPanel {

	private static final long serialVersionUID = -5274539572483246530L;

	private JPanel flashingPanel = new JPanel();

	private JCheckBox flashingEnabled = new JCheckBox();
	private JComboBox flashingType = new JComboBox();
	
	public FlashingPreferenceDialog() {
		flashingPanel.setLayout(new GridBagLayout());
		
		flashingEnabled.setText(FlashingResources.getString("flashing.enable"));
		flashingEnabled.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				updateUI(flashingEnabled.isSelected());	
			}
			
		});
		
		flashingType.addItem(FlashingResources.getString("flashing.type.continuous"));
		flashingType.addItem(FlashingResources.getString("flashing.type.temporary"));
		
		flashingPanel.add(flashingEnabled, new GridBagConstraints(0, 0, 3, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		flashingPanel.add(new JLabel(FlashingResources.getString("flashing.type")), new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		flashingPanel.add(flashingType, new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		
		
		flashingPanel.setBorder(BorderFactory.createTitledBorder(FlashingResources.getString("title.flashing")));
		
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
