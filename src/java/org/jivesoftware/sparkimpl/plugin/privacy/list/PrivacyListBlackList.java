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

import java.util.List;
import org.jivesoftware.smack.packet.PrivacyItem;


/**
 * PrivacyList will contain all items that allways must be blocked.
 * 
 * @author Zolotarev Konstantin
 */
public class PrivacyListBlackList extends SparkPrivacyList {

    /**
     * Create name of list
     */
    public PrivacyListBlackList() {
        super("spark:blackList");
        

    }

    /**
     * 
     * @return list of blocked items
     */
    public List<PrivacyItem> getBlockedItems() {
        return privacyItems;
    }

    /**
     * Prepare privacy item for this list
     * 
     * @param jid prepared users jid
     * @return newly created Privacy item
     */
    @Override
    public PrivacyItem prepareItem(String jid) {
        // Creating new Privacy Item
        PrivacyItem newItem = new PrivacyItem(PrivacyItem.Type.jid.name(), false, getNewItemOrder());
        newItem.setValue(jid);
        newItem.setFilterMessage(true); // Locking only messages from contact
        newItem.setFilterPresence_out(true); // And hiding from blocked contact
        return newItem;
    }


}
