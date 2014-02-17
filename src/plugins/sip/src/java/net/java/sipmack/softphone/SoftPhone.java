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
package net.java.sipmack.softphone;

import net.java.sipmack.media.MediaException;
import net.java.sipmack.sip.InterlocutorUI;
import net.java.sipmack.sip.SipRegisterStatus;
import net.java.sipmack.sip.event.CommunicationsListener;
import net.java.sipmack.softphone.gui.DefaultGuiManager;
import net.java.sipmack.softphone.listeners.InterlocutorListener;
import net.java.sipmack.softphone.listeners.SoftPhoneListener;

import java.util.List;

public interface SoftPhone {

    /**
     * Handle an UnregisterRequest
     */
    public void handleUnregisterRequest();

    /**
     * Handle a Re-Register Request.
     * This method will only have effect if the user has successfully registered beforeat least once before.
     */
    public void handleReRegisterRequest();

    /**
     * Handle a RegisterRequest
     *
     * @param username username
     * @param password password
     */
    public void handleRegisterRequest(String username, String password);

    /**
     * Handle a RegisterRequest
     *
     * @param username username
     * @param authName Authorization username
     * @param password password
     */
    public void handleRegisterRequest(String username, String authName, String password);

    /**
     * Handle an exit request
     */
    public void handleExitRequest();

    /**
     * Return the DefaultGuiManager
     */
    public DefaultGuiManager getDefaultGuiManager();

    /**
     * Create the softphone handlers and stack
     */
    public void createSoftPhone(String server) throws MediaException;

    /**
     * Add SoftPhoneListener
     *
     * @param softPhoneListener
     */
    public void addSoftPhoneListener(SoftPhoneListener softPhoneListener);

    /**
     * Add InterlocutorListener
     *
     * @param interlocutorListener
     */
    public void addInterlocutorListener(InterlocutorListener interlocutorListener);

    /**
     * Remove InterlocutorListener
     *
     * @param interlocutorListener
     */
    public void removeInterlocutorListener(InterlocutorListener interlocutorListener);

    /**
     * Returns the current interlocutors
     *
     * @return List<InterlocutorUI>
     */
    public List<InterlocutorUI> getInterlocutors();

    /**
     * Add a CommunicationsListener
     *
     * @param communicationsListener CommunicationsListener
     */
    public void addCommunicationsListener(CommunicationsListener communicationsListener);

    /**
     * Gets the current username
     *
     * @return The current connection username
     */
    public String getUsername();

    /**
     * Gets the current server
     *
     * @return The current connection server
     */
    public String getServer();

    /**
     * Return the current SIP connection status
     *
     * @return status
     * @uml.property name="status"
     */
    public SipRegisterStatus getStatus();

    /**
     * Handle a Hold request
     */
    public void handleMute(InterlocutorUI iui, boolean mic);
}
