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
package org.jivesoftware.sparkimpl.plugin.alerts;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;

import org.jivesoftware.resource.Default;
import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Message.Type;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.delay.packet.DelayInformation;
import org.jivesoftware.smackx.jiveproperties.packet.JivePropertiesExtension;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.SparkManager;
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
import org.jivesoftware.sparkimpl.preference.sounds.SoundPreference;
import org.jivesoftware.sparkimpl.preference.sounds.SoundPreferences;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.util.XmppStringUtils;
import org.jivesoftware.spark.ui.BroadcastHistoryFrame;

/**
 * Handles broadcasts from server and allows for roster wide broadcasts.
 */
public class BroadcastPlugin extends SparkTabHandler implements Plugin, StanzaListener {

    private final Set<ChatRoom> broadcastRooms = new HashSet<>();

    @Override
    public void initialize() {
        // See if we should disable all "Broadcast" menu items
        if (Default.getBoolean(Default.DISABLE_BROADCAST_MENU_ITEM) || !Enterprise.containsFeature(Enterprise.BROADCAST_FEATURE)) {
            return;
        }

        // Add as ContainerDecoratr
        SparkManager.getChatManager().addSparkTabHandler(this);

        StanzaFilter serverFilter = new StanzaTypeFilter(Message.class);
        SparkManager.getConnection().addAsyncStanzaListener(this, serverFilter);

        // Register with action menu
        final JMenu actionsMenu = SparkManager.getMainWindow().getMenuByName(Res.getString("menuitem.actions"));
        JMenuItem broadcastHistoryMenu = new JMenuItem(Res.getString("title.broadcast.history"), SparkRes.getImageIcon(SparkRes.HISTORY_16x16));
        JMenuItem broadcastMenu = new JMenuItem(Res.getString("title.broadcast.message"), SparkRes.getImageIcon(SparkRes.MEGAPHONE_16x16));
        ResourceUtils.resButton(broadcastMenu, Res.getString("title.broadcast.message"));
        actionsMenu.add(broadcastHistoryMenu);
        actionsMenu.add(broadcastMenu);

        broadcastMenu.addActionListener(e -> broadcastToRoster());
        broadcastHistoryMenu.addActionListener(e -> new BroadcastHistoryFrame().run());
        // Register with action menu
        JMenuItem startConversationtMenu = new JMenuItem("", SparkRes.getImageIcon(SparkRes.SMALL_MESSAGE_IMAGE));
        ResourceUtils.resButton(startConversationtMenu, Res.getString("menuitem.start.a.chat"));
        if (!Default.getBoolean(Default.HIDE_START_A_CHAT) && Enterprise.containsFeature(Enterprise.START_A_CHAT_FEATURE)) {
            actionsMenu.add(startConversationtMenu, 0);
        }
        startConversationtMenu.addActionListener(e -> {
            ContactList contactList = SparkManager.getWorkspace().getContactList();
            Collection<ContactItem> selectedUsers = contactList.getSelectedUsers();
            String selectedUser = "";
            Iterator<ContactItem> selectedUsersIterator = selectedUsers.iterator();
            if (selectedUsersIterator.hasNext()) {
                ContactItem contactItem = selectedUsersIterator.next();
                selectedUser = contactItem.getJid().toString();
            }

            UIManager.put("OptionPane.okButtonText", Res.getString("ok"));
            UIManager.put("OptionPane.cancelButtonText", Res.getString("cancel"));

            String jid = (String) JOptionPane.showInputDialog(SparkManager.getMainWindow(), Res.getString("label.enter.address"), Res.getString("title.start.chat"), JOptionPane.QUESTION_MESSAGE, null, null, selectedUser);
            if (ModelUtil.hasLength(jid) && ModelUtil.hasLength(XmppStringUtils.parseDomain(jid))) {
                if (ModelUtil.hasLength(jid) && jid.indexOf('@') == -1) {
                    // Append server address
                    jid = jid + "@" + SparkManager.getConnection().getXMPPServiceDomain();
                }

                EntityBareJid entityBareJid = JidCreate.entityBareFromUnescapedOrThrowUnchecked(jid);

                String nickname = SparkManager.getUserManager().getUserNicknameFromJID(entityBareJid);

                ChatRoom chatRoom = SparkManager.getChatManager().createChatRoom(entityBareJid, nickname, nickname);
                SparkManager.getChatManager().getChatContainer().activateChatRoom(chatRoom);
            }
        });

        // Add send to selected users.
        final ContactList contactList = SparkManager.getWorkspace().getContactList();
        contactList.addContextMenuListener(new ContextMenuListener() {
            @Override
            public void poppingUp(Object component, JPopupMenu popup) {
                if (component instanceof ContactGroup) {
                    final ContactGroup group = (ContactGroup) component;
                    Action broadcastMessageAction = new AbstractAction() {
                        private static final long serialVersionUID = -6411248110270296726L;

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            broadcastToGroup(group);
                        }
                    };

                    broadcastMessageAction.putValue(Action.NAME, Res.getString("menuitem.broadcast.to.group"));
                    broadcastMessageAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.MEGAPHONE_16x16));

                    popup.add(broadcastMessageAction);
                }
            }

