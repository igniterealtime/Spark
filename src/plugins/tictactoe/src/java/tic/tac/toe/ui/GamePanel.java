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
package tic.tac.toe.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.PacketExtensionFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ShakeWindow;
import org.jivesoftware.spark.util.log.Log;
import org.jxmpp.util.XmppStringUtils;
import tic.tac.toe.GameBoard;
import tic.tac.toe.Mark;
import tic.tac.toe.TTTRes;
import tic.tac.toe.packet.InvalidMove;
import tic.tac.toe.packet.MovePacket;

/**
 * Holds the GameBoard and the Playerdisplay
 * 
 * @author wolf.posdorfer
 * @version 16.06.2011
 */
public class GamePanel extends JPanel {

    private static final long serialVersionUID = 5481864290352375841L;

    private GameBoardPanel _gameboardpanel;

    private PlayerDisplay _playerdisplay;

    private Mark me;

    private GameBoard _gameboard;

    private XMPPConnection _connection;

    private int _gameID;

    private String _opponent;
    private JFrame _frame;

    public GamePanel(XMPPConnection connection, final int gameID,
	    boolean imStarting, String opponentJID, JFrame frame) {
	
	_frame = frame;

	_opponent = opponentJID;

	_gameID = gameID;
	_gameboard = new GameBoard();
	_connection = connection;
	_gameboardpanel = new GameBoardPanel(this);

	if (imStarting) {
	    me = Mark.X;
	} else {
	    me = Mark.O;
	}

	_playerdisplay = new PlayerDisplay(me, opponentJID);

	setLayout(new BorderLayout());

	add(_gameboardpanel, BorderLayout.CENTER);
	add(_playerdisplay, BorderLayout.SOUTH);
	_connection.addAsyncStanzaListener(new StanzaListener() {

	    @Override
	    public void processPacket(Stanza stanza) throws SmackException.NotConnectedException
		{

		MovePacket move = (MovePacket) stanza.getExtension(
			MovePacket.ELEMENT_NAME, MovePacket.NAMESPACE);

		if (move.getGameID() == _gameID) {
		    
		    if(_gameboard.isValidMove(getYourMark() ,move.getPositionX(), move.getPositionY()))
		    {
		    _gameboardpanel.placeMark(getYourMark(),
			    move.getPositionX(), move.getPositionY());
		    }
		    else
		    {
			InvalidMove inval = new InvalidMove();
			inval.setGameID(move.getGameID());
			inval.setPositionX(move.getPositionX());
			inval.setPositionY(move.getPositionY());
			Message message =new Message(_opponent);
			message.addExtension(inval);
			_connection.sendStanza(message);
			
			ChatRoom cr = SparkManager.getChatManager().getChatRoom( XmppStringUtils.parseBareJid(_opponent));
			cr.getTranscriptWindow().insertCustomText(_opponent+"seems to be cheating\n"+
				"He tried placing a wrong Move", true, false, Color.red);
			
		    }
		}

	    }
	}, new PacketExtensionFilter(MovePacket.ELEMENT_NAME,
		MovePacket.NAMESPACE));
	
	_connection.addAsyncStanzaListener(new StanzaListener() {
	    
	    @Override
	    public void processPacket(Stanza stanza) {
		
		//InvalidMove im = (InvalidMove)packet.getExtension(InvalidMove.ELEMENT_NAME, InvalidMove.NAMESPACE);
		ChatRoom cr = SparkManager.getChatManager().getChatRoom(XmppStringUtils.parseBareJid(_opponent));
		cr.getTranscriptWindow().insertCustomText("You seem to be Cheating\n"+
			"You placed a wrong Move", true, false, Color.red);
		ShakeWindow sw = new ShakeWindow(_frame);
		    sw.startRandomMovement(10);
		
	    }
	}, new PacketExtensionFilter(InvalidMove.ELEMENT_NAME,
		InvalidMove.NAMESPACE));

    }

    public PlayerDisplay getPlayerDisplay() {
	return _playerdisplay;
    }

    public GameBoardPanel getGameBoardPanel() {
	return _gameboardpanel;
    }

    /**
     * being called from GameBoardPanel, places the Mark on the Logical Board
     * and sends the Move if it was one
     * 
     * @param m
     * @param x
     * @param y
     */
    public void onGameBoardPlaceMark(Mark m, int x, int y) {

	_gameboard.placeMark(x, y);
	_playerdisplay.setCurrentPlayer(_gameboard.getCurrentPlayer());

	if (m == getMyMark()) {

	    MovePacket move = new MovePacket();
	    move.setGameID(_gameID);
	    move.setPositionX(x);
	    move.setPositionY(y);

	    Message message = new Message(_opponent);
	    message.addExtension(move);

		try
		{
			_connection.sendStanza(message);
		}
		catch ( SmackException.NotConnectedException e )
		{
			Log.warning( "Unable to send move to " + message.getTo(), e );
		}

	}

	if (_gameboard.isGameFinished()) {
	   _gameboardpanel.colorizeWinners(_gameboard.getWinningPositions());
	    
	    if (_gameboard.getWinner() == getMyMark().getValue()) {
		
		remove(_playerdisplay);
		add(new GameEndsUI(TTTRes.getString("ttt.win"), Color.GREEN), BorderLayout.SOUTH);
		
	    }
	    if (_gameboard.getWinner() == getYourMark().getValue()) {
		remove(_playerdisplay);
		add(new GameEndsUI(TTTRes.getString("ttt.lose"), Color.RED), BorderLayout.SOUTH);
	    }
	    if (_gameboard.getWinner() == -1) {
		remove(_playerdisplay);
		add(new GameEndsUI(TTTRes.getString("ttt.tie"), Color.BLACK), BorderLayout.SOUTH);
	    }

	  
	    ShakeWindow sw = new ShakeWindow(_frame);
	    sw.startShake();
	    repaint();
	    revalidate();

	}
    }

    public Mark getMyMark() {
	return me;
    }

    public Mark getYourMark() {
	if (me == Mark.X)
	    return Mark.O;
	else
	    return Mark.X;
    }

    public boolean myTurn() {
	return _playerdisplay.getCurrentPlayer() == getMyMark()
		&& !_gameboard.isGameFinished();
    }

    public boolean isFree(int x, int y) {
	return _gameboard.getMarkAtPos(x, y) == Mark.BLANK;
    }

}
