/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkimpl.plugin.viewer;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.protocol.Protocol;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
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
import org.jivesoftware.sparkimpl.updater.EasySSLProtocolSocketFactory;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
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
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.Color;
import java.awt.Component;
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

public class PluginViewer extends JPanel implements Plugin {

    private JToolBar toolbar = new JToolBar();
    private JButton installButton = new JButton();
    private JButton uninstallButton = new JButton();


    private JTabbedPane tabbedPane = new JTabbedPane();

    private boolean loaded = false;

    private String retrieveListURL = "http://www.jivesoftware.org/updater/plugins.jsp";

    private JProgressBar progressBar;
    private PluginViewer viewer;

    private final JPanel installedPanel = new JPanel();
    private final JPanel availablePanel = new JPanel();

    public PluginViewer() {
        setLayout(new GridBagLayout());

        installedPanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP, 0, 0, true, false));
        installedPanel.setBackground(Color.white);

        availablePanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP, 0, 0, true, false));
        availablePanel.setBackground(Color.white);

        // Add TabbedPane
        add(tabbedPane, new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

        // Add Tabs
        tabbedPane.addTab("Installed", new JScrollPane(installedPanel));
        tabbedPane.addTab("Available", new JScrollPane(availablePanel));


        loadInstalledPlugins();

        tabbedPane.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent changeEvent) {
                if (tabbedPane.getSelectedIndex() == 1) {
                    if (true) {
                        loadAvailablePlugins();
                        loaded = true;
                    }
                }
            }
        });
    }

    private void loadInstalledPlugins() {
        PluginManager pluginManager = PluginManager.getInstance();
        List plugins = pluginManager.getPublicPlugins();
        Iterator iter = plugins.iterator();
        while (iter.hasNext()) {
            PublicPlugin plugin = (PublicPlugin)iter.next();
            final SparkPlugUI ui = new SparkPlugUI(plugin);
            ui.useLocalIcon();
            installedPanel.add(ui);
            addSparkPlugUIListener(ui);
        }
    }


    public void initialize() {
        // Add Plugins Menu
        JMenuBar menuBar = SparkManager.getMainWindow().getJMenuBar();
        int count = menuBar.getMenuCount();

        // Get last menu which is help
        JMenu sparkMenu = menuBar.getMenu(0);

        JMenuItem viewPluginsMenu = new JMenuItem();

        Action viewAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                invokeViewer();
            }
        };

        viewAction.putValue(Action.NAME, "Plugins");
        viewAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.PLUGIN_IMAGE));
        viewPluginsMenu.setAction(viewAction);

        sparkMenu.insert(viewPluginsMenu, 2);
    }

    private boolean uninstall(PublicPlugin plugin) {
        int ok = JOptionPane.showConfirmDialog(installedPanel, "Are you sure you want to uninstall \"" + plugin.getName() + "\"?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
            // Delete main jar.
            File pluginDir = plugin.getPluginDir();
            File pluginJAR = new File(plugin.getPluginDir().getParentFile(), pluginDir.getName() + ".jar");
            boolean deleted = pluginJAR.delete();

            JOptionPane.showMessageDialog(this, "You will need to restart Spark to have the changes take place.", "Reminder", JOptionPane.INFORMATION_MESSAGE);
            PluginManager.getInstance().removePublicPlugin(plugin);
            return true;
        }

        return false;
    }

    private void invokeViewer() {
        viewer = new PluginViewer();
        MessageDialog.showComponent("Plugins", "Plugins available and installed.", null, viewer, SparkManager.getMainWindow(), 600, 600, false);
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

        JLabel label = new JLabel("Loading. Please wait...");
        availablePanel.add(label);


        SwingWorker worker = new SwingWorker() {
            Collection pluginList = null;

            public Object construct() {
                // Prepare HTTP post
                final GetMethod post = new GetMethod(retrieveListURL);

                // Get HTTP client
                Protocol.registerProtocol("https", new Protocol("https", new EasySSLProtocolSocketFactory(), 443));
                final HttpClient httpclient = new HttpClient();
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

                // Execute request

                try {
                    int result = httpclient.executeMethod(post);
                    if (result != 200) {
                        return null;
                    }

                    pluginList = getPluginList(post.getResponseBodyAsStream());
                }
                catch (Exception ex) {
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

                    JOptionPane.showMessageDialog(availablePanel, "Unable to contact the plugin repository.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Iterator plugs = pluginList.iterator();
                availablePanel.removeAll();

                while (plugs.hasNext()) {
                    PublicPlugin plugin = (PublicPlugin)plugs.next();
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
        if (ModelUtil.hasLength(proxyHost) && ModelUtil.hasLength(proxyPort)) {
            try {
                httpclient.getHostConfiguration().setProxy(proxyHost, Integer.parseInt(proxyPort));
            }
            catch (NumberFormatException e) {
                Log.error(e);
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

            final JFrame frame = new JFrame("Downloading " + plugin.getName());

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
                        for (int i = 0; i < comps.length; i++) {
                            Component comp = comps[i];
                            if (comp instanceof SparkPlugUI) {
                                SparkPlugUI sparkPlug = (SparkPlugUI)comp;
                                if (sparkPlug.getPlugin().getDownloadURL().equals(plugin.getDownloadURL())) {
                                    availablePanel.remove(sparkPlug);

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

                    }
                    finally {
                        // Release current connection to the connection pool once you are done
                        post.releaseConnection();
                    }
                }
            });


            frame.getContentPane().setLayout(new GridBagLayout());
            frame.getContentPane().add(new JLabel("Downloading Spark-Plug"), new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
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


    public Collection getPluginList(InputStream response) {
        final List pluginList = new ArrayList();
        SAXReader saxReader = new SAXReader();
        Document pluginXML = null;

        try {
            pluginXML = saxReader.read(response);
        }
        catch (DocumentException e) {
            Log.error(e);
        }

        List plugins = pluginXML.selectNodes("/plugins/plugin");

        Iterator iter = plugins.iterator();
        while (iter.hasNext()) {
            PublicPlugin publicPlugin = new PublicPlugin();

            String clazz = null;
            String name = null;
            try {
                Element plugin = (Element)iter.next();


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

                    Node changeLog = plugin.selectSingleNode("changeLog");
                    if (changeLog != null) {
                        publicPlugin.setChangeLogAvailable(true);
                    }

                    Node readMe = plugin.selectSingleNode("readme");
                    if (readMe != null) {
                        publicPlugin.setReadMeAvailable(true);
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
                    System.out.println("We can ignore these.");
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
                progressBar.setValue(read);
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
                for (int i = 0; i < comps.length; i++) {
                    Component comp = (Component)comps[i];
                    if (comp instanceof SparkPlugUI) {
                        SparkPlugUI sparkPlug = (SparkPlugUI)comp;
                        sparkPlug.setSelected(false);
                    }
                }

                // Clear all selections
                comps = availablePanel.getComponents();
                for (int i = 0; i < comps.length; i++) {
                    Component comp = comps[i];
                    if (comp instanceof SparkPlugUI) {
                        SparkPlugUI sparkPlug = (SparkPlugUI)comp;
                        sparkPlug.setSelected(false);
                    }
                }

                ui.setSelected(true);

                final PluginManager pluginManager = PluginManager.getInstance();
                ui.getInstallButton().addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        boolean isInstalled = pluginManager.isInstalled(ui.getPlugin());
                        if (isInstalled) {
                            boolean uninstalled = uninstall(ui.getPlugin());
                            if (uninstalled) {
                                installedPanel.remove(ui);
                                installedPanel.invalidate();
                                installedPanel.repaint();
                                return;
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

    public void uninstall() {
        // Do nothing.
    }
}
