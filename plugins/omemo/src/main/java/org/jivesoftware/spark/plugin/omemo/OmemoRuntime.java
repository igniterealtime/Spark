package org.jivesoftware.spark.plugin.omemo;

import java.io.File;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.carbons.packet.CarbonExtension;
import org.jivesoftware.smackx.omemo.OmemoConfiguration;
import org.jivesoftware.smackx.omemo.OmemoManager;
import org.jivesoftware.smackx.omemo.OmemoMessage;
import org.jivesoftware.smackx.omemo.OmemoService;
import org.jivesoftware.smackx.omemo.listener.OmemoMessageListener;
import org.jivesoftware.smackx.omemo.signal.SignalFileBasedOmemoStore;
import org.jivesoftware.smackx.omemo.signal.SignalOmemoService;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.impl.JidCreate;

public final class OmemoRuntime implements AutoCloseable {
    private static final Logger LOG =
        Logger.getLogger(OmemoRuntime.class.getName());

    public interface Listener {
        void onSecureMessage(String from, String body);

        void onStatus(String status);
    }

    private final AbstractXMPPConnection connection;
    private final File storePath;
    private final List<Listener> listeners = new CopyOnWriteArrayList<Listener>();

    private OmemoManager manager;
    private OmemoMessageListener omemoListener;

    public OmemoRuntime(AbstractXMPPConnection connection, File storePath) {
        this.connection = connection;
        this.storePath = storePath;
    }

    public synchronized void initialize() throws Exception {
        if (manager != null) {
            return;
        }
        if (connection == null || !connection.isAuthenticated()) {
            throw new IllegalStateException("Spark doit être connecté avant l'initialisation OMEMO.");
        }
        if (!storePath.exists() && !storePath.mkdirs()) {
            throw new IllegalStateException("Impossible de créer le stockage OMEMO : " + storePath);
        }
        SignalOmemoService.acknowledgeLicense();
        if (!SignalOmemoService.isServiceRegistered()) {
            SignalOmemoService.setup();
        }
        try {
            @SuppressWarnings({"rawtypes", "unchecked"})
            OmemoService service = OmemoService.getInstance();
            service.setOmemoStoreBackend(new SignalFileBasedOmemoStore(storePath));
        } catch (IllegalStateException alreadyConfigured) {
            LOG.log(Level.FINE, "Le backend OMEMO était déjà configuré.", alreadyConfigured);
        }

        OmemoConfiguration.setAddOmemoHintBody(false);
        OmemoConfiguration.setCompleteSessionWithEmptyMessage(false);
        OmemoConfiguration.setDeleteStaleDevices(true);
        manager = OmemoManager.getInstanceFor(connection);
        File trustFile = new File(storePath, "trust-decisions.properties");

        manager.setTrustCallback(new PersistentTrustCallback(trustFile));

        omemoListener = new OmemoMessageListener() {
            @Override
            public void onOmemoMessageReceived(Stanza stanza, OmemoMessage.Received decryptedMessage) {
                if (decryptedMessage == null || decryptedMessage.isKeyTransportMessage()) {
                    return;
                }
                String from = stanza.getFrom() == null ? "inconnu" : stanza.getFrom().asBareJid().toString();
                emitStatus("Callback onOmemoMessageReceived — from=" + from + ", keyTransport=" + decryptedMessage.isKeyTransportMessage());

                notifySecureMessage(from, decryptedMessage.getBody());
            }

            @Override
            public void onOmemoCarbonCopyReceived(
                CarbonExtension.Direction direction,
                Message carbonCopy,
                Message wrappingMessage,
                OmemoMessage.Received decryptedCarbonCopy) {

                if (decryptedCarbonCopy == null
                    || decryptedCarbonCopy.isKeyTransportMessage()) {
                    return;
                }

                Message source = carbonCopy != null
                    ? carbonCopy
                    : wrappingMessage;

                String from = source == null || source.getFrom() == null
                    ? "copie-carbone"
                    : source.getFrom().asBareJid().toString();

                emitStatus(
                    "Callback onOmemoCarbonCopyReceived — direction="
                        + direction
                        + ", from="
                        + from);

                notifySecureMessage(from, decryptedCarbonCopy.getBody());
            }
        };

        manager.addOmemoMessageListener(omemoListener);

        emitStatus("Avant manager.initialize() — manager=" + manager.getClass().getName());
        manager.initialize();
        emitStatus("Après manager.initialize() — appareil " + manager.getDeviceId());
    }

    public void addListener(Listener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    public boolean isReady() {
        return connection != null && connection.isAuthenticated() && manager != null;
    }

    public int getDeviceId() {
        Integer id = manager == null ? null : manager.getDeviceId();
        return id == null ? -1 : id;
    }

    public void sendEncrypted(BareJid recipient, String body) throws Exception {
        if (!isReady()) {
            throw new IllegalStateException("Connexion XMPP/OMEMO indisponible.");
        }
        if (recipient == null) {
            throw new IllegalArgumentException("Le JID du destinataire est vide.");
        }
        if (body == null || body.trim().isEmpty()) {
            throw new IllegalArgumentException("Le message est vide.");
        }
        emitStatus("Avant encrypt — recipient=" + recipient + ", bodyLength=" + body.length());
        OmemoMessage.Sent encrypted = manager.encrypt(recipient, body);
        emitStatus("Après encrypt — recipient=" + recipient);
        Message stanza = encrypted.buildMessage(connection.getStanzaFactory().buildMessageStanza(), recipient);
        connection.sendStanza(stanza);
    }

    public void sendEncrypted(String recipient, String body) throws Exception {
        sendEncrypted(JidCreate.bareFrom(recipient.trim()), body);
    }

    /**
     * Rafraîchissement explicite réservé aux opérations de maintenance.
     * <p>
     * Ne pas appeler cette méthode lors de l'activation du cadenas : certains
     * serveurs Openfire renvoient alors un SimplePayload que Smack 4.4.6 ne
     * sait pas convertir directement en OmemoDeviceListElement.
     */
    public void requestDeviceListUpdate(BareJid recipient) throws Exception {
        if (!isReady()) {
            throw new IllegalStateException(
                "OMEMO non initialisé.");
        }
        manager.requestDeviceListUpdateFor(recipient);
    }

    public void purgeOwnDeviceList() throws Exception {
        if (!isReady()) {
            throw new IllegalStateException(
                "OMEMO non initialisé.");
        }

        manager.purgeDeviceList();
        emitStatus(
            "Liste locale des appareils OMEMO republiée.");
    }

    private void notifySecureMessage(String from, String body) {
        for (Listener listener : listeners) {
            listener.onSecureMessage(from, body == null ? "" : body);
        }
    }

    private void emitStatus(String status) {
        LOG.info(status);
        for (Listener listener : listeners) {
            listener.onStatus(status);
        }
    }

    @Override
    public synchronized void close() {
        if (manager != null && omemoListener != null) {
            manager.removeOmemoMessageListener(omemoListener);
        }

        omemoListener = null;
        manager = null;
        listeners.clear();
    }
}
