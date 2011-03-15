/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jivesoftware.sparkimpl.plugin.privacy.list;

/**
 *
 * @author Zolotarev Konstantin
 */
public interface SparkPrivacyListListener {

    /**
     * New Item added into PrivacyList
     *
     * @param jid privacyItem jid
     */
    public void itemAdded(String jid);

    /**
     * Item removed from PrivacyList
     * 
     * @param jid privacyItem jid
     */
    public void itemRemoved(String jid);
    
}
