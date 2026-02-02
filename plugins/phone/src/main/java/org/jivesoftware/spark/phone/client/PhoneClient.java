package org.jivesoftware.spark.phone.client;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.spark.phone.client.event.BasePhoneEventListener;
import org.jivesoftware.sparkimpl.plugin.phone.PhonePlugin;
import org.jxmpp.jid.BareJid;

import java.util.concurrent.CopyOnWriteArrayList;

public class PhoneClient {
    private final XMPPConnection connection;
    private final CopyOnWriteArrayList<BasePhoneEventListener> listeners = new CopyOnWriteArrayList();

    public PhoneClient(XMPPConnection conn) {
        this.connection = conn;
    }

    public boolean isPhoneEnabled(BareJid bareJid) {
        return true;
    }

    public void addEventListener(BasePhoneEventListener phoneListener) {
        listeners.addIfAbsent(phoneListener);
    }

    public void dialByExtension(String number) {
    }

    public void dialByJID(BareJid jid) {
    }
}
