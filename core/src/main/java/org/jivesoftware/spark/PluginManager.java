/*
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

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.jivesoftware.MainWindowListener;
import org.jivesoftware.Spark;
import org.jivesoftware.resource.Default;
import org.jivesoftware.spark.PluginRes.ResourceType;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.plugin.PluginClassLoader;
import org.jivesoftware.spark.plugin.PluginDependency;
import org.jivesoftware.spark.plugin.PublicPlugin;
import org.jivesoftware.spark.util.StringUtils;
import org.jivesoftware.spark.util.URLFileSystem;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.settings.JiveInfo;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;
import org.xml.sax.SAXException;

import java.awt.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * This manager is responsible for the loading of all Plugins and Workspaces within the Spark environment.
 *
 * @author Derek DeMoro
 */
public class PluginManager implements MainWindowListener
{
    public static final Map<String, String> INCOMPATIBLE_PLUGINS = Map.of(
        normalizePluginName("OTR Plugin"), "0.4 Beta"
    );

    private final List<Plugin> plugins = new ArrayList<>();

    private final List<PublicPlugin> publicPlugins = new CopyOnWriteArrayList<>();
    private static PluginManager singleton;

    /**
     * The root Plugins Directory.
     */
    public static File PLUGINS_DIRECTORY = new File( Spark.getBinDirectory().getParent(), "plugins" ).getAbsoluteFile();
    public static File PROFILE_PLUGINS_DIRECTORY = new File( Spark.getLogDirectory().getParentFile(), "plugins" ).getAbsoluteFile();

    private PluginClassLoader classLoader;

    private final Collection<String> _blacklistPlugins;

    /**
     * Returns the singleton instance of PluginManager, creating it if necessary.
     *
     * @return the singleton instance of PluginManager (never null).
     */
    public synchronized static PluginManager getInstance()
    {
        if ( null == singleton )
        {
            singleton = new PluginManager();
        }
        return singleton;
    }

    private PluginManager()
    {
        try
        {
            PLUGINS_DIRECTORY = new File( Spark.getBinDirectory().getParentFile(), "plugins" ).getCanonicalFile();
        }
        catch ( IOException e )
        {
            Log.error( e );
        }

        // Do not use deployable plugins if not installed.
        if ( System.getProperty( "plugin" ) == null )
        {
            movePlugins();
        }

        // Create the extension directory if one does not exist.
        if ( !PLUGINS_DIRECTORY.exists() )
        {
            PLUGINS_DIRECTORY.mkdirs();
        }
        Log.debug("Loading plugins from: " + PLUGINS_DIRECTORY.getAbsolutePath());

        _blacklistPlugins = Default.getPluginBlacklist();
    }

    private void movePlugins()
    {
        // Current Plugin directory
        File newPlugins = PROFILE_PLUGINS_DIRECTORY;
        newPlugins.mkdirs();
        deleteOldPlugins( newPlugins );
        deletePluginIfNotExistInInstallFolder( newPlugins );

        File[] files = PLUGINS_DIRECTORY.listFiles();
        if ( files != null )
        {
            for (File file : files) {
                if (file.isFile()) {
                    // Copy over
                    File newFile = new File(newPlugins, file.getName());

                    if (newFile.lastModified() >= file.lastModified()) {
                        continue;
                    }

                    try {
                        URLFileSystem.copy(file.toURI().toURL(), newFile);
                    } catch (IOException e) {
                        Log.error(e);
                    }
                }
            }
        }

        PLUGINS_DIRECTORY = newPlugins;
    }

