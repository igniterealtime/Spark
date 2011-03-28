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
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
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
	
	final JTextField red = new JTextField();
	red.setPreferredSize(new Dimension(80,20));
	JLabel redlabel = new JLabel(Res.getString("lookandfeel.color.red"));
	
	final JTextField green = new JTextField();
	green.setPreferredSize(new Dimension(80,20));
	JLabel greenlabel = new JLabel(Res.getString("lookandfeel.color.green"));
	
	final JTextField blue = new JTextField();
	blue.setPreferredSize(new Dimension(80,20));
	JLabel bluelabel = new JLabel(Res.getString("lookandfeel.color.blue"));
	
	final JTextField alfa = new JTextField();
	alfa.setPreferredSize(new Dimension(80,20));
	JLabel alfalabel = new JLabel(Res.getString("lookandfeel.color.opacity")); //100 = 100%Visible
	
	final JButton savebutton = new JButton(Res.getString("apply"));
	
	final JLabel errorlabel = new JLabel("");
	errorlabel.setForeground(Color.red);
	


	final JLabel colorPreview = new JLabel();
	colorPreview.setBackground(new Color(0,0,0,0));
	colorPreview.setPreferredSize(new Dimension(80,20));
	colorPreview.setMaximumSize(new Dimension(80,20));
	colorPreview.setMinimumSize(new Dimension(80,20));
	
	rightpanel.add(colorPreview, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

	
	rightpanel.add(redlabel , new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0,GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
	rightpanel.add(red, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0,GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
	rightpanel.add(new JLabel("0-255"), new GridBagConstraints(2, 1, 1, 1, 1.0, 0.0,GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
	
	
	rightpanel.add(greenlabel , new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0,GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
	rightpanel.add(green, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0,GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
	rightpanel.add(new JLabel("0-255"), new GridBagConstraints(2, 2, 1, 1, 1.0, 0.0,GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
	
	
	rightpanel.add(bluelabel , new GridBagConstraints(0, 3, 1, 1, 1.0, 0.0,GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
	rightpanel.add(blue, new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0,GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
	rightpanel.add(new JLabel("0-255"), new GridBagConstraints(2, 3, 1, 1, 1.0, 0.0,GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
	
	
	rightpanel.add(alfalabel , new GridBagConstraints(0, 4, 1, 1, 1.0, 0.0,GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
	rightpanel.add(alfa, new GridBagConstraints(1, 4, 1, 1, 1.0, 0.0,GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
	rightpanel.add(new JLabel("0-100%"), new GridBagConstraints(2, 4, 1, 1, 1.0, 0.0,GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
	
	
	rightpanel.add(savebutton, new GridBagConstraints(0, 5, 2, 1, 1.0, 0.0,GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
	rightpanel.add(errorlabel, new GridBagConstraints(0, 6, 2, 1, 0.0, 0.0,GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

	
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

		red.setText(""+c.getRed());
		green.setText(""+c.getGreen());
		blue.setText(""+c.getBlue());
		
		alfa.setText(""+(c.getAlpha()*100/255));
		
		colorPreview.setBackground(new Color(c.getRed(),c.getGreen(),c.getBlue()));
		colorPreview.repaint();
		colorPreview.revalidate();
		errorlabel.setText("");
	    }
	});
	
	savebutton.addActionListener(new ActionListener() {
	    
	    @Override
	    public void actionPerformed(ActionEvent e) {
		
		try{
		    
		    // convert to value between 0 and 255
		final int redvalue =   Math.max(0,Math.min(255,Integer.parseInt(red.getText())));
		final int greenvalue = Math.max(0,Math.min(255,Integer.parseInt(green.getText())));
		final int bluevalue =  Math.max(0,Math.min(255,Integer.parseInt(blue.getText())));
		// convert to percent
		int alfavalue =  Math.max(0,Math.min(100,Integer.parseInt(alfa.getText())));

		alfavalue = alfavalue*255/100;
		
		Color c = new Color(redvalue,greenvalue,bluevalue,alfavalue);	
		_colorsettings.setColorForProperty((String)_colorliste.getSelectedValue(), c);
		
		UIManager.put((String)_colorliste.getSelectedValue(), c);
		
		EventQueue.invokeLater(new Runnable() {
		    
		    @Override
		    public void run() {
			colorPreview.setBackground(new Color(redvalue,greenvalue,bluevalue));
			colorPreview.repaint();
			colorPreview.revalidate();
			
		    }
		});
			
		errorlabel.setText(Res.getString("lookandfeel.color.saved"));
		
		}
		catch(Exception ex)
		{
		    errorlabel.setText(Res.getString("title.error"));
		    errorlabel.revalidate();
		}
	    }
	});

    }
    
    private void sortList(Vector<String> set)
    {
	
	Collections.sort(set);
    }
    
}
