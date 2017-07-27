package org.jivesoftware.sparkimpl.certificates;

import java.security.Provider;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLContextSpi;

public class SparkSSLContext extends SSLContext {

    protected SparkSSLContext(SSLContextSpi contextSpi, Provider provider, String protocol) {
        super(contextSpi, provider, protocol);
        // TODO Auto-generated constructor stub
    }

}
