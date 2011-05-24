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
package org.jivesoftware.spark.util;

import java.awt.Color;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jivesoftware.resource.Res;

/**
 * A simple Colorpicker with sliders
 * @author wolf.posdorfer
 *
 */
public class ColorPick extends JPanel implements ChangeListener {
    
    private static final long serialVersionUID = 2709297435120012839L;
    private JSlider[] _sliderarray;
    private JLabel _preview;


    /**
     * Creates a Colorpicker with initial values 0,0,0,0
     * 
     * @param opacity
     *            , true if Opacity Slider should be visible
     */
    public ColorPick(boolean opacity)
    {
	this.setLayout(new GridBagLayout());
	
	
	
	JLabel red = new JLabel(Res.getString("lookandfeel.color.red"));
	JLabel green = new JLabel(Res.getString("lookandfeel.color.green"));
	JLabel blue = new JLabel(Res.getString("lookandfeel.color.blue"));
	JLabel opaq = new JLabel(Res.getString("lookandfeel.color.opacity"));
	
	JSlider redslider = new JSlider(0,255);
	JSlider greenslider = new JSlider(0,255);
	JSlider blueslider = new JSlider(0,255);
	JSlider opaqslider = new JSlider(0,255);
	
	
	_sliderarray = new JSlider[4];
	_sliderarray[0] = redslider;
	_sliderarray[1] = greenslider;
	_sliderarray[2] = blueslider;
	_sliderarray[3] = opaqslider;
	
	for(JSlider s : _sliderarray)
	{
	    s.addChangeListener(this);
	    s.setMajorTickSpacing(256/3);
	    s.setMinorTickSpacing(0);
	    s.setPaintTicks(true);
	    s.setPaintLabels(true);
	}


	_preview = new JLabel("   ");
	_preview.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
	_preview.setOpaque(true);
	
	this.add(red,new GridBagConstraints(0, 0, 1, 1, 0.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5,5,5,5),0,0));
	this.add(redslider,new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5,5,5,5),0,0));
	
	this.add(green,new GridBagConstraints(0, 1, 1, 1, 0.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5,5,5,5),0,0));
	this.add(greenslider,new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5,5,5,5),0,0));
	
	this.add(blue,new GridBagConstraints(0, 2, 1, 1, 0.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5,5,5,5),0,0));
	this.add(blueslider,new GridBagConstraints(1, 2, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5,5,5,5),0,0));
	
	
	if(opacity)
	{
        	this.add(opaq,new GridBagConstraints(0, 3, 1, 1, 0.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5,5,5,5),0,0));
        	this.add(opaqslider,new GridBagConstraints(1, 3, 1, 1, 1.0, 1.0,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5,5,5,5),0,0));
	}
	
	this.add(_preview,new GridBagConstraints(2, 0, 1, 4, 0.1, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5,5,5,5),0,0));
	
	
    }
    /**
     * Creates a Colorpicker with initialvalues provided by c
     * @param opacity, true if Opacity Slider should be visible
     * @param c, the initial Color
     */
    public ColorPick(boolean opacity, Color c)
    {
	this(opacity);
	for(int i=0; i < 3; i++)
	{
	   float w = c.getColorComponents(new float[3])[i];
	   int x = Math.round(w*255f/1f);
	   _sliderarray[i].setValue(x);  
	}
	
	_sliderarray[3].setValue(c.getAlpha());
	
	this.revalidate();
	
    }
        
    /**
     * Returns the Color of the Current View
     * @return
     */
    public Color getColor() {

	return new Color(_sliderarray[0].getValue(), _sliderarray[1].getValue(), _sliderarray[2].getValue(),
		_sliderarray[3].getValue());
    }
    
    
    /**
     * Sets the Color of this Colorpicker
     * @param c
     */
    public void setColor(Color c) {
	for (int i = 0; i < 3; i++) {
	    float w = c.getColorComponents(new float[3])[i];
	    int x = Math.round(w * 255f / 1f);
	    _sliderarray[i].setValue(x);
	}

	_sliderarray[3].setValue(c.getAlpha());

	this.revalidate();
    }
    
    public void addChangeListener(ChangeListener cl)
    {
	for(JSlider sl : _sliderarray)
	{
	    sl.addChangeListener(cl);
	}
    }


    @Override
    public void stateChanged(ChangeEvent e) {

	_preview.setBackground(new Color(_sliderarray[0].getValue(), _sliderarray[1].getValue(), _sliderarray[2]
		.getValue(), _sliderarray[3].getValue()));

	_preview.setForeground(new Color(_sliderarray[0].getValue(), _sliderarray[1].getValue(), _sliderarray[2]
		.getValue(), _sliderarray[3].getValue()));

	_preview.invalidate();
	_preview.repaint();
	_preview.revalidate();

	Container c = _preview.getParent();
	if (c instanceof JPanel) {
	    ((JPanel) c).repaint();
	    ((JPanel) c).revalidate();
	}
    }

}
