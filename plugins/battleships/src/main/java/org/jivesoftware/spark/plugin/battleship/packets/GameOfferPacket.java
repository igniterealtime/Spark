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

import java.io.IOException;
import java.text.ParseException;
import java.util.Random;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IqData;
import org.jivesoftware.smack.packet.XmlEnvironment;
import org.jivesoftware.smack.parsing.SmackParsingException;
import org.jivesoftware.smack.provider.IqProvider;
import org.jivesoftware.smack.xml.XmlPullParser;
import org.jivesoftware.smack.xml.XmlPullParserException;
import org.jxmpp.JxmppContext;

/**
 * The Game Offer Packet
 *
 * @author wolf.posdorfer
 */
public class GameOfferPacket extends IQ {
    public static final String ELEMENT_NAME = "battleship";
    public static final String NAMESPACE = "battleship";

    private int gameID;
    private boolean imTheStartingPlayer;

    public GameOfferPacket() {
        super(ELEMENT_NAME, NAMESPACE);
        imTheStartingPlayer = new Random().nextBoolean();
        gameID = Math.abs(new Random().nextInt());
    }

    /**
     * Returns the game ID.
     */
    public int getGameID() {
        return gameID;
    }

    /**
     * Sets the game ID.
     */
    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    /**
     * Returns true if the user making the game invitation is the starting player.
     */
    public boolean isStartingPlayer() {
        return imTheStartingPlayer;
    }

    /**
     * Sets whether the user making the game invitation is the starting player.
     *
     * @param startingPlayer true if the user making the game invite is the starting player.
     */
    public void setStartingPlayer(boolean startingPlayer) {
        this.imTheStartingPlayer = startingPlayer;
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder buf) {
        buf.rightAngleBracket();
        buf.append("<" + ELEMENT_NAME + " xmlns=\"" + NAMESPACE + "\">");
        if (getType() == IQ.Type.get) {
            buf.element("gameID", String.valueOf(gameID));
            buf.element("startingPlayer", String.valueOf(imTheStartingPlayer));
            buf.append(getExtensions());
        }
        buf.append("</" + ELEMENT_NAME + ">");
        return buf;
    }

    public static class Provider extends IqProvider<GameOfferPacket> {
        public Provider() {
            super();
        }

        @Override
        public GameOfferPacket parse(XmlPullParser parser, int initialDepth, IqData iqData, XmlEnvironment xmlEnvironment, JxmppContext jxmppContext) throws XmlPullParserException, IOException, SmackParsingException, ParseException {
            final GameOfferPacket gameOffer = new GameOfferPacket();

            boolean done = false;
            while (!done) {
                XmlPullParser.Event eventType = parser.next();
                if (eventType == XmlPullParser.Event.START_ELEMENT) {
                    if (parser.getName().equals("gameID")) {
                        final int gameID = Integer.parseInt(parser.nextText());
                        gameOffer.setGameID(gameID);
                    } else if (parser.getName().equals("startingPlayer")) {
                        boolean startingPlayer = Boolean.parseBoolean(parser.nextText());
                        gameOffer.setStartingPlayer(startingPlayer);
                    }
                } else if (eventType == XmlPullParser.Event.END_ELEMENT) {
                    if (parser.getName().equals(ELEMENT_NAME)) {
                        done = true;
                    }
                }
            }

            return gameOffer;
        }
    }
}
