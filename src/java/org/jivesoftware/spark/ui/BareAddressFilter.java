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

import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.StringUtils;

/**
 * Filters for packets where the "from" field contains a specified value.
 *
 * @author Derek DeMoro
 */
public class BareAddressFilter implements PacketFilter {

    private String from;

    /**
     * Creates a "from" contains filter using the "from" field part.
     *
     * @param from the from field value the packet must contain.
     */
    public BareAddressFilter(String from) {
        if (from == null) {
            throw new IllegalArgumentException("Parameter cannot be null.");
        }
        this.from = from.toLowerCase();
    }

    public boolean accept(Packet packet) {
        if (packet.getFrom() == null) {
            return false;
        }
        else {
            if (packet.getFrom().toLowerCase().equals(from.toLowerCase())) {
                return true;
            }
            else if (StringUtils.parseBareAddress(packet.getFrom()).equals(StringUtils.parseBareAddress(from))) {
                return true;
            }
        }
        return false;
    }
}