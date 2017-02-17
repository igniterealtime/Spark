/**
 * Copyright (C) 2004-2011 Jive Software. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jivesoftware.game.reversi;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Random;

/**
 * An IQ packet that's an offer to start a new Reversi game. The offer indicates whether the player
 * making the offer will be the starting player (black). The starting player is selected randomly
 * by default, which is recommended.
 *
 * @author Matt Tucker
 */
public class GameOffer extends IQ
{
    public static final String ELEMENT_NAME = "reversi";
    public static final String NAMESPACE = "http://jivesoftware.org/protocol/game/reversi";

    private static Random random = new Random();
    private int gameID;

    private boolean startingPlayer;

    /**
     * Constructs a new game offer with a random game ID and random value for the starting player.
     */
    public GameOffer()
    {
        super( ELEMENT_NAME, NAMESPACE );
        // Randomly choose if the user making the game offer will be the starting player (black).
        startingPlayer = random.nextBoolean();
        gameID = Math.abs( random.nextInt() );
    }

    /**
     * Returns the game ID.
     *
     * @return the game ID.
     */
    public int getGameID()
    {
        return gameID;
    }

    /**
     * Sets the game ID.
     *
     * @param gameID the game ID.
     */
    public void setGameID( int gameID )
    {
        this.gameID = gameID;
    }

    /**
     * Returns true if the user making the game invitation is the starting player.
     *
     * @return true if the user making the game invite is the starting player.
     */
    public boolean isStartingPlayer()
    {
        return startingPlayer;
    }

    /**
     * Sets whether the user making the game invitation is the starting player.
     *
     * @param startingPlayer true if the user making the game invite is the starting player.
     */
    public void setStartingPlayer( boolean startingPlayer )
    {
        this.startingPlayer = startingPlayer;
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder( IQChildElementXmlStringBuilder buf )
    {
        buf.rightAngleBracket();
        buf.append( "<" + ELEMENT_NAME + " xmlns=\"" + NAMESPACE + "\">" );
        buf.element( "gameID", Integer.toString( gameID ) );
        buf.element( "startingPlayer", Boolean.toString( startingPlayer ) );
        buf.append( "</" + ELEMENT_NAME + ">" );
        return buf;
    }

    public static class Provider extends IQProvider<GameOffer>
    {
        public Provider()
        {
            super();
        }

        public GameOffer parse( XmlPullParser parser, int i ) throws XmlPullParserException, IOException
        {
            final GameOffer gameOffer = new GameOffer();

            boolean done = false;
            while ( !done )
            {
                int eventType = parser.next();
                if ( eventType == XmlPullParser.START_TAG )
                {
                    if ( parser.getName().equals( "gameID" ) )
                    {
                        final int gameID = Integer.valueOf( parser.nextText() );
                        gameOffer.setGameID( gameID );
                    }
                    else if ( parser.getName().equals( "startingPlayer" ) )
                    {
                        boolean startingPlayer = Boolean.valueOf( parser.nextText() );
                        gameOffer.setStartingPlayer( startingPlayer );
                    }
                }

                else if ( eventType == XmlPullParser.END_TAG )
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
