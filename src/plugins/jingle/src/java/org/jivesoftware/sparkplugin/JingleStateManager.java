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
package org.jivesoftware.sparkplugin;

import org.jivesoftware.spark.ui.ChatRoom;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class JingleStateManager {
    private static JingleStateManager singleton;
    private static final Object LOCK = new Object();


    private Map<ChatRoom, JingleRoomState> jingleRooms = new HashMap<>();

    /**
     * Type of states a jingle call can be in.
     */
    public static enum JingleRoomState {
        /**
         * The room contains a jingle session.
         */
        inJingleCall,

        /**
         * The rooms contained a jingle session, but the call was ended.
         */
        callWasEnded,

        /**
         * The room contains an incoming jingle session.
         */
        ringing,

        /**
         * The jingle session is muted.
         */
        muted
    }

    /**
     * Returns the singleton instance of <CODE>JingleManager</CODE>,
     * creating it if necessary.
     * <p/>
     *
     * @return the singleton instance of <Code>JingleManager</CODE>
     */
    public static JingleStateManager getInstance() {
        // Synchronize on LOCK to ensure that we don't end up creating
        // two singletons.
        synchronized (LOCK) {
            if (null == singleton) {
                JingleStateManager controller = new JingleStateManager();
                singleton = controller;
                return controller;
            }
        }
        return singleton;
    }

    private JingleStateManager() {

    }

    /**
     * Adds a new JingleRoomState.
     *
     * @param room  the room the jingle session is taking place.
     * @param state the state of the jingle call.
     */
    public void addJingleSession(ChatRoom room, JingleRoomState state) {
        jingleRooms.put(room, state);
    }

    /**
     * Removes a JingleRoomState.
     *
     * @param room the room the jingle session was taking place.
     */
    public void removeJingleSession(ChatRoom room) {
        jingleRooms.remove(room);
    }

    /**
     * Returns the jingle state of a <code>ChatRoom</code>. If no jingle session is taking place,
     * this method will return null.
     *
     * @param room the <code>ChatRoom</code>.
     * @return the JingleRoomState.`
     */
    public JingleRoomState getJingleRoomState(ChatRoom room) {
        return jingleRooms.get(room);
    }

}
