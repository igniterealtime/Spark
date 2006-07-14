/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkimpl.plugin.transcripts;

import org.jivesoftware.MainWindowListener;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.VerticalFlowLayout;
import org.jivesoftware.spark.component.panes.CollapsiblePane;
import org.jivesoftware.spark.component.panes.CollapsiblePaneListener;
import org.jivesoftware.spark.plugin.ContextMenuListener;
import org.jivesoftware.spark.ui.ChatContainer;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatRoomButton;
import org.jivesoftware.spark.ui.ChatRoomListener;
import org.jivesoftware.spark.ui.ContactItem;
import org.jivesoftware.spark.ui.ContactList;
import org.jivesoftware.spark.ui.TranscriptWindow;
import org.jivesoftware.spark.ui.rooms.ChatRoomImpl;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The <code>ChatTranscriptPlugin</code> is responsible for transcript handling within Spark.
 */
public class ChatTranscriptPlugin implements ChatRoomListener {
    private String LAST_YEAR = "LAST_YEAR";
    private String LAST_MONTH = "LAST_MONTH";
    private String LAST_WEEK = "LAST_WEEK";
    private String LAST_TWO_MONTHS = "LAST_TWO_MONTHS";
    private String LAST_SIX_MONTHS = "LAST_SIX_MONTHS";
    private String BEFORE = "BEFORE";

