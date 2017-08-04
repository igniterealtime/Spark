package org.jivesoftware.sparkimpl.certificates;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.SecureRandom;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLContextSpi;

public class SparkSSLContext extends SSLContext {

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
     * @throws NoSuchProviderException
     */
    public static SSLContext setUpContext() throws KeyManagementException, NoSuchAlgorithmException {
        SSLContext context = SparkSSLContext.getInstance("TLS");
        context.init(null, SparkTrustManager.getTrustManagerList(), new SecureRandom());
        return context;
    }
}
