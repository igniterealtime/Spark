/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.spark;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jivesoftware.MainWindowListener;
import org.jivesoftware.Spark;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.plugin.PluginClassLoader;
import org.jivesoftware.spark.plugin.PublicPlugin;
import org.jivesoftware.spark.util.URLFileSystem;
import org.jivesoftware.spark.util.log.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipFile;

import javax.swing.SwingUtilities;

/**
 * This manager is responsible for the loading of all Plugins and Workspaces within Spark environment.
 *
 * @author Derek DeMoro
 */
public class PluginManager implements MainWindowListener {
    private final List<Plugin> plugins = new ArrayList<Plugin>();

    private final List<PublicPlugin> publicPlugins = new CopyOnWriteArrayList<PublicPlugin>();
    private static PluginManager singleton;
    private static final Object LOCK = new Object();

    /**
     * The root Plugins Directory.
     */
    public static File PLUGINS_DIRECTORY = new File(Spark.getBinDirectory().getParent(), "plugins").getAbsoluteFile();

    private PluginClassLoader classLoader;


    /**
     * Returns the singleton instance of <CODE>PluginManager</CODE>,
     * creating it if necessary.
     * <p/>
     *
     * @return the singleton instance of <Code>PluginManager</CODE>
     */
    public static PluginManager getInstance() {
        // Synchronize on LOCK to ensure that we don't end up creating
        // two singletons.
        synchronized (LOCK) {
            if (null == singleton) {
                PluginManager controller = new PluginManager();
                singleton = controller;
                return controller;
            }
        }
        return singleton;
    }

    private PluginManager() {
        try {
            PLUGINS_DIRECTORY = new File(Spark.getBinDirectory().getParentFile(), "plugins").getCanonicalFile();
        }
        catch (IOException e) {
            Log.error(e);
        }

        // Copy Files over if not on Windows. This is a workaround for installation issues.
        if (!Spark.isWindows()) {
            copyFiles();
        }

        SparkManager.getMainWindow().addMainWindowListener(this);

        // Create the extension directory if one does not exist.
        if (!PLUGINS_DIRECTORY.exists()) {
            PLUGINS_DIRECTORY.mkdirs();
        }
    }

    private void copyFiles() {
        // Current Plugin directory
        File newPlugins = new File(Spark.getLogDirectory().getParentFile(), "plugins").getAbsoluteFile();
        newPlugins.mkdirs();

        File[] files = PLUGINS_DIRECTORY.listFiles();
        final int no = files != null ? files.length : 0;
        for (int i = 0; i < no; i++) {
            File file = files[i];
            if (file.isFile()) {
                // Copy over
                File newFile = new File(newPlugins, file.getName());
                try {
                    URLFileSystem.copy(file.toURL(), newFile);
                }
                catch (IOException e) {
                    Log.error(e);
                }
            }
        }

        PLUGINS_DIRECTORY = newPlugins;
    }

    /**
     * Loads all {@link Plugin} from the agent plugins.xml and extension lib.
     */
    public void loadPlugins() {
        // Delete all old plugins
        File[] oldFiles = PLUGINS_DIRECTORY.listFiles();
        final int no = oldFiles != null ? oldFiles.length : 0;
        for (int i = 0; i < no; i++) {
            File file = oldFiles[i];
            if (file.isDirectory()) {
                // Check to see if it has an associated .jar
                File jarFile = new File(PLUGINS_DIRECTORY, file.getName() + ".jar");
                if (!jarFile.exists()) {
                    uninstall(file);
                }
            }
        }

        updateClasspath();

        // At the moment, the plug list is hardcode internally until I begin
        // using external property files. All depends on deployment.
        final URL url = getClass().getClassLoader().getResource("META-INF/plugins.xml");
        try {
            InputStreamReader reader = new InputStreamReader(url.openStream());
            loadInternalPlugins(reader);
        }
        catch (IOException e) {
            Log.error("Could not load plugins.xml file.");
        }

        // Load extension plugins
        loadPublicPlugins();

        // For development purposes, load the plugin specified by -Dplugin=...
        String plugin = System.getProperty("plugin");
        if (plugin != null) {
            final StringTokenizer st = new StringTokenizer(plugin, ",", false);
            while (st.hasMoreTokens()) {
                String token = st.nextToken();
                File pluginXML = new File(token);
                loadPublicPlugin(pluginXML.getParentFile());
            }
        }
    }