            @Override
            public void poppingDown(JPopupMenu popup) {

            }

            @Override
            public boolean handleDefaultAction(MouseEvent e) {
                return false;
            }
        });

        // Add Broadcast to roster
        StatusBar statusBar = SparkManager.getWorkspace().getStatusBar();

        RolloverButton broadcastToRosterButton = new RolloverButton(SparkRes.getImageIcon(SparkRes.MEGAPHONE_16x16));
        broadcastToRosterButton.setToolTipText(Res.getString("message.send.a.broadcast"));

        statusBar.invalidate();
        statusBar.validate();
        statusBar.repaint();

        broadcastToRosterButton.addActionListener(e -> broadcastToRoster());
    }

    @Override
    public void shutdown() {

    }

    @Override
    public boolean canShutDown() {
        return false;
    }

    @Override
    public void processStanza(final Stanza stanza) {
        SwingUtilities.invokeLater(() -> {
            try {
                final Message message = (Message) stanza;

                // Do not handle errors or offline messages
                if (message.hasExtension("delay", "urn:xmpp:delay") || message.getError() != null) {
                    return;
                }

                final JivePropertiesExtension extension = ((JivePropertiesExtension) message.getExtension(JivePropertiesExtension.NAMESPACE));
                final boolean broadcast = extension != null && extension.getProperty("broadcast") != null;

                if ((broadcast || message.getType() == Type.normal
                        || message.getType() == Type.headline) && message.getBody() != null) {
                    showAlert((Message) stanza);
                } else {
                    DomainBareJid host = SparkManager.getSessionManager().getServerAddress();
                    Jid from = stanza.getFrom();
                    if (host.equals(from)) {
                        showAlert((Message) stanza);
                    }
                }
            } catch (Exception e) {
                Log.error(e);
            }
        });

    }

    /**
     * Show Server Alert.
     *
     * @param message the message to show.
     */
    private void showAlert(Message message) {
        Type type = message.getType();
        // Do not show alert if the message is an error.
        if (message.getError() != null) {
            return;
        }

        final String body = message.getBody();
        String subject = message.getSubject();

        StringBuilder buf = new StringBuilder();
        if (subject != null) {
            buf.append(Res.getString("subject")).append(": ").append(subject);
            buf.append("\n\n");
        }

        buf.append(body);

        Jid from = message.getFrom();

        final TranscriptWindow window = new TranscriptWindow();
        window.insertNotificationMessage(buf.toString(), ChatManager.TO_COLOR);

        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());
        p.add(window, BorderLayout.CENTER);
        p.setBorder(BorderFactory.createLineBorder(Color.lightGray));

        // Count the number of linebreaks <br> and \n
        String s = message.getBody();
        s = s.replace("<br/>", "\n");
        s = s.replace("<br>", "\n");
        int linebreaks = org.jivesoftware.spark.util.StringUtils.
                countNumberOfOccurences(s, '\n');

        //If the message is blank, then it should not be displayed.
        if(org.apache.commons.lang3.StringUtils.isBlank(s)){
            return;
        }

        // Currently Serverbroadcasts dont contain Subjects, so this might be a MOTD message
        boolean mightbeMOTD = message.getSubject() != null;

        if (!from.hasLocalpart()) {
            // if theres no "@" it means the message came from the server
            if (Default.getBoolean(Default.BROADCAST_IN_CHAT_WINDOW)
                    || linebreaks > 20 || message.getBody().length() > 1000 || mightbeMOTD) {
                // if we have more than 20 linebreaks or the message is longer
                // than 1000characters we should broadcast
                // in a normal chatwindow
                broadcastInChat(message);
            } else {
                broadcastWithPanel(message);
            }

        } else if (message.getFrom() != null) {
            userToUserBroadcast(message, type, from);
        }
    }

    /**
     * Handles Broadcasts made from a user to another user
     *
     * @param message the message
     * @param type the message type
     * @param from the sender
     */
    private void userToUserBroadcast(Message message, Type type, Jid from) {
        EntityBareJid jid = from.asEntityBareJidOrThrow();
        Resourcepart nickname = SparkManager.getUserManager().getUserNicknameAsResourcepartFromJID(jid);
        ChatManager chatManager = SparkManager.getChatManager();
        ChatContainer container = chatManager.getChatContainer();

        ChatRoomImpl chatRoom;
        try {
            chatRoom = (ChatRoomImpl) container.getChatRoom(jid);
        } catch (ChatRoomNotFoundException e) {
            chatRoom = new ChatRoomImpl(jid, nickname, nickname);
            SparkManager.getChatManager().getChatContainer().addChatRoom(chatRoom);
        }

        Message m = new Message();
        m.setBody(message.getBody());
        m.setTo(message.getTo());
        m.setFrom(message.getFrom());

        String broadcasttype = type == Message.Type.normal ? Res.getString("broadcast") : Res.getString("message.alert.notify");

        chatRoom.getTranscriptWindow().insertMessage((nickname + " - " + broadcasttype), message, ChatManager.FROM_COLOR);
        chatRoom.addToTranscript(m, true);
        chatRoom.increaseUnreadMessageCount();
        broadcastRooms.add(chatRoom);

        LocalPreferences pref = SettingsManager.getLocalPreferences();
        if (pref.getShowToasterPopup()) {
            SparkToaster toaster = new SparkToaster();
            toaster.setDisplayTime(30000);
            toaster.setBorder(BorderFactory.createLineBorder(Color.lightGray, 1, true));
            toaster.setTitle(nickname + " - " + broadcasttype);
            toaster.showToaster(message.getBody());
        }

        SparkManager.getChatManager().fireGlobalMessageReceievedListeners(chatRoom, message);

        if (message.hasExtension("delay", "urn:xmpp:delay")) {
            SoundPreference soundPreference = (SoundPreference) SparkManager.getPreferenceManager().getPreference(new SoundPreference().getNamespace());
            SoundPreferences preferences = soundPreference.getPreferences();
            if (preferences.isPlayIncomingSound()) {
                File incomingFile = new File(preferences.getIncomingSound());
                SparkManager.getSoundManager().playClip(incomingFile);
            }
        }

        chatRoom.addMessageListener(new MessageListener() {
            boolean waiting = true;

            @Override
            public void messageReceived(ChatRoom room, Message message) {
                removeAsBroadcast(room);
            }

            @Override
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

    @Override
    public void uninstall() {
        // Do nothing.
    }

    @Override
    public boolean isTabHandled(SparkTab tab, Component component, boolean isSelectedTab, boolean chatFrameFocused) {
        if (component instanceof ChatRoom) {
            ChatRoom chatroom = (ChatRoom) component;
            if (broadcastRooms.contains(chatroom)) {
                final ChatRoomImpl room = (ChatRoomImpl) component;
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

    /**
     * Displays the Serverbroadcast like all other messages in its on
     * chatcontainer with transcript history
     *
     * @param message
     */
    private void broadcastInChat(Message message) {
        String from = message.getFrom() != null ? message.getFrom().toString() : "";

        String windowtitle = message.getSubject() != null ? message.getSubject() : Res.getString("administrator");
        EntityBareJid jid = JidCreate.entityBareFromOrThrowUnchecked("serveralert@" + from);
        Resourcepart resourcepart = Resourcepart.fromOrThrowUnchecked(Res.getString("broadcast"));
        ChatRoomImpl chatRoom = new ChatRoomImpl(jid, resourcepart, windowtitle);
        chatRoom.getBottomPanel().setVisible(false);
        chatRoom.hideToolbar();
        SparkManager.getChatManager().getChatContainer().addChatRoom(chatRoom);

        chatRoom.getTranscriptWindow().insertNotificationMessage(message.getBody(), ChatManager.NOTIFICATION_COLOR);
        broadcastRooms.add(chatRoom);
    }

    private String linkCreator(String message) {
        message = message.replace("\n", "<br/>");
        String[] lines = message.split("\\s+");
        StringBuilder html = new StringBuilder();
        for (String line : lines) {
            if (line.startsWith("www")) {
                line = "http://" + line;
            }
            if (line.startsWith("http:") || line.startsWith("https:")) {
                line = "<a href='" + line + "'>" + line + "</a>";
                //  final JLabel label = new JLabel("<html><a href=\" " + strURL + "\"> click </a></html>");
            }
            html.append(" ").append(line);
        }
        return html.toString();
    }

    /**
     * Displays a Serverbroadcast within a JFrame<br>
     * Messages can contain html-tags
     *
     * @param message
     */
    private void broadcastWithPanel(Message message) {

        String title = Res.getString("message.broadcast.from",
                Res.getString("administrator"));
        final JFrame alert = new JFrame(title);

        alert.setLayout(new GridBagLayout());
        alert.setIconImage(SparkRes.getImageIcon(SparkRes.MAIN_IMAGE)
                .getImage());
        String mylink = linkCreator(message.getBody());

        JLabel icon = new JLabel(SparkRes.getImageIcon(SparkRes.ALERT));

        JButton close = new JButton(Res.getString("close"));

        close.addActionListener(new AbstractAction() {
            private static final long serialVersionUID = -3822361866008590946L;

            @Override
            public void actionPerformed(ActionEvent e) {
                alert.setVisible(false);
                alert.dispose();
            }
        });
        final JEditorPane textPane = new JEditorPane();
        textPane.setEditable(false);
        textPane.setContentType("text/html");
        textPane.setText(mylink);

        textPane.addHyperlinkListener(e -> {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                try {
                    try {
                        Log.error(e.getURL().toString());
                        Desktop.getDesktop().browse(new URI(e.getURL().toString()));
                    } catch (IOException ex) {
                        Log.error(ex.getCause());
                    }
                } catch (URISyntaxException ex) {
                    Log.error(ex.getCause());
                }
            }
        });
        JScrollPane scrollPane = new JScrollPane(textPane);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        textPane.setCaretPosition(0);

        alert.add(icon, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        alert.add(scrollPane, new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
        alert.add(close, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

        alert.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        alert.setVisible(true);

        alert.setMinimumSize(new Dimension(340, 200));
        alert.setPreferredSize(new Dimension(340,200));
        alert.pack();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (dim.width - alert.getSize().width) / 2;
        int y = (dim.height - alert.getSize().height) / 2;
        alert.setLocation(x, y);
        alert.toFront();
        alert.requestFocus();
    }
}
