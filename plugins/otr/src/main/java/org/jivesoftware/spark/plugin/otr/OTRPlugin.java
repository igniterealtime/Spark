package org.jivesoftware.spark.plugin.otr;

import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.plugin.otr.pref.OTRPreferences;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.preference.Preference;

/**
 * OTR Plugin
 * 
 * @author Bergunde Holger
 */
public class OTRPlugin implements Plugin {

    @Override
    public void initialize() {
        // Create OTRManager singleton
        OTRManager.getInstance();
        // The following will add an Entry into the Spark Preferences Window
        Preference mypreference = new OTRPreferences();
        SparkManager.getPreferenceManager().addPreference(mypreference);
    }

    @Override
    public void shutdown() {
    }

    @Override
    public boolean canShutDown() {
        return false;
    }

    @Override
    public void uninstall() {
    }

}
