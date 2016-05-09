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

import java.util.EventObject;

public class RegistrationEvent extends EventObject {
	private static final long serialVersionUID = 6759871915588020048L;

	public enum Type {
        Normal, WrongPass, NotFound, Forbidden, WrongAuthUser, TimeOut
    };

    private Type type = Type.Normal;

    public RegistrationEvent(String registrationAddress) {
        super(registrationAddress);
    }

    public RegistrationEvent(String registrationAddress, Type type) {
        super(registrationAddress);
        this.type = type;
    }

    public String getReason() {
        return (String) getSource();
    }

    public Type getType() {
        return type;
    }

}
