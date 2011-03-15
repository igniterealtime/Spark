/**
 * $RCSfile: ,v $
 * $Revision: $
 * $Date: $
 * 
 * Copyright (C) 2004-2010 Jive Software. All rights reserved.
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
package org.jivesoftware.sparkimpl.plugin.privacy;

import org.jivesoftware.sparkimpl.plugin.privacy.list.SparkPrivacyListListener;

import java.util.Collection;

import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.PrivacyListManager;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.ui.ContactItem;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.privacy.list.PrivacyListBlackList;

/**
 *
 * @author Zolotarev Konstantin
 */
public class PrivacyManager implements SparkPrivacyListListener {

    private static PrivacyManager singleton;
	private static final Object LOCK = new Object();
    
    private PrivacyListManager privacyManager;
    private PrivacyListBlackList blackList;

    /**
     * PrivacyLists will be used in spark
     */
    //private HashMap<String, SparkPrivacyList> sparkPrivacyLists = new HashMap<String, SparkPrivacyList>();

    /**
     * Creating PrivacyListManager instance
     */
    private PrivacyManager() {
        XMPPConnection conn = SparkManager.getConnection();
        if ( conn == null ) {
            Log.error("Privacy plugin: Connection not initialized.");
        }
        privacyManager = PrivacyListManager.getInstanceFor(conn);        
    }

    /**
	 * Get Class instance
	 *
	 * @return instance of {@link PrivacyManager}
	 */
	public static PrivacyManager getInstance() {
		// Synchronize on LOCK to ensure that we don't end up creating
		// two singletons.
        synchronized (LOCK) {
            if (null == singleton) {
                singleton = new PrivacyManager();
            }
        }
		return singleton;
	}

    @Deprecated
    public void getPrivacyList(String listName) {
        // @todo Create different privacy Lists Functionality.
    }

    /**
     * Check for active list existence
     * 
     * @return boolean
     */
    public boolean hasActiveList() {
        try {
            getPrivacyListManager().getActiveList();
        } catch(XMPPException e) {
            if ( e.getXMPPError().getCode() == 404 ) { // No active list found
                return false;
            }
        }
        return true;
    }

    /**
     * Check if active list exist
     *
     * @return boolean
     */
    public boolean hasDefaultList() {
        try {
            getPrivacyListManager().getDefaultList();
        } catch(XMPPException e) {
            if ( e.getXMPPError().getCode() == 404 ) { // No default list found
                return false;
            }
        }
        return true;
    }

    /**
     * Get Black List
     * 
     * @return blackList containing item that always must be blocked
     */
    public final PrivacyListBlackList getBlackList() {
        if ( blackList == null ) {
            blackList = new PrivacyListBlackList();
            blackList.addBlockListener(this);
        }
        return blackList;
    }
    /**
     * Get <code>org.jivesoftware.smack.PrivacyListManager</code> instance
     * 
     * @return PrivacyListManager
     */
    public PrivacyListManager getPrivacyListManager() {
        return privacyManager;
    }

    /**
     * Send Unavailable (offline status) to jid .
     *
     * @param jid JID to send offline status
     */
    public void sendUnavailableTo(String jid) {
        Presence pack = new Presence(Presence.Type.unavailable); // Generate unavailable presence type
        pack.setTo(jid);
        SparkManager.getConnection().sendPacket(pack);
    }

    /**
     * Send my presence for user
     *
     * @param jid JID to send presence
     */
    public void sendRealPresenceTo(String jid) {
        Presence presence = SparkManager.getWorkspace().getStatusBar().getPresence(); //Get User Presence
        Presence pack = new Presence(presence.getType(), presence.getStatus(), 1, presence.getMode()); //Generate real presence
        pack.setTo(jid);
        SparkManager.getConnection().sendPacket(pack);
    }

    /**
     * Set blocked Icon for contact
     * @param jid contact's jid to ad locked icon
     */
    public void setBlockedIconToContact(String jid) {
        Collection<ContactItem> items = SparkManager.getWorkspace().getContactList().getContactItemsByJID(jid);
        for (ContactItem contactItem : items) {
            if (contactItem != null) {
                // @todo Works only for offline group.
                contactItem.setSideIcon( SparkRes.getImageIcon( SparkRes.BLOCK_CONTACT_16x16 ) );
            }
        }
        //Set icon to offline contacts into all groups.
//        for( ContactGroup group : SparkManager.getWorkspace().getContactList().getContactGroups() ) {
//            for (ContactItem offlineItem : group.getOfflineContacts() ) {
//                if ( offlineItem != null && offlineItem.getJID().equalsIgnoreCase(jid) ) {
//                    offlineItem.setSideIcon( SparkRes.getImageIcon( SparkRes.BLOCK_CONTACT_16x16 ) );
//                }
//            }
//        }
        SparkManager.getContactList().updateUI();
    }

    /**
     * Remove blocked Icon from contact
     * 
     * @param jid contact's jid to remove blocked icon
     */
    public void removeBlockedIconFromContact(String jid) {
        // We have to remove icon from all offline contacts into all groups
//        for( ContactGroup group : SparkManager.getWorkspace().getContactList().getContactGroups() ) {
//            ContactItem offlineItem = group.getOfflineContactItemByJID(jid);
//            if (offlineItem != null) {
//                offlineItem.setSideIcon( null );
//            }
//        }
        Collection<ContactItem> items = SparkManager.getWorkspace().getContactList().getContactItemsByJID(jid); //And then using this function.
        for (ContactItem item : items) {
            if (item != null) {
                item.setSideIcon( null );
            }
        }
        SparkManager.getContactList().updateUI();
    }

    @Override
    public void itemAdded(String jid) {
        sendUnavailableTo(jid); //Send unavaliable presens for user
        setBlockedIconToContact(jid); //Add Blocked icon
    }

    @Override
    public void itemRemoved(String jid) {
        sendRealPresenceTo(jid); // @todo update users presence
        removeBlockedIconFromContact(jid);
    }
}
