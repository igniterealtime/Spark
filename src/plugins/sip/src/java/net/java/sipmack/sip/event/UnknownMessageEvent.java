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

import javax.sip.message.Message;
import javax.sip.message.Request;
import javax.sip.message.Response;

import java.util.EventObject;

public class UnknownMessageEvent extends EventObject {
	private static final long serialVersionUID = 239781911809975348L;

	public UnknownMessageEvent(Message source) {
        super(source);
    }

    public String getMessage() {
        return (source == null) ? "" : source.toString();
    }

    public String getMessageName() {
        if (source instanceof Request) {
            return (source == null) ? "" : ((Request)source).getMethod();
        }
        else {
            return (source == null) ? "" : ((Response)source).getStatusCode()
                    + " " + ((Response)source).getReasonPhrase();
        }
    }
}