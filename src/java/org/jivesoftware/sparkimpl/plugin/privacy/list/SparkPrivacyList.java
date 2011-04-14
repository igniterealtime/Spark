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

import org.jivesoftware.smack.PrivacyList;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.PrivacyItem;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.ui.ContactGroup;
import org.jivesoftware.spark.ui.ContactItem;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.privacy.PrivacyException;
import org.jivesoftware.sparkimpl.plugin.privacy.PrivacyManager;

/**
 *
 * @author Zolotarev Konstantin
 */
public abstract class SparkPrivacyList {

    /**
     * List name will be used to identify PrivacyList
     */
    protected String listName   = "";

    /**
     * List Items
     */
    protected ArrayList<PrivacyItem> privacyItems;

    /**
     * Listeners
     */
    private final List<SparkPrivacyListListener> listeners = new ArrayList<SparkPrivacyListListener>();

    /**
     * Action associated with the items, it MUST be filled and will allow or deny
     * the communication by default
     */
    protected boolean allowByDefault = true;

    
    /**
     * Set PrivacyList name
     * 
     * Only children can set listName
     * 
     * @param listName name of the PrivacyList
     */
    protected final void setListName(String listName) {
        this.listName = listName;
    }

    /**
     * Loads list from server.
     *
     * If list not exist we will try to create it.
     * 
     */
    protected final void loadList() throws XMPPException, PrivacyException {
        if ( listName.isEmpty() ) {
            throw new PrivacyException("List name not defined.");
        }
        try {
            if ( PrivacyManager.getInstance().getPrivacyListManager() == null ) {
                // @todo handle error
            }
            PrivacyList list = PrivacyManager.getInstance().getPrivacyListManager().getPrivacyList( getListName() );
            privacyItems = (ArrayList<PrivacyItem>) list.getItems();
            if ( privacyItems.size() > 0 ) { // Check for the default item
                // @todo
                PrivacyItem defaultItem = getDefaultPrivacyItem(); //Get Last Item (Default Item allways must be the last one.)
                if ( defaultItem != null ) { // Is this Item default ?
                    //Yep this item is default get its rule.
                    allowByDefault = defaultItem.isAllow();
                }
                privacyItems.remove(defaultItem); // Removing Default Item from array list, we will add it on next save.
            }
        } catch(XMPPException ex) {
            if ( ex.getXMPPError().getCode() == 404 ) { // List not found. Let's create it.
                createList(); // Creating new PrivacyList.
            } else
            {
        	Log.error(ex); // Log all errors
            }
            
        }
        
    }

    /**
     * Get maximal Order value from PrivacyItemList
     *
     * @return
     */
    protected int getMaxItemOrder() {
        if(getLastItem() != null) {
            return getLastItem().getOrder();
        }
        return 1;
    }
 
    /**
     * Cheks is jid already blocked
     * @param jid user to check
     * @return is user blocked
     */
    public boolean isBlockedItem(String jid) {
        if ( searchPrivacyItem(jid) != null ) {
             return true;
        }
        return false;
    }
    
    /**
     * Load default privecy item
     * 
     * @return
     */
    protected PrivacyItem getDefaultPrivacyItem() {
        PrivacyItem item = null;
        for (PrivacyItem privacyItem : getPrivacyItems()) {
            if ( privacyItem.getValue() == null && privacyItem.getType() == null ) {
                item = privacyItem;
            }
        }
        return item;
    }

    /**
     * Get last PrivacyItem from list ordered by PrivacyItem.order
     *
     * @return last PrivacyItem ordered by Item order
     */
    protected PrivacyItem getLastItem() {
        int order = 0;
        PrivacyItem item = null;
        for (PrivacyItem privacyItem : getPrivacyItems()) {
            if ( order < privacyItem.getOrder() ) {
                order = privacyItem.getOrder();
                item = privacyItem;
            }
        }
        return item;
    }

    /**
     * Return order id for new PrivacyItem
     * 
     * @return
     */
    public int getNewItemOrder() {
        return (getMaxItemOrder()+1);
    }

    /**
     * Returns new created Default PrivacyItem like <code><item action='allow' order='999999'/></code>.
     * Order is set to 999999 because this item must be last
     *
     * @return new privacy item
     */
    protected PrivacyItem createDefaultPrivacyItem() {
        return createDefaultPrivacyItem( 999999 );
    }

    /**
     * Returns new created Default PrivacyItem
     *
     * Privacy item xml will look like:
     * <code><item action='allow' order='999999'/></code>.<br/>
     * With order seted here.
     *
     * @param order order for default PrivacyItem
     */
    protected PrivacyItem createDefaultPrivacyItem(int order) {
        return new PrivacyItem(null, isItemAllowByDefault(), order);
    }

