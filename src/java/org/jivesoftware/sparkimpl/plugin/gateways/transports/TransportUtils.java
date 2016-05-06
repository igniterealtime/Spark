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

import org.jivesoftware.smack.*;
import org.jivesoftware.smack.filter.IQReplyFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smackx.iqregister.packet.Registration;
import org.jivesoftware.smackx.iqprivate.PrivateDataManager;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.util.TaskEngine;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.gateways.GatewayPrivateData;
import org.jxmpp.util.XmppStringUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles some basic handling of
 */
public class TransportUtils {

    private static Map<String, Transport> transports = new HashMap<>();
    private static GatewayPrivateData gatewayPreferences;

    private TransportUtils() {
    }

    static {
        PrivateDataManager.addPrivateDataProvider(GatewayPrivateData.ELEMENT, GatewayPrivateData.NAMESPACE, new GatewayPrivateData.ConferencePrivateDataProvider());

        final Runnable loadGateways = () -> {
            PrivateDataManager pdm = SparkManager.getSessionManager().getPersonalDataManager();
            gatewayPreferences = null;
            //Re: SPARK-1483 comment the loop as it causes Out Of Memory (infinite loop) if preferences not found
            //If really necessary to try more times, a Thread Pool may be used: java ScheduledThreadPoolExecutor for example
            //while (gatewayPreferences == null){
                try {
                    gatewayPreferences = (GatewayPrivateData)pdm.getPrivateData(GatewayPrivateData.ELEMENT, GatewayPrivateData.NAMESPACE);
                }
                catch (XMPPException | SmackException e) {
                    Log.error("Unable to load private data for Gateways", e);
                }
            //}
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
    		catch (XMPPException | SmackException e) {
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
        String serviceName = XmppStringUtils.parseDomain(jid);
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
        catch (XMPPException | SmackException e) {
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
    public static void registerUser(XMPPConnection con, String gatewayDomain, String username, String password, String nickname, StanzaListener callback) throws SmackException.NotConnectedException
    {
        Map<String, String> attributes = new HashMap<>();
        if (username != null) {
            attributes.put("username", username);
        }
        if (password != null) {
            attributes.put("password", password);
        }
        if (nickname != null) {
            attributes.put("nick", nickname);
        }
        Registration registration = new Registration( attributes );
        registration.setType(IQ.Type.set);
        registration.setTo(gatewayDomain);
        registration.addExtension(new GatewayRegisterExtension());

        con.sendStanzaWithResponseCallback( registration, new IQReplyFilter( registration, con ), callback);
    }

    /**
     * @param con           the XMPPConnection.
     * @param gatewayDomain the domain of the gateway (service name)
     * @throws XMPPException thrown if there was an issue unregistering with the gateway.
     */
    public static void unregister(XMPPConnection con, String gatewayDomain) throws SmackException.NotConnectedException
    {
        Map<String,String> map = new HashMap<>();
        map.put("remove", "");
        Registration registration = new Registration( map );
        registration.setType(IQ.Type.set);
        registration.setTo(gatewayDomain);

        con.sendStanzaWithResponseCallback( registration, new IQReplyFilter( registration, con ), stanza -> {
            IQ response = (IQ) stanza;
            if (response.getType() == IQ.Type.error ) {
                Log.warning( "Unable to unregister from gateway: " + stanza );
            }
        } );
    }


    static class GatewayRegisterExtension implements ExtensionElement {

        public String getElementName() {
            return "x";
        }

        public String getNamespace() {
            return "jabber:iq:gateway:register";
        }

        public String toXML() {
            String builder = "<" + getElementName() + " xmlns=\"" + getNamespace() +
                    "\"/>";
            return builder;
        }
    }

}
