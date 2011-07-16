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
package battleship.gui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.PacketExtensionFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

import battleship.listener.ShipPlacementListener;
import battleship.logic.GameBoard;
import battleship.observer.Observeable;
import battleship.observer.Observer;
import battleship.packets.MoveAnswerPacket;
import battleship.packets.MovePacket;

public class GUI extends JPanel implements Observer{

    private static final long serialVersionUID = -7538765009749015196L;

    private Display _display;
    private GameboardGUI _myfield;
    private GameboardGUI _theirfield;
    private JFrame _owner;
    private XMPPConnection _connection;
    private int _gameID;
    private GameBoard _gameboard;
    
    private ShipPlacementListener _splistener;

    public GUI(boolean imStarting, JFrame owner, XMPPConnection connection, int gameID) {
	setLayout(new BorderLayout());
	_owner = owner;
	_connection = connection;
	_gameID = gameID;
	_gameboard = new GameBoard(imStarting);

	_display = new Display();
	_myfield = new GameboardGUI();
	_theirfield = new GameboardGUI();

	JPanel West = new JPanel(new BorderLayout(0, 5));
	JPanel wnorth = new JPanel(new BorderLayout());
	wnorth.add(new JLabel("Their Board"), BorderLayout.NORTH);
	wnorth.add(_theirfield, BorderLayout.SOUTH);

	JPanel wsouth = new JPanel(new BorderLayout());
	wsouth.add(new JLabel("My Board"), BorderLayout.NORTH);
	wsouth.add(_myfield);

	JPanel East = new JPanel();
	West.add(wsouth, BorderLayout.SOUTH);
	West.add(wnorth, BorderLayout.NORTH);
	East.add(_display);

	add(East, BorderLayout.EAST);
	add(West, BorderLayout.WEST);
	
	_connection.addPacketListener(new PacketListener() {

	    @Override
	    public void processPacket(Packet packet) {

		MovePacket move = (MovePacket) packet.getExtension(
			MovePacket.ELEMENT_NAME, MovePacket.NAMESPACE);

		if (move.getGameID() == _gameID) {
		    boolean opponentMadeHit = _gameboard.placeBomb(move.getPositionX(), move.getPositionY());
		    
		    if(opponentMadeHit)
		    {
			Message m = createAnswer(move, packet.getFrom());
			_connection.sendPacket(m);
		    }
		}

	    }
	}, new PacketExtensionFilter(MovePacket.ELEMENT_NAME,
		MovePacket.NAMESPACE));

	// Start placing of the Ships
        _splistener = new ShipPlacementListener(_display, _gameboard, _myfield);
        _splistener.addObserver(this);
        
        _myfield.initiateShipPlacement(_splistener);
	
    }
    
    
    private Message createAnswer(MovePacket incoming, String from)
    {
	Message answer = new Message();
	answer.setTo(from);
	
	MoveAnswerPacket map = new MoveAnswerPacket();
	map.setGameID(incoming.getGameID());
	map.setPositionX(incoming.getPositionX());
	map.setPositionY(incoming.getPositionY());
	
	
	return answer;
    }


    @Override
    public void update(Observeable obs) {
        //TODO Initial ship placement is done
        // Start with normal moves
       obs.removeObserver(this);
        
    }

}