    /**
     * Search privancyItem using Type & value
     * 
     * @param type type of privacy item
     * @param value value of item
     * @return privacyItem or null if Item not found
     */
    protected PrivacyItem searchPrivacyItem(String value) {
        for (PrivacyItem privacyItem : getPrivacyItems()) {
            if ( privacyItem.getValue().equalsIgnoreCase(value)) {
                return privacyItem;
            }
        }
        return null; //error
    }

    /**
     * Search privancyItem using Type & value
     *
     * @param type type of privacy item
     * @param value value of item
     * @return privacyItem or null if Item not found
     */
    protected PrivacyItem searchPrivacyItem(PrivacyItem.Type type, String value) {
        for (PrivacyItem privacyItem : getPrivacyItems()) {
            if ( privacyItem.getValue().equalsIgnoreCase(value) && privacyItem.getType() == type ) {
                return privacyItem;
            }
        }
        return null; //error
    }

    /**
     * Search privancyItem using Type & value
     *
     * @param type type of privacy item
     * @param value value of item
     * @return privacyItem id of item into PrivacyItems or -1 on Item not found
     */
    protected ArrayList<PrivacyItem> searchPrivacyItems(PrivacyItem.Type type, String value) {
        ArrayList<PrivacyItem> items = new ArrayList<PrivacyItem>();
        for (PrivacyItem privacyItem : getPrivacyItems()) {
            if ( privacyItem.getValue().equalsIgnoreCase(value) && privacyItem.getType() == type ) {
                items.add(privacyItem);
            }
        }
        return items; //error
    }

    /**
     * Init Privacy list
     * 
     * @param listname
     */
    public SparkPrivacyList(String listname) {
        setListName(listname);
        try {
            loadList();
        } catch (XMPPException ex) {
            Log.error(ex);
        }
    }

    public abstract PrivacyItem prepareItem(String jid);


    public void addItem(String jid) throws XMPPException {
        String bareJid = StringUtils.parseBareAddress(jid);
        addPrivacyItem(prepareItem(bareJid));        
        save(); // Store list
    }

    public void removeItem(String jid) throws XMPPException {
        String bareJid = StringUtils.parseBareAddress(jid);
        removePrivacyItem(PrivacyItem.Type.jid, bareJid);
        save();
    }



    /**
     * Returns Privasy List name
     * @return PrivacyList name
     */
    public String getListName() {
        return listName;
    }

    /**
     * Answer the privacy list items with the allowed and blocked permissions.
     * @return list items
     */
    public ArrayList<PrivacyItem> getPrivacyItems() {
        if ( privacyItems == null ) {
            privacyItems = new ArrayList<PrivacyItem>();
        }
        return privacyItems;
    }

    /**
     * Set the privacy list items with the allowed and blocked permissions.
     * @param items Items of the list
     */
    public void setPrivacyItems(List<PrivacyItem> items) {
        privacyItems = (ArrayList<PrivacyItem>) items;
    }

    /**
     * Add New privacy item without saving list
     *
     * @param item new privacyItem
     */
    public void addPrivacyItem(PrivacyItem item) throws XMPPException {
	if (item.getValue() == null || item.getValue().isEmpty()) {
	    throw new PrivacyException("Item must contain JID!");
	}
	if (searchPrivacyItems(item.getType(), item.getValue()).size() < 1) {
	    getPrivacyItems().add(item);
	}
	
	if (item.isFilterPresence_out() && this.isActive()) {
	    if (item.getType().equals(PrivacyItem.Type.jid)) {

		PrivacyManager.getInstance().sendUnavailableTo(item.getValue());

	    } else if (item.getType().equals(PrivacyItem.Type.group)) {
		ContactGroup group = SparkManager.getContactList().getContactGroup(item.getValue());
		for (ContactItem cI:group.getContactItems())
		{
		    PrivacyManager.getInstance().sendUnavailableTo(cI.getJID());
		}
	    }
	}

	fireItemAdded(item.getValue());
    }

    
    /**
     * Removes PrivacyItem From list without saving
     *
     * @param type type of privacy item
     * @param value value of item
     */
    public void removePrivacyItem(PrivacyItem.Type type, String value) {
	if (getPrivacyItems().size() < 1) {
	    return;
	}
	// If somewhere in list are more than one item
	ArrayList<PrivacyItem> itemsFound = searchPrivacyItems(type, value);
	for (PrivacyItem privacyItem : itemsFound) {
	    getPrivacyItems().remove(privacyItem);
	}
	if (type.equals(PrivacyItem.Type.group) && this.isActive()) {
	    ContactGroup group = SparkManager.getContactList().getContactGroup(value);
	    for (ContactItem cI : group.getContactItems()) {
		PrivacyManager.getInstance().sendRealPresenceTo(cI.getJID());
	    }
	} else
	{
	    PrivacyManager.getInstance().sendRealPresenceTo(value);
	}

	fireItemRemoved(value);
    }

