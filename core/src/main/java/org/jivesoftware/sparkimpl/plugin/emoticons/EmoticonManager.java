/**
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
package org.jivesoftware.sparkimpl.plugin.emoticons;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.jivesoftware.Spark;
import org.jivesoftware.spark.util.StringUtils;
import org.jivesoftware.spark.util.URLFileSystem;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;
import org.xml.sax.SAXException;

import javax.swing.ImageIcon;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipFile;

import static java.util.Arrays.asList;

/**
 * Responsible for the handling of all Emoticon packs. Using the
 * EmoticonManager, you can specify any defined Emoticon Pack, retrieve any
 * emoticon based on its text equivalent, and retrieve its associated image url.
 *
 * @author Derek DeMoro
 */
public class EmoticonManager {

    private static EmoticonManager singleton;
    private static final Object LOCK = new Object();

    // Mapped by pack name, then by 'equivalent' key.
    private final Map<String, Map<String, Emoticon>> emoticonMap = new HashMap<>();
    private final Map<String, ImageIcon> imageMap = new HashMap<>();

    /**
     * The root emoticon directory.
     */
    public static File EMOTICON_DIRECTORY;

    /**
     * Returns the singleton instance of <code>EmoticonManager</code>.
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

    /**
     * Initialize the EmoticonManager
     */
    private EmoticonManager() {
        EMOTICON_DIRECTORY = new File(Spark.getBinDirectory().getParent(),
            "xtra/emoticons").getAbsoluteFile();

        File[] files;
        files = EMOTICON_DIRECTORY.listFiles();

        // If files in this directory, copy these files into the Spark User Home
        // Directory
        if (files != null) {
            // Copy over to allow for non-admins to extract.
            copyFiles();
            final LocalPreferences pref = SettingsManager.getLocalPreferences();
            String emoticonPack = pref.getEmoticonPack();
            try {
                addEmoticonPack(emoticonPack);
            } catch (Exception e) {
                Log.error(e);
            }
        }
    }

