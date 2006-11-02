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

import org.jdesktop.jdic.browser.WebBrowser;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.URLFileSystem;
import org.jivesoftware.spark.util.log.Log;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

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

    public void setTheme(File theme) {
        // Load Template
        File template = new File(theme, "template.html");
        templateText = URLFileSystem.getContents(template);

        // Load header
        File header = new File(theme, "Header.html");
        if (header.exists()) {
            String headerText = URLFileSystem.getContents(header);
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
    }

    public String getTemplate() {
        return templateText;
    }

    public String getIncomingMessage(String sender, String time, String message) {
        String incoming = incomingText;
        incoming = incoming.replaceAll("%sender%", sender);
        incoming = incoming.replaceAll("%time%", time);
        incoming = incoming.replaceAll("%message%", message);
        return incoming;
    }


    public static void main(String args[]) {
        File file = new File("C:\\adium\\pin\\Contents\\Resources");

        final ThemeManager themeManager = ThemeManager.getInstance();
        themeManager.setTheme(file);

        // Write out new template
        String tempTemplate = themeManager.getTemplate();

        File tempFile = new File(file, "temp.html");
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(tempFile));
            out.write(tempTemplate);
            out.close();
        }
        catch (IOException e) {
        }

        System.out.println(tempTemplate);


        final WebBrowser browser = new WebBrowser();
        try {
            browser.setURL(new File(file, "sample.html").toURL());
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        JFrame frame = new JFrame();
        frame.setLayout(new BorderLayout());

        final JScrollPane scrollpane = new JScrollPane(browser);
        frame.add(browser, BorderLayout.CENTER);

        JButton button = new JButton("Add");
        frame.add(button, BorderLayout.SOUTH);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String incomingText = themeManager.getIncomingMessage("Don Juan", "7 A.m.", "hello there fucker");
                incomingText = incomingText.replaceAll("\"", "");
                incomingText = incomingText.replaceAll("\n", "");
                System.out.println(incomingText);
                browser.executeScript("appendMessage('" + incomingText + "')");
                try {
                    JScrollBar sb = scrollpane.getVerticalScrollBar();
                    sb.setValue(sb.getMaximum());
                }
                catch (Exception ee) {
                    Log.error(ee);
                }
            }
        });

        frame.setSize(400, 400);
        GraphicUtils.centerWindowOnScreen(frame);
        frame.setVisible(true);
    }
}
