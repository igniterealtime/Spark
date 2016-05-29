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
package battleship.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.geom.AffineTransform;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import battleship.listener.ShipPlacementListener;
import battleship.types.Types;

public class GameboardGUI extends JPanel{
    private static final long serialVersionUID = -6429298293488133059L;
    
    private Image _bg;
    
    private JLabel[][] _labels;
    
    public GameboardGUI()
    {
	ClassLoader cl = getClass().getClassLoader();
	_bg = new ImageIcon(cl.getResource("water.png")).getImage();
	
	
	setLayout(new GridLayout(10,10));
	
	_labels = new JLabel[10][10];
	
	for(int x =0 ; x<10;x++)
	{
	    for(int y=0 ;y < 10; y++)
	    {
	        _labels[x][y] = new JLabel("empty");
	        _labels[x][y].setBorder(BorderFactory.createLineBorder(Color.lightGray));
                add(_labels[x][y]);
	    }
	}
	
	this.setPreferredSize(new Dimension(400,400));
	
    }
    
    public void setField(int x, int y, Types t)
    {
        System.out.println("setting field"+x+","+y);
	_labels[x][y].setIcon(t.getImage());
	repaint();
	revalidate();
    }
    
    

    public void paintComponent(Graphics g) {
	super.paintComponent(g);
	final Image backgroundImage = _bg;
	double scaleX = getWidth() / (double) backgroundImage.getWidth(null);
	double scaleY = getHeight() / (double) backgroundImage.getHeight(null);
	AffineTransform xform = AffineTransform
		.getScaleInstance(scaleX, scaleY);
	((Graphics2D) g).drawImage(backgroundImage, xform, this);
    }
    
    
    public void initiateShipPlacement(ShipPlacementListener spl)
    {
        for(int x =0 ; x<10;x++)
        {
            for(int y=0 ;y < 10; y++)
            {
               _labels[x][y].addMouseListener(spl);
            }
        }
        
    }
    
    public JLabel[][] getLabels()
    {
        return _labels;
    }
    
  

}