    /**
     * Deletes Plugins in pathToSearch that have a different md5-hash than its correspondant in install\spark\plugins\
     */
    private void deleteOldPlugins( File pathToSearch )
    {
        final String installPath = Spark.getBinDirectory().getParentFile() + File.separator + "plugins" + File.separator;
        final File[] files = new File( installPath ).listFiles();
        final List<File> installerFiles;
        if ( files == null )
        {
            installerFiles = Collections.emptyList();
        }
        else
        {
            installerFiles = Arrays.asList( files );
        }

        final File[] installedPlugins = pathToSearch.listFiles( File::isDirectory );
        if ( installedPlugins == null )
        {
            return;
        }

        for ( final File file : installedPlugins )
        {
            final File jarFile = new File( pathToSearch, file.getName() + ".jar" );
            if ( !jarFile.exists() )
            {
                uninstall( file );
            }
            else
            {
                try
                {
                    final File f = new File( installPath + jarFile.getName() );
                    // Compare old and new files by checksums
                    if ( installerFiles.contains( f ) )
                    {
                        final String oldFile = StringUtils.getMD5Checksum( jarFile.getAbsolutePath() );
                        final String newFile = StringUtils.getMD5Checksum( f.getAbsolutePath() );
                        if ( !oldFile.equals( newFile ) )
                        {
                            Log.debug( "deleting: " + file.getAbsolutePath() + "," + jarFile.getAbsolutePath() );
                            uninstall( file );
                            jarFile.delete();
                        }
                    }
                }
                catch ( Exception e )
                {
                    Log.error( "No such file", e );
                }
            }
        }
    }

    /**
     * Deletes Plugins in pathToSearch which doesn't exist in install\spark\plugins\
     */
    private void deletePluginIfNotExistInInstallFolder(File pathToSearch)
    {
        final File[] files = new File( PLUGINS_DIRECTORY.toString() ).listFiles();
        Set<String> installerFiles;
        if ( files == null )
        {
            installerFiles = Collections.emptySet();
        }
        else
        {
            installerFiles = new TreeSet<>();
            for (File file : files) {
                installerFiles.add(file.getName());
                installerFiles.add(file.getName().split("\\.")[0]);
            }
        }

        final File[] installedPlugins = pathToSearch.listFiles();
        if ( installedPlugins == null )
        {
            return;
        }
            for(File plugin : installedPlugins){
                if(!installerFiles.contains(plugin.getName())){
                    uninstall(plugin);
            }
        }
    }

    /**
     * Loads all {@link Plugin} from the agent plugins.xml and extension lib.
     */
    public void loadPlugins()
    {
        // Delete all old plugins
        File[] oldFiles = PLUGINS_DIRECTORY.listFiles();
        if ( oldFiles != null )
        {
            for ( File file : oldFiles )
            {
                if ( file.isDirectory() )
                {
                    // Check to see if it has an associated .jar
                    File jarFile = new File( PLUGINS_DIRECTORY, file.getName() + ".jar" );
                    if ( !jarFile.exists() )
                    {
                        uninstall( file );
                    }
                }
            }
        }

        updateClasspath();

        loadInternalPlugins();

        // Load extension plugins
        loadPublicPlugins();

        // For development purposes, load the plugin specified by -Dplugin=...
        String plugin = System.getProperty( "plugin" );
        if ( plugin != null )
        {
            final StringTokenizer st = new StringTokenizer( plugin, ",", false );
            while ( st.hasMoreTokens() )
            {
                String token = st.nextToken();
                File pluginXML = new File( token );
                loadPublicPlugin( pluginXML.getParentFile() );
            }
        }

        loadPluginResources();
    }

    private boolean hasDependencies( File pluginFile )
    {
        SAXReader saxReader = SAXReader.createDefault();
        try
        {
            final Document pluginXML = saxReader.read( pluginFile );
            final List<?> dependencies = pluginXML.selectNodes( "plugin/depends/plugin" );
            return dependencies != null && !dependencies.isEmpty();
        }
        catch ( DocumentException e )
        {
            Log.error( "Unable to read plugin dependencies from " + pluginFile, e );
            return false;
        }
    }

    static String normalizePluginName( String value )
    {
        return value.replaceAll( "[^0-9a-zA-Z]", "" ).toLowerCase();
    }

