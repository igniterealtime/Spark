/**
 * $Revision$
 * $Date$
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 * This software is the proprietary information of Jive Software. Use is subject to license terms.
 */

package org.jivesoftware.game.reversi;

import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.ui.ChatRoomListener;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatRoomListenerAdapter;
import org.jivesoftware.spark.ui.ChatRoomButton;
import org.jivesoftware.spark.ui.rooms.ChatRoomImpl;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.component.BackgroundPanel;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Reversi plugin. Reversi is a two-player, turn-based game. See {@link ReversiModel} for more details
 * on how the game works.
 *
 * @author Matt Tucker
 * @author Bill Lynch
 */
public class ReversiPlugin implements Plugin {

    private ChatRoomListener chatRoomListener;
    private PacketListener gameOfferListener;

    private ConcurrentHashMap<String,JPanel> gameOffers;
    private ConcurrentHashMap<String,JPanel> gameInvitations;

    public void initialize() {
        // Offers and invitations hold all pending game offers we've sent to other users or incoming
        // invitations. The map key is always the opponent's JID. The map value is a transcript alert
        // UI component.
        gameOffers = new ConcurrentHashMap<String,JPanel>();
        gameInvitations = new ConcurrentHashMap<String,JPanel>();

        // Add Reversi item to chat toolbar.
        addToolbarButton();

        // Add Smack providers. The plugin uses custom XMPP extensions to communicate game offers
        // and current game state. Adding the Smack providers lets us use the custom protocol.
        ProviderManager.getInstance().addIQProvider(GameOffer.ELEMENT_NAME, GameOffer.NAMESPACE, GameOffer.class);
        ProviderManager.getInstance().addExtensionProvider(GameMove.ELEMENT_NAME, GameMove.NAMESPACE, GameMove.class);
        ProviderManager.getInstance().addExtensionProvider(GameForfeit.ELEMENT_NAME, GameForfeit.NAMESPACE, GameForfeit.class);

        // Add IQ listener to listen for incoming game invitations.
        gameOfferListener = new PacketListener() {
            public void processPacket(Packet packet) {
                GameOffer invitation = (GameOffer)packet;
                if (invitation.getType() == IQ.Type.GET) {
                    showInvitationAlert(invitation);
                }
            }
        };
        SparkManager.getConnection().addPacketListener(gameOfferListener, new PacketTypeFilter(GameOffer.class));
    }

    public void shutdown() {
        // Remove Reversi button from chat toolbar.
        removeToolbarButton();

        // Remove IQ listener
        SparkManager.getConnection().removePacketListener(gameOfferListener);

        // See if there are any pending offers or invitations. If so, cancel them.
        for (Iterator i=gameOffers.keySet().iterator(); i.hasNext(); ) {
            //String opponentJID = (String)i.next();
            // TODO: cancel game offer.
        }
        for (Iterator i=gameInvitations.keySet().iterator(); i.hasNext(); ) {
            //String opponentJID = (String)i.next();
            // TODO: reject game invitation.
        }
        gameOffers.clear();
        gameInvitations.clear();

        // Remove Smack providers.
        ProviderManager.getInstance().removeIQProvider(GameOffer.ELEMENT_NAME, GameOffer.NAMESPACE);
        ProviderManager.getInstance().removeExtensionProvider(GameMove.ELEMENT_NAME, GameMove.NAMESPACE);
        ProviderManager.getInstance().removeExtensionProvider(GameForfeit.ELEMENT_NAME, GameForfeit.NAMESPACE);
    }

    public boolean canShutDown() {
        // The plugin is able to fully clean itself up, so return true.
        return true;
    }

    public void uninstall() {
        // Do nothing. Reversi has no permanent resources so shutdown can already do a full cleanup.
    }

