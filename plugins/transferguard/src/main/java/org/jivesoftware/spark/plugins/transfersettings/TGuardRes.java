package org.jivesoftware.spark.plugins.transfersettings;

import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.jivesoftware.spark.util.log.Log;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * Use for TransferGuard Ressource Internationalization.
 * 
 * @author Tim Jentz
 */
public class TGuardRes {
    private static final PropertyResourceBundle prb = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/transferguard_i18n");
    private static ClassLoader cl = TGuardRes.class.getClassLoader();
    static final Icon TRANSFERGUARD_ICON = new ImageIcon(cl.getResource("/images/transferguard/guard.png"));

    private TGuardRes() {
    }

    static String getString(String propertyName) {
        try {
            return prb.getString(propertyName);
        } catch (Exception e) {
            Log.error(e);
            return propertyName;
        }
    }
}
