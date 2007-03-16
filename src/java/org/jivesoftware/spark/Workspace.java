/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.spark;

import org.jivesoftware.MainWindow;
import org.jivesoftware.MainWindowListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.debugger.EnhancedDebuggerWindow;
import org.jivesoftware.smackx.packet.DelayInformation;
import org.jivesoftware.spark.component.tabbedPane.SparkTabbedPane;
import org.jivesoftware.spark.filetransfer.SparkTransferManager;
import org.jivesoftware.spark.phone.PhoneManager;
import org.jivesoftware.spark.search.SearchManager;
import org.jivesoftware.spark.ui.ChatContainer;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatRoomNotFoundException;
import org.jivesoftware.spark.ui.CommandPanel;
import org.jivesoftware.spark.ui.ContactItem;
import org.jivesoftware.spark.ui.ContactList;
import org.jivesoftware.spark.ui.conferences.ConferencePlugin;
import org.jivesoftware.spark.ui.status.StatusBar;
import org.jivesoftware.spark.util.TaskEngine;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.manager.Enterprise;
import org.jivesoftware.sparkimpl.plugin.transcripts.ChatTranscriptPlugin;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;


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
public class Workspace extends JPanel implements PacketListener {
    private SparkTabbedPane workspacePane;
    private StatusBar statusBox;
    private CommandPanel commandPanel;
    private ContactList contactList;
    private ConferencePlugin conferences;

