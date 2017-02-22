/**
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
package org.jivesoftware.spark;

import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.iqprivate.PrivateDataManager;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.disco.packet.DiscoverItems;
import org.jivesoftware.spark.ui.PresenceListener;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.manager.Features;
import org.jxmpp.util.XmppStringUtils;

import javax.swing.SwingUtilities;

import java.util.ArrayList;
import java.util.List;

/**
 * This manager is responsible for the handling of the XMPPConnection used within Spark. This is used
 * for the changing of the users presence, the handling of connection errors and the ability to add
 * presence listeners and retrieve the connection used in Spark.
 *
 * @author Derek DeMoro
 */
public final class SessionManager implements ConnectionListener {
    private AbstractXMPPConnection connection;
    private PrivateDataManager personalDataManager;

    private String serverAddress;
    private String username;
    private String password;

    private String JID;

    private List<PresenceListener> presenceListeners = new ArrayList<>();

    private String userBareAddress;
    private DiscoverItems discoverItems;

    // Stores our presence state at the time that the last connectionClosedOnError happened.
    private Presence preError;

    public SessionManager() {
    }

    /**
     * Initializes session.
     *
     * @param connection the XMPPConnection used in this session.
     * @param username   the agents username.
     * @param password   the agents password.
     */
    public void initializeSession( AbstractXMPPConnection connection, String username, String password) {
        this.connection = connection;
        this.username = username;
        this.password = password;
        this.userBareAddress = XmppStringUtils.parseBareJid(connection.getUser());

        // create workgroup session
        personalDataManager = PrivateDataManager.getInstanceFor( getConnection() );

        // Discover items
        discoverItems();


        ProviderManager.addExtensionProvider("event", "http://jabber.org/protocol/disco#info", new Features.Provider());
    }

    /**
     * Does the initial service discovery.
     */
    private void discoverItems() {
        ServiceDiscoveryManager disco = ServiceDiscoveryManager.getInstanceFor(SparkManager.getConnection());
        try {
            discoverItems = disco.discoverItems(SparkManager.getConnection().getServiceName());
        }
        catch (XMPPException | SmackException e) {
            Log.error(e);
            discoverItems = new DiscoverItems();
        }
    }

    /**
     * Returns the XMPPConnection used for this session.
     *
     * @return the XMPPConnection used for this session.
     */
    public XMPPConnection getConnection() {
        return connection;
    }


    /**
     * Returns the PrivateDataManager responsible for handling all private data for individual
     * agents.
     *
     * @return the PrivateDataManager responsible for handling all private data for individual
     *         agents.
     */
    public PrivateDataManager getPersonalDataManager() {
        return personalDataManager;
    }


    /**
     * Returns the host for this connection.
     *
     * @return the connection host.
     */
    public String getServerAddress() {
        return serverAddress;
    }

    /**
     * Set the server address
     *
     * @param address the address of the server.
     */
    public void setServerAddress(String address) {
        this.serverAddress = address;
    }

    /**
     * Notify agent the connection was closed due to an exception.
     *
     * @param ex the Exception that took place.
     */
    public void connectionClosedOnError(final Exception ex) {
        SwingUtilities.invokeLater( () -> {
            preError = Workspace.getInstance().getStatusBar().getPresence();
            final Presence presence = new Presence(Presence.Type.unavailable);
            changePresence(presence);

            Log.debug("Connection closed on error.: " + ex.getMessage());
        } );
    }

    @Override
    public void connected( XMPPConnection xmppConnection )
    {

    }

    @Override
    public void authenticated( XMPPConnection xmppConnection, boolean b )
    {

    }

    /**
     * Notify agent that the connection has been closed.
     */
    public void connectionClosed() {
    }

    /**
     * Return the username associated with this session.
     *
     * @return the username associated with this session.
     */
    public String getUsername() {
        return XmppStringUtils.unescapeLocalpart(username);
    }

    /**
     * Return the password associated with this session.
     *
     * @return the password assoicated with this session.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Update the current availability of the user
     *
     * @param presence the current presence of the user.
     */
    public void changePresence(Presence presence) {
        // Fire Presence Listeners
        for (PresenceListener listener : new ArrayList<>( this.presenceListeners )) {
            listener.presenceChanged(presence);
        }

        // Do NOT  send presence if disconnected.
        if (SparkManager.getConnection().isConnected()) {
            // Send Presence Packet
            try
            {
                SparkManager.getConnection().sendStanza(presence);
            }
            catch ( SmackException.NotConnectedException e )
            {
                Log.error( "Unable to send presence to " + presence.getTo(), e );
            }
        }
    }

    /**
     * Returns the jid of the Spark user.
     *
     * @return the jid of the Spark user.
     */
    public String getJID() {
        return JID;
    }

    /**
     * Sets the jid of the current Spark user.
     *
     * @param jid the jid of the current Spark user.
     */
    public void setJID(String jid) {
        this.JID = jid;
    }

    /**
     * Adds a <code>PresenceListener</code> to Spark. PresenceListener's are used
     * to allow notification of when the Spark users changes their presence.
     *
     * @param listener the listener.
     */
    public void addPresenceListener(PresenceListener listener) {
        presenceListeners.add(listener);
    }

    /**
     * Remove a <code>PresenceListener</code> from Spark.
     *
     * @param listener the listener.
     */
    public void removePresenceListener(PresenceListener listener) {
        presenceListeners.remove(listener);
    }

    /**
     * Returns the users bare address. A bare-address is the address without a resource (ex. derek@jivesoftware.com/spark would
     * be derek@jivesoftware.com)
     *
     * @return the users bare address.
     */
    public String getBareAddress() {
        return userBareAddress;
    }


    /**
     * Returns the Discovered Items.
     *
     * @return the discovered items found on startup.
     */
    public DiscoverItems getDiscoveredItems() {
        return discoverItems;
    }

    public void setConnection(AbstractXMPPConnection con) {
        this.connection = con;
    }

    public void reconnectingIn(int i) {
    }

    public void reconnectionSuccessful()
    {
        // Restore the presence state that we were in just before the disconnection happened.
        if ( preError != null )
        {
            SwingUtilities.invokeLater( () ->
            {
                changePresence( preError );
                preError = null;
            });
        }
    }

    public void reconnectionFailed(Exception exception) {
    }

}
