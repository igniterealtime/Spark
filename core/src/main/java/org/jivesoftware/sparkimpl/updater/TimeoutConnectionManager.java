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
import org.apache.hc.core5.http.io.SocketConfig;
import org.jivesoftware.smack.SmackConfiguration;

import java.util.concurrent.TimeUnit;

/**
 * Provides an HTTP client connection manager that applies Spark's default network timeouts.
 *
 * Unlike a plain connection manager, the connect and socket timeouts are derived from
 * {@link SmackConfiguration#getDefaultReplyTimeout()}, so a slow or unresponsive host cannot block
 * the calling thread indefinitely. TLS certificate and hostname verification use the standard,
 * validating defaults.
 */
public class TimeoutConnectionManager
{
    private TimeoutConnectionManager() {
    }

    public static BasicHttpClientConnectionManager getInstance()
    {
        final long timeout = SmackConfiguration.getDefaultReplyTimeout();
        final BasicHttpClientConnectionManager result = new BasicHttpClientConnectionManager();
        result.setSocketConfig(SocketConfig.custom().setSoTimeout(timeout, TimeUnit.MILLISECONDS).build());
        result.setConnectionConfig(ConnectionConfig.custom().setConnectTimeout(timeout, TimeUnit.MILLISECONDS).build());
        return result;
    }
}