    /**
     * Is PrivacyList Active
     * 
     * @return is This list active or not
     */
    public boolean isActive() {
        try {
            return PrivacyManager.getInstance().getPrivacyListManager().getActiveList().toString().equals(getListName());
        } catch (XMPPException ex) {
            if (ex.getXMPPError().getCode() == 404) {
                return false;
            }
            Log.error(ex);
        }
        return false;
    }

    /**
     * Is PrivacyList set as default
     *
     * @return is this list default
     */
    public boolean isDefault() {
        try {
            return PrivacyManager.getInstance().getPrivacyListManager().getDefaultList().toString().equals(getListName());
        } catch (XMPPException ex) {
            if (ex.getXMPPError().getCode() == 404) {
                return false;
            }
            Log.error(ex);
        }
        return false;
    }

    /**
     * Set PrivacyList as active on server
     * @throws XMPPException
     */
    public void setListAsActive() throws XMPPException {
        PrivacyManager.getInstance().getPrivacyListManager().setActiveListName( getListName() );
        if (listName.equals(PrivacyManager.getInstance().getBlackList().getListName()))
	{
	    final Presence myPresence = SparkManager.getWorkspace().getStatusBar()
		.getPresence();
	    	SparkManager.getSessionManager().changePresence(myPresence);
	    	return;
	}
	for (PrivacyItem pI :privacyItems)
	{
	    if (pI.isFilterPresence_out())
	    {
		if (pI.getType().equals(PrivacyItem.Type.jid))
		{
		    PrivacyManager.getInstance().sendUnavailableTo(pI.getValue());
		}
		if (pI.getType().equals(PrivacyItem.Type.group))
		{
		    ContactGroup group = SparkManager.getContactList().getContactGroup(pI.getValue());
		    for (ContactItem cI:group.getContactItems())
		    {
			PrivacyManager.getInstance().sendUnavailableTo(cI.getJID());
		    }
		}
	    }
	}
    }

    /**
     * Set Privacy list as default on server
     * 
     * @throws XMPPException
     */
    public void setListAsDefault() throws XMPPException {
        PrivacyManager.getInstance().getPrivacyListManager().setDefaultListName( getListName() );
    }

    /**
     * Returns the action associated with the items, it MUST be filled and will allow or deny
     * the communication by default
     *
     * @return the default allow communication status.
     */
    public boolean isItemAllowByDefault() {
        return allowByDefault;
    }

    /**
     * Set the action associated with the items, it MUST be filled and will allow or deny
     * the communication by default
     *
     */
    public void setItemAllowByDefault(boolean allow) {
        allowByDefault = allow;
    }

    /**
     * Create an empty privacy list if it didn't created
     * 
     * @see org.jivesoftware.sparkimpl.plugin.privacy.PrivacyManager#createList(java.lang.String, java.util.List)
     */
    public void createList() throws XMPPException {
        getPrivacyItems().clear(); //Clear list if it is already exist
        PrivacyItem defItem = createDefaultPrivacyItem();
        getPrivacyItems().add(defItem); //Add default privacy action
        PrivacyManager.getInstance().getPrivacyListManager().createPrivacyList(getListName(), getPrivacyItems());
        getPrivacyItems().remove(defItem); // remove Default item it will be added on next save operation
    }

    /**
     * Store PrivacyList on server
     * 
     * @throws XMPPException
     */
    public void save() throws XMPPException {
        PrivacyItem defItem = createDefaultPrivacyItem();
        getPrivacyItems().add(defItem); //Add default privacy action
        PrivacyManager.getInstance().getPrivacyListManager().updatePrivacyList(getListName(), getPrivacyItems());
        getPrivacyItems().remove(defItem); // remove Default item it will be added before next save
    }

    /**
     * 
     * @return listName
     */
    @Override
    public String toString() {
        return getListName();
    }

    /**
     * Add SparkPrivacyListListener
     * @param listener 
     */
    public void addBlockListener(SparkPrivacyListListener listener) {
        listeners.add(listener);
    }

    /**
     * Remove SparkPrivacyListListener
     * @param listener 
     */
    public void removeBlockListener(SparkPrivacyListListener listener) {
        if ( listeners.contains(listener) ) {
            listeners.remove(listener);
        }
    }

    /**
     *
     * @param jid user was added into blockList
     */
    protected void fireItemAdded(String jid) {
        for (SparkPrivacyListListener listener : new ArrayList<SparkPrivacyListListener>(listeners)) {
            listener.itemAdded(jid);
        }
    }

    /**
     *
     * @param jid user removed from blackList
     */
    protected void fireItemRemoved(String jid) {
        for (SparkPrivacyListListener listener : new ArrayList<SparkPrivacyListListener>(listeners)) {
            listener.itemRemoved(jid);
        }
    }

}
