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
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JButton;
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

    public RoarPreferencePanel() {

	this.setLayout(new VerticalFlowLayout());
	ClassLoader cl = getClass().getClassLoader();
	_backgroundimage = new ImageIcon(cl.getResource("background.png"))
		.getImage();

	RoarProperties props = new RoarProperties();

	String farbe = props.getProperty(RoarProperties.BACKGROUNDCOLOR);

	_color = new JTextField(farbe);
	_color.setSize(200, 20);
	JButton button = new JButton("activate");
	this.add(button);
	this.add(_color);
    }

    public void paintComponent(Graphics g) {
	g.drawImage(_backgroundimage, 0, 0, this.getWidth(), this.getHeight(),
		this);
    }

    public Color getBackgroundColor() {
	try {
	    String[] arr = _color.getText().split(",");

	    return new Color(Integer.parseInt(arr[0]),
		    Integer.parseInt(arr[1]), Integer.parseInt(arr[2]));
	} catch (Exception e) {
	    return Color.BLACK;
	}
    }
}
