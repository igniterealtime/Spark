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


