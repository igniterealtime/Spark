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
import org.jivesoftware.spark.util.StringUtils;
import org.jivesoftware.spark.util.URLFileSystem;
import org.jivesoftware.spark.SparkManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

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
        BrowserEngineManager bem = BrowserEngineManager.instance();
        //specific engine if you want and the engine you specified will return
        bem.setActiveEngine(BrowserEngineManager.MOZILLA);

        //IBrowserEngine be = bem.setActiveEngine(...);
        IBrowserEngine be = bem.getActiveEngine();//default or specified engine is returned

        // Note that the install directory is my name for temporary files and
        // not about mozilla. Me love Mozilla. 
        be.setEnginePath("C:\\crapola\\mozilla\\mozilla.exe");


        URL url = getClass().getResource("/themes/renkoo2.3/renkoo.AdiumMessageStyle");
        setTheme(URLFileSystem.url2File(url));
       //setTheme(new File("C:\\adium\\Satin.AdiumMessageStyle\\"));

        // Add Preference
        SparkManager.getPreferenceManager().addPreference(new ThemePreference());
    }

    public void setTheme(File theme) {
        theme = new File(theme, "/Contents/Resources");

        File template = new File(theme, "template.html");
        templateText = URLFileSystem.getContents(template);

        // Load header
        File header = new File(theme, "Header.html");
        if (header.exists()) {
            String headerText = URLFileSystem.getContents(header);
            headerText = html(headerText);
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
        incoming = incoming.replaceAll("%sender%", sender);
        incoming = incoming.replaceAll("%time%", time);
        incoming = incoming.replaceAll("%message%", message);
        incoming = incoming.replaceAll("%service%", "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");

        incoming = html(incoming);
        if (iconPath != null) {
            incoming = incoming.replaceAll("%userIconPath%", iconPath.toExternalForm());
        }
        return incoming;
    }

    public String getIncomingHistoryMessage(String sender, String time, String message, URL iconPath) {
        String incoming = incomingHistoryText;
        incoming = incoming.replaceAll("%sender%", sender);
        incoming = incoming.replaceAll("%time%", time);
        incoming = incoming.replaceAll("%message%", message);
        incoming = incoming.replaceAll("%service%", "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
        incoming = html(incoming);
        if (iconPath != null) {
            incoming = incoming.replaceAll("%userIconPath%", iconPath.toExternalForm());
        }
        return incoming;
    }


    public String getOutgoingMessage(String sender, String time, String message, URL iconPath) {
        String outgoing = outgoingText;
        outgoing = outgoing.replaceAll("%sender%", sender);
        outgoing = outgoing.replaceAll("%time%", time);
        outgoing = outgoing.replaceAll("%message%", message);
        outgoing = outgoing.replaceAll("%service%", "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
        outgoing = html(outgoing);
        if (iconPath != null) {
            outgoing = outgoing.replaceAll("%userIconPath%", iconPath.toExternalForm());
        }
        return outgoing;
    }

    public String getOutgoingHistoryMessage(String sender, String time, String message, URL iconPath) {
        String outgoing = outgoingHistoryText;
        outgoing = outgoing.replaceAll("%sender%", sender);
        outgoing = outgoing.replaceAll("%time%", time);
        outgoing = outgoing.replaceAll("%message%", message);
        outgoing = outgoing.replaceAll("%service%", "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
        outgoing = html(outgoing);
        if (iconPath != null) {
            outgoing = outgoing.replaceAll("%userIconPath%", iconPath.toExternalForm());
        }
        return outgoing;
    }

    public String getStatusMessage(String message, String time) {
        String status = statusText;
        status = status.replaceAll("%time%", time);
        status = status.replaceAll("%message%", message);
        status = html(status);
        return status;
    }

    public String getNotificationMessage(String message, boolean allowQuotes) {
        String status = statusText;
        status = status.replaceAll("%time%", "");
        status = status.replaceAll("%message%", message);
        if (!allowQuotes) {
            status = html(status);
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
        incoming = html(incoming);
        return incoming;
    }

    public String getNextIncomingHistoryMessage(String message, String time) {
        String incoming = nextIncomingHistoryText;
        incoming = incoming.replaceAll("%time%", time);
        incoming = incoming.replaceAll("%message%", message);
        incoming = html(incoming);
        return incoming;
    }

    public String getNextOutgoingMessage(String message, String time) {
        String out = nextOutgoingHistoryText;
        out = out.replaceAll("%time%", time);
        out = out.replaceAll("%message%", message);
        out = html(out);
        return out;
    }

    public String getNextOutgoingHistoryString(String message, String time) {
        String out = nextOutgoingHistoryText;
        out = out.replaceAll("%time%", time);
        out = out.replaceAll("%message%", message);
        out = html(out);
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


    private String html(String text) {
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
