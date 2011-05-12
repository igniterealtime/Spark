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
package net.java.sipmack.sip;

import net.java.sipmack.common.Log;
import net.java.sipmack.sip.event.CallListener;
import net.java.sipmack.sip.event.CallStateEvent;
import net.java.sipmack.softphone.gui.GuiCallback;

/**
 * Title: SIPark
 * Description:JAIN-SIP Audio/Video phone application
 *
 * @author Thiago Rocha Camargo (thiago@jivesoftware.com)
 */

public class Interlocutor implements InterlocutorUI, CallListener {
    private Call call;

    private GuiCallback guiCallback;

    public static String BUSY = "BUSY";

    public static String RINGING = "RINGING";

    public static String ALERTING = "ALERTING";

    /**
     * @param call The call to set.
     * @uml.property name="call"
     */
    public void setCall(Call call) {
        this.call = call;
        call.addStateChangeListener(this);
    }

    /**
     * @return Returns the call.
     * @uml.property name="call"
     */
    public Call getCall() {
        return call;
    }

    // InterlocutorUI
    public boolean isCaller() {
        return call.isIncoming();
    }

    public boolean onHoldMic() {
        return call.onHoldMic();
    }

    public boolean onHoldCam() {
        return call.onHoldCam();
    }

    public String getAddress() {
        return call.getAddress();
    }

    public String getName() {
        return call.getRemoteName();
    }

    public int getID() {
        return call.getID();
    }

    public String getCallState() {
        return call.getState();
    }

    public void setCallback(GuiCallback callback) {
        this.guiCallback = callback;
    }

    // CallListener
    public void callStateChanged(CallStateEvent evt) {
        try {
            guiCallback.update(this);

            if (evt.getNewState() == Call.DISCONNECTED) {
                guiCallback.remove(this);
            }
            if (evt.getNewState() != evt.getOldState()) {
                if (evt.getOldState() == Call.ALERTING) {
                    guiCallback.stopAlert(ALERTING);
                } else if (evt.getOldState() == Call.RINGING) {
                    guiCallback.stopAlert(RINGING);
                } else if (evt.getOldState() == Call.BUSY) {
                    guiCallback.stopAlert(BUSY);
                    // Start current alert
                }
                if (evt.getNewState() == Call.ALERTING) {
                    guiCallback.startAlert(ALERTING);
                } else if (evt.getNewState() == Call.RINGING) {
                    if (evt.getSourceCall().getRemoteSdpDescription() == null || evt.getSourceCall().getRemoteSdpDescription().toString() == "")
                        guiCallback.startAlert(RINGING);
                } else if (evt.getNewState() == Call.BUSY) {
                    guiCallback.startAlert(BUSY);
                }
            }
        }
        catch (Exception e) {
            Log.error("callStateChanged-Interlocutor", e);
        }       
    }

}