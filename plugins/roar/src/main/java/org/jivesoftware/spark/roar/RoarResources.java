package org.jivesoftware.spark.roar;

import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.jivesoftware.resource.UTF8Control;
import org.jivesoftware.spark.util.log.Log;

public class RoarResources {

    private static final PropertyResourceBundle prb;

    static {
	prb = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/roar_i18n", new UTF8Control());
    }

    public static String getString(String propertyName) {
	try {
        return prb.getString(propertyName);
	} catch (Exception e) {
	    Log.error(e);
	    return propertyName;
	}
    }

}
