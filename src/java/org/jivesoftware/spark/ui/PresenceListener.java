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
package org.jivesoftware.spark.ui;

import org.jivesoftware.smack.packet.Presence;

/**
 * The <code>PresenceListener</code> is used to listen for Personal Presence changes within the system.
 * <p/>
 * Presence listeners can be registered using the {@link org.jivesoftware.spark.SessionManager}
 */
public interface PresenceListener {

    /**
     * Called when the user of Sparks presence has changed.
     *
     * @param presence the presence.
     */
    void presenceChanged(Presence presence);

}
