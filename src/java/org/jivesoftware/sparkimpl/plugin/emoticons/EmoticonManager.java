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
import org.jivesoftware.spark.util.log.Log;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

    private List<Emoticon> emoticons = new ArrayList<Emoticon>();
    private File emoticonDirectory;

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

    }

    /**
     * Loads an emoticon set.
     *
     * @param emoticonSet the directory of the emoticon set to load.
     */
    public void loadEmoticonSet(File emoticonSet) {
        // Set the current emoticon directory.
        this.emoticonDirectory = emoticonSet;

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

            final Emoticon emoticon = new Emoticon(key, name, equivs);
            emoticons.add(emoticon);
        }
    }

    /**
     * Retrieve the URL to an emoticon.
     *
     * @param emoticon the emoticon.
     * @return the URL of the image.
     */
    public URL getEmoticonURL(Emoticon emoticon) {
        final String imageName = emoticon.getImageName();

        File file = new File(emoticonDirectory, imageName);
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
    public Emoticon getEmoticon(String key) {
        for (Emoticon emoticon : emoticons) {
            for (String string : emoticon.getEquivalants()) {
                if (key.equals(string)) {
                    return emoticon;
                }
            }
        }

        return null;
    }

    /**
     * Test Bed
     *
     * @param args the args man.
     */
    public static void main(String args[]) {
        final File emoticonSet = new File("C:\\adium\\Adiumy.AdiumEmoticonset");

        EmoticonManager manager = EmoticonManager.getInstance();
        manager.loadEmoticonSet(emoticonSet);

        Emoticon emoticon = manager.getEmoticon(":)");
        System.out.println(emoticon);
        System.out.println(manager.getEmoticonURL(emoticon));
    }
}
