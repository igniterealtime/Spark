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
package org.jivesoftware.spark.roar;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jivesoftware.spark.component.VerticalFlowLayout;

/**
 * Super Awesome Preference Panel
 * 
 * @author wolf.posdorfer
 * 
 */
public class RoarPreferencePanel extends JPanel {

    private static final long serialVersionUID = -5334936099931215962L;

    private Image _backgroundimage;

    private JTextField _color;
    private JTextField _duration;
    private JTextField _amount;
    private JCheckBox _checkbox;

    public RoarPreferencePanel() {

	JPanel contents = new JPanel();
	contents.setLayout(new GridBagLayout());
	contents.setBackground(new Color(0,0,0,0));
	this.setLayout(new VerticalFlowLayout());
	contents.setBorder(BorderFactory.createTitledBorder("Roar Settings"));
	
	
	add(contents);
	
	ClassLoader cl = getClass().getClassLoader();
	_backgroundimage = new ImageIcon(cl.getResource("background.png"))
		.getImage();
	
	_color = new JTextField();
	_duration = new JTextField();
	_amount = new JTextField();
	
	_checkbox = new JCheckBox("Popups enabled");
	_checkbox.setBackground(new Color(0,0,0,0));

	Insets in = new Insets(5,5,5,5);

	contents.add(new JLabel("Background color:"), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, in, 0, 0));
	contents.add(_color, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, in, 0, 0));

	contents.add(new JLabel("Duration in ms:"), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, in, 0, 0));
	contents.add(_duration, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, in, 0, 0));

	contents.add(new JLabel("Maximum Popups on Screen (0=infinity):"), new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, in, 0, 0));
	contents.add(_amount, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, in, 0, 0));

	
	contents.add(_checkbox, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, in, 0, 0));

    }

    public void paintComponent(Graphics g) {
	
	int imgwi = _backgroundimage.getWidth(null);
	int imghe = _backgroundimage.getHeight(null);
	
	int x = this.getSize().width;
	x = (x/2)-(imgwi/2) < 0 ? 0 : (x/2)-(imgwi/2) ;
	
	int y = this.getSize().height;
	y = (y/2) -(imghe/2)< 0 ? 0 : y/2-(imghe/2) ;

	g.drawImage(_backgroundimage, x, y, this);
    }

    
    
    public Color getBackgroundColor() {
	return RoarProperties.convertString(_color.getText());
    }
    
    public void setBackgroundColor(Color c)
    {
	_color.setText(RoarProperties.convertColor(c));
    }
    
    public boolean getShowingPopups() {
	return _checkbox.isSelected();
    }

    public void setShowingPopups(boolean pop) {
	_checkbox.setSelected(pop);
    }
    
    public int getDuration() {
	try {
	    return Integer.parseInt(_duration.getText());
	} catch (Exception e) {
	    return 3000;
	}
    }
    
    public void setDuration(int duration)
    {
	_duration.setText(""+duration);
    }
    
    public void setAmount(int am)
    {
	_amount.setText(""+am);
    }
    
    public int getAmount()
    {
	return Integer.parseInt(_amount.getText());
    }
    
    
    
    
    
}
