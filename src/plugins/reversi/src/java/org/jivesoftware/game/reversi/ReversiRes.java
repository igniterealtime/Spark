package org.jivesoftware.game.reversi;

import java.net.URL;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;

public class ReversiRes {
	   	private static PropertyResourceBundle prb;
	   	public static final String REVERSI_ICON = "REVERSI_ICON";
	   	
	    private ReversiRes() {

	    }

	    static ClassLoader cl = ReversiRes.class.getClassLoader();

	    static {
	    	ReversiRes.prb = (PropertyResourceBundle) ResourceBundle.getBundle("reversi");
	    }
	    
	    public static final String getString(String propertyName) {
	        return ReversiRes.prb.getString(propertyName);
	    }

	    public static final ImageIcon getImageIcon(String imageName) {
	        try {
	            final String iconURI = ReversiRes.getString(imageName);
	            final URL imageURL = ReversiRes.cl.getResource(iconURI);
	            return new ImageIcon(imageURL);
	        }
	        catch (Exception ex) {
	            System.out.println(imageName + " not found.");
	        }
	        return null;
	    }

	    public static final URL getURL(String propertyName) {
	        return ReversiRes.cl.getResource(ReversiRes.getString(propertyName));
	    }
	    
}