    /**
     * Loads public plugins.
     *
     * @param pluginDir the directory of the expanded public plugin.
     * @return the new Plugin model for the Public Plugin.
     */
    private Plugin loadPublicPlugin( File pluginDir )
    {
        File pluginFile = new File( pluginDir, "plugin.xml" );
        SAXReader saxReader = SAXReader.createDefault();
        Document pluginXML = null;
        try
        {
            pluginXML = saxReader.read( pluginFile );
        }
        catch ( DocumentException e )
        {
            Log.error( "Unable to read plugin XML file from " + pluginDir, e );
            return null;
        }

        List<? extends Node> plugins = pluginXML.selectNodes( "/plugin" );
        for ( Node plugin : plugins )
        {
            PublicPlugin publicPlugin = new PublicPlugin();

            String clazz = null;
            String name;
            String minVersion;
            String version;

            try
            {
                name = plugin.selectSingleNode( "name" ) != null ? plugin.selectSingleNode( "name" ).getText().trim() : null;
                clazz = plugin.selectSingleNode( "class" ) != null ? plugin.selectSingleNode( "class" ).getText().trim() : null;
                version = plugin.selectSingleNode( "version" ) != null ? plugin.selectSingleNode( "version" ).getText().trim() : null;

                try
                {
                    String lower = normalizePluginName( name );
                    // Don't load the plugin if it's known to be incompatible with this version of Spark.
                    if ( INCOMPATIBLE_PLUGINS.containsKey( lower ) && INCOMPATIBLE_PLUGINS.get( lower ).compareTo( version ) >= 0 ) {
                        Log.warning( "Not loading plugin " + name + " (version " + version + ") as it is incompatible with this version of Spark." );
                        return null;
                    }
                    // Don't load the plugin if it's on the Blacklist
                    if ( _blacklistPlugins.contains( lower ) || _blacklistPlugins.contains( clazz )
                        || SettingsManager.getLocalPreferences().getDeactivatedPlugins().contains( name ) )
                    {
                        Log.warning( "Not loading plugin " + name + " as it is blacklisted." );
                        return null;
                    }
                }
                catch ( Exception e )
                {
                    Log.warning( "An exception occurred while checking the plugin blacklist for " + name, e );
                    return null;
                }

                // Check for a minimum version of Spark
                try
                {
                    minVersion = plugin.selectSingleNode( "minSparkVersion" ) != null ? plugin.selectSingleNode( "minSparkVersion" ).getText().trim() : "";

                    String buildNumber = JiveInfo.getVersion();
                    boolean ok = buildNumber.compareTo( minVersion ) >= 0;

                    if ( !ok )
                    {
                        Log.warning( "Not loading plugin " + name + " as it is not supported by Spark version." );
                        return null;
                    }
                }
                catch ( Exception e )
                {
                    Log.error( "Unable to load plugin " + name + " due to missing <minSparkVersion>-Tag in plugin.xml." );
                    return null;
                }

                // Check for minimum Java version
                try
                {
                    Node nodeJavaVersion = plugin.selectSingleNode("java");
                    final String pluginMinVersion = nodeJavaVersion != null ? nodeJavaVersion.getText().trim() : "";
                    final int jv = !pluginMinVersion.isEmpty() ? StringUtils.getJavaMajorVersion(pluginMinVersion) : 11;
                    final int mv = StringUtils.getJavaMajorVersion( System.getProperty( "java.version" ) );

                    boolean ok = ( mv >= jv );

                    if ( !ok )
                    {
                        Log.error( "Unable to load plugin " + name +" due to old JavaVersion.\n" +
                                       "It Requires " + pluginMinVersion +
                                       " you have " + System.getProperty( "java.version" ) );
                        return null;
                    }

                }
                catch ( NullPointerException e )
                {
                    Log.warning( "Plugin " + name + " has no <java>-Tag, consider getting a newer Version" );
                }

                // set dependencies
                try
                {
                    List<? extends Node> dependencies = plugin.selectNodes( "depends/plugin" );
                    for ( Node depend1 : dependencies )
                    {
                        Element depend = (Element) depend1;
                        PluginDependency dependency = new PluginDependency();
                        dependency.setVersion( depend.selectSingleNode( "version" ).getText() );
                        dependency.setName( depend.selectSingleNode( "name" ).getText() );
                        publicPlugin.addDependency( dependency );
                    }
                }
                catch ( Exception e )
                {
                    Log.warning( "An exception occurred during the setting of dependencies while loading plugin " + name, e );
                }

                // Do operating system check.
                boolean operatingSystemOK = isOperatingSystemOK( plugin );
                if ( !operatingSystemOK )
                {
                    return null;
                }

                publicPlugin.setPluginClass( clazz );
                publicPlugin.setName( name );

                try
                {
                    publicPlugin.setVersion( version );

                    String author = plugin.selectSingleNode( "author" ) != null ? plugin.selectSingleNode( "author" ).getText() : null;
                    publicPlugin.setAuthor( author );

                    String email = plugin.selectSingleNode( "email" ) != null ? plugin.selectSingleNode( "email" ).getText() : null;
                    publicPlugin.setEmail( email );

                    String description = plugin.selectSingleNode( "description" ) != null ? plugin.selectSingleNode( "description" ).getText() : null;
                    publicPlugin.setDescription( description );

                    String homePage = plugin.selectSingleNode( "homePage" ) != null ? plugin.selectSingleNode( "homePage" ).getText() : null;
                    publicPlugin.setHomePage( homePage );
                }
                catch ( Exception e )
                {
                    Log.debug( "An ignorable exception occurred while loading plugin " + name + ": " + e.getMessage() );
                }

                try
                {
                    Class<? extends Plugin> pluginType = getParentClassLoader().loadClass( clazz ).asSubclass(Plugin.class);
                    Plugin pluginInstance = pluginType.getDeclaredConstructor().newInstance();
                    Log.debug( name + " has been loaded." );
                    publicPlugin.setPluginDir( pluginDir );
                    publicPlugins.add( publicPlugin );

                    registerPlugin(pluginInstance);
                    return pluginInstance;
                }
                catch ( Throwable e )
                {
                    Log.error( "Unable to load plugin " + clazz + ".", e );
                }
            }
            catch ( Exception ex )
            {
                Log.error( "Unable to load plugin " + clazz + ".", ex );
            }
        }
        return null;
    }

