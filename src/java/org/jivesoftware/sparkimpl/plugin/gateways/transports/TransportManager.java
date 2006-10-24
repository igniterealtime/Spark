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

import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Registration;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class TransportManager {

    private static Map<String, Transport> transports = new HashMap<String, Transport>();


    private TransportManager() {

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
        if (presence == null || presence.getType() == Presence.Type.unavailable) {
            registered = false;
        }
        return registered;
    }


    public static void registerUser(XMPPConnection con, String gatewayDomain, String username, String password) throws XMPPException {
        Registration registration = new Registration();
        registration.setType(IQ.Type.SET);
        registration.setTo(gatewayDomain);

        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("username", username);
        attributes.put("password", password);
        registration.setAttributes(attributes);

        PacketCollector collector = con.createPacketCollector(new PacketIDFilter(registration.getPacketID()));
        con.sendPacket(registration);

        IQ response = (IQ)collector.nextResult(SmackConfiguration.getPacketReplyTimeout());
        collector.cancel();
        if (response == null) {
            throw new XMPPException("Server timed out");
        }
        if (response.getType() == IQ.Type.ERROR) {
            throw new XMPPException("Error registering user", response.getError());
        }

    }

    public static void unregister(XMPPConnection con, String gatewayDomain) throws XMPPException {
        Registration registration = new Registration();
        registration.setType(IQ.Type.SET);
        registration.setTo(gatewayDomain);
        Map map = new HashMap();
        map.put("remove", "");
        registration.setAttributes(map);


        PacketCollector collector = con.createPacketCollector(new PacketIDFilter(registration.getPacketID()));
        con.sendPacket(registration);

        IQ response = (IQ)collector.nextResult(SmackConfiguration.getPacketReplyTimeout());
        collector.cancel();
        if (response == null) {
            throw new XMPPException("Server timed out");
        }
        if (response.getType() == IQ.Type.ERROR) {
            throw new XMPPException("Error registering user", response.getError());
        }

    }

}
