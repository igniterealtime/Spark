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
package org.jivesoftware.sparkimpl.plugin.privacy;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo.Feature;
import org.jivesoftware.smackx.privacy.PrivacyList;
import org.jivesoftware.smackx.privacy.PrivacyListManager;
import org.jivesoftware.smackx.privacy.packet.PrivacyItem;
import org.jivesoftware.spark.PresenceManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.privacy.list.PrivacyPresenceHandler;
import org.jivesoftware.sparkimpl.plugin.privacy.list.SparkPrivacyList;
import org.jivesoftware.sparkimpl.plugin.privacy.list.SparkPrivacyListListener;

import java.util.*;


/**
 * @author Zolotarev Konstantin, Bergunde Holger
 */
public class PrivacyManager {

    private static PrivacyManager singleton;
    private static final Object LOCK = new Object();

    // XEP-0126. To help ensure cross-client compatibility, 
    // it is RECOMMENDED to use the privacy list names "visible" and "invisible" 
    // for simple global visibility and invisibility respectively. 
    // It is also RECOMMENDED to use list names of the form "visible-to-GroupName" 
    // and "invisible-to-JID" for simple lists that implement visibility or invisibility 
    // with regard to roster groups and JIDs. Obviously list names could become rather complex, 
    // such as "visible-to-Group1 Group2 Group3".
    private static final String INVISIBLE_LIST_NAME = "invisible";
    private List<SparkPrivacyList> _privacyLists = new ArrayList<>();
    private PrivacyListManager privacyManager;
    private PrivacyPresenceHandler _presenceHandler = new PrivacyPresenceHandler();
    private Set<SparkPrivacyListListener> _listListeners = new HashSet<>();
    private boolean _active = false;
    private SparkPrivacyList previousActiveList;

