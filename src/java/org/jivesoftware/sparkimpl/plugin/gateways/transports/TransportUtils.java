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
package org.jivesoftware.sparkimpl.plugin.gateways.transports;

import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.PrivateDataManager;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.packet.DiscoverInfo;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.util.TaskEngine;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.gateways.GatewayPrivateData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles some basic handling of
 */
public class TransportUtils {

    private static Map<String, Transport> transports = new HashMap<String, Transport>();
    private static GatewayPrivateData gatewayPreferences;

    private TransportUtils() {
    }

    static {
        PrivateDataManager.addPrivateDataProvider(GatewayPrivateData.ELEMENT, GatewayPrivateData.NAMESPACE, new GatewayPrivateData.ConferencePrivateDataProvider());

        final Runnable loadGateways = new Runnable() {
            public void run() {
            	PrivateDataManager pdm = SparkManager.getSessionManager().getPersonalDataManager();
            	gatewayPreferences = null;
            	//Re: SPARK-1483 comment the loop as it causes Out Of Memory (infinite loop) if preferences not found
            	//If really necessary to try more times, a Thread Pool may be used: java ScheduledThreadPoolExecutor for example            	
                //while (gatewayPreferences == null){
                	try {
                        gatewayPreferences = (GatewayPrivateData)pdm.getPrivateData(GatewayPrivateData.ELEMENT, GatewayPrivateData.NAMESPACE);
                    }
                    catch (XMPPException e) {
                        Log.error("Unable to load private data for Gateways", e);
                    }
                //}
            }
        };

        TaskEngine.getInstance().submit(loadGateways);
    }

    public static boolean autoJoinService(String serviceName) {
        if (gatewayPreferences != null) {
        	return gatewayPreferences.autoLogin(serviceName);
        }else{
        	return false;
        }
    }

    public static void setAutoJoin(String serviceName, boolean autoJoin) {
    	if (gatewayPreferences != null) {
    		gatewayPreferences.addService(serviceName, autoJoin);
    		PrivateDataManager pdm = SparkManager.getSessionManager().getPersonalDataManager();
    		try {
    			pdm.setPrivateData(gatewayPreferences);
    		}
    		catch (XMPPException e) {
    			Log.error(e);
    		}
    	} else {
    		Log.warning("Cannot set privacy data as gatewayPreferences is NULL");
    	}
    }

    public static Transport getTransport(String serviceName) {
        // Return transport.
        if (transports.containsKey(serviceName)) {
            return transports.get(serviceName);
        }

        return null;
    }

    /**
     * Returns true if the jid is from a gateway.
     * @param jid the jid.
     * @return true if the jid is from a gateway.
     */
    public static boolean isFromGateway(String jid) {
        jid = StringUtils.parseBareAddress(jid);
        String serviceName = StringUtils.parseServer(jid);
        return transports.containsKey(serviceName);
    }

    public static void addTransport(String serviceName, Transport transport) {
        transports.put(serviceName, transport);
    }

    public static Collection<Transport> getTransports() {
        return transports.values();
    }

    /**
     * Checks if the user is registered with a gateway.
     *
     * @param con       the XMPPConnection.
     * @param transport the transport.
     * @return true if the user is registered with the transport.
     */
    public static boolean isRegistered(XMPPConnection con, Transport transport) {
        if (!con.isConnected()) {
            return false;
        }

        ServiceDiscoveryManager discoveryManager = ServiceDiscoveryManager.getInstanceFor(con);
        try {
            DiscoverInfo info = discoveryManager.discoverInfo(transport.getServiceName());
            return info.containsFeature("jabber:iq:registered");
        }
        catch (XMPPException e) {
            Log.error(e);
        }
        return false;
    }

    /**
     * Registers a user with a gateway.
     *
     * @param con           the XMPPConnection.
     * @param gatewayDomain the domain of the gateway (service name)
     * @param username      the username.
     * @param password      the password.
     * @param nickname      the nickname.
     * @throws XMPPException thrown if there was an issue registering with the gateway.
     */
    public static void registerUser(XMPPConnection con, String gatewayDomain, String username, String password, String nickname) throws XMPPException {
        Registration registration = new Registration();
        registration.setType(IQ.Type.SET);
        registration.setTo(gatewayDomain);
        registration.addExtension(new GatewayRegisterExtension());

        Map<String, String> attributes = new HashMap<String, String>();
        if (username != null) {
            attributes.put("username", username);
        }
        if (password != null) {
            attributes.put("password", password);
        }
        if (nickname != null) {
            attributes.put("nick", nickname);
        }
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

    /**
     * @param con           the XMPPConnection.
     * @param gatewayDomain the domain of the gateway (service name)
     * @throws XMPPException thrown if there was an issue unregistering with the gateway.
     */
    public static void unregister(XMPPConnection con, String gatewayDomain) throws XMPPException {
        Registration registration = new Registration();
        registration.setType(IQ.Type.SET);
        registration.setTo(gatewayDomain);
        Map<String,String> map = new HashMap<String,String>();
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


    static class GatewayRegisterExtension implements PacketExtension {

        public String getElementName() {
            return "x";
        }

        public String getNamespace() {
            return "jabber:iq:gateway:register";
        }

        public String toXML() {
            StringBuilder builder = new StringBuilder();
            builder.append("<").append(getElementName()).append(" xmlns=\"").append(getNamespace()).append(
                "\"/>");
            return builder.toString();
        }
    }

}
