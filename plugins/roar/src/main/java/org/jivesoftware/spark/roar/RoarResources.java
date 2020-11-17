package org.jivesoftware.spark.roar;

import java.nio.charset.StandardCharsets;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.jivesoftware.spark.util.log.Log;

public class RoarResources {

    private static final PropertyResourceBundle prb;

    static {
	prb = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/roar_i18n");
    }

    public static String getString(String propertyName) {
	try {
        /* Revert to this code after Spark is moved to Java 11 or newer
        return prb.getString(propertyName);
        */
	    return new String(prb.getString(propertyName).getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
	} catch (Exception e) {
	    Log.error(e);
	    return propertyName;
	}
    }

}
