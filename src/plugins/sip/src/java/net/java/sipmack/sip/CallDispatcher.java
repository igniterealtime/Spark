/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2009 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */
package net.java.sipmack.sip;

import net.java.sipmack.sip.event.CallListener;
import net.java.sipmack.sip.event.CallStateEvent;

import javax.sip.Dialog;
import javax.sip.message.Request;

import java.util.Enumeration;
import java.util.Hashtable;

class CallDispatcher implements CallListener {

    /**
     * All currently active calls.
     */
    Hashtable<Integer, Call> calls = new Hashtable<Integer, Call>();

    Call createCall(Dialog dialog, Request initialRequest) {
        Call call = null;
        if (dialog.getDialogId() != null) {
            call = findCall(dialog);
        }
        if (call == null) {
            call = new Call();
        }
        call.setDialog(dialog);
        call.setInitialRequest(initialRequest);
        // call.setState(Call.DIALING);
        calls.put(Integer.valueOf(call.getID()), call);
        call.addStateChangeListener(this);
        return call;
    }

    Call getCall(int id) {
        return (Call) calls.get(Integer.valueOf(id));
    }

    /**
     * Find the call that contains the specified dialog.
     *
     * @param dialog the dialog whose containg call is to be found
     * @return the call that contains the specified dialog.
     */
    Call findCall(Dialog dialog) {
        if (dialog == null) {
            return null;
        }
        synchronized (calls) {
            Enumeration<Call> callsEnum = calls.elements();
            while (callsEnum.hasMoreElements()) {
                Call item = (Call) callsEnum.nextElement();
                if (item.getDialog().getCallId().equals(dialog.getCallId())) {
                    return item;
                }
            }
        }
        return null;
    }

    /**
     * Find the call with the specified CallID header value.
     *
     * @param callId the CallID header value of the searched call.
     * @return the call with the specified CallID header value.
     */
    Call findCall(String callId) {
        if (callId == null) {
            return null;
        }
        synchronized (calls) {
            Enumeration<Call> callsEnum = calls.elements();
            while (callsEnum.hasMoreElements()) {
                Call item = (Call) callsEnum.nextElement();
                if (item.getDialog().getCallId().equals(callId)) {
                    return item;
                }
            }
        }
        return null;

    }

    Object[] getAllCalls() {
        return calls.keySet().toArray();
    }

    private void removeCall(Call call) {
        calls.remove(Integer.valueOf(call.getID()));
    }

    // ================================ DialogListener =================
    public void callStateChanged(CallStateEvent evt) {
        if (evt.getNewState().equals(Call.DISCONNECTED)) {
            removeCall(evt.getSourceCall());
        }
    }
}