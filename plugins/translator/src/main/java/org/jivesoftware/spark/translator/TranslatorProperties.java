package org.jivesoftware.spark.translator;

import org.jivesoftware.Spark;
import org.jivesoftware.spark.util.log.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class TranslatorProperties {
    private final Properties props;
    private static final Object LOCK = new Object();
    private static TranslatorProperties instance = null;

    /**
     * returns the Instance of this Properties file
     */
    public static TranslatorProperties getInstance() {
        synchronized (LOCK) {
            if (instance == null) {
                instance = new TranslatorProperties();
            }
            return instance;
        }
    }

    private TranslatorProperties() {
        this.props = new Properties();

        try {
            props.load(new FileInputStream(getConfigFile()));
        } catch (IOException e) {
            // Can't load ConfigFile
        }
    }

    private File getConfigFile() {
        File configFile = new File(Spark.getSparkUserHome(), "translator.properties");
        return configFile;
    }

    public void save() {
        try {
            props.store(new FileOutputStream(getConfigFile()), "Storing Translator properties");
        } catch (Exception e) {
            Log.error(e);
        }
    }

    public boolean getEnabledTranslator() {
        return getBoolean("active", false);
    }

    public void setEnabledTranslator(boolean enable) {
        setBoolean("active", enable);
    }

    public boolean getUseCustomUrl() {
        return getBoolean("useCustomUrl", false);
    }

    public void setUseCustomUrl(boolean enable) {
        setBoolean("useCustomUrl", enable);
    }

    public String getUrl() {
        return props.getProperty("url");
    }

    public void setUrl(String url) {
        props.setProperty("url", url);
    }

    public String getApiKey() {
        return props.getProperty("apiKey");
    }

    public void setApiKey(String apiKey) {
        props.setProperty("apiKey", apiKey);
    }

    public String getMyLanguage() {
        return props.getProperty("myLanguage");
    }

    public void setMyLanguage(String language) {
        props.setProperty("myLanguage", language);
    }

    private boolean getBoolean(String property, boolean defaultValue) {
        return Boolean.parseBoolean(props.getProperty(property, Boolean.toString(defaultValue)));
    }

    public void setBoolean(String property, boolean value) {
        props.setProperty(property, Boolean.toString(value));
    }
}
