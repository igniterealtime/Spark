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
package org.jivesoftware.fastpath.workspace;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jivesoftware.MainWindow;
import org.jivesoftware.Spark;
import org.jivesoftware.fastpath.FastpathPlugin;
import org.jivesoftware.fastpath.FpRes;
import org.jivesoftware.fastpath.resources.FastpathRes;
import org.jivesoftware.fastpath.workspace.assistants.ChatMacroMenu;
import org.jivesoftware.fastpath.workspace.assistants.CoBrowser;
import org.jivesoftware.fastpath.workspace.assistants.Notes;
import org.jivesoftware.fastpath.workspace.assistants.RoomInformation;
import org.jivesoftware.fastpath.workspace.assistants.UserHistory;
import org.jivesoftware.fastpath.workspace.invite.InvitationManager;
import org.jivesoftware.fastpath.workspace.invite.WorkgroupInvitationDialog;
import org.jivesoftware.fastpath.workspace.macros.MacrosEditor;
import org.jivesoftware.fastpath.workspace.panes.AgentConversations;
import org.jivesoftware.fastpath.workspace.panes.BackgroundPane;
import org.jivesoftware.fastpath.workspace.panes.ChatHistory;
import org.jivesoftware.fastpath.workspace.panes.ChatQueue;
import org.jivesoftware.fastpath.workspace.panes.InvitationPane;
import org.jivesoftware.fastpath.workspace.panes.OnlineAgents;
import org.jivesoftware.fastpath.workspace.panes.QueueActivity;
import org.jivesoftware.fastpath.workspace.panes.UserInvitationPane;
import org.jivesoftware.fastpath.workspace.search.ChatSearch;
import org.jivesoftware.fastpath.workspace.util.RequestUtils;
import org.jivesoftware.resource.SoundsRes;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.workgroup.MetaData;
import org.jivesoftware.smackx.workgroup.agent.InvitationRequest;
import org.jivesoftware.smackx.workgroup.agent.Offer;
import org.jivesoftware.smackx.workgroup.agent.OfferListener;
import org.jivesoftware.smackx.workgroup.agent.RevokedOffer;
import org.jivesoftware.smackx.workgroup.agent.TransferRequest;
import org.jivesoftware.smackx.workgroup.user.Workgroup;
import org.jivesoftware.spark.DataManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.UserManager;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.search.SearchManager;
import org.jivesoftware.spark.ui.ChatContainer;
import org.jivesoftware.spark.ui.ChatFrame;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatRoomButton;
import org.jivesoftware.spark.ui.ChatRoomListenerAdapter;
import org.jivesoftware.spark.ui.ChatRoomNotFoundException;
import org.jivesoftware.spark.ui.PresenceListener;
import org.jivesoftware.spark.ui.conferences.ConferenceUtils;
import org.jivesoftware.spark.ui.conferences.GroupChatParticipantList;
import org.jivesoftware.spark.ui.conferences.RoomInvitationListener;
import org.jivesoftware.spark.ui.rooms.GroupChatRoom;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.TaskEngine;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.alerts.SparkToaster;

public class Workpane {
    // Tracks all the offers coming into the client.
    private Map offerMap = new HashMap();
    private Map inviteMap = new HashMap();
    private Map<String, UserInvitationPane> invitations = new HashMap<String, UserInvitationPane>();


    private Map<ChatRoom, RoomState> fastpathRooms = new HashMap<ChatRoom, RoomState>();

    private OnlineAgents onlineAgentsPane;
    private AgentConversations agentCons;
    private ChatOfferListener offerListener;
    private RoomInvitationListener roomInviteListener;
    private ChatSearch chatSearch;

    private QueueActivity queueActivity;

    private RolloverButton historyButton;
    private RolloverButton workgroupGroupButton;
    private RolloverButton macrosButton;


    public static final String INITIAL_RESPONSE_PROPERTY = "initialResponse";

    private JPanel toolbar;

    private List listeners = new ArrayList();

