package org.jivesoftware.spark.plugins.transfersettings;

import java.nio.charset.StandardCharsets;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import org.jivesoftware.spark.util.log.Log;

/**
 * Use for Transferguard Ressource Internationalization.
 * 
 * @author tim.jentz
 */
public class TGuardRes {
    private static final PropertyResourceBundle prb;
	
    private TGuardRes() {

    }

    static {
        prb = (PropertyResourceBundle) ResourceBundle
        		.getBundle("i18n/transferguard_i18n");
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
