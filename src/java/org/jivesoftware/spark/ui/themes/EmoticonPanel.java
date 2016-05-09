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
package org.jivesoftware.spark.ui.themes;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;

/**
 * Class designed to hold emoticons
 * in a N x M GridbagPanel
 * @author wolf.posdorfer
 *
 */
public class EmoticonPanel extends JPanel {

    private static final long serialVersionUID = 4884193790861293275L;
    private int _spalte=0;
    private int _zeile=0;
    private int _numberInRow;
    
    /**
     * Creates a new EmotionPanel
     * 
     * @param numberinrow
     *            how many emoticons should be in one row?
     */
    public EmoticonPanel(int numberinrow) {

	this.setLayout(new GridBagLayout());
	_numberInRow = numberinrow - 1;
    }

    @Override
    public Component add(Component comp) {

	add(comp,new GridBagConstraints(_spalte,_zeile, 1, 1, 0.1, 0.1, 
		GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0,0,0,0), 0, 0));
	
	_spalte++;	
	if(_spalte>_numberInRow)
	{
	    _spalte=0;
	    _zeile++;
	}
	return comp;	
    }
    
    @Override
    public void removeAll() {
        super.removeAll();
        _zeile=0;
        _spalte=0;
    }
    
    public int getNumRows()
    {
	return _zeile;
    }


}
