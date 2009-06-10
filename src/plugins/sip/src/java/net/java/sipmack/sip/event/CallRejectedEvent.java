/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2009 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */
package net.java.sipmack.sip.event;

import net.java.sipmack.sip.Call;

import javax.sip.message.Message;

import java.util.EventObject;

public class CallRejectedEvent extends EventObject {

	private static final long serialVersionUID = 5078823819392681774L;
	private String reason = null;
	private Call call = null;

    public CallRejectedEvent(String reason, Message source, Call call) {
        super(source);
        this.reason = reason;
        this.call = call;
    }

    public String getDetailedReason() {
        return (source == null) ? "" : source.toString();
    }

    /**
     * Get the reason
     *
     * @return Returns the reason.
     */
    public String getReason() {
        return reason;
    }

    /**
     * Get the associated call
     * @return the associated call
     */
    public Call getCall() {
        return call;
    }
}