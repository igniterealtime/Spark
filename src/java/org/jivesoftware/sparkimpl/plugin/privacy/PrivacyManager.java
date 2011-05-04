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

import org.jivesoftware.sparkimpl.plugin.privacy.list.SparkPrivacyList;
import org.jivesoftware.sparkimpl.plugin.privacy.list.SparkPrivacyListListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.PrivacyList;
import org.jivesoftware.smack.PrivacyListManager;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.PrivacyItem;
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
    private static SparkPrivacyList _activeList = null;
    private List<String> _nameList = new ArrayList<String>();
    private Map<String,SparkPrivacyList> _privacyLists = new HashMap<String, SparkPrivacyList>();
    private PrivacyListManager privacyManager;
    private PrivacyListBlackList blackList;
    private boolean _startUpDone = false;
    private Thread _removeUITask;

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
		singleton.forceReloadLists();

	    }	    
	}
	
	return singleton;
    }

  
	public void removePrivacyList(String listName)
	{
	    try {
		privacyManager.deletePrivacyList(listName);
	    } catch (XMPPException e) {
		Log.warning("Could not remove PrivacyList "+listName); 
		e.printStackTrace();
	    }
	    
	    singleton.forceReloadLists();
	}
	
	
    private SparkPrivacyList getSparkListFromPrivacyList (String listName) {
        SparkPrivacyList privacyList = new SparkPrivacyList(listName) {
	    
	    @Override
	    public PrivacyItem prepareItem(String jid) {
		    // Creating new Privacy Item
	        PrivacyItem newItem = new PrivacyItem(PrivacyItem.Type.jid.name(), false, getNewItemOrder());
	        newItem.setValue(jid);
	        newItem.setFilterMessage(true); // Locking only messages from contact
	        newItem.setFilterPresence_out(true); // And hiding from blocked contact
	        return newItem;
	    }
	};
	
	return privacyList;
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
     * Returns the active PrivacyList
     * @return the list if there is one, else null
     */
    public SparkPrivacyList getActiveList() {
//	if (!hasActiveList())
//	    return null;
	
	if (_activeList == null) {
	    try {
		_activeList = this.getSparkListFromPrivacyList(privacyManager.getActiveList().toString());
	    } catch (XMPPException e) {
		Log.warning("server reportet there is an active list available, but could not load id",e);
	    }
	}

	return _activeList;

    }

    /**
     * this forces the PrivacyManager to reload the active list. maybe another
     * resource changed the active list, we can check this by updating the
     * active list
     */
    public void forceActiveListReload() {
	try {
	    _activeList = this.getSparkListFromPrivacyList(privacyManager.getActiveList().toString());
	} catch (XMPPException e) {
	    Log.warning("force reload active list failed", e);
	}
    }

    /**
     * Returns the sparkprivacylist that the manager keeps local, to get updated
     * version try to forcereloadlists
     * 
     * @param s
     *            the name of the list
     * @return SparkPrivacyList
     */
    public SparkPrivacyList getPrivacyList(String s)
    {
	return _privacyLists.get(s);
    }
    
    
    /**
     * Normally the manager will load the privacy lists from the server and
     * managed them locally if you want to reload the lists from the server
     * (because another resource changed sth) use this method
     */
    public void forceReloadLists() {

	if (_removeUITask != null && _removeUITask.isAlive()) {
	    return;
	}
	_removeUITask = new Thread("catch privacy lists") {
	    public void run() {
		_nameList = new ArrayList<String>();
		_privacyLists = new HashMap<String, SparkPrivacyList>();
		try {
		    for (PrivacyList pl : privacyManager.getPrivacyLists()) {
			_nameList.add(pl.toString());
			SparkPrivacyList sparkList = getSparkListFromPrivacyList(pl.toString());
			_privacyLists.put(pl.toString(), sparkList);
			if (pl.isDefaultList() && !_startUpDone) {
			    _startUpDone = true;
			    sparkList.setListAsDefault();
			    sparkList.setListAsActive();

			}
		    }
		} catch (XMPPException e) {
		    Log.warning("Error load privaylist names");
		    e.printStackTrace();
		}
	    }
	};

	_removeUITask.start();

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

    
    public void createPrivacyList (String listName)
    {
	SparkPrivacyList list = new SparkPrivacyList(listName) {
	    
	    @Override
	    public PrivacyItem prepareItem(String jid) {
		    // Creating new Privacy Item
	        PrivacyItem newItem = new PrivacyItem(PrivacyItem.Type.jid.name(), false, getNewItemOrder());
	        newItem.setValue(jid);
	        newItem.setFilterMessage(true); // Locking only messages from contact
	        newItem.setFilterPresence_out(true); // And hiding from blocked contact
	        return newItem;
	    }
	};
	
	try {
	    list.createList();
	} catch (XMPPException e) {
	    if (!(e.getXMPPError().getCode() == 404))
	    {
		Log.warning("error creating list", e);
		e.printStackTrace();
	    }
	} 
    }
    
    
    /**
     * The server can store different privacylists.
     * This method will return the names of the lists, currently available on the server
     * 
     * @return All Listnames
     */
    
    public List<String> getPrivacyListNames()
    {
	   return _nameList;
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
