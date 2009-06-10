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

/**
 * @author thiagoc
 */
public class CommunicationsException extends Exception {
	private static final long serialVersionUID = -4143243596000097474L;
    private boolean isFatal = false;

    public CommunicationsException() {
        this("CommunicationsException");
    }

    public CommunicationsException(String message) {
        this(message, null);
    }

    public CommunicationsException(String message, Throwable cause) {
        this(message, cause, false);
    }

    public CommunicationsException(String message, Throwable cause,
                                   boolean isFatal) {
        super(message, cause);
        setFatal(isFatal);
    }

    // ------------------ is fatal

    /**
     * @return
     * @uml.property name="isFatal"
     */
    public boolean isFatal() {
        return isFatal;
    }

    /**
     * @param isFatal The isFatal to set.
     * @uml.property name="isFatal"
     */
    public void setFatal(boolean isFatal) {
        this.isFatal = isFatal;
    }
}