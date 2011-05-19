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
package org.jivesoftware.sparkimpl.plugin.privacy.ui;

import javax.swing.tree.DefaultMutableTreeNode;
import org.jivesoftware.smack.packet.PrivacyItem;
import org.jivesoftware.sparkimpl.plugin.privacy.PrivacyManager;
import org.jivesoftware.sparkimpl.plugin.privacy.list.SparkPrivacyList;

/**
 * Class to handle privacy tree nodes
 * @author Bergunde Holger
 */
public class PrivacyTreeNode extends DefaultMutableTreeNode {

    
    private static final long serialVersionUID = -8723928570664159522L;
    private boolean _isContactGroup = false;
    private PrivacyItem _item = null;
    private SparkPrivacyList _list = null;
    private boolean _isPlaceHolder = false;
    private boolean _isGroupGroup = false;
    private boolean _isDefault = false;
    private boolean _isActive = false;
    private PrivacyManager _pmanager = PrivacyManager.getInstance(); 

    /**
     * Creates a Node with a reference to the SparkPrivacyList and changes the
     * Name to list's name
     * 
     * @param list
     *            the list the Node will refer to
     */
    public PrivacyTreeNode(SparkPrivacyList list) {
	_list = list;
	_isActive = _list.isActive();
	_isDefault = _list.isDefault();
    }

    /**
     * Creates a Node with a String. Used for creating top node or nodes for a
     * better structured view, like Contact nodes or Group nodes
     * 
     * @param nodeName
     *            the name of the node
     */
    public PrivacyTreeNode(String nodeName) {
	super(nodeName);
	_isPlaceHolder = true;
    }

    /**
     * Creates a Node with a reference to the PrivacyItem and changes the Name
     * to item's name
     * 
     * @param item
     *            the privacyItem which should be displayed as a node
     */
    public PrivacyTreeNode(PrivacyItem item) {
	_item = item;
    }

    /**
     * Returns if this node has a reference to the currently active list
     * 
     * @return true if this list is the active list
     */
    public boolean isActiveList() {
	return _isActive;
    }

    /**
     * Returns if this node has a reference to the default list
     * 
     * @return true if this list is the default list
     */
    public boolean isDefaultList() {
	return _isDefault;
    }

    /**
     * Set the list as active list.
     * 
     */
    public void setListAsActive() {
	_isActive = true;
	_pmanager.setListAsActive(_list.getListName());
    }

    /**
     * Set the list as default list
     * 
     */
    public void setListAsDefault() {
	_isDefault = true;
	_pmanager.setListAsDefault(_list.getListName());
	

    }

    /**
     * Check if the current node is the parent node for all contacts
     * 
     * @return true if this node represents the contact node
     */
    public boolean isContactGroup() {
	return _isContactGroup;
    }

    /**
     * Set this node as the contact node or not.
     * 
     * @param bool
     */
    public void setisContactGroup(boolean bool) {
	this._isContactGroup = bool;
	this._isPlaceHolder = bool;
    }

    /**
     * Check if the current node is the parent node for all groups
     * 
     * @return true if this node represents the group node
     */
    public boolean isGroupNode() {
	return _isGroupGroup;
    }

    /**
     * Set this node as the group node or not.
     * 
     * @param bool
     */
    public void setisGroupNode(boolean bool) {
	this._isGroupGroup = bool;
	this._isPlaceHolder = bool;
    }

    /**
     * Get the pricayList this node refers to
     * 
     * @return the privacylist if node represents a privayList, if not null
     */
    public SparkPrivacyList getPrivacyList() {
	return _list;
    }

    /**
     * Get the privacyItem this node refers to
     * 
     * @return the privacyitem if node represents a privacyItem, if not null
     */
    public PrivacyItem getPrivacyItem() {
	return _item;
    }

    /**
     * Check if this node represents a PrivacyList
     * 
     * @return true if node has reference to privacyList, if not null
     */
    public boolean isPrivacyList() {
	return _list != null;
    }

    /**
     * Check if this node represents a PrivacyItem
     * 
     * @return true if node has reference to PrivacyItem, if not null
     */
    public boolean isPrivacyItem() {
	return _item != null;
    }

    /**
     * Check if this node is for structured reasons without any reference to
     * privacyItem e.g. (like GroupNode, ContactNode)
     * 
     * @return true if is structured node, if not null
     */
    public boolean isStructureNode() {
	return _isPlaceHolder;
    }
}