    /**
     * Loads an internal plugin.
     */
    private void loadInternalPlugins() {
        // At the moment, the plug list is hardcode internally until I begin using external property files. All depends on deployment.
        final URL url = getClass().getClassLoader().getResource( "META-INF/plugins.xml" );
        try ( final InputStreamReader reader = new InputStreamReader( url.openStream() ) )
        {
            loadInternalPlugins( reader );
        }
        catch ( IOException e )
        {
            Log.error( "Could not load plugins.xml file." );
        }
    }

    private void loadInternalPlugins( InputStreamReader reader )
    {
        SAXReader saxReader = SAXReader.createDefault();
        Document pluginXML;
        try
        {
            pluginXML = saxReader.read( reader );
        }
        catch ( DocumentException e )
        {
            Log.error( e );
            return;
        }
        List<Node> plugins = pluginXML.selectNodes( "/plugins/plugin" );
        for ( final Node plugin : plugins )
        {
            EventQueue.invokeLater( () -> {
                String clazz = null;
                String name;
                try
                {
                    name = plugin.selectSingleNode( "name" ).getText();
                    clazz = plugin.selectSingleNode( "class" ).getText();
                    Class<? extends Plugin> pluginType = Class.forName(clazz).asSubclass(Plugin.class);
                    Plugin pluginInstance = pluginType.getDeclaredConstructor().newInstance();
                    Log.debug( name + " has been loaded. Internal plugin." );
                    registerPlugin( pluginInstance );
                }
                catch ( Throwable ex )
                {
                    Log.error( "Unable to load plugin " + clazz + ".", ex );
                }
            } );
        }
    }

