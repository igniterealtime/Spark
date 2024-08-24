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

import javax.swing.*;

import java.util.Random;


public class XmppProviders {
    /**
     * <a href="https://data.xmpp.net/providers/v2/providers-A.json">Providers A</a>
     * Disabled providers that requires a CAPTCHA
     */
    private static final String[] providers = new String[]{
//        "07f.de",
//        "chalec.org",
//        "chapril.org",
//        "chatrix.one",
//        "draugr.de",
//        "hookipa.net",
        "jabber.fr",
//        "macaw.me",
//        "magicbroccoli.de",
//        "nixnet.services",
//        "projectsegfau.lt",
//        "redlibre.es",
//        "suchat.org",
//        "sure.im",
//        "trashserver.net",
        "xmpp.earth",
        "yax.im",
    };

    public static ComboBoxModel<String> getXmppProvidersModel() {
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        for (String provider : providers) {
            model.addElement(provider);
        }
        // Randomly pre-select a provider
        int randomProviderIdx = new Random().nextInt(providers.length);
        model.setSelectedItem(providers[randomProviderIdx]);
        return model;
    }
}
