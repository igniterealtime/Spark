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
import java.io.IOException;
import java.io.InputStream;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipFile;

import org.dts.spell.SpellChecker;
import org.dts.spell.dictionary.openoffice.OpenOfficeSpellDictionary;
import org.dts.spell.dictionary.SpellDictionary;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.util.log.Log;

public class SpellcheckManager {
    private static SpellcheckManager instance = null;
    private SpellChecker checker;
    private ArrayList<String> languages;
    private SpellcheckerPreference preferences;

    public static SpellcheckManager getInstance() {
	if (instance == null) {
	    instance = new SpellcheckManager();
	}

	return instance;
    }

    private SpellcheckManager() {

	loadSupportedLanguages();

	try {
	    preferences = new SpellcheckerPreference(languages);

	    String language = SparkManager.getMainWindow().getLocale()
		    .getLanguage();
	    if (preferences.getPreferences().getSpellLanguage() != null) {
		language = preferences.getPreferences().getSpellLanguage();
	    }

	    checker = new SpellChecker(getDictionary(language));
	} catch (Exception e) {
	    e.printStackTrace();
	}

    }

    public void loadDictionary(String language) {
	checker.setDictionary(getDictionary(language));
    }

    public SpellDictionary getDictionary(String language) {
	SpellDictionary dict = null;
	try {
	    InputStream dictionary = getClass().getClassLoader()
		    .getResourceAsStream("dictionary/" + language + ".zip");

	    if (dictionary == null)
		Log.error("Dictionary not found");

	    File personalDictionary = new File(SparkManager.getUserDirectory(),
		    "personalDictionary.dict");
	    dict = new OpenOfficeSpellDictionary(dictionary, personalDictionary);
	} catch (IOException e) {
	    e.printStackTrace();
	}
	return dict;
    }

    public SpellcheckerPreference getSpellcheckerPreference() {
	return preferences;
    }

    public ArrayList<String> getSupportedLanguages() {
	return languages;
    }

    public SpellChecker getSpellChecker() {
	return checker;
    }

    private void loadSupportedLanguages() {

	languages = new ArrayList<String>();
	try {
	    String qualifiedClassName = getClass().getName();
	    Class<?> qc = Class.forName(qualifiedClassName);
	    CodeSource source = qc.getProtectionDomain().getCodeSource();

	    File jarFile = new File(source.getLocation().getFile());
	    if (jarFile.exists() && jarFile.isFile()) {
		ZipFile zipFile = new JarFile(jarFile);
		for (Enumeration<?> e = zipFile.entries(); e.hasMoreElements();) {
		    JarEntry entry = (JarEntry) e.nextElement();

		    if (entry.getName().startsWith("dictionary/")
			    && entry.getName().endsWith(".zip")) {
			String languageFile = entry.getName().substring(11);
			String lang = languageFile.substring(0,
				languageFile.lastIndexOf(".zip"));
			languages.add(lang);
		    }
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
}
