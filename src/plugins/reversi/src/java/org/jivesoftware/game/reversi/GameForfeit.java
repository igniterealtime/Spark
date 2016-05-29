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

import org.jivesoftware.smack.packet.ExtensionElement;

/**
 * A packet extension sent to indicate that the player forfeits the game.
 */
public class GameForfeit implements ExtensionElement {

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