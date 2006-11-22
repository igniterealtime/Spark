/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.spark.ui.themes;

import org.jdesktop.jdic.browser.BrowserEngineManager;
import org.jdesktop.jdic.browser.IBrowserEngine;
import org.jivesoftware.Spark;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.util.StringUtils;
import org.jivesoftware.spark.util.URLFileSystem;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.emoticons.EmoticonManager;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipFile;

/**
 * Manages Themes.
 *
 * @author Derek DeMoro
 * @todo FINISH :)
 */
public class ThemeManager {

    private static ThemeManager singleton;
    private static final Object LOCK = new Object();

    private StringBuilder builder = new StringBuilder();

    private String templateText;
    private String incomingText;
    private String outgoingText;
    private String statusText;
    private String nextIncomingText;
    private String nextOutgoingText;
    private String outgoingTransferText;
    private String incomingTransferText;

    private String incomingHistoryText;
    private String outgoingHistoryText;
    private String nextIncomingHistoryText;
    private String nextOutgoingHistoryText;

    private File tempFile;
    private String chatName;

    private EmoticonManager emoticonManager;

    /**
     * The root themes directory.
     */
    public static File THEMES_DIRECTORY;

    /**
     * Returns the singleton instance of <CODE>ThemeManager</CODE>,
     * creating it if necessary.
     * <p/>
     *
     * @return the singleton instance of <Code>ThemeManager</CODE>
     */
    public static ThemeManager getInstance() {
        // Synchronize on LOCK to ensure that we don't end up creating
        // two singletons.
        synchronized (LOCK) {
            if (null == singleton) {
                ThemeManager controller = new ThemeManager();
                singleton = controller;
                return controller;
            }
        }
        return singleton;
    }

    private ThemeManager() {
        emoticonManager = EmoticonManager.getInstance();

        BrowserEngineManager bem = BrowserEngineManager.instance();
        //specific engine if you want and the engine you specified will return
        bem.setActiveEngine(BrowserEngineManager.MOZILLA);

        //IBrowserEngine be = bem.setActiveEngine(...);
        IBrowserEngine be = bem.getActiveEngine();//default or specified engine is returned

        // Note that the install directory is my name for temporary files and
        // not about mozilla. Me love Mozilla. 
        //be.setEnginePath("C:\\mozilla\\mozilla.exe");

        File mozilla = new File(Spark.getBinDirectory(), "mozilla");
        be.setEnginePath(mozilla.getAbsolutePath());
        THEMES_DIRECTORY = new File(Spark.getBinDirectory().getParent(), "xtra/themes").getAbsoluteFile();

        // For Testing
        //THEMES_DIRECTORY = new File("c:\\xtra\\themes");

        expandNewThemes();

        final LocalPreferences pref = SettingsManager.getLocalPreferences();

        String themeName = pref.getTheme();

        File theme = new File(THEMES_DIRECTORY, themeName);

        try {
            setTheme(theme);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // Add Preference
        SparkManager.getPreferenceManager().addPreference(new ThemePreference());
    }

    private void expandNewThemes() {
        File[] jars = THEMES_DIRECTORY.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                boolean accept = false;
                String smallName = name.toLowerCase();
                if (smallName.endsWith(".zip")) {
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
                File directory = new File(THEMES_DIRECTORY, name);
                if (directory.exists() && directory.isDirectory()) {
                    continue;
                }
                else {
                    // Unzip contents into directory
                    unzipTheme(file, directory.getParentFile());
                }
            }
        }
    }

    public void installTheme(File theme) {
        // Copy the file to the themes directory
        unzipTheme(theme, THEMES_DIRECTORY);
    }

    /**
     * Unzips a theme from a ZIP file into a directory.
     *
     * @param zip the ZIP file
     * @param dir the directory to extract the plugin to.
     */
    private void unzipTheme(File zip, File dir) {
        try {
            ZipFile zipFile = new JarFile(zip);

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
            Log.error("Error unzipping Theme", e);
        }
    }


