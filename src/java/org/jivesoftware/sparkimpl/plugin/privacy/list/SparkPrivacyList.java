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
package org.jivesoftware.sparkimpl.plugin.privacy.list;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.privacy.PrivacyList;
import org.jivesoftware.smackx.privacy.packet.PrivacyItem;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.privacy.PrivacyManager;

import java.util.*;

/**
 *
 * @author Zolotarev Konstantin, Bergunde Holger
 */
public class SparkPrivacyList {

    /**
     * List name will be used to identify PrivacyList
     */
    private String _listName   = "";
    private boolean _isActive = false;
    private boolean _isDefault = false;
    private List<PrivacyItem> _privacyItems = new LinkedList<>();
    private PrivacyList _myPrivacyList;
    private final Set<SparkPrivacyItemListener> _listeners = new HashSet<>();
    /**
     * Action associated with the items, it MUST be filled and will allow or deny
     * the communication by default
     */


    public SparkPrivacyList(PrivacyList list) throws SmackException.NotConnectedException
    {
        _listName = list.toString();
        _myPrivacyList = list;
        _isActive = _myPrivacyList.isActiveList();
        _isDefault = _myPrivacyList.isDefaultList();
        loadItems();
    }

    
    private void loadItems() throws SmackException.NotConnectedException
    {
       List<PrivacyItem> itemList = _myPrivacyList.getItems();
       
       for (PrivacyItem item: itemList)
       {
           if (item.getValue() == null || item.getType() == null)
               removeItem(item);
           else
           _privacyItems.add(item);
       }   
    }


    /**
     * Get maximal Order value from PrivacyItemList
     *
     * @return
     */
    private long getMaxItemOrder() {
        if(getLastItem() != null) {
            return getLastItem().getOrder();
        }
        return 1;
    }
 
    /**
     * Checks is jid already blocked
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
     * Get last PrivacyItem from list ordered by PrivacyItem.order
     *
     * @return last PrivacyItem ordered by Item order
     */
    public PrivacyItem getLastItem() {
        long order = 0;
        PrivacyItem item = null;
        for (PrivacyItem privacyItem : _privacyItems) {
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
    public long getNewItemOrder() {
        return (getMaxItemOrder()+1);
    }

    /**
     * Search privancyItem using Type & value
     * 
     * @param value value of item
     * @return privacyItem or null if Item not found
     */
    private PrivacyItem searchPrivacyItem(String value) {
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
    //TODO REMOVE
    @SuppressWarnings("unused")
    private PrivacyItem searchPrivacyItem(PrivacyItem.Type type, String value) {
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
    public ArrayList<PrivacyItem> searchPrivacyItems(PrivacyItem.Type type, String value) {
        ArrayList<PrivacyItem> items = new ArrayList<>();
        for (PrivacyItem privacyItem : getPrivacyItems()) {
            if ( privacyItem.getValue().equalsIgnoreCase(value) && privacyItem.getType() == type ) {
                items.add(privacyItem);
            }
        }
        return items; //error
    }

    
    public void addItem (PrivacyItem item) throws SmackException.NotConnectedException
    {
        _privacyItems.add(item);
        fireItemAdded(item);
    }

    
    public void removeItem(PrivacyItem item) throws SmackException.NotConnectedException
    {
        _privacyItems.remove(item);
        fireItemRemoved(item);
    }
    
    public void removeItem(String name) throws SmackException.NotConnectedException
    {
        List<PrivacyItem> tempList = new ArrayList<>( _privacyItems );
        for (PrivacyItem item: tempList)
        {
            if (item.getValue().equals(name))
            {
                _privacyItems.remove(item);
                fireItemRemoved(item);
            }
        }
    }
    
    /**
     * Returns Privasy List name
     * @return PrivacyList name
     */
    public String getListName() {
        return _listName;
    }

    /**
     * Answer the privacy list items with the allowed and blocked permissions.
     * @return list items
     */
    public ArrayList<PrivacyItem> getPrivacyItems() {
        return new ArrayList<>( _privacyItems );
    }


    /**
     * Is PrivacyList Active
     * 
     * @return is This list active or not
     */
    public boolean isActive() {
        return _isActive;
    }

    /**
     * Is PrivacyList set as default
     *
     * @return is this list default
     */
    public boolean isDefault() {
       return _isDefault;
    }

    /**
     * Set PrivacyList as active on server
     * @param active 
     * @throws XMPPException
     */
    public void setListAsActive(boolean active)
    {
       _isActive = active;
//       if (active)
//       {
//           fireListActivated();
//       }

    }







    /**
     * Store PrivacyList on server
     * 
     * @throws XMPPException
     */
    public void save() {
        try {
            PrivacyItem item = new PrivacyItem(true,999999);
           _privacyItems.add(item);
            PrivacyManager.getInstance().getPrivacyListManager().updatePrivacyList(getListName(), _privacyItems);
            PrivacyManager.getInstance().getPrivacyListManager().getPrivacyList(_listName).getItems().remove(item);
            _privacyItems.remove(item);
        } catch (XMPPException | SmackException e) {
            Log.warning("Could not save PrivacyList "+_listName);
            e.printStackTrace();
        }
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
     * @param item user was added into blockList
     */
    private void fireItemAdded( PrivacyItem item ) throws SmackException.NotConnectedException
    {
        for ( final SparkPrivacyItemListener listener : _listeners )
        {
            try
            {
                listener.itemAdded( item, _listName );
            }
            catch ( Exception e )
            {
                Log.error( "A SparkPrivacyItemListener (" + listener + ") threw an exception while processing a 'itemAdded' event for: " + item + " on list: " + _listName, e );
            }
        }
    }

    /**
     * @param item user removed from blackList
     */
    private void fireItemRemoved( PrivacyItem item ) throws SmackException.NotConnectedException
    {
        for ( final SparkPrivacyItemListener listener : _listeners )
        {
            try
            {
                listener.itemRemoved( item, _listName );
            }
            catch ( Exception e )
            {
                Log.error( "A SparkPrivacyItemListener (" + listener + ") threw an exception while processing a 'itemRemoved' event for: " + item + " on list: " + _listName, e );
            }
        }
    }

    
    public void addSparkPrivacyListener(SparkPrivacyItemListener listener)
    {
        _listeners.add(listener);
    }
    
    public void removeSparkPrivacyListener(SparkPrivacyItemListener listener)
    {
        _listeners.remove(listener);
    }


    public void setListIsDefault(boolean b) {
       _isDefault = b;   
    }

}
