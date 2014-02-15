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

import java.util.ArrayList;
import java.util.List;

import net.java.sipmack.common.AlertManager;
import net.java.sipmack.common.Log;
import net.java.sipmack.sip.Call;
import net.java.sipmack.sip.Interlocutor;
import net.java.sipmack.sip.InterlocutorUI;
import net.java.sipmack.softphone.listeners.InterlocutorListener;
import net.java.sipmack.events.UserActionListener;

/**
 * The <code>GuiManager</code> class that Manage all the actions and Events of
 * User Interface.
 *
 * @author Thiago Rocha Camargo (thiago@jivesoftware.com)
 * @version 1.0, 20/07/2006
 */

public class GuiManager implements GuiCallback, DefaultGuiManager {

    private List<InterlocutorUI> interlocutors = new ArrayList<InterlocutorUI>();

    private AlertManager alertManager = new AlertManager();

    public List<UserActionListener> actionHandlers = new ArrayList<UserActionListener>();

    public List<InterlocutorListener> interlocutorListeners = new ArrayList<InterlocutorListener>();

    private boolean autoAnswer = false;

    /**
     * Constructor of the class. Instantiate DTMFSounds and create the GUI
     */
    public GuiManager() {
    }

    /**
     * Loads the config form SIPConfig class
     */
    public void loadConfig() {
    }

    /**
     * Sets the actionListener
     *
     * @param ual UserActionListener that will handle actions
     */
    public void addUserActionListener(UserActionListener ual) {
        actionHandlers.add(ual);
    }

    /**
     * Adds an InterlocutorListener
     *
     * @param interlocutorListener
     */
    public void addInterlocutorListener(InterlocutorListener interlocutorListener) {
        interlocutorListeners.add(interlocutorListener);
    }

    /**
     * Removes an InterlocutorListener
     *
     * @param interlocutorListener
     */
    public void removeInterlocutorListener(InterlocutorListener interlocutorListener) {
        interlocutorListeners.remove(interlocutorListener);
    }

    /**
     * Add a new interlocutor
     *
     * @param interlocutors InterlocutorUI to be added.
     */
    public synchronized void addInterlocutor(InterlocutorUI interlocutors) {
        interlocutors.setCallback(this);

        this.interlocutors.add(interlocutors);

        for (InterlocutorListener interlocutorListener : interlocutorListeners) {
            interlocutorListener.interlocutorAdded(interlocutors);
        }
    }

    /**
     * Update the interlocutor
     *
     * @param interlocutorUI To be updated
     */
    public void update(InterlocutorUI interlocutorUI) {

    }

    /**
     * Returns the current interlocutors
     *
     * @return List<InterlocutorUI>
     */
    public List<InterlocutorUI> getInterlocutors() {
        return interlocutors;
    }

    /**
     * Counts the current interlocutors number
     */
    public int countInterlocutors() {
        return interlocutors.size();
    }

    /**
     * Remove an interlocutor
     *
     * @param interlocutorUI To be removed
     */
    public synchronized void remove(InterlocutorUI interlocutorUI) {
        interlocutors.remove(interlocutorUI);
        for (InterlocutorListener interlocutorListener : interlocutorListeners)
            interlocutorListener.interlocutorRemoved(interlocutorUI);
    }

    /**
     * Start to play a wav.
     *
     * @param alertResourceName The wav to be played
     */
    public void startAlert(String alertResourceName) {
        try {
            alertManager.startAlert(alertResourceName);
        }
        catch (Throwable ex) {
            // OK, no one cares really
        }
    }

    /**
     * Stop to play a wav.
     *
     * @param alertResourceName The wav to be stop
     */
    public void stopAlert(String alertResourceName) {
        try {
            alertManager.stopAlert(alertResourceName);
        }
        catch (Throwable ex) {
            // OK, no one cares really
        }
    }

    /**
     * Stop all waves.
     */
    public void stopAllAlerts() {
        try {
            alertManager.stopAllAlerts();
        }
        catch (Throwable ex) {
            // OK, no one cares really
        }
    }

