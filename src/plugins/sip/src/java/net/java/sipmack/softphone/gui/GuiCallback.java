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

/**
 * Title: SIPark
 * Description:JAIN-SIP Audio/Video phone application
 *
 * @author Thiago Rocha Camargo (thiago@jivesoftware.com)
 *         <p/>
 *         The <code>GuiCallback</code> interface to call GUI methods
 */

public interface GuiCallback {
    /**
     * Dial a number
     *
     * @param callee Number to be called
     */
    public void dial(String callee);

    /**
     * Answer the current ringing call
     */
    public boolean answer();

    /**
     * Hangup all current calls
     */
    public boolean hangupAll();

    /**
     * Hangup the call associated with the informed InterlocutorUI
     * @param interlocutorUI
     * @return
     */
    public boolean hangup(InterlocutorUI interlocutorUI);
   
    /**
     * Update the interlocutor
     *
     * @param interlocutorUI To be updated
     */
    public void update(InterlocutorUI interlocutorUI);

    /**
     * Remove an interlocutor
     *
     * @param interlocutorUI To be removed
     */
    public void remove(InterlocutorUI interlocutorUI);

    /**
     * Start to play a wav.
     *
     * @param alertResourceName The wav to be played
     */
    public void startAlert(String alertResourceName);

    /**
     * Stop to play a wav.
     *
     * @param alertResourceName The wav to be stop
     */
    public void stopAlert(String alertResourceName);

    /**
     * Get the autoAnswer option
     *
     * @return The value
     */
    public boolean getAutoAnswer();

}