    /**
     * Loads public plugins.
     *
     * @param pluginDir the directory of the expanded public plugin.
     * @return the new Plugin model for the Public Plugin.
     */
    private Plugin loadPublicPlugin(File pluginDir) {
        File pluginFile = new File(pluginDir, "plugin.xml");
        SAXReader saxReader = new SAXReader();
        Document pluginXML = null;
        try {
            pluginXML = saxReader.read(pluginFile);
        }
        catch (DocumentException e) {
            Log.error(e);
        }

        Plugin pluginClass = null;

        List plugins = pluginXML.selectNodes("/plugin");
        Iterator iter = plugins.iterator();
        while (iter.hasNext()) {
            PublicPlugin publicPlugin = new PublicPlugin();

            String clazz = null;
            String name = null;
            try {
                Element plugin = (Element)iter.next();

                name = plugin.selectSingleNode("name").getText();
                clazz = plugin.selectSingleNode("class").getText();

                try {
                    plugin.selectSingleNode("minSparkVersion").getText();
                }
                catch (Exception e) {
                    Log.error("Unable to load plugin " + name + " due to no minSparkVersion.");
                    return null;
                }

                publicPlugin.setPluginClass(clazz);
                publicPlugin.setName(name);

                try {
                    String version = plugin.selectSingleNode("version").getText();
                    publicPlugin.setVersion(version);

                    String author = plugin.selectSingleNode("author").getText();
                    publicPlugin.setAuthor(author);

                    String email = plugin.selectSingleNode("email").getText();
                    publicPlugin.setEmail(email);

                    String description = plugin.selectSingleNode("description").getText();
                    publicPlugin.setDescription(description);

                    String homePage = plugin.selectSingleNode("homePage").getText();
                    publicPlugin.setHomePage(homePage);
                }
                catch (Exception e) {
                    Log.debug("We can ignore these.");
                }


                try {
                    pluginClass = (Plugin)getParentClassLoader().loadClass(clazz).newInstance();
                    Log.debug(name + " has been loaded.");
                    publicPlugin.setPluginDir(pluginDir);
                    publicPlugins.add(publicPlugin);


                    registerPlugin(pluginClass);
                }
                catch (Exception e) {
                    Log.error("Unable to load plugin " + clazz + ".", e);
                }
            }
            catch (Exception ex) {
                Log.error("Unable to load plugin " + clazz + ".", ex);
            }


        }

        return pluginClass;
    }

    private void loadInternalPlugins(InputStreamReader reader) {
        SAXReader saxReader = new SAXReader();
        Document pluginXML = null;
        try {
            pluginXML = saxReader.read(reader);
        }
        catch (DocumentException e) {
            Log.error(e);
        }
        List plugins = pluginXML.selectNodes("/plugins/plugin");
        Iterator iter = plugins.iterator();
        while (iter.hasNext()) {


            String clazz = null;
            String name = null;
            try {
                Element plugin = (Element)iter.next();


                name = plugin.selectSingleNode("name").getText();
                clazz = plugin.selectSingleNode("class").getText();


                Plugin pluginClass = (Plugin)Class.forName(clazz).newInstance();
                Log.debug(name + " has been loaded. Internal plugin.");

                registerPlugin(pluginClass);
            }
            catch (Exception ex) {
                Log.error("Unable to load plugin " + clazz + ".", ex);
            }
        }
    }

    private void updateClasspath() {
        try {
            classLoader = new PluginClassLoader(getParentClassLoader(), PLUGINS_DIRECTORY);
        }
        catch (MalformedURLException e) {
            Log.error("Error updating classpath.", e);
        }
        Thread.currentThread().setContextClassLoader(classLoader);
    }

    /**
     * Returns the plugin classloader.
     *
     * @return the plugin classloader.
     */
    public ClassLoader getPluginClassLoader() {
        return classLoader;
    }

    /**
     * Registers a plugin.
     *
     * @param plugin the plugin to register.
     */
    public void registerPlugin(Plugin plugin) {
        plugins.add(plugin);
    }

    /**
     * Removes a plugin from the plugin list.
     *
     * @param plugin the plugin to remove.
     */
    public void removePlugin(Plugin plugin) {
        plugins.remove(plugin);
    }

    /**
     * Returns a Collection of Plugins.
     *
     * @return a Collection of Plugins.
     */
    public Collection getPlugins() {
        return plugins;
    }

    /**
     * Returns the instance of the plugin class initialized during startup.
     *
     * @param communicatorPlugin the plugin to find.
     * @return the instance of the plugin.
     */
    public Plugin getPlugin(Class communicatorPlugin) {
        Iterator iter = getPlugins().iterator();
        while (iter.hasNext()) {
            Plugin plugin = (Plugin)iter.next();
            if (plugin.getClass() == communicatorPlugin) {
                return plugin;
            }
        }
        return null;
    }

