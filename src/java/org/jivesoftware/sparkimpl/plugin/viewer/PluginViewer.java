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
package org.jivesoftware.sparkimpl.plugin.viewer;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.protocol.Protocol;
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
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.URLFileSystem;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.settings.JiveInfo;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;
import org.jivesoftware.sparkimpl.updater.EasySSLProtocolSocketFactory;

/**
 * Class to handle the viewing of installed and downloadable Plugins
 */
public class PluginViewer extends JPanel implements Plugin {

	private static final long serialVersionUID = -4249017716988031394L;

	private JTabbedPane tabbedPane;

    private boolean loaded = false;

    private String retrieveListURL = Default.getString("PLUGIN_REPOSITORY");

    private JProgressBar progressBar;

    private JPanel installedPanel;
    private JPanel availablePanel;
    private JPanel deactivatedPanel;
    private LocalPreferences _prefs;
    private List<String> _deactivatedPlugins;

    public PluginViewer() {
	_prefs = SettingsManager.getLocalPreferences();
	_deactivatedPlugins = _prefs.getDeactivatedPlugins();

	EventQueue.invokeLater(new Runnable() {
	   

	    public void run() {

		tabbedPane = new JTabbedPane();
		installedPanel = new JPanel();
		availablePanel = new JPanel();
		deactivatedPanel = new JPanel();
		setLayout(new GridBagLayout());

		installedPanel.setLayout(new VerticalFlowLayout(
			VerticalFlowLayout.TOP, 0, 0, true, false));
		installedPanel.setBackground(Color.white);

		availablePanel.setLayout(new VerticalFlowLayout(
			VerticalFlowLayout.TOP, 0, 0, true, false));
		availablePanel.setBackground(Color.white);
				

		// Add TabbedPane
		add(tabbedPane, new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0,
			GridBagConstraints.WEST, GridBagConstraints.BOTH,
			new Insets(5, 5, 5, 5), 0, 0));

		// Add Tabs
		tabbedPane.addTab(Res.getString("tab.installed.plugins"),
			new JScrollPane(installedPanel));
		if (!Default.getBoolean(Default.INSTALL_PLUGINS_DISABLED)) {
		    tabbedPane.addTab(Res.getString("tab.available.plugins"),
			    new JScrollPane(availablePanel));
		}
		
		loadInstalledPlugins();
		loadDeactivatedPlugins();

		tabbedPane.addChangeListener(new ChangeListener() {
		    public void stateChanged(ChangeEvent changeEvent) {
			if (tabbedPane.getSelectedComponent().equals(
				((JViewport)availablePanel.getParent()).getParent())) {
			    loadAvailablePlugins();
			    loaded = true;
			}
		    }
		});
	    }
	});
    }

    private void loadInstalledPlugins() {
        PluginManager pluginManager = PluginManager.getInstance();
        List<PublicPlugin> plugins = pluginManager.getPublicPlugins();
        for (Object plugin1 : plugins) {
            PublicPlugin plugin = (PublicPlugin) plugin1;
            final SparkPlugUI ui = new SparkPlugUI(plugin);
            ui.useLocalIcon();
            installedPanel.add(ui);
            addSparkPlugUIListener(ui);
        }
    }
    
    /**
     * Initializes the Deactivated Plugins Tab
     */
    private void loadDeactivatedPlugins() {
	deactivatedPanel.setLayout(new VerticalFlowLayout(
		VerticalFlowLayout.TOP, 0, 0, true, false));
	if (!Default.getBoolean(Default.DEINSTALL_PLUGINS_DISABLED)) {
	tabbedPane.addTab(Res.getString("tab.deactivated.plugins"), new JScrollPane(deactivatedPanel));
	}
	for (final String s : _deactivatedPlugins) {
	    PublicPlugin plg = new PublicPlugin();
	    plg.setName(s);
	    final SparkPlugUI ui = new SparkPlugUI(plg);
            ui.useLocalIcon();
            deactivatedPanel.add(ui);
            addDeactivatedListener(ui);

	}

    }


    public void initialize() {
        // Add Plugins Menu
        JMenuBar menuBar = SparkManager.getMainWindow().getJMenuBar();

        // Get last menu which is help
        JMenu sparkMenu = menuBar.getMenu(0);

        JMenuItem viewPluginsMenu = new JMenuItem();

        Action viewAction = new AbstractAction() {
			private static final long serialVersionUID = 6518407602062984752L;

			public void actionPerformed(ActionEvent e) {
                invokeViewer();
            }
        };

        viewAction.putValue(Action.NAME, Res.getString("menuitem.plugins"));
        viewAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.PLUGIN_IMAGE));
        viewPluginsMenu.setAction(viewAction);

        sparkMenu.insert(viewPluginsMenu, 2);
    }

    private boolean uninstall(final PublicPlugin plugin) {
        int ok = JOptionPane.showConfirmDialog(installedPanel, Res.getString("message.prompt.plugin.uninstall", plugin.getName()), Res.getString("title.confirmation"), JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
            
            // DOENST DELETE ANYMORE, Plugin will be added to a 'do-not-load-list'
	    // Delete main jar.
	    // File pluginDir = plugin.getPluginDir();
	    // File pluginJAR = new File(plugin.getPluginDir().getParentFile(),
	    // pluginDir.getName() + ".jar");
	    // File mainpluginJar = new
	    // File(Spark.getBinDirectory().getParent()+"/plugins/"+pluginJAR.getName());
	    // pluginJAR.delete();
	    // mainpluginJar.delete();
            
            List<String> deact = _prefs.getDeactivatedPlugins();
            deact.add(plugin.getName());    
            _prefs.setDeactivatedPlugins(deact);
            _deactivatedPlugins = deact;
            
            final SparkPlugUI ui = new SparkPlugUI(plugin);
            deactivatedPanel.add(ui);
            addDeactivatedListener(ui);
         
            
            JOptionPane.showMessageDialog(this, Res.getString("message.restart.spark.changes"), Res.getString("title.reminder"), JOptionPane.INFORMATION_MESSAGE);
            PluginManager.getInstance().removePublicPlugin(plugin);
            return true;
        }

        return false;
    }

    private void invokeViewer() {
        PluginViewer viewer = new PluginViewer();
        MessageDialog.showComponent(Res.getString("title.plugins"), "", null, viewer, SparkManager.getMainWindow(), 600, 600, false);
    }

    public void shutdown() {

    }

    public boolean canShutDown() {
        return false;
    }

    private void loadAvailablePlugins() {
        availablePanel.removeAll();
        availablePanel.invalidate();
        availablePanel.validate();
        availablePanel.repaint();

        JLabel label = new JLabel(Res.getString("message.loading.please.wait"));
        availablePanel.add(label);


        SwingWorker worker = new SwingWorker() {
            Collection<PublicPlugin> pluginList = null;

            public Object construct() {
                // Prepare HTTP post
                final GetMethod post = new GetMethod(retrieveListURL);

                // Get HTTP client
                Protocol.registerProtocol("https", new Protocol("https", new EasySSLProtocolSocketFactory(), 443));
                final HttpClient httpclient = new HttpClient();

                if(Default.getBoolean("PLUGIN_REPOSITORY_USE_PROXY"))
                {
                    String proxyHost = System.getProperty("http.proxyHost");
                    String proxyPort = System.getProperty("http.proxyPort");
                    if (ModelUtil.hasLength(proxyHost) && ModelUtil.hasLength(proxyPort)) {
                        try {
                            httpclient.getHostConfiguration().setProxy(proxyHost, Integer.parseInt(proxyPort));
                        }
                        catch (NumberFormatException e) {
                            Log.error(e);
                        }
                    }
                }

                // Execute request

                try {
                    int result = httpclient.executeMethod(post);
                    if (result != 200) {
                        return null;
                    }

                    pluginList = getPluginList(post.getResponseBodyAsStream());
                }
                catch (Exception ex) {
                    // Nothing to do
                }
                return "ok";
            }

            public void finished() {
                final PluginManager pluginManager = PluginManager.getInstance();
                if (pluginList == null) {
                    availablePanel.removeAll();
                    availablePanel.invalidate();
                    availablePanel.validate();
                    availablePanel.repaint();

                    JOptionPane.showMessageDialog(availablePanel, Res.getString("message.plugins.not.available"), Res.getString("title.error"), JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Iterator<PublicPlugin> plugs = pluginList.iterator();
                availablePanel.removeAll();

                while (plugs.hasNext()) {
                    PublicPlugin plugin = plugs.next();
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

    private void downloadPlugin(final PublicPlugin plugin) {
        // Prepare HTTP post
        final GetMethod post = new GetMethod(plugin.getDownloadURL());

        // Get HTTP client
        Protocol.registerProtocol("https", new Protocol("https", new EasySSLProtocolSocketFactory(), 443));
        final HttpClient httpclient = new HttpClient();
        String proxyHost = System.getProperty("http.proxyHost");
        String proxyPort = System.getProperty("http.proxyPort");
        
	if (Default.getBoolean("PLUGIN_REPOSITORY_USE_PROXY")) {
	    if (ModelUtil.hasLength(proxyHost)
		    && ModelUtil.hasLength(proxyPort)) {
		try {
		    httpclient.getHostConfiguration().setProxy(proxyHost,
			    Integer.parseInt(proxyPort));
		} catch (NumberFormatException e) {
		    Log.error(e);
		}
	    }
	}
        // Execute request


        try {
            int result = httpclient.executeMethod(post);
            if (result != 200) {
                return;
            }

            long length = post.getResponseContentLength();
            int contentLength = (int)length;

            progressBar = new JProgressBar(0, contentLength);

            final JFrame frame = new JFrame(Res.getString("message.downloading", plugin.getName()));

            frame.setIconImage(SparkRes.getImageIcon(SparkRes.SMALL_MESSAGE_IMAGE).getImage());

            final Thread thread = new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(2000);
                        InputStream stream = post.getResponseBodyAsStream();

                        URL url = new URL(plugin.getDownloadURL());
                        String name = URLFileSystem.getFileName(url);
                        String directoryName = URLFileSystem.getName(url);

                        File pluginDownload = new File(PluginManager.PLUGINS_DIRECTORY, name);

                        FileOutputStream out = new FileOutputStream(pluginDownload);
                        copy(stream, out);
                        out.close();

                        frame.dispose();

                        // Remove SparkPlugUI
                        // Clear all selections
                        Component[] comps = availablePanel.getComponents();
                        for (Component comp : comps) {
                            if (comp instanceof SparkPlugUI) {
                                SparkPlugUI sparkPlug = (SparkPlugUI) comp;
                                if (sparkPlug.getPlugin().getDownloadURL().equals(plugin.getDownloadURL())) {
                                    availablePanel.remove(sparkPlug);

                                    _deactivatedPlugins.remove(sparkPlug.getPlugin().getName());
                                    _prefs.setDeactivatedPlugins(_deactivatedPlugins);
                                    
                                    PluginManager.getInstance().addPlugin(sparkPlug.getPlugin());

                                    sparkPlug.showOperationButton();
                                    installedPanel.add(sparkPlug);
                                    sparkPlug.getPlugin().setPluginDir(new File(PluginManager.PLUGINS_DIRECTORY, directoryName));
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
                    catch (Exception ex) {
                        // Nothing to do
                    }
                    finally {
                        // Release current connection to the connection pool once you are done
                        post.releaseConnection();
                    }
                }
            });


            frame.getContentPane().setLayout(new GridBagLayout());
            frame.getContentPane().add(new JLabel(Res.getString("message.downloading.spark.plug")), new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
            frame.getContentPane().add(progressBar, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
            frame.pack();
            frame.setSize(400, 100);
            GraphicUtils.centerWindowOnComponent(frame, this);


            frame.setVisible(true);
            thread.start();

        }
        catch (IOException e) {
            Log.error(e);
        }
    }


    public Collection<PublicPlugin> getPluginList(InputStream response) {
        final List<PublicPlugin> pluginList = new ArrayList<PublicPlugin>();
        SAXReader saxReader = new SAXReader();
        Document pluginXML = null;

        try {
            pluginXML = saxReader.read(response);
        }
        catch (DocumentException e) {
            Log.error(e);
        }

        List<? extends Node> plugins = pluginXML.selectNodes("/plugins/plugin");

        for (Node plugin1 : plugins) {
            PublicPlugin publicPlugin = new PublicPlugin();

            String clazz;
            String name = null;
            try {
                Element plugin = (Element) plugin1;

                try {
                    String version = plugin.selectSingleNode("minSparkVersion").getText();
                    if (!isGreaterOrEqual(JiveInfo.getVersion(), version)) {
                        Log.error("Unable to load plugin " + name + " due to min version incompatibility.");
                        continue;
                    }
                }
                catch (Exception e) {
                    Log.error("Unable to load plugin " + name + " due to no minSparkVersion.");
                    continue;
                }

                name = plugin.selectSingleNode("name").getText();
                clazz = plugin.selectSingleNode("class").getText();
                publicPlugin.setPluginClass(clazz);
                publicPlugin.setName(name);

                try {
                    String version = plugin.selectSingleNode("version").getText();
                    publicPlugin.setVersion(version);

                    String author = plugin.selectSingleNode("author").getText();
                    publicPlugin.setAuthor(author);


                    Node emailNode = plugin.selectSingleNode("email");
                    if (emailNode != null) {
                        publicPlugin.setEmail(emailNode.getText());
                    }

                    Node descriptionNode = plugin.selectSingleNode("description");
                    if (descriptionNode != null) {
                        publicPlugin.setDescription(descriptionNode.getText());
                    }

                    Node homePageNode = plugin.selectSingleNode("homePage");
                    if (homePageNode != null) {
                        publicPlugin.setHomePage(homePageNode.getText());
                    }

                    Node downloadNode = plugin.selectSingleNode("downloadURL");
                    if (downloadNode != null) {
                        String downloadURL = downloadNode.getText();
                        publicPlugin.setDownloadURL(downloadURL);
                    }

                    Node changeLogNode = plugin.selectSingleNode("changeLog");
                    if (changeLogNode != null) {
                        publicPlugin.setChangeLogURL(changeLogNode.getText());
                    }

                    Node readMeNode = plugin.selectSingleNode("readme");
                    if (readMeNode != null) {
                        publicPlugin.setReadMeURL(readMeNode.getText());
                    }

                    Node smallIcon = plugin.selectSingleNode("smallIcon");
                    if (smallIcon != null) {
                        publicPlugin.setSmallIconAvailable(true);
                    }

                    Node largeIcon = plugin.selectSingleNode("largeIcon");
                    if (largeIcon != null) {
                        publicPlugin.setLargeIconAvailable(true);
                    }

                }
                catch (Exception e) {
                    Log.error("Error retrieving PluginInformation from xml.",e);
                }
                pluginList.add(publicPlugin);
            }
            catch (Exception ex) {
                ex.printStackTrace();
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
    private void copy(final InputStream in, final OutputStream out) {
        int read = 0;
        while (true) {
            try {
                try {
                    Thread.sleep(10);
                }
                catch (InterruptedException e) {
                    Log.error(e);
                }
                final byte[] buffer = new byte[4096];

                int bytesRead = in.read(buffer);
                if (bytesRead < 0) {
                    break;
                }
                out.write(buffer, 0, bytesRead);
                read += bytesRead;
                final int readprogr = read;
                EventQueue.invokeLater(new Runnable() {
		    
		    @Override
		    public void run() {
			progressBar.setValue(readprogr);
			
		    }
		});
            }
            catch (IOException e) {
                Log.error(e);
            }
        }
    }


    private void addSparkPlugUIListener(final SparkPlugUI ui) {
        ui.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent mouseEvent) {
                // Clear all selections
                Component[] comps = installedPanel.getComponents();
                for (Component comp : comps) {
                    if (comp instanceof SparkPlugUI) {
                        SparkPlugUI sparkPlug = (SparkPlugUI) comp;
                        sparkPlug.setSelected(false);
                    }
                }

                // Clear all selections
                comps = availablePanel.getComponents();
                for (Component comp : comps) {
                    if (comp instanceof SparkPlugUI) {
                        SparkPlugUI sparkPlug = (SparkPlugUI) comp;
                        sparkPlug.setSelected(false);
                    }
                }

                ui.setSelected(true);

                final PluginManager pluginManager = PluginManager.getInstance();
                ui.getInstallButton().addMouseListener(new MouseAdapter() {
                    
                    @Override
                    public void mouseClicked(MouseEvent e) {

                        boolean isInstalled = pluginManager.isInstalled(ui.getPlugin());                       
                        if (isInstalled) {
                            boolean uninstalled = uninstall(ui.getPlugin());
                            if (uninstalled) {
                                installedPanel.remove(ui);
                                installedPanel.invalidate();
                                installedPanel.repaint();
                                installedPanel.revalidate();
                            }
                        }
                        else {
                            downloadPlugin(ui.getPlugin());
                        }
                    }
		});
            }
        });
    }
    /**
     * Adds the MouseClick Listener to the PluginPreview <br>
     * Adds the MouseClick Listener to the InstallButton
     * @param ui
     */
    private void addDeactivatedListener(final SparkPlugUI ui) {

	ui.addMouseListener(new MouseAdapter() {
	    @Override
	    public void mouseClicked(MouseEvent e) {
		
		for(Component c : deactivatedPanel.getComponents())
		{
		    if (c instanceof SparkPlugUI)
		    {
			((SparkPlugUI)c).setSelected(false);
		    }
		}	
		ui.setSelected(true);
	    }
	});

	ui.getInstallButton().addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		deactivatedPanel.remove(ui);
		_deactivatedPlugins.remove(ui.getPlugin().getName());
		_prefs.setDeactivatedPlugins(_deactivatedPlugins);
		deactivatedPanel.repaint();
		deactivatedPanel.revalidate();
	    }
	});
    }

    public void uninstall() {
        // Do nothing.
    }

    /**
     * Returns true if the first version number is greater than the second.
     *
     * @param firstVersion the first version number.
     * @param secondVersion the second version number.
     * @return returns true if the first version is greater than the second.
     */
    public boolean isGreaterOrEqual(String firstVersion, String secondVersion) {
        return firstVersion.compareTo(secondVersion) >= 0;
    }

    public boolean isLoaded() {
        return loaded;
    }
}
