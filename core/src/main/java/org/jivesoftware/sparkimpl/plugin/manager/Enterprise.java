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
package org.jivesoftware.sparkimpl.plugin.manager;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo;
import org.jivesoftware.smackx.disco.packet.DiscoverItems;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.util.log.Log;

/**
 * EnterpriseSparkManager is responsible for the detecting of features on the server. This allows for fine-grain control of
 * feature sets to enable/disable within Spark.
 *
 * @author Derek DeMoro
 */
public class Enterprise {

    public static final String ACCOUNTS_REG_FEATURE = "accounts-reg";
    public static final String ADD_CONTACTS_FEATURE = "add-contacts";
    public static final String ADD_GROUPS_FEATURE = "add-groups";
    public static final String ADVANCED_CONFIG_FEATURE = "advanced-config";
    public static final String AVATAR_TAB_FEATURE = "avatar-tab";
    public static final String BROADCAST_FEATURE = "broadcast";
    public static final String REMOVALS_FEATURE = "removals";
    public static final String RENAMES_FEATURE = "renames";
    public static final String FILE_TRANSFER_FEATURE = "file-transfer";
    public static final String HELP_FORUMS_FEATURE = "help-forums";
    public static final String HELP_USERGUIDE_FEATURE = "help-userguide";
    public static final String HISTORY_SETTINGS_FEATURE = "history-settings";
    public static final String HOST_NAME_FEATURE = "host-name";
    public static final String INVISIBLE_LOGIN_FEATURE = "invisible-login";
    public static final String ANONYMOUS_LOGIN_FEATURE = "anonymous-login";
    public static final String LOGOUT_EXIT_FEATURE = "logout-exit";
    public static final String MOVE_COPY_FEATURE = "move-copy";
    public static final String MUC_FEATURE = "muc";
    public static final String PASSWORD_CHANGE_FEATURE = "password-change";
    public static final String PERSON_SEARCH_FEATURE = "person-search";
    public static final String PLUGINS_MENU_FEATURE = "plugins-menu";
    public static final String PREFERENCES_MENU_FEATURE = "preferences-menu";
    public static final String PRESENCE_STATUS_FEATURE = "presence-status";
    public static final String VCARD_FEATURE = "vcard";
    public static final String SAVE_PASSWORD_FEATURE = "save-password";
    public static final String UPDATES_FEATURE = "updates";
    public static final String VIEW_NOTES_FEATURE = "view-notes";
    public static final String VIEW_TASKS_FEATURE = "view-tasks";

    private static DiscoverInfo featureInfo;

    private boolean sparkManagerInstalled;

    public Enterprise() {
        // Retrieve feature list.
        populateFeatureSet();
    }

    /**
     * Returns true if the Enterprise Spark Manager module is installed on the server we are currently connected to.
     *
     * @return true if Enterprise Spark Manager exists.
     */
    public boolean isSparkManagerInstalled() {
        return sparkManagerInstalled;
    }

    /**
     * Returns true if the feature is available.
     *
     * @param feature the name of the feature to detect.
     * @return true if the feature is available on the server, otherwise false.
     */
    public static boolean containsFeature(String feature) {
        if (featureInfo == null) {
            return true;
        }

        return featureInfo.containsFeature(feature);
    }

    private void populateFeatureSet() {
        final ServiceDiscoveryManager disco = ServiceDiscoveryManager.getInstanceFor(SparkManager.getConnection());
        final DiscoverItems items = SparkManager.getSessionManager().getDiscoveredItems();
        for (DiscoverItems.Item item : items.getItems() ) {
            String entity = item.getEntityID();
            if (entity != null) {
                if (entity.startsWith("manager.")) {
                    sparkManagerInstalled = true;

                    // Populate with feature sets.
                    try {
                        featureInfo = disco.discoverInfo(item.getEntityID());
                    }
                    catch (XMPPException | SmackException e) {
                        Log.error("Error while retrieving feature list for SparkManager.", e);
                    }

                }
            }
        }
    }
}
