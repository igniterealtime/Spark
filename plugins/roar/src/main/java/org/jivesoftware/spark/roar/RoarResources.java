package org.jivesoftware.spark.roar;

import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.jivesoftware.spark.util.log.Log;

public class RoarResources {

    private static PropertyResourceBundle prb;

    static ClassLoader cl = RoarResources.class.getClassLoader();

    static {
	prb = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/roar_i18n");
    }

    public static final String getString(String propertyName) {
	try {
	    return prb.getString(propertyName);
	} catch (Exception e) {
	    Log.error(e);
	    return propertyName;
	}
    }

}
