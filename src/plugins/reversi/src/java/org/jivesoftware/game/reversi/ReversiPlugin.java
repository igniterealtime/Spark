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
package org.jivesoftware.game.reversi;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.StanzaIdFilter;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.BackgroundPanel;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatRoomButton;
import org.jivesoftware.spark.ui.ChatRoomListener;
import org.jivesoftware.spark.ui.ChatRoomListenerAdapter;
import org.jivesoftware.spark.ui.rooms.ChatRoomImpl;
import org.jivesoftware.spark.util.log.Log;
import org.jxmpp.util.XmppStringUtils;

/**
 * Reversi plugin. Reversi is a two-player, turn-based game. See
 * {@link ReversiModel} for more details on how the game works.
 * 
 * @author Matt Tucker
 * @author Bill Lynch
 */
public class ReversiPlugin implements Plugin {

    private ChatRoomListener chatRoomListener;
    private StanzaListener gameOfferListener;

    private ConcurrentHashMap<String, JPanel> gameOffers;
    private ConcurrentHashMap<String, JPanel> gameInvitations;
    
    private JPanel inviteAlert;

    public void initialize() {
        // Offers and invitations hold all pending game offers we've sent to
        // other users or incoming
        // invitations. The map key is always the opponent's JID. The map value
        // is a transcript alert
        // UI component.
        gameOffers = new ConcurrentHashMap<String, JPanel>();
        gameInvitations = new ConcurrentHashMap<String, JPanel>();

        // Add Reversi item to chat toolbar.
        addToolbarButton();

        // Add Smack providers. The plugin uses custom XMPP extensions to
        // communicate game offers
        // and current game state. Adding the Smack providers lets us use the
        // custom protocol.
        ProviderManager.addIQProvider(GameOffer.ELEMENT_NAME, GameOffer.NAMESPACE, GameOffer.class);
        ProviderManager.addExtensionProvider(GameMove.ELEMENT_NAME, GameMove.NAMESPACE, GameMove.class);
        ProviderManager.addExtensionProvider(GameForfeit.ELEMENT_NAME, GameForfeit.NAMESPACE, GameForfeit.class);

        // Add IQ listener to listen for incoming game invitations.
        gameOfferListener = new StanzaListener() {
            public void processPacket(Stanza stanza) {
                GameOffer invitation = (GameOffer) stanza;
                if (invitation.getType() == IQ.Type.get) {
                    showInvitationAlert(invitation);
                } else if (invitation.getType() == IQ.Type.error) {
                    handleErrorIQ(invitation);
                }
            }
        };
        SparkManager.getConnection().addAsyncStanzaListener(gameOfferListener, new StanzaTypeFilter(GameOffer.class));
    }

    public void shutdown() {
        // Remove Reversi button from chat toolbar.
        removeToolbarButton();
        // Remove IQ listener
        SparkManager.getConnection().removeAsyncStanzaListener(gameOfferListener);
//
//        // See if there are any pending offers or invitations. If so, cancel
//        // them.
//        for (Iterator<String> i = gameOffers.keySet().iterator(); i.hasNext();i.next()) {
//          System.out.println("gameoffer");
//
//        }
//        System.out.println(gameInvitations.size());
//        for (Iterator<String> i = gameInvitations.keySet().iterator(); i.hasNext(); i.next()) {
//          
//        }
        gameOffers.clear();
        gameInvitations.clear();

        // Remove Smack providers.
        ProviderManager.removeIQProvider(GameOffer.ELEMENT_NAME, GameOffer.NAMESPACE);
        ProviderManager.removeExtensionProvider(GameMove.ELEMENT_NAME, GameMove.NAMESPACE);
        ProviderManager.removeExtensionProvider(GameForfeit.ELEMENT_NAME, GameForfeit.NAMESPACE);
    }

    public boolean canShutDown() {
        // The plugin is able to fully clean itself up, so return true.
        return true;
    }

    public void uninstall() {
        // Do nothing. Reversi has no permanent resources so shutdown can
        // already do a full cleanup.
    }