    private void updateClasspath()
    {
        try
        {
            classLoader = new PluginClassLoader( getParentClassLoader(), PLUGINS_DIRECTORY );
            PluginRes.setClassLoader( classLoader );
        }
        catch ( MalformedURLException e )
        {
            Log.error( "Error updating classpath.", e );
        }
        Thread.currentThread().setContextClassLoader( classLoader );
    }

    private void loadPluginResources( String resourceName, ResourceType type )
    {
        try
        {
            PropertyResourceBundle prbPlugin = (PropertyResourceBundle) ResourceBundle.getBundle( resourceName, Locale.getDefault(), classLoader );
            for ( String key : prbPlugin.keySet() )
            {
                PluginRes.putRes( key, prbPlugin.getString( key ), type );
            }
        }
        catch ( Exception ex )
        {
            Log.debug( resourceName + " is not overwritten in plugin " );
        }
    }

    /**
     * Loads property resources from spark.properties, default.properties, spark_i18n.properties (properly localized)
     * located in a plugin's jar, if any In case the plugin contains preferences.properties, plugin specific defaults will
     * be loaded instead of spark defaults for preferences This method is called right after all plugins are loaded,
     * specifically after plugins class loader is initialized and plugins jars are loaded in classpath
     */
    private void loadPluginResources()
    {
        loadPluginResources( "spark", ResourceType.SPARK );
        loadPluginResources( "default", ResourceType.DEFAULT );
        loadPluginResources( "preferences", ResourceType.PREFERENCES );
        loadPluginResources( "spark_i18n", ResourceType.I18N );
    }

    /**
     * Returns the plugin classloader.
     *
     * @return the plugin classloader.
     */
    public ClassLoader getPluginClassLoader()
    {
        return classLoader;
    }

    /**
     * Registers a plugin.
     *
     * @param plugin the plugin to register.
     */
    public void registerPlugin( Plugin plugin )
    {
        plugins.add( plugin );
    }

    /**
     * Removes a plugin from the plugin list.
     *
     * @param plugin the plugin to remove.
     */
    public void removePlugin( Plugin plugin )
    {
        plugins.remove( plugin );
    }

    /**
     * Returns a Collection of Plugins.
     */
    public Collection<Plugin> getPlugins()
    {
        return plugins;
    }

    /**
     * Returns the instance of the plugin class initialized during startup.
     *
     * @param communicatorPlugin the plugin to find.
     */
    public Plugin getPlugin( Class<? extends Plugin> communicatorPlugin )
    {
        for ( Plugin plugin : getPlugins() )
        {
            if ( plugin.getClass() == communicatorPlugin )
            {
                return plugin;
            }
        }
        return null;
    }

