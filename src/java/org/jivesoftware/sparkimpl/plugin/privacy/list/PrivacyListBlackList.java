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
package org.jivesoftware.sparkimpl.plugin.privacy.list;

import java.util.ArrayList;
import java.util.List;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.PrivacyItem;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.privacy.PrivacyManager;

/**
 * PrivacyList will contain all items that allways must be blocked.
 * 
 * @author Zolotarev Konstantin
 */
public class PrivacyListBlackList extends SparkPrivacyList {

    /**
     * Listeners
     */
    private final List<PrivacyListBlackListListener> blockListeners = new ArrayList<PrivacyListBlackListListener>();

    /**
     * Create name of list
     */
    public PrivacyListBlackList() {
        super("spark:blackList");
        
        if ( !isDefault() ) {
            try {
                setListAsDefault();
            } catch (XMPPException ex) {
                if ( ex.getXMPPError().getCode() == 409 ) { 
                    try { // Couldn't set default PrivacyList, trying to set it as Active
                        setListAsActive();
                    } catch (XMPPException ex1) {
                        Log.error(ex1);
                    }
                }
                Log.error(ex);
            }
        }
        
    }

    /**
     * 
     * @return list of blocked items
     */
    public List<PrivacyItem> getBlockedItems() {
        return privacyItems;
    }

    /**
     * Block user by his jid.
     * @param jid user to block
     * @throws XMPPException
     */
    public void addBlockedItem(String jid) throws XMPPException {
        String bareJid = StringUtils.parseBareAddress(jid);
        // Creating new Privacy Item
        PrivacyItem newItem = new PrivacyItem(PrivacyItem.Type.jid.name(), false, getBlockedItems().size());
        newItem.setValue(bareJid);
        newItem.setFilterMessage(true); // Locking only messages from contact
        newItem.setFilterPresence_out(true); // And hiding from him

        addPrivacyItem(newItem);
        save(); // Store list
        PrivacyManager.getInstance().sendUnavailableTo(bareJid); //Send unavaliable presens for user
        PrivacyManager.getInstance().setBlockedIconToContact(bareJid); //Add Blocked icon
    }

    /**
     * Unblock user
     * @param jid user to unblock
     * @throws XMPPException
     */
    public void removeBlockedItem(String jid) throws XMPPException {
        String bareJid = StringUtils.parseBareAddress(jid);
        removePrivacyItem(PrivacyItem.Type.jid, bareJid, true);
        PrivacyManager.getInstance().sendRealPresenceTo(bareJid); // @todo update users presence
        PrivacyManager.getInstance().removeBlockedIconFromContact(bareJid);
        //ContactItem item = SparkManager.getContactList().getContactItemByJID(jid);
    }

    /**
     * Cheks is jid already blocked
     * @param jid user to check
     * @return is user blocked
     */
    public boolean isBlockedItem(String jid) {
        if ( searchPrivacyItem(jid) >= 0 ) {
             return true;
        }
        return false;
    }

    /**
     * Add BlockListener
     * @param listener blockListener
     */
    public void addBlockListener(PrivacyListBlackListListener listener) {
        blockListeners.add(listener);
    }

    /**
     * remove BlockListener
     * @param listener blockListener
     */
    public void removeBlockListener(PrivacyListBlackListListener listener) {
        if ( blockListeners.contains(listener) ) {
            blockListeners.remove(listener);
        }
    }

    /**
     *
     * @param jid user was added into blockList
     */
    protected void fireAddBlockedItem(String jid) {
        for (PrivacyListBlackListListener blockListener : new ArrayList<PrivacyListBlackListListener>(blockListeners)) {
            blockListener.addedBlockedItem(jid);
        }
    }

    /**
     * 
     * @param jid user removed from blackList
     */
    protected void fireRemoveBlockedItem(String jid) {
        for (PrivacyListBlackListListener blockListener : new ArrayList<PrivacyListBlackListListener>(blockListeners)) {
            blockListener.removedBlockedItem(jid);
        }
    }

}
