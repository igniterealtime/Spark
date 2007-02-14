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

import org.jdesktop.swingx.calendar.DateUtils;
import org.jivesoftware.Spark;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.packet.DelayInformation;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.plugin.ContextMenuListener;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * The <CODE>TranscriptWindow</CODE> class. Provides a default implementation
 * of a Chat Window. In general, extensions could override this class
 * to offer more support within the chat, but should not be necessary.
 */
public class TranscriptWindow extends ChatArea {


    private final SimpleDateFormat notificationDateFormatter;
    private final SimpleDateFormat messageDateFormatter;


    private Date lastUpdated;

    /**
     * The default font used in the chat window for all messages.
     */
    private Font defaultFont = new Font("Dialog", Font.PLAIN, 12);

    private Date lastPost;

    /**
     * Creates a default instance of <code>TranscriptWindow</code>.
     */
    public TranscriptWindow() {
        setEditable(false);

        addMouseListener(this);
        addMouseMotionListener(this);
        setDragEnabled(true);

        final TranscriptWindow window = this;
        addContextMenuListener(new ContextMenuListener() {
            public void poppingUp(Object object, JPopupMenu popup) {
                Action printAction = new AbstractAction() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        SparkManager.printChatTranscript(window);
                    }
                };


                Action clearAction = new AbstractAction() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        clear();
                    }
                };