    /**
     * Register the listeners for transcript persistence.
     */
    public void initialize() {
        SparkManager.getChatManager().addChatRoomListener(this);

        final ContactList contactList = SparkManager.getWorkspace().getContactList();


        final Action viewHistoryAction = new AbstractAction() {
            public void actionPerformed(ActionEvent actionEvent) {
                ContactItem item = (ContactItem)contactList.getSelectedUsers().iterator().next();
                final String jid = item.getFullJID();

                showHistory(jid);
            }
        };

        viewHistoryAction.putValue(Action.NAME, "View Contact History");
        viewHistoryAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.HISTORY_16x16));

        contactList.addContextMenuListener(new ContextMenuListener() {
            public void poppingUp(Object object, JPopupMenu popup) {
                if (object instanceof ContactItem) {
                    popup.add(viewHistoryAction);
                }
            }

            public void poppingDown(JPopupMenu popup) {

            }

            public boolean handleDefaultAction(MouseEvent e) {
                return false;
            }
        });

        SparkManager.getMainWindow().addMainWindowListener(new MainWindowListener() {
            public void shutdown() {
                persistConversations();
            }

            public void mainWindowActivated() {

            }

            public void mainWindowDeactivated() {

            }
        });


        SparkManager.getConnection().addConnectionListener(new ConnectionListener() {
            public void connectionClosed() {
            }

            public void connectionClosedOnError(Exception e) {
                persistConversations();
            }
        });
    }

    public void persistConversations() {
        ChatContainer chatRooms = SparkManager.getChatManager().getChatContainer();
        Collection rooms = chatRooms.getChatRooms();
        Iterator iter = rooms.iterator();
        while (iter.hasNext()) {
            ChatRoom room = (ChatRoom)iter.next();
            if (room instanceof ChatRoomImpl) {
                ChatRoomImpl roomImpl = (ChatRoomImpl)room;
                if (roomImpl.isActive()) {
                    persistChatRoom(roomImpl);
                }
            }
        }
    }

    public boolean canShutDown() {
        return true;
    }

    public void chatRoomOpened(final ChatRoom room) {
        LocalPreferences pref = SettingsManager.getLocalPreferences();
        if (pref.isHideChatHistory()) {
            return;
        }

        final String jid = room.getRoomname();
        File transcriptFile = ChatTranscripts.getTranscriptFile(jid);
        if (!transcriptFile.exists()) {
            return;
        }

        final TranscriptWindow roomWindow = room.getTranscriptWindow();

        final TranscriptWindow window = new TranscriptWindow();
        window.setEditable(false);
        window.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                room.getChatInputEditor().requestFocusInWindow();
            }
        });
        insertHistory(room, roomWindow);

        if (room instanceof ChatRoomImpl) {
            // Add History Button
            ChatRoomButton chatRoomButton = new ChatRoomButton(SparkRes.getImageIcon(SparkRes.HISTORY_24x24_IMAGE));
            room.getToolBar().addChatRoomButton(chatRoomButton);
            chatRoomButton.setToolTipText("View history");
            chatRoomButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent actionEvent) {
                    ChatRoomImpl roomImpl = (ChatRoomImpl)room;
                    showHistory(roomImpl.getParticipantJID());
                }
            });
        }
    }

    private void insertHistory(final ChatRoom room, final TranscriptWindow roomWindow) {
        final StringBuffer buf = new StringBuffer();


        if (room.getChatType() == Message.Type.CHAT) {
            SwingWorker worker = new SwingWorker() {
                public Object construct() {
                    try {
                        Thread.sleep(10);
                    }
                    catch (InterruptedException e) {
                        Log.error("Exception in Chat Transcript Loading.", e);
                    }
                    final String jid = room.getRoomname();

                    ChatTranscript transcript = ChatTranscripts.getChatTranscript(jid);

                    final Iterator messages = transcript.getNumberOfEntries(20).iterator();
                    boolean isNew = false;
                    while (messages.hasNext()) {
                        while (messages != null && messages.hasNext()) {
                            isNew = true;
                            try {
                                HistoryMessage message = (HistoryMessage)messages.next();
                                String from = StringUtils.parseName(message.getFrom());
                                Date date = message.getDate();
                                final SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy h:mm a");
                                String dateValue = "[" + formatter.format(date) + "] ";
                                buf.append(dateValue);
                                buf.append(" ");
                                buf.append(from);
                                buf.append(": ");
                                buf.append(message.getBody());
                                buf.append("\n");
                            }
                            catch (Exception e) {
                                break;
                            }
                        }

                    }
                    return new Boolean(isNew);
                }

                public void finished() {
                    Boolean boo = (Boolean)get();
                    if (boo.booleanValue()) {
                        StyledDocument doc = (StyledDocument)roomWindow.getDocument();
                        final SimpleAttributeSet styles = new SimpleAttributeSet();
                        StyleConstants.setFontSize(styles, 12);
                        StyleConstants.setFontFamily(styles, "Dialog");
                        StyleConstants.setForeground(styles, Color.LIGHT_GRAY);

                        // Insert the image at the end of the text
                        try {
                            doc.insertString(0, buf.toString(), styles);
                        }
                        catch (BadLocationException e) {
                            Log.error(e);
                        }
                    }

                    room.scrollToBottom();
                }
            };
            worker.start();
        }
    }

    public void chatRoomLeft(ChatRoom room) {

    }

    public void chatRoomClosed(final ChatRoom room) {
        // Persist only agent to agent chat rooms.
        if (room.getChatType() == Message.Type.CHAT) {

            SwingWorker persistMessageWorker = new SwingWorker() {
                public Object construct() {
                    persistChatRoom(room);
                    return "ok";
                }
            };
            persistMessageWorker.start();
        }
    }

    private void persistChatRoom(final ChatRoom room) {
        LocalPreferences pref = SettingsManager.getLocalPreferences();
        if (pref.isHideChatHistory()) {
            return;
        }

        final String jid = room.getRoomname();

        List transcripts = room.getTranscripts();

        Iterator messages = transcripts.iterator();

        ChatTranscript transcript = ChatTranscripts.getChatTranscript(jid);
        while (messages.hasNext()) {
            Message message = (Message)messages.next();
            HistoryMessage history = new HistoryMessage();
            history.setTo(message.getTo());
            history.setFrom(message.getFrom());
            history.setBody(message.getBody());
            Date date = (Date)message.getProperty("date");
            if (date != null) {
                history.setDate(date);
            }
            else {
                history.setDate(new Date());
            }
            transcript.addHistoryMessage(history);
        }

        ChatTranscripts.saveTranscript(jid);
    }

    public void chatRoomActivated(ChatRoom room) {

    }

    public void userHasJoined(ChatRoom room, String userid) {

    }

    public void userHasLeft(ChatRoom room, String userid) {

    }

    public void uninstall() {
        // Do nothing.
    }

    private void showHistory(final String jid) {
        final Map dateMap = new LinkedHashMap();
        SwingWorker worker = new SwingWorker() {
            final TranscriptWindow pane = new TranscriptWindow();

            public Object construct() {
                ChatTranscript transcript = ChatTranscripts.getChatTranscript(jid);
                Iterator messages = transcript.getMessages().iterator();

                Calendar cal = Calendar.getInstance();
                Date now = new Date();
                cal.setTime(now);

                cal.add(Calendar.DATE, -7);

                Date lastWeek = cal.getTime();

                cal.setTime(now);
                cal.add(Calendar.MONTH, -1);

                Date lastMonth = cal.getTime();

                cal.setTime(now);
                cal.add(Calendar.MONTH, -2);

                Date twoMonths = cal.getTime();

                cal.setTime(now);

                cal.add(Calendar.MONTH, -6);

                Date sixMonths = cal.getTime();

                cal.setTime(now);

                cal.add(Calendar.YEAR, -1);

                Date lastYear = cal.getTime();


                while (messages.hasNext()) {
                    HistoryMessage message = (HistoryMessage)messages.next();
                    Date datePosted = message.getDate();

                    String key = "";

                    if (datePosted.getTime() > lastWeek.getTime()) {
                        // Add to last week
                        key = "This Week";
                    }
                    else if (datePosted.getTime() > lastMonth.getTime()) {
                        // Add to last month
                        key = "One Month";
                    }
                    else if (datePosted.getTime() > twoMonths.getTime()) {
                        // Add to two months
                        key = "Two Months";
                    }
                    else if (datePosted.getTime() > sixMonths.getTime()) {
                        // Add to six months
                        key = "Six Months";
                    }
                    else if (datePosted.getTime() > lastYear.getTime()) {
                        // Add to last year.
                        key = "Year Ago";
                    }
                    else {
                        // Add to before.
                        key = "More than a year ago";
                    }


                    if (!dateMap.containsKey(key)) {
                        final List list = new ArrayList();
                        list.add(message);
                        dateMap.put(key, list);
                    }
                    else {
                        final List list = (ArrayList)dateMap.get(key);
                        list.add(message);
                    }
                }

                return pane;
            }

            public void finished() {
                final JPanel mainPanel = new JPanel();
                mainPanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP, 0, 0, true, false));
                mainPanel.setBackground(Color.white);
                JScrollPane scrollPane = new JScrollPane(mainPanel);

                // Iterate through and print dates
                final Iterator d = dateMap.keySet().iterator();
                List list = new ArrayList();
                while (d.hasNext()) {
                    list.add(d.next());
                }

                Iterator dates = ModelUtil.reverseListIterator(list.listIterator());
                while (dates.hasNext()) {
                    final String date = (String)dates.next();

                    // Create collapsible pane
                    final CollapsiblePane pane = new CollapsiblePane(date);

                    pane.setCollapsed(true);

                    mainPanel.add(pane);
                    pane.addCollapsiblePaneListener(new CollapsiblePaneListener() {
                        boolean expanded = false;

                        public void paneExpanded() {
                            if (!expanded) {
                                List messages = (List)dateMap.get(date);
                                final TranscriptWindow window = new TranscriptWindow() {
                                    public Dimension getPreferredSize() {
                                        final Dimension size = super.getPreferredSize();
                                        size.width = 0;
                                        return size;
                                    }
                                };

                                Iterator messageIter = messages.iterator();
                                while (messageIter.hasNext()) {
                                    HistoryMessage m = (HistoryMessage)messageIter.next();
                                    String from = m.getFrom();
                                    String nickname = StringUtils.parseName(from);
                                    final SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy h:mm a");
                                    final String date = formatter.format(m.getDate());

                                    String prefix = nickname + " [" + date + "]";

                                    if (from.equals(SparkManager.getSessionManager().getJID())) {
                                        window.insertCustomMessage(prefix, m.getBody());
                                    }
                                    else {
                                        window.insertCustomOtherMessage(prefix, m.getBody());
                                    }
                                }
                                pane.setContentPane(window);
                                expanded = true;
                            }
                        }

                        public void paneCollapsed() {
                        }
                    });


                }

                final JFrame frame = new JFrame("History For " + jid);
                frame.setIconImage(SparkRes.getImageIcon(SparkRes.HISTORY_16x16).getImage());
                frame.getContentPane().setLayout(new BorderLayout());

                scrollPane.getVerticalScrollBar().setBlockIncrement(50);
                scrollPane.getVerticalScrollBar().setUnitIncrement(20);

                frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
                frame.pack();
                frame.setSize(600, 400);
                GraphicUtils.centerWindowOnScreen(frame);
                frame.setVisible(true);

            }
        };

        worker.start();
    }
}