    /**
     * Display an alert that allows the user to accept or reject a game
     * invitation.
     * 
     * @param invitation
     *            the game invitation.
     */
    private void showInvitationAlert(final GameOffer invitation) {
        // Got an offer to start a new game. So, make sure that a chat is
        // started with the other
        // user and show an invite panel.
        final ChatRoom room = SparkManager.getChatManager().getChatRoom( XmppStringUtils.parseBareJid(invitation.getFrom()) );

        inviteAlert = new JPanel();
        inviteAlert.setLayout(new BorderLayout());

        JPanel invitePanel = new JPanel() {
            private static final long serialVersionUID = 5942001917654498678L;

            protected void paintComponent(Graphics g) {
                ImageIcon imageIcon = ReversiRes.getImageIcon(ReversiRes.REVERSI_ICON);
                Image image = imageIcon.getImage();
                g.drawImage(image, 0, 0, null);
            }
        };
        invitePanel.setPreferredSize(new Dimension(24, 24));
        inviteAlert.add(invitePanel, BorderLayout.WEST);
        JPanel content = new JPanel(new BorderLayout());
        String opponentName = invitation.getFrom(); // TODO: convert to more
                                                    // readable name.
        content.add(new JLabel(opponentName + " is requesting a Reversi game ..."), BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();

        // The accept button. When clicked, accept the game offer.
        final JButton acceptButton = new JButton("Accept");
        final JButton declineButton = new JButton("Decline");

        acceptButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Accept the game offer by sending a positive reply packet.
                GameOffer reply = new GameOffer();
                reply.setTo(invitation.getFrom());
                reply.setStanzaId(invitation.getStanzaId());
                reply.setType(IQ.Type.result);
                try
                {
                    SparkManager.getConnection().sendStanza(reply);
                }
                catch ( SmackException.NotConnectedException e1 )
                {
                    Log.warning( "Unable to accept game offer from " + invitation.getFrom(), e1 );
                }

                // Hide the response panel. TODO: make this work.
                room.getTranscriptWindow().remove(inviteAlert);
                inviteAlert.remove(1);
                inviteAlert.add(new JLabel("Starting game..."), BorderLayout.CENTER);
                declineButton.setEnabled(false);
                acceptButton.setEnabled(false);
                // Remove the invitation from the map.
                gameInvitations.remove(invitation.getFrom());
                // Show the game board.
                showReversiBoard(invitation.getGameID(), room, !invitation.isStartingPlayer(), invitation.getFrom());
            }
        });
        buttonPanel.add(acceptButton);

        // The decline button. When clicked, reject the game offer.

        declineButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Reject the game offer by sending an error packet.
                GameOffer reply = new GameOffer();
                reply.setTo(invitation.getFrom());
                reply.setStanzaId(invitation.getPacketID());
                reply.setType(IQ.Type.error);
                try
                {
                    SparkManager.getConnection().sendStanza(reply);
                }
                catch ( SmackException.NotConnectedException e1 )
                {
                    Log.warning( "Unable to decline game offer from " + invitation.getFrom(), e1 );
                }

                // Hide the response panel. TODO: make this work.
                room.getTranscriptWindow().remove(inviteAlert);

                declineButton.setVisible(false);
                acceptButton.setVisible(false);
                // Remove the invitation from the map.
                gameInvitations.remove(invitation.getFrom());
            }
        });
        buttonPanel.add(declineButton);
        content.add(buttonPanel, BorderLayout.SOUTH);
        inviteAlert.add(content, BorderLayout.CENTER);

        // Add the invitation to the Map of invites. If there's a pending
        // invite, remove it
        // before adding the new one (possible if the opponent sends two invites
        // in a row).
        Object oldInvitation = gameInvitations.put(invitation.getFrom(), inviteAlert);
        if (oldInvitation != null) {
            // TODO: clean it up by removing it from the transcript window.
        }

        // Add the response panel to the transcript window.
        room.getTranscriptWindow().addComponent(inviteAlert);
    }

    private void handleErrorIQ(final GameOffer invitation) {
        // Maybe the initiator canceled the game offer, lets check that
        if (gameInvitations.containsKey(invitation.getFrom())) {
            inviteAlert.remove(1);
            JLabel userCanceled = new JLabel("The other player rejected the game invitation"); 
            inviteAlert.add(userCanceled);
            gameInvitations.remove(invitation.getFrom());
        }
    }

    /**
     * Adds the Reversi toolbar button.
     */
    private void addToolbarButton() {
        ChatManager manager = SparkManager.getChatManager();
        chatRoomListener = new ChatRoomListenerAdapter() {

            ImageIcon icon = ReversiRes.getImageIcon(ReversiRes.REVERSI_ICON);

            public void chatRoomOpened(final ChatRoom room) {
        	if(!(room instanceof ChatRoomImpl))
		{
		    // Don't do anything if this is not a 1on1-Chat
		    return;
		}
                ChatRoomButton button = new ChatRoomButton(icon);
                button.setToolTipText("Reversi");
                room.getToolBar().addChatRoomButton(button);

                // Add a button listener that sends out a game invite on a user
                // click.
                button.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        // Show "requesting a game panel"
                        final JPanel request = new JPanel();
                        request.setLayout(new BorderLayout());
                        JPanel requestPanel = new JPanel() {
                            private static final long serialVersionUID = 4490592207923738251L;

                            protected void paintComponent(Graphics g) {
                                g.drawImage(icon.getImage(), 0, 0, null);
                            }
                        };
                        requestPanel.setPreferredSize(new Dimension(24, 24));
                        request.add(requestPanel, BorderLayout.WEST);

                        String opponentJID = ((ChatRoomImpl) room).getJID();
                        String opponentName = "[" + opponentJID + "]"; // TODO:
                                                                       // convert
                                                                       // to
                                                                       // more
                                                                       // readable
                                                                       // name.

                        final JPanel content = new JPanel(new BorderLayout());
                        final JLabel label = new JLabel("Requesting a Reversi game with " + opponentName + ", please wait..."); 
                        content.add(label, BorderLayout.CENTER);
                        JPanel buttonPanel = new JPanel();
                        final JButton cancelButton = new JButton("Cancel");
                        cancelButton.addActionListener(new ActionListener() {

                            @Override
                            public void actionPerformed(ActionEvent e) {
                               GameOffer reply = new GameOffer();
                               reply.setTo(((ChatRoomImpl) room).getJID());
                               reply.setType(IQ.Type.error);
                               try
                               {
                                   SparkManager.getConnection().sendStanza(reply);
                               }
                               catch ( SmackException.NotConnectedException e1 )
                               {
                                   Log.warning( "Unable to send invitation cancellation to " + reply.getTo(), e1 );
                               }
                               cancelButton.setText("Canceled");
                               cancelButton.setEnabled(false);
                            }
                        });
                        buttonPanel.add(cancelButton, BorderLayout.SOUTH);
                        content.add(buttonPanel, BorderLayout.SOUTH);
                        request.add(content, BorderLayout.CENTER);
                        room.getTranscriptWindow().addComponent(request);

                        final GameOffer offer = new GameOffer();
                        offer.setTo(opponentJID);

                        // Add a listener for a reply to our offer.
                        SparkManager.getConnection().addAsyncStanzaListener(new StanzaListener() {
                            public void processPacket(Stanza stanza) {
                                GameOffer offerReply = ((GameOffer) stanza);

                                if (offerReply.getType() == IQ.Type.result) {
                                    // Remove the offer panel
                                    room.getTranscriptWindow().remove(request);
                                    content.remove(1);
                                    label.setText("Starting game...");
                                    // Show game board (using original offer!).
                                    showReversiBoard(offer.getGameID(), room, offer.isStartingPlayer(), offerReply.getFrom());
                                } else if (offerReply.getType() == IQ.Type.error) {
                                    cancelButton.setVisible(false);
                                    JPanel userDeclinedPanel = new JPanel(new BorderLayout());
                                    JLabel userDeclined = new JLabel("User declined...");
                                    userDeclinedPanel.add(userDeclined, BorderLayout.SOUTH);
                                    request.add(userDeclinedPanel, BorderLayout.SOUTH);
                                }
                                // TODO: Handle error case
                            }
                        }, new StanzaIdFilter(offer.getStanzaId()));

                        try
                        {
                            SparkManager.getConnection().sendStanza(offer);
                        }
                        catch ( SmackException.NotConnectedException e1 )
                        {
                            Log.warning( "Unable to send invitation to " + offer.getTo(), e1 );
                        }
                    }
                });

            }

            public void chatRoomClosed(ChatRoom room) {
                super.chatRoomClosed(room);
                // TODO: if game is in progress, close it down. What we need is
                // an API that lets us see
                // TODO: if there's a transcript alert currently there.
            }
        };
        manager.addChatRoomListener(chatRoomListener);
    }

    /**
     * Displays the game board. This is called after an offer has been accepted
     * or after accepting an invitation.
     * 
     * @param gameID
     *            the game ID.
     * @param room
     *            the chat room to display the game board in.
     * @param startingPlayer
     *            true if this player is the starting player (black).
     * @param opponentJID
     *            the opponent's JID.
     */
    private void showReversiBoard(int gameID, ChatRoom room, boolean startingPlayer, String opponentJID) {
        JSplitPane pane = room.getSplitPane();
        pane.setResizeWeight(1.0);
        BackgroundPanel reversiBackground = new BackgroundPanel();
        reversiBackground.setLayout(new BorderLayout());
        reversiBackground.setOpaque(false);
        ReversiPanel reversi = new ReversiPanel(SparkManager.getConnection(), gameID, startingPlayer, opponentJID);
        reversiBackground.add(reversi, BorderLayout.CENTER);
        pane.setRightComponent(reversiBackground);
        reversi.setMinimumSize(new Dimension(ReversiPanel.TOTAL_WIDTH, ReversiPanel.TOTAL_HEIGHT));
    }

    /**
     * Removes the Reversi toolbar button.
     */
    private void removeToolbarButton() {
        ChatManager manager = SparkManager.getChatManager();
        manager.removeChatRoomListener(chatRoomListener);
        // TODO: remove actual buttons from toolbar.
    }
}