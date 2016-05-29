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
package tic.tac.toe;

import javax.swing.ImageIcon;

/**
 * The Variations of Marks
 * 
 * @author wolf.posdorfer
 * @version 16.06.2011
 */
public enum Mark {
    BLANK (0, "empty.png", "empty.png"),
    X	  (1, "x.png", "x.blue.png"),
    O	  (2, "o.png", "o.blue.png");

    private int value;
    private String icon;
    private String redicon;

    public static Mark valueOf(int x) {
	switch (x) {
	case 0:
	    return BLANK;
	case 1:
	    return X;
	case 2:
	    return O;
	default:
	    return BLANK;
	}
    }

    public ImageIcon getImage() {
	ClassLoader cl = getClass().getClassLoader();
	return new ImageIcon(cl.getResource(icon));

    }
    
    public ImageIcon getRedImage()
    {
	ClassLoader cl = getClass().getClassLoader();
	return new ImageIcon(cl.getResource(redicon));
    }

    public int getValue() {
	return value;
    }

    private Mark(int value, String icon, String redicon) {
	this.value = value;
	this.icon = icon;
	this.redicon = redicon;
    }
}
