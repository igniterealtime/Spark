package org.jivesoftware.spellchecker;

import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.jivesoftware.spark.util.log.Log;

public class SpellcheckerResource {
	private static PropertyResourceBundle prb;
	
	static ClassLoader cl = SpellcheckerResource.class.getClassLoader();

	static {
		prb = (PropertyResourceBundle)ResourceBundle.getBundle("i18n/spellchecker_i18n");
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
