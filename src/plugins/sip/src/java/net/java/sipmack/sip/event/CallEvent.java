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


public class CallEvent extends EventObject {
	private static final long serialVersionUID = -7791513047352316593L;

	public CallEvent(Object source) {
        super(source);
    }

    public Call getSourceCall() {
        return (Call)source;
    }
}