/**
 * Copyright (C) 2004-2011 Jive Software. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jivesoftware.sparkimpl.plugin.viewer;

import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.jivesoftware.resource.Default;
import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.spark.PluginManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.MessageDialog;
import org.jivesoftware.spark.component.VerticalFlowLayout;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.plugin.PublicPlugin;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.URLFileSystem;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.layout.LayoutSettingsManager;
import org.jivesoftware.sparkimpl.plugin.manager.Enterprise;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;
import org.jivesoftware.sparkimpl.updater.AcceptAllCertsConnectionManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Plugin Viewer Plugin.
 * Class to handle the viewing of installed and downloadable Plugins.
 * Allows users to download, install, and uninstall 3rd party plugins.
 *
 * @author Derek DeMoro
 */
public class PluginViewer extends JPanel implements Plugin
{
    private JTabbedPane tabbedPane;
    private boolean loaded = false;
    private final String retrieveListURL = Default.getString( Default.PLUGIN_REPOSITORY );
    private JPanel installedPanel;
    private JPanel availablePanel;
    private JPanel deactivatedPanel;
    private final LocalPreferences _prefs;
    private List<String> _deactivatedPlugins;

    public PluginViewer()
    {
        _prefs = SettingsManager.getLocalPreferences();
        _deactivatedPlugins = new ArrayList<>(_prefs.getDeactivatedPlugins());

        EventQueue.invokeLater( () ->
                                {
                                    tabbedPane = new JTabbedPane();
                                    installedPanel = new JPanel();
                                    availablePanel = new JPanel();
                                    deactivatedPanel = new JPanel();
                                    setLayout( new GridBagLayout() );

                                    installedPanel.setLayout( new VerticalFlowLayout(
                                            VerticalFlowLayout.TOP, 0, 0, true, false ) );
                                    installedPanel.setBackground( Color.white );

                                    availablePanel.setLayout( new VerticalFlowLayout(
                                            VerticalFlowLayout.TOP, 0, 0, true, false ) );
                                    availablePanel.setBackground( Color.white );


                                    // Add TabbedPane
                                    add( tabbedPane, new GridBagConstraints( 0, 1, 2, 1, 1.0, 1.0,
                                                                             GridBagConstraints.WEST, GridBagConstraints.BOTH,
                                                                             new Insets( 5, 5, 5, 5 ), 0, 0 ) );

                                    // Add Tabs
                                    tabbedPane.addTab( Res.getString( "tab.installed.plugins" ),
                                                       new JScrollPane( installedPanel ) );
                                    if ( !Default.getBoolean( Default.INSTALL_PLUGINS_DISABLED ) )
                                    {
                                        tabbedPane.addTab( Res.getString( "tab.available.plugins" ),
                                                           new JScrollPane( availablePanel ) );
                                    }

                                    loadInstalledPlugins();
                                    loadDeactivatedPlugins();

                                    tabbedPane.addChangeListener( changeEvent ->
                                                                  {
                                                                      if ( tabbedPane.getSelectedComponent().equals(
                                                                              availablePanel.getParent().getParent() ) )
                                                                      {
                                                                          loadAvailablePlugins();
                                                                          loaded = true;
                                                                      }
                                                                  } );
                                } );
    }

    private void loadInstalledPlugins()
    {
        PluginManager pluginManager = PluginManager.getInstance();
        List<PublicPlugin> plugins = pluginManager.getPublicPlugins();
        for ( PublicPlugin plugin : plugins )
        {
            final SparkPlugUI ui = new SparkPlugUI( plugin );
            ui.useLocalIcon();
            installedPanel.add( ui );
            addSparkPlugUIListener( ui );
        }
    }

    /**
     * Initializes the Deactivated Plugins Tab
     */
    private void loadDeactivatedPlugins()
    {
        deactivatedPanel.setLayout( new VerticalFlowLayout(
                VerticalFlowLayout.TOP, 0, 0, true, false ) );
        if ( !Default.getBoolean( Default.UNINSTALL_PLUGINS_DISABLED) )
        {
            tabbedPane.addTab( Res.getString( "tab.deactivated.plugins" ), new JScrollPane( deactivatedPanel ) );
        }
        for ( final String s : _deactivatedPlugins )
        {
            PublicPlugin plg = new PublicPlugin();
            plg.setName( s );
            final SparkPlugUI ui = new SparkPlugUI( plg );
            ui.useLocalIcon();
            deactivatedPanel.add( ui );
            addDeactivatedListener( ui );
        }
    }


