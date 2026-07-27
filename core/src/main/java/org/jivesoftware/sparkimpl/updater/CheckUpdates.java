/**
 * Copyright (C) 2004-2011 Jive Software, 2023 Ignite Realtime Foundation. All rights reserved.
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
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.jivesoftware.Spark;
import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.StanzaError;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.disco.packet.DiscoverItems;
import org.jivesoftware.spark.SessionManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.ConfirmDialog;
import org.jivesoftware.spark.component.ConfirmDialog.ConfirmListener;
import org.jivesoftware.spark.component.TitlePanel;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.*;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.settings.JiveInfo;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;

import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckUpdates {
    public static final String UPDATER_SERVICE_SUBDOMAIN = "updater.";
    private final String mainUpdateURL;
    private JProgressBar bar;
    private TitlePanel titlePanel;
    private volatile boolean downloadComplete = false;
    private volatile boolean cancel = false;
    public static boolean UPDATING = false;
    private final XStream xstream = new XStream();
    private String sizeText;

    public CheckUpdates() {
        // Set the Jabber IQ Provider for Jabber:iq:spark
        ProviderManager.addIQProvider(SparkVersion.ELEMENT, SparkVersion.NAMESPACE, new SparkVersion.Provider());
        // For simplicity, use an alias for the root xml tag
        xstream.allowTypes(new Class[] {
            SparkVersion.class,
        });
        xstream.alias("Version", SparkVersion.class);
        xstream.registerConverter(new InstantConverter());
        // Specify the main update url for JiveSoftware
        this.mainUpdateURL = "http://www.igniterealtime.org/updater/updater";
    }

    public SparkVersion newBuildAvailable() {
        SparkVersion serverVersion = null;
        if (isSparkPluginInstalled()) {
            serverVersion = getLatestVersion();
        }
        if (serverVersion == null && !Spark.disableUpdatesOnCustom()) {
            serverVersion = isNewBuildAvailableFromSite();
        }
        if (serverVersion == null) {
            return null;
        }
        return isGreater(serverVersion.getVersion(), JiveInfo.getVersion()) ? serverVersion : null;
    }

    /**
     * Returns a release version if there is a new build available for download.
     */
    public SparkVersion isNewBuildAvailableFromSite() {
        String os = currentOs();
        if (os == null) return null;

        //        Properties isBetaCheckingEnabled is now used to indicate if updates are allowed
        //        // Check to see if the beta should be included.
        //        LocalPreferences pref = SettingsManager.getLocalPreferences();
        //        boolean isBetaCheckingEnabled = pref.isBetaCheckingEnabled();
        //        if (isBetaCheckingEnabled) {
        //            post.addParameter("beta", "true");
        //        }
        try (final CloseableHttpClient httpClient =
                 HttpClients.custom().useSystemProperties()
                     .setConnectionManager(AcceptAllCertsConnectionManager.getInstance())
                     .build()
        ) {
            final ClassicHttpRequest request = ClassicRequestBuilder.post(mainUpdateURL)
                .addParameter("os", os)
                .setHeader("User-Agent", "Spark HttpFileUpload")
                .build();

            return httpClient.execute(request, httpResponse -> {
                if (httpResponse.getCode() == 200) {
                    String xml = EntityUtils.toString(httpResponse.getEntity());
                    // Server Version
                    SparkVersion serverVersion = (SparkVersion)xstream.fromXML(xml);
                    return serverVersion;
                }
                Log.warning("Bad status code for updates descriptor " + httpResponse.getCode());
                return null;
            });
        } catch (Exception e) {
            Log.error(e);
        }
        return null;
    }

    private static String currentOs() {
        if (Spark.isWindows()) {
            return "windows";
        } else if (Spark.isMac()) {
            return "mac";
        } else if (Spark.isLinux()) {
            return "linux";
        } else {
            return null;
        }
    }


    public void downloadUpdate(final File downloadedFile, final SparkVersion version) {
        cancel = false;
        downloadComplete = false;
        bar = new JProgressBar(0, 100);
        bar.setStringPainted(true);

        final JFrame frame = new JFrame(Res.getString("title.downloading.im.client"));
        frame.setIconImage(SparkRes.getImageIcon(SparkRes.Icon.SMALL_MESSAGE_IMAGE).getImage());
        titlePanel = new TitlePanel(
            Res.getString("title.upgrading.client"),
            Res.getString("message.version", version.getVersion()),
            SparkRes.getImageIcon(SparkRes.Icon.SEND_FILE_24x24),
            true
        );

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
            } else if (version.getDisplayMessage() != null) {
                pane.setText(version.getDisplayMessage());
            }
            if (displayContentPane) {
                frame.getContentPane().add(new JScrollPane(pane), new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
            }
        } catch (IOException e) {
            Log.warning("Unable to load Spark update information.", e);
        }

        frame.getContentPane().setBackground(Color.WHITE);
        frame.pack();
        frame.setSize(displayContentPane ? 600 : 400, displayContentPane ? 400 : 110);
        frame.setLocationRelativeTo(SparkManager.getMainWindow());
        GraphicUtils.centerWindowOnScreen(frame);

        final Thread thread = new Thread(() -> {
            final Path target = downloadedFile.toPath();
            final Path partial = target.resolveSibling(target.getFileName() + ".part");
            try {
                Files.createDirectories(target.getParent());
                Files.deleteIfExists(partial);

                final HttpGet request = new HttpGet(version.getDownloadURL());
                try (final CloseableHttpClient httpClient = HttpClients.custom()
                    .useSystemProperties()
                    .setConnectionManager(AcceptAllCertsConnectionManager.getInstance())
                    .build())
                {
                    httpClient.execute(request, response -> {
                        if (response.getCode() != 200) {
                            throw new IOException("Spark update download returned HTTP " + response.getCode());
                        }
                        final HttpEntity entity = response.getEntity();
                        final long contentLength = entity.getContentLength();
                        final ByteFormat formatter = new ByteFormat();
                        sizeText = contentLength >= 0 ? formatter.format(contentLength) : Res.getString("unknown");
                        SwingUtilities.invokeLater(() -> titlePanel.setDescription(
                            Res.getString("message.version", version.getVersion()) + "\n" +
                            Res.getString("message.file.size", sizeText)
                        ));
                        try (InputStream stream = entity.getContent(); OutputStream out = Files.newOutputStream(partial)) {
                            copy(stream, out, contentLength);
                        }
                        return null;
                    });
                }

                if (cancel) {
                    Files.deleteIfExists(partial);
                    return;
                }
                verifyChecksum(partial, version.getSha256());
                try {
                    Files.move(partial, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
                } catch (AtomicMoveNotSupportedException e) {
                    Files.move(partial, target, StandardCopyOption.REPLACE_EXISTING);
                }
                downloadComplete = true;
                SwingUtilities.invokeLater(() -> {
                    frame.dispose();
                    if (!cancel) {
                        promptForInstallation(downloadedFile, Res.getString("title.download.complete"), Res.getString("message.restart.spark"));
                    }
                });
            } catch (Exception ex) {
                try {
                    Files.deleteIfExists(partial);
                } catch (IOException cleanupError) {
                    Log.warning("Unable to delete incomplete Spark update " + partial, cleanupError);
                }
                if (cancel) {
                    return;
                }
                Log.error("Unable to download or validate Spark update.", ex);
                SwingUtilities.invokeLater(() -> {
                    frame.dispose();
                    JOptionPane.showMessageDialog(
                        SparkManager.getMainWindow(),
                        ex.getLocalizedMessage(),
                        Res.getString("title.error"),
                        JOptionPane.ERROR_MESSAGE
                    );
                });
            } finally {
                UPDATING = false;
            }
        }, "Spark update downloader");
        thread.setDaemon(true);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                cancel = true;
                thread.interrupt();
                UPDATING = false;
                if (!downloadComplete) {
                    JOptionPane.showMessageDialog(SparkManager.getMainWindow(), Res.getString("message.updating.cancelled"), Res.getString("title.cancelled"), JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        frame.setVisible(true);
        thread.start();
    }

    /**
     * Common code for copy routines.  By convention, the streams are
     * closed in the same method in which they were opened.  Thus,
     * this method does not close the streams when the copying is done.
     *
     * @param in Source stream
     * @param out Destination stream
     */
    private void copy(final InputStream in, final OutputStream out, final long expectedSize) throws IOException {
        long totalRead = 0;
        final byte[] buffer = new byte[64 * 1024];
        while (!cancel) {
            final int bytesRead = in.read(buffer);
            if (bytesRead < 0) {
                break;
            }
            out.write(buffer, 0, bytesRead);
            totalRead += bytesRead;
            if (expectedSize > 0) {
                final int progress = (int)Math.min(100L, totalRead * 100L / expectedSize);
                SwingUtilities.invokeLater(() -> bar.setValue(progress));
            }
        }
        if (cancel) {
            throw new IOException("Spark update download was cancelled");
        }
        if (expectedSize >= 0 && totalRead != expectedSize) {
            throw new IOException("Incomplete Spark update: expected " + expectedSize + " bytes, received " + totalRead);
        }
    }

    static void verifyChecksum(Path file, String expectedSha256) throws Exception {
        if (expectedSha256 == null || expectedSha256.isBlank()) {
            Log.warning("Spark update descriptor does not contain SHA-256. Integrity validation was skipped.");
            return;
        }
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (InputStream in = Files.newInputStream(file)) {
            final byte[] buffer = new byte[64 * 1024];
            int read;
            while ((read = in.read(buffer)) >= 0) {
                digest.update(buffer, 0, read);
            }
        }
        final StringBuilder actual = new StringBuilder(64);
        for (byte value : digest.digest()) {
            actual.append(String.format("%02x", value));
        }
        if (!actual.toString().equalsIgnoreCase(expectedSha256.trim())) {
            throw new IOException("Spark update SHA-256 mismatch. Expected " + expectedSha256 + ", received " + actual);
        }
    }

    /**
     * Checks Spark Manager and/or Jive Software for the latest version of Spark.
     *
     * @param explicit true if the user explicitly asks for the latest version.
     */
    public void checkForUpdate(boolean explicit) {
        if (UPDATING) {
            return;
        }
        UPDATING = true;
        if (isLocalBuildAvailable()) {
            UPDATING = false;
            return;
        }
        LocalPreferences localPreferences = SettingsManager.getLocalPreferences();
        final boolean serverUpdaterAvailable = isSparkPluginInstalled();
        //defaults to 7, 0=disabled
        int CheckForUpdates = localPreferences.getCheckForUpdates();
        if (CheckForUpdates == 0) {
            UPDATING = false;
            return;
        }

        Instant lastChecked = localPreferences.getLastCheckForUpdates();
        if (lastChecked == null) {
            lastChecked = Instant.now();
            // This is the first invocation of Communicator
            localPreferences.setLastCheckForUpdates(lastChecked);
        }

        // Check to see if it has been a CheckForUpdates (default 7) days
        Instant lastCheckedPlusAPeriod = lastChecked.plus(CheckForUpdates, ChronoUnit.DAYS);
        boolean periodOrLonger = Instant.now().isAfter(lastCheckedPlusAPeriod);
        if (!periodOrLonger && !explicit && !serverUpdaterAvailable) {
            UPDATING = false;
            return;
        }
            // Check version on server.
            localPreferences.setLastCheckForUpdates(Instant.now());
            final SparkVersion serverVersion = newBuildAvailable();
            if (serverVersion == null) {
                UPDATING = false;
                if (explicit) {
                    JOptionPane.showMessageDialog(SparkManager.getMainWindow(), Res.getString("message.no.updates"), Res.getString("title.no.updates"), JOptionPane.INFORMATION_MESSAGE);
                }
                return;
            }
            if (!explicit && isPreRelease(serverVersion.getVersion()) && !localPreferences.isBetaCheckingEnabled()) {
                UPDATING = false;
                return;
            }

            // Otherwise updates are available
            String downloadURL = serverVersion.getDownloadURL();
            String filename = determineFileName(serverVersion);
            // Set Download Directory
            final File downloadDir = new File(Spark.getSparkUserHome(), "updates");
            //noinspection ResultOfMethodCallIgnored
            downloadDir.mkdirs();
            // Set file to download.
            final File fileToDownload = new File(downloadDir, filename);
            if (fileToDownload.exists()) {
                //noinspection ResultOfMethodCallIgnored
                fileToDownload.delete();
            }

            ConfirmDialog confirm = new ConfirmDialog();
            confirm.showConfirmDialog(SparkManager.getMainWindow(), Res.getString("title.new.version.available"),
                    Res.getString("message.new.spark.available", filename), Res.getString("yes"), Res.getString("no"),
                    null);
            confirm.setDialogSize(400, 300);
            confirm.setConfirmListener(new ConfirmListener() {
                @Override
				public void yesOption() {
                    SwingWorker worker = new SwingWorker() {
                        @Override
						public Object construct() {
                            try {
                                Thread.sleep(50);
                            }
                            catch (InterruptedException e) {
                                Log.error(e);
                            }
                            return "ok";
                        }

                        @Override
						public void finished() {
                            if (Spark.isWindows() || Spark.isMac()) {
                                downloadUpdate(fileToDownload, serverVersion);
                            }
                            else {
                                // Launch browser to download page.
                                if (isSparkPluginInstalled()) {
                                    BrowserLauncher.openURL(serverVersion.getDownloadURL());
                                } else {
                                    BrowserLauncher.openURL("https://igniterealtime.org/downloads/index.jsp#spark");
                                }
                                UPDATING = false;
                            }
                        }
                    };
                    worker.start();
                }

                @Override
				public void noOption() {
                    UPDATING = false;
                }
            });
    }

    /**
     * Returns true if the first version number is greater than the second.
     *
     * @param firstVersion  the first version number.
     * @param secondVersion the second version number.
     * @return returns true if the first version is greater than the second.
     */
    public static boolean isGreater(String firstVersion, String secondVersion) {
        return compareVersions(firstVersion, secondVersion) > 0;
    }

    static int compareVersions(String firstVersion, String secondVersion) {
        final ParsedVersion first = ParsedVersion.parse(firstVersion);
        final ParsedVersion second = ParsedVersion.parse(secondVersion);
        final int count = Math.max(first.numbers.size(), second.numbers.size());
        for (int i = 0; i < count; i++) {
            final int left = i < first.numbers.size() ? first.numbers.get(i) : 0;
            final int right = i < second.numbers.size() ? second.numbers.get(i) : 0;
            if (left != right) {
                return Integer.compare(left, right);
            }
        }
        if (first.qualifierRank != second.qualifierRank) {
            return Integer.compare(first.qualifierRank, second.qualifierRank);
        }
        return Integer.compare(first.qualifierNumber, second.qualifierNumber);
    }

    static boolean isPreRelease(String version) {
        return ParsedVersion.parse(version).qualifierRank < ParsedVersion.RELEASE_RANK;
    }

    public static String getVersion(String version) {
        return ParsedVersion.parse(version).normalized;
    }

    static String determineFileName(SparkVersion version) {
        if (version.getFileName() != null && !version.getFileName().isBlank()) {
            return new File(version.getFileName()).getName();
        }
        try {
            final URI uri = URI.create(version.getDownloadURL());
            final String query = uri.getRawQuery();
            if (query != null) {
                for (String parameter : query.split("&")) {
                    final int separator = parameter.indexOf('=');
                    if (separator > 0 && parameter.substring(0, separator).equals("client")) {
                        return new File(URLDecoder.decode(parameter.substring(separator + 1), StandardCharsets.UTF_8)).getName();
                    }
                }
            }
            final String path = uri.getPath();
            if (path != null && path.contains("/")) {
                final String name = path.substring(path.lastIndexOf('/') + 1);
                if (!name.isBlank()) {
                    return name;
                }
            }
        } catch (Exception e) {
            Log.warning("Unable to determine Spark update filename from " + version.getDownloadURL(), e);
        }
        return "spark-update.bin";
    }

    private static final class ParsedVersion {
        private static final int RELEASE_RANK = 5;
        private static final Pattern PATTERN = Pattern.compile(
            "(\\d+(?:[._]\\d+){1,3})(?:[-._]?(snapshot|alpha|beta|rc)(\\d*)?)?",
            Pattern.CASE_INSENSITIVE
        );

        private final List<Integer> numbers;
        private final int qualifierRank;
        private final int qualifierNumber;
        private final String normalized;

        private ParsedVersion(List<Integer> numbers, int qualifierRank, int qualifierNumber, String normalized) {
            this.numbers = numbers;
            this.qualifierRank = qualifierRank;
            this.qualifierNumber = qualifierNumber;
            this.normalized = normalized;
        }

        private static ParsedVersion parse(String value) {
            final String input = value == null ? "0.0.0" : value.toLowerCase(Locale.ROOT);
            final Matcher matcher = PATTERN.matcher(input);
            if (!matcher.find()) {
                return new ParsedVersion(List.of(0), 0, 0, "0");
            }
            final String numeric = matcher.group(1).replace('_', '.');
            final List<Integer> numbers = new ArrayList<>();
            for (String part : numeric.split("\\.")) {
                numbers.add(Integer.parseInt(part));
            }
            final String qualifier = matcher.group(2);
            final int rank;
            if (qualifier == null) {
                rank = RELEASE_RANK;
            } else {
                switch (qualifier.toLowerCase(Locale.ROOT)) {
                    case "rc": rank = 4; break;
                    case "beta": rank = 3; break;
                    case "alpha": rank = 2; break;
                    case "snapshot": rank = 1; break;
                    default: rank = 0;
                }
            }
            final String qualifierNumberText = matcher.group(3);
            final int qualifierNumber = qualifierNumberText == null || qualifierNumberText.isBlank() ? 0 : Integer.parseInt(qualifierNumberText);
            final String qualifierSuffix = qualifierNumberText == null ? "" : qualifierNumberText;
            final String normalized = numeric + (qualifier == null ? "" : "-" + qualifier + qualifierSuffix);
            return new ParsedVersion(numbers, rank, qualifierNumber, normalized);
        }
    }

    /**
     * Returns the latest version of Spark available via Spark Manager or website.
     *
     * @return the information for about the latest Spark Client or null.
     */
    public static SparkVersion getLatestVersion()
    {
        AbstractXMPPConnection connection = SparkManager.getConnection();
        SparkVersion request = new SparkVersion();
        request.setType(IQ.Type.get);
        request.setTo(JidCreate.fromOrThrowUnchecked(UPDATER_SERVICE_SUBDOMAIN + connection.getXMPPServiceDomain()));
        try {
            SparkVersion response = connection.sendIqRequestAndWaitForResponse(request);
            return response;
        } catch (XMPPException.XMPPErrorException e) {
            if (e.getStanzaError().getCondition() == StanzaError.Condition.item_not_found) {
                Log.debug("no new version available");
                return null;
            }
            Log.warning("Unable the check for new build.", e);
            return null;
        }  catch (Exception e) {
            Log.error("Unable the check for new build.", e);
            return null;
        }
    }

    /**
     * Does a service discovery on the server to see if a Client Control plugin is enabled.
     *
     * @return true if Spark Manager is available.
     */
    public static boolean isSparkPluginInstalled() {
        XMPPConnection con = SparkManager.getConnection();
        if (!con.isConnected()) {
            return false;
        }
        try {
            SessionManager sessionManager = SparkManager.getSessionManager();
            DomainBareJid serverDomain = sessionManager.getServerAddress();
            var items = sessionManager.getDiscoveredItems();
            Jid serviceJid = JidCreate.fromOrNull(UPDATER_SERVICE_SUBDOMAIN + serverDomain);
            DiscoverItems.Item item = items.get(serviceJid);
            if (item != null) {
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
            @Override
            public void yesOption() {
                boolean launched = false;
                try {
                    if (Spark.isWindows()) {
                        new ProcessBuilder(downloadedFile.getAbsolutePath()).start();
                        launched = true;
                    } else if (Spark.isMac()) {
                        new ProcessBuilder("open", downloadedFile.getCanonicalPath()).start();
                        launched = true;
                    }
                } catch (IOException e) {
                    Log.error("Unable to launch Spark update installer.", e);
                    JOptionPane.showMessageDialog(SparkManager.getMainWindow(), e.getLocalizedMessage(), Res.getString("title.error"), JOptionPane.ERROR_MESSAGE);
                }
                if (launched) {
                    SparkManager.getMainWindow().shutdown();
                }
            }

            @Override
            public void noOption() {
                // Keep the downloaded package for a later manual installation.
            }
        });
    }

    /**
     * Checks to see if a new version of Spark has already been downloaded by not installed.
     */
    private boolean isLocalBuildAvailable() {
        // Check the bin directory for previous downloads. If there is a
        // newer version of Spark, ask if they wish to install.
            File binDirectory = Spark.getBinDirectory();
            File[] files = binDirectory.listFiles(File::isFile);
            if (files == null) {
                return false;
            }
            for (File file : files) {
                String fileName = file.getName();
                if (Spark.isWindows() && fileName.endsWith(".exe") ||
                    Spark.isMac() && fileName.endsWith(".dmg")) {
                    if (isIsGreater(fileName)) {
                        // Prompt
                        promptForInstallation(file, Res.getString("title.new.client.available"), Res.getString("message.restart.spark.to.install"));
                        return true;
                    } else {
                        //noinspection ResultOfMethodCallIgnored
                        file.delete();
                    }
                }
            }
        return false;
    }

    private static boolean isIsGreater(String fileName) {
        int index = fileName.indexOf('_');
        // Add version number
        String versionNumber = fileName.substring(index + 1);
        int indexOfPeriod = versionNumber.indexOf('.');
        versionNumber = versionNumber.substring(0, indexOfPeriod);
        versionNumber = versionNumber.replace("_online", "");
        versionNumber = versionNumber.replace("_", ".");
        boolean isGreater = versionNumber.compareTo(JiveInfo.getVersion()) >= 1;
        return isGreater;
    }

}
