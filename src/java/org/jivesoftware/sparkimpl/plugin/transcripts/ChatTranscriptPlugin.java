/**
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.jivesoftware.MainWindowListener;
import org.jivesoftware.resource.Default;
import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.jiveproperties.packet.JivePropertiesExtension;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.plugin.ContextMenuListener;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatRoomButton;
import org.jivesoftware.spark.ui.ChatRoomClosingListener;
import org.jivesoftware.spark.ui.ChatRoomListener;
import org.jivesoftware.spark.ui.ContactItem;
import org.jivesoftware.spark.ui.ContactList;
import org.jivesoftware.spark.ui.rooms.ChatRoomImpl;
import org.jivesoftware.spark.util.UIComponentRegistry;
import org.jivesoftware.sparkimpl.plugin.manager.Enterprise;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

/**
 * The <code>ChatTranscriptPlugin</code> is responsible for transcript handling within Spark.
 *
 * @author Derek DeMoro
 */
public class ChatTranscriptPlugin implements ChatRoomListener {

    private final SimpleDateFormat notificationDateFormatter;
    private final SimpleDateFormat messageDateFormatter;
    private HashMap<String,Message> lastMessage = new HashMap<>();
    private JDialog Frame;
    private HistoryTranscript transcript = null;
    /**
     * Register the listeners for transcript persistence.
     */
    public ChatTranscriptPlugin() {
        SparkManager.getChatManager().addChatRoomListener(this);

        String dateFormat = ( (SimpleDateFormat) SimpleDateFormat.getDateInstance( SimpleDateFormat.FULL ) ).toPattern();
        notificationDateFormatter = new SimpleDateFormat( dateFormat );
        String timeFormat = "HH:mm:ss";
        messageDateFormatter = new SimpleDateFormat( timeFormat );

        final ContactList contactList = SparkManager.getWorkspace().getContactList();

        final Action viewHistoryAction = new AbstractAction() {
			private static final long serialVersionUID = -6498776252446416099L;

			public void actionPerformed(ActionEvent actionEvent) {
                ContactItem item = contactList.getSelectedUsers().iterator().next();
                final String jid = item.getJID();
                transcript = new HistoryTranscript(notificationDateFormatter, messageDateFormatter);
                transcript.showHistory(jid);
                //showHistory(jid);
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
                	if (!Default.getBoolean("HISTORY_DISABLED") && Enterprise.containsFeature(Enterprise.HISTORY_SETTINGS_FEATURE)) popup.add(viewHistoryAction);
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
            @Override
            public void connected( XMPPConnection xmppConnection ) {
            }

            @Override
            public void authenticated( XMPPConnection xmppConnection, boolean b ) {
            }

            @Override
            public void connectionClosed() {
            }

            @Override
            public void connectionClosedOnError(Exception e) {
                persistConversations();
            }

            @Override
            public void reconnectingIn(int i) {
            }

            @Override
            public void reconnectionSuccessful() {
            }

            @Override
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
    	if (lastMessage.get(jid) != null)
    	{
    		count = transcripts.indexOf(lastMessage.get(jid)) + 1;
    	}
        for (Message message : transcripts) {
        	if (i < count)
        	{
            	i++;
        		continue;
        	}
      	  	lastMessage.put(jid,message);
            HistoryMessage history = new HistoryMessage();
            history.setTo(message.getTo());
            history.setFrom(message.getFrom());
            history.setBody(message.getBody());
            final JivePropertiesExtension extension = ((JivePropertiesExtension) message.getExtension( JivePropertiesExtension.NAMESPACE ));
            Date date = null;
            if ( extension != null ) {
                date = (Date) extension.getProperty( "date" );
            }
            history.setDate( date == null ? new Date() : date );
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

    private void showStatusMessage(ContactItem item)
    {
   	 Frame = new JDialog();
   	 Frame.setTitle(item.getDisplayName() + " - Status");
   	 JPanel pane = new JPanel();
   	 JTextArea textArea = new JTextArea(5, 30);
   	 JButton btn_close = new JButton(Res.getString("button.close"));

   	 btn_close.addActionListener( e -> Frame.setVisible(false) );

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
    final Comparator<HistoryMessage> dateComparator = ( messageOne, messageTwo ) -> {

        long time1 = messageOne.getDate().getTime();
        long time2 = messageTwo.getDate().getTime();

        if (time1 < time2) {
            return 1;
        }
        else if (time1 > time2) {
            return -1;
        }
        return 0;

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
            if (!Default.getBoolean("HISTORY_DISABLED") && Enterprise.containsFeature(Enterprise.HISTORY_SETTINGS_FEATURE)) chatRoom.addChatRoomButton(chatHistoryButton);
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
        	transcript = new HistoryTranscript(notificationDateFormatter, messageDateFormatter);
        	transcript.showHistory(roomImpl.getParticipantJID());
        }
    }


}

