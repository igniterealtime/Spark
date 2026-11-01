package org.jivesoftware.spark.translator;

import org.jivesoftware.Spark;
import org.jivesoftware.spark.util.log.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static space.dynomake.libretranslate.ApiProviders.API_URL_LIBRETRANSLATE;

public class TranslatorProperties {
    private final Properties props;
    private static final Object LOCK = new Object();
    private static TranslatorProperties instance = null;

    public static TranslatorProperties getInstance() {
        if (instance != null) {
            return instance;
        }
        synchronized (LOCK) {
            instance = new TranslatorProperties();
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
            props.store(new FileOutputStream(getConfigFile()), null);
        } catch (Exception e) {
            Log.error("Unable to store Translator properties", e);
        }
    }

    public boolean isEnabledTranslator() {
        boolean active = getBoolean("active", false);

        return active;
    }

    public void setEnabledTranslator(boolean enable) {
        setBoolean("active", enable);
    }

    public boolean isUseCustomUrl() {
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

    public boolean isApiAccessible() {
        if (!isEnabledTranslator()) {
            return false;
        }
        // The LibreTranslate API now requires an API key
        if (API_URL_LIBRETRANSLATE.equals(getUrl())) {
            return !isBlank(getApiKey());
        }
        return true;
    }

    private boolean getBoolean(String property, boolean defaultValue) {
        String propertyVal = props.getProperty(property);
        return propertyVal != null ? Boolean.parseBoolean(propertyVal) : defaultValue;
    }

    public void setBoolean(String property, boolean value) {
        props.setProperty(property, Boolean.toString(value));
    }
}
