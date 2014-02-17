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
package net.java.sipmack.softphone.gui;

import net.java.sipmack.sip.InterlocutorUI;

import java.util.List;

public interface DefaultGuiManager {

    /**
     * Dial a number.
     *
     * @param number Number to be called
     */
    public void dial(String number);

    /**
     * Hold the current call. In fact it holds all medias depending of the
     * server.
     */
    public void holdAll();

    /**
     * Hold current call of associated interlocutor. In fact it holds all medias depending of the
     * server.
     *
     * @param interlocutor interlocutor that will be holded
     */
    public void hold(InterlocutorUI interlocutor);

    /**
     * Answer the current ringing call
     */
    public boolean answer();

    /**
     * Hangup all current call
     */
    public boolean hangupAll();

    /**
     * Hangup the call associated with the informed InterlocutorUI
     *
     * @param interlocutorUI
     * @return
     */
    public boolean hangup(InterlocutorUI interlocutorUI);

    /**
     *  Send a DTMF Tone to all current calls
     *
     * @param digit DTMF digit to be sent
     */
    public void sendDTMF(String digit);

    /**
     * Mute all current calls.
     */
    public void muteAll(boolean mic);

    /**
     * Mute the current call associated with the informed interlocutor.
     *
     * @param interlocutor
     * @param mic
     */
    public void mute(InterlocutorUI interlocutor, boolean mic);

    /**
     * Get all current interlocutors.
     *
     * @return
     */
    public List<InterlocutorUI> getInterlocutors();


}