    /**
     * Creating PrivacyListManager instance
     */
    private PrivacyManager() {
        XMPPConnection conn = SparkManager.getConnection();
        if (conn == null) {
            Log.error("Privacy plugin: Connection not initialized.");
        }

       _active = checkIfPrivacyIsSupported(conn);
    
        if (_active)
        {
            privacyManager = PrivacyListManager.getInstanceFor(conn);
            initializePrivacyLists();
        }
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

    
    private boolean checkIfPrivacyIsSupported(XMPPConnection conn) {
    	ServiceDiscoveryManager servDisc = ServiceDiscoveryManager.getInstanceFor(conn);
        DiscoverInfo info = null;
    	try {
    		info = servDisc.discoverInfo(conn.getServiceName());
        } catch (XMPPException | SmackException e) {
            	// We could not query the server
        }
        if (info != null) {
            for ( final Feature feature : info.getFeatures() ) {
                if (feature.getVar().contains("jabber:iq:privacy")) {
                    return true;
                }
            }
        } 
        return false;
    }
    
    private void initializePrivacyLists()
    {
        try {
            List<PrivacyList> lists = privacyManager.getPrivacyLists();
            
            for (PrivacyList list: lists)
            {
               SparkPrivacyList sparkList = new SparkPrivacyList(list);
               sparkList.addSparkPrivacyListener(_presenceHandler);
               if (!isListHidden(sparkList))
                   _privacyLists.add(sparkList);
            }  
        } catch (XMPPException | SmackException e) {
            Log.error("Could not load PrivacyLists", e);
        }
        
        if(hasDefaultList())
        {
            setListAsActive(getDefaultList().getListName());
        }
    }
    
    
    
    public void removePrivacyList(String listName) {
        try {
            privacyManager.deletePrivacyList(listName);
           
            _privacyLists.remove(getPrivacyList(listName));
        } catch (XMPPException | SmackException e) {
            Log.warning("Could not remove PrivacyList " + listName, e);
        }
    }


    /**
     * Check for active list existence
     * 
     * @return boolean
     */
    public boolean hasActiveList() {
        for (SparkPrivacyList list: _privacyLists)
        {
            if (list.isActive())
                return true;
        }
        return false;
    }

    /**
     * Returns the active PrivacyList
     * 
     * @return the list if there is one, else null
     */
    public SparkPrivacyList getActiveList() {
        for (SparkPrivacyList list: _privacyLists)
        {
            if (list.isActive())
                return list;
        }
        return null;
    }


    /**
     * Returns the sparkprivacylist that the manager keeps local, to get updated
     * version try to forcereloadlists
     * 
     * @param s
     *            the name of the list
     * @return SparkPrivacyList
     */
    public SparkPrivacyList getPrivacyList(String s) {
        for (SparkPrivacyList list: _privacyLists)
        {
            if (list.getListName().equals(s))
                return list;
        }
        return createPrivacyList(s);
    }

    /**
     * Check if active list exist
     * 
     * @return boolean
     */
    public boolean hasDefaultList() {
        for (SparkPrivacyList list: _privacyLists)
        {
            if (list.isDefault())
                return true;
        }
        return false;
    }

    public SparkPrivacyList getDefaultList()
    {
        for (SparkPrivacyList list: _privacyLists)
        {
            if (list.isDefault())
                return list;
        }
        return null;
    }
    

    /**
     * Get <code>org.jivesoftware.smackx.privacy.PrivacyListManager</code> instance
     * 
     * @return PrivacyListManager
     */
    public PrivacyListManager getPrivacyListManager() {
        return privacyManager;
    }

    public SparkPrivacyList createPrivacyList(String listName) {
        PrivacyItem item = new PrivacyItem(true,999999);
        ArrayList<PrivacyItem> items = new ArrayList<>();
        items.add(item);
        SparkPrivacyList sparklist = null;
        try {
            privacyManager.createPrivacyList(listName, items);
            privacyManager.getPrivacyList(listName).getItems().remove(item);
            sparklist = new SparkPrivacyList(privacyManager.getPrivacyList(listName));
            _privacyLists.add(sparklist);
            sparklist.addSparkPrivacyListener(_presenceHandler);
        } catch (XMPPException | SmackException e) {
            Log.warning("Could not create PrivacyList "+listName, e);
        }
        
        return sparklist;
        
    }

    /**
     * The server can store different privacylists. This method will return the
     * names of the lists, currently available on the server
     * 
     * @return All Listnames
     */






    public List<SparkPrivacyList> getPrivacyLists() {
        return new ArrayList<>( _privacyLists );
    }

    
    
    public void setListAsActive(String listname)
    {
        try {
            privacyManager.setActiveListName(listname);
            fireListActivated(listname);
            if (hasActiveList())
            {
                _presenceHandler.removeIconsForList(getActiveList());
            }
            getPrivacyList(listname).setListAsActive(true);
            for (SparkPrivacyList plist : _privacyLists) {
                if (!plist.getListName().equals(listname))
                    plist.setListAsActive(false);
            }
            _presenceHandler.setIconsForList(getActiveList());
            
        } catch (XMPPException | SmackException e) {
            Log.warning("Could not activate PrivacyList " + listname, e);
        }
    }
    
    public void setListAsDefault(String listname) {

        try {
            privacyManager.setDefaultListName(listname);
            fireListSetAsDefault(listname);
            getPrivacyList(listname).setListIsDefault(true);
            for (SparkPrivacyList plist : _privacyLists) {
                if (!plist.getListName().equals(listname))
                    plist.setListIsDefault(false);
            }
        } catch (XMPPException | SmackException e) {
            Log.warning("Could not set PrivacyList " + listname+" as default", e);
        }

    }
    
    public void declineActiveList()
    {
        try {
            
            if(hasActiveList())
            {
                privacyManager.declineActiveList();
                fireListDeActivated(getActiveList().getListName());
                _presenceHandler.removeIconsForList(getActiveList());
            }
            for (SparkPrivacyList plist : _privacyLists) {
                plist.setListAsActive(false);
            }
        } catch (XMPPException | SmackException e) {
            Log.warning("Could not decline active privacy list", e);
        }    
    }
    
    public void declineDefaultList()
    {
        try {
            if (hasDefaultList())
            {
                privacyManager.declineDefaultList();
                fireListRemovedAsDefault(getDefaultList().getListName());
                for (SparkPrivacyList plist : _privacyLists) {
                    plist.setListIsDefault(false);
                }
            }
        } catch (XMPPException | SmackException e) {
            Log.warning("Could not decline default privacy list", e);
        }    
    }
    
    public boolean isPrivacyActive()
    {
        return _active ;
    }
    
    public void addListListener (SparkPrivacyListListener listener)
    {
        _listListeners.add(listener);
    }
    
    public void deleteListListener (SparkPrivacyListListener listener)
    {
        _listListeners.remove(listener);
    }

    private void fireListActivated( String listName )
    {
        for ( final SparkPrivacyListListener listener : _listListeners )
        {
            try
            {
                listener.listActivated( listName );
            }
            catch ( Exception e )
            {
                Log.error( "A SparkPrivacyListListener (" + listener + ") threw an exception while processing a 'listActivated' event for: " + listName, e );
            }
        }
    }

    private void fireListDeActivated( String listName )
    {
        for ( final SparkPrivacyListListener listener : _listListeners )
        {
            try
            {
                listener.listDeActivated( listName );
            }
            catch ( Exception e )
            {
                Log.error( "A SparkPrivacyListListener (" + listener + ") threw an exception while processing a 'listDeActivated' event for: " + listName, e );
            }
        }
    }

    private void fireListSetAsDefault( String listName )
    {
        for ( final SparkPrivacyListListener listener : _listListeners )
        {
            try
            {
                listener.listSetAsDefault( listName );
            }
            catch ( Exception e )
            {
                Log.error( "A SparkPrivacyListListener (" + listener + ") threw an exception while processing a 'listSetAsDefault' event for: " + listName, e );
            }
        }
    }

    private void fireListRemovedAsDefault( String listName )
    {
        for ( final SparkPrivacyListListener listener : _listListeners )
        {
            try
            {
                listener.listRemovedAsDefault( listName );
            }
            catch ( Exception e )
            {
                Log.error( "A SparkPrivacyListListener (" + listener + ") threw an exception while processing a 'listRemovedAsDefault' event for: " + listName, e );
            }
        }
    }
    
    public void goToInvisible() 
    {
    	if (!_active)
    		return;
    	
        ensureGloballyInvisibleListExists();
        // make it active
        activateGloballyInvisibleList();
    }
    
    public void goToVisible() 
    {
    	if (!_active)
    		return;
    	
        try {
            if (!isGloballyInvisibleListActive()) 
                return;
            
            privacyManager.declineActiveList();
            SparkManager.getConnection().sendStanza(PresenceManager.getAvailablePresence());
            Log.debug("List \"" + INVISIBLE_LIST_NAME + "\" has been disabled ");
            if (previousActiveList != null) {
                setListAsActive(previousActiveList.getListName());
                Log.debug("List \"" + previousActiveList.getListName() + "\" has been activated instead. ");
            }
            
        } catch (Exception e) {
        	Log.error("PrivacyManager#goToVisible: ", e);
        }
    }
    
    public void activateGloballyInvisibleList() {
    	if (!_active)
    		return;
    	
        if (!SparkManager.getConnection().isConnected() || isGloballyInvisibleListActive()) 
            return;
        
        try {
            previousActiveList = getActiveList();
            privacyManager.setActiveListName(INVISIBLE_LIST_NAME);
            SparkManager.getConnection().sendStanza(PresenceManager.getAvailablePresence());
            Log.debug("List \"" + INVISIBLE_LIST_NAME + "\" has been activated ");
        } catch (Exception e) {
        	Log.error("PrivacyManager#activateGloballyInvisibleList: ", e);
        }
    }
   
    public static boolean isListHidden(SparkPrivacyList list) {
        return list != null && INVISIBLE_LIST_NAME.equalsIgnoreCase(list.getListName());
    }
    
    public boolean isGloballyInvisibleListActive() {
    	if (!_active)
    		return false; 
    	
    	try {
    		PrivacyList pl = privacyManager.getActiveList();
    		return pl != null && INVISIBLE_LIST_NAME.equalsIgnoreCase(pl.toString());
    	} catch (Exception e){
           // it can return item-not-found if there is no active list.
           // so it is fine to fall here.
    		Log.error("PrivacyManager#isGloballyInvisibleListActive: ", e);
       }
       return false;
    }
    
    private PrivacyList ensureGloballyInvisibleListExists() {
    	if (!_active)
    		return null; 
    	
        PrivacyList list = null;
        try 
        {
            list = privacyManager.getPrivacyList(INVISIBLE_LIST_NAME);
            if (list != null)
                return list;
            
        } catch (XMPPException | SmackException e1) {
            Log.debug("PrivacyManager#ensureGloballyInvisibleListExists: Could not find globally invisible list. We need to create one");
        }

        try {
            PrivacyItem item = new PrivacyItem(false, 1);
            item.setFilterPresenceOut(true);

            List<PrivacyItem> items = Arrays.asList(item);
            privacyManager.createPrivacyList(INVISIBLE_LIST_NAME, items);
            list = privacyManager.getPrivacyList(INVISIBLE_LIST_NAME);
            Log.debug("List \"" + INVISIBLE_LIST_NAME + "\" has been created ");
        } 
        catch (XMPPException | SmackException e)
        {
            Log.warning("PrivacyManager#ensureGloballyInvisibleListExists: Could not create PrivacyList " + INVISIBLE_LIST_NAME, e);
        }
        
        return list;
    }
}
