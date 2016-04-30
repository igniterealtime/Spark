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
package battleship.packets;

import java.util.Random;

import org.jivesoftware.smack.packet.IQ;

/**
 * The Game Offer Packet
 * 
 * @author wolf.posdorfer
 * @version 20.06.2011
 */
public class GameOfferPacket extends IQ {

    public static final String ELEMENT_NAME = "battleship";
    public static final String NAMESPACE = "battleship";

    private int gameID;
    private boolean imTheStartingPlayer;

    public GameOfferPacket() {
	super(ELEMENT_NAME, NAMESPACE);
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

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder( IQChildElementXmlStringBuilder buf )
    {
        buf.rightAngleBracket();
        buf.append("<" + ELEMENT_NAME + " xmlns=\"" + NAMESPACE + "\">");
        if (getType() == IQ.Type.get) {
            buf.append("<gameID>").append(Integer.toString( gameID )).append("</gameID>");
            buf.append("<startingPlayer>").append(Boolean.toString( imTheStartingPlayer )).append("</startingPlayer>");
            buf.append(getExtensionsXML());
        }
        buf.append("</" + ELEMENT_NAME + ">");
        return buf;
    }

}
