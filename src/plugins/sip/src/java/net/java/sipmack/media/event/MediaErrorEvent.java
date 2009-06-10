/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2009 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */
package net.java.sipmack.media.event;

import java.util.EventObject;

public class MediaErrorEvent extends EventObject {
	private static final long serialVersionUID = -223468673281672684L;

	public MediaErrorEvent(Throwable source) {
        super(source);
    }

    public Throwable getCause() {
        return (Throwable)source;
    }
}