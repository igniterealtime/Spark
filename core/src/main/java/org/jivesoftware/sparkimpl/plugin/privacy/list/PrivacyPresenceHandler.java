package org.jivesoftware.sparkimpl.plugin.privacy.list;



import java.util.Collection;

import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.privacy.packet.PrivacyItem;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.ui.ContactGroup;
import org.jivesoftware.spark.ui.ContactItem;
import org.jivesoftware.sparkimpl.plugin.privacy.PrivacyManager;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;


/**
 * @author Bergunde Holger
 */

public class PrivacyPresenceHandler implements SparkPrivacyItemListener {

  

    /**
     * Send Unavailable (offline status) to jid .
     * 
     * @param jid
     *            JID to send offline status
     * @deprecated use {#link {@link #sendUnavailableTo(Jid)}} instead. 
     */
    @Deprecated
    public void sendUnavailableTo(String jidString) throws SmackException.NotConnectedException
    {
        Jid jid;
        try {
            jid = JidCreate.from(jidString);
        } catch (XmppStringprepException e) {
            throw new IllegalStateException(e);
        }
        sendUnavailableTo(jid);
    }

    /**
     * Send Unavailable (offline status) to jid .
     * 
     * @param jid
     *            JID to send offline status
     * @throws InterruptedException 
     */
    public void sendUnavailableTo(Jid jid) throws SmackException.NotConnectedException
    {
        Presence pack = new Presence(Presence.Type.unavailable);                                                  
        pack.setTo(jid);
        try {
            SparkManager.getConnection().sendStanza(pack);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Send my presence for user
     * 
     * @param jidString
     *            JID to send presence
     * @deprecated use {@link #sendRealPresenceTo(Jid)} instead.
     */
    @Deprecated
    public void sendRealPresenceTo(String jidString) throws SmackException.NotConnectedException
    {
        Jid jid;
        try {
            jid = JidCreate.from(jidString);
        } catch (XmppStringprepException e) {
            throw new IllegalStateException(e);
        }
        sendUnavailableTo(jid);
    }
    
    /**
     * Send my presence for user
     * 
     * @param jid
     *            JID to send presence
     */
    public void sendRealPresenceTo(Jid jid) throws SmackException.NotConnectedException
    {
        Presence presence = SparkManager.getWorkspace().getStatusBar().getPresence(); 
        Presence pack = new Presence(presence.getType(), presence.getStatus(), 1, presence.getMode()); 
        pack.setTo(jid);
        try {
            SparkManager.getConnection().sendStanza(pack);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

    public void setIconsForList(SparkPrivacyList list) throws SmackException.NotConnectedException
    {
        for (PrivacyItem pItem : list.getPrivacyItems()) {
            if (pItem.getType().equals(PrivacyItem.Type.jid)) {
                setBlockedIconToContact(pItem.getValue());
                if (pItem.isFilterPresenceOut()) {
                    sendUnavailableTo(pItem.getValue());
                }
            }

            if (pItem.getType().equals(PrivacyItem.Type.group)) {
                ContactGroup group = SparkManager.getWorkspace().getContactList().getContactGroup(pItem.getValue());
                for (ContactItem citem : group.getContactItems()) {
                    setBlockedIconToContact(citem.getJID());
                    if (pItem.isFilterPresenceOut()) {
                        sendUnavailableTo(citem.getJID());
                    }
                }

            }

        }
        SparkManager.getContactList().updateUI();
    }

    private void setBlockedIconToContact(CharSequence cs) {
        Jid jid = JidCreate.fromOrThrowUnchecked(cs);
        setBlockedIconToContact(jid);
    }

    private void setBlockedIconToContact(Jid jid) {
        Collection<ContactItem> items = SparkManager.getWorkspace().getContactList().getContactItemsByJID(jid);
        for (ContactItem contactItem : items) {
            if (contactItem != null) {
                contactItem.setSpecialIcon(SparkRes.getImageIcon("PRIVACY_ICON_SMALL"));
            }
        }
    }

    public void removeIconsForList(SparkPrivacyList list) throws SmackException.NotConnectedException
    {
        for (PrivacyItem pItem : list.getPrivacyItems()) {
            if (pItem.getType().equals(PrivacyItem.Type.jid)) {
                removeBlockedIconFromContact(pItem.getValue());
                if (pItem.isFilterPresenceOut()) {
                    sendRealPresenceTo(pItem.getValue());
                }
            }

            if (pItem.getType().equals(PrivacyItem.Type.group)) {
                ContactGroup group = SparkManager.getWorkspace().getContactList().getContactGroup(pItem.getValue());
                for (ContactItem citem : group.getContactItems()) {
                    removeBlockedIconFromContact(citem.getJid());
                    if (pItem.isFilterPresenceOut()) {
                        sendRealPresenceTo(citem.getJid());
                    }
                }
            }

        }
        SparkManager.getContactList().updateUI();
    }

    private void removeBlockedIconFromContact(CharSequence cs) {
        Jid jid = JidCreate.fromOrThrowUnchecked(cs);
        removeBlockedIconFromContact(jid);
    }

    private void removeBlockedIconFromContact(Jid jid) {
        Collection<ContactItem> items = SparkManager.getWorkspace().getContactList().getContactItemsByJID(jid); 
        for (ContactItem item : items) {
            if (item != null) {
                item.setSpecialIcon(null);
            }
        }

    }

    @Override
    public void itemAdded(PrivacyItem item, String listname) throws SmackException.NotConnectedException
    {
        PrivacyManager pmanager = PrivacyManager.getInstance();
        if (pmanager.getPrivacyList(listname).isActive()) {
            if (item.getType().equals(PrivacyItem.Type.jid)) {
                setBlockedIconToContact(item.getValue());
                if (item.isFilterPresenceOut()) {
                    sendUnavailableTo(item.getValue());
                }
            }

            if (item.getType().equals(PrivacyItem.Type.group)) {
                ContactGroup group = SparkManager.getWorkspace().getContactList().getContactGroup(item.getValue());
                for (ContactItem citem : group.getContactItems()) {
                    setBlockedIconToContact(citem.getJID());
                    if (item.isFilterPresenceOut()) {
                        sendUnavailableTo(citem.getJID());
                    }
                }

            }
            SparkManager.getContactList().updateUI();
        }
    }

    @Override
    public void itemRemoved(PrivacyItem item, String listname) throws SmackException.NotConnectedException
    {
        PrivacyManager pmanager = PrivacyManager.getInstance();
        if (pmanager.getPrivacyList(listname).isActive()) {
            if (item.getType().equals(PrivacyItem.Type.jid)) {
                removeBlockedIconFromContact(item.getValue());
                if (item.isFilterPresenceOut()) {
                    sendRealPresenceTo(item.getValue());
                }
            }

            if (item.getType().equals(PrivacyItem.Type.group)) {
                ContactGroup group = SparkManager.getWorkspace().getContactList().getContactGroup(item.getValue());
                for (ContactItem citem : group.getContactItems()) {
                    removeBlockedIconFromContact(citem.getJID());
                    if (item.isFilterPresenceOut()) {
                        sendRealPresenceTo(citem.getJID());
                    }
                }

            }
            SparkManager.getContactList().updateUI();
        }

    }

}
