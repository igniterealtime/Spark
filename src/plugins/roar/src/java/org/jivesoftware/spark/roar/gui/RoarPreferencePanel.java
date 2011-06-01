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
package org.jivesoftware.spark.roar.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jivesoftware.spark.component.VerticalFlowLayout;
import org.jivesoftware.spark.roar.RoarResources;
import org.jivesoftware.spark.roar.displaytype.BottomRight;
import org.jivesoftware.spark.roar.displaytype.SparkToasterHandler;
import org.jivesoftware.spark.roar.displaytype.TopRight;
import org.jivesoftware.spark.util.ColorPick;

/**
 * Super Awesome Preference Panel
 * 
 * @author wolf.posdorfer
 * 
 */
public class RoarPreferencePanel extends JPanel implements ChangeListener {

    private static final long serialVersionUID = -5334936099931215962L;

    private Image _backgroundimage;

    private JTextField _duration;
    private JTextField _amount;
    private JCheckBox _checkbox;
    
    private JList _colorlist;
    private JComboBox _typelist;

    private ColorPick _colorpicker;
    
    private HashMap<ColorTypes,Color> _colormap;

    private String[] _typelistdata;
    

    public RoarPreferencePanel() {
	
	_colormap = new HashMap<ColorTypes, Color>();
	for(ColorTypes e : ColorTypes.values())
	{
	    _colormap.put(e, Color.BLACK );
	}

	JPanel contents = new JPanel();
	contents.setLayout(new GridBagLayout());
	contents.setBackground(new Color(0,0,0,0));
	this.setLayout(new VerticalFlowLayout());
	contents.setBorder(BorderFactory.createTitledBorder(RoarResources.getString("roar.settings")));
	
	
	add(contents);
	ClassLoader cl = getClass().getClassLoader();
	_backgroundimage = new ImageIcon(cl.getResource("background2.png"))
		.getImage();
		
	_colorpicker = new ColorPick(false);
	
	_colorpicker.addChangeListener(this);
	
	_duration = new JTextField();
	_amount = new JTextField();
	
	_checkbox = new JCheckBox(RoarResources.getString("roar.enabled"));
	
	ColorTypes[] colortypesdata = {ColorTypes.BACKGROUNDCOLOR, ColorTypes.HEADERCOLOR, ColorTypes.TEXTCOLOR};
	_colorlist = new JList(colortypesdata);
	
	_typelistdata = new String[3];
	_typelistdata[0] = TopRight.getLocalizedName();
	_typelistdata[1] = BottomRight.getLocalizedName();
	_typelistdata[2] = SparkToasterHandler.getLocalizedName();
	
	_typelist = new JComboBox(_typelistdata);


	Insets in = new Insets(5,5,5,5);

	contents.add(_colorlist, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, in, 0, 0));
	contents.add(_colorpicker, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, in, 0, 0));

	contents.add(new JLabel(RoarResources.getString("roar.duration")), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, in, 0, 0));
	contents.add(_duration, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, in, 0, 0));

	contents.add(new JLabel(RoarResources.getString("roar.amount")), new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, in, 0, 0));
	contents.add(_amount, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, in, 0, 0));

	
	contents.add(_checkbox, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, in, 0, 0));

	
	contents.add(new JLabel(RoarResources.getString("roar.location")), new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, in, 0, 0));

	contents.add(_typelist, new GridBagConstraints(1, 4, 1, 1, 0.8, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, in, 0, 0));


	
	_colorlist.addMouseListener(new MouseAdapter() {
	    @Override
	    public void mouseClicked(MouseEvent e) {
		colorListMouseClicked(e);
	    }
	});
    }

   
    /**
     * returns the Current Backgroundcolor of Popups
     * @return
     */
    public Color getColor() {
	return _colorpicker.getColor();
    }
    
    /**
     * Sets the Background Color for Popups
     * @param c
     */
    public void setBackgroundColor(Color c) {
	_colorpicker.setColor(c);
    }
    
    /**
     * Are Popups enabled
     * @return boolean
     */
    public boolean getShowingPopups() {
	return _checkbox.isSelected();
    }

    /**
     * Set Popups enabled/disabled
     * @param pop
     */
    public void setShowingPopups(boolean pop) {
	_checkbox.setSelected(pop);
    }
    
    /**
     * returns the popup duration
     * @return int
     */
    public int getDuration() {
	try {
	    return Integer.parseInt(_duration.getText());
	} catch (Exception e) {
	    return 3000;
	}
    }
    
    /**
     * Sets Popup duration 
     * @param duration
     */
    public void setDuration(int duration)
    {
	_duration.setText(""+duration);
    }
    
    /**
     * Set the Amount of Windows on Screen
     * @param am
     */
    public void setAmount(int am)
    {
	_amount.setText(""+am);
    }
    
    /**
     * Amount of Windows on Screen
     * @return int
     */
    public int getAmount()
    {
	return Integer.parseInt(_amount.getText());
    }
    
    
    public Color getColor(ColorTypes type)
    {
	return _colormap.get(type);	
    }
    
    public void setColor(ColorTypes type, Color color)
    {
	_colormap.put(type, color);
    }
    
    private void colorListMouseClicked(MouseEvent e) {

	ColorTypes key = (ColorTypes) _colorlist.getSelectedValue();

	_colorpicker.setColor(_colormap.get(key));

    }
    
    public void setDisplayType(String t) {

	if (t.equals(TopRight.getName())) {
	    _typelist.setSelectedItem(TopRight.getLocalizedName());
	}
	else if(t.equals(SparkToasterHandler.getName()))
	{
	    _typelist.setSelectedItem(SparkToasterHandler.getLocalizedName());
	}	
	else {
	    _typelist.setSelectedItem(BottomRight.getLocalizedName());
	}
    }

    public String getDisplayType() {
	String o = (String) _typelist.getSelectedItem();
	if (o.equals(TopRight.getLocalizedName())) 
	{
	    return TopRight.getName();
	} 
	else if (o.equals(SparkToasterHandler.getLocalizedName())) 
	{
	    return SparkToasterHandler.getName();
	}
	else 
	{
	    return BottomRight.getName();
	}

    }
    
    
    // ====================================================================================
    // ====================================================================================
    // ====================================================================================
    public void paintComponent(Graphics g) {
	// CENTER LOGO
	// int imgwi = _backgroundimage.getWidth(null);
	// int imghe = _backgroundimage.getHeight(null);
	// int x = this.getSize().width;
	// x = (x/2)-(imgwi/2) < 0 ? 0 : (x/2)-(imgwi/2) ;
	//
	// int y = this.getSize().height;
	// y = (y/2) -(imghe/2)< 0 ? 0 : y/2-(imghe/2) ;

	
	// LOGO in bottom right corner

	int x = this.getSize().width - _backgroundimage.getWidth(null);
	int y = this.getSize().height - _backgroundimage.getHeight(null);
	
	super.paintComponent(g);
	g.drawImage(_backgroundimage, x, y, this);
    }
    
    
    public enum ColorTypes {
	BACKGROUNDCOLOR (RoarResources.getString("roar.background")),
	HEADERCOLOR (RoarResources.getString("roar.header")),
	TEXTCOLOR (RoarResources.getString("roar.text"));

	private String string;

	private ColorTypes(String c) {
	    string = c;
	}
	
	public String toString()
	{
	    return string;
	}

    }


    @Override
    public void stateChanged(ChangeEvent e) {
	if(e.getSource() instanceof JSlider)
	{
	   _colormap.put( (ColorTypes)_colorlist.getSelectedValue() , _colorpicker.getColor());
	}
    }
     
    
}