    /**
     * Loads and initializes all Plugins.
     *
     * @see Plugin
     */
    public void initializePlugins()
    {
        try
        {
            Log.debug("Start plugin dependency check");
            int j;
            boolean dependencyFound;

            // Dependency check
            for ( int i = 0; i < publicPlugins.size(); i++ )
            {
                // if dependencies are available, check these
                PublicPlugin publicPlugin = publicPlugins.get(i);
                if (!publicPlugin.getDependency().isEmpty())
                {
                    List<PluginDependency> dependencies = publicPlugin.getDependency();

                    // go through all dependencies
                    for ( PluginDependency dependency : dependencies )
                    {
                        j = 0;
                        dependencyFound = false;

                        // look for the specific plugin
                        for ( PublicPlugin plugin : publicPlugins )
                        {
                            if ( plugin.getName() != null && plugin.getName().equals( dependency.getName() ) )
                            {
                                // if the version is compatible then reorder
                                if ( dependency.compareVersion( plugin.getVersion() ) )
                                {
                                    dependencyFound = true;

                                    // when the depended Plugin hadn't been installed yet
                                    if ( j > i )
                                    {
                                        // find the position of plugins-List because it has more entries
                                        int counter = 0, x = 0, z = 0;
                                        for ( Plugin plug : plugins )
                                        {
                                            // find the position of the aim-object
                                            if ( plug.getClass().toString().substring( 6 ).equals( publicPlugins.get( j ).getPluginClass() ) )
                                            {
                                                x = counter;
                                            }
                                            // find the change-position
                                            else if ( plug.getClass().toString().substring( 6 ).equals( publicPlugin.getPluginClass() ) )
                                            {
                                                z = counter;
                                            }
                                            counter++;
                                        }

                                        // change the order
                                        publicPlugins.add( i, publicPlugins.get( j ) );
                                        publicPlugins.remove( j + 1 );

                                        plugins.add( z, plugins.get( x ) );
                                        plugins.remove( x + 1 );

                                        // start again, to check the other dependencies
                                        i--;
                                    }
                                }
                                // else don't load the plugin and show an error
                                else
                                {
                                    Log.error( "Depended Plugin " + dependency.getName() + " hasn't the right version (" + dependency.getVersion() + "<>" + plugin.getVersion() );
                                }
                                break;
                            }
                            j++;
                        }

                        // If the depended Plugin wasn't found, then show error.
                        if ( !dependencyFound )
                        {
                            Log.error( "Depended Plugin " + dependency.getName() + " is missing for the Plugin " + publicPlugin.getName() );

                            // find the position of plugins-List because it has more entries
                            int counter = 0;
                            for ( Plugin plug : plugins )
                            {
                                // find the delete-position
                                if ( plug.getClass().toString().substring( 6 ).equals( publicPlugin.getPluginClass() ) )
                                {
                                    break;
                                }
                                counter++;
                            }
                            // delete the Plugin, because the depended Plugin is missing
                            publicPlugins.remove( i );
                            plugins.remove( counter );
                            i--;
                            break;
                        }
                    }
                }
            }
            Log.debug("Completed plugin dependency check");

            EventQueue.invokeLater( () -> {
                for ( Plugin plugin : plugins )
                {
                    try
                    {
                        plugin.initialize();
                        Log.debug("Initialized " + plugin.getClass().getSimpleName());
                    }
                    catch ( Throwable e )
                    {
                        Log.error( "An exception occurred while initializing plugin " + plugin.getClass().getSimpleName(), e );
                    }
                }
            } );
        }
        catch ( Exception e )
        {
            Log.error( "An exception occurred while initializing plugins.", e );
        }
    }

    @Override
	public void shutdown()
    {
        for ( Plugin plugin : plugins )
        {
            try
            {
                plugin.shutdown();
            }
            catch ( NoSuchMethodError e )
            {
                Log.error( "NoSuchMethodError on shutdown of plugin" + e );
            }
            catch ( Exception e )
            {
                Log.warning( "Exception on shutdown of plugin.", e );
            }
        }
    }

    @Override
	public void mainWindowActivated()
    {
    }

    @Override
	public void mainWindowDeactivated()
    {
    }

    /**
     * Locates the best class loader based on context (see class description).
     *
     * @return The best parent classloader to use
     */
    private ClassLoader getParentClassLoader()
    {
        ClassLoader parent = Thread.currentThread().getContextClassLoader();
        if ( parent == null )
        {
            parent = this.getClass().getClassLoader();
            if ( parent == null )
            {
                parent = ClassLoader.getSystemClassLoader();
            }
        }
        return parent;
    }

