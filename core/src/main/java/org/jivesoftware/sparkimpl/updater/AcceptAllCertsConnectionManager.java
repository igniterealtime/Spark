/**
 * Copyright (C) 2023 Ignite Realtime Foundation. All rights reserved.
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
package org.jivesoftware.sparkimpl.updater;

import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.impl.io.BasicHttpClientConnectionManager;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.ssl.TrustStrategy;
import org.jivesoftware.smack.SmackConfiguration;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

/**
 * A HTTP Client connection manager that knowingly by-passes all verification of TLS certificates.
 *
 * This SHOULD NOT be used for productive systems due to security reasons, unless it is a conscious decision and you are
 * perfectly aware of security implications of accepting self-signed certificates.
 *
 * Usage example:
 * <code>
 *     AcceptAllCertsConnectionManager connectionManager = AcceptAllCertsConnectionManager.getInstance();
 *     try( CloseableHttpClient httpClient = HttpClients.custom()
 *          .setConnectionManager(connectionManager)
 *          .build();
 *
 *     CloseableHttpResponse response = (CloseableHttpResponse) httpClient
 *         .execute(getMethod, new CustomHttpClientResponseHandler())) {
 *
 *         final int statusCode = response.getCode();
 *         assertThat(statusCode, equalTo(HttpStatus.SC_OK));
 *     };
 * </code>
 * @author Guus der Kinderen, guus.der.kinderen@gmail.com
 */
public class AcceptAllCertsConnectionManager extends BasicHttpClientConnectionManager
{
    public static BasicHttpClientConnectionManager getInstance() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException
    {
        // Taken from https://www.baeldung.com/httpclient-ssl
        final TrustStrategy acceptingTrustStrategy = (cert, authType) -> true;
        final SSLContext sslContext = SSLContexts.custom()
            .loadTrustMaterial(null, acceptingTrustStrategy)
            .build();
        final SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
        final Registry<ConnectionSocketFactory> socketFactoryRegistry =
            RegistryBuilder.<ConnectionSocketFactory> create()
                .register("https", sslsf)
                .register("http", new PlainConnectionSocketFactory())
                .build();

        final BasicHttpClientConnectionManager result = new BasicHttpClientConnectionManager(socketFactoryRegistry);
        result.setSocketConfig(SocketConfig.custom().setSoTimeout(SmackConfiguration.getDefaultReplyTimeout(), TimeUnit.MILLISECONDS).build());
        result.setConnectionConfig(ConnectionConfig.custom().setConnectTimeout(SmackConfiguration.getDefaultReplyTimeout(), TimeUnit.MILLISECONDS).build());
        return result;
    }
}
