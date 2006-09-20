/**
 * $Revision$
 * $Date$
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 * This software is the proprietary information of Jive Software. Use is subject to license terms.
 */

package org.jivesoftware.game.reversi;

import org.jivesoftware.smack.packet.IQ;

import java.util.Random;

/**
 * An IQ packet that's an offer to start a new Reversi game. The offer indicates whether the player
 * making the offer will be the starting player (black). The starting player is selected randomly
 * by default, which is recommended.
 *
 * @author Matt Tucker
 */
public class GameOffer extends IQ {

    public static final String ELEMENT_NAME = "reversi";
    public static final String NAMESPACE = "http://jivesoftware.org/protocol/game/reversi";

    private static Random random = new Random();
    private int gameID;

    private boolean startingPlayer;

    /**
     * Constructs a new game offer with a random game ID and random value for the starting player.
     */
    public GameOffer() {
        super();
        // Randomly choose if the user making the game offer will be the starting player (black).
        startingPlayer = random.nextBoolean();
        gameID = Math.abs(random.nextInt());
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
     * @param gameID the game ID.
     */
    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    /**
     * Returns true if the user making the game invitation is the starting player.
     *
     * @return true if the user making the game invite is the starting player.
     */
    public boolean isStartingPlayer() {
        return startingPlayer;
    }

    /**
     * Sets whether the user making the game invitation is the starting player.
     *
     * @param startingPlayer true if the user making the game invite is the starting player.
     */
    public void setStartingPlayer(boolean startingPlayer) {
        this.startingPlayer = startingPlayer;
    }

    public String getChildElementXML() {
        StringBuffer buf = new StringBuffer();
        buf.append("<" + ELEMENT_NAME + " xmlns=\"" + NAMESPACE + "\">");
        if (getType() == IQ.Type.GET) {
            buf.append("<gameID>").append(gameID).append("</gameID>");
            buf.append("<startingPlayer>").append(startingPlayer).append("</startingPlayer>");
            buf.append(getExtensionsXML());
        }
        buf.append("</" + ELEMENT_NAME + ">");
        return buf.toString();
    }
}