    /**
     * Expands all plugin packs (.jar files located in the plugin dir with plugin.xml).
     */
    private void expandNewPlugins()
    {
        File[] jars = PLUGINS_DIRECTORY.listFiles( ( dir, name ) -> {
            boolean accept = false;
            String smallName = name.toLowerCase();
            if ( smallName.endsWith( ".jar" ) )
            {
                accept = true;
            }
            return accept;
        } );

        // Do nothing if no jar or zip files were found
        if ( jars == null )
        {
            return;
        }

        for ( File jar : jars )
        {
            if ( jar.isFile() )
            {

                URL url = null;
                try
                {
                    url = jar.toURI().toURL();
                }
                catch ( MalformedURLException e )
                {
                    Log.error( e );
                }
                String name = URLFileSystem.getName( url );
                File directory = new File( PLUGINS_DIRECTORY, name );
                if ( directory.exists() && directory.isDirectory() )
                {
                    // Check to see if directory contains the plugin.xml file.
                    // If not, delete directory.
                    File pluginXML = new File( directory, "plugin.xml" );
                    if ( pluginXML.exists() )
                    {
                        if ( pluginXML.lastModified() < jar.lastModified() )
                        {
                            uninstall( directory );
                            unzipPlugin( jar, directory );
                        }
                        continue;
                    }
                    uninstall( directory );
                }
                else
                {
                    // Unzip contents into directory
                    unzipPlugin( jar, directory );
                }
            }
        }
    }

    private void loadPublicPlugins()
    {
        // First, expand all plugins that have yet to be expanded.
        expandNewPlugins();
        File[] files = PLUGINS_DIRECTORY.listFiles( ( dir, name ) -> dir.isDirectory() );
        // Do nothing if no jar or zip files were found
        if ( files == null )
        {
            return;
        }
        //Make sure to load first the plugins with no dependencies
        // If a plugin with dependencies gets loaded before one of the dependencies,
        //class not found exception may be thrown if a dependency class is used during plugin creation
        List<File> dependencies = new ArrayList<>();
        List<File> nodependencies = new ArrayList<>();
        for ( File file : files )
        {
            File pluginXML = new File( file, "plugin.xml" );
            if ( pluginXML.exists() )
            {
                if ( hasDependencies( pluginXML ) )
                {
                    dependencies.add( file );
                }
                else
                {
                    nodependencies.add( file );
                }
            }
        }

        try
        {
            for ( File file : nodependencies )
            {
                loadPlugin( classLoader, file );
            }
            for ( File file : dependencies )
            {
                loadPlugin( classLoader, file );
            }
        }
        catch ( Throwable e )
        {
            Log.error( "Unable to load dirs", e );
        }
    }

    private void loadPlugin( PluginClassLoader classLoader, File file ) throws MalformedURLException
    {
        Log.debug("Start loading plugin " + file.getAbsolutePath());
        classLoader.addPlugin( file );
        loadPublicPlugin( file );
    }

    /**
     * Adds and installs a new plugin into Spark.
     *
     * @param plugin the plugin to install.
     * @throws Exception thrown if there was a problem loading the plugin.
     */
    public void addPlugin( PublicPlugin plugin ) throws Exception
    {
        expandNewPlugins();

        URL url = new URL( plugin.getDownloadURL() );
        String name = URLFileSystem.getName( url );
        File pluginDownload = new File( PluginManager.PLUGINS_DIRECTORY, name );

        ( (PluginClassLoader) getParentClassLoader() ).addPlugin( pluginDownload );

        Plugin pluginInstance = loadPublicPlugin(pluginDownload);
        if (pluginInstance == null) {
            return;
        }

        try
        {
            EventQueue.invokeAndWait( () -> {
                Log.debug( "Trying to initialize " + pluginInstance );
                pluginInstance.initialize();
                Log.debug( "Done initializing " + pluginInstance );

            } );
        }
        catch ( Exception e )
        {
            Log.error( e );
        }

    }

