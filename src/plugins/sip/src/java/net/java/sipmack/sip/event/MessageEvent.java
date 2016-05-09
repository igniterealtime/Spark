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

import javax.sip.header.FromHeader;
import javax.sip.message.Request;

import java.util.EventObject;

public class MessageEvent extends EventObject {
	private static final long serialVersionUID = 5165120956285347269L;

	public MessageEvent(Request source) {
        super(source);
    }

    public String getBody() {
        Request request = (Request)getSource();
        Object content = request.getContent();
        String text = null;
        if (content instanceof String) {
            text = (String)content;
        }
        else if (content instanceof byte[]) {
            text = new String((byte[])content);
        }
        return text == null ? "" : text;
    }

    public String getFromAddress() {
        Request request = (Request)getSource();
        String fromAddress = "<unknown>";
        try {
            FromHeader fromHeader = (FromHeader)request
                    .getHeader(FromHeader.NAME);
            fromAddress = fromHeader.getAddress().getURI().toString();
        }
        catch (NullPointerException exc) {
            // Noone wants to know about ou null pointer exception

        }
        return fromAddress;
    }

    public String getFromName() {
        Request request = (Request)getSource();
        String fromName = null;
        try {
            FromHeader fromHeader = (FromHeader)request
                    .getHeader(FromHeader.NAME);
            fromName = fromHeader.getAddress().getDisplayName();
        }
        catch (NullPointerException exc) {
            // Noone wants to know about ou null pointer exception

        }
        return fromName == null ? "<unknown>" : fromName;
    }
}