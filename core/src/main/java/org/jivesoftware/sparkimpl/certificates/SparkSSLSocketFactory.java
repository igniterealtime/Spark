/**
 * Copyright (C) 2004-2011 Jive Software. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jivesoftware.sparkimpl.certificates;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.certificates.SparkSSLContextCreator.Options;

/**
 * An SSL socket factory that will let any certifacte past, even if it's expired or not singed by a root CA.
 */
public class SparkSSLSocketFactory extends SSLSocketFactory {

    private SSLSocketFactory factory;

    public SparkSSLSocketFactory(Options options) {

        SSLContext sslcontent;
        try {
            sslcontent = SparkSSLContextCreator.setUpContext(options);
            factory = sslcontent.getSocketFactory();
        } catch (KeyManagementException | UnrecoverableKeyException | NoSuchAlgorithmException | KeyStoreException
                | NoSuchProviderException e) {
            Log.error("Couldn't create SSLSocketFactory", e);
        }

    }

    @Override
	public Socket createSocket(Socket socket, String s, int i, boolean flag) throws IOException {
        return factory.createSocket(socket, s, i, flag);
    }

    @Override
	public Socket createSocket(InetAddress inaddr, int i, InetAddress inaddr2, int j) throws IOException {
        return factory.createSocket(inaddr, i, inaddr2, j);
    }

    @Override
	public Socket createSocket(InetAddress inaddr, int i) throws IOException {
        return factory.createSocket(inaddr, i);
    }

    @Override
	public Socket createSocket(String s, int i, InetAddress inaddr, int j) throws IOException {
        return factory.createSocket(s, i, inaddr, j);
    }

    @Override
	public Socket createSocket(String s, int i) throws IOException {
        return factory.createSocket(s, i);
    }

    @Override
	public Socket createSocket() throws IOException {
        return factory.createSocket();
    }

    @Override
	public String[] getDefaultCipherSuites() {
        return factory.getSupportedCipherSuites();
    }

    @Override
	public String[] getSupportedCipherSuites() {
        return factory.getSupportedCipherSuites();
    }
}