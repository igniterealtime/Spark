package org.jivesoftware.spark.translator;

import org.jivesoftware.Spark;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class TranslatorProperties {

    private final Properties props;

    private File configFile;

    public static final String ACTIVE = "active";

    public static final String USE_CUSTOM_URL = "useCustomUrl";

    public static final String URL = "url";

    private static final Object LOCK = new Object();

    private static TranslatorProperties instance = null;

    /**
     * returns the Instance of this Properties file
     *
     * @return
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
        if (configFile == null)
            configFile = new File(Spark.getSparkUserHome(), "translator.properties");

        return configFile;
    }

    public void save() {
        try {
            props.store(new FileOutputStream(getConfigFile()), "Storing Translator properties");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean getEnabledTranslator(){
        return getBoolean(ACTIVE,false);
    }
    public void setEnabledTranslator(boolean enable){
        setBoolean(ACTIVE,enable);
    }

    public boolean getUseCustomUrl(){
        return getBoolean(USE_CUSTOM_URL, false);
    }

    public void setUseCustomUrl(boolean enable){
        setBoolean(USE_CUSTOM_URL,enable);
    }

    public String getUrl(){
        return props.getProperty(URL);
    }

    public void setUrl(String url){
        props.setProperty(URL,url);
    }


    public boolean getBoolean(String property, boolean defaultValue) {
        return Boolean.parseBoolean(props.getProperty(property, Boolean.toString(defaultValue)));
    }

    public void setBoolean(String property, boolean value) {
        props.setProperty(property, Boolean.toString(value));
    }
}
