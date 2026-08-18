package org.jivesoftware.spark.phone.client;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.StanzaExtensionFilter;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo;
import org.jivesoftware.smackx.disco.packet.DiscoverItems;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.phone.client.action.PhoneActionIQ;
import org.jivesoftware.spark.phone.client.event.BasePhoneEventListener;
import org.jivesoftware.spark.phone.client.event.HangUpEvent;
import org.jivesoftware.spark.phone.client.event.OnPhoneEvent;
import org.jivesoftware.spark.phone.client.event.PhoneEventExtension;
import org.jivesoftware.spark.phone.client.event.RingEvent;
import org.jivesoftware.spark.util.log.Log;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.Jid;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Client for the Asterisk-IM Openfire plugin.
 * <p>
 * Construction is deliberately strict: it fails unless the server publishes a 'phone'
 * component <em>and</em> the logged-in user has phone support (that is, the user is mapped
 * to a device on the plugin's Phone Mappings page). {@link org.jivesoftware.sparkimpl.plugin.phone.PhonePlugin}
 * relies on that to decide whether to show any phone UI at all.
 */
public class PhoneClient {
    /** Disco item name published by the Asterisk-IM plugin. */
    private static final String COMPONENT_NAME = "phone";

    /** Disco feature that marks a user as phone-enabled. */
    private static final String PHONE_FEATURE = "http://jivesoftware.com/phone";

    private final XMPPConnection connection;
    private final Jid component;
    private final CopyOnWriteArrayList<BasePhoneEventListener> listeners = new CopyOnWriteArrayList<>();

    public PhoneClient(XMPPConnection conn) throws XMPPException, SmackException, InterruptedException {
        if (!conn.isAuthenticated()) {
            throw new SmackException.SmackMessageException("Connection is not authenticated!");
        }
        this.connection = conn;
        final ServiceDiscoveryManager discoveryManager = ServiceDiscoveryManager.getInstanceFor(conn);
        component = findDiscoComponent();
        String node = conn.getUser().getLocalpart().asUnescapedString();
        DiscoverInfo info = discoveryManager.discoverInfo(component, node);
        if (!info.containsFeature(PHONE_FEATURE)) {
            throw new SmackException.SmackMessageException("User does not have phone support");
        }

        conn.addAsyncStanzaListener(new PhoneEventStanzaListener(),
                new StanzaExtensionFilter(PhoneEventExtension.ELEMENT, PhoneEventExtension.NAMESPACE));
    }

    private Jid findDiscoComponent() throws SmackException.SmackMessageException {
        Map<Jid, DiscoverItems.Item> discoItems = SparkManager.getSessionManager().getDiscoveredItems();
        for (var entry : discoItems.entrySet()) {
            DiscoverItems.Item item = entry.getValue();
            if (COMPONENT_NAME.equals(item.getName())) {
                Jid found = item.getEntityID();
                return found;
            }
        }
        throw new SmackException.SmackMessageException("Server does not have phone services");
    }

    /**
     * Returns true when the given user is mapped to a device, and can therefore be called.
     */
    public boolean isPhoneEnabled(BareJid bareJid) throws XMPPException, SmackException, InterruptedException {
        final DiscoverInfo info = ServiceDiscoveryManager.getInstanceFor(connection)
                .discoverInfo(component, bareJid.toString());
        return info.containsFeature(PHONE_FEATURE);
    }

    public void addEventListener(BasePhoneEventListener phoneListener) {
        listeners.addIfAbsent(phoneListener);
    }

    public void removeEventListener(BasePhoneEventListener phoneListener) {
        listeners.remove(phoneListener);
    }

    /**
     * Dials an extension from the user's own device. Asterisk rings the user first, and
     * dials the extension once they answer.
     */
    public void dialByExtension(String number) throws XMPPException, SmackException, InterruptedException {
        final PhoneActionIQ action = new PhoneActionIQ(PhoneActionIQ.DIAL);
        action.setType(org.jivesoftware.smack.packet.IQ.Type.set);
        action.setExtension(number);
        // Must be addressed to the phone component: without a 'to' the request goes to the
        // user's own server default and never reaches the plugin's IQ handler.
        action.setTo(component);
        action.setFrom(connection.getUser());
        connection.sendIqRequestAndWaitForResponse(action);
    }

    /**
     * Dials another user's primary device.
     */
    public void dialByJID(BareJid jid) throws XMPPException, SmackException, InterruptedException {
        final PhoneActionIQ action = new PhoneActionIQ(PhoneActionIQ.DIAL);
        action.setType(org.jivesoftware.smack.packet.IQ.Type.set);
        action.setJid(jid.toString());
        action.setTo(component);
        action.setFrom(connection.getUser());
        connection.sendIqRequestAndWaitForResponse(action);
    }

    private class PhoneEventStanzaListener implements StanzaListener {

        @Override
        public void processStanza(Stanza stanza) {
            final List<PhoneEventExtension> extensions = stanza.getExtensions(PhoneEventExtension.class);
            for (final PhoneEventExtension event : extensions) {
                try {
                    dispatch(event);
                } catch (Exception e) {
                    Log.warning("Unable to dispatch phone event", e);
                }
            }
        }

        private void dispatch(PhoneEventExtension event) {
            final String type = event.getType();
            if (type == null) {
                return;
            }
            switch (type) {
                case PhoneEventExtension.RING: {
                    final RingEvent ringEvent = new RingEvent(event.getCallID(), event.getDevice(),
                            event.getCallerID(), event.getCallerIDName());
                    listeners.forEach(listener -> listener.handleRing(ringEvent));
                    break;
                }
                case PhoneEventExtension.ON_PHONE: {
                    final OnPhoneEvent onPhoneEvent = new OnPhoneEvent(event.getCallID(), event.getDevice(),
                            event.getCallerID(), event.getCallerIDName());
                    listeners.forEach(listener -> listener.handleOnPhone(onPhoneEvent));
                    break;
                }
                case PhoneEventExtension.HANG_UP: {
                    final HangUpEvent hangUpEvent = new HangUpEvent(event.getCallID(), event.getDevice());
                    listeners.forEach(listener -> listener.handleHangUp(hangUpEvent));
                    break;
                }
                default:
                    // DIALED and anything else the server may add: no UI hook in Spark.
                    break;
            }
        }
    }
}
