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

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * The Panel to be displayed on Winning/Losing/Tie
 * @author wolf.posdorfer
 * @version 16.06.2011
 */
public class GameEndsUI extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = -5947922803585454129L;
    
    
    public GameEndsUI(String text, Color c)
    {
	
	JLabel label = new JLabel(text);
	label.setFont(new Font("Dialog", Font.BOLD, 32));
	label.setForeground(c);
	add(label);
	
    }

}
