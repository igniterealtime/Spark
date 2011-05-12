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