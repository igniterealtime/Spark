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
package org.jivesoftware.game.reversi;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.XmlEnvironment;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smack.xml.XmlPullParser;
import org.jivesoftware.smack.xml.XmlPullParserException;

import javax.xml.namespace.QName;
import java.io.IOException;

/**
 * A packet extension sent to indicate that the player forfeits the game.
 */
public class GameForfeit implements ExtensionElement {

    public static final String ELEMENT_NAME = "reversi-forfeit";
    public static final String NAMESPACE = "http://jivesoftware.org/protocol/game/reversi";
    public static final QName QNAME = new QName(NAMESPACE, ELEMENT_NAME);

    private int gameID;

    public String getElementName() {
        return ELEMENT_NAME;
    }

    public String getNamespace() {
        return NAMESPACE;
    }

    @Override
    public String toXML(XmlEnvironment xmlEnvironment) {
        return "<" + ELEMENT_NAME + " xmlns=\"" + NAMESPACE + "\">"
            + "<gameID>" + gameID
            + "</gameID>"
            + "</" + ELEMENT_NAME + ">";
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


    public static class Provider extends ExtensionElementProvider<GameForfeit>
    {
        public GameForfeit parse(XmlPullParser parser, int initialDepth, XmlEnvironment xmlEnvironment) throws XmlPullParserException, IOException
        {
            final GameForfeit gameForfeit = new GameForfeit();
            boolean done = false;
            while ( !done )
            {
                final XmlPullParser.Event eventType = parser.next();

                if ( eventType == XmlPullParser.Event.START_ELEMENT )
                {
                    if ( "gameID".equals( parser.getName() ) )
                    {
                        final int gameID = Integer.parseInt( parser.nextText() );
                        gameForfeit.setGameID( gameID );
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

            return gameForfeit;
        }
    }
}