    public void setTheme(File theme) throws Exception {
        theme = new File(theme, "/Contents/Resources");

        File template = new File(theme, "template.html");
        templateText = URLFileSystem.getContents(template);

        // Load header
        File header = new File(theme, "Header.html");
        if (header.exists()) {
            String headerText = URLFileSystem.getContents(header);
            headerText = filter(headerText);
            templateText = templateText.replaceAll("%header%", headerText);
        }
        else {
            templateText = templateText.replaceAll("%header%", "");
        }

        // Load Footer
        File footer = new File(theme, "Footer.html");
        if (footer.exists()) {
            String footerText = URLFileSystem.getContents(footer);
            templateText = templateText.replaceAll("%footer%", footerText);
        }
        else {
            templateText = templateText.replaceAll("%footer%", "");
        }

        // Load Outgoing
        File outgoingMessage = new File(theme, "/Outgoing/Content.html");
        outgoingText = URLFileSystem.getContents(outgoingMessage);

        // Load Incoming
        File incomingMessage = new File(theme, "/Incoming/Content.html");
        incomingText = URLFileSystem.getContents(incomingMessage);

        // Load status
        File statusFile = new File(theme, "Status.html");
        statusText = URLFileSystem.getContents(statusFile);

        // Load Next Incoming Text
        File nextIncomingTextFile = new File(theme, "/Incoming/NextContent.html");
        nextIncomingText = URLFileSystem.getContents(nextIncomingTextFile);

        // Load Next Outgoing Text
        File nextOutgoingTextFile = new File(theme, "/Outgoing/NextContent.html");
        nextOutgoingText = URLFileSystem.getContents(nextOutgoingTextFile);

        // Set Base Href
        String baseHref = null;
        try {
            baseHref = theme.toURL().toExternalForm();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        baseHref = StringUtils.replace(baseHref, "\\", "\\\\");
        templateText = templateText.replaceAll("%base_href%", baseHref);

        tempFile = new File(theme, "temp.html");
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(tempFile));
            out.write(templateText);
            out.close();
        }
        catch (IOException e) {
        }

        // Load outgoing transfer text
        File outgoingTransferFile = new File(theme, "/Outgoing/filetransfer.html");
        outgoingTransferText = URLFileSystem.getContents(outgoingTransferFile);

        // Load incoming transfer text
        File incomingTransferFile = new File(theme, "/Incoming/filetransfer.html");
        incomingTransferText = URLFileSystem.getContents(incomingTransferFile);

        // Load incoming history text
        incomingHistoryText = URLFileSystem.getContents(new File(theme, "/Incoming/Context.html"));

        // Load outgoing history text
        outgoingHistoryText = URLFileSystem.getContents(new File(theme, "/Outgoing/Context.html"));

        // Load next incoming history text
        nextIncomingHistoryText = URLFileSystem.getContents(new File(theme, "/Incoming/NextContext.html"));

