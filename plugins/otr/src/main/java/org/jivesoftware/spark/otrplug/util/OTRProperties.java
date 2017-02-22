package org.jivesoftware.spark.otrplug.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import org.jivesoftware.spark.SparkManager;

/**
 * OTRProperties file stuff
 * 
 * @author Bergunde Holger
 * 
 */
public class OTRProperties {
    private Properties props;
    private File configFile;

    private static final Object LOCK = new Object();
    private static OTRProperties instance = null;

    /**
     * returns the Instance of this Properties file
     * 
     * @return
     */
    public static OTRProperties getInstance() {
        synchronized (LOCK) {
            if (instance == null) {
                instance = new OTRProperties();
            }
            return instance;
        }
    }

    private OTRProperties() {
        this.props = new Properties();

        try {
            props.load(new FileInputStream(getConfigFile()));
        } catch (IOException e) {
            // Can't load ConfigFile
        }

    }

    private File getConfigFile() {
        if (configFile == null)
            configFile = new File(SparkManager.getUserDirectory(), "otr.properties");

        return configFile;
    }

    public void save() {
        try {
            props.store(new FileOutputStream(getConfigFile()), "Storing OTRPlugin properties");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean getIsOTREnabled() {
        return getBoolean("isOTREnabeld", true);
    }

    public void setIsOTREnabled(boolean enabled) {
        setBoolean("isOTREnabeld", enabled);
    }

    public boolean getOTRCloseOnDisc() {
        return getBoolean("OTRCloseOnDisc", true);
    }

    public void setOTRCloseOnDisc(boolean enabled) {
        setBoolean("OTRCloseOnDisc", enabled);
    }

    public boolean getOTRCloseOnChatClose() {
        return getBoolean("OTRCloseOnChatClose", false);
    }

    public void setOTRCloseOnChatClose(boolean enabled) {
        setBoolean("OTRCloseOnChatClose", enabled);
    }

    // ===============================================================================
    // ===============================================================================
    // ===============================================================================
    private boolean getBoolean(String property, boolean defaultValue) {
        return Boolean.parseBoolean(props.getProperty(property, Boolean.toString(defaultValue)));
    }

    private void setBoolean(String property, boolean value) {
        props.setProperty(property, Boolean.toString(value));
    }

    private int getInt(String property) {
        return Integer.parseInt(props.getProperty(property, "0"));
    }

    private void setInt(String property, int integer) {
        props.setProperty(property, "" + integer);
    }

    public String getProperty(String property) {
        return props.getProperty(property);
    }

}
