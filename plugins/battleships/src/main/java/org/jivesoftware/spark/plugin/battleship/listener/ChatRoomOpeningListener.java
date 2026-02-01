package org.jivesoftware.spark.plugin.battleship.listener;

import java.awt.Color;

import javax.swing.JFrame;


import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.filter.StanzaIdFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatRoomButton;
import org.jivesoftware.spark.ui.ChatRoomListenerAdapter;
import org.jivesoftware.spark.ui.rooms.ChatRoomImpl;

import org.jivesoftware.spark.plugin.battleship.BsRes;
import org.jivesoftware.spark.plugin.battleship.gui.GUI;
import org.jivesoftware.spark.plugin.battleship.packets.GameOfferPacket;
import org.jivesoftware.spark.util.log.Log;
import org.jxmpp.jid.Jid;
import org.jxmpp.util.XmppStringUtils;

public class ChatRoomOpeningListener extends ChatRoomListenerAdapter {

    @Override
    public void chatRoomOpened(final ChatRoom room) {
        // Check for 1on1 Chat
        if (!(room instanceof ChatRoomImpl)) {
            return;
        }

        final ChatRoomButton sendGameButton = new ChatRoomButton("BS");
        room.getToolBar().addChatRoomButton(sendGameButton);
        final Jid opponentJID = ((ChatRoomImpl) room).getJID();

        sendGameButton.addActionListener(e -> {
            final GameOfferPacket offer = new GameOfferPacket();
            offer.setTo(opponentJID);
            offer.setType(IQ.Type.get);

            room.getTranscriptWindow().insertCustomText(
                BsRes.getString("request"), false, false,
                Color.BLUE);
            try {
                SparkManager.getConnection().sendStanza(offer);
            } catch (SmackException.NotConnectedException | InterruptedException e1) {
                Log.warning("Unable to send offer to " + opponentJID, e1);
            }

            SparkManager.getConnection().addAsyncStanzaListener(stanza -> {
                GameOfferPacket answer = (GameOfferPacket) stanza;
                answer.setStartingPlayer(offer.isStartingPlayer());
                answer.setGameID(offer.getGameID());
                String name = opponentJID.getLocalpartOrNull().toString();
                if (answer.getType() == IQ.Type.result) {
                    // ACCEPT
                    room.getTranscriptWindow()
                        .insertCustomText(BsRes.getString("accepted", name), false,
                            false, Color.BLUE);
                    createWindow(answer, opponentJID.toString());
                } else {
                    // DECLINE
                    room.getTranscriptWindow()
                        .insertCustomText(BsRes.getString("declined", name), false,
                            false, Color.RED);
                }
            }, new StanzaIdFilter(offer.getStanzaId()));
        });

    }

    public static void createWindow(GameOfferPacket answer, String opponentJID) {
        JFrame frame = new JFrame(BsRes.getString("versus", XmppStringUtils.parseLocalpart(opponentJID)));
        frame.add(new GUI(answer.isStartingPlayer(), frame, SparkManager.getConnection(), answer.getGameID()));
        frame.pack();
        frame.setLocationRelativeTo(SparkManager.getChatManager().getChatContainer());
        frame.setVisible(true);
    }
}