    /**
     * Display an alert that allows the user to accept or reject a game invitation.
     *
     * @param invitation the game invitation.
     */
    private void showInvitationAlert(final GameOffer invitation) {
        // Got an offer to start a new game. So, make sure that a chat is started with the other
        // user and show an invite panel.
        final ChatRoom room = SparkManager.getChatManager().getChatRoom(invitation.getFrom());

        final JPanel inviteAlert = new JPanel();
        inviteAlert.setLayout(new BorderLayout());

        JPanel invitePanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                ImageIcon imageIcon = new ImageIcon(getClass().getResource("images/reversi-icon.png"));
                Image image = imageIcon.getImage();
                g.drawImage(image, 0, 0, null);
            }
        };
        invitePanel.setPreferredSize(new Dimension(24,24));
        inviteAlert.add(invitePanel, BorderLayout.WEST);
        JPanel content = new JPanel(new BorderLayout());
        String opponentName = invitation.getFrom(); // TODO: convert to more readable name.
        content.add(new JLabel(opponentName + " is requesting a Reversi game ..."), BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();

        // The accept button. When clicked, accept the game offer.
        JButton acceptButton = new JButton("Accept");
        acceptButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Accept the game offer by sending a positive reply packet.
                GameOffer reply = new GameOffer();
                reply.setTo(invitation.getFrom());
                reply.setPacketID(invitation.getPacketID());
                reply.setType(IQ.Type.RESULT);
                SparkManager.getConnection().sendPacket(reply);
                // Hide the response panel. TODO: make this work.
                room.getTranscriptWindow().remove(inviteAlert);
                // Remove the invitation from the map.
                gameInvitations.remove(invitation.getFrom());
                // Show the game board.
                showReversiBoard(invitation.getGameID(), room, !invitation.isStartingPlayer(), invitation.getFrom());
            }
        });
        buttonPanel.add(acceptButton);

        // The decline button. When clicked, reject the game offer.
        JButton declineButton = new JButton("Decline");
        declineButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Reject the game offer by sending an error packet.
                GameOffer reply = new GameOffer();
                reply.setTo(invitation.getFrom());
                reply.setPacketID(invitation.getPacketID());
                reply.setType(IQ.Type.ERROR);
                SparkManager.getConnection().sendPacket(reply);
                // Hide the response panel. TODO: make this work.
                room.getTranscriptWindow().remove(inviteAlert);
                // Remove the invitation from the map.
                gameInvitations.remove(invitation.getFrom());
            }
        });
        buttonPanel.add(declineButton);
        content.add(buttonPanel, BorderLayout.SOUTH);
        inviteAlert.add(content, BorderLayout.CENTER);

        // Add the invitation to the Map of invites. If there's a pending invite, remove it
        // before adding the new one (possible if the opponent sends two invites in a row).
        Object oldInvitation = gameInvitations.put(invitation.getFrom(), inviteAlert);
        if (oldInvitation != null) {
            // TODO: clean it up by removing it from the transcript window.
        }

        // Add the response panel to the transcript window.
        room.getTranscriptWindow().addComponent(inviteAlert);
    }

    /**
     * Adds the Reversi toolbar button.
     */
    private void addToolbarButton() {
        ChatManager manager = SparkManager.getChatManager();
        chatRoomListener = new ChatRoomListenerAdapter() {

            ImageIcon icon = new ImageIcon(getClass().getResource("images/reversi-icon.png"));

            public void chatRoomOpened(final ChatRoom room) {
                ChatRoomButton button = new ChatRoomButton("Reversi", icon);
                room.getToolBar().addChatRoomButton(button);

                // Add a button listener that sends out a game invite on a user click.
                button.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        // Show "requesting a game panel"
                        final JPanel request = new JPanel();
                        request.setLayout(new BorderLayout());
                        JPanel requestPanel = new JPanel() {
                            protected void paintComponent(Graphics g) {
                                g.drawImage(icon.getImage(), 0, 0, null);
                            }
                        };
                        requestPanel.setPreferredSize(new Dimension(24,24));
                        request.add(requestPanel, BorderLayout.WEST);

                        String opponentJID = ((ChatRoomImpl)room).getJID();
                        String opponentName = "["+opponentJID+"]"; // TODO: convert to more readable name.

                        JPanel content = new JPanel(new BorderLayout());
                        content.add(new JLabel("Requesting a Reversi game with " + opponentName + ", please wait..."), BorderLayout.CENTER);
                        JPanel buttonPanel = new JPanel();
                        buttonPanel.add(new JButton("Cancel"), BorderLayout.SOUTH);
                        content.add(buttonPanel, BorderLayout.SOUTH);
                        request.add(content, BorderLayout.CENTER);
                        room.getTranscriptWindow().addComponent(request);

                        final GameOffer offer = new GameOffer();
                        offer.setTo(opponentJID);

                        // Add a listener for a reply to our offer.
                        SparkManager.getConnection().addPacketListener(new PacketListener() {
                            public void processPacket(Packet packet) {
                                GameOffer offerReply = ((GameOffer)packet);

                                if (offerReply.getType() == IQ.Type.RESULT) {
                                    // Remove the offer panel
                                    room.getTranscriptWindow().remove(request);
                                    // Show game board (using original offer!).
                                    showReversiBoard(offer.getGameID(), room, offer.isStartingPlayer(),
                                            offerReply.getFrom());
                                }
                                else {
                                    // TODO: show they declined
                                }
                                // TODO: Handle error case
                            }
                        }, new PacketIDFilter(offer.getPacketID()));

                        SparkManager.getConnection().sendPacket(offer);
                    }
                });

            }

            public void chatRoomClosed(ChatRoom room) {
                super.chatRoomClosed(room);
                // TODO: if game is in progress, close it down. What we need is an API that lets us see
                // TODO: if there's a transcript alert currently there.
            }
        };
        manager.addChatRoomListener(chatRoomListener);
    }

    /**
     * Displays the game board. This is called after an offer has been accepted or after accepting an
     * invitation.
     *
     * @param gameID the game ID.
     * @param room the chat room to display the game board in.
     * @param startingPlayer true if this player is the starting player (black).
     * @param opponentJID the opponent's JID.
     */
    private void showReversiBoard(int gameID, ChatRoom room, boolean startingPlayer, String opponentJID) {
        JSplitPane pane = room.getSplitPane();
        pane.setResizeWeight(1.0);
        BackgroundPanel reversiBackground = new BackgroundPanel();
        reversiBackground.setLayout(new BorderLayout());
        reversiBackground.setOpaque(false);
        ReversiPanel reversi = new ReversiPanel(SparkManager.getConnection(), gameID,
                startingPlayer, opponentJID);
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