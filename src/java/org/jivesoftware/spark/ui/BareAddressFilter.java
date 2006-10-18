/**
 * $Revision$
 * $Date$
 *
 * Copyright (C) 1999-2005 Jive Software. All rights reserved.
 * This software is the proprietary information of Jive Software. Use is subject to license terms.
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