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

import org.jdesktop.swingx.calendar.DateUtils;
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
import org.jivesoftware.spark.ui.VCardPanel;
import org.jivesoftware.spark.ui.rooms.ChatRoomImpl;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.TaskEngine;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.text.html.HTMLEditorKit;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

/**
 * The <code>ChatTranscriptPlugin</code> is responsible for transcript handling within Spark.
 *
 * @author Derek DeMoro
 */
public class ChatTranscriptPlugin implements ChatRoomListener {

    private final SimpleDateFormat notificationDateFormatter;
    private final SimpleDateFormat messageDateFormatter;

    /**
     * Register the listeners for transcript persistence.
     */
    public ChatTranscriptPlugin() {
        SparkManager.getChatManager().addChatRoomListener(this);

        notificationDateFormatter = new SimpleDateFormat("EEEEE, MMMMM d, yyyy");
        messageDateFormatter = new SimpleDateFormat("h:mm a");

        final ContactList contactList = SparkManager.getWorkspace().getContactList();


        final Action viewHistoryAction = new AbstractAction() {
            public void actionPerformed(ActionEvent actionEvent) {
                ContactItem item = (ContactItem)contactList.getSelectedUsers().iterator().next();
                final String jid = item.getJID();

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

            public void reconnectionSuccessful() {
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

    public void chatRoomLeft(ChatRoom room) {

    }

    public void chatRoomClosed(final ChatRoom room) {
        // Persist only agent to agent chat rooms.
        if (room.getChatType() == Message.Type.chat) {
            persistChatRoom(room);
        }
    }

    private void persistChatRoom(final ChatRoom room) {
        LocalPreferences pref = SettingsManager.getLocalPreferences();
        if (!pref.isChatHistoryEnabled()) {
            return;
        }

        final String jid = room.getRoomname();

        final List<Message> transcripts = room.getTranscripts();
        ChatTranscript transcript = new ChatTranscript();
        for (Message message : transcripts) {
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

        ChatTranscripts.appendToTranscript(jid, transcript);
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

                final JEditorPane window = new JEditorPane();
                window.setEditorKit(new HTMLEditorKit());
                window.setBackground(Color.white);
                final JScrollPane pane = new JScrollPane(window);
                pane.getVerticalScrollBar().setBlockIncrement(50);
                pane.getVerticalScrollBar().setUnitIncrement(20);

                mainPanel.add(pane, BorderLayout.CENTER);


                final ChatTranscript transcript = (ChatTranscript)get();
                final List<HistoryMessage> list = transcript.getMessages();
                final String personalNickname = SparkManager.getUserManager().getNickname();


                final JFrame frame = new JFrame(Res.getString("title.history.for", jid));
                frame.setIconImage(SparkRes.getImageIcon(SparkRes.HISTORY_16x16).getImage());
                frame.getContentPane().setLayout(new BorderLayout());

                frame.getContentPane().add(mainPanel, BorderLayout.CENTER);
                frame.pack();
                frame.setSize(600, 400);
                window.setCaretPosition(0);
                GraphicUtils.centerWindowOnScreen(frame);
                frame.setVisible(true);
                window.setEditable(false);

                final StringBuilder builder = new StringBuilder();
                builder.append("<html><body><table cellpadding=0 cellspacing=0>");


                final TimerTask transcriptTask = new TimerTask() {
                    public void run() {
                        Date lastPost = null;
                        String lastPerson = null;
                        boolean initialized = false;
                        for (HistoryMessage message : list) {
                            String color = "blue";

                            String from = message.getFrom();
                            String nickname = SparkManager.getUserManager().getUserNicknameFromJID(message.getFrom());
                            String body = message.getBody();
                            if (nickname.equals(message.getFrom())) {
                                String otherJID = StringUtils.parseBareAddress(message.getFrom());
                                String myJID = SparkManager.getSessionManager().getBareAddress();

                                if (otherJID.equals(myJID)) {
                                    nickname = personalNickname;
                                }
                                else {
                                    nickname = StringUtils.parseName(nickname);
                                }
                            }

                            if (!StringUtils.parseBareAddress(from).equals(SparkManager.getSessionManager().getBareAddress())) {
                                color = "red";
                            }


                            long lastPostTime = lastPost != null ? lastPost.getTime() : 0;
                            int diff = DateUtils.getDaysDiff(lastPostTime, message.getDate().getTime());
                            if (diff != 0) {
                                if (initialized) {
                                    builder.append("<tr><td><br></td></tr>");
                                }

                                builder.append("<tr><td colspan=2><font size=4 color=gray><b><u>" + notificationDateFormatter.format(message.getDate()) + "</u></b></font></td></tr>");
                                lastPerson = null;
                                initialized = true;
                            }

                            String value = "[" + messageDateFormatter.format(message.getDate()) + "]&nbsp;&nbsp;  ";

                            boolean newInsertions = lastPerson == null || !lastPerson.equals(nickname);
                            if (newInsertions) {
                                builder.append("<tr valign=top><td colspan=2 nowrap>");
                                builder.append("<font size=4 color='" + color + "'><b>");
                                builder.append(nickname);
                                builder.append("</b></font>");
                                builder.append("</td></tr>");
                            }

                            builder.append("<tr valign=top><td align=left nowrap>");
                            builder.append(value);
                            builder.append("</td><td align=left>");
                            builder.append(body);

                            builder.append("</td></tr>");

                            lastPost = message.getDate();
                            lastPerson = nickname;
                        }
                        builder.append("</table></body></html>");

                        // Handle no history
                        if (transcript.getMessages().size() == 0) {
                            builder.append("<b>" + Res.getString("message.no.history.found") + "</b>");
                        }

                        window.setText(builder.toString());
                    }
                };

                TaskEngine.getInstance().schedule(transcriptTask, 10);
            }
        };

        transcriptLoader.start();
    }

    /**
     * Sort HistoryMessages by date.
     */
    final Comparator dateComparator = new Comparator() {
        public int compare(Object messageOne, Object messageTwo) {
            final HistoryMessage historyMessageOne = (HistoryMessage)messageOne;
            final HistoryMessage historyMessageTwo = (HistoryMessage)messageTwo;

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
