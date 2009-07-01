package org.jivesoftware.spellchecker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

import org.jivesoftware.Spark;

public class SpellcheckerPreferences 
{
	private Properties props;
	private File configFile;
	
    public SpellcheckerPreferences() {
    	this.props = new Properties();
    	
        try {
        	props.load(new FileInputStream(getConfigFile()));        	
        }
        catch (IOException e) {
        	// Can't load ConfigFile
        }

    }
    
    public File getConfigFile() {
    	if (configFile == null)
    		configFile = new File(Spark.getSparkUserHome(),
    							  "spellchecking.properties");
    	
    	return configFile;
    }
    
    public void save() {
    	try {
    		props.store(new FileOutputStream(getConfigFile()), "");
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    public void setSpellLanguage(String name) {
        props.setProperty("selectedSpellLanguage", name);
    }
    
    public String getSpellLanguage() {
       return props.getProperty("selectedSpellLanguage", Locale.getDefault().getLanguage());
    }
	
    public void setSpellCheckerEnabled(boolean enabled) {
        setBoolean("spellCheckerEnabled", enabled);
    }
    
    public boolean isSpellCheckerEnabled() {
        return getBoolean("spellCheckerEnabled", true);
    }
    
    public void setAutoSpellCheckerEnabled(boolean enabled) {
        setBoolean("autoSpellCheckerEnabled", enabled);
    }
    
    public boolean isAutoSpellCheckerEnabled() {
        return getBoolean("autoSpellCheckerEnabled", true);
    }
    
    private boolean getBoolean(String property, boolean defaultValue) {
        return Boolean.parseBoolean(props.getProperty(property, Boolean.toString(defaultValue)));
    }

    private void setBoolean(String property, boolean value) {
        props.setProperty(property, Boolean.toString(value));
    }
}
