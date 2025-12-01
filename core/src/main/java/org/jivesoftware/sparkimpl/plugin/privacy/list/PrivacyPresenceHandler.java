package org.jivesoftware.sparkimpl.plugin.privacy.list;



import java.util.Collection;

import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.StanzaBuilder;
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
     * @param jid the JID to send offline status
     * @throws SmackException.NotConnectedException if Spark is not connected
     */
    public void sendUnavailableTo(Jid jid) throws SmackException.NotConnectedException
    {
        Presence pack = StanzaBuilder.buildPresence()
            .ofType(Presence.Type.unavailable)
            .build();
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
     * @param jid the JID to send presence
     * @throws SmackException.NotConnectedException if Spark is not connected
     */
    public void sendRealPresenceTo(Jid jid) throws SmackException.NotConnectedException
    {
        Presence presence = SparkManager.getWorkspace().getStatusBar().getPresence(); 
        Presence pack = StanzaBuilder.buildPresence()
            .ofType(presence.getType())
            .setStatus(presence.getStatus())
            .setPriority(1)
            .setMode(presence.getMode())
            .build();
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
            setIconsForItem(pItem);
        }
        SparkManager.getContactList().updateUI();
    }

    private void setIconsForItem(PrivacyItem item) throws SmackException.NotConnectedException {
        if (item.getType().equals(PrivacyItem.Type.jid)) {
            Jid jid;
            try {
                jid = JidCreate.from(item.getValue());
            } catch (XmppStringprepException e) {
                throw new IllegalStateException(e);
            }
            setBlockedIconToContact(jid);
            if (item.isFilterPresenceOut()) {
                sendUnavailableTo(jid);
            }
        }

        if (item.getType().equals(PrivacyItem.Type.group)) {
            ContactGroup group = SparkManager.getWorkspace().getContactList().getContactGroup(item.getValue());
            for (ContactItem contact : group.getContactItems()) {
                setBlockedIconToContact(contact.getJid());
                if (item.isFilterPresenceOut()) {
                    sendUnavailableTo(contact.getJid());
                }
            }
        }
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
            removeIconsForItem(pItem);
        }
        SparkManager.getContactList().updateUI();
    }

    private void removeIconsForItem(PrivacyItem item) throws SmackException.NotConnectedException {
        if (item.getType().equals(PrivacyItem.Type.jid)) {
            Jid jid;
            try {
                jid = JidCreate.from(item.getValue());
            } catch (XmppStringprepException e) {
                throw new IllegalStateException(e);
            }
            removeBlockedIconFromContact(jid);
            if (item.isFilterPresenceOut()) {
                sendRealPresenceTo(jid);
            }
        }

        if (item.getType().equals(PrivacyItem.Type.group)) {
            ContactGroup group = SparkManager.getWorkspace().getContactList().getContactGroup(item.getValue());
            for (ContactItem contact : group.getContactItems()) {
                removeBlockedIconFromContact(contact.getJid());
                if (item.isFilterPresenceOut()) {
                    sendRealPresenceTo(contact.getJid());
                }
            }
        }
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
    public void itemAdded(PrivacyItem item, String listName) throws SmackException.NotConnectedException
    {
        if (PrivacyManager.getInstance().getPrivacyList(listName).isActive()) {
            setIconsForItem(item);
            SparkManager.getContactList().updateUI();
        }
    }

    @Override
    public void itemRemoved(PrivacyItem item, String listName) throws SmackException.NotConnectedException
    {
        if (PrivacyManager.getInstance().getPrivacyList(listName).isActive()) {
            removeIconsForItem(item);
            SparkManager.getContactList().updateUI();
        }
    }
}
