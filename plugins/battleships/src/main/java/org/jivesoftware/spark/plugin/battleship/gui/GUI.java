/**
 * Copyright (C) 2004-2011 Jive Software. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jivesoftware.spark.plugin.battleship.gui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.StanzaExtensionFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.StanzaBuilder;

import org.jivesoftware.spark.plugin.battleship.listener.ShipPlacementListener;
import org.jivesoftware.spark.plugin.battleship.logic.GameBoard;

import java.util.Observable;
import java.util.Observer;

import org.jivesoftware.spark.plugin.battleship.packets.MoveAnswerPacket;
import org.jivesoftware.spark.plugin.battleship.packets.MovePacket;
import org.jxmpp.jid.Jid;

public class GUI extends JPanel implements Observer {

    private static final long serialVersionUID = -7538765009749015196L;

    private final Display _display;
    private final GameboardGUI _myField;
    private final GameboardGUI _theirField;
    private final JFrame _owner;
    private final XMPPConnection _connection;
    private final int _gameID;
    private final GameBoard _gameboard;

    private final ShipPlacementListener _spListener;

    public GUI(boolean imStarting, JFrame owner, XMPPConnection connection, int gameID) {
        setLayout(new BorderLayout());
        _owner = owner;
        _connection = connection;
        _gameID = gameID;
        _gameboard = new GameBoard(imStarting);

        _display = new Display();
        _myField = new GameboardGUI();
        _theirField = new GameboardGUI();

        JPanel West = new JPanel(new BorderLayout(0, 5));
        JPanel wNorth = new JPanel(new BorderLayout());
        wNorth.add(new JLabel("Their Board"), BorderLayout.NORTH);
        wNorth.add(_theirField, BorderLayout.SOUTH);

        JPanel wSouth = new JPanel(new BorderLayout());
        wSouth.add(new JLabel("My Board"), BorderLayout.NORTH);
        wSouth.add(_myField);

        JPanel East = new JPanel();
        West.add(wSouth, BorderLayout.SOUTH);
        West.add(wNorth, BorderLayout.NORTH);
        East.add(_display);

        add(East, BorderLayout.EAST);
        add(West, BorderLayout.WEST);

        _connection.addAsyncStanzaListener(stanza -> {
            MovePacket move = stanza.getExtension(MovePacket.class);
            if (move.getGameID() == _gameID) {
                boolean opponentMadeHit = _gameboard.placeBomb(move.getPositionX(), move.getPositionY());
                if (opponentMadeHit) {
                    Message m = createAnswer(move, stanza.getFrom());
                    _connection.sendStanza(m);
                }
            }
        }, new StanzaExtensionFilter(MovePacket.ELEMENT_NAME, MovePacket.NAMESPACE));

        // Start placing of the Ships
        _spListener = new ShipPlacementListener(_display, _gameboard, _myField);
        _spListener.addObserver(this);

        _myField.initiateShipPlacement(_spListener);
    }

    private Message createAnswer(MovePacket incoming, Jid from) {
        Message answer = StanzaBuilder.buildMessage().build();
        answer.setTo(from);

        MoveAnswerPacket map = new MoveAnswerPacket();
        map.setGameID(incoming.getGameID());
        map.setPositionX(incoming.getPositionX());
        map.setPositionY(incoming.getPositionY());
        return answer;
    }

    @Override
    public void update(Observable observable, Object arg) {
        //TODO Initial ship placement is done
        // Start with normal moves
        observable.deleteObserver(this);
    }
}
