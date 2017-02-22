package org.jivesoftware.spark.otrplug;

import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.otrplug.pref.OTRPreferences;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.preference.Preference;

/**
 * OTR Plugin
 * 
 * @author Bergunde Holger
 */

public class OTRPlugin implements Plugin {

    OTRManager _manager;

    @Override
    public void initialize() {
        // Create OTRManager singleton

        _manager = OTRManager.getInstance();

        // The following will add an Entry into the Spark Preferences Window
        Preference mypreference = new OTRPreferences();
        SparkManager.getPreferenceManager().addPreference(mypreference);

    }

    @Override
    public void shutdown() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean canShutDown() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void uninstall() {
        // TODO Auto-generated method stub

    }

}