        // Load next outgoing history text
        nextOutgoingHistoryText = URLFileSystem.getContents(new File(theme, "/Outgoing/NextContext.html"));
    }

    public String getTemplate() {
        return templateText;
    }

    public URL getTemplateURL() {
        try {
            return tempFile.toURL();
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return null;
    }


    public String getIncomingMessage(String sender, String time, String message, URL iconPath) {
        String incoming = incomingText;
        if (sender == null) {
            sender = "";
        }
        incoming = incoming.replaceAll("%sender%", sender);
        incoming = incoming.replaceAll("%time%", time);
        incoming = incoming.replaceAll("%message%", message);
        incoming = incoming.replaceAll("%service%", "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");

        incoming = filter(incoming);
        if (iconPath != null) {
            incoming = incoming.replaceAll("%userIconPath%", iconPath.toExternalForm());
        }
        return incoming;
    }

    public String getIncomingHistoryMessage(String sender, String time, String message, URL iconPath) {
        String incoming = incomingHistoryText;
        if (sender == null) {
            sender = "";
        }
        incoming = incoming.replaceAll("%sender%", sender);
        incoming = incoming.replaceAll("%time%", time);
        incoming = incoming.replaceAll("%message%", message);
        incoming = incoming.replaceAll("%service%", "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
        incoming = filter(incoming);
        if (iconPath != null) {
            incoming = incoming.replaceAll("%userIconPath%", iconPath.toExternalForm());
        }
        return incoming;
    }


    public String getOutgoingMessage(String sender, String time, String message, URL iconPath) {
        String outgoing = outgoingText;
        if (sender == null) {
            sender = "";
        }
        outgoing = outgoing.replaceAll("%sender%", sender);
        outgoing = outgoing.replaceAll("%time%", time);
        outgoing = outgoing.replaceAll("%message%", message);
        outgoing = outgoing.replaceAll("%service%", "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
        outgoing = filter(outgoing);
        if (iconPath != null) {
            outgoing = outgoing.replaceAll("%userIconPath%", iconPath.toExternalForm());
        }
        return outgoing;
    }

    public String getOutgoingHistoryMessage(String sender, String time, String message, URL iconPath) {
        String outgoing = outgoingHistoryText;
        if (sender == null) {
            sender = "";
        }
        outgoing = outgoing.replaceAll("%sender%", sender);
        outgoing = outgoing.replaceAll("%time%", time);
        outgoing = outgoing.replaceAll("%message%", message);
        outgoing = outgoing.replaceAll("%service%", "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
        outgoing = filter(outgoing);
        if (iconPath != null) {
            outgoing = outgoing.replaceAll("%userIconPath%", iconPath.toExternalForm());
        }
        return outgoing;
    }

    public String getStatusMessage(String message, String time) {
        String status = statusText;
        status = status.replaceAll("%time%", time);
        status = status.replaceAll("%message%", message);
        status = filter(status);
        return status;
    }

    public String getNotificationMessage(String message, boolean allowQuotes) {
        String status = statusText;
        status = StringUtils.replace(status, "%time%", "");
        status = StringUtils.replace(status, "%message%", message);
        if (!allowQuotes) {
            status = filter(status);
        }
        else {
            status = status.replaceAll("\"", "\\\"");
            status = status.replaceAll("\n", "");
            status = status.replaceAll("\t", "");
            status = status.replaceAll("\r", "");
        }
        return status;
    }

    public String getNextIncomingMessage(String message, String time) {
        String incoming = nextIncomingText;
        incoming = incoming.replaceAll("%time%", time);
        incoming = incoming.replaceAll("%message%", message);
        incoming = filter(incoming);
        return incoming;
    }

    public String getNextIncomingHistoryMessage(String message, String time) {
        String incoming = nextIncomingHistoryText;
        incoming = incoming.replaceAll("%time%", time);
        incoming = incoming.replaceAll("%message%", message);
        incoming = filter(incoming);
        return incoming;
    }

    public String getNextOutgoingMessage(String message, String time) {
        String out = nextOutgoingHistoryText;
        out = out.replaceAll("%time%", time);
        out = out.replaceAll("%message%", message);
        out = filter(out);
        return out;
    }

    public String getNextOutgoingHistoryString(String message, String time) {
        String out = nextOutgoingHistoryText;
        out = out.replaceAll("%time%", time);
        out = out.replaceAll("%message%", message);
        out = filter(out);
        return out;
    }


    public String getIncomingTransferUI(String title, String filename, String size, String requestID) {
        String text = incomingTransferText;
        text = text.replaceAll("%title%", title);
        text = text.replaceAll("%filename%", filename);
        text = text.replaceAll("%filesize%", size);
        text = text.replaceAll("%requestID%", requestID);
        text = htmlKeepQuotes(text);
        return text;
    }

    public String htmlKeepQuotes(String text) {
        text = text.replaceAll("\n", "");
        text = text.replaceAll("\"", "\\\"");
        text = text.replaceAll("\t", "");
        text = text.replaceAll("\r", "");
        text = text.replaceAll("%userIconPath%", "file:///c:/zapwire_desktop.png");

        if (getChatName() != null) {
            text = text.replaceAll("%chatName%", getChatName());
        }

        final SimpleDateFormat formatter = new SimpleDateFormat("h:mm");
        String time = formatter.format(new Date());

        text = text.replaceAll("%timeOpened", time);
        return text;
    }


    private String filter(String text) {
        text = text.replaceAll("\n", "");
        text = text.replaceAll("\'", "&#180;");
        text = text.replaceAll("\t", "");
        text = text.replaceAll("\r", "");

        final SimpleDateFormat formatter = new SimpleDateFormat("h:mm");
        String time = formatter.format(new Date());

        text = text.replaceAll("%timeOpened", time);

        return text;
    }


    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public String getChatName() {
        return chatName;
    }


}
