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
package org.jivesoftware.spark.plugin.battleship.packets;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.XmlEnvironment;

import javax.xml.namespace.QName;

/**
 * The Move Packet extension
 *
 * @author wolf.posdorfer
 */
public class MovePacket implements ExtensionElement {

    public static final String ELEMENT_NAME = "bs-move";
    public static final String NAMESPACE = "battleship";
    public static final QName QNAME = new QName(NAMESPACE, ELEMENT_NAME);

    private int posX;
    private int posY;
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
        return posX;
    }

    public void setPositionX(int posX) {
        this.posX = posX;
    }

    public int getPositionY() {
        return posY;
    }

    public void setPositionY(int posY) {
        this.posY = posY;
    }

    @Override
    public CharSequence toXML(XmlEnvironment xmlEnvironment) {
        String buf = "<" + ELEMENT_NAME + " xmlns=\"" + NAMESPACE + "\">" +
            "<gameID>" + gameID + "</gameID>" +
            "<positionX>" + posX + "</positionX>" +
            "<positionY>" + posY + "</positionY>" +
            "</" + ELEMENT_NAME + ">";
        return buf;
    }

}
