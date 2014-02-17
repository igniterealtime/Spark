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