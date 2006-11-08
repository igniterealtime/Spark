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
import org.jdesktop.jdic.browser.WebBrowser;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.StringUtils;
import org.jivesoftware.spark.util.URLFileSystem;

import javax.swing.JFrame;

import java.awt.BorderLayout;
import java.io.File;
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
        // URL url = getClass().getResource("/themes/pin");
        File file = new File("C:\\adium\\renkoo2.3\\renkoo.AdiumMessageStyle");
        setTheme(file);
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

        // Load outgoing transfer text
        File outgoingTransferFile = new File(theme, "/Outgoing/filetransfer.html");
        outgoingTransferText = URLFileSystem.getContents(outgoingTransferFile);

        // Load incoming transfer text
        File incomingTransferFile = new File(theme, "/Incoming/filetransfer.html");
        incomingTransferText = URLFileSystem.getContents(incomingTransferFile);
    }

    public String getTemplate(String chatName) {
        SimpleDateFormat formatter = new SimpleDateFormat("MMMMM, d,yyyy");
        String time = formatter.format(new Date());

        String text = templateText;
        text = text.replaceAll("%timeOpened%", time);
        text = text.replaceAll("%chatOpened%", chatName);

        return text;
    }

    public String getTemplate() {
        return templateText;
    }

    public String getIncomingMessage(String sender, String time, String message) {
        String incoming = incomingText;
        incoming = incoming.replaceAll("%sender%", sender);
        incoming = incoming.replaceAll("%time%", time);
        incoming = incoming.replaceAll("%message%", message);
        incoming = html(incoming);
        return incoming;
    }

    public String getOutgoingMessage(String sender, String time, String message) {
        String outgoing = outgoingText;
        outgoing = outgoing.replaceAll("%sender%", sender);
        outgoing = outgoing.replaceAll("%time%", time);
        outgoing = outgoing.replaceAll("%message%", message);
        outgoing = html(outgoing);
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

    public String getNextOutgoingMessage(String message, String time) {
        String out = nextOutgoingText;
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

        String timestamp = findTimeStamp(text);
        if (timestamp != null) {
            String newTimestamp = getTimeStamp(timestamp);
            text = StringUtils.replace(text, timestamp, newTimestamp);
        }
        return text;
    }


    private String html(String text) {
        text = text.replaceAll("\n", "");
        text = text.replaceAll("\'", "&#180;");
        text = text.replaceAll("\t", "");
        text = text.replaceAll("\r", "");
        text = text.replaceAll("%userIconPath%", "file:///c:/zapwire_desktop.png");

        if (getChatName() != null) {
            text = text.replaceAll("%chatName%", getChatName());
        }

        String timestamp = findTimeStamp(text);
        if (timestamp != null) {
            String newTimestamp = getTimeStamp(timestamp);
            text = StringUtils.replace(text, timestamp, newTimestamp);
        }
        return text;
    }


    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public String getChatName() {
        return chatName;
    }

    public String findTimeStamp(String text) {
        int index = text.indexOf("%timeOpened{");
        if (index != 1) {
            int index2 = text.indexOf("}");
            if (index2 != -1) {
                String timestamp = text.substring(index, index2 + 2);
                return timestamp;
            }
        }

        return null;
    }


    public String getTimeStamp(String timestamp) {
        //%timeOpened{%B %e, %Y}%
        String token = "%timeOpened{";
        int index = timestamp.indexOf("%timeOpened{");
        if (index != 1) {
            int index2 = timestamp.indexOf("}");
            if (index2 != -1) {
                String inner = timestamp.substring(index + token.length(), index2);

                // Do the replacements
                inner = inner.replace("%B", "MMMMM");
                inner = inner.replace("%e", "d");
                inner = inner.replace("%Y", "yyyy");

                SimpleDateFormat formatter = new SimpleDateFormat(inner);
                String time = formatter.format(new Date());
                return time;
            }
        }

        return "November 3, 2006";
    }


    public static void main(String args[]) {
        File tempFile = new File("C:\\Demo.html");
        String contents = URLFileSystem.getContents(tempFile);

        BrowserEngineManager bem = BrowserEngineManager.instance();
        //specific engine if you want and the engine you specified will return
        bem.setActiveEngine(BrowserEngineManager.MOZILLA);

        //IBrowserEngine be = bem.setActiveEngine(...);
        IBrowserEngine be = bem.getActiveEngine();//default or specified engine is returned
        be.setEnginePath("C:\\crapoloa\\mozilla\\mozilla.exe");
        final WebBrowser browser = new WebBrowser();


        browser.setContent(contents);


        JFrame frame = new JFrame();
        frame.setLayout(new BorderLayout());

        frame.add(browser.asComponent(), BorderLayout.CENTER);


        frame.setSize(400, 400);
        GraphicUtils.centerWindowOnScreen(frame);
        frame.setVisible(true);
    }
}
