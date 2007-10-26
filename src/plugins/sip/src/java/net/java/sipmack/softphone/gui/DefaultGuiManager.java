/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2007 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
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
