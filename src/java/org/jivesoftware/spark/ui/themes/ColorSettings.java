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

import java.awt.Color;
import java.util.HashMap;
import java.util.Set;

public class ColorSettings {

    private HashMap<String, String> _hashmap;

    public ColorSettings(HashMap<String, String> settingmap) {
	_hashmap = settingmap;
    }
    
    public void setColorForProperty(String propertyname, Color color)
    {
	int r = color.getRed();
	int g = color.getGreen();
	int b = color.getBlue();
	int a = color.getAlpha();
	String c = r + "," + g + "," + b + "," + a;	
	_hashmap.put(propertyname, c );
	
    }

    public Color getColorFromProperty(String propertyname) {

	String s = _hashmap.get(propertyname).replaceAll(" ","");
	s = s.replaceAll("[a-zA-Z]","");

	String[] items = s.split(",");

	Color c = new Color(Integer.parseInt(items[0]),
		Integer.parseInt(items[1]), Integer.parseInt(items[2]),
		Integer.parseInt(items[3]));

	return c;

    }
    
    /**
     * Returns all the PropertyNames
     * @return
     */
    public Set<String> getKeys()
    {
	
	return _hashmap.keySet();
	
    }
}


