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
package tic.tac.toe;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jivesoftware.resource.Res;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatRoomButton;
import org.jivesoftware.spark.ui.ChatRoomListener;
import org.jivesoftware.spark.ui.ChatRoomListenerAdapter;
import org.jivesoftware.spark.ui.rooms.ChatRoomImpl;
import tic.tac.toe.packet.GameOfferPacket;
import tic.tac.toe.packet.InvalidMove;
import tic.tac.toe.packet.MovePacket;
import tic.tac.toe.ui.GamePanel;


/**
 * Tic Tac Toe plugin for Spark
 * 
 * @author wolf.posdorfer
 * @version 16.06.2011
 * 
 */
public class TicTacToePlugin implements Plugin {

    private ChatRoomListener _chatRoomListener;
    private PacketListener _gameOfferListener;
    
    private HashSet<String> _currentInvitations;
       
    private ImageIcon buttonimg;
    
    

    @Override
    public void initialize() {
	ClassLoader cl = getClass().getClassLoader();
	buttonimg = new ImageIcon(cl.getResource("ttt.button.png"));
	_currentInvitations = new HashSet<String>();

	ProviderManager.getInstance().addIQProvider(GameOfferPacket.ELEMENT_NAME, GameOfferPacket.NAMESPACE,GameOfferPacket.class);
	ProviderManager.getInstance().addExtensionProvider(MovePacket.ELEMENT_NAME, MovePacket.NAMESPACE, MovePacket.class);
	ProviderManager.getInstance().addExtensionProvider(InvalidMove.ELEMENT_NAME, InvalidMove.NAMESPACE, InvalidMove.class);

	// Add IQ listener to listen for incoming game invitations.
	_gameOfferListener = new PacketListener() {
	    public void processPacket(Packet packet) {
		GameOfferPacket invitation = (GameOfferPacket) packet;
		if (invitation.getType() == IQ.Type.GET) {
		    showInvitationAlert(invitation);
		}
	    }

	};

	SparkManager.getConnection().addPacketListener(_gameOfferListener,
		new PacketTypeFilter(GameOfferPacket.class));

	addButtonToToolBar();

    }
    /**
     * Add the TTT-Button to every opening Chatroom
     * and create Listeners for it
     */
    private void addButtonToToolBar() {
	

	_chatRoomListener = new ChatRoomListenerAdapter() {
	    @Override
	    public void chatRoomOpened(final ChatRoom room) {
		
		if(!(room instanceof ChatRoomImpl))
		{
		    // Don't do anything if this is not a 1on1-Chat
		    return;
		}
		
		final ChatRoomButton sendGameButton = new ChatRoomButton(buttonimg);
		room.getToolBar().addChatRoomButton(sendGameButton);

		final String opponentJID = ((ChatRoomImpl) room).getJID();

		sendGameButton.addActionListener(new ActionListener() {

		    @Override
		    public void actionPerformed(ActionEvent e) {
			
			if(_currentInvitations.contains(StringUtils.parseBareAddress(opponentJID)))
			{
			    return;
			}
			
			final GameOfferPacket offer = new GameOfferPacket();
			offer.setTo(opponentJID);
			offer.setType(IQ.Type.GET );
			
			_currentInvitations.add(StringUtils.parseBareAddress(opponentJID));
			room.getTranscriptWindow().insertCustomText
			    (TTTRes.getString("ttt.request.sent"), false, false, Color.BLUE);
			SparkManager.getConnection().sendPacket(offer);
			
			SparkManager.getConnection().addPacketListener(
				new PacketListener() {
				    @Override
				    public void processPacket(Packet packet) {

					GameOfferPacket answer = (GameOfferPacket)packet;
					answer.setStartingPlayer(offer.isStartingPlayer());
					answer.setGameID(offer.getGameID());
					if (answer.getType() == IQ.Type.RESULT) {
					    // ACCEPT
					    _currentInvitations.remove(StringUtils.parseBareAddress(opponentJID));
					    
					    room.getTranscriptWindow().insertCustomText
					    (TTTRes.getString("ttt.request.accept"), false, false, Color.BLUE);
					    
					    createTTTWindow(answer, opponentJID);
					} else {
					    // DECLINE
					    room.getTranscriptWindow().insertCustomText
					    (TTTRes.getString("ttt.request.decline"), false, false, Color.RED);
					    _currentInvitations.remove(StringUtils.parseBareAddress(opponentJID));
					}

				    }
				},
				new PacketIDFilter(offer.getPacketID()));
		    }
		});

	    }

	    @Override
	    public void chatRoomClosed(ChatRoom room) {
		if (room instanceof ChatRoomImpl) {
		    ChatRoomImpl cri = (ChatRoomImpl) room;
		    _currentInvitations.remove(cri.getParticipantJID());
		}
	    }
	};
	

	SparkManager.getChatManager().addChatRoomListener(_chatRoomListener);

    }

