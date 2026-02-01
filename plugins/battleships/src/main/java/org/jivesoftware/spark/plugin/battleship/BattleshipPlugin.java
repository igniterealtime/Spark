package org.jivesoftware.spark.plugin.battleship;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jivesoftware.resource.Res;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.ui.ChatRoom;

import org.jivesoftware.spark.plugin.battleship.listener.ChatRoomOpeningListener;
import org.jivesoftware.spark.plugin.battleship.packets.GameOfferPacket;
import org.jivesoftware.spark.plugin.battleship.packets.MoveAnswerPacket;
import org.jivesoftware.spark.plugin.battleship.packets.MovePacket;
import org.jivesoftware.spark.util.log.Log;

public class BattleshipPlugin implements Plugin {

    @Override
    public void initialize() {
        ProviderManager.addIQProvider(GameOfferPacket.ELEMENT_NAME, GameOfferPacket.NAMESPACE, GameOfferPacket.class);
        ProviderManager.addExtensionProvider(MovePacket.ELEMENT_NAME, MovePacket.NAMESPACE, MovePacket.class);
        ProviderManager.addExtensionProvider(MoveAnswerPacket.ELEMENT_NAME, MoveAnswerPacket.NAMESPACE, MoveAnswerPacket.class);

        StanzaListener _gameOfferListener = stanza -> {
            GameOfferPacket invitation = (GameOfferPacket) stanza;
            if (invitation.getType() == IQ.Type.get) {
                showInvitationInChat(invitation);
            }
        };

        SparkManager.getConnection().addAsyncStanzaListener(_gameOfferListener,
            new StanzaTypeFilter(GameOfferPacket.class));
        ChatRoomOpeningListener _chatRoomListener = new ChatRoomOpeningListener();
        SparkManager.getChatManager().addChatRoomListener(_chatRoomListener);
    }

    private void showInvitationInChat(final GameOfferPacket invitation) {
        invitation.setType(IQ.Type.result);
        invitation.setTo(invitation.getFrom());

        final ChatRoom room = SparkManager.getChatManager().getChatRoom(invitation.getFrom().asEntityBareJidIfPossible());
        String name = invitation.getFrom().getLocalpartOrNull().toString();
        final JPanel panel = new JPanel();
        JLabel text = new JLabel("Game request from" + name);
        JLabel game = new JLabel("Battleships");
        game.setFont(new Font("Dialog", Font.BOLD, 24));
        game.setForeground(Color.RED);
        JButton accept = new JButton(Res.getString("button.accept").replace("&", ""));
        JButton decline = new JButton(Res.getString("button.decline").replace("&", ""));
        panel.add(text);
        panel.add(game);
        panel.add(accept);
        panel.add(decline);
        room.getTranscriptWindow().addComponent(panel);

        accept.addActionListener(e -> {
            try {
                SparkManager.getConnection().sendStanza(invitation);
            } catch (SmackException.NotConnectedException | InterruptedException e1) {
                Log.warning("Unable to send invitation accept to " + invitation.getTo(), e1);
            }
            invitation.setStartingPlayer(!invitation.isStartingPlayer());
            ChatRoomOpeningListener.createWindow(invitation, invitation.getFrom().toString());
            panel.remove(3);
            panel.remove(2);
            panel.repaint();
            panel.revalidate();
        });

        decline.addActionListener(e -> {
            invitation.setType(IQ.Type.error);
            try {
                SparkManager.getConnection().sendStanza(invitation);
            } catch (SmackException.NotConnectedException | InterruptedException e1) {
                Log.warning("Unable to send invitation decline to " + invitation.getTo(), e1);
            }
            panel.remove(3);
            panel.remove(2);
            panel.repaint();
            panel.revalidate();
        });
    }

    @Override
    public void shutdown() {
    }

    @Override
    public boolean canShutDown() {
        return false;
    }

    @Override
    public void uninstall() {
    }

}