    /**
     * Copy the files directly over to an accepted permissions directory.
     */
    private void copyFiles() {
        // Current Plugin directory
        File newEmoticonDir = new File(Spark.getLogDirectory().getParentFile(),
            "xtra/emoticons").getAbsoluteFile();
        newEmoticonDir.mkdirs();
        deleteOldEmoticons(newEmoticonDir);

        File[] files = EMOTICON_DIRECTORY.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                try {
                    // Copy over
                    File newFile = new File(newEmoticonDir, file.getName());

                    // Check timestamps
                    long installerFile = file.lastModified();
                    long copiedFile = newFile.lastModified();
                    if (installerFile > copiedFile) {
                        // Check if File is Zip-File
                        int endIndex = file.getName().indexOf(".zip");
                        if (endIndex > 0) {
                            String unzipURL = file.getName().substring(0, endIndex);
                            File unzipFile = new File(newEmoticonDir, unzipURL);

                            if (!unzipFile.exists() || !checkIfSameFile(file, newFile)) {
                                // Copy over and expand :)
                                URLFileSystem.copy(file.toURI().toURL(), newFile);
                                expandNewPack(newFile, newEmoticonDir);
                            }
                        }
                    }
                } catch (IOException e) {
                    Log.error(e);
                }
            }
        }
        EMOTICON_DIRECTORY = newEmoticonDir;
    }

    private boolean checkIfSameFile(File oldZip, File newZip) {
        boolean result = true;

        ZipFile oldZipFile = null;
        ZipFile newZipFile = null;
        try {
            oldZipFile = new JarFile(oldZip);
            newZipFile = new JarFile(newZip);
            if (oldZipFile.size() == newZipFile.size()) {
                for (Enumeration<?> e = newZipFile.entries(); e.hasMoreElements(); ) {
                    JarEntry entry = (JarEntry) e.nextElement();
                    if (oldZipFile.getEntry(entry.getName()) == null ||
                        entry.hashCode() != oldZipFile.getEntry(entry.getName()).hashCode()) {
                        result = false;
                        break;
                    }
                }
            } else {
                result = false;
            }
        } catch (IOException e) {
            Log.error(e);
        }
        closeFile(newZipFile);
        closeFile(oldZipFile);
        return result;
    }

    private void closeFile(ZipFile zipFile) {
        try {
            if (zipFile != null) {
                zipFile.close();
            }
        } catch (IOException e) {
            Log.error(e);
        }
    }

    /**
     * Returns the active emoticon set within Spark.
     */
    public Collection<Emoticon> getActiveEmoticonSet() {
        final LocalPreferences pref = SettingsManager.getLocalPreferences();
        String emoticonPack = pref.getEmoticonPack();
        // If EmoticonPack is set
        //When no emoticon set is available, return an empty list
        if (emoticonPack != null) {
            Map<String, Emoticon> emoticons = emoticonMap.get(emoticonPack);
            Collection<Emoticon> empty = List.of();
            return emoticons == null ? empty : new LinkedHashSet<>(emoticons.values());
        }
        return Collections.emptyList();
    }

    /**
     * Returns the name of the active emoticon set.
     */
    public String getActiveEmoticonSetName() {
        final LocalPreferences pref = SettingsManager.getLocalPreferences();
        return pref.getEmoticonPack();
    }

    /**
     * Sets the active emoticon set.
     *
     * @param pack the archive containing the emoticon pack.
     */
    public void setActivePack(String pack) {
        final LocalPreferences pref = SettingsManager.getLocalPreferences();
        pref.setEmoticonPack(pack);
        SettingsManager.saveSettings();
        imageMap.clear();
    }

    /**
     * Installs a new Adium style emoticon pack into Spark.
     *
     * @param pack the emoticon pack (contains Emotiocons.plist)
     * @return the name of the newly installed emoticon set.
     */
    public String installPack(File pack) {
        if (!containsEmoticonPList(pack)) {
            return null;
        }

        String name;
        // Copy to the emoticon area
        try {
            File dst = new File(EMOTICON_DIRECTORY, pack.getName());
            URLFileSystem.copy(pack.toURI().toURL(), dst);

            File rootDirectory = unzipPack(pack, EMOTICON_DIRECTORY);
            name = URLFileSystem.getName(rootDirectory.toURI().toURL());
            addEmoticonPack(name);
        } catch (IOException e) {
            Log.error(e);
            return null;
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

        if (!emoticonSet.exists()) {
            emoticonSet = new File(EMOTICON_DIRECTORY, "Default.adiumemoticonset");
            packName = "Default";
            setActivePack("Default");
        }

        final File plist = new File(emoticonSet, "Emoticons.plist");
        if (!plist.exists()) {
            Log.error("Emoticons.plist not found in " + emoticonSet.getAbsolutePath());
            return;
        }
        Map<String, Emoticon> emoticons = new LinkedHashMap<>();

        // Create SaxReader and set to non-validating parser.
        // This will allow for non-http problems to not break spark :)
        final SAXReader saxParser = new SAXReader();
        saxParser.setValidation(false);
        try {
            saxParser.setFeature("http://xml.org/sax/features/validation", false);
            saxParser.setFeature("http://xml.org/sax/features/namespaces", false);
            saxParser.setFeature("http://apache.org/xml/features/validation/schema", false);
            saxParser.setFeature("http://apache.org/xml/features/validation/schema-full-checking", false);
            saxParser.setFeature("http://apache.org/xml/features/validation/dynamic", false);
            saxParser.setFeature("http://apache.org/xml/features/allow-java-encodings", true);
            saxParser.setFeature("http://apache.org/xml/features/continue-after-fatal-error", true);
            saxParser.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);

            // SPARK-2147: Disable certain features for security purposes (CVE-2020-10683)
            saxParser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            saxParser.setFeature("http://xml.org/sax/features/external-general-entities", false);
            saxParser.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        } catch (SAXException e) {
            Log.error(e);
            return;
        }

        Document emoticonFile;
        try {
            emoticonFile = saxParser.read(plist);
        } catch (DocumentException e) {
            Log.error(e);
            return;
        }

        Node root = emoticonFile.selectSingleNode("/plist/dict/dict");
        List<Node> keyList = root.selectNodes("key");
        List<Node> dictonaryList = root.selectNodes("dict");
        Iterator<Node> dicts = dictonaryList.iterator();
        for (Node keyEntry : keyList) {
            String key = keyEntry.getText();
            Node dict = dicts.next();
            String name = dict.selectSingleNode("string").getText();
            // Load equivalents
            final List<? extends Node> equivalents = dict.selectNodes("array/string");
            final List<String> equivs = new ArrayList<>(equivalents.size());
            for (Node equivalent : equivalents) {
                equivs.add(equivalent.getText());
            }
            final Emoticon emoticon = new Emoticon(key, name, equivs, emoticonSet);
            for (String equivalent : emoticon.getEquivalants()) {
                emoticons.put(equivalent, emoticon);
            }
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
            return file.toURI().toURL();
        } catch (MalformedURLException ignored) {
        }
        return null;
    }

    /**
     * Retrieves the associated key emoticon.
     *
     * @param packName the name of the Archive Pack File.
     * @param key      the key.
     * @return the emoticon.
     */
    public Emoticon getEmoticon(String packName, String key) {
        final Map<String, Emoticon> emoticons = emoticonMap.get(packName);
        if (emoticons == null) {
            return null;
        }
        return emoticons.get(key);
    }

    /**
     * Returns the <code>Emoticon</code> associated with the given key. Note:
     * This gets the emoticon from the active emoticon pack.
     *
     * @param key the key.
     * @return the Emoticon found. If no emoticon is found, null is returned.
     */
    public Emoticon getEmoticon(String key) {
        return getEmoticon(getActiveEmoticonSetName(), key);
    }

    /**
     * Returns the Icon that is mapped to a given key.
     *
     * @param key the key to search for.
     * @return the Icon representing the key.
     */
    public ImageIcon getEmoticonImage(String key) {
        final Emoticon emoticon = getEmoticon(key);
        if (emoticon != null) {
            ImageIcon icon = imageMap.get(key);
            if (icon == null) {
                URL url = getEmoticonURL(emoticon);
                icon = new ImageIcon(url);
                imageMap.put(key, icon);
            }
            return imageMap.get(key);
        }
        return null;
    }

    /**
     * Returns a list of all available emoticon packs.
     *
     * @return Collection of Emoticon Pack names.
     */
    public Collection<String> getEmoticonPacks() {
        final List<String> emoticonList = new ArrayList<>();
        File[] dirs = EMOTICON_DIRECTORY.listFiles();
        // If no emoticons are available
        if (dirs == null) {
            return null;
        }

        for (File file : dirs) {
            if (file.isDirectory()
                && file.getName().toLowerCase().endsWith("adiumemoticonset")) {
                try {
                    String name = URLFileSystem.getName(file.toURI().toURL());
                    name = name.replaceAll("adiumemoticonset", "");
                    name = name.replaceAll("AdiumEmoticonset", "");
                    emoticonList.add(name);
                } catch (MalformedURLException ignored) {
                }
            }
        }
        return emoticonList;
    }

    /**
     * Expands any zipped Emoticon Packs.
     *
     * @param file File to unpack.
     * @param dist Dist file.
     */
    private void expandNewPack(File file, File dist) {
        URL url;
        try {
            url = file.toURI().toURL();
        } catch (MalformedURLException ignored) {
            return;
        }
        String name = URLFileSystem.getName(url);
        File directory = new File(dist, name);

        // Unzip contents into directory
        unzipPack(file, directory.getParentFile());
    }

    /**
     * Checks the zip file for the Emoticons.plist file. This is Sparks way of
     * detecting a valid file.
     *
     * @param zip the zip file to check.
     * @return true if the EmoticonPlist exists in the archive.
     */
    private boolean containsEmoticonPList(File zip) {
        ZipFile zipFile = null;
        boolean result = false;
        try {
            zipFile = new JarFile(zip);
            for (Enumeration<?> e = zipFile.entries(); e.hasMoreElements(); ) {
                JarEntry entry = (JarEntry) e.nextElement();
                if (entry.getName().contains("Emoticons.plist")) {
                    result = true;
                    break;
                }
            }
        } catch (IOException e) {
            Log.error(e);
        }
        closeFile(zipFile);
        return result;
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
            for (Enumeration<?> e = zipFile.entries(); e.hasMoreElements(); ) {
                JarEntry entry = (JarEntry) e.nextElement();
                File entryFile = new File(dir, entry.getName());

                //Fix Zip Slip Vulnerability
                if (!entryFile.toPath().normalize().startsWith(dir.toPath().normalize())) {
                    throw new RuntimeException("Bad zip entry");
                }

                // Ignore any manifest.mf entries.
                if (entry.getName().toLowerCase().endsWith("manifest.mf")) {
                    continue;
                }

                if (entry.isDirectory() && rootDirectory == null) {
                    rootDirectory = entryFile;
                }

                // Extract non‑directory entries from ZIP to filesystem
                if (!entry.isDirectory()) {
                    entryFile.getParentFile().mkdirs();
                    FileOutputStream out = new FileOutputStream(entryFile);
                    InputStream zin = zipFile.getInputStream(entry);
                    byte[] b = new byte[512];
                    int len;
                    while ((len = zin.read(b)) != -1) {
                        out.write(b, 0, len);
                    }
                    out.flush();
                    out.close();
                    zin.close();
                }
            }
            zipFile.close();
        } catch (Exception e) {
            Log.error("Error unzipping emoticon pack", e);
        }
        return rootDirectory;
    }

    /**
     * Deletes Emoticons in pathToSearch that have a different md5-hash than its correspondant in install\spark\xtra\emoticons
     */
    private void deleteOldEmoticons(File pathToSearch) {
        final String installPath = Spark.getBinDirectory().getParentFile() + File.separator + "xtra" + File.separator +
            "emoticons" + File.separator;
        final File[] files = new File(installPath).listFiles();
        final List<File> installerFiles;
        if (files == null) {
            installerFiles = List.of();
        } else {
            installerFiles = asList(files);
        }

        final File[] installedEmoticons = pathToSearch.listFiles(File::isDirectory);
        if (installedEmoticons == null) {
            return;
        }

        for (final File file : installedEmoticons) {
            final File zipFile = new File(pathToSearch, file.getName() + ".zip");
            if (!zipFile.exists()) {
                uninstall(file);
            } else {
                try {
                    final File f = new File(installPath + zipFile.getName());
                    // Compare old and new files by checksums
                    if (installerFiles.contains(f)) {
                        final String oldfile = StringUtils.getMD5Checksum(zipFile.getAbsolutePath());
                        final String newfile = StringUtils.getMD5Checksum(f.getAbsolutePath());
                        if (!oldfile.equals(newfile)) {
                            Log.debug("deleting: " + file.getAbsolutePath() + "," + zipFile.getAbsolutePath());
                            uninstall(file);
                            zipFile.delete();
                        }
                    }
                } catch (Exception e) {
                    Log.error("No such file", e);
                }
            }
        }
    }

    private void uninstall(File emoticonDir) {
        try {
            Files.walkFileTree(emoticonDir.toPath(), new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            Log.error("An unexpected exception occurred while trying to uninstall a emoticon from: " + emoticonDir, e);
        }
    }

}