    @Override
	public void initialize()
    {
        // Add Plugins Menu
        JMenuBar menuBar = SparkManager.getMainWindow().getJMenuBar();

        // Get last menu which is help
        JMenu sparkMenu = menuBar.getMenu( 0 );

        JMenuItem viewPluginsMenu = new JMenuItem();

        Action viewAction = new AbstractAction()
        {
            private static final long serialVersionUID = 6518407602062984752L;

            @Override
			public void actionPerformed( ActionEvent e )
            {
                invokeViewer();
            }
        };

        viewAction.putValue( Action.NAME, Res.getString( "menuitem.plugins" ) );
        viewAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.Icon.PLUGIN_IMAGE));
        viewPluginsMenu.setAction( viewAction );

        // See if we should disable the "Plugins" menu item
        if ( !Default.getBoolean( Default.DISABLE_PLUGINS_MENU_ITEM ) && Enterprise.containsFeature( Enterprise.PLUGINS_MENU_FEATURE ) )
        {
            sparkMenu.insert( viewPluginsMenu, 2 );
        }
    }

    private boolean uninstall( final PublicPlugin plugin )
    {
        UIManager.put( "OptionPane.yesButtonText", Res.getString( "yes" ) );
        UIManager.put( "OptionPane.noButtonText", Res.getString( "no" ) );
        UIManager.put( "OptionPane.okButtonText", Res.getString( "ok" ) );

        int ok = JOptionPane.showConfirmDialog( installedPanel, Res.getString( "message.prompt.plugin.uninstall", plugin.getName() ), Res.getString( "title.confirmation" ), JOptionPane.YES_NO_OPTION );
        if ( ok == JOptionPane.YES_OPTION )
        {

            // DOENST DELETE ANYMORE, Plugin will be added to a 'do-not-load-list'
            // Delete main jar.
            // File pluginDir = plugin.getPluginDir();
            // File pluginJAR = new File(plugin.getPluginDir().getParentFile(),
            // pluginDir.getName() + ".jar");
            // File mainpluginJar = new
            // File(Spark.getBinDirectory().getParent()+"/plugins/"+pluginJAR.getName());
            // pluginJAR.delete();
            // mainpluginJar.delete();

            _deactivatedPlugins.add(plugin.getName());
            _prefs.setDeactivatedPlugins( _deactivatedPlugins );

            final SparkPlugUI ui = new SparkPlugUI( plugin );
            deactivatedPanel.add( ui );
            addDeactivatedListener( ui );

            JOptionPane.showMessageDialog( this, Res.getString( "message.restart.spark.changes" ), Res.getString( "title.reminder" ), JOptionPane.INFORMATION_MESSAGE );
            PluginManager.getInstance().removePublicPlugin( plugin );
            return true;
        }

        return false;
    }

    private void invokeViewer()
    {
        final JDialog dialog = MessageDialog.createComponent( Res.getString( "title.plugins" ), "", null, new PluginViewer(), SparkManager.getMainWindow(), false );

        final Rectangle bounds = LayoutSettingsManager.getLayoutSettings().getPluginViewerBounds();
        if ( bounds == null || bounds.width <= 0 || bounds.height <= 0 )
        {
            // Use default settings.
            dialog.setSize( 600, 600 );
            dialog.setLocationRelativeTo( SparkManager.getMainWindow() );
        }
        else
        {
            dialog.setBounds( bounds );
        }

        dialog.addComponentListener( new ComponentAdapter()
        {
            @Override
            public void componentResized( ComponentEvent e )
            {
                LayoutSettingsManager.getLayoutSettings().setPluginViewerBounds( dialog.getBounds() );
            }

            @Override
            public void componentMoved( ComponentEvent e )
            {
                LayoutSettingsManager.getLayoutSettings().setPluginViewerBounds( dialog.getBounds() );
            }
        } );

        dialog.setVisible(true);
        dialog.toFront();
        dialog.requestFocus();
    }

    @Override
	public void shutdown()
    {

    }

    @Override
	public boolean canShutDown()
    {
        return false;
    }

    private void loadAvailablePlugins()
    {
        availablePanel.removeAll();
        availablePanel.invalidate();
        availablePanel.validate();
        availablePanel.repaint();

        JLabel label = new JLabel( Res.getString( "message.loading.please.wait" ) );
        availablePanel.add( label );


        SwingWorker worker = new SwingWorker()
        {
            Collection<PublicPlugin> pluginList = null;

            @Override
			public Object construct()
            {
                final HttpGet request = new HttpGet(retrieveListURL);
                try (final CloseableHttpClient httpClient =
                         HttpClients.custom().useSystemProperties()
                             .setConnectionManager(AcceptAllCertsConnectionManager.getInstance())
                             .build()
                ) {
                    return httpClient.execute(request, response -> {
                        if (response.getCode() != 200) {
                            return null;
                        }
                        final HttpEntity entity = response.getEntity();
                        pluginList = getPluginList( entity.getContent() );
                        EntityUtils.consume(entity);
                        return "ok";
                    });
                } catch (Exception e) {
                    Log.error(e);
                    return e.getMessage();
                }
            }

            @Override
			public void finished()
            {
                final PluginManager pluginManager = PluginManager.getInstance();
                if ( pluginList == null )
                {
                    availablePanel.removeAll();
                    availablePanel.invalidate();
                    availablePanel.validate();
                    availablePanel.repaint();

                    UIManager.put( "OptionPane.okButtonText", Res.getString( "ok" ) );
                    JOptionPane.showMessageDialog( availablePanel, Res.getString( "message.plugins.not.available" ), Res.getString( "title.error" ), JOptionPane.ERROR_MESSAGE );
                    return;
                }
                availablePanel.removeAll();
                for (PublicPlugin plugin : pluginList) {
                    if (!pluginManager.isInstalled(plugin)) {
                        SparkPlugUI ui = new SparkPlugUI(plugin);
                        availablePanel.add(ui);
                        addSparkPlugUIListener(ui);
                    }
                }

                availablePanel.invalidate();
                availablePanel.validate();
                availablePanel.repaint();
            }
        };

        worker.start();
    }

    private void downloadPlugin( final PublicPlugin plugin )
    {
        if (StringUtils.isEmpty(plugin.getDownloadURL())) {
            Log.warning( "Unable to download plugin " + plugin.getName() + " due to missing download URL.");
            return;
        }
        final HttpGet request = new HttpGet(plugin.getDownloadURL());
        try (final CloseableHttpClient httpClient =
                 HttpClients.custom().useSystemProperties()
                     .setConnectionManager(AcceptAllCertsConnectionManager.getInstance())
                     .build()
        ) {
            httpClient.execute(request, response -> {
                if (response.getCode() != 200) {
                    return null;
                }
                final HttpEntity entity = response.getEntity();
                try
                {
                    InputStream stream = entity.getContent();
                    URL url = new URL( plugin.getDownloadURL() );
                    String directoryName = URLFileSystem.getFileName(url).replaceFirst("-\\d+(\\.\\d+)*-spark-plugin\\.jar$", "");
                    String name1 = directoryName + ".jar";

                    File pluginDownload = new File( PluginManager.PLUGINS_DIRECTORY, name1 );
                    File pluginDir = new File(PluginManager.PLUGINS_DIRECTORY, directoryName);

                    FileOutputStream out = new FileOutputStream( pluginDownload );
                    copy( stream, out );
                    out.close();

                    PluginManager.getInstance().addPlugin(pluginDir);

                    // Remove SparkPlugUI
                    // Clear all selections
                    Component[] comps = availablePanel.getComponents();
                    for ( Component comp : comps )
                    {
                        if ( comp instanceof SparkPlugUI )
                        {
                            SparkPlugUI sparkPlug = (SparkPlugUI) comp;
                            if (sparkPlug.getPlugin().getName().equals(plugin.getName()))
                            {
                                availablePanel.remove( sparkPlug );

                                _deactivatedPlugins.remove( sparkPlug.getPlugin().getName() );
                                _prefs.setDeactivatedPlugins( _deactivatedPlugins );

                                sparkPlug.showOperationButton();
                                installedPanel.add( sparkPlug );
                                sparkPlug.getPlugin().setPluginDir(pluginDir);
                                installedPanel.invalidate();
                                installedPanel.repaint();
                                availablePanel.invalidate();
                                availablePanel.invalidate();
                                availablePanel.validate();
                                availablePanel.repaint();
                            }
                        }
                    }
                }
                catch ( Exception ex )
                {
                    // Nothing to do
                    Log.error(ex);
                }
                return null;
            });
        } catch (Exception e) {
            Log.error( e );
        }
    }


    public Collection<PublicPlugin> getPluginList( InputStream response )
    {
        final List<PublicPlugin> pluginList = new ArrayList<>();
        SAXReader saxReader = SAXReader.createDefault();
        Document pluginXML;

        try
        {
            pluginXML = saxReader.read( response );
        }
        catch ( DocumentException e )
        {
            Log.error( e );
            return Collections.emptyList();
        }

        List<? extends Node> plugins = pluginXML.selectNodes( "/plugins/plugin" );
        for ( Node plugin1 : plugins )
        {
            Element plugin = (Element) plugin1;
            PublicPlugin publicPlugin = PluginManager.getInstance().parsePluginXml(plugin);
            if (publicPlugin != null) {
                pluginList.add(publicPlugin);
            }
        }
        return pluginList;
    }

    /**
     * Common code for copy routines.  By convention, the streams are
     * closed in the same method in which they were opened.  Thus,
     * this method does not close the streams when the copying is done.
     *
     * @param in Stream to copy from.
     * @param out Stream to copy to.
     */
    private void copy( final InputStream in, final OutputStream out )
    {
        while ( true )
        {
            try
            {
                final byte[] buffer = new byte[ 4096 ];
                int bytesRead = in.read( buffer );
                if ( bytesRead < 0 )
                {
                    break;
                }
                out.write( buffer, 0, bytesRead );
            }
            catch ( IOException e )
            {
                Log.error( e );
            }
        }
    }


    private void addSparkPlugUIListener( final SparkPlugUI ui )
    {
        ui.addMouseListener( new MouseAdapter()
        {
            @Override
			public void mouseClicked( MouseEvent mouseEvent )
            {
                // Clear all selections
                Component[] comps = installedPanel.getComponents();
                for ( Component comp : comps )
                {
                    if ( comp instanceof SparkPlugUI )
                    {
                        SparkPlugUI sparkPlug = (SparkPlugUI) comp;
                        sparkPlug.setSelected( false );
                    }
                }

                // Clear all selections
                comps = availablePanel.getComponents();
                for ( Component comp : comps )
                {
                    if ( comp instanceof SparkPlugUI )
                    {
                        SparkPlugUI sparkPlug = (SparkPlugUI) comp;
                        sparkPlug.setSelected( false );
                    }
                }

                ui.setSelected( true );

                final PluginManager pluginManager = PluginManager.getInstance();
                ui.getInstallButton().addMouseListener( new MouseAdapter()
                {

                    @Override
                    public void mouseClicked( MouseEvent e )
                    {

                        boolean isInstalled = pluginManager.isInstalled( ui.getPlugin() );
                        if ( isInstalled )
                        {
                            boolean uninstalled = uninstall( ui.getPlugin() );
                            if ( uninstalled )
                            {
                                installedPanel.remove( ui );
                                installedPanel.invalidate();
                                installedPanel.repaint();
                                installedPanel.revalidate();
                            }
                        }
                        else
                        {
                            downloadPlugin( ui.getPlugin() );
                        }
                    }
                } );
            }
        } );
    }

    /**
     * Adds the MouseClick Listener to the PluginPreview <br>
     * Adds the MouseClick Listener to the InstallButton
     * @param ui
     */
    private void addDeactivatedListener( final SparkPlugUI ui )
    {

        ui.addMouseListener( new MouseAdapter()
        {
            @Override
            public void mouseClicked( MouseEvent e )
            {
                for ( Component c : deactivatedPanel.getComponents() )
                {
                    if ( c instanceof SparkPlugUI )
                    {
                        ( (SparkPlugUI) c ).setSelected( false );
                    }
                }
                ui.setSelected( true );
            }
        } );

        ui.getInstallButton().addActionListener(e -> {
            deactivatedPanel.remove(ui);

            _deactivatedPlugins.remove(ui.getPlugin().getName());
            _prefs.setDeactivatedPlugins(_deactivatedPlugins);

            deactivatedPanel.repaint();
            deactivatedPanel.revalidate();
            JOptionPane.showMessageDialog( this, Res.getString( "message.restart.spark.changes" ), Res.getString( "title.reminder" ), JOptionPane.INFORMATION_MESSAGE );
        });
    }

    @Override
	public void uninstall()
    {
        // Do nothing.
    }

    public boolean isLoaded()
    {
        return loaded;
    }
}
