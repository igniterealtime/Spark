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

import com.webrenderer.BrowserFactory;
import com.webrenderer.IBrowserCanvas;
import com.webrenderer.event.MouseEvent;
import com.webrenderer.event.MouseListener;
import com.webrenderer.event.NetworkEvent;
import com.webrenderer.event.NetworkListener;
import org.jivesoftware.Spark;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.packet.DelayInformation;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.VerticalFlowLayout;
import org.jivesoftware.spark.ui.themes.ThemeManager;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.emoticons.Emoticon;
import org.jivesoftware.sparkimpl.plugin.emoticons.EmoticonManager;
import org.jivesoftware.sparkimpl.profile.VCardManager;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * The <CODE>TranscriptWindow</CODE> class. Provides a default implementation
 * of a Chat Window. In general, extensions could override this class
 * to offer more support within the chat, but should not be necessary.
 */
public class TranscriptWindow extends JPanel {

    private List<TranscriptWindowInterceptor> interceptors = new ArrayList<TranscriptWindowInterceptor>();

    private Date lastUpdated;

    private IBrowserCanvas browser;

    private ThemeManager themeManager;

    private String activeUser;
    private String activeHistoryUser;


    private boolean documentLoaded;

    private JPanel extraPanel;

    private VCardManager vcardManager;

    private Timer timer;

    private javax.swing.Timer activeTimer;

    private StringBuilder scriptBuilder = new StringBuilder();

    private final SimpleDateFormat formatter = new SimpleDateFormat("h:mm");


