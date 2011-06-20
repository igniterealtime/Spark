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

import org.jivesoftware.smack.packet.PacketExtension;

/**
 * The Move Packet extension
 * 
 * @author wolf.posdorfer
 * @version 20.06.2011
 */
public class MovePacket implements PacketExtension {

    public static final String ELEMENT_NAME = "bs-move";
    public static final String NAMESPACE = "battleship";

    private int posx;
    private int posy;
    private int gameID;

    public int getGameID() {
	return gameID;
    }

    public void setGameID(int gameID) {
	this.gameID = gameID;
    }

    @Override
    public String getElementName() {
	return ELEMENT_NAME;
    }

    @Override
    public String getNamespace() {
	return NAMESPACE;
    }

    public int getPositionX() {
	return posx;
    }

    public void setPositionX(int posx) {
	this.posx = posx;
    }

    public int getPositionY() {
	return posy;
    }

    public void setPositionY(int posy) {
	this.posy = posy;
    }

    @Override
    public String toXML() {

	StringBuffer buf = new StringBuffer();
	buf.append("<" + ELEMENT_NAME + " xmlns=\"" + NAMESPACE + "\">");

	buf.append("<gameID>").append(gameID).append("</gameID>");

	buf.append("<positionX>").append(posx).append("</positionX>");

	buf.append("<positionY>").append(posy).append("</positionY>");

	buf.append("</" + ELEMENT_NAME + ">");
	return buf.toString();

    }

}