    /**
     * Answer the current ringing call
     */
    public boolean answer() {
        if (interlocutors.size() < 1) {
            Log.debug("answer", "No interlocutors");
            return false;
        }

        boolean found = false;

        for (InterlocutorUI interlocutor : interlocutors) {
            Interlocutor inter = (Interlocutor) interlocutor;
            if (!inter.getCall().isIncoming() || !inter.getCall().getState().equals(Call.ALERTING)) continue;
            found = true;
            for (UserActionListener ual : actionHandlers) {
                ual.handleAnswerRequest(inter);
            }
        }
        Log.debug("answer", "Answered");
        return found;
    }

    /**
     * Hold all current calls. In fact it holds all medias depending of the
     * server.
     */
    public void holdAll() {
        if (interlocutors.size() < 1) {
            Log.debug("hold", "No interlocutors");
            return;
        }

        for (InterlocutorUI interlocutor : interlocutors) {
            boolean mic = interlocutor.onHoldMic(), cam = interlocutor.onHoldCam();
            for (UserActionListener ual : actionHandlers) {
                ual.handleHold(interlocutor, !mic, cam);
            }
        }
    }

    /**
     * Hold current call of associated interlocutor. In fact it holds all medias depending of the
     * server.
     *
     * @param interlocutor interlocutor that will be holded
     */
    public void hold(InterlocutorUI interlocutor) {
        boolean mic = interlocutor.onHoldMic(), cam = interlocutor.onHoldCam();
        for (UserActionListener ual : actionHandlers) {
            ual.handleHold(interlocutor, !mic, cam);
        }
    }

    /**
     * Mute all current calls.
     */
    public void muteAll(boolean mic) {
        if (interlocutors.size() < 1) {
            Log.debug("mute", "No interlocutors");
            return;
        }
        for (InterlocutorUI interlocutor : interlocutors) {
            for (UserActionListener ual : actionHandlers) {
                ual.handleMute(interlocutor, mic);
            }
        }
    }

    /**
     * Mute the current call associated with the informed interlocutor.
     *
     * @param interlocutor
     * @param mic
     */
    public void mute(InterlocutorUI interlocutor, boolean mic) {
        for (UserActionListener ual : actionHandlers) {
            ual.handleMute(interlocutor, mic);
        }
    }

    /**
     * Send a DTMF Tone to all current calls
     *
     * @param digit DTMF digit to be sent
     */
    public void sendDTMF(String digit) {
        if (interlocutors.size() < 1) {
            Log.debug("sendDTMF", "No interlocutors");
            return;
        }
        int selectedRow = 0;
        Interlocutor inter = (Interlocutor) interlocutors.get(selectedRow);
        for (UserActionListener ual : actionHandlers) {
            ual.handleDTMF(inter, digit);
        }
    }

    /**
     * Dial a number
     *
     * @param callee Number to be called
     */
    public void dial(String callee) {
        for (UserActionListener ual : actionHandlers) {
            ual.handleDialRequest(callee);
        }
    }

    /**
     * Hangup the current call
     */
    public boolean hangupAll() {
        if (interlocutors.size() < 1) {
            Log.debug("hangup", "No interlocutors");
            return false;
        }
        Interlocutor inter;
        for (int i = 0; i < interlocutors.size(); i++) {
            inter = (Interlocutor) interlocutors.get(i);
            for (UserActionListener ual : actionHandlers) {
                ual.handleHangupRequest(inter);
            }
        }
        return true;
    }

    /**
     * Hangup the call associated with the informed InterlocutorUI
     *
     * @param interlocutorUI
     * @return
     */
    public boolean hangup(InterlocutorUI interlocutorUI) {
        boolean result = true;
        for (UserActionListener ual : actionHandlers) {
            result = ual.handleHangupRequest((Interlocutor) interlocutorUI) ? result ? true : false : false;
        }
        return result;
    }

    /**
     * Set the autoAnswer option
     *
     * @param value The value to be set
     */
    public void setAutoAnswer(boolean value) {
        autoAnswer = value;
    }

    /**
     * Get the autoAnswer option
     *
     * @return The value
     */
    public boolean getAutoAnswer() {
        return autoAnswer;
    }

}
