/**
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

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.XmlEnvironment;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smack.xml.XmlPullParser;
import org.jivesoftware.smack.xml.XmlPullParserException;

import javax.xml.namespace.QName;
import java.io.IOException;

/**
 * The Move Packet extension
 * 
 * @author wolf.posdorfer
 * @version 16.06.2011
 */
public class MovePacket implements ExtensionElement {

    public static final String ELEMENT_NAME = "ttt-move";
    public static final String NAMESPACE = "tictactoe";
    public static final QName QNAME = new QName(NAMESPACE, ELEMENT_NAME);

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
    public String toXML(XmlEnvironment xmlEnvironment) {
        return "<" + ELEMENT_NAME + " xmlns=\"" + NAMESPACE + "\">"
            + "<gameID>" + gameID + "</gameID>"
            + "<positionX>" + posx + "</positionX>"
            + "<positionY>" + posy + "</positionY>"
            + "</" + ELEMENT_NAME + ">";
    }

    public static class Provider extends ExtensionElementProvider<MovePacket>
    {
        public MovePacket parse(XmlPullParser parser, int initialDepth, XmlEnvironment xmlEnvironment) throws XmlPullParserException, IOException
        {
            final MovePacket gameMove = new MovePacket();
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