    /**
     * Creates a default instance of <code>TranscriptWindow</code>.
     */
    public TranscriptWindow() {
        setLayout(new BorderLayout());

        themeManager = ThemeManager.getInstance();
        vcardManager = SparkManager.getVCardManager();

        extraPanel = new JPanel();

        //Core function to create browser
        browser = BrowserFactory.spawnMozilla();
        browser.addNetworkListener(new NetworkListener() {
            public void onProgressChange(NetworkEvent networkEvent) {
            }

            public void onDocumentLoad(NetworkEvent networkEvent) {
                documentLoaded = true;
            }

            public void onDocumentComplete(NetworkEvent networkEvent) {
            }

            public void onNetworkStatus(NetworkEvent networkEvent) {
            }

            public void onNetworkError(NetworkEvent networkEvent) {
            }

            public void onHTTPResponse(NetworkEvent networkEvent) {
            }

            public void onHTTPInterceptHeaders(NetworkEvent networkEvent) {
            }
        });


        browser.loadURL(themeManager.getTemplateURL());

        documentLoaded = true;

        browser.enableDefaultContextMenu(false);

        browser.addMouseListener(new MouseListener() {
            public void onClick(MouseEvent mouseEvent) {
            }

            public void onDoubleClick(MouseEvent mouseEvent) {
                mouseEvent.consume();
            }

            public void onMouseDown(MouseEvent mouseEvent) {
                mouseEvent.consume();
            }

            public void onMouseUp(MouseEvent mouseEvent) {
                mouseEvent.consume();
            }
        });

        add((Canvas)browser, BorderLayout.CENTER);


        extraPanel.setBackground(Color.white);
        extraPanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP, 0, 0, true, false));

        add(extraPanel, BorderLayout.SOUTH);

        setBorder(BorderFactory.createLineBorder(Color.lightGray));

        final Action resetAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                activeUser = null;
            }
        };

        int fiveMinutes = 5000 * 60;
        activeTimer = new javax.swing.Timer(fiveMinutes, resetAction);
        activeTimer.start();

        final Action insertAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (documentLoaded) {
                    final String script = scriptBuilder.toString();
                    if (ModelUtil.hasLength(script)) {
                        Thread thread = new Thread(new Runnable() {
                            public void run() {
                                browser.executeScript(script);
                            }
                        });
                        thread.start();

                        scriptBuilder.setLength(0);
                    }
                }
                timer.restart();
            }
        };

        timer = new Timer(50, insertAction);
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
        body = org.jivesoftware.spark.util.StringUtils.escapeHTMLTags(body);
        body = filterBody(body);
        String date = getDate(null);

        String jid = SparkManager.getSessionManager().getJID();


        if (userid.equals(activeUser)) {
            String text = themeManager.getNextOutgoingMessage(body, date);
            executeScript("appendNextMessage('" + text + "')");
        }
        else {
            String text = themeManager.getOutgoingMessage(userid, date, body, vcardManager.getAvatar(jid));
            executeScript("appendMessage('" + text + "')");
        }

        setActiveUser(userid);
    }


    public void insertCustomMessage(String prefix, String message) {
        message = filterBody(message);
        String text = themeManager.getOutgoingMessage(prefix, "", message, vcardManager.getAvatar(""));
        executeScript("appendMessage('" + text + "')");
    }

    /**
     * Inserts a custom message into the transcript window.
     *
     * @param prefix  the prefix of the message (ex: johndoe:)
     * @param message the body of the message.
     */
    public void insertCustomOtherMessage(String prefix, String message) {
        message = filterBody(message);
        String text = themeManager.getIncomingMessage(prefix, "", message, vcardManager.getAvatar(""));
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

            body = org.jivesoftware.spark.util.StringUtils.escapeHTMLTags(body);
            body = filterBody(body);

            if (userid.equals(activeUser)) {
                String text = themeManager.getNextIncomingMessage(body, theDate);
                executeScript("appendNextMessage('" + text + "')");
            }
            else {
                String text = themeManager.getIncomingMessage(userid, theDate, body, vcardManager.getAvatar(message.getFrom()));
                executeScript("appendMessage('" + text + "')");
            }

            setActiveUser(userid);
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
        message = filterBody(message);
        String text = themeManager.getStatusMessage(message, "");
        executeScript("appendMessage('" + text + "')");
    }

    /**
     * Creates and inserts an error message.
     *
     * @param message the information message to insert.
     */
    public void insertErrorMessage(String message) {
        message = filterBody(message);
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
        final LocalPreferences localPreferences = SettingsManager.getLocalPreferences();

        if (insertDate == null) {
            insertDate = new Date();
        }


        if (localPreferences.isTimeDisplayedInChat()) {
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
     * @param jid     the users jid.
     * @param userid  the userid of the sender.
     * @param message the message to insert.
     * @param date    the Date object created when the message was delivered.
     */
    public void insertHistoryMessage(String jid, String userid, String message, Date date) {
        final String sessionJID = SparkManager.getSessionManager().getJID();
        boolean outgoingMessage = false;
        if (StringUtils.parseBareAddress(sessionJID).equals(jid)) {
            outgoingMessage = true;
        }

        final SimpleDateFormat formatter = new SimpleDateFormat("h:mm");
        String time = formatter.format(date);

        message = filterBody(message);

        if (userid.equals(activeHistoryUser)) {
            if (outgoingMessage) {
                String text = themeManager.getNextOutgoingHistoryString(message, time);
                executeScript("appendNextMessage('" + text + "')");
            }
            else {
                String text = themeManager.getNextIncomingHistoryMessage(message, time);
                executeScript("appendNextMessage('" + text + "')");
            }
        }
        else {
            if (outgoingMessage) {
                String text = themeManager.getOutgoingHistoryMessage(userid, time, message, vcardManager.getAvatar(jid));
                executeScript("appendMessage('" + text + "')");
            }
            else {
                String text = null;
                try {
                    text = themeManager.getIncomingHistoryMessage(userid, time, message, vcardManager.getAvatar(jid));
                    executeScript("appendMessage('" + text + "')");
                }
                catch (Exception e) {
                    Log.error(e);
                }

            }
        }

        activeHistoryUser = userid;
    }

    /**
     * Disable the entire <code>TranscriptWindow</code> and visually represent
     * it as disabled.
     */
    public void showDisabledWindowUI() {
        //TODO: Update disabled code.
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

                    if (Message.Type.groupchat == message.getType()) {
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

    public void setInnerHTML(String elementID, String value) {
        final StringBuilder builder = new StringBuilder();
        builder.append("var myVar = document.getElementById(\"").append(elementID).append("\");");
        builder.append("if(myVar){ myVar.innerHTML = '").append(value).append("';}");
        executeScript(builder.toString());
    }

    public void executeScript(final String script) {
        scriptBuilder.append(script);
        if (!script.endsWith(";")) {
            scriptBuilder.append(";");
        }

        scriptBuilder.append(" ");
        timer.restart();
    }

    public void setURL(URL url) {
        documentLoaded = false;

        browser.loadURL(url);
    }

    public void addComponent(JComponent component) {
        extraPanel.add(component);
        extraPanel.setVisible(true);
        invalidate();
        validate();
        repaint();
    }

    public void removeComponent(JComponent component) {
        if (extraPanel.getComponentCount() == 0) {
            extraPanel.setVisible(false);
        }

        extraPanel.remove(component);
        invalidate();
        validate();
        repaint();
    }

    public Dimension getPreferredSize() {
        final Dimension size = super.getPreferredSize();
        size.width = 0;
        return size;
    }

    private String filterBody(String text) {
        EmoticonManager emoticonManager = EmoticonManager.getInstance();
        StringBuilder builder = new StringBuilder();


        final StringTokenizer tokenizer = new StringTokenizer(text, " \n \t", true);
        while (tokenizer.hasMoreTokens()) {
            String textFound = tokenizer.nextToken();
            if (textFound.startsWith("http://") || textFound.startsWith("ftp://")
                    || textFound.startsWith("https://") || textFound.startsWith("www.") || textFound.startsWith("\\") || textFound.indexOf("://") != -1) {
                builder.append("<a href=\"").append(textFound).append("\" target=_blank>").append(textFound).append("</a>");
            }
            else if (emoticonManager.getEmoticon(textFound) != null) {
                Emoticon emot = emoticonManager.getEmoticon(textFound);
                URL url = emoticonManager.getEmoticonURL(emot);
                builder.append("<img src=\"").append(url.toExternalForm()).append("\" />");
            }
            else {
                builder.append(textFound);
            }
        }

        return builder.toString();

    }

    /**
     * Sets the current active user.
     *
     * @param user the active user.
     */
    private void setActiveUser(String user) {
        activeUser = user;
        activeTimer.stop();
        activeTimer.start();
    }


}

