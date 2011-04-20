/**
 * $RCSfile: ,v $
 * $Revision: $
 * $Date: $
 * 
 * Copyright (C) 2004-2010 Jive Software. All rights reserved.
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
import java.awt.Dimension;
import java.awt.EventQueue;
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
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jivesoftware.resource.Res;
import org.jivesoftware.spark.component.tabbedPane.SparkTabbedPane;

public class ColorPreferencePanel extends SparkTabbedPane {

    private static final long serialVersionUID = -3594152276094474130L;

    private JScrollPane _jScrollPane;
    private JList _colorliste;
    private ColorSettings _colorsettings;

    private JLabel _colorPreview;
    private JLabel _errorlabel;
    private JTextField _red;
    private JTextField _green;
    private JTextField _blue;
    private JTextField _alfa;

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
	
	_red = new JTextField();
	_red.setPreferredSize(new Dimension(80,20));
	JLabel redlabel = new JLabel(Res.getString("lookandfeel.color.red"));
	
	_green = new JTextField();
	_green.setPreferredSize(new Dimension(80,20));
	JLabel greenlabel = new JLabel(Res.getString("lookandfeel.color.green"));
	
	_blue = new JTextField();
	_blue.setPreferredSize(new Dimension(80,20));
	JLabel bluelabel = new JLabel(Res.getString("lookandfeel.color.blue"));
	
	_alfa = new JTextField();
	_alfa.setPreferredSize(new Dimension(80,20));
	JLabel alfalabel = new JLabel(Res.getString("lookandfeel.color.opacity")); //100 = 100%Visible
	
	final JButton savebutton = new JButton(Res.getString("apply"));
	
	final JButton restoreDefaults = new JButton(Res.getString("use.default"));
	
	_errorlabel = new JLabel("");
	_errorlabel.setForeground(Color.red);
	


	_colorPreview = new JLabel();
	_colorPreview.setBackground(new Color(0,0,0,0));
	_colorPreview.setPreferredSize(new Dimension(80,20));
	_colorPreview.setMaximumSize(new Dimension(80,20));
	_colorPreview.setMinimumSize(new Dimension(80,20));
	
	rightpanel.add(_colorPreview, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

	
	rightpanel.add(redlabel , new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0,GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
	rightpanel.add(_red, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0,GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
	rightpanel.add(new JLabel("0-255"), new GridBagConstraints(2, 1, 1, 1, 1.0, 0.0,GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
	
	
	rightpanel.add(greenlabel , new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0,GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
	rightpanel.add(_green, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0,GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
	rightpanel.add(new JLabel("0-255"), new GridBagConstraints(2, 2, 1, 1, 1.0, 0.0,GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
	
	
	rightpanel.add(bluelabel , new GridBagConstraints(0, 3, 1, 1, 1.0, 0.0,GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
	rightpanel.add(_blue, new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0,GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
	rightpanel.add(new JLabel("0-255"), new GridBagConstraints(2, 3, 1, 1, 1.0, 0.0,GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
	
	
	rightpanel.add(alfalabel , new GridBagConstraints(0, 4, 1, 1, 1.0, 0.0,GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
	rightpanel.add(_alfa, new GridBagConstraints(1, 4, 1, 1, 1.0, 0.0,GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
	rightpanel.add(new JLabel("0-100%"), new GridBagConstraints(2, 4, 1, 1, 1.0, 0.0,GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
	
	
	rightpanel.add(savebutton, new GridBagConstraints(0, 5, 2, 1, 1.0, 0.0,GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
	rightpanel.add(_errorlabel, new GridBagConstraints(0, 6, 2, 1, 0.0, 0.0,GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

	rightpanel.add(restoreDefaults, new GridBagConstraints(0, 9, 2, 1, 1.0, 0.0,GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
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

		_red.setText(""+c.getRed());
		_green.setText(""+c.getGreen());
		_blue.setText(""+c.getBlue());
		
		_alfa.setText(""+(c.getAlpha()*100/255));
		
		_colorPreview.setBackground(new Color(c.getRed(),c.getGreen(),c.getBlue()));
		_colorPreview.repaint();
		_colorPreview.revalidate();
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
    private void savebuttonaction(ActionEvent e)
    {
	try{
	    
	    // convert to value between 0 and 255
	final int redvalue =   Math.max(0,Math.min(255,Integer.parseInt(_red.getText())));
	final int greenvalue = Math.max(0,Math.min(255,Integer.parseInt(_green.getText())));
	final int bluevalue =  Math.max(0,Math.min(255,Integer.parseInt(_blue.getText())));
	// convert to percent

	int alfavalue =  Math.max(0,Math.min(100,Integer.parseInt(_alfa.getText())));

	alfavalue = alfavalue*255/100;
	
	Color c = new Color(redvalue,greenvalue,bluevalue,alfavalue);	
	_colorsettings.setColorForProperty((String)_colorliste.getSelectedValue(), c);
	
	UIManager.put((String)_colorliste.getSelectedValue(), c);
	
	EventQueue.invokeLater(new Runnable() {
	    
	    @Override
	    public void run() {
		_colorPreview.setBackground(new Color(redvalue,greenvalue,bluevalue));
		_colorPreview.repaint();
		_colorPreview.revalidate();
		
	    }
	});
		
	_errorlabel.setText(Res.getString("lookandfeel.color.saved"));
	
	}
	catch(Exception ex)
	{
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
