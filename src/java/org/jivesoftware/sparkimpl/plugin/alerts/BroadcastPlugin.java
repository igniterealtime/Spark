/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkimpl.plugin.alerts;

import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.packet.DelayInformation;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.UserManager;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.component.tabbedPane.SparkTab;
import org.jivesoftware.spark.plugin.ContextMenuListener;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.ui.ChatContainer;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatRoomNotFoundException;
import org.jivesoftware.spark.ui.ContactGroup;
import org.jivesoftware.spark.ui.ContactItem;
import org.jivesoftware.spark.ui.ContactList;
import org.jivesoftware.spark.ui.MessageListener;
import org.jivesoftware.spark.ui.SparkTabHandler;
import org.jivesoftware.spark.ui.TranscriptWindow;
import org.jivesoftware.spark.ui.rooms.ChatRoomImpl;
import org.jivesoftware.spark.ui.status.StatusBar;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.manager.Enterprise;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

/**
 * Handles broadcasts from server and allows for roster wide broadcasts.
 */
public class BroadcastPlugin extends SparkTabHandler implements Plugin, PacketListener {

    private Set<ChatRoom> broadcastRooms = new HashSet<ChatRoom>();

    public void initialize() {
        boolean enabled = Enterprise.containsFeature(Enterprise.BROADCAST_FEATURE);
        if (!enabled) {
            return;
        }

        // Add as ContainerDecoratr
        SparkManager.getChatManager().addSparkTabHandler(this);

        PacketFilter serverFilter = new PacketTypeFilter(Message.class);
        SparkManager.getConnection().addPacketListener(this, serverFilter);

        // Register with action menu
        final JMenu actionsMenu = SparkManager.getMainWindow().getMenuByName(Res.getString("menuitem.actions"));
        JMenuItem broadcastMenu = new JMenuItem(Res.getString("title.broadcast.message"), SparkRes.getImageIcon(SparkRes.MEGAPHONE_16x16));
        ResourceUtils.resButton(broadcastMenu, Res.getString("title.broadcast.message"));
        actionsMenu.add(broadcastMenu);
        broadcastMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                broadcastToRoster();
            }
        });

        // Register with action menu
        JMenuItem startConversationtMenu = new JMenuItem("", SparkRes.getImageIcon(SparkRes.SMALL_MESSAGE_IMAGE));
        ResourceUtils.resButton(startConversationtMenu, Res.getString("menuitem.start.a.chat"));
        actionsMenu.add(startConversationtMenu);
        startConversationtMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ContactList contactList = SparkManager.getWorkspace().getContactList();
                Collection selectedUsers = contactList.getSelectedUsers();
                String selectedUser = "";
                Iterator selectedUsersIterator = selectedUsers.iterator();
                if (selectedUsersIterator.hasNext()) {
                    ContactItem contactItem = (ContactItem)selectedUsersIterator.next();
                    selectedUser = contactItem.getJID();
                }

                String jid = (String)JOptionPane.showInputDialog(SparkManager.getMainWindow(), Res.getString("label.enter.address"), Res.getString("title.start.chat"), JOptionPane.QUESTION_MESSAGE, null, null, selectedUser);
                if (ModelUtil.hasLength(jid) && ModelUtil.hasLength(StringUtils.parseServer(jid))) {
                    if (ModelUtil.hasLength(jid) && jid.indexOf('@') == -1) {
                        // Append server address
                        jid = jid + "@" + SparkManager.getConnection().getServiceName();
                    }

                    String nickname = SparkManager.getUserManager().getUserNicknameFromJID(jid);

                    jid = UserManager.escapeJID(jid);
                    ChatRoom chatRoom = SparkManager.getChatManager().createChatRoom(jid, nickname, nickname);
                    SparkManager.getChatManager().getChatContainer().activateChatRoom(chatRoom);
                }
            }
        });

        // Add send to selected users.
        final ContactList contactList = SparkManager.getWorkspace().getContactList();
        contactList.addContextMenuListener(new ContextMenuListener() {
            public void poppingUp(Object component, JPopupMenu popup) {
                if (component instanceof ContactGroup) {
                    final ContactGroup group = (ContactGroup)component;
                    Action broadcastMessageAction = new AbstractAction() {
                        public void actionPerformed(ActionEvent e) {
                            broadcastToGroup(group);
                        }
                    };

                    broadcastMessageAction.putValue(Action.NAME, Res.getString("menuitem.broadcast.to.group"));
                    broadcastMessageAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.MEGAPHONE_16x16));
                    popup.add(broadcastMessageAction);
                }
            }

            public void poppingDown(JPopupMenu popup) {

            }

            public boolean handleDefaultAction(MouseEvent e) {
                return false;
            }
        });

        // Add Broadcast to roster
        StatusBar statusBar = SparkManager.getWorkspace().getStatusBar();
        final JPanel commandPanel = SparkManager.getWorkspace().getCommandPanel();

        RolloverButton broadcastToRosterButton = new RolloverButton(SparkRes.getImageIcon(SparkRes.MEGAPHONE_16x16));
        broadcastToRosterButton.setToolTipText(Res.getString("message.send.a.broadcast"));

        // Add Broadcast button to command panel.
        commandPanel.add(broadcastToRosterButton);

        statusBar.invalidate();
        statusBar.validate();
        statusBar.repaint();

        broadcastToRosterButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                broadcastToRoster();
            }
        });
    }

    public void shutdown() {

    }

    public boolean canShutDown() {
        return false;
    }

    public void processPacket(final Packet packet) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    final Message message = (Message)packet;

                    // Do not handle errors or offline messages
                    final DelayInformation offlineInformation = (DelayInformation)message.getExtension("x", "jabber:x:delay");
                    if (offlineInformation != null || message.getError() != null) {
                        return;
                    }

                    boolean broadcast = message.getProperty("broadcast") != null;

                    if ((broadcast || message.getType() == Message.Type.normal) && message.getBody() != null) {
                        showAlert((Message)packet);
                    }
                    else if (message.getType() == Message.Type.headline && message.getBody() != null) {
                        SparkToaster toaster = new SparkToaster();
                        toaster.setDisplayTime(30000);
                        toaster.setBorder(BorderFactory.createBevelBorder(0));
                        toaster.setTitle(Res.getString("title.notification"));
                        toaster.showToaster(message.getBody());
                    }
                    else {
                        String host = SparkManager.getSessionManager().getServerAddress();
                        String from = packet.getFrom() != null ? packet.getFrom() : "";
                        if (host.equalsIgnoreCase(from) || !ModelUtil.hasLength(from)) {
                            showAlert((Message)packet);
                        }
                    }
                }
                catch (Exception e) {
                    Log.error(e);
                }
            }
        });

    }

    /**
     * Show Server Alert.
     *
     * @param message the message to show.
     */
    private void showAlert(Message message) {
        // Do not show alert if the message is an error.
        if (message.getError() != null) {
            return;
        }

        final String body = message.getBody();
        String subject = message.getSubject();

        StringBuffer buf = new StringBuffer();
        if (subject != null) {
            buf.append(Res.getString("subject")).append(": ").append(subject);
            buf.append("\n\n");
        }

        buf.append(body);

        String from = message.getFrom() != null ? message.getFrom() : "";

        final TranscriptWindow window = new TranscriptWindow();
        window.insertPrefixAndMessage(null, buf.toString(), ChatManager.TO_COLOR);

        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());
        p.add(window, BorderLayout.CENTER);
        p.setBorder(BorderFactory.createLineBorder(Color.lightGray));


        SparkToaster toaster = new SparkToaster();
        toaster.setDisplayTime(30000);
        toaster.setBorder(BorderFactory.createBevelBorder(0));


        if (!from.contains("@")) {
            ChatManager chatManager = SparkManager.getChatManager();
            ChatContainer container = chatManager.getChatContainer();

            ChatRoomImpl chatRoom;
            try {
                chatRoom = (ChatRoomImpl)container.getChatRoom(from);
            }
            catch (ChatRoomNotFoundException e) {
                chatRoom = new ChatRoomImpl("serveralert@" + from, Res.getString("broadcast"), Res.getString("broadcast"));
                chatRoom.getBottomPanel().setVisible(false);
                chatRoom.getToolBar().setVisible(false);
                SparkManager.getChatManager().getChatContainer().addChatRoom(chatRoom);
            }


            chatRoom.insertMessage(message);
            broadcastRooms.add(chatRoom);
        }
        else if (message.getFrom() != null) {
            String jid = StringUtils.parseBareAddress(from);
            String nickname = SparkManager.getUserManager().getUserNicknameFromJID(jid);
            ChatManager chatManager = SparkManager.getChatManager();
            ChatContainer container = chatManager.getChatContainer();

            ChatRoomImpl chatRoom;
            try {
                chatRoom = (ChatRoomImpl)container.getChatRoom(jid);
            }
            catch (ChatRoomNotFoundException e) {
                chatRoom = new ChatRoomImpl(jid, nickname, nickname);
                SparkManager.getChatManager().getChatContainer().addChatRoom(chatRoom);
            }

            chatRoom.insertMessage(message);
            broadcastRooms.add(chatRoom);

            chatRoom.addMessageListener(new MessageListener() {
                boolean waiting = true;

                public void messageReceived(ChatRoom room, Message message) {
                    removeAsBroadcast(room);
                }

                public void messageSent(ChatRoom room, Message message) {
                    removeAsBroadcast(room);
                }

                private void removeAsBroadcast(ChatRoom room) {
                    if (waiting) {
                        broadcastRooms.remove(room);

                        // Notify decorators
                        SparkManager.getChatManager().notifySparkTabHandlers(room);
                        waiting = false;
                    }
                }
            });
        }
    }

    /**
     * Broadcasts a message to all in the roster.
     */
    private void broadcastToRoster() {
        final BroadcastDialog broadcastDialog = new BroadcastDialog();
        broadcastDialog.invokeDialog();
    }

    /**
     * Broadcasts a message to all selected users.
     *
     * @param group the Contact Group to send the messages to.
     */
    private void broadcastToGroup(ContactGroup group) {
        final BroadcastDialog broadcastDialog = new BroadcastDialog();
        broadcastDialog.invokeDialog(group);
    }

    public void uninstall() {
        // Do nothing.
    }


    public boolean isTabHandled(SparkTab tab, Component component, boolean isSelectedTab, boolean chatFrameFocused) {
        if (component instanceof ChatRoom) {
            ChatRoom chatroom = (ChatRoom)component;
            if (broadcastRooms.contains(chatroom)) {
                final ChatRoomImpl room = (ChatRoomImpl)component;
                tab.setIcon(SparkRes.getImageIcon(SparkRes.INFORMATION_IMAGE));
                String nickname = room.getTabTitle();
                nickname = Res.getString("message.broadcast.from", nickname);
                tab.setTabTitle(nickname);


                if ((!chatFrameFocused || !isSelectedTab) && room.getUnreadMessageCount() > 0) {
                    // Make tab red.
                    tab.setTitleColor(Color.red);
                    tab.setTabBold(true);
                }
                else {
                    tab.setTitleColor(Color.black);
                    tab.setTabFont(tab.getDefaultFont());
                    room.clearUnreadMessageCount();
                }


                return true;
            }
        }

        return false;
    }
}
