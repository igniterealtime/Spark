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
package org.jivesoftware.spark;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.TimerTask;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.jivesoftware.MainWindow;
import org.jivesoftware.MainWindowListener;
import org.jivesoftware.Spark;
import org.jivesoftware.resource.Default;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.debugger.EnhancedDebuggerWindow;
import org.jivesoftware.smackx.delay.packet.DelayInformation;
import org.jivesoftware.smackx.jiveproperties.packet.JivePropertiesExtension;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jivesoftware.spark.component.tabbedPane.SparkTabbedPane;
import org.jivesoftware.spark.filetransfer.SparkTransferManager;
import org.jivesoftware.spark.search.SearchManager;
import org.jivesoftware.spark.ui.ChatContainer;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatRoomNotFoundException;
import org.jivesoftware.spark.ui.CommandPanel;
import org.jivesoftware.spark.ui.ContactItem;
import org.jivesoftware.spark.ui.ContactList;
import org.jivesoftware.spark.ui.conferences.ConferenceServices;
import org.jivesoftware.spark.ui.status.StatusBar;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.TaskEngine;
import org.jivesoftware.spark.util.UIComponentRegistry;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.alerts.BroadcastPlugin;
import org.jivesoftware.sparkimpl.plugin.bookmarks.BookmarkPlugin;
import org.jivesoftware.sparkimpl.plugin.gateways.GatewayPlugin;
import org.jivesoftware.sparkimpl.plugin.manager.Enterprise;
import org.jivesoftware.sparkimpl.plugin.transcripts.ChatTranscriptPlugin;
import org.jxmpp.util.XmppStringUtils;

/**
 * The inner Container for Spark. The Workspace is the container for all plugins into the Spark
 * install. Plugins would use this for the following:
 * <p/>
 * <ul>
 * <li>Add own tab to the main tabbed pane. ex.
 * <p/>
 * <p/>
 * Workspace workspace = SparkManager.getWorkspace();
 * JButton button = new JButton("HELLO SPARK USERS");
 * workspace.getWorkspacePane().addTab("MyPlugin", button);
 * </p>
 * <p/>
 * <li>Retrieve the ContactList.
 */
public class Workspace extends JPanel implements StanzaListener {

	private static final long serialVersionUID = 7076407890063933765L;
	private SparkTabbedPane workspacePane;
    private StatusBar statusBox;

    private ContactList contactList;
    private ConferenceServices conferences;
    private GatewayPlugin gatewayPlugin;
    private BookmarkPlugin bookmarkPlugin;
    private ChatTranscriptPlugin transcriptPlugin;
    private BroadcastPlugin broadcastPlugin;

    private static Workspace singleton;
    private static final Object LOCK = new Object();

    private JPanel cardPanel;
    private CardLayout cardLayout;

    public static final String WORKSPACE_PANE = "WORKSPACE_PANE";


    /**
     * Returns the singleton instance of <CODE>Workspace</CODE>,
     * creating it if necessary.
     * <p/>
     *
     * @return the singleton instance of <Code>Workspace</CODE>
     */
    public static Workspace getInstance() {
        // Synchronize on LOCK to ensure that we don't end up creating
        // two singletons.
        synchronized (LOCK) {
            if (null == singleton) {
                Workspace controller = new Workspace();
                singleton = controller;
                return controller;
            }
        }
        return singleton;
    }


