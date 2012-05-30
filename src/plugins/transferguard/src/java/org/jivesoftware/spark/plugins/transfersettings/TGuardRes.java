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
        return prb.getString(propertyName);
    }
    

}