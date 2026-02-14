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
package org.jivesoftware.fastpath.workspace;

import org.jivesoftware.MainWindow;
import org.jivesoftware.Spark;
import org.jivesoftware.fastpath.FastpathPlugin;
import org.jivesoftware.fastpath.FpRes;
import org.jivesoftware.fastpath.resources.FastpathRes;
import org.jivesoftware.fastpath.workspace.assistants.*;
import org.jivesoftware.fastpath.workspace.invite.InvitationManager;
import org.jivesoftware.fastpath.workspace.invite.WorkgroupInvitationDialog;
import org.jivesoftware.fastpath.workspace.macros.MacrosEditor;
import org.jivesoftware.fastpath.workspace.panes.*;
import org.jivesoftware.fastpath.workspace.search.ChatSearch;
import org.jivesoftware.fastpath.workspace.util.RequestUtils;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.MessageBuilder;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.StanzaBuilder;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.workgroup.MetaData;
import org.jivesoftware.smackx.workgroup.agent.*;
import org.jivesoftware.smackx.workgroup.user.Workgroup;
import org.jivesoftware.smackx.xdata.form.Form;
import org.jivesoftware.spark.DataManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.search.SearchManager;
import org.jivesoftware.spark.ui.*;
import org.jivesoftware.spark.ui.conferences.ConferenceUtils;
import org.jivesoftware.spark.ui.conferences.GroupChatParticipantList;
import org.jivesoftware.spark.ui.conferences.RoomInvitationListener;
import org.jivesoftware.spark.ui.rooms.GroupChatRoom;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.alerts.SparkToaster;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Localpart;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;

import static org.jivesoftware.spark.Event.CHAT_REQUEST;

public class Workpane {
    // Tracks all the offers coming into the client.
    private final Map<String, Offer> offerMap = new HashMap<>();
    private final Map<String, Map<String, List<String>>> inviteMap = new HashMap<>();
    private final Map<String, UserInvitationPane> invitations = new HashMap<>();


    private final Map<ChatRoom, RoomState> fastpathRooms = new HashMap<>();

    private final OnlineAgents onlineAgentsPane;
    private AgentConversations agentCons;
    private final ChatOfferListener offerListener;
    private final RoomInvitationListener roomInviteListener;
    private final ChatSearch chatSearch;

    private final QueueActivity queueActivity;

    private final RolloverButton historyButton;
    private final RolloverButton workgroupGroupButton;
    private final RolloverButton macrosButton;


    public static final String INITIAL_RESPONSE_PROPERTY = "initialResponse";

    private final JPanel toolbar;

    private final List<FastpathListener> listeners = new ArrayList<>();

    private final PresenceChangeListener presenceListener = new PresenceChangeListener();

    /**
     * Type of states a fastpath room can be in.
     */
    public enum RoomState {
        /**
         * The room contains an incoming request.
         */
        incomingRequest,
        /**
         * The rooms contains an invitation from another agent.
         */
        invitationRequest,
        /**
         * The room is in an active state.
         */
        activeRoom
    }