                printAction.putValue(Action.NAME, "Print");
                printAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.PRINTER_IMAGE_16x16));

                clearAction.putValue(Action.NAME, "Clear");
                clearAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.ERASER_IMAGE));
                popup.addSeparator();
                popup.add(printAction);

                popup.add(clearAction);
            }

            public void poppingDown(JPopupMenu popup) {

            }

            public boolean handleDefaultAction(MouseEvent e) {
                return false;
            }
        });

        // Make sure ctrl-c works
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("Ctrl c"), "copy");

        getActionMap().put("copy", new AbstractAction("copy") {
            public void actionPerformed(ActionEvent evt) {
                StringSelection ss = new StringSelection(getSelectedText());
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
            }
        });

        notificationDateFormatter = new SimpleDateFormat("EEEEE, MMMMM d, yyyy");
        messageDateFormatter = new SimpleDateFormat("h:mm a");
    }

    /**
     * Inserts a component into the transcript window.
     *
     * @param component the component to insert.
     */
    public void addComponent(Component component) {
        final StyledDocument doc = (StyledDocument)getDocument();

        // The image must first be wrapped in a style
        Style style = doc.addStyle("StyleName", null);


        StyleConstants.setComponent(style, component);

        // Insert the image at the end of the text
        try {
            doc.insertString(doc.getLength(), "ignored text", style);
            doc.insertString(doc.getLength(), "\n", null);
        }
        catch (BadLocationException e) {
            Log.error(e);
        }
    }

    /**
     * Create and insert a message from the current user.
     *
     * @param nickname   the nickname of the current user.
     * @param message    the message to insert.
     * @param foreground the color to use for the message foreground.
     */
    public void insertMessage(String nickname, Message message, Color foreground) {
        // Check interceptors.
        for (TranscriptWindowInterceptor interceptor : SparkManager.getChatManager().getTranscriptWindowInterceptors()) {
            boolean handled = interceptor.isMessageIntercepted(this, nickname, message);
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

            String date = getDate(sentDate);

            // Agent color is always blue
            StyleConstants.setBold(styles, false);
            StyleConstants.setForeground(styles, foreground);
            final Document doc = getDocument();
            styles.removeAttribute("link");

            StyleConstants.setFontSize(styles, defaultFont.getSize());
            doc.insertString(doc.getLength(), date + nickname + ": ", styles);

            // Reset Styles for message
            StyleConstants.setBold(styles, false);
            setText(body);
            insertText("\n");
        }
        catch (BadLocationException e) {
            Log.error("Error message.", e);
        }
    }

    /**
     * Inserts a full line using a prefix and message.
     *
     * @param prefix     the prefix to use. If null is used, then only the message will be inserted.
     * @param message    the message to insert.
     * @param foreground the foreground color for the message.
     */
    public void insertPrefixAndMessage(String prefix, String message, Color foreground) {
        try {
            // Agent color is always blue
            StyleConstants.setBold(styles, false);
            StyleConstants.setForeground(styles, foreground);
            final Document doc = getDocument();
            styles.removeAttribute("link");

            StyleConstants.setFontSize(styles, defaultFont.getSize());
            if (prefix != null) {
                doc.insertString(doc.getLength(), prefix + ": ", styles);
            }

            // Reset Styles for message
            StyleConstants.setBold(styles, false);
            setText(message);
            insertText("\n");
        }
        catch (BadLocationException e) {
            Log.error("Error message.", e);
        }
    }


    /**
     * Create and insert a notification message. A notification message generally is a
     * presence update, but can be used for most anything related to the room.
     *
     * @param message         the information message to insert.
     * @param foregroundColor the foreground color to use.
     */
    public synchronized void insertNotificationMessage(String message, Color foregroundColor) {
        try {
            // Agent color is always blue
            StyleConstants.setBold(styles, false);
            StyleConstants.setForeground(styles, foregroundColor);
            final Document doc = getDocument();
            styles.removeAttribute("link");

            StyleConstants.setFontSize(styles, defaultFont.getSize());
            doc.insertString(doc.getLength(), "", styles);

            // Reset Styles for message
            StyleConstants.setBold(styles, false);
            setForeground(foregroundColor);
            setText(message);
            insertText("\n");
            setForeground(Color.black);
        }
        catch (BadLocationException ex) {
            Log.error("Error message.", ex);
        }
    }

    /**
     * Create and insert a notification message. A notification message generally is a
     * presence update, but can be used for most anything related to the room.
     *
     * @param text      the text to insert.
     * @param bold      true to use bold text.
     * @param underline true to have text underlined.
     * @param foreground the foreground color.
     */
    public synchronized void insertCustomText(String text, boolean bold, boolean underline, Color foreground) {
        try {
            // Agent color is always blue
            StyleConstants.setBold(styles, true);
            StyleConstants.setForeground(styles, foreground);
            final Document doc = getDocument();
            styles.removeAttribute("link");

            StyleConstants.setFontSize(styles, defaultFont.getSize());
            doc.insertString(doc.getLength(), "", styles);

            // Reset Styles for message
            StyleConstants.setBold(styles, bold);
            StyleConstants.setUnderline(styles, underline);
            setForeground(foreground);
            setText(text);
            insertText("\n");
            StyleConstants.setUnderline(styles, false);
            setForeground(Color.black);
        }
        catch (BadLocationException ex) {
            Log.error("Error message.", ex);
        }
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

        StyleConstants.setFontFamily(styles, defaultFont.getFontName());
        StyleConstants.setFontSize(styles, defaultFont.getSize());

        if (pref.isTimeDisplayedInChat()) {
            final SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
            final String date = formatter.format(insertDate);

            return "[" + date + "] ";
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
        try {
            String value = "";

            long lastPostTime = lastPost != null ? lastPost.getTime() : 0;
            int diff = DateUtils.getDaysDiff(lastPostTime, date.getTime());
            if (diff != 0) {
                insertCustomText(notificationDateFormatter.format(date), true, true, Color.GRAY);
            }

            value = "[" + messageDateFormatter.format(date) + "] ";
            value = value + userid + ": ";


            lastPost = date;

            // Agent color is always blue
            StyleConstants.setBold(styles, false);
            StyleConstants.setForeground(styles, Color.gray);
            final Document doc = getDocument();
            styles.removeAttribute("link");

            StyleConstants.setFontSize(styles, defaultFont.getSize());
            doc.insertString(doc.getLength(), value, styles);

            // Reset Styles for message
            StyleConstants.setBold(styles, false);
            setForeground((Color)UIManager.get("History.foreground"));
            setText(message);
            setForeground(Color.BLACK);
            insertText("\n");
        }
        catch (BadLocationException ex) {
            Log.error("Error message.", ex);
        }
    }

    /**
     * Disable the entire <code>TranscriptWindow</code> and visually represent
     * it as disabled.
     */
    public void showWindowDisabled() {
        final Document document = getDocument();
        final SimpleAttributeSet attrs = new SimpleAttributeSet();
        StyleConstants.setForeground(attrs, Color.LIGHT_GRAY);

        final int length = document.getLength();
        StyledDocument styledDocument = getStyledDocument();
        styledDocument.setCharacterAttributes(0, length, attrs, false);
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

 
    public void setFont(Font font) {
        this.defaultFont = font;
    }

    public Font getFont() {
        return defaultFont;
    }
}
