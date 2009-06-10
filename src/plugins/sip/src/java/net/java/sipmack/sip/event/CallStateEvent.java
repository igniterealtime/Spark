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

import java.util.EventObject;

public class CallStateEvent extends EventObject {

	private static final long serialVersionUID = -9218156329743857188L;
	private String oldStatus;

    public CallStateEvent(Call source) {
        super(source);
    }

    public Call getSourceCall() {
        return (Call)getSource();
    }

    public void setOldState(String status) {
        this.oldStatus = status;
    }

    public String getOldState() {
        return oldStatus;
    }

    public String getNewState() {
        return getSourceCall().getState();
    }
}