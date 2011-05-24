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
package org.jivesoftware.spark.ui.themes;


import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.Set;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jivesoftware.resource.Res;
import org.jivesoftware.spark.component.tabbedPane.SparkTabbedPane;
import org.jivesoftware.spark.util.ColorPick;

/**
 * Handles the Color Preferences in Spark
 * 
 * @author wolf.posdorfer
 * 
 */
public class ColorPreferencePanel extends SparkTabbedPane {

    private static final long serialVersionUID = -3594152276094474130L;

    private JScrollPane _jScrollPane;
    private JList _colorliste;
    private ColorSettings _colorsettings;

    private JLabel _errorlabel;
    
    private ColorPick _colorpick;

    public ColorPreferencePanel() {

	createUI();
    }

    private void createUI() {

	_colorsettings = ColorSettingManager.getColorSettings();
	Set<String> sets  = _colorsettings.getKeys();
	
	
	Vector<String> keys= new Vector<String>();
	for(String s : sets)
	{
	    keys.add(s);
	}	
	sortList(keys);
	
	JPanel rightpanel = new JPanel(new GridBagLayout());
	
	_colorpick = new ColorPick(true);
	
	final JButton savebutton = new JButton(Res.getString("apply"));
	
	final JButton restoreDefaults = new JButton(Res.getString("use.default"));
	
	_errorlabel = new JLabel(" ");
	_errorlabel.setForeground(Color.red);
	
	
	rightpanel.add(_colorpick, new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0,GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

	
	rightpanel.add(savebutton, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0 , GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
	rightpanel.add(restoreDefaults, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0 , GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

	rightpanel.add(_errorlabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0 , GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

	_colorliste = new JList(keys);
	_jScrollPane = new JScrollPane(_colorliste);
	
	setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
	add(_jScrollPane);
	add(rightpanel);
	
	
	_colorliste.addListSelectionListener(new ListSelectionListener() {
		
	    @Override
	    public void valueChanged(ListSelectionEvent e) {
		
		String v = (String) _colorliste.getSelectedValue();
		
		Color c = _colorsettings.getColorFromProperty(v);
		
		_colorpick.setColor(c);
		_errorlabel.setText("");
	    }
	});
	
	savebutton.addActionListener(new ActionListener() {
	    
	    @Override
	    public void actionPerformed(ActionEvent e) {
		savebuttonaction(e);
	    }
	});
	
	restoreDefaults.addActionListener(new ActionListener() {
	    
	    @Override
	    public void actionPerformed(ActionEvent e) {
		ColorSettingManager.restoreDefault();
		_colorsettings = ColorSettingManager.getColorSettings();
	    }
	});

    }
    
    /**
     * Called when the Save-Button is pressed
     * @param e
     */
    private void savebuttonaction(ActionEvent e) {
	try {

	    Color c = _colorpick.getColor();

	    _colorsettings.setColorForProperty(
		    (String) _colorliste.getSelectedValue(), c);

	    UIManager.put((String) _colorliste.getSelectedValue(), c);

	    _errorlabel.setText(Res.getString("lookandfeel.color.saved"));

	} catch (Exception ex) {
	    _errorlabel.setText(Res.getString("title.error"));
	    _errorlabel.revalidate();
	}
    }
    
    /**
     * Sorts the provided List
     * @param set
     */
    private void sortList(Vector<String> set)
    {
	Collections.sort(set);
    }
    
}
