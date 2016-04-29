package battleship.listener;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;


import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatRoomButton;
import org.jivesoftware.spark.ui.ChatRoomListenerAdapter;
import org.jivesoftware.spark.ui.rooms.ChatRoomImpl;


import battleship.BsRes;
import battleship.gui.GUI;
import battleship.packets.GameOfferPacket;

public class ChatRoomOpeningListener extends ChatRoomListenerAdapter {

    @Override
    public void chatRoomOpened(final ChatRoom room) {

	if (!(room instanceof ChatRoomImpl)) // Check for 1on1 Chat
	{
	    return;
	}

	final ChatRoomButton sendGameButton = new ChatRoomButton("BS");
	room.getToolBar().addChatRoomButton(sendGameButton);
	final String opponentJID = ((ChatRoomImpl) room).getJID();

	sendGameButton.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {

		final GameOfferPacket offer = new GameOfferPacket();
		offer.setTo(opponentJID);
		offer.setType(IQ.Type.GET);

		room.getTranscriptWindow().insertCustomText(
			BsRes.getString("request"), false, false,
			Color.BLUE);
		SparkManager.getConnection().sendStanza(offer);

		SparkManager.getConnection().addAsyncStanzaListener(
			new StanzaListener() {
			    @Override
			    public void processPacket(Stanza stanza) {

				GameOfferPacket answer = (GameOfferPacket) stanza;
				answer.setStartingPlayer(offer
					.isStartingPlayer());
				answer.setGameID(offer.getGameID());
				String name = StringUtils.parseName(opponentJID);
				if (answer.getType() == IQ.Type.RESULT) {
				    // ACCEPT

				    room.getTranscriptWindow()
					    .insertCustomText(BsRes.getString("accepted", name), false,
						    false, Color.BLUE);

				    createWindow(answer, opponentJID);
				} else {
				    // DECLINE
				    room.getTranscriptWindow()
					    .insertCustomText(BsRes.getString("declined", name), false,
						    false, Color.RED);
				}

			    }
			}, new PacketIDFilter(offer.getPacketID()));

	    }

	});

    }

    public static void createWindow(GameOfferPacket answer, String opponentJID) {

	JFrame frame = new JFrame(BsRes.getString("versus", StringUtils.parseName(opponentJID)));
	frame.add(new GUI(answer.isStartingPlayer(),frame,SparkManager.getConnection(),answer.getGameID()));
	frame.pack();
	frame.setLocationRelativeTo(SparkManager.getChatManager().getChatContainer());
	frame.setVisible(true);
	

    }
}
