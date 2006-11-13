/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkimpl.plugin.emoticons;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.jivesoftware.Spark;
import org.jivesoftware.spark.util.URLFileSystem;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipFile;

/**
 * Responsible for the handling of all Emoticon packs. Using the EmoticonManager, you can specify
 * any defined Emoticon Pack, retrieve any emoticon based on its text equivalant, and retrieve its
 * associated image url.
 *
 * @author Derek DeMoro
 */
public class EmoticonManager {

    private static EmoticonManager singleton;
    private static final Object LOCK = new Object();


    private Map<String, List> emoticonMap = new HashMap<String, List>();

    /**
     * The root emoticon directory.
     */
    public static File EMOTICON_DIRECTORY;

    /**
     * Returns the singleton instance of <CODE>EmoticonManager</CODE>,
     * creating it if necessary.
     * <p/>
     *
     * @return the singleton instance of <Code>EmoticonManager</CODE>
     */
    public static EmoticonManager getInstance() {
        // Synchronize on LOCK to ensure that we don't end up creating
        // two singletons.
        synchronized (LOCK) {
            if (null == singleton) {
                EmoticonManager controller = new EmoticonManager();
                singleton = controller;
                return controller;
            }
        }
        return singleton;
    }

    private EmoticonManager() {
        EMOTICON_DIRECTORY = new File(Spark.getBinDirectory().getParent(), "xtra/emoticons").getAbsoluteFile();

        expandNewPacks();

        final LocalPreferences pref = SettingsManager.getLocalPreferences();
        String emoticonPack = pref.getEmoticonPack();

        try {
            addEmoticonPack(emoticonPack);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Collection<Emoticon> getActiveEmoticonSet() {
        final LocalPreferences pref = SettingsManager.getLocalPreferences();
        String emoticonPack = pref.getEmoticonPack();
        return emoticonMap.get(emoticonPack);
    }

    public String getActiveEmoticonSetName() {
        final LocalPreferences pref = SettingsManager.getLocalPreferences();
        String emoticonPack = pref.getEmoticonPack();
        return emoticonPack;
    }

    public void setActivePack(String pack) {
        final LocalPreferences pref = SettingsManager.getLocalPreferences();
        pref.setEmoticonPack(pack);
    }

    public String installPack(File pack) {
        String name = null;
        // Copy to the emoticon area
        try {
            URLFileSystem.copy(pack.toURL(), new File(EMOTICON_DIRECTORY, pack.getName()));

            File rootDirectory = unzipPack(pack, EMOTICON_DIRECTORY);
            name = URLFileSystem.getName(rootDirectory.toURL());
            addEmoticonPack(name);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return name;
    }

    /**
     * Loads an emoticon set.
     *
     * @param packName the name of the pack.
     */
    public void addEmoticonPack(String packName) {
        File emoticonSet = new File(EMOTICON_DIRECTORY, packName + ".adiumemoticonset");
        if (!emoticonSet.exists()) {
            emoticonSet = new File(EMOTICON_DIRECTORY, packName + ".AdiumEmoticonset");
        }

        List<Emoticon> emoticons = new ArrayList<Emoticon>();

        final File plist = new File(emoticonSet, "Emoticons.plist");
        SAXReader saxReader = new SAXReader();
        Document emoticonFile = null;
        try {
            emoticonFile = saxReader.read(plist);
        }
        catch (DocumentException e) {
            Log.error(e);
        }

        Node root = emoticonFile.selectSingleNode("/plist/dict/dict");

        List keyList = root.selectNodes("key");
        List dictonaryList = root.selectNodes("dict");

        Iterator keys = keyList.iterator();
        Iterator dicts = dictonaryList.iterator();

        while (keys.hasNext()) {
            Element keyEntry = (Element)keys.next();
            String key = keyEntry.getText();

            Element dict = (Element)dicts.next();
            String name = dict.selectSingleNode("string").getText();

            // Load equivilants
            final List<String> equivs = new ArrayList<String>();
            final List equivilants = dict.selectNodes("array/string");
            Iterator iter = equivilants.iterator();
            while (iter.hasNext()) {
                Element equivilant = (Element)iter.next();
                String equivilantString = equivilant.getText();
                equivs.add(equivilantString);
            }

            final Emoticon emoticon = new Emoticon(key, name, equivs, emoticonSet);
            emoticons.add(emoticon);
        }

        emoticonMap.put(packName, emoticons);
    }

    /**
     * Retrieve the URL to an emoticon.
     *
     * @param emoticon the emoticon.
     * @return the URL of the image.
     */
    public URL getEmoticonURL(Emoticon emoticon) {
        final String imageName = emoticon.getImageName();

        File file = new File(emoticon.getEmoticonDirectory(), imageName);
        try {
            return file.toURL();
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves the associated key emoticon.
     *
     * @param key the key.
     * @return the emoticon.
     */
    public Emoticon getEmoticon(String packName, String key) {
        final List<Emoticon> emoticons = emoticonMap.get(packName);

        for (Emoticon emoticon : emoticons) {
            for (String string : emoticon.getEquivalants()) {
                if (key.equals(string)) {
                    return emoticon;
                }
            }
        }

        return null;
    }

    public Emoticon getEmoticon(String key) {
        final List<Emoticon> emoticons = emoticonMap.get(getActiveEmoticonSetName());

        for (Emoticon emoticon : emoticons) {
            for (String string : emoticon.getEquivalants()) {
                if (key.equals(string)) {
                    return emoticon;
                }
            }
        }

        return null;
    }

    public Collection<String> getEmoticonPacks() {
        final List<String> emoticonList = new ArrayList<String>();

        File[] dirs = EMOTICON_DIRECTORY.listFiles();
        for (int i = 0; i < dirs.length; i++) {
            File file = dirs[i];
            if (file.isDirectory() && file.getName().toLowerCase().endsWith("adiumemoticonset")) {
                try {
                    String name = URLFileSystem.getName(file.toURL());
                    name = name.replaceAll("adiumemoticonset", "");
                    name = name.replaceAll("AdiumEmoticonset", "");
                    emoticonList.add(name);
                }
                catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }
        return emoticonList;
    }


    private void expandNewPacks() {
        File[] jars = EMOTICON_DIRECTORY.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                boolean accept = false;
                String smallName = name.toLowerCase();
                if (smallName.endsWith(".zip")) {
                    accept = true;
                }
                return accept;
            }
        });

        // Do nothing if no jar or zip files were found
        if (jars == null) {
            return;
        }


        for (int i = 0; i < jars.length; i++) {
            if (jars[i].isFile()) {
                File file = jars[i];

                URL url = null;
                try {
                    url = file.toURL();
                }
                catch (MalformedURLException e) {
                    Log.error(e);
                }
                String name = URLFileSystem.getName(url);
                File directory = new File(EMOTICON_DIRECTORY, name);
                if (directory.exists() && directory.isDirectory()) {
                    continue;
                }
                else {
                    // Unzip contents into directory
                    unzipPack(file, directory.getParentFile());

                    // Delete the pack
                    file.delete();
                }
            }
        }
    }

    /**
     * Unzips a theme from a ZIP file into a directory.
     *
     * @param zip the ZIP file
     * @param dir the directory to extract the plugin to.
     * @return the root directory.
     */
    private File unzipPack(File zip, File dir) {
        File rootDirectory = null;
        try {
            ZipFile zipFile = new JarFile(zip);

            dir.mkdir();
            for (Enumeration e = zipFile.entries(); e.hasMoreElements();) {
                JarEntry entry = (JarEntry)e.nextElement();
                File entryFile = new File(dir, entry.getName());
                // Ignore any manifest.mf entries.
                if (entry.getName().toLowerCase().endsWith("manifest.mf")) {
                    continue;
                }

                if (entry.isDirectory() && rootDirectory == null) {
                    rootDirectory = entryFile;
                }

                if (!entry.isDirectory()) {
                    entryFile.getParentFile().mkdirs();
                    FileOutputStream out = new FileOutputStream(entryFile);
                    InputStream zin = zipFile.getInputStream(entry);
                    byte[] b = new byte[512];
                    int len = 0;
                    while ((len = zin.read(b)) != -1) {
                        out.write(b, 0, len);
                    }
                    out.flush();
                    out.close();
                    zin.close();
                }
            }
            zipFile.close();
            zipFile = null;
        }
        catch (Exception e) {
            Log.error("Error unzipping emoticon pack", e);
        }

        return rootDirectory;
    }

}
