package org.jivesoftware.spark.plugins.transfersettings;

import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;


/**
 * Use for Transferguard Ressource Internationalization.
 * 
 * @author tim.jentz
 */
public class TGuardRes {
    private static PropertyResourceBundle prb;
	
    private TGuardRes() {

    }
    
    static ClassLoader cl = TGuardRes.class.getClassLoader();
    
    static {
        prb = (PropertyResourceBundle) ResourceBundle
        		.getBundle("i18n/transferguard_i18n");
    }
    
    public static final String getString(String propertyName) {
        /* Revert to this code after Spark is moved to Java 11 or newer
        return prb.getString(propertyName);
        */
        return new String(prb.getString(propertyName).getBytes("ISO-8859-1"), "UTF-8");
    }
    

}
