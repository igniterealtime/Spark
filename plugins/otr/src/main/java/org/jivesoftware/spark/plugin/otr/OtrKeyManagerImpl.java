package org.jivesoftware.spark.plugin.otr;

import net.java.otr4j.OtrKeyManagerStore;
import net.java.otr4j.session.SessionID;

import java.io.IOException;import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Fixed version of OtrKeyManagerImpl.
 * TODO Remove after https://github.com/jitsi/otr4j/pull/16 merged
 */
class OtrKeyManagerImpl extends net.java.otr4j.OtrKeyManagerImpl {
    private final OtrKeyManagerStore store;

    public OtrKeyManagerImpl(OtrKeyManagerStore store) {
        super(store);
        this.store = store;
    }

    @Override
    public void generateLocalKeyPair(SessionID sessionID) {
        if (sessionID != null) {
            String accountID = sessionID.getAccountID();

            KeyPair keyPair;
            try {
                final KeyPairGenerator kg = KeyPairGenerator.getInstance("DSA");
                kg.initialize(1024);
                keyPair = kg.genKeyPair();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return;
            }

            PublicKey pubKey = keyPair.getPublic();
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(pubKey.getEncoded());
            this.store.setProperty(accountID + ".publicKey", x509EncodedKeySpec.getEncoded());
            PrivateKey privKey = keyPair.getPrivate();
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privKey.getEncoded());
            this.store.setProperty(accountID + ".privateKey", pkcs8EncodedKeySpec.getEncoded());
        }
    }
}
