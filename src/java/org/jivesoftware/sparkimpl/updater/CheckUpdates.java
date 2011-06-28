/**
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

package org.jivesoftware.sparkimpl.updater;

import com.thoughtworks.xstream.XStream;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.protocol.Protocol;
import org.jivesoftware.Spark;
import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.packet.DiscoverItems;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.ConfirmDialog;
import org.jivesoftware.spark.component.ConfirmDialog.ConfirmListener;
import org.jivesoftware.spark.component.TitlePanel;
import org.jivesoftware.spark.util.BrowserLauncher;
import org.jivesoftware.spark.util.ByteFormat;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.settings.JiveInfo;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.text.html.HTMLEditorKit;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.TimerTask;

public class CheckUpdates {
    private String mainUpdateURL;
    private JProgressBar bar;
    private TitlePanel titlePanel;
    private boolean downloadComplete = false;
    private boolean cancel = false;
    public static boolean UPDATING = false;
    private boolean sparkPluginInstalled;
    private XStream xstream = new XStream();
    private String sizeText;


    public CheckUpdates() {
        // Set the Jabber IQ Provider for Jabber:iq:spark
        ProviderManager.getInstance().addIQProvider("query", "jabber:iq:spark", new SparkVersion.Provider());

        // For simplicity, use an alias for the root xml tag
        xstream.alias("Version", SparkVersion.class);

        // Specify the main update url for JiveSoftware
        this.mainUpdateURL = "http://www.igniterealtime.org/updater/updater";

        sparkPluginInstalled = isSparkPluginInstalled(SparkManager.getConnection());
    }

    public SparkVersion newBuildAvailable() {
        if (!sparkPluginInstalled && !Spark.disableUpdatesOnCustom()) {
            // Handle Jivesoftware.org update
            return isNewBuildAvailableFromJivesoftware();
        }
        else if (sparkPluginInstalled) {
            try {
                SparkVersion serverVersion = getLatestVersion(SparkManager.getConnection());
                if (isGreater(serverVersion.getVersion(), JiveInfo.getVersion())) {
                    return serverVersion;
                }
            }
            catch (XMPPException e) {
                // Nothing to do
            }

        }

        return null;
    }


    /**
     * Returns true if there is a new build available for download.
     *
     * @return true if there is a new build available for download.
     */
    public SparkVersion isNewBuildAvailableFromJivesoftware() {
        PostMethod post = new PostMethod(mainUpdateURL);
        if (Spark.isWindows()) {
            post.addParameter("os", "windows");
        }
        else if (Spark.isMac()) {
            post.addParameter("os", "mac");
        }
        else {
            post.addParameter("os", "linux");
        }

//        Properties isBetaCheckingEnabled is now used to indicate if updates are allowed
//        // Check to see if the beta should be included.
//        LocalPreferences pref = SettingsManager.getLocalPreferences();
//        boolean isBetaCheckingEnabled = pref.isBetaCheckingEnabled();
//        if (isBetaCheckingEnabled) {
//            post.addParameter("beta", "true");
//        }


        Protocol.registerProtocol("https", new Protocol("https", new EasySSLProtocolSocketFactory(), 443));
        HttpClient httpclient = new HttpClient();
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
        try {
            int result = httpclient.executeMethod(post);
            if (result != 200) {
                return null;
            }


            String xml = post.getResponseBodyAsString();

            // Server Version
            SparkVersion serverVersion = (SparkVersion)xstream.fromXML(xml);
            if (isGreater(serverVersion.getVersion(), JiveInfo.getVersion())) {
                return serverVersion;
            }
        }
        catch (IOException e) {
            Log.error(e);
        }
        return null;
    }


    public void downloadUpdate(final File downloadedFile, final SparkVersion version) {
        final java.util.Timer timer = new java.util.Timer();

        // Prepare HTTP post
        final GetMethod post = new GetMethod(version.getDownloadURL());

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

            bar = new JProgressBar(0, contentLength);
        }
        catch (IOException e) {
            Log.error(e);
        }

        final JFrame frame = new JFrame(Res.getString("title.downloading.im.client"));

        frame.setIconImage(SparkRes.getImageIcon(SparkRes.SMALL_MESSAGE_IMAGE).getImage());

        titlePanel = new TitlePanel(Res.getString("title.upgrading.client"), Res.getString("message.version", version.getVersion()), SparkRes.getImageIcon(SparkRes.SEND_FILE_24x24), true);

        final Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    InputStream stream = post.getResponseBodyAsStream();
                    long size = post.getResponseContentLength();
                    ByteFormat formater = new ByteFormat();
                    sizeText = formater.format(size);
                    titlePanel.setDescription(Res.getString("message.version", version.getVersion()) + " \n" + Res.getString("message.file.size", sizeText));


                    downloadedFile.getParentFile().mkdirs();

                    FileOutputStream out = new FileOutputStream(downloadedFile);
                    copy(stream, out);
                    out.close();

                    if (!cancel) {
                        downloadComplete = true;
                        promptForInstallation(downloadedFile, Res.getString("title.download.complete"), Res.getString("message.restart.spark"));
                    }
                    else {
                        out.close();
                        downloadedFile.delete();
                    }


                    UPDATING = false;
                    frame.dispose();
                }
                catch (Exception ex) {
                    // Nothing to do
                }
                finally {
                    timer.cancel();
                    // Release current connection to the connection pool once you are done
                    post.releaseConnection();
                }
            }
        });


        frame.getContentPane().setLayout(new GridBagLayout());
        frame.getContentPane().add(titlePanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        frame.getContentPane().add(bar, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

        JEditorPane pane = new JEditorPane();
        boolean displayContentPane = version.getChangeLogURL() != null || version.getDisplayMessage() != null;

        try {
            pane.setEditable(false);
            if (version.getChangeLogURL() != null) {
                pane.setEditorKit(new HTMLEditorKit());
                pane.setPage(version.getChangeLogURL());
            }
            else if (version.getDisplayMessage() != null) {
                pane.setText(version.getDisplayMessage());
            }

            if (displayContentPane) {
                frame.getContentPane().add(new JScrollPane(pane), new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
            }
        }
        catch (IOException e) {
            Log.error(e);
        }

        frame.getContentPane().setBackground(Color.WHITE);
        frame.pack();
        if (displayContentPane) {
            frame.setSize(600, 400);
        }
        else {
            frame.setSize(400, 100);
        }
        frame.setLocationRelativeTo(SparkManager.getMainWindow());
        GraphicUtils.centerWindowOnScreen(frame);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                thread.interrupt();
                cancel = true;

                UPDATING = false;

                if (!downloadComplete) {
                    JOptionPane.showMessageDialog(SparkManager.getMainWindow(), Res.getString("message.updating.cancelled"), Res.getString("title.cancelled"), JOptionPane.ERROR_MESSAGE);
                }

            }
        });
        frame.setVisible(true);
        thread.start();


        timer.scheduleAtFixedRate(new TimerTask() {
            int seconds = 1;

            public void run() {
                ByteFormat formatter = new ByteFormat();
                long value = bar.getValue();
                long average = value / seconds;
                String text = formatter.format(average) + "/Sec";

                String total = formatter.format(value);
                titlePanel.setDescription(Res.getString("message.version", version.getVersion()) + " \n" + Res.getString("message.file.size", sizeText) + "\n" + Res.getString("message.transfer.rate") + ": " + text + "\n" + Res.getString("message.total.downloaded") + ": " + total);
                seconds++;
            }
        }, 1000, 1000);
    }

    /**
     * Common code for copy routines.  By convention, the streams are
     * closed in the same method in which they were opened.  Thus,
     * this method does not close the streams when the copying is done.
     *
     * @param in Source stream
     * @param out Destination stream
     */
    private void copy(final InputStream in, final OutputStream out) {
        int read = 0;

        try {
            final byte[] buffer = new byte[4096];
            while (!cancel) {
                int bytesRead = in.read(buffer);
                if (bytesRead < 0) {
                    break;
                }
                out.write(buffer, 0, bytesRead);
                read += bytesRead;
                bar.setValue(read);
            }
        }
        catch (IOException e) {
            Log.error(e);
        }
    }

    /**
     * Checks Spark Manager and/or Jive Software for the latest version of Spark.
     *
     * @param explicit true if the user explicitly asks for the latest version.
     * @throws Exception if there is an error during check
     */
    public void checkForUpdate(boolean explicit) throws Exception {
        if (UPDATING) {
            return;
        }

        UPDATING = true;

        if (isLocalBuildAvailable()) {
            return;
        }

        LocalPreferences localPreferences = SettingsManager.getLocalPreferences();

        //defaults to 7, 0=disabled
        int CheckForUpdates = localPreferences.getCheckForUpdates();
        if (CheckForUpdates == 0) {
            return;
        }

        Date lastChecked = localPreferences.getLastCheckForUpdates();
        if (lastChecked == null) {
            lastChecked = new Date();
            // This is the first invocation of Communicator
            localPreferences.setLastCheckForUpdates(lastChecked);
            SettingsManager.saveSettings();
        }

        // Check to see if it has been a CheckForUpdates (default 7) days
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(lastChecked);
        calendar.add(Calendar.DATE, CheckForUpdates);

        final Date lastCheckedPlusAPeriod = calendar.getTime();

        boolean periodOrLonger = new Date().getTime() >= lastCheckedPlusAPeriod.getTime();


        if (periodOrLonger || explicit || sparkPluginInstalled) {
            
            if (!explicit && !localPreferences.isBetaCheckingEnabled())
            {
                return;
            }
            // Check version on server.
            lastChecked = new Date();
            localPreferences.setLastCheckForUpdates(lastChecked);
            SettingsManager.saveSettings();
            
            final SparkVersion serverVersion = newBuildAvailable();
            if (serverVersion == null) {
                UPDATING = false;

                if (explicit) {
                    JOptionPane.showMessageDialog(SparkManager.getMainWindow(), Res.getString("message.no.updates"), Res.getString("title.no.updates"), JOptionPane.INFORMATION_MESSAGE);
                }
                return;
            }

            // Otherwise updates are available
            String downloadURL = serverVersion.getDownloadURL();
            String filename = downloadURL.substring(downloadURL.lastIndexOf("/") + 1);

            if (filename.indexOf('=') != -1) {
                filename = filename.substring(filename.indexOf('=') + 1);
            }

            // Set Download Directory 
            final File downloadDir = new File(Spark.getSparkUserHome(), "updates");
            downloadDir.mkdirs();

            // Set file to download.
            final File fileToDownload = new File(downloadDir, filename);
            if (fileToDownload.exists()) {
                fileToDownload.delete();
            }

            ConfirmDialog confirm = new ConfirmDialog();
            confirm.showConfirmDialog(SparkManager.getMainWindow(), Res.getString("title.new.version.available"),
                    Res.getString("message.new.spark.available", filename), Res.getString("yes"), Res.getString("no"),
                    null);
            confirm.setDialogSize(400, 300);
            confirm.setConfirmListener(new ConfirmListener() {
                public void yesOption() {
                    SwingWorker worker = new SwingWorker() {
                        public Object construct() {
                            try {
                                Thread.sleep(50);
                            }
                            catch (InterruptedException e) {
                                Log.error(e);
                            }
                            return "ok";
                        }

                        public void finished() {
                            if (Spark.isWindows()) {
                                downloadUpdate(fileToDownload, serverVersion);
                            }
                            else {
                                // Launch browser to download page.
                                try {
                                    if (sparkPluginInstalled) {
                                        BrowserLauncher.openURL(serverVersion.getDownloadURL());
                                    }
                                    else {
                                        BrowserLauncher.openURL("http://www.igniterealtime.org/downloads/index.jsp#spark");
                                    }
                                }

                                catch (Exception e) {
                                    Log.error(e);
                                }
                                UPDATING = false;
                            }
                        }

                    };
                    worker.start();
                }

                public void noOption() {
                    UPDATING = false;
                }
            });
        }
        else {
            UPDATING = false;
        }

    }


    /**
     * Returns true if the first version number is greater than the second.
     *
     * @param firstVersion  the first version number.
     * @param secondVersion the second version number.
     * @return returns true if the first version is greater than the second.
     */
    public static boolean isGreater(String firstVersion, String secondVersion) {
        int indexOne = firstVersion.indexOf("_");
        if (indexOne != -1) {
            firstVersion = firstVersion.substring(indexOne + 1);
        }

        int indexTwo = secondVersion.indexOf("_");
        if (indexTwo != -1) {
            secondVersion = secondVersion.substring(indexTwo + 1);
        }

        firstVersion = firstVersion.replaceAll(".online", "");
        secondVersion = secondVersion.replace(".online", "");

        boolean versionOneBetaOrAlpha = firstVersion.toLowerCase().contains("beta") || firstVersion.toLowerCase().contains("alpha");
        boolean versionTwoBetaOrAlpha = secondVersion.toLowerCase().contains("beta") || secondVersion.toLowerCase().contains("alpha");

        // Handle case where they are both betas / alphas
        if ((versionOneBetaOrAlpha && versionTwoBetaOrAlpha) || (!versionOneBetaOrAlpha && !versionTwoBetaOrAlpha)) {
            return firstVersion.compareTo(secondVersion) >= 1;
        }

        // Handle the case where version 1 is a beta or alpha
        if (versionOneBetaOrAlpha) {
            String versionOne = getVersion(firstVersion);
            return versionOne.compareTo(secondVersion) >= 1;
        }
        else if (versionTwoBetaOrAlpha) {
            String versionTwo = getVersion(secondVersion);
            int result = firstVersion.compareTo(versionTwo);
            return result >= 0;
        }


        return firstVersion.compareTo(secondVersion) >= 1;
    }

    public static String getVersion(String version) {
        int lastIndexOf = version.lastIndexOf(".");
        if (lastIndexOf != -1) {
            return version.substring(0, lastIndexOf);
        }

        return version;

    }

    /**
     * Returns the latest version of Spark available via Spark Manager or Jive Software.
     *
     * @param connection the XMPPConnection to use.
     * @return the information for about the latest Spark Client.
     * @throws XMPPException If unable to retrieve latest version.
     */
    public static SparkVersion getLatestVersion(XMPPConnection connection) throws XMPPException {
        SparkVersion request = new SparkVersion();
        request.setType(IQ.Type.GET);
        request.setTo("updater." + connection.getServiceName());

        PacketCollector collector = connection.createPacketCollector(new PacketIDFilter(request.getPacketID()));
        connection.sendPacket(request);


        SparkVersion response = (SparkVersion)collector.nextResult(SmackConfiguration.getPacketReplyTimeout());

        // Cancel the collector.
        collector.cancel();
        if (response == null) {
            throw new XMPPException("No response from server.");
        }
        if (response.getError() != null) {
            throw new XMPPException(response.getError());
        }
        return response;
    }

    /**
     * Does a service discvery on the server to see if a Spark Manager
     * is enabled.
     *
     * @param con the XMPPConnection to use.
     * @return true if Spark Manager is available.
     */
    public static boolean isSparkPluginInstalled(XMPPConnection con) {
        if (!con.isConnected()) {
            return false;
        }


        try {
            DiscoverItems items = SparkManager.getSessionManager().getDiscoveredItems();
            Iterator<DiscoverItems.Item> iter = items.getItems();
            while (iter.hasNext()) {
                DiscoverItems.Item item = (DiscoverItems.Item)iter.next();
                if ("Spark Updater".equals(item.getName())) {
                    return true;
                }
            }
        }
        catch (Exception e) {
            Log.error(e);
        }

        return false;

    }

    /**
     * Prompts the user to install the latest Spark.
     *
     * @param downloadedFile the location of the latest downloaded client.
     * @param title          the title
     * @param message        the message
     */
    private void promptForInstallation(final File downloadedFile, String title, String message) {
        ConfirmDialog confirm = new ConfirmDialog();
        confirm.showConfirmDialog(SparkManager.getMainWindow(), title,
                message, Res.getString("yes"), Res.getString("no"),
                null);
        confirm.setConfirmListener(new ConfirmListener() {
            public void yesOption() {
                try {
                    if (Spark.isWindows()) {
                        Runtime.getRuntime().exec(downloadedFile.getAbsolutePath());
                    }
                    else if (Spark.isMac()) {
                        Runtime.getRuntime().exec("open " + downloadedFile.getCanonicalPath());
                    }
                }
                catch (IOException e) {
                    Log.error(e);
                }
                SparkManager.getMainWindow().shutdown();
            }

            public void noOption() {

            }
        });
    }

    /**
     * Checks to see if a new version of Spark has already been downloaded by not installed.
     *
     * @return true if a newer version exists.
     */
    private boolean isLocalBuildAvailable() {
        // Check the bin directory for previous downloads. If there is a
        // newer version of Spark, ask if they wish to install.
        if (Spark.isWindows()) {
            File binDirectory = Spark.getBinDirectory();
            File[] files = binDirectory.listFiles();
            if (files != null) {
                int no = files.length;
                for (int i = 0; i < no; i++) {
                    File file = files[i];
                    String fileName = file.getName();
                    if (fileName.endsWith(".exe")) {
                        int index = fileName.indexOf("_");

                        // Add version number
                        String versionNumber = fileName.substring(index + 1);
                        int indexOfPeriod = versionNumber.indexOf(".");

                        versionNumber = versionNumber.substring(0, indexOfPeriod);
                        versionNumber = versionNumber.replaceAll("_online", "");
                        versionNumber = versionNumber.replaceAll("_", ".");

                        boolean isGreater = versionNumber.compareTo(JiveInfo.getVersion()) >= 1;
                        if (isGreater) {
                            // Prompt
                            promptForInstallation(file, Res.getString("title.new.client.available"), Res.getString("message.restart.spark.to.install"));
                            return true;
                        }
                        else {
                            file.delete();
                        }

                    }
                }
            }
        }

        return false;
    }


}
