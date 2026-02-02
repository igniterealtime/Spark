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
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smack.xml.XmlPullParser;
import org.jivesoftware.smack.xml.XmlPullParserException;
import org.jxmpp.JxmppContext;

import javax.xml.namespace.QName;
import java.io.IOException;

/**
 * The MoveAnswer Packet Extension
 *
 * @author wolf.posdorfer
 */
public class MoveAnswerPacket implements ExtensionElement {

    public static final String ELEMENT_NAME = "bs-move";
    public static final String NAMESPACE = "battleship";
    public static final QName QNAME = new QName(NAMESPACE, ELEMENT_NAME);

    private int posX;
    private int posY;
    private int gameID;
    private int hit;
    private int shipType;

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

    public void setPositionX(int posx) {
        this.posX = posx;
    }

    public int getPositionY() {
        return posY;
    }

    public void setPositionY(int posy) {
        this.posY = posy;
    }

    public int getHit() {
        return hit;
    }

    public void setHit(int hit) {
        this.hit = hit;
    }

    public void setShipType(int x) {
        this.shipType = x;
    }

    public int getShipType() {
        return shipType;
    }

    @Override
    public CharSequence toXML(XmlEnvironment xmlEnvironment) {
        String buf = "<" + ELEMENT_NAME + " xmlns=\"" + NAMESPACE + "\">" +
            "<gameID>" + gameID + "</gameID>" +
            "<positionX>" + posX + "</positionX>" +
            "<positionY>" + posY + "</positionY>" +
            "<hit>" + hit + "</hit>" +
            "<shiptype>" + shipType + "</shiptype>" +
            "</" + ELEMENT_NAME + ">";
        return buf;
    }

    public static class Provider extends ExtensionElementProvider<MoveAnswerPacket>
    {
        @Override
        public MoveAnswerPacket parse(XmlPullParser parser, int initialDepth, XmlEnvironment xmlEnvironment, JxmppContext jxmppContext) throws XmlPullParserException, IOException
        {
            final MoveAnswerPacket gameMove = new MoveAnswerPacket();
            boolean done = false;
            while ( !done )
            {
                final XmlPullParser.Event eventType = parser.next();

                if ( eventType == XmlPullParser.Event.START_ELEMENT )
                {
                    if ( "gameID".equals( parser.getName() ) )
                    {
                        final int gameID = Integer.parseInt( parser.nextText() );
                        gameMove.setGameID( gameID );
                    }
                    if ( "positionX".equals( parser.getName() ) )
                    {
                        final int position = Integer.parseInt( parser.nextText() );
                        gameMove.setPositionX( position );
                    }
                    if ( "positionY".equals( parser.getName() ) )
                    {
                        final int position = Integer.parseInt( parser.nextText() );
                        gameMove.setPositionY( position );
                    }
                    if ( "hit".equals( parser.getName() ) )
                    {
                        final int hit = Integer.parseInt( parser.nextText() );
                        gameMove.setHit( hit );
                    }
                    if ( "shiptype".equals( parser.getName() ) )
                    {
                        final int shipType = Integer.parseInt( parser.nextText() );
                        gameMove.setShipType( shipType );
                    }
                }
                else if ( eventType == XmlPullParser.Event.END_ELEMENT )
                {
                    if ( ELEMENT_NAME.equals( parser.getName() ) )
                    {
                        done = true;
                    }
                }
            }

            return gameMove;
        }
    }
}
