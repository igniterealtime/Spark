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

import java.io.IOException;
import java.util.Random;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.XmlEnvironment;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.xml.XmlPullParser;
import org.jivesoftware.smack.xml.XmlPullParserException;

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
	super( ELEMENT_NAME, NAMESPACE );
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
        buf.append( "<" + ELEMENT_NAME + " xmlns=\"" + NAMESPACE + "\">" );
        buf.element( "gameID", Integer.toString( gameID ) );
        buf.element( "startingPlayer", Boolean.toString( imTheStartingPlayer ) );
        buf.append( "</" + ELEMENT_NAME + ">" );
        return buf;
    }
    public static class Provider extends IQProvider<GameOfferPacket>
    {
        public Provider()
        {
            super();
        }

        public GameOfferPacket parse(XmlPullParser parser, int i, XmlEnvironment xmlEnvironment) throws XmlPullParserException, IOException
        {
            final GameOfferPacket gameOffer = new GameOfferPacket();

            boolean done = false;
            while ( !done )
            {
                XmlPullParser.Event eventType = parser.next();
                if ( eventType == XmlPullParser.Event.START_ELEMENT )
                {
                    if ( parser.getName().equals( "gameID" ) )
                    {
                        final int gameID = Integer.parseInt( parser.nextText() );
                        gameOffer.setGameID( gameID );
                    }
                    else if ( parser.getName().equals( "startingPlayer" ) )
                    {
                        boolean startingPlayer = Boolean.parseBoolean( parser.nextText() );
                        gameOffer.setStartingPlayer( startingPlayer );
                    }
                }

                else if ( eventType == XmlPullParser.Event.END_ELEMENT )
                {
                    if ( parser.getName().equals( ELEMENT_NAME ) )
                    {
                        done = true;
                    }
                }
            }

            return gameOffer;
        }
    }

}
