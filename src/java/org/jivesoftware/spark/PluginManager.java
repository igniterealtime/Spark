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
package org.jivesoftware.spark;

import java.awt.EventQueue;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.JPanel;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.jivesoftware.MainWindowListener;
import org.jivesoftware.Spark;
import org.jivesoftware.resource.Default;
import org.jivesoftware.spark.PluginRes.ResourceType;
import org.jivesoftware.spark.component.tabbedPane.SparkTabbedPane;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.plugin.PluginClassLoader;
import org.jivesoftware.spark.plugin.PluginDependency;
import org.jivesoftware.spark.plugin.PublicPlugin;
import org.jivesoftware.spark.util.StringUtils;
import org.jivesoftware.spark.util.URLFileSystem;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.settings.JiveInfo;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

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

    private Plugin pluginClass;
    private PluginClassLoader classLoader;

    private Collection<String> _blacklistPlugins;

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

        // Do not use deployable plugins if not installed.
        if (System.getProperty("plugin") == null) {
            movePlugins();
        }


        // Create the extension directory if one does not exist.
        if (!PLUGINS_DIRECTORY.exists()) {
            PLUGINS_DIRECTORY.mkdirs();
        }

        _blacklistPlugins = Default.getPluginBlacklist();
    }

    private void movePlugins() {
        // Current Plugin directory
        File newPlugins = new File(Spark.getLogDirectory().getParentFile(), "plugins").getAbsoluteFile();
        newPlugins.mkdirs();
        deleteOldPlugins(newPlugins);

        File[] files = PLUGINS_DIRECTORY.listFiles();
        if (files != null) {
            final int no = files.length;
            for (int i = 0; i < no; i++) {
                File file = files[i];
                if (file.isFile()) {
                    // Copy over
                    File newFile = new File(newPlugins, file.getName());

                    if (newFile.lastModified() >= file.lastModified()) {
                        continue;
                    }

                    try {
                        URLFileSystem.copy(file.toURI().toURL(), newFile);
                    }
                    catch (IOException e) {
                        Log.error(e);
                    }

                }
            }
        }

        PLUGINS_DIRECTORY = newPlugins;
    }

    /**
     * Deletes Plugins in pathtosearch that have a different md5-hash than
     * its correspondant in install\spark\plugins\
     * @param pathtosearch
     */
    public void deleteOldPlugins(File pathtosearch) {

	String installPath = Spark.getBinDirectory().getParentFile()
		+ File.separator + "plugins" + File.separator;

	List<File> installerFiles = Arrays.asList(new File(installPath)
		.listFiles());

	File[] oldFiles = pathtosearch.listFiles();
	if (oldFiles != null) {
	    for (File file : oldFiles) {

		if (file.isDirectory()) {
		    File jarFile = new File(pathtosearch, file.getName()
			    + ".jar");
		    if (!jarFile.exists()) {
			uninstall(file);
		    } else {
			try {
			    File f = new File(installPath + jarFile.getName());
			    if (installerFiles.contains(f)) {
				String oldfile = StringUtils.getMD5Checksum(jarFile.getAbsolutePath());
				String newfile = StringUtils.getMD5Checksum(f.getAbsolutePath());

				Log.debug(f.getAbsolutePath() + "   " + jarFile.getAbsolutePath());
				Log.debug(newfile + " " + oldfile + " equal:" + oldfile.equals(newfile));

				if (!oldfile.equals(newfile)) {
				    Log.debug("deleting: "+ file.getAbsolutePath() + "," + jarFile.getAbsolutePath());
				    uninstall(file);
				    jarFile.delete();
				}

			    }

			} catch (Exception e) {
			    Log.error("No such file", e);
			}
		    }

		}
	    }
	}

    }

    /**
     * Loads all {@link Plugin} from the agent plugins.xml and extension lib.
     */
    public void loadPlugins() {
        // Delete all old plugins
        File[] oldFiles = PLUGINS_DIRECTORY.listFiles();
        if (oldFiles != null) {
            for (File file : oldFiles) {
                if (file.isDirectory()) {
                    // Check to see if it has an associated .jar
                    File jarFile = new File(PLUGINS_DIRECTORY, file.getName() + ".jar");
                    if (!jarFile.exists()) {
                        uninstall(file);
                    }
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

        loadPluginResources();
    }
    
	private boolean hasDependencies(File pluginFile) {
		SAXReader saxReader = new SAXReader();
		Document pluginXML = null;
		try {
			pluginXML = saxReader.read(pluginFile);
			List<? extends Node> dependencies = pluginXML.selectNodes("plugin/depends/plugin");
			return dependencies != null && dependencies.size() > 0 ? true : false;
		} catch (DocumentException e) {
			Log.error(e);
			return false;
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

        List<? extends Node> plugins = pluginXML.selectNodes("/plugin");
        for (Node plugin1 : plugins) {
            PublicPlugin publicPlugin = new PublicPlugin();

            String clazz = null;
            String name;
            String minVersion;

            try {

                name = plugin1.selectSingleNode("name").getText();
                clazz = plugin1.selectSingleNode("class").getText();

		try {
		    String lower = name.replaceAll("[^0-9a-zA-Z]","").toLowerCase();
		    // Dont load the plugin if its on the Blacklist
		    if(_blacklistPlugins.contains(lower) || _blacklistPlugins.contains(clazz)
			    || SettingsManager.getLocalPreferences().getDeactivatedPlugins().contains(name))
		    {
			return null;
		    }
		} catch (Exception e) {
		    // Whatever^^
		    return null;
		}

                // Check for minimum Spark version
                try {
                    minVersion = plugin1.selectSingleNode("minSparkVersion").getText();

                    String buildNumber = JiveInfo.getVersion();
                    boolean ok = buildNumber.compareTo(minVersion) >= 0;

                    if (!ok) {
                        return null;
                    }
                }
                catch (Exception e) {
                    Log.error("Unable to load plugin " + name + " due to missing <minSparkVersion>-Tag in plugin.xml.");
                    return null;
                }

                // Check for minimum Java version
                try {
                  String javaversion = plugin1.selectSingleNode("java").getText().replaceAll("[^0-9]", "");
                  javaversion = javaversion == null? "0" : javaversion;
                  int jv = Integer.parseInt(attachMissingZero(javaversion));

                  String myversion = System.getProperty("java.version").replaceAll("[^0-9]", "");
                  int mv = Integer.parseInt(attachMissingZero(myversion));

                  boolean ok = (mv >= jv);

                  if (!ok) {
                      Log.error("Unable to load plugin " + name +
                	    " due to old JavaVersion.\nIt Requires "+plugin1.selectSingleNode("java").getText()+
                	    " you have "+ System.getProperty("java.version"));
                      return null;
                  }

                }
                catch (NullPointerException e) {
                    Log.warning("Plugin "+name+" has no <java>-Tag, consider getting a newer Version");
                }

                // set dependencies
                try {
                   List<? extends Node> dependencies = plugin1.selectNodes("depends/plugin");
                   for (Node depend1 : dependencies) {
                      Element depend = (Element) depend1;
                  	 PluginDependency dependency = new PluginDependency();
                  	 dependency.setVersion(depend.selectSingleNode("version").getText());
                  	 dependency.setName(depend.selectSingleNode("name").getText());
                  	 publicPlugin.addDependency(dependency);
                   }
                }
                catch (Exception e) {
               	 e.printStackTrace();
                }


                // Do operating system check.
                boolean operatingSystemOK = isOperatingSystemOK(plugin1);
                if (!operatingSystemOK) {
                    return null;
                }

                publicPlugin.setPluginClass(clazz);
                publicPlugin.setName(name);

                try {
                    String version = plugin1.selectSingleNode("version").getText();
                    publicPlugin.setVersion(version);

                    String author = plugin1.selectSingleNode("author").getText();
                    publicPlugin.setAuthor(author);

                    String email = plugin1.selectSingleNode("email").getText();
                    publicPlugin.setEmail(email);

                    String description = plugin1.selectSingleNode("description").getText();
                    publicPlugin.setDescription(description);

                    String homePage = plugin1.selectSingleNode("homePage").getText();
                    publicPlugin.setHomePage(homePage);
                }
                catch (Exception e) {
                    Log.debug("We can ignore these.");
                }


                try {
                    pluginClass = (Plugin) getParentClassLoader().loadClass(clazz).newInstance();
                    Log.debug(name + " has been loaded.");
                    publicPlugin.setPluginDir(pluginDir);
                    publicPlugins.add(publicPlugin);


                    registerPlugin(pluginClass);
                }
                catch (Throwable e) {
                    Log.error("Unable to load plugin " + clazz + ".", e);
                }
            }
            catch (Exception ex) {
                Log.error("Unable to load plugin " + clazz + ".", ex);
            }


        }

        return pluginClass;
    }

    private String attachMissingZero(String value)
    {
	while(value.length()<5)
	{
	    value = value+"0";
	}
	return value;
    }

    /**
     * Loads an internal plugin.
     *
     * @param reader the inputstreamreader for an internal plugin.
     */
    private void loadInternalPlugins(InputStreamReader reader) {
        SAXReader saxReader = new SAXReader();
        Document pluginXML = null;
        try {
            pluginXML = saxReader.read(reader);
        }
        catch (DocumentException e) {
            Log.error(e);
        }
        List<? extends Node> plugins = pluginXML.selectNodes("/plugins/plugin");
        for (final Object plugin1 : plugins) {

          		EventQueue.invokeLater(new Runnable() {
         			public void run() {
                     String clazz = null;
                     String name;
                     try {
                         Element plugin = (Element) plugin1;

                         name = plugin.selectSingleNode("name").getText();
                         clazz = plugin.selectSingleNode("class").getText();
                         Plugin pluginClass = (Plugin) Class.forName(clazz).newInstance();
                         Log.debug(name + " has been loaded. Internal plugin.");

                         registerPlugin(pluginClass);
                     }
                     catch (Throwable ex) {
                         Log.error("Unable to load plugin " + clazz + ".", ex);
                     }
         			}
          		});


        }
    }

    private void updateClasspath() {
        try {
            classLoader = new PluginClassLoader(getParentClassLoader(), PLUGINS_DIRECTORY);
            PluginRes.setClassLoader(classLoader);
        }
        catch (MalformedURLException e) {
            Log.error("Error updating classpath.", e);
        }
        Thread.currentThread().setContextClassLoader(classLoader);
    }

    private void loadPluginResources(String resourceName, ResourceType type) {
        try {
            PropertyResourceBundle prbPlugin = (PropertyResourceBundle) ResourceBundle.getBundle(resourceName,
                    Locale.getDefault(), classLoader);
            for (String key : prbPlugin.keySet()) {
                PluginRes.putRes(key, prbPlugin.getString(key), type);
            }
        } catch (Exception ex) {
            Log.debug(resourceName + "is not overwritten in plugin ");
        }
    }

    /**
     * Loads property resources from spark.properties, default.properties, spark_i18n.properties (properly localized)
     * located in plugin jar, if any
     * In case the plugin contains preferences.properties, plugin specific defaults will be loaded instead of spark defaults for preferences
     * This method is called right after all plugins are loaded, specifically after plugins class
     * loader is initialized and plugins jars are loaded in classpath
     */
    private void loadPluginResources() {
        loadPluginResources("spark", ResourceType.SPARK);
        loadPluginResources("default", ResourceType.DEFAULT);
        loadPluginResources("preferences", ResourceType.PREFERENCES);
        loadPluginResources("spark_i18n", ResourceType.I18N);
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
    public Collection<Plugin> getPlugins() {
        return plugins;
    }

    /**
     * Returns the instance of the plugin class initialized during startup.
     *
     * @param communicatorPlugin the plugin to find.
     * @return the instance of the plugin.
     */
    public Plugin getPlugin(Class<? extends Plugin> communicatorPlugin) {
        for (Object o : getPlugins()) {
            Plugin plugin = (Plugin) o;
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
      try
		{
      	int j = 0;
			boolean dependsfound = false;

      	// Dependency check
      	for (int i = 0; i< publicPlugins.size(); i++) {
      		// if dependencies are available, check these
      		if((publicPlugins.get(i)).getDependency().size()>0) {
      			List<PluginDependency> dependencies = (publicPlugins.get(i)).getDependency();

      			// go trough all dependencies
      			for( PluginDependency dependency : dependencies) {
      				j = 0;
      				dependsfound = false;
      				// look for the specific plugin
      				for(PublicPlugin plugin1 :publicPlugins) {

      					if(plugin1.getName()!= null
      						&& plugin1.getName().equals(dependency.getName()))	{
      						// if the version is compatible then reorder
      						if(dependency.compareVersion(plugin1.getVersion())){
      							dependsfound = true;
      							// when depended Plugin hadn't been installed yet
      							if(j>i){

      								// find the position of plugins-List because it has more entries
      								int counter = 0, x = 0, z = 0;
      								for(Plugin plug : plugins) {
      									// find the position of the aim-object
      									if(plug.getClass().toString().substring(6).equals(publicPlugins.get(j).getPluginClass())) {
      										x = counter;
      									}
      									// find the change-position
      									else if(plug.getClass().toString().substring(6).equals(publicPlugins.get(i).getPluginClass())) {
      										z = counter;
      									}
      									counter ++;
      								}
      								// change the order
      								publicPlugins.add(i, publicPlugins.get(j));
      								publicPlugins.remove(j+1);

      								plugins.add(z, plugins.get(x));
      								plugins.remove(x+1);

      								// start again, to check the other dependencies
      								i--;
      							}
      						}
      						// else don't load the plugin and show an error
      						else {
      							Log.error("Depended Plugin " + dependency.getName() + " hasn't the right version (" + dependency.getVersion() + "<>" + plugin1.getVersion());
      						}
      						break;
      					}
      					j++;
      				}
      				// if the depended Plugin wasn't found, then show error
      				if(!dependsfound) {
      					Log.error("Depended Plugin " + dependency.getName() + " is missing for the Plugin " + (publicPlugins.get(i)).getName());

      					// find the posiion of plugins-List because it has more entries
      					int counter = 0;
							for(Plugin plug : plugins) {
								// find the delete-position
								if(plug.getClass().toString().substring(6).equals(publicPlugins.get(i).getPluginClass())) {
									break;
								}
								counter ++;
							}
      					// delete the Plugin, because the depended Plugin is missing
							publicPlugins.remove(i);
							plugins.remove(counter);
							i--;
							break;
      				}
      			}
      		}
      	}

			EventQueue.invokeLater(new Runnable() {
			      public void run() {
			          for (Plugin plugin1 : plugins) {
			              long start = System.currentTimeMillis();
			              Log.debug("Trying to initialize " + plugin1);
			              try {
			                  plugin1.initialize();
			              }
			              catch (Throwable e) {
			                  Log.error(e);
			              }

			              long end = System.currentTimeMillis();
			              Log.debug("Took " + (end - start) + " ms. to load " + plugin1);
			          }
			      }
			  });
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

    }

    public void shutdown() {
        for (Plugin plugin1 : plugins) {
            try {
                plugin1.shutdown();
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

    /**
     * Expands all plugin packs (.jar files located in the plugin dir with plugin.xml).
     */
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


        for (File jar : jars) {
            if (jar.isFile()) {

                URL url = null;
                try {
                    url = jar.toURI().toURL();
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
                        if (pluginXML.lastModified() < jar.lastModified()) {
                            uninstall(directory);
                            unzipPlugin(jar, directory);
                        }
                        continue;
                    }

                    uninstall(directory);
                } else {
                    // Unzip contents into directory
                    unzipPlugin(jar, directory);
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
		//Make sure to load first the plugins with no dependencies
		//If a plugin with dependencies gets loaded before one of dependencies, 
		//class not found exception may be thrown if a dependency class is used during plugin creation
		List<File> dependencies = new ArrayList<File>();
		List<File> nodependencies = new ArrayList<File>();
		for (File file : files) {
			File pluginXML = new File(file, "plugin.xml");
			if (pluginXML.exists()) {
				if (hasDependencies(pluginXML)) {
					dependencies.add(file);
				} else {
					nodependencies.add(file);
				}
			}
		}

		try {
			for (File file : nodependencies) {
				loadPlugin(classLoader, file);
			}
			for(File file : dependencies) {
				loadPlugin(classLoader, file);
			}
		} catch (Throwable e) {
			Log.error("Unable to load dirs", e);
		}
	}
	
	private void loadPlugin(PluginClassLoader classLoader, File file) throws MalformedURLException {
		classLoader.addPlugin(file);
		loadPublicPlugin(file);
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

	((PluginClassLoader) getParentClassLoader()).addPlugin(pluginDownload);

	pluginClass = loadPublicPlugin(pluginDownload);

	try {
	    EventQueue.invokeAndWait(new Runnable() {
		@Override
		public void run() {

		    Log.debug("Trying to initialize " + pluginClass);
		    pluginClass.initialize();
		}
	    });
	} catch (Exception e) {
	    Log.error(e);
	}

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
            for (Enumeration<? extends ZipEntry> e = zipFile.entries(); e.hasMoreElements();) {
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
        }
        catch (Throwable e) {
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
        for (File f : files) {
            if (f.isFile()) {
                f.delete();
            }
        }

        File libDir = new File(pluginDir, "lib");

        File[] libs = libDir.listFiles();
        if (libs != null) {
            for (File f : libs) {
                f.delete();
            }
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

    /**
     * Checks the plugin for required operating system.
     *
     * @param plugin the Plugin element to check.
     * @return true if the operating system is ok for the plugin to run on.
     */
    private boolean isOperatingSystemOK(Node plugin) {
        // Check for operating systems
        try {

            final Element osElement = (Element)plugin.selectSingleNode("os");
            if (osElement != null) {
                String operatingSystem = osElement.getText();

                boolean ok = false;

                final String currentOS = JiveInfo.getOS().toLowerCase();

                // Iterate through comma delimited string
                StringTokenizer tkn = new StringTokenizer(operatingSystem, ",");
                while (tkn.hasMoreTokens()) {
                    String os = tkn.nextToken().toLowerCase();
                    if (currentOS.contains(os) || currentOS.equalsIgnoreCase(os)) {
                        ok = true;
                    }
                }

                if (!ok) {
                    Log.debug("Unable to load plugin " + plugin.selectSingleNode("name").getText() + " due to invalid operating system. Required OS = " + operatingSystem);
                    return false;
                }
            }
        }
        catch (Exception e) {
            Log.error(e);
        }

        return true;
    }


}