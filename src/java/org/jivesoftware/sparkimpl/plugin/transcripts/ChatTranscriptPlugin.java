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
import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.BackgroundPanel;
import org.jivesoftware.spark.plugin.ContextMenuListener;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatRoomButton;
import org.jivesoftware.spark.ui.ChatRoomListener;
import org.jivesoftware.spark.ui.ContactItem;
import org.jivesoftware.spark.ui.ContactList;
import org.jivesoftware.spark.ui.TranscriptWindow;
import org.jivesoftware.spark.ui.VCardPanel;
import org.jivesoftware.spark.ui.rooms.ChatRoomImpl;
import org.jivesoftware.spark.util.GraphicUtils;
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
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * The <code>ChatTranscriptPlugin</code> is responsible for transcript handling within Spark.
 *
 * @author Derek DeMoro
 */
public class ChatTranscriptPlugin implements ChatRoomListener {

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

        viewHistoryAction.putValue(Action.NAME, Res.getString("menuitem.view.contact.history"));
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

            public void reconnectingIn(int i) {
            }

            public void reconectionSuccessful() {
            }

            public void reconnectionFailed(Exception exception) {
            }
        });
    }

    public void persistConversations() {
        for (ChatRoom room : SparkManager.getChatManager().getChatContainer().getChatRooms()) {
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
        if (!pref.isChatHistoryEnabled()) {
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
            chatRoomButton.setToolTipText(Res.getString("tooltip.view.history"));
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
        final String jid = room.getRoomname();

        if (room.getChatType() == Message.Type.CHAT) {
            SwingWorker worker = new SwingWorker() {
                public Object construct() {
                    try {
                        Thread.sleep(10);
                    }
                    catch (InterruptedException e) {
                        Log.error("Exception in Chat Transcript Loading.", e);
                    }


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
                                Log.error(e);
                                break;
                            }
                        }

                    }
                    return Boolean.valueOf(isNew);
                }

                public void finished() {
                    Boolean boo = (Boolean)get();
                    if (boo) {
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
        if (!pref.isChatHistoryEnabled()) {
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

        SwingWorker transcriptLoader = new SwingWorker() {
            public Object construct() {
                String bareJID = StringUtils.parseBareAddress(jid);
                return ChatTranscripts.getChatTranscript(bareJID);
            }

            public void finished() {
                final JPanel mainPanel = new BackgroundPanel();

                mainPanel.setLayout(new BorderLayout());

                final VCardPanel topPanel = new VCardPanel(jid);
                mainPanel.add(topPanel, BorderLayout.NORTH);

                final JTextArea window = new JTextArea();
                window.setWrapStyleWord(true);
                window.setFont(new Font("Dialog", Font.PLAIN, 12));
                final JScrollPane pane = new JScrollPane(window);
                pane.getVerticalScrollBar().setBlockIncrement(50);
                pane.getVerticalScrollBar().setUnitIncrement(20);

                mainPanel.add(pane, BorderLayout.CENTER);


                ChatTranscript transcript = (ChatTranscript)get();
                List<HistoryMessage> list = transcript.getMessages();
                //Collections.sort(list, dateComparator);

                StringBuffer buf = new StringBuffer();
                for (HistoryMessage message : list) {
                    String from = message.getFrom();
                    String nickname = StringUtils.parseName(from);
                    final SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy h:mm a");
                    final String date = formatter.format(message.getDate());

                    String prefix = nickname + " [" + date + "]";

                    if (from.equals(SparkManager.getSessionManager().getJID())) {
                        //   window.insertCustomMessage(prefix, message.getBody());
                        buf.append(prefix + ": " + message.getBody());
                        buf.append("\n");
                    }
                    else {
                    buf.append(prefix + ": " + message.getBody());
                        buf.append("\n");
                        //    window.insertCustomOtherMessage(prefix, message.getBody());
                    }

                }

                window.setText(buf.toString());

                // Handle no history
                if (transcript.getMessages().size() == 0) {
                    window.setText(Res.getString("message.no.history.found"));
                }

                final JFrame frame = new JFrame(Res.getString("title.history.for", jid));
                frame.setIconImage(SparkRes.getImageIcon(SparkRes.HISTORY_16x16).getImage());
                frame.getContentPane().setLayout(new BorderLayout());

                frame.getContentPane().add(mainPanel, BorderLayout.CENTER);
                frame.pack();
                frame.setSize(600, 400);
                window.setCaretPosition(0);
                GraphicUtils.centerWindowOnScreen(frame);
                frame.setVisible(true);

            }
        };

        transcriptLoader.start();
    }

    /**
     * Sort HistoryMessages by date.
     */
    final Comparator<HistoryMessage> dateComparator = new Comparator() {
        public int compare(Object o1, Object o2) {
            final HistoryMessage historyMessageOne = (HistoryMessage)o1;
            final HistoryMessage historyMessageTwo = (HistoryMessage)o2;

            long time1 = historyMessageOne.getDate().getTime();
            long time2 = historyMessageTwo.getDate().getTime();

            if (time1 < time2) {
                return 1;
            }
            else if (time1 > time2) {
                return -1;
            }
            return 0;

        }
    };



}
