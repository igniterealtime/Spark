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