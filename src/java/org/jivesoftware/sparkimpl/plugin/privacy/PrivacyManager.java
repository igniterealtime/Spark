/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2010 Ignite Realtime. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkimpl.plugin.privacy;

import java.util.Collection;
import java.util.HashMap;

import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.PrivacyListManager;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.ui.ContactGroup;
import org.jivesoftware.spark.ui.ContactItem;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.privacy.list.PrivacyListBlackList;
import org.jivesoftware.sparkimpl.plugin.privacy.list.SparkPrivacyList;

/**
 *
 * @author Zolotarev Konstantin
 */
public class PrivacyManager {

    private static PrivacyManager singleton;
	private static final Object LOCK = new Object();
    
    private PrivacyListManager privacyManager;
    private PrivacyListBlackList blackList;

    /**
     * PrivacyLists will be used in spark
     */
    private HashMap<String, SparkPrivacyList> sparkPrivacyLists = new HashMap<String, SparkPrivacyList>();

    /**
     * PrivacyList selected to use
     */
    private String selectedPrivacyList = null;

    /**
     * Creating PrivacyListManager instance
     */
    private PrivacyManager() {
        XMPPConnection conn = SparkManager.getConnection();
        if ( conn == null ) {
            Log.error("Connection not initialized.");
        }
        privacyManager = PrivacyListManager.getInstanceFor(conn);
        //@todo Add PrivacyListener
    }

    /**
	 * Returns the singleton instance of <CODE>PrivacyManager</CODE>, creating
	 * it if necessary.
	 * <p/>
	 *
	 * @return the singleton instance of <Code>PrivacyManager</CODE>
	 */
	public static PrivacyManager getInstance() {
		// Synchronize on LOCK to ensure that we don't end up creating
		// two singletons.
		if (null == singleton) {
            singleton = new PrivacyManager();
        }
		return singleton;
	}

    @Deprecated
    public void getPrivacyList(String listName) {
        // @todo Create different privacy Lists Functionality.
    }

    /**
     * Get Black List
     * @return blackList containing item that always must be blocked
     */
    public PrivacyListBlackList getBlackList() {
        if ( blackList == null ) {
            blackList = new PrivacyListBlackList();
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
     * Send Unavailable Type to jid we have added into block list.
     * @param jid JID to send offline status
     */
    public void sendUnavailableTo(String jid) {
        Presence pack = new Presence(Presence.Type.unavailable); // Generate unavailable presence type
        pack.setTo(jid);
        SparkManager.getConnection().sendPacket(pack);
    }

    /**
     * Send User real presence (For users was unblocked)
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
     * @param jid contact's jid
     */
    public void setBlockedIconToContact(String jid) {
        Collection<ContactItem> items = SparkManager.getWorkspace().getContactList().getContactItemsByJID(jid);
        for (ContactItem contactItem : items) {
            if (contactItem != null) {
                // @todo Works only for offline group.
                contactItem.setSideIcon( SparkRes.getImageIcon( SparkRes.BLOCK_CONTACT_16x16 ) );
            }
        }
        /*for( ContactGroup group : SparkManager.getWorkspace().getContactList().getContactGroups() ) {
            for (ContactItem offlineItem : group.getOfflineContacts() ) {
                if ( offlineItem != null && offlineItem.getJID().equalsIgnoreCase(jid) ) {
                    offlineItem.setSideIcon( SparkRes.getImageIcon( SparkRes.BLOCK_CONTACT_16x16 ) );
                }
            }
        }*/
        SparkManager.getContactList().updateUI();
    }

    /**
     * Set blocked Icon for contact
     * @param jid contact's jid
     */
    public void removeBlockedIconFromContact(String jid) {
        /*for( ContactGroup group : SparkManager.getWorkspace().getContactList().getContactGroups() ) { // I have to scan all groups for offline contacts.
            for (ContactItem offlineItem : group.getOfflineContacts() ) {
                if ( offlineItem != null && offlineItem.getJID().equalsIgnoreCase(jid) ) {
                    offlineItem.setSideIcon( null );
                }
            }
        }*/
        Collection<ContactItem> items = SparkManager.getWorkspace().getContactList().getContactItemsByJID(jid); //And then using this function.
        for (ContactItem item : items) {
            if (item != null) {
                item.setSideIcon( null );
            }
        }
        SparkManager.getContactList().updateUI();
    }
}
