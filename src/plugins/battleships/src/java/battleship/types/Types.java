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
package battleship.types;

import javax.swing.ImageIcon;

public enum Types {

    EMPTY("empty.png"),
    MISS("miss.png"), 
    SHIP("ship.png"), 
    SHIPHIT("shiphit.png");    

    public Types getValue(int x) {
	switch (x) {
	case 1:
	    return MISS;
	case 2:
	    return SHIP;
	case 3:
	    return SHIPHIT;
	default:
	    return EMPTY;
	}

    }

    private String _iconme;

    private Types(String iconme) {
	_iconme=iconme;
    }
    
    public ImageIcon getImage()
    {
	ClassLoader cl = getClass().getClassLoader();
	return new ImageIcon(cl.getResource(_iconme));
    }
}
