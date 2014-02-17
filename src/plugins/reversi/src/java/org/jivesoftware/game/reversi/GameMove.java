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

import org.jivesoftware.smack.packet.PacketExtension;

/**
 * A packet extension that represents an individual game move. Each move is simply an integer
 * indicating the position on the board the user wishes to place their stone. It's assumed that
 * both players maintain their own copy of the game model and only send valid moves. If an
 * invalid move is received from the opponent, the player should immediately terminate the game.<p>
 *
 * The game board is 64 squares; see {@link ReversiModel} for full details.
 *
 * @author Matt Tucker
 */
public class GameMove implements PacketExtension {

    public static final String ELEMENT_NAME = "reversi-move";
    public static final String NAMESPACE = "http://jivesoftware.org/protocol/game/reversi";

    private int gameID;
    private int position;

    public String getElementName() {
        return ELEMENT_NAME;
    }

    public String getNamespace() {
        return NAMESPACE;
    }

    public String toXML() {
        StringBuffer buf = new StringBuffer();
        buf.append("<" + ELEMENT_NAME + " xmlns=\"" + NAMESPACE + "\">");
        buf.append("<gameID>").append(gameID).append("</gameID>");
        buf.append("<position>").append(position).append("</position>");
        buf.append("</" + ELEMENT_NAME + ">");
        return buf.toString();
    }

    /**
     * Returns the game ID that this move pertains to.
     *
     * @return the game ID.
     */
    public int getGameID() {
        return gameID;
    }

    /**
     * Sets the game ID that this move pertains to.
     *
     * @param gameID the game ID.
     */
    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    /**
     * Returns the move position; an integer between 0 and 63.
     *
     * @return the move position.
     */
    public int getPosition() {
        return position;
    }

    /**
     * Sets the move position; an integer between 0 and 63.
     *
     * @param position the move position.
     */
    public void setPosition(int position) {
        if (position < 0 || position > 63) {
            throw new IllegalArgumentException("Position " + position + " invalid; must be between 0 and 63.");
        }
        this.position = position;
    }
}
