package org.jivesoftware.sparkimpl.certificates;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLContextSpi;

import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

public class SparkSSLContext extends SSLContext {

    /**
     * 
     * ClientSide is authentication by
     */
    public enum Options {
        BOTH, ONLY_CLIENT_SIDE, ONLY_SERVER_SIDE;

    }

    protected SparkSSLContext(SSLContextSpi contextSpi, Provider provider, String protocol) {
        super(contextSpi, provider, protocol);
        // TODO Auto-generated constructor stub
    }

    /**
     * Create SSLContext and initialize it
     * 
     * @return initialized SSL context with BouncyCastleProvider
     * @throws KeyManagementException
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException 
     * @throws UnrecoverableKeyException 
     * @throws NoSuchProviderException
     */
    public static SSLContext setUpContext(Options options) throws KeyManagementException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException, NoSuchProviderException {
        SSLContext context = SparkSSLContext.getInstance("TLS");
        if (options == options.ONLY_SERVER_SIDE) {
            context.init(null, SparkTrustManager.getTrustManagerList(), new SecureRandom());
        } else if (options == options.BOTH) {

            IdentityController identityController = new IdentityController(SettingsManager.getLocalPreferences());
            context.init(identityController.initKeyManagerFactory().getKeyManagers(), SparkTrustManager.getTrustManagerList(), new SecureRandom());
  
        } else if (options == options.ONLY_CLIENT_SIDE){
            IdentityController identityController = new IdentityController(SettingsManager.getLocalPreferences());
            context.init(identityController.initKeyManagerFactory().getKeyManagers(), null, new SecureRandom());
            
        }
        return context;
    }
}
