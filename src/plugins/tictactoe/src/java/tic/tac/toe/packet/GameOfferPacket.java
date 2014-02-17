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
package tic.tac.toe.packet;

import java.util.Random;

import org.jivesoftware.smack.packet.IQ;

/**
 * The Game Offer Packet
 * 
 * @author wolf.posdorfer
 * @version 16.06.2011
 */
public class GameOfferPacket extends IQ {

    public static final String ELEMENT_NAME = "tictactoe";
    public static final String NAMESPACE = "tictactoe";

    private int gameID;
    private boolean imTheStartingPlayer;

    public GameOfferPacket() {
	super();
	imTheStartingPlayer = new Random().nextBoolean();
	gameID = Math.abs(new Random().nextInt());
    }

    /**
     * Returns the game ID.
     * 
     * @return the game ID.
     */
    public int getGameID() {
	return gameID;
    }

    /**
     * Sets the game ID.
     * 
     * @param gameID
     *            the game ID.
     */
    public void setGameID(int gameID) {
	this.gameID = gameID;
    }

    /**
     * Returns true if the user making the game invitation is the starting
     * player.
     * 
     * @return true if the user making the game invite is the starting player.
     */
    public boolean isStartingPlayer() {
	return imTheStartingPlayer;
    }

    /**
     * Sets whether the user making the game invitation is the starting player.
     * 
     * @param startingPlayer
     *            true if the user making the game invite is the starting
     *            player.
     */
    public void setStartingPlayer(boolean startingPlayer) {
	this.imTheStartingPlayer = startingPlayer;
    }

    public String getChildElementXML() {
	StringBuffer buf = new StringBuffer();
	buf.append("<" + ELEMENT_NAME + " xmlns=\"" + NAMESPACE + "\">");
	if (getType() == IQ.Type.GET) {
	    buf.append("<gameID>").append(gameID).append("</gameID>");
	    buf.append("<startingPlayer>").append(imTheStartingPlayer)
		    .append("</startingPlayer>");
	    buf.append(getExtensionsXML());
	}
	buf.append("</" + ELEMENT_NAME + ">");
	return buf.toString();
    }

}