    @Override
    public void shutdown() {

	_currentInvitations.clear();
	SparkManager.getChatManager().removeChatRoomListener(_chatRoomListener);
	SparkManager.getConnection().removePacketListener(_gameOfferListener);

    }

    @Override
    public boolean canShutDown() {
	return false;
    }

    @Override
    public void uninstall() {

    }

    /**
     * insert the Invitation Dialog into the Chat
     * 
     * @param invitation
     */
    private void showInvitationAlert(final GameOfferPacket invitation) {
	
	
	invitation.setType(IQ.Type.RESULT);
	invitation.setTo(invitation.getFrom());
	
	
	final ChatRoom room = SparkManager.getChatManager().getChatRoom(StringUtils.parseBareAddress(invitation.getFrom()));
	
	String name = StringUtils.parseName(invitation.getFrom());
	
	final JPanel panel = new JPanel();
	JLabel text = new JLabel(TTTRes.getString("ttt.game.request",name)); 
	JLabel game = new JLabel(TTTRes.getString("ttt.game.name"));
	game.setFont(new Font("Dialog", Font.BOLD, 24));
	game.setForeground(Color.RED);
	JButton accept = new JButton(Res.getString("button.accept").replace("&", ""));
	JButton decline = new JButton(Res.getString("button.decline").replace("&", ""));
	panel.add(text);
	panel.add(game);
	panel.add(accept);
	panel.add(decline);
	
	room.getTranscriptWindow().addComponent(panel);
	
	accept.addActionListener(new ActionListener() {
	    
	    @Override
	    public void actionPerformed(ActionEvent e) {
		SparkManager.getConnection().sendPacket(invitation);
		invitation.setStartingPlayer(!invitation.isStartingPlayer());
		createTTTWindow(invitation, invitation.getFrom());
		panel.remove(3);
		panel.remove(2);
		panel.repaint();
		panel.revalidate();
	    }
	});
	
	decline.addActionListener(new ActionListener() {
	    
	    @Override
	    public void actionPerformed(ActionEvent e) {
		invitation.setType(IQ.Type.ERROR);
		SparkManager.getConnection().sendPacket(invitation);
		panel.remove(3);
		panel.remove(2);
		panel.repaint();
		panel.revalidate();
	    }
	});
	
	
    }


    /**
     * Creates The TicTacToe Window and starts the Game
     * @param gop
     * @param opponentJID
     */
    private void createTTTWindow(GameOfferPacket gop, String opponentJID) {
	
	String name = StringUtils.parseName(opponentJID);
	
	// tictactoe versus ${name}
	JFrame f = new JFrame(TTTRes.getString("ttt.window.title", TTTRes.getString("ttt.game.name"),name));
	
	f.setIconImage(buttonimg.getImage());
	GamePanel gp = new GamePanel(SparkManager.getConnection(),
		gop.getGameID(), gop.isStartingPlayer(), opponentJID,f);
	f.add(gp);
	f.pack();
	f.setLocationRelativeTo(SparkManager.getChatManager().getChatContainer());
	f.setVisible(true);
	
    }

}