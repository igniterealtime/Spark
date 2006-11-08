/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.spark.ui;

import org.jdesktop.jdic.browser.BrowserEngineManager;
import org.jdesktop.jdic.browser.IBrowserEngine;
import org.jdesktop.jdic.browser.WebBrowser;
import org.jdesktop.jdic.browser.WebBrowserEvent;
import org.jdesktop.jdic.browser.WebBrowserListener;
import org.jivesoftware.Spark;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.packet.DelayInformation;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.VerticalFlowLayout;
import org.jivesoftware.spark.ui.themes.ThemeManager;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * The <CODE>TranscriptWindow</CODE> class. Provides a default implementation
 * of a Chat Window. In general, extensions could override this class
 * to offer more support within the chat, but should not be necessary.
 */
public class TranscriptWindow extends JPanel {

    private List<TranscriptWindowInterceptor> interceptors = new ArrayList<TranscriptWindowInterceptor>();

    private Date lastUpdated;

    private WebBrowser browser;

    private ThemeManager themeManager;

    private String activeUser;

    private boolean documentLoaded;

    private JPanel extraPanel = new JPanel();

    /**
     * Creates a default instance of <code>TranscriptWindow</code>.
     */
    public TranscriptWindow() {
        setLayout(new BorderLayout());

        themeManager = ThemeManager.getInstance();

        browser = new WebBrowser();

        browser.setURL(themeManager.getTemplateURL());

        browser.addWebBrowserListener(new WebBrowserListener() {
            public void downloadStarted(WebBrowserEvent webBrowserEvent) {
            }

            public void downloadCompleted(WebBrowserEvent webBrowserEvent) {

            }

            public void downloadProgress(WebBrowserEvent webBrowserEvent) {
            }

            public void downloadError(WebBrowserEvent webBrowserEvent) {
            }

            public void documentCompleted(WebBrowserEvent webBrowserEvent) {
                documentLoaded = true;
            }

            public void titleChange(WebBrowserEvent webBrowserEvent) {
            }

            public void statusTextChange(WebBrowserEvent webBrowserEvent) {
            }


            public void initializationCompleted(WebBrowserEvent webBrowserEvent) {
            }
        });

        add(browser, BorderLayout.CENTER);

        extraPanel.setBackground(Color.white);
        extraPanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP, 0, 0, true, false));

        add(extraPanel, BorderLayout.SOUTH);
    }


    /**
     * Create and insert a message from the current user.
     *
     * @param userid  the userid of the current user.
     * @param message the message to insert.
     */
    public void insertMessage(String userid, Message message) {
        // Check interceptors.
        for (TranscriptWindowInterceptor interceptor : interceptors) {
            boolean handled = interceptor.handleInsertMessage(userid, message);
            if (handled) {
                // Do nothing.
                return;
            }
        }

        String body = message.getBody();
        String date = getDate(null);


        if (userid.equals(activeUser)) {
            String text = themeManager.getNextOutgoingMessage(body, date);
            executeScript("appendNextMessage('" + text + "')");
        }
        else {
            String text = themeManager.getOutgoingMessage(userid, date, body);
            executeScript("appendMessage('" + text + "')");
        }

        activeUser = userid;
    }


    public void insertCustomMessage(String prefix, String message) {
        String text = themeManager.getOutgoingMessage(prefix, "", message);
        executeScript("appendMessage('" + text + "')");
    }

    public void insertCustomOtherMessage(String prefix, String message) {
        String text = themeManager.getIncomingMessage(prefix, "", message);
        executeScript("appendMessage('" + text + "')");
    }

    /**
     * Create and insert a message from a customer.
     *
     * @param userid  the userid of the customer.
     * @param message the message from the customer.
     */
    public void insertOthersMessage(String userid, Message message) {
        // Check interceptors.
        for (TranscriptWindowInterceptor in : interceptors) {
            boolean handled = in.handleOtherMessage(userid, message);
            if (handled) {
                // Do nothing.
                return;
            }
        }

        String body = message.getBody();

        try {
            DelayInformation inf = (DelayInformation)message.getExtension("x", "jabber:x:delay");
            Date sentDate = null;
            if (inf != null) {
                sentDate = inf.getStamp();

                body = "(Offline) " + body;
            }
            else {
                sentDate = new Date();
            }

            String theDate = getDate(sentDate);

            if (userid.equals(activeUser)) {
                String text = themeManager.getNextIncomingMessage(body, theDate);
                executeScript("appendNextMessage('" + text + "')");
            }
            else {
                String text = themeManager.getIncomingMessage(userid, theDate, body);
                executeScript("appendMessage('" + text + "')");
            }

            activeUser = userid;
        }
        catch (Exception ex) {
            Log.error("Error message.", ex);
        }
    }


    /**
     * Create and insert a notification message. A notification message generally is a
     * presence update, but can be used for most anything related to the room.
     *
     * @param message the information message to insert.
     */
    public synchronized void insertNotificationMessage(String message) {
        String text = themeManager.getStatusMessage(message, "");
        executeScript("appendMessage('" + text + "')");
    }

    /**
     * Creates and inserts an error message.
     *
     * @param message the information message to insert.
     */
    public void insertErrorMessage(String message) {
        String text = themeManager.getStatusMessage(message, "");
        executeScript("appendMessage('" + text + "')");
    }

    /**
     * Create and insert a question message. A question message is specified by the
     * end customer during the initial request.
     *
     * @param question the question asked by the customer.
     */
    public void insertQuestionMessage(String question) {
        String text = themeManager.getStatusMessage(question, "");
        executeScript("appendMessage('" + text + "')");
    }

    public void insertHTML(String html) {
        executeScript("appendMessage('" + html + "')");
    }


    /**
     * Returns the formatted date.
     *
     * @param insertDate the date to format.
     * @return the formatted date.
     */
    private String getDate(Date insertDate) {
        final LocalPreferences pref = SettingsManager.getLocalPreferences();

        if (insertDate == null) {
            insertDate = new Date();
        }


        if (pref.isTimeDisplayedInChat()) {
            final SimpleDateFormat formatter = new SimpleDateFormat("h:mm");
            return formatter.format(insertDate);
        }
        lastUpdated = insertDate;
        return "";
    }


    /**
     * Return the last time the <code>TranscriptWindow</code> was updated.
     *
     * @return the last time the <code>TranscriptWindow</code> was updated.
     */
    public Date getLastUpdated() {
        return lastUpdated;
    }

    /**
     * Inserts a history message.
     *
     * @param userid  the userid of the sender.
     * @param message the message to insert.
     * @param date    the Date object created when the message was delivered.
     */
    public void insertHistoryMessage(String userid, String message, Date date) {
        final SimpleDateFormat formatter = new SimpleDateFormat("h:mm");
        String time = formatter.format(date);

        if (userid.equals(activeUser)) {
            String text = themeManager.getNextOutgoingMessage(message, time);
            executeScript("appendNextMessage('" + text + "')");
        }
        else {
            String text = themeManager.getOutgoingMessage(userid, time, message);
            executeScript("appendMessage('" + text + "')");
        }

        activeUser = userid;
    }

    /**
     * Disable the entire <code>TranscriptWindow</code> and visually represent
     * it as disabled.
     */
    public void showDisabledWindowUI() {

    }

    /**
     * Persist a current transcript.
     *
     * @param fileName   the name of the file to save the transcript as. Note: This can be modified by the user.
     * @param transcript the collection of transcript.
     * @param headerData the string to prepend to the transcript.
     * @see ChatRoom#getTranscripts()
     */
    public void saveTranscript(String fileName, List transcript, String headerData) {
        final LocalPreferences pref = SettingsManager.getLocalPreferences();

        try {
            SimpleDateFormat formatter;

            File defaultSaveFile = new File(new File(Spark.getUserHome()), fileName);
            final JFileChooser fileChooser = new JFileChooser(defaultSaveFile);
            fileChooser.setSelectedFile(defaultSaveFile);

            // Show save dialog; this method does not return until the dialog is closed
            int result = fileChooser.showSaveDialog(this);
            final File selFile = fileChooser.getSelectedFile();

            if (selFile != null && result == JFileChooser.APPROVE_OPTION) {
                final StringBuffer buf = new StringBuffer();
                final Iterator transcripts = transcript.iterator();
                buf.append("<html><body>");
                if (headerData != null) {
                    buf.append(headerData);
                }

                buf.append("<table width=600>");
                while (transcripts.hasNext()) {
                    final Message message = (Message)transcripts.next();
                    String from = message.getFrom();
                    if (from == null) {
                        from = pref.getNickname();
                    }

                    if (Message.Type.GROUP_CHAT == message.getType()) {
                        if (ModelUtil.hasLength(StringUtils.parseResource(from))) {
                            from = StringUtils.parseResource(from);
                        }
                    }

                    final String body = message.getBody();
                    final Date insertionDate = (Date)message.getProperty("insertionDate");
                    formatter = new SimpleDateFormat("hh:mm:ss");

                    String value = "";
                    if (insertionDate != null) {
                        value = "[" + formatter.format(insertionDate) + "] ";
                    }
                    buf.append("<tr><td nowrap><font size=2>").append(value).append("<strong>").append(from).append(":</strong>&nbsp;").append(body).append("</font></td></tr>");

                }
                buf.append("</table></body></html>");
                final BufferedWriter writer = new BufferedWriter(new FileWriter(selFile));
                writer.write(buf.toString());
                writer.close();
                JOptionPane.showMessageDialog(SparkManager.getMainWindow(), "Chat transcript has been saved.",
                        "Chat Transcript Saved", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        catch (Exception ex) {
            Log.error("Unable to save chat transcript.", ex);
            JOptionPane.showMessageDialog(SparkManager.getMainWindow(), "Could not save transcript.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void scrollToBottom() {
        executeScript("scrollToBottom();");
    }


    public void addTranscriptWindowInterceptor(TranscriptWindowInterceptor interceptor) {
        interceptors.add(interceptor);
    }

    public void removeTranscriptWindowInterceptor(TranscriptWindowInterceptor interceptor) {
        interceptors.remove(interceptor);
    }

    public boolean isDocumentLoaded() {
        return documentLoaded;
    }

    public void executeScript(final String script) {
        SwingWorker worker = new SwingWorker() {
            public Object construct() {
                while (true) {
                    if (documentLoaded) {
                        browser.executeScript(script);
                        break;
                    }

                    try {
                        Thread.sleep(50);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return documentLoaded;
            }
        };

        worker.start();

    }

    private void startCommandListener() {
        SwingWorker worker = new SwingWorker() {
            public Object construct() {
                while (true) {
                    if (documentLoaded) {
                        break;
                    }

                    try {
                        Thread.sleep(50);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return documentLoaded;
            }

            public void finished() {
                final Timer timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {
                    public void run() {
                        final String command = browser.executeScript("getNextCommand();");
                        if (command != null) {
                            System.out.println(command);
                        }
                    }
                }, 500, 500);
            }
        };

        worker.start();
    }

    public void addComponent(JComponent component) {
        extraPanel.add(component);
        extraPanel.setVisible(true);
        extraPanel.invalidate();
        extraPanel.validate();
        extraPanel.repaint();
    }

    public void removeComponent(JComponent component) {
        if (extraPanel.getComponentCount() == 0) {
            extraPanel.setVisible(false);
        }

        extraPanel.remove(component);
        extraPanel.invalidate();
        extraPanel.validate();
        extraPanel.repaint();
    }
}