    public Workpane() {
        onlineAgentsPane = new OnlineAgents();
        agentCons = new AgentConversations();
        offerListener = new ChatOfferListener();
        roomInviteListener = new InviteListener();
        chatSearch = new ChatSearch();

        addOnlineAgents();
        handleRoomOpenings();

        FastpathPlugin.getUI().getMainPanel().addTab(FpRes.getString("tab.current.chats"), null, agentCons);


        SearchManager.getInstance().addSearchService(chatSearch);

        // Add Queue Activity Menu
        queueActivity = new QueueActivity();
        setupQueueViewer();

        // Add presence listener. This is used to send presence changes to the workgroup itself.
        SparkManager.getSessionManager().addPresenceListener(presenceListener);

        toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolbar.setOpaque(false);
        historyButton = new RolloverButton();
        ResourceUtils.resButton(historyButton, FpRes.getString("button.history"));
        toolbar.add(historyButton);
        historyButton.setIcon(FastpathRes.getImageIcon(FastpathRes.HISTORY_16x16));

        historyButton.addActionListener(e -> {
            final ChatHistory chatHistory = new ChatHistory();
            chatHistory.showDialog();
        });

        workgroupGroupButton = new RolloverButton();
        ResourceUtils.resButton(workgroupGroupButton, FpRes.getString("button.conference"));
        workgroupGroupButton.setIcon(FastpathRes.getImageIcon(FastpathRes.CONFERENCE_IMAGE_16x16));
        toolbar.add(workgroupGroupButton);

        workgroupGroupButton.addActionListener(e -> {
            final Workgroup workgroup = FastpathPlugin.getWorkgroup();
            String serviceName = "conference." + SparkManager.getSessionManager().getServerAddress();
            final EntityBareJid roomName = JidCreate.entityBareFromOrThrowUnchecked("workgroup-" + workgroup.getWorkgroupJID().getLocalpartOrThrow() + "@" + serviceName);
            ConferenceUtils.joinConferenceOnSeparateThread("Workgroup Chat", roomName, null, null);
        });

        macrosButton = new RolloverButton(FastpathRes.getImageIcon(FastpathRes.NOTEBOOK_IMAGE));
        ResourceUtils.resButton(macrosButton, FpRes.getString("button.macros"));

        macrosButton.addActionListener(e -> {
            MacrosEditor editor = new MacrosEditor();
            editor.showEditor(macrosButton);
        });
        toolbar.add(macrosButton);

        FastpathPlugin.getMainPanel().add(toolbar, new GridBagConstraints(0, 2, 3, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 2, 2, 2), 0, 0));

    }


    public void unload() {
        FastpathPlugin.getUI().getMainPanel().removeComponent(onlineAgentsPane);

        // Remove offer listener
        FastpathPlugin.getAgentSession().removeOfferListener(offerListener);

        // Add own invitation listener
        SparkManager.getChatManager().removeInvitationListener(roomInviteListener);

        // Remove Chat Search
        SearchManager.getInstance().removeSearchService(chatSearch);

        // Remove Queue Listener
        queueActivity.removeListener();
        FastpathPlugin.getUI().getMainPanel().removeComponent(queueActivity);

        FastpathPlugin.getUI().getMainPanel().removeComponent(agentCons);
        agentCons = null;

        FastpathPlugin.getMainPanel().remove(toolbar);

        SparkManager.getSessionManager().removePresenceListener(presenceListener);
    }


    private void addOnlineAgents() {
        FastpathPlugin.getUI().getMainPanel().addTab(FpRes.getString("tab.online.agents"), null, onlineAgentsPane);
    }

    public void listenForOffers() {
        FastpathPlugin.getAgentSession().addOfferListener(offerListener);

        // Add own invitation listener
        SparkManager.getChatManager().addInvitationListener(roomInviteListener);
    }

    public Map<String, List<String>> getMetadata(String sessionID) {
        Map<String, List<String>> map = null;
        if (offerMap.get(sessionID) != null) {
            Offer offer = offerMap.get(sessionID);
            map = offer.getMetaData();
        }
        else if (inviteMap.get(sessionID) != null) {
            map = inviteMap.get(sessionID);
        }
        return map;
    }

    private void handleRoomOpenings() {
        SparkManager.getChatManager().addChatRoomListener(new ChatRoomListener() {
            public void chatRoomOpened(ChatRoom room) {
                if (!(room instanceof GroupChatRoom)) {
                    return;
                }
                EntityBareJid roomName = room.getBareJid();
                Localpart sessionID = roomName.getLocalpart();
                if (offerMap.get(sessionID.toString()) != null) {
                    Offer offer = offerMap.get(sessionID.toString());
                    Map<String, List<String>> metadata = offer.getMetaData();
                    decorateRoom(room, metadata);
                }
            }

            public void chatRoomClosed(ChatRoom room) {
                EntityBareJid roomName = room.getBareJid();
                Localpart sessionID = roomName.getLocalpart();
                offerMap.remove(sessionID.toString());
            }
        });
    }

    public void checkForDecoration(ChatRoom chatRoom, String sessionID) {
        if (inviteMap.get(sessionID) != null) {
            Map<String, List<String>> metadata = inviteMap.get(sessionID);
            decorateRoom(chatRoom, metadata);
        }
    }

    public void decorateRoom(ChatRoom room, Map<String, List<String>> metadata) {
        EntityBareJid roomName = room.getBareJid();
        Localpart sessionID =roomName.getLocalpart();

        RequestUtils utils = new RequestUtils(metadata);

        addRoomInfo(sessionID, utils, room);

        addButtons(sessionID.toString(), utils, room);

        // Specify to use Typing notifications.
        GroupChatRoom groupChat = (GroupChatRoom)room;
        groupChat.setChatStatEnabled(true);

        Properties props = FastpathPlugin.getLitWorkspace().getWorkgroupProperties();
        String initialResponse = props.getProperty(INITIAL_RESPONSE_PROPERTY);
        if (ModelUtil.hasLength(initialResponse)) {
            MessageBuilder messageBuilder = StanzaBuilder.buildMessage()
                .setBody(initialResponse);
            GroupChatRoom groupChatRoom = (GroupChatRoom)room;
            groupChatRoom.sendMessage(messageBuilder);
        }
    }

    private void addButtons(final String sessionID, final RequestUtils utils, final ChatRoom room) {
        final ChatRoomButton inviteButton = new ChatRoomButton(FastpathRes.getImageIcon(FastpathRes.CHAT_INVITE_IMAGE_24x24));
        final ChatRoomButton transferButton = new ChatRoomButton(FastpathRes.getImageIcon(FastpathRes.CHAT_TRANSFER_IMAGE_24x24));
        final RolloverButton cannedResponses = new RolloverButton(FastpathRes.getImageIcon(FastpathRes.DOWN_ARROW_IMAGE));
        final ChatRoomButton endButton = new ChatRoomButton(FastpathRes.getImageIcon(FastpathRes.CHAT_ENDED_IMAGE_24x24));
        final ChatRoomButton cobrowseButton = new ChatRoomButton(FastpathRes.getImageIcon(FastpathRes.CHAT_COBROWSE_IMAGE_24x24));

        // Set tooltips
        inviteButton.setToolTipText("Invite another user to join in this conversation.");
        transferButton.setToolTipText("Transfer this conversation to another agent.");
        endButton.setToolTipText("End this conversation.");
        cobrowseButton.setToolTipText("Start a co-browsing session with this user.");

        // Update Canned Response button.
        ResourceUtils.resButton(cannedResponses, FpRes.getString("button.canned.responses"));

        room.getToolBar().addChatRoomButton(inviteButton);
        room.getToolBar().addChatRoomButton(transferButton);
        room.getToolBar().addChatRoomButton(cobrowseButton);
        room.getToolBar().addChatRoomButton(endButton);

        inviteButton.addActionListener(e -> inviteOrTransfer(room, utils.getWorkgroup(), sessionID, false));

        cobrowseButton.addActionListener(e -> {
            CoBrowser browser = new CoBrowser(sessionID, room);
            browser.showDialog();
        });

        transferButton.addActionListener(actionEvent -> inviteOrTransfer(room, utils.getWorkgroup(), sessionID, true));

        room.getEditorBar().add(cannedResponses);
        cannedResponses.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                final ChatMacroMenu chatMacroMenu = new ChatMacroMenu(room);
                chatMacroMenu.show(cannedResponses, e.getX(), e.getY());
            }
        });

        endButton.addActionListener(actionEvent -> {
            final GroupChatRoom groupChatRoom = (GroupChatRoom)room;
            groupChatRoom.leaveChatRoom();
        });

        // Set Room is Active
        addFastpathChatRoom(room, RoomState.activeRoom);
    }


    /**
     * Invite or transfer this conversation to another agent, queue or workgroup.
     *
     * @param room      the <code>ChatRoom</code>.
     * @param sessionID the current sessionid of this conversation.
     * @param transfer  true if you wish to transfer this room.
     */
    private void inviteOrTransfer(ChatRoom room, String workgroup, String sessionID, boolean transfer) {
        WorkgroupInvitationDialog dialog = new WorkgroupInvitationDialog();

        boolean ok = dialog.hasSelectedAgent(room, transfer);
        if (ok) {
            String jidString = dialog.getSelectedJID();
            Jid jid = JidCreate.fromUnescapedOrThrowUnchecked(jidString);

            String message = dialog.getMessage();

            // Determine who to send to.
            if (jid.hasResource()) {
                // Queueu
                InvitationManager.transferOrInviteToQueue(room, workgroup, sessionID, jid, message, transfer);
            }
            else if (jid.getDomain().toString().startsWith("workgroup")) {
                InvitationManager.transferOrInviteToWorkgroup(room, workgroup, sessionID, jid, message, transfer);
            }
            else {
                InvitationManager.transferOrInviteUser(room, workgroup, sessionID, jid, message, transfer);
            }
        }
    }


    private void addRoomInfo(final CharSequence sessionID, final RequestUtils utils, final ChatRoom room) {
        final JTabbedPane tabbedPane = new JTabbedPane();

        GroupChatParticipantList participantList = ((GroupChatRoom)room).getConferenceRoomInfo();

        room.getSplitPane().setRightComponent(tabbedPane);

        Form form;
        try {
            form = FastpathPlugin.getWorkgroup().getWorkgroupForm();
        }
        catch (XMPPException | SmackException | InterruptedException e) {
            Log.error(e);
            return;
        }

        final BackgroundPane transcriptAlert = new BackgroundPane() {
            public Dimension getPreferredSize() {
                final Dimension size = super.getPreferredSize();
                size.width = 0;
                return size;
            }
        };
        transcriptAlert.setLayout(new GridBagLayout());

        JLabel userImage = new JLabel(FastpathRes.getImageIcon(FastpathRes.FASTPATH_IMAGE_24x24));
        userImage.setHorizontalAlignment(JLabel.LEFT);
        userImage.setText(utils.getUsername());
        transcriptAlert.add(userImage, new GridBagConstraints(0, 0, 4, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        userImage.setFont(new Font("Dialog", Font.BOLD, 12));


        final RoomInformation roomInformation = new RoomInformation();
        roomInformation.showFormInformation(form, utils);
        final UserHistory userHistory = new UserHistory(utils.getUserID());

        final JScrollPane userScroll = new JScrollPane(roomInformation);
        final JScrollPane historyScroll = new JScrollPane(userHistory);
        historyScroll.getVerticalScrollBar().setBlockIncrement(50);
        historyScroll.getVerticalScrollBar().setUnitIncrement(20);

        tabbedPane.addTab(FpRes.getString("tab.user.info"), userScroll);
        tabbedPane.addTab(FpRes.getString("tab.participants"), participantList);
        tabbedPane.addTab(FpRes.getString("tab.user.history"), historyScroll);

        final Notes notes = new Notes(sessionID, room);

        tabbedPane.addTab(FpRes.getString("tab.notes"), notes);


        tabbedPane.addChangeListener(new ChangeListener() {
            boolean loaded;

            public void stateChanged(ChangeEvent e) {
                if (!loaded) {
                    if (tabbedPane.getSelectedComponent() == historyScroll) {
                        userHistory.loadHistory();
                        loaded = true;
                    }
                }
            }
        });


        final GroupChatRoom groupRoom = (GroupChatRoom)room;
        room.getTranscriptWindow().clear();

        final ChatFrame frame = SparkManager.getChatManager().getChatContainer().getChatFrame();
        if (frame != null) {
            int height = frame.getHeight();
            int width = frame.getWidth();
            if (height < 400) {
                height = 400;
            }

            if (width < 600) {
                width = 600;
            }

            frame.setSize(width, height);
            frame.validate();
            frame.repaint();
        }

        fireFastPathChatOpened(room, sessionID.toString(), utils, tabbedPane);
        DataManager.getInstance().setMetadataForRoom(room, utils.getMetadata());
    }

    public void blink() {
        final MainWindow mainWindow = SparkManager.getMainWindow();

        if (!mainWindow.isFocused()) {
            // Set to new tab.
            if (Spark.isWindows()) {
                mainWindow.addWindowListener(new WindowAdapter() {
                    public void windowActivated(WindowEvent e) {
                        SparkManager.getNativeManager().stopFlashing(mainWindow);
                    }
                });


                if (!mainWindow.isFocused() && mainWindow.isVisible()) {
                    SparkManager.getNativeManager().flashWindow(mainWindow);
                }
                else if (!mainWindow.isVisible()) {
                    mainWindow.setState(Frame.ICONIFIED);
                    mainWindow.setVisible(true);
                    SparkManager.getNativeManager().flashWindow(mainWindow);
                }
            }
        }
    }

    private class ChatOfferListener implements OfferListener {
        ChatQueue chatQueue;
        private SparkToaster toasterManager;

        public void offerReceived(final Offer offer) {
            SwingUtilities.invokeLater(() -> handleOffer(offer));
        }

        private void handleOffer(final Offer offer) {
            if (offer.getContent() instanceof InvitationRequest || offer.getContent() instanceof TransferRequest) {
                handleOfferInvite(offer);
                return;
            }


            chatQueue = new ChatQueue();
            chatQueue.offerRecieved(offer);

            toasterManager = new SparkToaster();
            toasterManager.setHidable(false);


            toasterManager.setToasterHeight((int)chatQueue.getPreferredSize().getHeight() + 40);

            int width = (int)chatQueue.getPreferredSize().getWidth() + 40;
            if (width < 300) {
                width = 300;
            }
            toasterManager.setToasterWidth(width);
            toasterManager.setDisplayTime(500000000);


            final JScrollPane pane = new JScrollPane(chatQueue, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            pane.setBorder(BorderFactory.createEmptyBorder());
            toasterManager.showToaster("Incoming Fastpath Request", pane);
            toasterManager.hideTitle();

            chatQueue.getAcceptButton().addActionListener(e -> {
                toasterManager.close();
                chatQueue.setVisible(false);

                offerMap.put(offer.getSessionID(), offer);
                try
                {
                    offer.accept();
                }
                catch ( SmackException.NotConnectedException | InterruptedException e1 )
                {
                    Log.warning( "Unable to accept offer from " + offer.getUserJID(), e1 );
                }
            });

            chatQueue.getDeclineButton().addActionListener(e -> {
                toasterManager.close();
                chatQueue.setVisible(false);
                try
                {
                    offer.reject();
                }
                catch ( SmackException.NotConnectedException | InterruptedException e1 )
                {
                    Log.warning( "Unable to reject offer from " + offer.getUserJID(), e1 );
                }
                SparkManager.getWorkspace().remove(chatQueue);
                offerMap.remove(offer.getSessionID());
            });

            SparkManager.getSoundManager().playClip(CHAT_REQUEST);
        }

        public void offerRevoked(final RevokedOffer revokedOffer) {
            SwingUtilities.invokeLater(() -> {
                SparkManager.getNativeManager().stopFlashing(SparkManager.getMainWindow());
                if (toasterManager != null) {
                    toasterManager.close();
                }

                // If the ChatQueue is visible, dispose of it.
                if (chatQueue != null) {
                    chatQueue.setVisible(false);
                    return;
                }

                UserInvitationPane pane = invitations.get(revokedOffer.getSessionID());
                if (pane != null) {
                    pane.dispose();
                    invitations.remove(revokedOffer.getSessionID());
                }
            });
        }
    }


    private class InviteListener implements RoomInvitationListener {
        // Add own invitation listener
    	@Override
        public boolean handleInvitation(final XMPPConnection conn, final MultiUserChat chat, final EntityBareJid inviter, final String reason, final String password, final Message message) {
            if (offerMap.containsKey(reason)) {
                RequestUtils utils = new RequestUtils(getMetadata(reason));
                String roomName = utils.getUsername() != null ? utils.getUsername() : chat.getRoom().getLocalpart().asUnescapedString();

                // Create the Group Chat Room
                GroupChatRoom groupChatRoom = ConferenceUtils.enterRoomOnSameThread(roomName, chat.getRoom(), password);
                groupChatRoom.getSplitPane().setDividerSize(5);
                groupChatRoom.getVerticalSlipPane().setDividerLocation(0.6);
                groupChatRoom.getSplitPane().setDividerLocation(0.6);

                groupChatRoom.getConferenceRoomInfo().setNicknameChangeAllowed(false);

                groupChatRoom.getToolBar().setVisible(true);
                fastpathRooms.put(groupChatRoom, RoomState.activeRoom);

                final ChatContainer chatContainer = SparkManager.getChatManager().getChatContainer();
                chatContainer.setChatRoomTitle(groupChatRoom, roomName);
                try {
                    if (chatContainer.getActiveChatRoom() == groupChatRoom) {
                        chatContainer.getChatFrame().setTitle(roomName);
                    }
                }
                catch (ChatRoomNotFoundException e) {
                    Log.debug(e.getMessage());
                }

                SparkManager.getChatManager().notifySparkTabHandlers(groupChatRoom);

                // Change subject line.
                groupChatRoom.setRoomLabel("<html><body><b>Fastpath Conversation with " + roomName + "</b></body></html>");

                return true;
            }
            else if (message != null) {
                MetaData metaDataExt = message.getExtension(MetaData.class);
                if (metaDataExt != null) {
                    Map<String, List<String>> metadata = metaDataExt.getMetaData();
                    List<String> values = new ArrayList<>();
                    values.add(chat.getRoom().getLocalpart().toString());
                    metadata.put("sessionID", values);

                    RequestUtils utils = new RequestUtils(metadata);
                    inviteMap.put(utils.getSessionID(), metadata);
                    InvitationPane pane = new InvitationPane(utils, chat.getRoom(), inviter, reason, password, message);
                    return true;
                }
            }

            return false;
        }
    }

    /**
     * Handles incoming invitations or transfers.
     *
     * @param offer the <code>Offer</code>
     */
    public void handleOfferInvite(final Offer offer) {
        Map<String, List<String>> metadata = offer.getMetaData();
        String sessionID = offer.getSessionID();
        List<String> values = new ArrayList<>();
        values.add(sessionID);
        metadata.put("sessionID", values);

        RequestUtils utils = new RequestUtils(metadata);
        inviteMap.put(sessionID, metadata);

        UserInvitationPane invitationPane = null;

        if (offer.getContent() instanceof InvitationRequest) {
            InvitationRequest request = (InvitationRequest)offer.getContent();
            EntityBareJid room = JidCreate.entityBareFromOrThrowUnchecked(request.getRoom());
            EntityBareJid inviter = JidCreate.entityBareFromOrThrowUnchecked(request.getInviter());
            invitationPane = new UserInvitationPane(offer, utils, room, inviter, request.getReason());
        }
        else if (offer.getContent() instanceof TransferRequest) {
            TransferRequest request = (TransferRequest)offer.getContent();
            EntityBareJid room = JidCreate.entityBareFromOrThrowUnchecked(request.getRoom());
            EntityBareJid inviter = JidCreate.entityBareFromOrThrowUnchecked(request.getInviter());
            invitationPane = new UserInvitationPane(offer, utils, room, inviter, request.getReason());
        }

        invitationPane.setAcceptListener(new UserInvitationPane.AcceptListener() {
            public void yesOption() {
                // Remove
                invitations.remove(offer.getSessionID());
            }

            public void noOption() {
                invitations.remove(offer.getSessionID());
            }
        });

        invitations.put(offer.getSessionID(), invitationPane);
    }


    public Properties getWorkgroupProperties() {
        Localpart workgroupName = FastpathPlugin.getWorkgroup().getWorkgroupJID().getLocalpartOrThrow();

        File workgroupDir = new File(Spark.getSparkUserHome(), "workgroups/" + workgroupName);
        workgroupDir.mkdirs();

        File propertiesFile = new File(workgroupDir, "workgroup.properties");
        Properties props = new Properties();
        try {
            props.load(new FileInputStream(propertiesFile));
        }
        catch (IOException e) {
            // File does not exist.
        }
        return props;
    }

    public void saveProperties(Properties props) {
        Localpart workgroupName = FastpathPlugin.getWorkgroup().getWorkgroupJID().getLocalpartOrThrow();

        File propertiesFile = new File(new File(Spark.getSparkUserHome(), "workgroups/" + workgroupName), "workgroup.properties");

        try {
            props.store(new FileOutputStream(propertiesFile), "Workgroup Properties");
        }
        catch (IOException e) {
            Log.error("Unable to save group properties.", e);
        }
    }

    private void setupQueueViewer() {
        FastpathPlugin.getUI().getMainPanel().addTab(FpRes.getString("tab.queue.activity"), null, queueActivity);
    }

    public void addFastPathListener(FastpathListener listener) {
        listeners.add(listener);
    }

    public void removeFastPathListener(FastpathListener listener) {
        listeners.remove(listener);
    }


    private void fireFastPathChatOpened( ChatRoom room, String sessionID, RequestUtils utils, JTabbedPane tabbedPane )
    {
        for ( final FastpathListener listener : listeners )
        {
            try
            {
                listener.fastpathRoomOpened( room, sessionID, utils, tabbedPane );
            }
            catch ( Exception e )
            {
                Log.error( "A FastpathListener (" + listener + ") threw an exception while processing a 'fastpathRoomOpened' event for: " + room + " in session: " + sessionID, e );
            }
        }
    }


    private static class PresenceChangeListener implements PresenceListener {
        public void presenceChanged(Presence presence) {
            String status = presence.getStatus();
            if (status == null) {
                status = "";
            }

            try {
                if (FastpathPlugin.getAgentSession().isOnline()) {
                    FastpathPlugin.getAgentSession().setStatus(presence.getMode(), status);
                }
            }
            catch (XMPPException | SmackException | InterruptedException e) {
                Log.error(e);
            }
        }
    }

    public void addFastpathChatRoom(ChatRoom chatRoom, RoomState state) {
        fastpathRooms.put(chatRoom, state);
    }

    public void removeFastpathChatRoom(ChatRoom chatRoom) {
        fastpathRooms.remove(chatRoom);
    }

    public RoomState getRoomState(ChatRoom chatRoom) {
        return fastpathRooms.get(chatRoom);
    }


}
