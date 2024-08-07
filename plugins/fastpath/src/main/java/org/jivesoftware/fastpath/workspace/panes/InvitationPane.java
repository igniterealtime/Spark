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
package org.jivesoftware.fastpath.workspace.panes;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jivesoftware.fastpath.FastpathPlugin;
import org.jivesoftware.fastpath.FpRes;
import org.jivesoftware.fastpath.resources.FastpathRes;
import org.jivesoftware.fastpath.workspace.Workpane.RoomState;
import org.jivesoftware.fastpath.workspace.assistants.RoomInformation;
import org.jivesoftware.fastpath.workspace.util.RequestUtils;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.xdata.form.FillableForm;
import org.jivesoftware.smackx.muc.Affiliate;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.workgroup.MetaData;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.ChatNotFoundException;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.LinkLabel;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.component.WrappedLabel;
import org.jivesoftware.spark.ui.ChatContainer;
import org.jivesoftware.spark.ui.conferences.ConferenceUtils;
import org.jivesoftware.spark.ui.rooms.GroupChatRoom;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.log.Log;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.util.JidUtil;
import org.jxmpp.util.XmppStringUtils;

public class InvitationPane {

    private Map<String, List<String>> metadata = null;
    private GroupChatRoom chatRoom;

    public InvitationPane(final RequestUtils request, final EntityBareJid room, final EntityBareJid inviter, String reason, final String password, final Message message) {
        final JPanel transcriptAlert = new JPanel();
        transcriptAlert.setBackground(Color.white);
        transcriptAlert.setLayout(new GridBagLayout());


        JLabel userImage = new JLabel(FastpathRes.getImageIcon(FastpathRes.FASTPATH_IMAGE_16x16));
        userImage.setHorizontalAlignment(JLabel.LEFT);
        String title = FpRes.getString("title.fastpath.invitation");
        if (request.isTransfer()) {
            title = FpRes.getString("title.fastpath.transfer");
        }


        userImage.setText(title);
        transcriptAlert.add(userImage, new GridBagConstraints(0, 0, 4, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        userImage.setFont(new Font("Dialog", Font.BOLD, 12));

        final JLabel inviterLabel = new JLabel(FpRes.getString("from") + ":");
        inviterLabel.setFont(new Font("Dialog", Font.BOLD, 11));
        final WrappedLabel inviterValueLabel = new WrappedLabel();

        String nickname = SparkManager.getUserManager().getUserNicknameFromJID(inviter);
        inviterValueLabel.setText(nickname);
        transcriptAlert.add(inviterLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
        transcriptAlert.add(inviterValueLabel, new GridBagConstraints(1, 1, 3, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));


        final JLabel nameLabel = new JLabel(FpRes.getString("room") + ":");
        nameLabel.setFont(new Font("Dialog", Font.BOLD, 11));
        final WrappedLabel valueLabel = new WrappedLabel();
        valueLabel.setText(room.toString());
        transcriptAlert.add(nameLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
        transcriptAlert.add(valueLabel, new GridBagConstraints(1, 2, 3, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));

        final JLabel messageLabel = new JLabel(FpRes.getString("message") + ":");
        messageLabel.setFont(new Font("Dialog", Font.BOLD, 11));
        final WrappedLabel messageValueLabel = new WrappedLabel();
        messageValueLabel.setText(reason);
        transcriptAlert.add(messageLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
        transcriptAlert.add(messageValueLabel, new GridBagConstraints(1, 3, 3, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));

        // Add accept button and reject button.
        final RolloverButton acceptButton = new RolloverButton("Accept", FastpathRes.getImageIcon(FastpathRes.CIRCLE_CHECK_IMAGE));
        final RolloverButton rejectButton = new RolloverButton("Decline", FastpathRes.getImageIcon(FastpathRes.SMALL_DELETE));

        ResourceUtils.resButton(acceptButton, FpRes.getString("button.accept"));
        ResourceUtils.resButton(rejectButton, FpRes.getString("button.reject"));

        LinkLabel infoButton = new LinkLabel(FpRes.getString("message.view.more.information"), null, Color.blue, Color.red);
        transcriptAlert.add(infoButton, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
        transcriptAlert.add(acceptButton, new GridBagConstraints(1, 4, 1, 1, 1.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
        transcriptAlert.add(rejectButton, new GridBagConstraints(2, 4, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
        transcriptAlert.add(new JLabel(), new GridBagConstraints(2, 5, 1, 1, 0.0, 1.0, GridBagConstraints.SOUTH, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));


        MetaData metaDataExt = message.getExtension(MetaData.ELEMENT_NAME, MetaData.NAMESPACE);
        if (metaDataExt != null) {
            metadata = metaDataExt.getMetaData();
        }


        infoButton.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                RoomInformation roomInformation = new RoomInformation();
                if (metadata != null) {
                    roomInformation.showAllInformation(metadata);
                    roomInformation.showRoomInformation();
                }

            }
        });


        acceptButton.addActionListener(actionEvent -> {
            SwingWorker waiter = new SwingWorker() {
                public Object construct() {
                    try {
                        Thread.sleep(50);
                    }
                    catch (InterruptedException e) {
                        Log.error(e);
                    }
                    return true;
                }

                public void finished() {
                    String roomName = request.getUsername();
                    chatRoom.getSplitPane().getRightComponent().setVisible(true);
                    chatRoom.getBottomPanel().setVisible(true);

                    chatRoom.getScrollPaneForTranscriptWindow().setVisible(true);
                    chatRoom.getEditorBar().setVisible(true);
                    chatRoom.getChatInputEditor().setEnabled(true);
                    chatRoom.getToolBar().setVisible(true);
                    chatRoom.getVerticalSlipPane().setDividerLocation(0.8);
                    chatRoom.getSplitPane().setDividerLocation(0.8);
                    transcriptAlert.setVisible(false);

                    String name = XmppStringUtils.parseLocalpart(roomName);

                    try {
                        chatRoom.setTabTitle(roomName);
                        chatRoom.getConferenceRoomInfo().setNicknameChangeAllowed(false);

                        chatRoom.getToolBar().setVisible(true);
                        chatRoom.getEditorBar().setVisible(true);
                        chatRoom.getChatInputEditor().setEnabled(true);

                        ChatContainer chatContainer = SparkManager.getChatManager().getChatContainer();
                        chatContainer.setChatRoomTitle(chatRoom, roomName);
                        if (chatContainer.getActiveChatRoom() == chatRoom) {
                            chatContainer.getChatFrame().setTitle(roomName);
                        }

                    }
                    catch (Exception e) {
                        Log.error(e);
                    }

                    ConferenceUtils.enterRoomOnSameThread(roomName, room, null, password);
                    removeOwner(chatRoom.getMultiUserChat());

                    FastpathPlugin.getLitWorkspace().checkForDecoration(chatRoom, request.getSessionID());
                }
            };

            waiter.start();
        });

        // Add to Chat window
        ChatManager chatManager = SparkManager.getChatManager();

        try {
            chatRoom = chatManager.getGroupChat(room);
        }
        catch (ChatNotFoundException e) {
            MultiUserChat chat = MultiUserChatManager.getInstanceFor( SparkManager.getConnection() ).getMultiUserChat( room );
            chatRoom = new GroupChatRoom(chat);
        }

        chatRoom.setTabTitle(title);
        chatRoom.setTabIcon(SparkRes.getImageIcon(SparkRes.FASTPATH_IMAGE_16x16));
        chatRoom.getChatWindowPanel().add(transcriptAlert, new GridBagConstraints(0, 9, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(1, 0, 1, 0), 0, 0));

        // set invisible
        chatRoom.getSplitPane().getRightComponent().setVisible(false);
        chatRoom.getBottomPanel().setVisible(false);
        chatRoom.getScrollPaneForTranscriptWindow().setVisible(false);

        SparkManager.getChatManager().getChatContainer().addChatRoom(chatRoom);

        FastpathPlugin.getLitWorkspace().addFastpathChatRoom(chatRoom, RoomState.invitationRequest);

        rejectButton.addActionListener(actionEvent -> {
            // Add to Chat window
            ChatManager chatManager1 = SparkManager.getChatManager();
            chatManager1.removeChat(chatRoom);

            try
            {
                MultiUserChatManager.getInstanceFor( SparkManager.getConnection() ).decline( room, inviter, "No thank you" );
            }
            catch ( SmackException.NotConnectedException | InterruptedException e )
            {
                Log.warning( "Unable to deline invatation from " + inviter + " to join room " + room, e );
            }
        });

    }

    /**
     * Removes oneself as an owner of the room.
     *
     * @param muc the <code>MultiUserChat</code> of the chat room.
     */
    private void removeOwner(MultiUserChat muc) {
        if (muc.isJoined()) {
            // Try and remove myself as an owner if I am one.
            Collection<Affiliate> owners;
            try {
                owners = muc.getOwners();
            }
            catch (XMPPException | SmackException | InterruptedException e1) {
                return;
            }

            if (owners == null) {
                return;
            }

            Iterator<Affiliate> iter = owners.iterator();

            List<Jid> list = new ArrayList<>();
            while (iter.hasNext()) {
                Affiliate affiliate = iter.next();
                Jid jid = affiliate.getJid();
                if (!jid.equals(SparkManager.getSessionManager().getUserBareAddress())) {
                    list.add(jid);
                }
            }
            if (list.size() > 0) {
                try {
                    FillableForm form = muc.getConfigurationForm().getFillableForm();
                    List<String> jidStrings = new ArrayList<>(list.size());
                    JidUtil.toStrings(list, jidStrings);
                    form.setAnswer("muc#roomconfig_roomowners", jidStrings);

                    // new DataFormDialog(groupChat, form);
                    muc.sendConfigurationForm(form);
                }
                catch (XMPPException | SmackException | InterruptedException e) {
                    Log.error(e);
                }
            }
        }
    }
}
