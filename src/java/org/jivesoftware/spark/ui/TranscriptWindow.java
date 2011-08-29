/**
 * $RCSfile: ,v $
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
package org.jivesoftware.spark.ui;

import org.jdesktop.swingx.calendar.DateUtils;
import org.jivesoftware.Spark;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.resource.Res;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.packet.DelayInformation;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.plugin.ContextMenuListener;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

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

/**
 * The <CODE>TranscriptWindow</CODE> class. Provides a default implementation
 * of a Chat Window. In general, extensions could override this class
 * to offer more support within the chat, but should not be necessary.
 */
public class TranscriptWindow extends ChatArea implements ContextMenuListener {

	private static final long serialVersionUID = -2168845249388070573L;
	private final SimpleDateFormat notificationDateFormatter;
    private final String notificationDateFormat = ((SimpleDateFormat)SimpleDateFormat.getDateInstance(SimpleDateFormat.FULL)).toPattern();

    private Date lastUpdated;

    /**
     * The default font used in the chat window for all messages.
     */
    private Font defaultFont;

    private Date lastPost;

    /**
     * Creates a default instance of <code>TranscriptWindow</code>.
     */
    public TranscriptWindow() {
        setEditable(false);

        // Set Default Font
        final LocalPreferences pref = SettingsManager.getLocalPreferences();
        int fontSize = pref.getChatRoomFontSize();
        defaultFont = new Font("Dialog", Font.PLAIN, fontSize);


        addMouseListener(this);
        addMouseMotionListener(this);
        setDragEnabled(true);
        addContextMenuListener(this);

        // Make sure ctrl-c works
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("Ctrl c"), "copy");

        getActionMap().put("copy", new AbstractAction("copy") {
			private static final long serialVersionUID = 1797491846835591379L;

			public void actionPerformed(ActionEvent evt) {
                StringSelection stringSelection = new StringSelection(getSelectedText());
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
            }
        });

