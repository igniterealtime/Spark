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
package org.jivesoftware.fastpath.workspace.panes;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import org.jivesoftware.fastpath.FastpathPlugin;
import org.jivesoftware.fastpath.FpRes;
import org.jivesoftware.fastpath.resources.FastpathRes;
import org.jivesoftware.fastpath.workspace.Workpane;
import org.jivesoftware.fastpath.workspace.assistants.RoomInformation;
import org.jivesoftware.fastpath.workspace.util.RequestUtils;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.muc.Affiliate;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.workgroup.agent.Offer;
import org.jivesoftware.smackx.workgroup.agent.TransferRequest;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.ChatNotFoundException;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.LinkLabel;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.component.WrappedLabel;
import org.jivesoftware.spark.ui.ChatContainer;
import org.jivesoftware.spark.ui.conferences.ConferenceUtils;
import org.jivesoftware.spark.ui.rooms.GroupChatRoom;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.SwingTimerTask;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.TaskEngine;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.alerts.SparkToaster;

/**
 * Handles invitations and transfers of Fastpath Requests.
 */
public class UserInvitationPane {

    private Map metadata;
    private AcceptListener listener;

    private Offer offer;

    private JProgressBar progressBar;

    private SparkToaster toasterManager;


    public UserInvitationPane(final Offer offer, final RequestUtils request, final String fullRoomJID, final String inviter, String reason) {
        // Add to Chat window
        ChatManager chatManager = SparkManager.getChatManager();


        try {
            GroupChatRoom chatRoom = chatManager.getGroupChat(fullRoomJID);
            if (chatRoom.isActive()) {
                offer.reject();
                return;
            }
        }
        catch (ChatNotFoundException e) {
        }


        final JPanel transcriptAlert = new JPanel();
        transcriptAlert.setBackground(Color.white);
        transcriptAlert.setLayout(new GridBagLayout());

        this.offer = offer;

        progressBar = new JProgressBar();
        progressBar.setFont(new Font("Dialog", Font.BOLD, 11));


        final JPanel topPanel = new JPanel();
        topPanel.setBackground(Color.white);
        topPanel.setLayout(new GridBagLayout());


        JLabel userImage = new JLabel(FastpathRes.getImageIcon(FastpathRes.FASTPATH_IMAGE_16x16));
        userImage.setHorizontalAlignment(JLabel.LEFT);
        String title = FpRes.getString("title.fastpath.invitation");
        if (offer.getContent() instanceof TransferRequest) {
            title = FpRes.getString("title.fastpath.transfer");
        }


        userImage.setText(title);
        topPanel.add(userImage, new GridBagConstraints(0, 0, 4, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        userImage.setFont(new Font("Dialog", Font.BOLD, 12));

        final JLabel inviterLabel = new JLabel(FpRes.getString("from") + ":");
        inviterLabel.setFont(new Font("Dialog", Font.BOLD, 11));
        final WrappedLabel inviterValueLabel = new WrappedLabel();
        inviterValueLabel.setBackground(Color.white);

        String nickname = SparkManager.getUserManager().getUserNicknameFromJID(inviter);
        inviterValueLabel.setText(nickname);
        topPanel.add(inviterLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
        topPanel.add(inviterValueLabel, new GridBagConstraints(1, 1, 3, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));


        final JLabel nameLabel = new JLabel(FpRes.getString("room") + ":");
        nameLabel.setFont(new Font("Dialog", Font.BOLD, 11));
        final WrappedLabel valueLabel = new WrappedLabel();
        valueLabel.setBackground(Color.white);
        valueLabel.setText(fullRoomJID);
        topPanel.add(nameLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
        topPanel.add(valueLabel, new GridBagConstraints(1, 2, 3, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));

        final JLabel messageLabel = new JLabel(FpRes.getString("message") + ":");
        messageLabel.setFont(new Font("Dialog", Font.BOLD, 11));
        final WrappedLabel messageValueLabel = new WrappedLabel();
        messageValueLabel.setBackground(Color.white);
        messageValueLabel.setText(reason);
        topPanel.add(messageLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
        topPanel.add(messageValueLabel, new GridBagConstraints(1, 3, 3, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));

        // Add accept button and reject button.
        RolloverButton acceptButton = new RolloverButton("Accept", FastpathRes.getImageIcon(FastpathRes.CIRCLE_CHECK_IMAGE));
        RolloverButton rejectButton = new RolloverButton("Decline", FastpathRes.getImageIcon(FastpathRes.SMALL_DELETE));

        ResourceUtils.resButton(acceptButton, FpRes.getString("button.accept"));
        ResourceUtils.resButton(rejectButton, FpRes.getString("button.reject"));

        LinkLabel infoButton = new LinkLabel(FpRes.getString("message.view.more.information"), null, Color.blue, Color.red);
        transcriptAlert.add(topPanel, new GridBagConstraints(0, 3, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));


        transcriptAlert.add(progressBar, new GridBagConstraints(0, 4, 3, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(15, 2, 2, 2), 0, 0));

        transcriptAlert.add(infoButton, new GridBagConstraints(0, 5, 1, 1, 0.0, 1.0, GridBagConstraints.SOUTH, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
        transcriptAlert.add(acceptButton, new GridBagConstraints(1, 5, 1, 1, 1.0, 1.0, GridBagConstraints.SOUTH, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
        transcriptAlert.add(rejectButton, new GridBagConstraints(2, 5, 1, 1, 0.0, 1.0, GridBagConstraints.SOUTH, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));

        metadata = offer.getMetaData();


        infoButton.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                RoomInformation roomInformation = new RoomInformation();
                if (metadata != null) {
                    roomInformation.showAllInformation(metadata);
                    roomInformation.showRoomInformation();
                }

            }
        });


        acceptButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {

                final TimerTask loadRoomTask = new SwingTimerTask() {
                    public void doRun() {
                        offer.accept();
                        closeToaster();
                        startFastpathChat(fullRoomJID, request.getUsername());
                    }
                };

                TaskEngine.getInstance().schedule(loadRoomTask, 100);
            }
        });


        rejectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                rejectOffer();
            }
        });

        // Start progress bart
        final Date endTime = offer.getExpiresDate();
        Date now = new Date();

        long mill = endTime.getTime() - now.getTime();
        int seconds = (int)(mill / 1000);
        progressBar.setMaximum(seconds);
        progressBar.setValue(seconds);


        SwingWorker worker = new SwingWorker() {
            public Object construct() {
                while (true) {
                    Date now = new Date();
                    if (now.getTime() >= endTime.getTime()) {
                        break;
                    }

                    try {
                        Thread.sleep(1000);
                    }
                    catch (InterruptedException e) {
                        Log.error(e);
                    }

                    progressBar.setValue(progressBar.getValue() - 1);
                    progressBar.setStringPainted(true);


                    int seconds = (int)(endTime.getTime() - now.getTime()) / 1000;
                    if (seconds <= 60) {
                        String timeString = seconds + " " + FpRes.getString("seconds");
                        progressBar.setString(timeString);
                    }
                    else {
                        long difference = endTime.getTime() - now.getTime();
                        String timeString = ModelUtil.getTimeFromLong(difference);

                        progressBar.setString(timeString);
                    }
                }
                return progressBar;
            }
        };

        worker.start();


        toasterManager = new SparkToaster();
        toasterManager.setHidable(false);


        toasterManager.setToasterHeight((int)transcriptAlert.getPreferredSize().getHeight() + 40);
        toasterManager.setToasterWidth(300);
        toasterManager.setDisplayTime(500000000);

        toasterManager.showToaster("Incoming Fastpath Request", transcriptAlert);
        toasterManager.hideTitle();
    }

    /**
     * Reject the current offer.
     */
    public void rejectOffer() {
        offer.reject();

        if (listener != null) {
            listener.noOption();
            listener = null;
        }

        closeToaster();
    }

    public void dispose() {
        if (listener != null) {
            listener.noOption();
            listener = null;
        }

        closeToaster();
    }

    /**
     * Removes oneself as an owner of the room.
     *
     * @param muc the <code>MultiUserChat</code> of the chat room.
     */
    private void removeOwner(MultiUserChat muc) {
        if (muc.isJoined()) {
            // Try and remove myself as an owner if I am one.
            Collection<Affiliate> owners = null;
            try {
                owners = muc.getOwners();
            }
            catch (XMPPException e1) {
                return;
            }

            if (owners == null) {
                return;
            }

            Iterator<Affiliate> iter = owners.iterator();

            List<String> list = new ArrayList<String>();
            while (iter.hasNext()) {
                Affiliate affilitate = iter.next();
                String jid = affilitate.getJid();
                if (!jid.equals(SparkManager.getSessionManager().getBareAddress())) {
                    list.add(jid);
                }
            }
            if (list.size() > 0) {
                try {
                    Form form = muc.getConfigurationForm().createAnswerForm();
                    form.setAnswer("muc#roomconfig_roomowners", list);

                    // new DataFormDialog(groupChat, form);
                    muc.sendConfigurationForm(form);
                }
                catch (XMPPException e) {
                    Log.error(e);
                }
            }
        }
    }

    /**
     * Sets the AcceptListener to use with this dialog instance.
     *
     * @param listener the <code>AcceptListener</code> to use with this instance.
     */
    public void setAcceptListener(AcceptListener listener) {
        this.listener = listener;
    }

    /**
     * Used to handle yes/no selection in dialog. You would use this simply to
     * be notified when a user has either clicked on the yes or no dialog.
     */
    public interface AcceptListener {

        /**
         * Fired when the Yes button has been clicked.
         */
        void yesOption();

        /**
         * Fired when the No button has been clicked.
         */
        void noOption();
    }

    private void closeToaster() {
        toasterManager.close();
    }

    private void startFastpathChat(String fullRoomJID, String roomName) {
        // Add to Chat window
        ChatManager chatManager = SparkManager.getChatManager();

        GroupChatRoom chatRoom;
        try {
            chatRoom = chatManager.getGroupChat(fullRoomJID);
            if (!chatRoom.isActive()) {
                // Remove old room, add new room.
                chatManager.removeChat(chatRoom);

                MultiUserChat chat = new MultiUserChat(SparkManager.getConnection(), fullRoomJID);
                chatRoom = new GroupChatRoom(chat);
            }
            else {
                // Already in the room, do not process invitation
                offer.reject();
                return;
            }

        }
        catch (ChatNotFoundException e) {
            MultiUserChat chat = new MultiUserChat(SparkManager.getConnection(), fullRoomJID);
            chatRoom = new GroupChatRoom(chat);
        }

        chatRoom.getSplitPane().setDividerSize(5);

        chatRoom.getSplitPane().getRightComponent().setVisible(true);
        chatRoom.getBottomPanel().setVisible(true);

        chatRoom.getScrollPaneForTranscriptWindow().setVisible(true);
        chatRoom.getEditorBar().setVisible(true);
        chatRoom.getChatInputEditor().setEnabled(true);
        chatRoom.getToolBar().setVisible(true);


        chatRoom.getVerticalSlipPane().setDividerLocation(0.8);
        chatRoom.getSplitPane().setDividerLocation(0.6);

        try {
            chatRoom.setTabTitle(roomName);
            chatRoom.getConferenceRoomInfo().setNicknameChangeAllowed(false);

            chatRoom.getToolBar().setVisible(true);
            chatRoom.getEditorBar().setVisible(true);
            chatRoom.getChatInputEditor().setEnabled(true);

            ChatContainer chatContainer = SparkManager.getChatManager().getChatContainer();
            chatContainer.addChatRoom(chatRoom);

            FastpathPlugin.getLitWorkspace().addFastpathChatRoom(chatRoom, Workpane.RoomState.activeRoom);

            chatContainer.setChatRoomTitle(chatRoom, roomName);

            if (chatContainer.getActiveChatRoom() == chatRoom) {
                chatContainer.getChatFrame().setTitle(roomName);
            }

        }
        catch (Exception e) {
            Log.error(e);
        }

        ConferenceUtils.enterRoomOnSameThread(roomName, fullRoomJID, null);
        removeOwner(chatRoom.getMultiUserChat());

        FastpathPlugin.getLitWorkspace().checkForDecoration(chatRoom, offer.getSessionID());

        if (listener != null) {
            listener.yesOption();
            listener = null;
        }
    }


}


