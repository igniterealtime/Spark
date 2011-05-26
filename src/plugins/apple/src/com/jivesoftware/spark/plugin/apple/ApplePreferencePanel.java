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
package com.jivesoftware.spark.plugin.apple;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jivesoftware.spark.component.VerticalFlowLayout;

public class ApplePreferencePanel extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 5817620627545918431L;

    private JCheckBox _dockbadges;
    private JCheckBox _dockbouncing;
    private JCheckBox _repeatedbouncing;

    public ApplePreferencePanel() {
	setLayout(new VerticalFlowLayout());
	
	JPanel mainpanel = new JPanel(new GridBagLayout());
	add(mainpanel);

	_dockbadges = new JCheckBox("Show Dock Badges");
	_dockbouncing = new JCheckBox("Bouncing Dockicon");
	_repeatedbouncing = new JCheckBox("Dockicon bounces repeatedly");
	
	ClassLoader cl = getClass().getClassLoader();
	ImageIcon badge = new ImageIcon(cl.getResource("images/badge.png"));
	ImageIcon bounce = new ImageIcon(cl.getResource("images/bounce.png"));

	
	mainpanel.add(_dockbadges, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 1, 1));
	mainpanel.add(_dockbouncing, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 1, 1));
	mainpanel.add(_repeatedbouncing, new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 1, 1));

	mainpanel.add(new JLabel(badge), new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 1, 1));
	mainpanel.add(new JLabel(bounce), new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 1, 1));


	_dockbouncing.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent arg0) {
		_repeatedbouncing.setEnabled(_dockbouncing.isSelected());
	    }
	});

    }

    public boolean getdockbadges() {
	return _dockbadges.isSelected();
    }

    public void setdockbadges(boolean dockbadging) {
	_dockbadges.setSelected(dockbadging);
    }

    public boolean getdockbounce() {
	return _dockbouncing.isSelected();
    }

    public void setdockbounce(boolean bouncing) {
	_dockbouncing.setSelected(bouncing);
	_repeatedbouncing.setEnabled(_dockbouncing.isSelected());
    }

    public boolean getrepeatbouncing() {
	return _repeatedbouncing.isSelected();
    }

    public void setrepeatbouncing(boolean repeat) {
	_repeatedbouncing.setSelected(repeat);
    }

}