        notificationDateFormatter = new SimpleDateFormat(notificationDateFormat);
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
        insertMessage(nickname, message, foreground, Color.white);
    }

    /**
     * Create and insert a message from the current user.
     *
     * @param nickname   the nickname of the current user.
     * @param message    the message to insert.
     * @param foreground the color to use for the message foreground.
     * @param background the color to use for the message background.
     */
    public void insertMessage(String nickname, Message message, Color foreground, Color background) {
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
            Date sentDate;
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
            StyleConstants.setBackground(styles, background);
            final Document doc = getDocument();
            styles.removeAttribute("link");

            StyleConstants.setFontSize(styles, defaultFont.getSize());
            doc.insertString(doc.getLength(), date + nickname + ": ", styles);

            // Reset Styles for message
            StyleConstants.setBold(styles, false);

            StyleConstants.setForeground(styles, getMessageColor());
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

            StyleConstants.setForeground(styles, getMessageColor());
            setText(message);
            insertText("\n");
        }
        catch (BadLocationException e) {
            Log.error("Error message.", e);
        }
    }

    protected Color getMessageColor() {
        return Color.BLACK;
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
            StyleConstants.setBackground(styles, new Color(0,0,0,0));
            StyleConstants.setBold(styles, false);
            StyleConstants.setForeground(styles, foregroundColor);
            setText(message);
            insertText("\n");

            // Default back to black
            StyleConstants.setForeground(styles, Color.black);
        }
        catch (BadLocationException ex) {
            Log.error("Error message.", ex);
        }
    }

    /**
     * Create and insert a notification message. A notification message generally is a
     * presence update, but can be used for most anything related to the room.
     *
     * @param text       the text to insert.
     * @param bold       true to use bold text.
     * @param underline  true to have text underlined.
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
            StyleConstants.setForeground(styles, foreground);
            setText(text);
            insertText("\n");
            StyleConstants.setUnderline(styles, false);
            StyleConstants.setForeground(styles, Color.black);
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

            final SimpleDateFormat formatter = new SimpleDateFormat(pref.getTimeFormat());
            final String date = formatter.format(insertDate);

            return "(" + date + ") ";
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
            String value;

            long lastPostTime = lastPost != null ? lastPost.getTime() : 0;
            long lastPostStartOfDay = DateUtils.startOfDayInMillis(lastPostTime);
            long newPostStartOfDay = DateUtils.startOfDayInMillis(date.getTime());

            int diff = DateUtils.getDaysDiff(lastPostStartOfDay, newPostStartOfDay);
            if (diff != 0) {
                insertCustomText(notificationDateFormatter.format(date), true, true, Color.BLACK);
            }

            value = getDate(date);
            value = value + userid + ": ";


            lastPost = date;

            // Agent color is always blue
            StyleConstants.setBold(styles, false);
            StyleConstants.setForeground(styles, Color.BLACK);
            final Document doc = getDocument();
            styles.removeAttribute("link");

            StyleConstants.setFontSize(styles, defaultFont.getSize());
            doc.insertString(doc.getLength(), value, styles);

            // Reset Styles for message
            StyleConstants.setBold(styles, false);
            StyleConstants.setForeground(styles, (Color)UIManager.get("History.foreground"));
            setText(message);
            StyleConstants.setForeground(styles, Color.BLACK);
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
    public void saveTranscript(String fileName, List<Message> transcript, String headerData) {
        final LocalPreferences pref = SettingsManager.getLocalPreferences();

        try {
            SimpleDateFormat formatter;

            File defaultSaveFile = new File(Spark.getSparkUserHome() + "/" + fileName);
            final JFileChooser fileChooser = new JFileChooser(defaultSaveFile);
            fileChooser.setSelectedFile(defaultSaveFile);

            // Show save dialog; this method does not return until the dialog is closed
            int result = fileChooser.showSaveDialog(this);
            final File selFile = fileChooser.getSelectedFile();

            if (selFile != null && result == JFileChooser.APPROVE_OPTION) {
                final StringBuffer buf = new StringBuffer();
                final Iterator<Message> transcripts = transcript.iterator();
                buf.append("<html><body>");
                if (headerData != null) {
                    buf.append(headerData);
                }

                buf.append("<table width=600>");
                while (transcripts.hasNext()) {
                    final Message message = transcripts.next();
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
                        value = "(" + formatter.format(insertionDate) + ") ";
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

    public void cleanup() {
        super.releaseResources();

        clear();

        removeMouseListener(this);
        removeMouseMotionListener(this);

        removeContextMenuListener(this);
        getActionMap().remove("copy");
    }


    public void setFont(Font font) {
        this.defaultFont = font;
    }

    public Font getFont() {
        return defaultFont;
    }


    /**
     * Adds Print and Clear actions.
     *
     * @param object the TransferWindow
     * @param popup  the popup menu to add to.
     */
    public void poppingUp(final Object object, JPopupMenu popup) {
        Action printAction = new AbstractAction() {
			private static final long serialVersionUID = -244227593637660347L;

			public void actionPerformed(ActionEvent actionEvent) {
                SparkManager.printChatTranscript((TranscriptWindow)object);
            }
        };


        Action clearAction = new AbstractAction() {
			private static final long serialVersionUID = -5664307353522844588L;

			public void actionPerformed(ActionEvent actionEvent) {

            	String user = null;
            	try {
            		ChatManager manager = SparkManager.getChatManager();
            		ChatRoom room = manager.getChatContainer().getActiveChatRoom();
            		user = room.getRoomname();

				} catch (ChatRoomNotFoundException e) {
					e.printStackTrace();
				}

                int ok = JOptionPane.showConfirmDialog((TranscriptWindow)object,
                    Res.getString("delete.permanently"), Res.getString("delete.log.permanently"),
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
                if (ok == JOptionPane.YES_OPTION) {
                	if(user != null){
                        // This actions must be move into Transcript Plugin!
	                    File transcriptDir = new File(SparkManager.getUserDirectory(), "transcripts");
	                    File transcriptFile = new File(transcriptDir ,user + ".xml");
	                    transcriptFile.delete();
	                    transcriptFile = new File(transcriptDir,user + "_current.xml");
	                    transcriptFile.delete();
	                    clear();
                    }
                }
            }
        };


        printAction.putValue(Action.NAME, Res.getString("action.print"));
        printAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.PRINTER_IMAGE_16x16));

        clearAction.putValue(Action.NAME, Res.getString("action.clear"));
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

    protected SimpleDateFormat getNotificationDateFormatter() {
        return notificationDateFormatter;
    }

    protected Date getLastPost() {
        return lastPost;
    }

    protected void setLastPost(Date lastPost) {
        this.lastPost = lastPost;
    }

    protected void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }


}
