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
package tic.tac.toe.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import tic.tac.toe.Mark;
import tic.tac.toe.Pair;

/**
 * The Gui to the Logical Board
 * 
 * @author wolf.posdorfer
 * @version 16.06.2011
 */
public class GameBoardPanel extends JPanel {

    private static final long serialVersionUID = -178497456422566485L;

    private Image _backgroundimage;

    private JLabel[][] _labels;

    private GamePanel _owner;

    public GameBoardPanel(GamePanel gamepanel) {

	_owner = gamepanel;

	setLayout(new GridLayout(3, 3));

	_labels = new JLabel[3][3];

	for (int x = 0; x < 3; x++) {
	    for (int y = 0; y < 3; y++) {
		JLabel toadd = new JLabel(Mark.BLANK.getImage());
		toadd.setOpaque(false);

		final int xx = x;
		final int yy = y;

		toadd.addMouseListener(new MouseAdapter() {
		    @Override
		    public void mouseClicked(MouseEvent e) {
			if (_owner.myTurn() && _owner.isFree(xx, yy)) {
			    placeMark(_owner.getMyMark(), xx, yy);
			}
		    }

		});

		_labels[x][y] = toadd;

		add(_labels[x][y]);
	    }
	}

	ClassLoader cl = getClass().getClassLoader();
	_backgroundimage = new ImageIcon(cl.getResource("board.png"))
		.getImage();

	setPreferredSize(new Dimension(500, 500));

    }

    /**
     * Places the Mark, and tells the Owner to place the mark on the logical
     * board
     * 
     * @param m
     * @param x
     * @param y
     */
    public void placeMark(Mark m, int x, int y) {

	_labels[x][y].setIcon(m.getImage());

	// Notify the Owner about Change
	_owner.onGameBoardPlaceMark(m, x, y);

	this.invalidate();
	this.repaint();
	this.revalidate();
    }
    
    public void colorizeWinners(Pair[] pairs)
    {
	if(pairs!=null)
	{
	    for(Pair p : pairs)
	    {
		_labels[p.getX()][p.getY()].setIcon(p.getMark().getRedImage());
	    }
	}
	this.repaint();
	this.revalidate();
	
    }

    public void paintComponent(Graphics g) {
	super.paintComponent(g);
	final Image backgroundImage = _backgroundimage;
	double scaleX = getWidth() / (double) backgroundImage.getWidth(null);
	double scaleY = getHeight() / (double) backgroundImage.getHeight(null);
	AffineTransform xform = AffineTransform
		.getScaleInstance(scaleX, scaleY);
	((Graphics2D) g).drawImage(backgroundImage, xform, this);
    }

}
