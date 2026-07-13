package org.jivesoftware.spark.plugin.omemo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jivesoftware.smackx.omemo.internal.OmemoDevice;
import org.jivesoftware.smackx.omemo.trust.OmemoTrustCallback;
import org.jivesoftware.smackx.omemo.trust.TrustState;
import org.jivesoftware.smackx.omemo.trust.OmemoFingerprint;

/**
 * Persistance locale des décisions de confiance OMEMO.
 * <p>
 * Politique Alpha 0.11 : TOFU (Trust On First Use).
 * Une nouvelle empreinte est acceptée lors de sa première observation, puis
 * mémorisée. Une modification ultérieure produit une nouvelle entrée et doit
 * être vérifiée via l'interface de gestion des empreintes d'une version future.
 */
public final class PersistentTrustCallback implements OmemoTrustCallback {
    private static final Logger LOG = Logger.getLogger(PersistentTrustCallback.class.getName());

    private final File trustFile;
    private final Properties states = new Properties();

    public PersistentTrustCallback(File trustFile) throws IOException {
        this.trustFile = trustFile;
        File parent = trustFile.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IOException("Impossible de créer le dossier de confiance : " + parent);
        }
        load();
    }

    @Override
    public synchronized TrustState getTrust(OmemoDevice device, OmemoFingerprint fingerprint) {
        String key = key(device, fingerprint);
        String stored = states.getProperty(key);
        if (stored != null) {
            try {
                return TrustState.valueOf(stored);
            } catch (IllegalArgumentException invalidState) {
                LOG.log(Level.WARNING, "État de confiance invalide pour " + key, invalidState);
            }
        }

        // TOFU : première empreinte observée acceptée puis figée localement.
        states.setProperty(key, TrustState.trusted.name());
        try {
            save();
        } catch (IOException error) {
            LOG.log(Level.WARNING, "Impossible d'enregistrer la décision TOFU.", error);
        }

        return TrustState.trusted;
    }

    @Override
    public synchronized void setTrust(OmemoDevice device, OmemoFingerprint fingerprint, TrustState state) {
        if (state == null) {
            state = TrustState.undecided;
        }
        states.setProperty(key(device, fingerprint), state.name());
        try {
            save();
        } catch (IOException error) {
            LOG.log(Level.WARNING, "Impossible d'enregistrer la décision de confiance.", error);
        }
    }

    private static String key(OmemoDevice device, OmemoFingerprint fingerprint) {
        return device.toString() + "|" + fingerprint.toString().toLowerCase();
    }

    private void load() throws IOException {
        if (!trustFile.isFile()) {
            return;
        }
        try (FileInputStream input = new FileInputStream(trustFile)) {
            states.load(input);
        }
    }

    private void save() throws IOException {
        File temporary = new File(trustFile.getParentFile(), trustFile.getName() + ".tmp");
        try (FileOutputStream output = new FileOutputStream(temporary)) {
            states.store(output, "AVHIRAL OMEMO trust decisions - local TOFU store");
            output.getFD().sync();
        }
        if (trustFile.exists() && !trustFile.delete()) {
            throw new IOException("Impossible de remplacer le fichier de confiance.");
        }
        if (!temporary.renameTo(trustFile)) {
            throw new IOException("Impossible d'activer le nouveau fichier de confiance.");
        }
    }
}
