/**
 * $RCSfile: ,v $
 * $Revision: $
 * $Date: $
 * 
 * Copyright (C) 2004-2011 Jive Software. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jivesoftware.spellchecker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

import org.jivesoftware.Spark;

public class SpellcheckerPreferences {
    private Properties props;
    private File configFile;

    public SpellcheckerPreferences() {
	this.props = new Properties();

	try {
	    props.load(new FileInputStream(getConfigFile()));
	} catch (IOException e) {
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
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public void setSpellLanguage(String name) {
	props.setProperty("selectedSpellLanguage", name);
    }

    public String getSpellLanguage() {
	return props.getProperty("selectedSpellLanguage", Locale.getDefault()
		.getLanguage());
    }

    public void setSpellCheckerEnabled(boolean enabled) {
	setBoolean("spellCheckerEnabled", enabled);
    }

    public boolean isSpellCheckerEnabled() {
	return getBoolean("spellCheckerEnabled", false);
    }

    public void setAutoSpellCheckerEnabled(boolean enabled) {
	setBoolean("autoSpellCheckerEnabled", enabled);
    }

    public boolean isAutoSpellCheckerEnabled() {
	return getBoolean("autoSpellCheckerEnabled", false);
    }
    
    public boolean getLanguageSelectionInChatRoom()
    {
       return getBoolean("showLanguageSelectionInChatRoom", false);
    }

    public void setLanguageSelectionInChatRoom(boolean value)
    {
       setBoolean("showLanguageSelectionInChatRoom", value);
    }
    
    private boolean getBoolean(String property, boolean defaultValue) {
	return Boolean.parseBoolean(props.getProperty(property,
		Boolean.toString(defaultValue)));
    }

    private void setBoolean(String property, boolean value) {
	props.setProperty(property, Boolean.toString(value));
    }
    
    public boolean getIgnoreUppercase()
    {
	return getBoolean("ignoreUppercase", false);
    }
    
    public void setIgnoreUppercase(boolean ignore)
    {
	setBoolean("ignoreUppercase",ignore);
    }
}
