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
package org.jivesoftware.sparkimpl.plugin.transcripts;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimerTask;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.text.html.HTMLEditorKit;

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
import org.jivesoftware.spark.ui.ChatRoomClosingListener;
import org.jivesoftware.spark.ui.ChatRoomListener;
import org.jivesoftware.spark.ui.ContactItem;
import org.jivesoftware.spark.ui.ContactList;
import org.jivesoftware.spark.ui.VCardPanel;
import org.jivesoftware.spark.ui.rooms.ChatRoomImpl;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.TaskEngine;
import org.jivesoftware.spark.util.UIComponentRegistry;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

/**
 * The <code>ChatTranscriptPlugin</code> is responsible for transcript handling within Spark.
 *
 * @author Derek DeMoro
 */
public class ChatTranscriptPlugin implements ChatRoomListener {

    private final String timeFormat = "HH:mm:ss";
    private final String dateFormat = ((SimpleDateFormat)SimpleDateFormat.getDateInstance(SimpleDateFormat.FULL)).toPattern();
    private final SimpleDateFormat notificationDateFormatter;
    private final SimpleDateFormat messageDateFormatter;
    private HashMap<ChatRoom,Message> lastMessage = new HashMap<ChatRoom,Message>();
    private JDialog Frame;

    /**
     * Register the listeners for transcript persistence.
     */
    public ChatTranscriptPlugin() {
        SparkManager.getChatManager().addChatRoomListener(this);

        notificationDateFormatter = new SimpleDateFormat(dateFormat);
        messageDateFormatter = new SimpleDateFormat(timeFormat);

        final ContactList contactList = SparkManager.getWorkspace().getContactList();

        final Action viewHistoryAction = new AbstractAction() {
			private static final long serialVersionUID = -6498776252446416099L;

			public void actionPerformed(ActionEvent actionEvent) {
                ContactItem item = contactList.getSelectedUsers().iterator().next();
                final String jid = item.getJID();

                showHistory(jid);
            }
        };

        viewHistoryAction.putValue(Action.NAME, Res.getString("menuitem.view.contact.history"));
        viewHistoryAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.HISTORY_16x16));

        final Action showStatusMessageAction = new AbstractAction() {
			private static final long serialVersionUID = -5000370836304286019L;

			public void actionPerformed(ActionEvent actionEvent) {
				ContactItem item = contactList.getSelectedUsers().iterator().next();
				showStatusMessage(item);
			}
       };

       showStatusMessageAction.putValue(Action.NAME, Res.getString("menuitem.show.contact.statusmessage"));

        contactList.addContextMenuListener(new ContextMenuListener() {
            public void poppingUp(Object object, JPopupMenu popup) {
                if (object instanceof ContactItem) {
               	 	popup.add(viewHistoryAction);
               	 	popup.add(showStatusMessageAction);
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
            new ChatRoomDecorator(room);
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

    public void persistChatRoom(final ChatRoom room) {
        LocalPreferences pref = SettingsManager.getLocalPreferences();
        if (!pref.isChatHistoryEnabled()) {
            return;
        }

        final String jid = room.getRoomname();

        final List<Message> transcripts = room.getTranscripts();
        ChatTranscript transcript = new ChatTranscript();
        int count = 0;
        int i = 0;
    	if (lastMessage.get(room) != null)
    	{
    		count = transcripts.indexOf(lastMessage.get(room)) + 1;
    	}
        for (Message message : transcripts) {
        	if (i < count)
        	{
            	i++;
        		continue;
        	}
      	  	lastMessage.put(room,message);
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
                // add search text input
                final JPanel topPanel = new BackgroundPanel();
                topPanel.setLayout(new GridBagLayout());

                final VCardPanel vacardPanel = new VCardPanel(jid);
                final JTextField searchField = new JTextField(25);
                searchField.setText(Res.getString("message.search.for.history"));
                searchField.setToolTipText(Res.getString("message.search.for.history"));
                searchField.setForeground((Color) UIManager.get("TextField.lightforeground"));

                topPanel.add(vacardPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(1, 5, 1, 1), 0, 0));
                topPanel.add(searchField, new GridBagConstraints(1, 0, GridBagConstraints.REMAINDER, 1, 1.0, 1.0, GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE, new Insets(1, 1, 6, 1), 0, 0));

                mainPanel.add(topPanel, BorderLayout.NORTH);

                final JEditorPane window = new JEditorPane();
                window.setEditorKit(new HTMLEditorKit());
                window.setBackground(Color.white);
                final JScrollPane pane = new JScrollPane(window);
                pane.getVerticalScrollBar().setBlockIncrement(200);
                pane.getVerticalScrollBar().setUnitIncrement(20);

                mainPanel.add(pane, BorderLayout.CENTER);

                final JFrame frame = new JFrame(Res.getString("title.history.for", jid));
                frame.setIconImage(SparkRes.getImageIcon(SparkRes.HISTORY_16x16).getImage());
                frame.getContentPane().setLayout(new BorderLayout());

                frame.getContentPane().add(mainPanel, BorderLayout.CENTER);
                frame.pack();
                frame.setSize(600, 400);
                window.setCaretPosition(0);
                window.requestFocus();
                GraphicUtils.centerWindowOnScreen(frame);
                frame.setVisible(true);

                window.setEditable(false);

                final StringBuilder builder = new StringBuilder();
                builder.append("<html><body><table cellpadding=0 cellspacing=0>");

                final TimerTask transcriptTask = new TimerTask() {
                    public void run() {
                        ChatTranscript transcript = (ChatTranscript)get();

                        // reduce the size of our transcript to the last 5000Messages
                        // This will prevent JavaOutOfHeap Errors
                        ArrayList<HistoryMessage> toobig = (ArrayList<HistoryMessage>) transcript.getMessage(null);

                        // Get the Maximum size from settingsfile
                        int maxsize = SettingsManager.getLocalPreferences().getMaximumHistory();
                        if (toobig.size() > maxsize)
                        {
                            transcript = new ChatTranscript();

                            for(int i = toobig.size()-1; i>=toobig.size()-maxsize;--i)
                            {
                        	transcript.addHistoryMessage(toobig.get(i));
                            }
                        }

                        final List<HistoryMessage> list = transcript.getMessage(
                		Res.getString("message.search.for.history").equals(searchField.getText())
                			? null : searchField.getText());


                        final String personalNickname = SparkManager.getUserManager().getNickname();
                        Date lastPost = null;
                        String lastPerson = null;
                        boolean initialized = false;
                        for (HistoryMessage message : list) {
                            String color = "blue";

                            String from = message.getFrom();
                            String nickname = SparkManager.getUserManager().getUserNicknameFromJID(message.getFrom());
                            String body = org.jivesoftware.spark.util.StringUtils.escapeHTMLTags(message.getBody());
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


                            int diff = 0;
			    if (DateUtils.getDaysDiff(lastPostTime, message
				    .getDate().getTime()) != 0) {
				diff = DateUtils.getDaysDiff(lastPostTime,
					message.getDate().getTime());
			    } else {
				diff = DateUtils.getDayOfWeek(lastPostTime)
					- DateUtils.getDayOfWeek(message
						.getDate().getTime());
			    }

                            if (diff != 0) {
                                if (initialized) {
                                    builder.append("<tr><td><br></td></tr>");
                                }
                                builder.append("<tr><td colspan=2><font size=4 color=gray><b><u>").append(notificationDateFormatter.format(message.getDate())).append("</u></b></font></td></tr>");
                                lastPerson = null;
                                initialized = true;
                            }

                            String value = "[" + messageDateFormatter.format(message.getDate()) + "]&nbsp;&nbsp;  ";

                            boolean newInsertions = lastPerson == null || !lastPerson.equals(nickname);
                            if (newInsertions) {
                                builder.append("<tr valign=top><td colspan=2 nowrap>");
                                builder.append("<font size=4 color='").append(color).append("'><b>");
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
                            builder.append("<b>").append(Res.getString("message.no.history.found")).append("</b>");
                        }

                        window.setText(builder.toString());
                        builder.replace(0, builder.length(), "");

                    }
                };

                searchField.addKeyListener(new KeyListener() {
        			@Override
        			public void keyTyped(KeyEvent e) {
        			}
        			@Override
        			public void keyReleased(KeyEvent e) {
        				if(e.getKeyChar() == KeyEvent.VK_ENTER) {
        	                TaskEngine.getInstance().schedule(transcriptTask, 10);
        	                searchField.requestFocus();
        				}
        			}
        			@Override
        			public void keyPressed(KeyEvent e) {

        			}
        		});
                searchField.addFocusListener(new FocusListener() {
                    public void focusGained(FocusEvent e) {
                    	searchField.setText("");
                    	searchField.setForeground((Color) UIManager.get("TextField.foreground"));
                    }

                    public void focusLost(FocusEvent e) {
                    	searchField.setForeground((Color) UIManager.get("TextField.lightforeground"));
                    	searchField.setText(Res.getString("message.search.for.history"));
                    }
                });

                TaskEngine.getInstance().schedule(transcriptTask, 10);

                frame.addWindowListener(new WindowAdapter() {
                    public void windowClosing(WindowEvent e) {
                        window.setText("");
                    }
                     @Override
                    public void windowClosed(WindowEvent e) {
                	frame.removeWindowListener(this);
                        frame.dispose();
                        transcriptTask.cancel();
                        topPanel.remove(vacardPanel);
                    }
                });
            }
        };



        transcriptLoader.start();
    }

    private void showStatusMessage(ContactItem item)
    {
   	 Frame = new JDialog();
   	 Frame.setTitle(item.getDisplayName() + " - Status");
   	 JPanel pane = new JPanel();
   	 JTextArea textArea = new JTextArea(5, 30);
   	 JButton btn_close = new JButton(Res.getString("button.close"));

   	 btn_close.addActionListener(new ActionListener()
	    {
	      public void actionPerformed(ActionEvent e)
	      {
	    	  	Frame.setVisible(false);
	      }
	    });

   	 textArea.setLineWrap(true);
   	 textArea.setWrapStyleWord(true);

   	 pane.add(new JScrollPane(textArea));
   	 Frame.setLayout(new BorderLayout());
   	 Frame.add(pane, BorderLayout.CENTER);
   	 Frame.add(btn_close, BorderLayout.SOUTH);

   	 textArea.setEditable(false);
   	 textArea.setText(item.getStatus());

   	 Frame.setLocationRelativeTo(SparkManager.getMainWindow());
   	 Frame.setBounds(Frame.getX() - 175, Frame.getY() - 75, 350, 150);
   	 Frame.setSize(350, 150);
   	 Frame.setResizable(false);
   	 Frame.setVisible(true);
    }


    /**
     * Sort HistoryMessages by date.
     */
    final Comparator<HistoryMessage> dateComparator = new Comparator<HistoryMessage>() {
        public int compare(HistoryMessage messageOne, HistoryMessage messageTwo) {

            long time1 = messageOne.getDate().getTime();
            long time2 = messageTwo.getDate().getTime();

            if (time1 < time2) {
                return 1;
            }
            else if (time1 > time2) {
                return -1;
            }
            return 0;

        }
    };

    private class ChatRoomDecorator implements ActionListener, ChatRoomClosingListener {

        private ChatRoom chatRoom;
        private ChatRoomButton chatHistoryButton;
        private final LocalPreferences localPreferences;

        public ChatRoomDecorator(ChatRoom chatRoom) {
            this.chatRoom = chatRoom;
            chatRoom.addClosingListener(this);

            // Add History Button
            localPreferences = SettingsManager.getLocalPreferences();
            if (!localPreferences.isChatHistoryEnabled()) {
                return;
            }
            chatHistoryButton = UIComponentRegistry.getButtonFactory().createChatTranscriptButton();
            chatRoom.addChatRoomButton(chatHistoryButton);
            chatHistoryButton.setToolTipText(Res.getString("tooltip.view.history"));
            chatHistoryButton.addActionListener(this);
        }


        public void closing() {
        	if (localPreferences.isChatHistoryEnabled()) {
        		chatHistoryButton.removeActionListener(this);
            }
            chatRoom.removeClosingListener(this);
            chatRoom = null;
            chatHistoryButton = null;
        }

        public void actionPerformed(ActionEvent e) {
            ChatRoomImpl roomImpl = (ChatRoomImpl)chatRoom;
            showHistory(roomImpl.getParticipantJID());
        }
    }


}

