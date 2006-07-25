/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkimpl.plugin.gateways.transports;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Presence;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class TransportFactory {

    private static Map<String, Transport> transports = new HashMap<String, Transport>();


    private TransportFactory() {

    }

    public static Transport getTransport(String serviceName) {
        // Return transport.
        if (transports.containsKey(serviceName)) {
            return transports.get(serviceName);
        }

        return null;
    }

    public static void addTransport(String serviceName, Transport transport) {
        transports.put(serviceName, transport);
    }

    public static Collection<Transport> getTransports() {
        return transports.values();
    }

    public static boolean isRegistered(XMPPConnection con, Transport transport) {
        Presence presence = con.getRoster().getPresence(transport.getServiceName());
        boolean registered = presence != null && presence.getMode() != null;
        return registered;
    }
}
