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
package org.jivesoftware.spark.ui.status;

import org.jivesoftware.smack.packet.Presence;

import javax.swing.Icon;
import javax.swing.JLabel;

/**
 * Represents a single UI instance in the status bar.
 */
public class StatusItem extends JLabel {
    private static final long serialVersionUID = 725324886254656704L;
    private Presence presence;

    /**
     * Creates a single StatusItem UI object.
     *
     * @param presence the presence.
     * @param icon     the icon
     */
    public StatusItem(Presence presence, Icon icon) {
        this.presence = presence;
        setIcon(icon);
        setText(presence.getStatus());
    }

    /**
     * Returns the Presence related to this item.
     *
     * @return the presence associated with this item.
     */
    public Presence getPresence() {
        return presence;
    }


}