    private static Workspace singleton;
    private static final Object LOCK = new Object();
    private List<Message> offlineMessages = new ArrayList<Message>();

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
            }

            public void mainWindowActivated() {

            }

            public void mainWindowDeactivated() {

            }
        });

        // Initialize workspace pane, defaulting the tabs to the bottom.
        workspacePane = new SparkTabbedPane(JTabbedPane.BOTTOM);
        workspacePane.setActiveButtonBold(true);

        // Add Panels.
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setOpaque(false);
        cardPanel.add(WORKSPACE_PANE, this);

        statusBox = new StatusBar();
        commandPanel = new CommandPanel();

        // Build default workspace
        this.setLayout(new GridBagLayout());
        add(workspacePane, new GridBagConstraints(0, 9, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 4, 4, 4), 0, 0));
        add(statusBox, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 4, 4, 4), 0, 0));
        add(commandPanel, new GridBagConstraints(0, 5, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 4, 0, 4), 0, 0));


        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F12"), "showDebugger");
        this.getActionMap().put("showDebugger", new AbstractAction("showDebugger") {
            public void actionPerformed(ActionEvent evt) {
                EnhancedDebuggerWindow window = EnhancedDebuggerWindow.getInstance();
                window.setVisible(true);
            }
        });

        // Set background
        setBackground(Color.white);
    }

    /**
     * Builds the Workspace layout.
     */
    public void buildLayout() {
        new Enterprise();

        // Initilaize tray
        SparkManager.getNotificationsEngine();

        // Initialize Contact List
        contactList = new ContactList();
        contactList.initialize();

        // Load VCard information for status box
        statusBox.loadVCard();

        conferences = new ConferencePlugin();
        conferences.initialize();

        // Initialize Search Service
        SearchManager.getInstance();

        // Initialise TransferManager
        SparkTransferManager.getInstance();

        ChatTranscriptPlugin transcriptPlugin = new ChatTranscriptPlugin();
        transcriptPlugin.initialize();

        PhoneManager.getInstance();
    }

    /**
     * Starts the Loading of all Spark Plugins.
     */
    public void loadPlugins() {
        // Add presence and message listeners
        // we listen for these to force open a 1-1 peer chat window from other operators if
        // one isn't already open
        PacketFilter workspaceMessageFilter = new PacketTypeFilter(Message.class);

        // Add the packetListener to this instance
        SparkManager.getSessionManager().getConnection().addPacketListener(this, workspaceMessageFilter);

        // Make presence available to anonymous requests, if from anonymous user in the system.
        PacketListener workspacePresenceListener = new PacketListener() {
            public void processPacket(Packet packet) {
                Presence presence = (Presence)packet;
                if (presence.getProperty("anonymous") != null) {
                    boolean isAvailable = statusBox.getPresence().getMode() == Presence.Mode.available;
                    Presence reply = new Presence(Presence.Type.available);
                    if (!isAvailable) {
                        reply.setType(Presence.Type.unavailable);
                    }
                    reply.setTo(presence.getFrom());
                    SparkManager.getSessionManager().getConnection().sendPacket(reply);
                }
            }
        };

        SparkManager.getSessionManager().getConnection().addPacketListener(workspacePresenceListener, new PacketTypeFilter(Presence.class));

        // Send Available status
        final Presence presence = SparkManager.getWorkspace().getStatusBar().getPresence();
        SparkManager.getSessionManager().changePresence(presence);

        // Schedule loading of the plugins after two seconds.
        TaskEngine.getInstance().schedule(new TimerTask() {
            public void run() {
                final PluginManager pluginManager = PluginManager.getInstance();
                pluginManager.loadPlugins();
                pluginManager.initializePlugins();
            }
        }, 2000);

        // Load the offline messages after 10 seconds.
        TaskEngine.getInstance().schedule(new TimerTask() {
            public void run() {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        for (Message offlineMessage : offlineMessages) {
                            handleOfflineMessage(offlineMessage);
                        }

                        offlineMessages.clear();
                    }
                });
            }
        }, 10000);

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
     * @param packet the smack packet to process.
     */
    public void processPacket(final Packet packet) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                handleIncomingPacket(packet);
            }
        });
    }


    private void handleIncomingPacket(Packet packet) {
        // We only handle message packets here.
        if (packet instanceof Message) {
            final Message message = (Message)packet;
            boolean isGroupChat = message.getType() == Message.Type.groupchat;

            // Check if Conference invite. If so, do not handle here.
            if (message.getExtension("x", "jabber:x:conference") != null) {
                return;
            }

            final String body = message.getBody();
            boolean broadcast = message.getProperty("broadcast") != null;

            if (body == null ||
                isGroupChat ||
                broadcast ||
                message.getType() == Message.Type.normal ||
                message.getType() == Message.Type.headline ||
                message.getType() == Message.Type.error) {
                return;
            }

            // Create new chat room for Agent Invite.
            final String from = packet.getFrom();
            final String host = SparkManager.getSessionManager().getServerAddress();

            // Don't allow workgroup notifications to come through here.
            final String bareJID = StringUtils.parseBareAddress(from);
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

            // Handle offline message.
            DelayInformation offlineInformation = (DelayInformation)message.getExtension("x", "jabber:x:delay");

            if (offlineInformation != null && (Message.Type.chat == message.getType() || Message.Type.normal == message.getType())) {
                offlineMessages.add(message);
            }

            // Check for anonymous user.
            else if (room == null) {
                createOneToOneRoom(bareJID, message);
            }
        }
    }

    /**
     * Creates a new room if necessary and inserts an offline message.
     *
     * @param message The Offline message.
     */
    private void handleOfflineMessage(Message message) {
        DelayInformation offlineInformation = (DelayInformation)message.getExtension("x", "jabber:x:delay");
        String bareJID = StringUtils.parseBareAddress(message.getFrom());
        ContactItem contact = contactList.getContactItemByJID(bareJID);
        String nickname = StringUtils.parseName(bareJID);
        if (contact != null) {
            nickname = contact.getNickname();
        }

        // Create the room if it does not exist.
        ChatRoom room = SparkManager.getChatManager().createChatRoom(bareJID, nickname, nickname);

        // Insert offline message
        room.getTranscriptWindow().insertMessage(nickname, message, ChatManager.FROM_COLOR);
        room.addToTranscript(message, true);

        // Send display and notified message back.
        SparkManager.getMessageEventManager().sendDeliveredNotification(message.getFrom(), message.getPacketID());
        SparkManager.getMessageEventManager().sendDisplayedNotification(message.getFrom(), message.getPacketID());
    }

    /**
     * Creates a new room based on an anonymous user.
     *
     * @param bareJID the bareJID of the anonymous user.
     * @param message the message from the anonymous user.
     */
    private void createOneToOneRoom(String bareJID, Message message) {
        ContactItem contact = contactList.getContactItemByJID(bareJID);
        String nickname = StringUtils.parseName(bareJID);
        if (contact != null) {
            nickname = contact.getNickname();
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
        return commandPanel;
    }
}
