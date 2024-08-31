/**
 * Copyright (C) 2004-2024 Jive Software. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.jivesoftware;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.jivesoftware.spark.util.log.Log;

import javax.swing.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.util.Arrays.asList;


public class XmppProviders {
    /**
     * <a href="https://data.xmpp.net/providers/v2/providers-A.json">Providers A</a>
     */
    private static final String[] providers = new String[]{
        "07f.de",
        "chalec.org",
        "chapril.org",
        "chatrix.one",
        "draugr.de",
        "hookipa.net",
        "jabber.fr",
        "macaw.me",
        "magicbroccoli.de",
        "nixnet.services",
        "projectsegfau.lt",
        "redlibre.es",
        "suchat.org",
        "sure.im",
        "trashserver.net",
        "xmpp.earth",
        "yax.im",
    };

    public static ComboBoxModel<String> getXmppProvidersModel() {
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        List<String> providersList = downloadProvidersList();
        if (providersList == null) {
            // fallback to static list
            providersList = asList(providers);
        }
        for (String provider : providersList) {
            model.addElement(provider);
        }
        // Randomly pre-select a provider
        int randomProviderIdx = new Random().nextInt(providersList.size());
        model.setSelectedItem(providersList.get(randomProviderIdx));
        return model;
    }

    static List<String> downloadProvidersList() {
        Log.debug("Download providers");
        try (CloseableHttpClient httpClient = HttpClients.createSystem()) {
            HttpGet request = new HttpGet("https://data.xmpp.net/providers/v2/providers-As.json");
            CloseableHttpResponse httpResponse = httpClient.execute(request);
            final int statusCode = httpResponse.getCode();
            if (statusCode == 200) {
                String json = EntityUtils.toString(httpResponse.getEntity(), StandardCharsets.UTF_8);
                return parseProvidersJson(json);
            }
            Log.error("Download providers: bad status " + statusCode);
            return null;
        } catch (Exception e) {
            Log.error("Download providers: error", e);
            return null;
        }
    }

    static List<String> parseProvidersJson(String json) {
        // manually parse JSON array
        json = json.trim();
        if (json.charAt(0) != '[' || json.charAt(json.length() - 1) != ']') {
            return null;
        }
        String[] lines = json.substring(1, json.length() - 1).split(",");
        List<String> providers = new ArrayList<>(lines.length);
        for (String line : lines) {
            line = line.trim();
            if (line.charAt(0) != '"' || line.charAt(line.length() - 1) != '"') {
                continue;
            }
            String provider = line.substring(1, line.length() - 1);
            providers.add(provider);
        }
        return providers;
    }
}