    /**
     * Creates the instance of the SupportChatWorkspace.
     */
    private Workspace() {
        final MainWindow mainWindow = SparkManager.getMainWindow();

	        // Add MainWindow listener
	        mainWindow.addMainWindowListener(new MainWindowListener() {
	            public void shutdown() {
	                final ChatContainer container = SparkManager.getChatManager().getChatContainer();
	                // Close all Chats.
	                for (ChatRoom chatRoom : container.getChatRooms()) {
	                    // Leave ChatRoom
	                    container.leaveChatRoom(chatRoom);
	                }

	                conferences.shutdown();
	                gatewayPlugin.shutdown();
	                bookmarkPlugin.shutdown();
	                broadcastPlugin.shutdown();
	            }

	            public void mainWindowActivated() {

	            }

	            public void mainWindowDeactivated() {

	            }
	        });


        // Initialize workspace pane, defaulting the tabs to the bottom.
	    boolean top = Default.getBoolean(Default.TABS_PLACEMENT_TOP);
        workspacePane = UIComponentRegistry.createWorkspaceTabPanel(top ? JTabbedPane.TOP : JTabbedPane.BOTTOM);
        workspacePane.setBorder(BorderFactory.createEmptyBorder());
        // Add Panels.
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setOpaque(false);
        cardPanel.add(WORKSPACE_PANE, this);

        statusBox = UIComponentRegistry.createStatusBar();

        // Build default workspace
        this.setLayout(new GridBagLayout());
        add(workspacePane, new GridBagConstraints(0, 9, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        add(statusBox, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));


        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F12"), "showDebugger");
        this.getActionMap().put("showDebugger", new AbstractAction("showDebugger") {
			private static final long serialVersionUID = 4066886679016416923L;

			public void actionPerformed(ActionEvent evt) {
                EnhancedDebuggerWindow window = EnhancedDebuggerWindow.getInstance();
                window.setVisible(true);
            }
        });

        // Set background
        setBackground(new Color(235, 239, 254));
    }

    /**
     * Builds the Workspace layout.
     */
    public void buildLayout() {
        new Enterprise();

        // Initialize Contact List
        contactList = UIComponentRegistry.createContactList();
        conferences = UIComponentRegistry.createConferenceServices();

        // Init contact list.
        contactList.initialize();

        // Load VCard information for status box
        statusBox.loadVCard();

        // Initialise TransferManager
        SparkTransferManager.getInstance();
    }

    /**
     * Starts the Loading of all Spark Plugins.
     */
    public void loadPlugins() {
    
        // Send Available status
        SparkManager.getSessionManager().changePresence(statusBox.getPresence());
        
        // Add presence and message listeners
        // we listen for these to force open a 1-1 peer chat window from other operators if
        // one isn't already open
        StanzaFilter workspaceMessageFilter = new StanzaTypeFilter(Message.class);

        // Add the packetListener to this instance
        SparkManager.getSessionManager().getConnection().addAsyncStanzaListener(this, workspaceMessageFilter);

        // Make presence available to anonymous requests, if from anonymous user in the system.
        StanzaListener workspacePresenceListener = stanza -> {
            Presence presence = (Presence)stanza;
            JivePropertiesExtension extension = (JivePropertiesExtension) presence.getExtension( JivePropertiesExtension.NAMESPACE );
            if (extension != null && extension.getProperty("anonymous") != null) {
                boolean isAvailable = statusBox.getPresence().getMode() == Presence.Mode.available;
                Presence reply = new Presence(Presence.Type.available);
                if (!isAvailable) {
                    reply.setType(Presence.Type.unavailable);
                }
                reply.setTo(presence.getFrom());
                try
                {
                    SparkManager.getSessionManager().getConnection().sendStanza(reply);
                }
                catch ( SmackException.NotConnectedException e )
                {
                    Log.warning( "Unable to send presence reply to " + reply.getTo(), e );
                }
            }
        };

        SparkManager.getSessionManager().getConnection().addAsyncStanzaListener(workspacePresenceListener, new StanzaTypeFilter(Presence.class));

        // Until we have better plugin management, will init after presence updates.
        gatewayPlugin = new GatewayPlugin();
        gatewayPlugin.initialize();

        // Load all non-presence related items.
        conferences.loadConferenceBookmarks();
        SearchManager.getInstance();
        transcriptPlugin = new ChatTranscriptPlugin();

        // Load Broadcast Plugin
        broadcastPlugin = new BroadcastPlugin();
        broadcastPlugin.initialize();

        // Load BookmarkPlugin
        bookmarkPlugin = new BookmarkPlugin();
        bookmarkPlugin.initialize();

        // Schedule loading of the plugins after two seconds.
        TaskEngine.getInstance().schedule(new TimerTask() {
            public void run() {
                final PluginManager pluginManager = PluginManager.getInstance();

                SparkManager.getMainWindow().addMainWindowListener(pluginManager);
                pluginManager.initializePlugins();

                // Subscriptions are always manual
                Roster roster = Roster.getInstanceFor( SparkManager.getConnection() );
                roster.setSubscriptionMode(Roster.SubscriptionMode.manual);
            }
        }, 2000);

        // Check URI Mappings
        SparkManager.getChatManager().handleURIMapping(Spark.ARGUMENTS);
    }


    /**
     * Returns the status box for the User.
     *
     * @return the status box for the user.
     */
    public StatusBar getStatusBar() {
        return statusBox;
    }

    /**
     * This is to handle agent to agent conversations.
     *
     * @param stanza the smack packet to process.
     */
    public void processPacket(final Stanza stanza) {
        SwingUtilities.invokeLater( () -> {
            try
            {
                handleIncomingPacket(stanza);
            }
            catch ( SmackException.NotConnectedException e )
            {
                // This would be odd: not being connected while receiving a stanza...
                Log.warning( "Unable to handle incoming stanza: " + stanza , e );
            }
        } );
    }


    private void handleIncomingPacket(Stanza stanza) throws SmackException.NotConnectedException
    {
        // We only handle message packets here.
        if (stanza instanceof Message) {
            final Message message = (Message)stanza;
            boolean isGroupChat = message.getType() == Message.Type.groupchat;

            // Check if Conference invite. If so, do not handle here.
            if (message.getExtension("x", "jabber:x:conference") != null) {
                return;
            }

            final String body = message.getBody();
            final JivePropertiesExtension extension = ((JivePropertiesExtension) message.getExtension( JivePropertiesExtension.NAMESPACE ));
            final boolean broadcast = extension != null && extension.getProperty( "broadcast" ) != null;

            // Handle offline message.
            DelayInformation offlineInformation = message.getExtension("delay", "urn:xmpp:delay");
            if (offlineInformation != null && (Message.Type.chat == message.getType() ||
                Message.Type.normal == message.getType())) {
                handleOfflineMessage(message);
            }

            if (body == null ||
                isGroupChat ||
                broadcast ||
                message.getType() == Message.Type.normal ||
                message.getType() == Message.Type.headline ||
                message.getType() == Message.Type.error) {
                return;
            }

            // Create new chat room for Agent Invite.
            final String from = stanza.getFrom();
            final String host = SparkManager.getSessionManager().getServerAddress();

            // Don't allow workgroup notifications to come through here.
            final String bareJID = XmppStringUtils.parseBareJid(from);
            if (host.equalsIgnoreCase(from) || from == null) {
                return;
            }


            ChatRoom room = null;
            try {
                room = SparkManager.getChatManager().getChatContainer().getChatRoom(bareJID);
            }
            catch (ChatRoomNotFoundException e) {
                // Ignore
            }

            // Check for non-existent rooms.
            if (room == null) {
                createOneToOneRoom(bareJID, message);
            }
        }
    }

    /**
     * Creates a new room if necessary and inserts an offline message.
     *
     * @param message The Offline message.
     */
    private void handleOfflineMessage(Message message) throws SmackException.NotConnectedException
    {
        if(!ModelUtil.hasLength(message.getBody())){
            return;
        }

        String bareJID = XmppStringUtils.parseBareJid(message.getFrom());
        ContactItem contact = contactList.getContactItemByJID(bareJID);
        String nickname = XmppStringUtils.parseLocalpart(bareJID);
        if (contact != null) {
            nickname = contact.getDisplayName();
        }

        // Create the room if it does not exist.
        ChatRoom room = SparkManager.getChatManager().createChatRoom(bareJID, nickname, nickname);
        if(!SparkManager.getChatManager().getChatContainer().getChatFrame().isVisible())
        {
            SparkManager.getChatManager().getChatContainer().getChatFrame().setVisible(true);
        }

        // Insert offline message
        room.getTranscriptWindow().insertMessage(nickname, message, ChatManager.FROM_COLOR, Color.white);
        room.addToTranscript(message, true);

        // Send display and notified message back.
        SparkManager.getMessageEventManager().sendDeliveredNotification(message.getFrom(), message.getStanzaId());
        SparkManager.getMessageEventManager().sendDisplayedNotification(message.getFrom(), message.getStanzaId());
    }

    /**
     * Creates a new room based on an anonymous user.
     *
     * @param bareJID the bareJID of the anonymous user.
     * @param message the message from the anonymous user.
     */
    private void createOneToOneRoom(String bareJID, Message message) {
        ContactItem contact = contactList.getContactItemByJID(bareJID);
        String nickname = XmppStringUtils.parseLocalpart(bareJID);
        if (contact != null) {
            nickname = contact.getDisplayName();
        }
        else {
            // Attempt to load VCard from users who we are not subscribed to.
            VCard vCard = SparkManager.getVCardManager().getVCard(bareJID);
            if (vCard != null && vCard.getError() == null) {
                String firstName = vCard.getFirstName();
                String lastName = vCard.getLastName();
                String userNickname = vCard.getNickName();
                if (ModelUtil.hasLength(userNickname)) {
                    nickname = userNickname;
                }
                else if (ModelUtil.hasLength(firstName) && ModelUtil.hasLength(lastName)) {
                    nickname = firstName + " " + lastName;
                }
                else if (ModelUtil.hasLength(firstName)) {
                    nickname = firstName;
                }
            }
        }

        SparkManager.getChatManager().createChatRoom(bareJID, nickname, nickname);
        try {
            insertMessage(bareJID, message);
        }
        catch (ChatRoomNotFoundException e) {
            Log.error("Could not find chat room.", e);
        }
    }


    private void insertMessage(final String bareJID, final Message message) throws ChatRoomNotFoundException {
        ChatRoom chatRoom = SparkManager.getChatManager().getChatContainer().getChatRoom(bareJID);
        chatRoom.insertMessage(message);
        int chatLength = chatRoom.getTranscriptWindow().getDocument().getLength();
        chatRoom.getTranscriptWindow().setCaretPosition(chatLength);
        chatRoom.getChatInputEditor().requestFocusInWindow();
    }


    /**
     * Returns the Workspace TabbedPane. If you wish to add your
     * component, simply use addTab( name, icon, component ) call.
     *
     * @return the workspace JideTabbedPane
     */
    public SparkTabbedPane getWorkspacePane() {
        return workspacePane;
    }


    /**
     * Returns the <code>ContactList</code> associated with this workspace.
     *
     * @return the ContactList associated with this workspace.
     */
    public ContactList getContactList() {
        return contactList;
    }

    public void changeCardLayout(String layout) {
        cardLayout.show(cardPanel, layout);
    }

    public JPanel getCardPanel() {
        return cardPanel;
    }

    /**
     * Returns the <code>CommandPanel</code> of this Workspace.
     *
     * @return the CommandPanel.
     */
    public CommandPanel getCommandPanel() {
        return statusBox.getCommandPanel();
    }

    public ChatTranscriptPlugin getTranscriptPlugin() {
        return transcriptPlugin;
    }
}
