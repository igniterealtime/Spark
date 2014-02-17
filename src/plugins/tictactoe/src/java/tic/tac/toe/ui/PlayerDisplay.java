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

import java.awt.FlowLayout;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jivesoftware.smack.util.StringUtils;

import tic.tac.toe.Mark;
import tic.tac.toe.TTTRes;

/**
 * A display for showing, which Mark is yours, your oppononents and whos turn it
 * is<br>
 * 
 * <pre>
 * you: X | max.max: O | current: O
 * </pre>
 * 
 * @author wolf.posdorfer
 * @version 16.06.2011
 * 
 */
public class PlayerDisplay extends JPanel {

    private static final long serialVersionUID = -8025502708415186558L;

    private JLabel _currentplayer;
    private Mark _currentMark;

    public PlayerDisplay(Mark myself, String opponent) {

	_currentplayer = new JLabel(" | "+TTTRes.getString("ttt.display.current"));
	
	_currentplayer.setHorizontalTextPosition(JLabel.LEFT);
	
	setCurrentPlayer(Mark.X);
	setLayout(new FlowLayout(FlowLayout.CENTER));

	JLabel mylabel = new JLabel(TTTRes.getString("ttt.display.me"));
	mylabel.setIcon(new ImageIcon(myself.getImage().getImage()
		.getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
	mylabel.setHorizontalTextPosition(JLabel.LEFT);

	Mark you;
	if (myself == Mark.X)
	    you = Mark.O;
	else
	    you = Mark.X;
	
	String name = StringUtils.parseName(opponent);
	JLabel yourlabel = new JLabel(" | "+name);
	yourlabel.setIcon(new ImageIcon(you.getImage().getImage()
		.getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
	yourlabel.setHorizontalTextPosition(JLabel.LEFT);

	add(mylabel);
	add(yourlabel);
	add(_currentplayer);
    }

    public void setCurrentPlayer(Mark m) {
	_currentMark = m;

	ImageIcon img = new ImageIcon(m.getImage().getImage()
		.getScaledInstance(16, 16, Image.SCALE_SMOOTH));
	
	_currentplayer.setIcon(img);
	_currentplayer.repaint();
	_currentplayer.revalidate();
    }

    public Mark getCurrentPlayer() {
	return _currentMark;
    }
}
