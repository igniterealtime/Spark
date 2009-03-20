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
import org.dts.spell.dictionary.OpenOfficeSpellDictionary;
import org.dts.spell.dictionary.SpellDictionary;
import org.jivesoftware.spark.SparkManager;

public class SpellcheckManager 
{
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
        preferences =  new SpellcheckerPreference(languages);
        
		String language = SparkManager.getMainWindow().getLocale().getLanguage();        
        if (preferences.getPreferences().getSpellLanguage() != null)
        {
        	language = preferences.getPreferences().getSpellLanguage();
        }
		
		checker = new SpellChecker(getDictionary(language));
	}
	
	public void loadDictionary(String language) {
		checker.setDictionary(getDictionary(language));
	}
	
	private SpellDictionary getDictionary(String language) {
		SpellDictionary dict = null;
		try	{
			InputStream dictionary  = getClass().getClassLoader().getResourceAsStream("dictionary/" + language + ".zip");
			
			if (dictionary != null)
				System.out.println("Dictionary: " + language + ".zip");
			else
				System.out.println("Dictionary not found");
	
			File personalDictionary =  new File(SparkManager.getUserDirectory(), "personalDictionary.dict");
			dict = new OpenOfficeSpellDictionary(dictionary,personalDictionary);
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
		try	{			
			String qualifiedClassName = getClass().getName();
			Class<?> qc = Class.forName( qualifiedClassName );
			CodeSource source = qc.getProtectionDomain().getCodeSource();
			System.out.println(source.getLocation());
			File jarFile = new File(source.getLocation().getFile());
			if (jarFile.exists() && jarFile.isFile()) {
		        ZipFile zipFile = new JarFile(jarFile);
		        for (Enumeration<?> e = zipFile.entries(); e.hasMoreElements();) {
		        	JarEntry entry = (JarEntry)e.nextElement();            
		        	
		        	if (entry.getName().startsWith("dictionary/") && 
		        		entry.getName().endsWith(".zip"))
		        	{     
		        		String languageFile = entry.getName().substring(11);
		        		String lang = languageFile.substring(0,languageFile.lastIndexOf(".zip"));
		        		languages.add(lang);
		        	}
		        }
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
