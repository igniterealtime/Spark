/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jivesoftware.sparkimpl.plugin.privacy.list;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smackx.privacy.packet.PrivacyItem;

/**
 *
 * @author Zolotarev Konstantin, Bergunde Holger
 */
public interface SparkPrivacyItemListener {

    /**
     * New Item added into PrivacyList
     *
     * @param item privacyItem jid
     * @param listname name of the privacy list.
     */
    void itemAdded(PrivacyItem item, String listname) throws SmackException.NotConnectedException;

    /**
     * Item removed from PrivacyList
     *
     * @param item privacyItem jid
     * @param listname name of the privacy list.
     */
    void itemRemoved(PrivacyItem item, String listname) throws SmackException.NotConnectedException;
   
}