    /**
     * Unzips a plugin from a JAR file into a directory. If the JAR file
     * isn't a plugin, this method will do nothing.
     *
     * @param file the JAR file
     * @param dir  the directory to extract the plugin to.
     */
    private void unzipPlugin( File file, File dir )
    {
        try
        {
            ZipFile zipFile = new JarFile( file );
            // Ensure that this JAR is a plugin.
            if ( zipFile.getEntry( "plugin.xml" ) == null )
            {
                return;
            }
            dir.mkdir();
            for ( Enumeration<? extends ZipEntry> e = zipFile.entries(); e.hasMoreElements(); )
            {
                JarEntry entry = (JarEntry) e.nextElement();
                File entryFile = new File( dir, entry.getName() );

                //Fix Zip Slip Vulnerability
                if (!entryFile.toPath().normalize().startsWith(dir.toPath().normalize())) {
                    throw new RuntimeException("Bad zip entry");
                }
                
                // Ignore any MANIFEST.MF entries.
                if (entry.getName().toUpperCase().endsWith("MANIFEST.MF"))
                {
                    continue;
                }
                if ( !entry.isDirectory() )
                {
                    entryFile.getParentFile().mkdirs();
                    FileOutputStream out = new FileOutputStream( entryFile );
                    InputStream zin = zipFile.getInputStream( entry );
                    byte[] b = new byte[ 512 ];
                    int len;
                    while ( ( len = zin.read( b ) ) != -1 )
                    {
                        out.write( b, 0, len );
                    }
                    out.flush();
                    out.close();
                    zin.close();
                }
            }
            zipFile.close();
        }
        catch ( Throwable e )
        {
            Log.error( "Error unzipping plugin", e );
        }
    }

    /**
     * Returns a collection of all public plugins.
     *
     * @return the collection of public plugins.
     */
    public List<PublicPlugin> getPublicPlugins()
    {
        return publicPlugins;
    }

    private void uninstall( File pluginDir )
    {
        try
        {
            Files.walkFileTree( pluginDir.toPath(), new SimpleFileVisitor<Path>()
            {
                @Override
                public FileVisitResult visitFile( Path file, BasicFileAttributes attrs ) throws IOException
                {
                    Files.delete( file );
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory( Path dir, IOException exc ) throws IOException
                {
                    Files.delete( dir );
                    return FileVisitResult.CONTINUE;
                }
            } );
        }
        catch ( IOException e )
        {
            Log.error( "An unexpected exception occurred while trying to uninstall a plugin from: " + pluginDir, e );
        }
   }

    /**
     * Removes and uninstall a plugin from Spark.
     *
     * @param plugin the plugin to uninstall.
     */
    public void removePublicPlugin( PublicPlugin plugin )
    {
        for ( PublicPlugin publicPlugin : getPublicPlugins() )
        {
            if ( plugin.getName().equals( publicPlugin.getName() ) )
            {
                uninstall( plugin.getPluginDir() );
                publicPlugins.remove( plugin );
            }
        }
    }

    /**
     * Returns true if the specified plugin is installed.
     *
     * @param plugin the <code>PublicPlugin</code> plugin to check.
     * @return true if installed.
     */
    public boolean isInstalled( PublicPlugin plugin )
    {
        for ( PublicPlugin publicPlugin : getPublicPlugins() )
        {
            if ( plugin.getName().equals( publicPlugin.getName() ) )
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks the plugin for the required operating system.
     *
     * @param plugin the Plugin element to check.
     * @return true if the operating system is ok for the plugin to run on.
     */
    private boolean isOperatingSystemOK( Node plugin )
    {
        try
        {
            final Element osElement = (Element) plugin.selectSingleNode( "os" );
            if ( osElement != null )
            {
                String operatingSystem = osElement.getText();
                boolean ok = false;
                final String currentOS = JiveInfo.getOS().toLowerCase();
                // Iterate through comma-delimited string
                StringTokenizer tkn = new StringTokenizer( operatingSystem, "," );
                while ( tkn.hasMoreTokens() )
                {
                    String os = tkn.nextToken().toLowerCase();
                    if ( currentOS.contains( os ) || currentOS.equalsIgnoreCase( os ) )
                    {
                        ok = true;
                    }
                }

                if ( !ok )
                {
                    Log.debug( "Unable to load plugin " + plugin.selectSingleNode( "name" ).getText() + " due to invalid operating system. Required OS = " + operatingSystem );
                    return false;
                }
            }
        }
        catch ( Exception e )
        {
            Log.error("An exception occurred while trying to determine operating system compatibility of plugin '" + plugin + "'", e);
        }

        return true;
    }
}
