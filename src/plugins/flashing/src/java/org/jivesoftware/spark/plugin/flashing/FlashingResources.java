package org.jivesoftware.spark.plugin.flashing;

import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.jivesoftware.spark.util.log.Log;

public class FlashingResources {
	private static PropertyResourceBundle prb;
	
	static ClassLoader cl = FlashingResources.class.getClassLoader();

	static {
		prb = (PropertyResourceBundle)ResourceBundle.getBundle("i18n/flashing_i18n");
	}
	
    public static final String getString(String propertyName) {
        try {
            return prb.getString(propertyName);
        }
        catch (Exception e) {
            Log.error(e);
            return propertyName;
        }

    }
}
