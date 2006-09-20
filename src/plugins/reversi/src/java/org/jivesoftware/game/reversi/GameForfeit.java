/**
 * $Revision$
 * $Date$
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 * This software is the proprietary information of Jive Software. Use is subject to license terms.
 */

package org.jivesoftware.game.reversi;

import org.jivesoftware.smack.packet.PacketExtension;

/**
 * A packet extension sent to indicate that the player forfeits the game.
 */
public class GameForfeit implements PacketExtension {

    public static final String ELEMENT_NAME = "reversi-forfeit";
    public static final String NAMESPACE = "http://jivesoftware.org/protocol/game/reversi";

    private int gameID;

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
        buf.append("</" + ELEMENT_NAME + ">");
        return buf.toString();
    }

    /**
     * Returns the game ID that this forfeit pertains to.
     *
     * @return the game ID.
     */
    public int getGameID() {
        return gameID;
    }

    /**
     * Sets the game ID that this forfeit pertains to.
     *
     * @param gameID the game ID.
     */
    public void setGameID(int gameID) {
        this.gameID = gameID;
    }
}