    /**
     * Loads and initalizes all Plugins.
     *
     * @see Plugin
     */
    public void initializePlugins() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                final Iterator iter = plugins.iterator();
                while (iter.hasNext()) {
                    long start = System.currentTimeMillis();
                    Plugin plugin = (Plugin)iter.next();
                    Log.debug("Trying to initialize " + plugin);
                    plugin.initialize();
                    long end = System.currentTimeMillis();
                    Log.debug("Took " + (end - start) + " ms. to load " + plugin);
                }
            }
        });

    }

    public void shutdown() {
        final Iterator pluginIter = plugins.iterator();
        while (pluginIter.hasNext()) {
            Plugin plugin = (Plugin)pluginIter.next();
            try {
                plugin.shutdown();
            }
            catch (Exception e) {
                Log.warning("Exception on shutdown of plugin.", e);
            }
        }
    }

    public void mainWindowActivated() {
    }

    public void mainWindowDeactivated() {
    }

    /**
     * Locates the best class loader based on context (see class description).
     *
     * @return The best parent classloader to use
     */
    private ClassLoader getParentClassLoader() {
        ClassLoader parent = Thread.currentThread().getContextClassLoader();
        if (parent == null) {
            parent = this.getClass().getClassLoader();
            if (parent == null) {
                parent = ClassLoader.getSystemClassLoader();
            }
        }
        return parent;
    }

    private void expandNewPlugins() {
        File[] jars = PLUGINS_DIRECTORY.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                boolean accept = false;
                String smallName = name.toLowerCase();
                if (smallName.endsWith(".jar")) {
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
                File directory = new File(PLUGINS_DIRECTORY, name);
                if (directory.exists() && directory.isDirectory()) {
                    // Check to see if directory contains the plugin.xml file.
                    // If not, delete directory.
                    File pluginXML = new File(directory, "plugin.xml");
                    if (pluginXML.exists()) {
                        if (pluginXML.lastModified() < file.lastModified()) {
                            uninstall(directory);
                            unzipPlugin(file, directory);
                            return;
                        }
                        continue;
                    }

                    uninstall(directory);
                }
                else {
                    // Unzip contents into directory
                    unzipPlugin(file, directory);
                }
            }
        }
    }

    private void loadPublicPlugins() {
        // First, expand all plugins that have yet to be expanded.
        expandNewPlugins();


        File[] files = PLUGINS_DIRECTORY.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return dir.isDirectory();
            }
        });

        // Do nothing if no jar or zip files were found
        if (files == null) {
            return;
        }

        for (int i = 0; i < files.length; i++) {
            File file = files[i];

            File pluginXML = new File(file, "plugin.xml");
            if (pluginXML.exists()) {
                try {
                    classLoader.addPlugin(file);
                }
                catch (MalformedURLException e) {
                    Log.error("Unable to load dirs", e);
                }

                loadPublicPlugin(file);
            }


        }
    }

    /**
     * Adds and installs a new plugin into Spark.
     *
     * @param plugin the plugin to install.
     * @throws Exception thrown if there was a problem loading the plugin.
     */
    public void addPlugin(PublicPlugin plugin) throws Exception {
        expandNewPlugins();

        URL url = new URL(plugin.getDownloadURL());
        String name = URLFileSystem.getName(url);
        File pluginDownload = new File(PluginManager.PLUGINS_DIRECTORY, name);

        classLoader.addPlugin(pluginDownload);
        Plugin pluginClass = loadPublicPlugin(pluginDownload);
        Log.debug("Trying to initialize " + pluginClass);
        pluginClass.initialize();

    }

    /**
     * Unzips a plugin from a JAR file into a directory. If the JAR file
     * isn't a plugin, this method will do nothing.
     *
     * @param file the JAR file
     * @param dir  the directory to extract the plugin to.
     */
    private void unzipPlugin(File file, File dir) {
        try {
            ZipFile zipFile = new JarFile(file);
            // Ensure that this JAR is a plugin.
            if (zipFile.getEntry("plugin.xml") == null) {
                return;
            }
            dir.mkdir();
            for (Enumeration e = zipFile.entries(); e.hasMoreElements();) {
                JarEntry entry = (JarEntry)e.nextElement();
                File entryFile = new File(dir, entry.getName());
                // Ignore any manifest.mf entries.
                if (entry.getName().toLowerCase().endsWith("manifest.mf")) {
                    continue;
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
            Log.error("Error unzipping plugin", e);
        }
    }

    /**
     * Returns a collection of all public plugins.
     *
     * @return the collection of public plugins.
     */
    public List<PublicPlugin> getPublicPlugins() {
        return publicPlugins;
    }

    private void uninstall(File pluginDir) {
        File[] files = pluginDir.listFiles();
        for (int i = 0; i < files.length; i++) {
            File f = files[i];
            if (f.isFile()) {
                f.delete();
            }
        }

        File libDir = new File(pluginDir, "lib");

        File[] libs = libDir.listFiles();
        final int no = libs != null ? libs.length : 0;
        for (int i = 0; i < no; i++) {
            File f = libs[i];
            f.delete();
        }

        libDir.delete();

        pluginDir.delete();
    }

    /**
     * Removes and uninstall a plugin from Spark.
     *
     * @param plugin the plugin to uninstall.
     */
    public void removePublicPlugin(PublicPlugin plugin) {
        for (PublicPlugin publicPlugin : getPublicPlugins()) {
            if (plugin.getName().equals(publicPlugin.getName())) {
                publicPlugins.remove(plugin);
            }
        }
    }

    /**
     * Returns true if the specified plugin is installed.
     *
     * @param plugin the <code>PublicPlugin</code> plugin to check.
     * @return true if installed.
     */
    public boolean isInstalled(PublicPlugin plugin) {
        for (PublicPlugin publicPlugin : getPublicPlugins()) {
            if (plugin.getName().equals(publicPlugin.getName())) {
                return true;
            }
        }

        return false;
    }


}