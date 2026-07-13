package org.jivesoftware.spark.plugin.omemo;

import java.util.logging.Logger;

import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.omemo.element.OmemoElement;
import org.jivesoftware.smackx.omemo.element.OmemoElement_VAxolotl;

/**
 * Supprime le corps de compatibilité OMEMO avant que Spark ne l'insère dans le
 * transcript.
 * <p>
 * Le moteur OMEMO de Smack déchiffre ensuite la charge
 * OmemoElement_VAxolotl et AvhOmemoPlugin.onSecureMessage() affiche le texte
 * clair.
 * <p>
 * Ce listener doit être enregistré avec addSyncStanzaListener(), pas avec un
 * MessageEventListener Spark : ce dernier intervient trop tard, après
 * l'affichage du fallback.
 */
public final class OmemoFallbackSuppressor implements StanzaListener {
    private static final Logger LOG =
        Logger.getLogger(OmemoFallbackSuppressor.class.getName());

    public interface Listener {
        void onFallbackSuppressed(
            String from,
            String stanzaId,
            String originalBody);
    }

    private final Listener listener;

    public OmemoFallbackSuppressor(Listener listener) {
        this.listener = listener;
    }

    @Override
    public void processStanza(Stanza stanza) {
        if (!(stanza instanceof Message)) {
            return;
        }
        Message message = (Message) stanza;
        if (!containsLegacyOmemo(message)) {
            return;
        }
        String body = message.getBody();
        if (body == null) {
            return;
        }
        /*
         * On ne supprime le corps que si une vraie charge OMEMO legacy est
         * présente. Le texte chiffré reste dans l'extension <encrypted/> et
         * sera déchiffré par OmemoManager.
         */
        message.setBody(null);
        String from = message.getFrom() == null ? "(inconnu)" : message.getFrom().toString();
        LOG.fine("Fallback OMEMO supprimé avant affichage : " + from + " stanza=" + message.getStanzaId());
        if (listener != null) {
            listener.onFallbackSuppressed(from, message.getStanzaId(), body);
        }
    }

    private static boolean containsLegacyOmemo(Message message) {
        return message.hasExtension(OmemoElement.NAME_ENCRYPTED, OmemoElement_VAxolotl.NAMESPACE);
    }
}
