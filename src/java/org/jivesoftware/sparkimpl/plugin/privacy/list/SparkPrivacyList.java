/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2010 Ignite Realtime. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkimpl.plugin.privacy.list;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.PrivacyList;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.PrivacyItem;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.privacy.PrivacyException;
import org.jivesoftware.sparkimpl.plugin.privacy.PrivacyManager;

/**
 *
 * @author Zolotarev Konstantin
 */
public class SparkPrivacyList {

    /**
     * List name will be used to identify PrivacyList
     */
    protected String listName = null;

    /**
     * List Items
     */
    protected ArrayList<PrivacyItem> privacyItems;

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
    protected void setListName(String listName) {
        this.listName = listName;
    }

    /**
     * Loads list from server.
     *
     * If list not exist we will try to create it.
     * 
     */
    protected void loadList() throws XMPPException {
        if ( listName == null || listName.isEmpty() ) {
            throw new PrivacyException("List name not defined.");
        }
        try {
            if ( PrivacyManager.getInstance().getPrivacyListManager() == null ) {
                // @todo handle error
            }
            PrivacyList list = PrivacyManager.getInstance().getPrivacyListManager().getPrivacyList( getListName() );
            privacyItems = (ArrayList<PrivacyItem>) list.getItems();
            if ( privacyItems.size() > 0 ) { // Check for the default item
                PrivacyItem defItem = privacyItems.get( privacyItems.size() - 1 ); //Get Last Item (Default Item allways must be the last one.)
                if ( defItem.getType() == null ) { // Is this Item default ?
                    //Yep this item is default get it rules.
                    allowByDefault = defItem.isAllow();
                }
                privacyItems.remove(defItem); // Removing Default Item, It will be added before save
            }
        } catch(XMPPException ex) {
            if ( ex.getXMPPError().getCode() == 404 ) { // List not found. Let's create it.
                createList(); // Creating new PrivacyList.
            }
            Log.error(ex); // Log all errors
        }
        
    }

    /**
     * Returns new created Default PrivacyItem like <code><item action='allow' order='1'/></code>.
     * With order of the last item into Items list + 1
     */
    protected PrivacyItem createDefaultPrivacyItem() {
        return new PrivacyItem(null, getDefaultListAllow(), privacyItems.size());
    }

    /**
     * Returns new created Default PrivacyItem like <code><item action='allow' order='1'/></code>.
     * With order seted here.
     *
     * @param order order of default PrivacyItem
     */
    protected PrivacyItem createDefaultPrivacyItem(int order) {
        return new PrivacyItem(null, getDefaultListAllow(), order);
    }

    /**
     * Search privancyItem using Type & value
     * 
     * @param type type of privacy item
     * @param value value of item
     * @return privacyItem id of item into PrivacyItems or -1 on Item not found
     */
    protected int searchPrivacyItem(String value) {
        for (PrivacyItem privacyItem : getPrivacyItems()) {
            if ( privacyItem.getValue().equalsIgnoreCase(value)) {
                return privacyItem.getOrder();
            }
        }
        return -1; //error
    }

    /**
     * Init Privacy list
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
     * @param type items presence type
     */
    public void addPrivacyItem(PrivacyItem.Type type, String value, boolean allow) throws XMPPException {
        boolean itemsDeleted = false;
        if (getPrivacyItems().size() < 1) {
            itemsDeleted = false;
        }
        //If somewhere in list are more than one item
        while ( !itemsDeleted ) {
            int itemId = searchPrivacyItem(value);
            if ( itemId >= 0 ) {
                getPrivacyItems().remove(itemId);//Removing old item
            } else {
                itemsDeleted = true;
            }
        }

        PrivacyItem item = new PrivacyItem(type.name(), allow, getPrivacyItems().size());
        item.setValue(value);
        getPrivacyItems().add(item);
    }
    
    /**
     * Add New privacy item without saving list
     *
     * @param item new privacyItem
     */
    public void addPrivacyItem(PrivacyItem item) throws XMPPException {
        boolean itemsDeleted = false;
        if ( item.getValue() == null || item.getValue().isEmpty() ) {
            throw new PrivacyException("Ietm must contain JID!");
        }
        if (getPrivacyItems().size() < 1) {
            itemsDeleted = false;
        }
        //If somewhere in list are more than one item
        while ( !itemsDeleted ) {
            int itemId = searchPrivacyItem(item.getValue());
            if ( itemId >= 0 ) {
                getPrivacyItems().remove(itemId);//Removing old item
            } else {
                itemsDeleted = true;
            }
        }
        getPrivacyItems().add(item);
    }

    /**
     * Removes all Items that are contained in the specified list
     * @param list
     */
    public void removePrivacyItems(List<PrivacyItem> list) {
        getPrivacyItems().removeAll(list);
    }

    /**
     * Add List of Items into Privacy List
     *
     * @param list
     */
    public void addPrivacyItems(List<PrivacyItem> list) {
        getPrivacyItems().addAll(list);
    }
    
    /**
     * Removes PrivacyItem From list without saving it
     * @param type
     * @param value
     * @throws XMPPException
     */
    public void removePrivacyItem(PrivacyItem.Type type, String value) throws XMPPException {
        removePrivacyItem(type, value, false);
    }

    /**
     * Removes PrivacyItem From list
     *
     * @param type type of privacy item
     * @param value value of item
     * @param save save privacy List or not
     */
    public void removePrivacyItem(PrivacyItem.Type type, String value, boolean save) throws XMPPException {
        boolean itemsDeleted = false;
        if ( getPrivacyItems().size() < 1 ) {
            return;
        }
        //If somewhere in list are more than one item
        while ( !itemsDeleted ) {
            int itemFound = searchPrivacyItem(value);
            if ( itemFound >= 0 ) {
                getPrivacyItems().remove(itemFound);
            } else {
                itemsDeleted = true;
            }
        }
        if ( save ) { // save list
            save();
        }
    }

    /**
     * Remove item using it's order
     * @param order
     * @throws XMPPException
     */
    public void delPrivacyItem(int order) throws XMPPException {
        if ( getPrivacyItems().size() < 1 ) {
            return;
        }
        if ( order > (getPrivacyItems().size() - 1) ) {
            throw new PrivacyException("Order is out of range.");
        }
        getPrivacyItems().remove(order);
    }

    /**
     * Is PrivacyList Active
     * 
     * @return is This list active or not
     */
    public boolean isActive() {
        try {
            return PrivacyManager.getInstance().getPrivacyListManager().getPrivacyList(getListName()).isActiveList();
        } catch (XMPPException ex) {
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
            return PrivacyManager.getInstance().getPrivacyListManager().getPrivacyList(getListName()).isDefaultList();
        } catch (XMPPException ex) {
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
    public boolean getDefaultListAllow() {
        return allowByDefault;
    }

    /**
     * Set the action associated with the items, it MUST be filled and will allow or deny
     * the communication by default
     *
     */
    public void setDefaultListAllow(boolean allow) {
        allowByDefault = allow;
    }

    /**
     * Create an empty privacy list if it didn't created
     * 
     * @see org.jivesoftware.sparkimpl.plugin.privacy.PrivacyManager#createList(java.lang.String, java.util.List)
     */
    public void createList() throws XMPPException {
        getPrivacyItems().clear(); //Clear list if it exist
        PrivacyItem defItem = createDefaultPrivacyItem();
        getPrivacyItems().add(defItem); //Add default privacy action
        PrivacyManager.getInstance().getPrivacyListManager().createPrivacyList(getListName(), getPrivacyItems());
        getPrivacyItems().remove(defItem); // remove Default item it will be added before save
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



}
