/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jivesoftware.sparkimpl.plugin.privacy.list;

import org.jivesoftware.smack.packet.PrivacyItem;

/**
 *
 * @author Zolotarev Konstantin, Bergunde Holger
 */
public interface SparkPrivacyItemListener {

    /**
     * New Item added into PrivacyList
     *
     * @param jid privacyItem jid
     */
    public void itemAdded(PrivacyItem item, String listname);

    /**
     * Item removed from PrivacyList
     * 
     * @param jid privacyItem jid
     */
    public void itemRemoved(PrivacyItem item, String listname);
   
}
