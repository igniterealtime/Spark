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

/**
 *
 * @author Zolotarev Konstantin
 */
public interface PrivacyListBlackListListener {

    /**
     * New Item added into blockList
     * @param jid Users jid
     */
    public void addedBlockedItem(String jid);

    /**
     * User removed from BlockList
     * @param jid users jid
     */
    public void removedBlockedItem(String jid);

}