    private PresenceChangeListener presenceListener = new PresenceChangeListener();

    /**
     * Type of states a fastpath room can be in.
     */
    public static enum RoomState {
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
        historyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final ChatHistory chatHistory = new ChatHistory();
                chatHistory.showDialog();
            }
        });

        workgroupGroupButton = new RolloverButton();
        ResourceUtils.resButton(workgroupGroupButton, FpRes.getString("button.conference"));
        workgroupGroupButton.setIcon(FastpathRes.getImageIcon(FastpathRes.CONFERENCE_IMAGE_16x16));
        toolbar.add(workgroupGroupButton);
        workgroupGroupButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final Workgroup workgroup = FastpathPlugin.getWorkgroup();
                String serviceName = "conference." + SparkManager.getSessionManager().getServerAddress();
                final String roomName = "workgroup-" + StringUtils.parseName(workgroup.getWorkgroupJID()) + "@" + serviceName;
                ConferenceUtils.joinConferenceOnSeperateThread("Workgroup Chat", roomName, null);
            }
        });


        macrosButton = new RolloverButton(FastpathRes.getImageIcon(FastpathRes.NOTEBOOK_IMAGE));
        ResourceUtils.resButton(macrosButton, FpRes.getString("button.macros"));
        macrosButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MacrosEditor editor = new MacrosEditor();
                editor.showEditor(macrosButton);
            }
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

    public Map getMetadata(String sessionID) {
        Map map = null;
        if (offerMap.get(sessionID) != null) {
            Offer offer = (Offer)offerMap.get(sessionID);
            map = offer.getMetaData();
        }
        else if (inviteMap.get(sessionID) != null) {
            map = (Map)inviteMap.get(sessionID);
        }
        return map;
    }

    private void handleRoomOpenings() {
        SparkManager.getChatManager().addChatRoomListener(new ChatRoomListenerAdapter() {
            public void chatRoomOpened(ChatRoom room) {
                if (!(room instanceof GroupChatRoom)) {
                    return;
                }
                String roomName = room.getRoomname();
                String sessionID = StringUtils.parseName(roomName);
                if (offerMap.get(sessionID) != null) {
                    Offer offer = (Offer)offerMap.get(sessionID);
                    Map metadata = offer.getMetaData();
                    decorateRoom(room, metadata);
                }
            }

            public void chatRoomClosed(ChatRoom room) {
                String roomName = room.getRoomname();
                String sessionID = StringUtils.parseName(roomName);
                offerMap.remove(sessionID);
            }
        });
    }

    public void checkForDecoration(ChatRoom chatRoom, String sessionID) {
        if (inviteMap.get(sessionID) != null) {
            Map metadata = (Map)inviteMap.get(sessionID);
            decorateRoom(chatRoom, metadata);
        }
    }

    public void decorateRoom(ChatRoom room, Map metadata) {
        String roomName = room.getRoomname();
        String sessionID = StringUtils.parseName(roomName);


        RequestUtils utils = new RequestUtils(metadata);

        addRoomInfo(sessionID, utils, room);

        addButtons(sessionID, utils, room);

        // Specify to use Typing notifications.
        GroupChatRoom groupChat = (GroupChatRoom)room;
        groupChat.setSendAndReceiveTypingNotifications(true);

        Properties props = FastpathPlugin.getLitWorkspace().getWorkgroupProperties();
        String initialResponse = props.getProperty(INITIAL_RESPONSE_PROPERTY);
        if (ModelUtil.hasLength(initialResponse)) {
            Message message = new Message();
            message.setBody(initialResponse);
            GroupChatRoom groupChatRoom = (GroupChatRoom)room;
            groupChatRoom.sendMessageWithoutNotification(message);
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

        inviteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                inviteOrTransfer(room, utils.getWorkgroup(), sessionID, false);
            }
        });

        cobrowseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CoBrowser browser = new CoBrowser(sessionID, room);
                browser.showDialog();
            }
        });


        transferButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                inviteOrTransfer(room, utils.getWorkgroup(), sessionID, true);
            }
        });

        room.getEditorBar().add(cannedResponses);
        cannedResponses.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                final ChatMacroMenu chatMacroMenu = new ChatMacroMenu(room);
                chatMacroMenu.show(cannedResponses, e.getX(), e.getY());
            }
        });

        endButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                final GroupChatRoom groupChatRoom = (GroupChatRoom)room;
                groupChatRoom.leaveChatRoom();
            }
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
            String jid = dialog.getSelectedJID();
            jid = UserManager.escapeJID(jid);

            String message = dialog.getMessage();

            // Determine who to send to.
            if (jid.contains("/")) {
                // Queueu
                InvitationManager.transferOrInviteToQueue(room, workgroup, sessionID, jid, message, transfer);
            }
            else if (StringUtils.parseServer(jid).startsWith("workgroup")) {
                InvitationManager.transferOrInviteToWorkgroup(room, workgroup, sessionID, jid, message, transfer);
            }
            else {
                InvitationManager.transferOrInviteUser(room, workgroup, sessionID, jid, message, transfer);
            }
        }
    }


    private void addRoomInfo(final String sessionID, final RequestUtils utils, final ChatRoom room) {
        final JTabbedPane tabbedPane = new JTabbedPane();

        GroupChatParticipantList participantList = ((GroupChatRoom)room).getConferenceRoomInfo();

        room.getSplitPane().setRightComponent(tabbedPane);

        Form form = null;
        try {
            form = FastpathPlugin.getWorkgroup().getWorkgroupForm();
        }
        catch (XMPPException e) {
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
        groupRoom.showPresenceMessages(false);
        room.getTranscriptWindow().clear();


        groupRoom.showPresenceMessages(true);

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

        fireFastPathChatOpened(room, sessionID, utils, tabbedPane);
        DataManager.getInstance().setMetadataForRoom(room, utils.getMetadata());
    }

    public void blink() {
        final MainWindow mainWindow = SparkManager.getMainWindow();

        if (mainWindow.isFocused()) {
            return;
        }
        else {
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
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    handleOffer(offer);
                }
            });
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

            chatQueue.getAcceptButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    toasterManager.close();
                    chatQueue.setVisible(false);

                    offerMap.put(offer.getSessionID(), offer);
                    offer.accept();
                }
            });

            chatQueue.getDeclineButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    toasterManager.close();
                    chatQueue.setVisible(false);
                    offer.reject();
                    SparkManager.getWorkspace().remove(chatQueue);
                    offerMap.remove(offer.getSessionID());
                }
            });

            final Runnable soundThread = new Runnable() {
                public void run() {
                    URL url = SoundsRes.getURL(SoundsRes.INCOMING_USER);
                    if (url != null) {
                        final AudioClip clip = Applet.newAudioClip(url);
                        SparkManager.getSoundManager().playClip(clip);
                    }
                }
            };

            TaskEngine.getInstance().submit(soundThread);
        }

        public void offerRevoked(final RevokedOffer revokedOffer) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
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
                }
            });
        }
    }


    private class InviteListener implements RoomInvitationListener {
        // Add own invitation listener
    	@Override
        public boolean handleInvitation(final Connection conn, final String room, final String inviter, final String reason, final String password, final Message message) {
            if (offerMap.containsKey(reason)) {
                RequestUtils utils = new RequestUtils(getMetadata(reason));
                String roomName = utils.getUsername();

                // Create the Group Chat Room
                final MultiUserChat chat = new MultiUserChat(SparkManager.getConnection(), room);

                GroupChatRoom groupChatRoom = ConferenceUtils.enterRoomOnSameThread(roomName, room, password);
                groupChatRoom.getSplitPane().setDividerSize(5);
                groupChatRoom.getVerticalSlipPane().setDividerLocation(0.6);
                groupChatRoom.getSplitPane().setDividerLocation(0.6);


                groupChatRoom.setTabTitle(roomName);
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
                groupChatRoom.getSubjectPanel().setRoomLabel("<html><body><b>Fastpath Conversation with " + roomName + "</b></body></html>");

                return true;
            }
            else if (message != null) {
                MetaData metaDataExt = (MetaData)message.getExtension(MetaData.ELEMENT_NAME, MetaData.NAMESPACE);
                if (metaDataExt != null) {
                    Map metadata = metaDataExt.getMetaData();
                    metadata.put("sessionID", StringUtils.parseName(room));

                    RequestUtils utils = new RequestUtils(metadata);
                    inviteMap.put(utils.getSessionID(), metadata);
                    InvitationPane pane = new InvitationPane(utils, room, inviter, reason, password, message);
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
        Map metadata = offer.getMetaData();
        String sessionID = offer.getSessionID();
        metadata.put("sessionID", sessionID);

        RequestUtils utils = new RequestUtils(metadata);
        inviteMap.put(sessionID, metadata);

        UserInvitationPane invitationPane = null;

        if (offer.getContent() instanceof InvitationRequest) {
            InvitationRequest request = (InvitationRequest)offer.getContent();
            invitationPane = new UserInvitationPane(offer, utils, request.getRoom(), request.getInviter(), request.getReason());
        }
        else if (offer.getContent() instanceof TransferRequest) {
            TransferRequest request = (TransferRequest)offer.getContent();
            invitationPane = new UserInvitationPane(offer, utils, request.getRoom(), request.getInviter(), request.getReason());
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
        String workgroupName = StringUtils.parseName(FastpathPlugin.getWorkgroup().getWorkgroupJID());

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
        String workgroupName = StringUtils.parseName(FastpathPlugin.getWorkgroup().getWorkgroupJID());

        File propertiesFile = new File(new File(Spark.getSparkUserHome(), "workgroups/" + workgroupName), "workgroup.properties");

        try {
            props.store(new FileOutputStream(propertiesFile), "Workgroup Properties");
        }
        catch (IOException e) {
            Log.error("Unable to save group properties.", e);
        }
    }

    private Message getMessage(String messageText, RequestUtils util, boolean transfer) {
        Map metadata = new HashMap();
        metadata.put("messageText", messageText);
        metadata.put("username", util.getUsername());
        metadata.put("userID", util.getUserID());
        metadata.put("transfer", Boolean.toString(transfer));
        metadata.put("question", util.getQuestion());
        metadata.put("email", util.getEmailAddress());
        metadata.put("workgroup", util.getWorkgroup());

        if (ModelUtil.hasLength(util.getRequestLocation())) {
            metadata.put("Location", util.getRequestLocation());
        }

        // Add Metadata as message extension
        final MetaData data = new MetaData(metadata);
        Message message = new Message();
        message.addExtension(data);
        return message;

    }

    private RequestUtils getRequestUtils(String sessionID) {
        Map map = getMetadata(sessionID);
        if (map != null) {
            return new RequestUtils(map);
        }
        return null;
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


    private void fireFastPathChatOpened(ChatRoom room, String sessionID, RequestUtils utils, JTabbedPane tabbedPane) {
        final Iterator list = new ArrayList(listeners).iterator();
        while (list.hasNext()) {
            FastpathListener listener = (FastpathListener)list.next();
            listener.fastpathRoomOpened(room, sessionID, utils, tabbedPane);
        }
    }


    private class PresenceChangeListener implements PresenceListener {
        public void presenceChanged(Presence presence) {
            String status = presence.getStatus();
            if (status == null) {
                status = "";
            }

            try {
                if (FastpathPlugin.getAgentSession().isOnline()) {
                    Presence.Mode mode = presence.getMode();
                    if (status == null) {
                        status = "";
                    }
                    if (mode == null) {
                        mode = Presence.Mode.available;
                    }
                    FastpathPlugin.getAgentSession().setStatus(presence.getMode(), status);
                }
            }
            catch (XMPPException e) {
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
