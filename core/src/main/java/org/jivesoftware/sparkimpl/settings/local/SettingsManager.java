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

package org.jivesoftware.sparkimpl.settings.local;

import org.jivesoftware.Spark;
import org.jivesoftware.resource.Default;
import org.jivesoftware.spark.util.log.Log;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * Responsible for the loading and persisting of LocalSettings.
 */
public class SettingsManager {
    private static LocalPreferences localPreferences;
    private static final CopyOnWriteArrayList<PreferenceListener> listeners = new CopyOnWriteArrayList<>();
    private static final int CURRENT_SETTINGS_VERSION = 2;

    private SettingsManager() {
    }

    static {
        // always save settings on shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(SettingsManager::saveSettings));
    }

    /**
     * Returns the LocalPreferences for this user.
     */
    public synchronized static LocalPreferences getLocalPreferences() {
        if (localPreferences != null) {
            return localPreferences;
        }
        // Do Initial Load from FileSystem.
        localPreferences = load();
        return localPreferences;
    }

    /**
     * Persists the settings to the local file system.
     */
    private static void saveSettings() {
        if (localPreferences == null) {
            return;
        }
        Properties props = localPreferences.getProperties();
        if (props.isEmpty()) {
            // happens in tests, just silently leave
            return;
        }
        Log.debug("Saving settings...");
        try {
            props.store(Files.newOutputStream(getSettingsFile().toPath()), "Spark Settings");
        } catch (Exception e) {
            Log.error("Error saving settings.", e);
            return;
        }
        Log.debug("Settings saved");
    }

    /**
     * Returns the settings file.
     */
    private static File getSettingsFile() {
        return new File(Spark.getSparkUserHome(), "spark.properties");
    }

    private static LocalPreferences load() {
        final Properties props = new Properties();
        File settingsFile = getSettingsFile();
        List<Map<String, String>> defaults = propsMigrationMaps();
        if (settingsFile.exists()) {
            try {
                props.load(Files.newInputStream(settingsFile.toPath()));
                migrateProperties(props, defaults);
            } catch (IOException e) {
                Log.error(e);
            }
        }
        // Override with global settings file
        File globalSettingsFile = new File("spark.properties");
        if (globalSettingsFile.exists()) {
            try {
                props.load(Files.newInputStream(globalSettingsFile.toPath()));
                migrateProperties(props, defaults);
            } catch (IOException e) {
                Log.error(e);
            }
        }
        return new LocalPreferences(props);
    }

    public static void addPreferenceListener(PreferenceListener listener) {
        listeners.addIfAbsent(listener);
    }

    public static void removePreferenceListener(PreferenceListener listener) {
        listeners.remove(listener);
    }

    public static void fireListeners() {
        for (PreferenceListener listener : listeners) {
            try {
                listener.preferencesChanged(localPreferences);
            } catch (Exception e) {
                Log.error("A PreferenceListener (" + listener + ") threw an exception while processing a 'referencesChanged' event.", e);
            }
        }
    }

    private static void migrateProperties(Properties props, List<Map<String, String>> defaults) {
        int settingsVersion = Integer.parseInt((String) props.getOrDefault("settingsVersion", "1"));
        if (settingsVersion >= CURRENT_SETTINGS_VERSION) {
            return;
        }
        for (int version = settingsVersion; version <= CURRENT_SETTINGS_VERSION; version++) {
            Log.warning("Mirating settings to version " + version);
            var migration = defaults.get(version - 1);
            for (Map.Entry<Object, Object> setting : props.entrySet()) {
                String key = (String) setting.getKey();
                String value = (String) setting.getValue();
                String defaultSettingVal = migration.get(key);
                if (defaultSettingVal == null) {
                    continue;
                }
                if (defaultSettingVal.equals(value)) {
                    Log.warning("setting " + key + " value: '" + value + "' the is default value");
                    props.remove(key);
                }
            }
        }
        props.setProperty("settingsVersion", String.valueOf(CURRENT_SETTINGS_VERSION));
    }

    /**
     * List of default setting values for each version.
     * version: map[setting, default]
     */
    private static List<Map<String, String>> propsMigrationMaps() {
        var v1 = new HashMap<String, String>(121);
        v1.put("timeout", "10");
        v1.put("reconnectDelay", "10");
        v1.put("idleOn", "true");
        v1.put("useAdHocRoom", "true");
        v1.put("idleOnMessage", "");
        v1.put("idleTime", "5");
        v1.put("autoLoginEnabled", "false");
        v1.put("loginAsInvisibleEnabled", "false");
        v1.put("loginAnonymously", "false");
        v1.put("passwordSaved", "false");
        v1.put("username", "");
        v1.put("server", "");
        v1.put("securityMode", "ifpossible");
        v1.put("sslEnabled", "false");
        v1.put("newInstall", "");
        v1.put("downloadDirectory", "");
        v1.put("proxyEnabled", "false");
        v1.put("host", "");
        v1.put("port", "0");
        v1.put("proxyUsername", "");
        v1.put("proxyPassword", "");
        v1.put("protocol", "SOCKS");
        v1.put("defaultNickname", "");
        v1.put("checkForUpdates", "7");
        v1.put("xmppPort", "5222");
        v1.put("xmppHost", "");
        v1.put("hostAndPort", "false");
        v1.put("resource", "Spark");
        v1.put("startHidden", "false");
        v1.put("useSingleTrayClick", "true");
        v1.put("timeDisplayed", "true");
        v1.put("timeFormat", "HH:mm");
        v1.put("spellCheckerEnabled", "true");
        v1.put("chatNotificationOn", "true");
        v1.put("showHistory", "true");
        v1.put("showPrevHistory", "true");
        v1.put("showEmptyGroups", "false");
        v1.put("showOfflineUsers", "true");
        v1.put("showTypingNotification", "false");
        v1.put("SystemTrayNotificationEnabled", "1");
        v1.put("fileTransferTimeout", "30");
        v1.put("defaultChatLengthTimeout", "15");
        v1.put("nickname", "");
        v1.put("toasterPopup", "false");
        v1.put("disableAsteriskToasterPopup", "false");
        v1.put("windowTakesFocus", "false");
        v1.put("startOnStartup", "false");
        v1.put("ReconnectPanelType", "1");
        v1.put("compressionOn", "false");
        v1.put("theme", "Default");
        v1.put("emoticonPack", "Default");
        v1.put("DisplayTime", "3");
        v1.put("notifyOnOffline", "false");
        v1.put("notifyOnOnline", "false");
        v1.put("dockingEnabled", "false");
        v1.put("autoCloseChatRoomsEnabled", "true");
        v1.put("tabsOnTop", "true");
        v1.put("tabsScroll", "true");
        v1.put("buzzEnabled", "true");
        v1.put("closeUnreadMessage", "true");
        v1.put("offlineGroupVisible", "true");
        v1.put("emoticonsEnabled", "true");
        v1.put("GrayingOut", "true");
        v1.put("LookAndFeel", "org.jivesoftware.spark.ui.themes.lafs.SparkLightLaf");
        v1.put("checkForBeta", "false");
        v1.put("isMucHighNameOn", "false");
        v1.put("isMucHighTextOn", "false");
        v1.put("isMucRandomColors", "true");
        v1.put("isMucHighToastOn", "false");
        v1.put("isShowingRoleIcons", "false");
        v1.put("isShowJoinLeaveMessagesOn", "false");
        v1.put("ssoEnabled", "false");
        v1.put("saslGssapiSmack3compat", "false");
        v1.put("ssoMethod", "file");
        v1.put("ssoRealm", "file");
        v1.put("ssoKDC", "");
        v1.put("debug", "false");
        v1.put("debuggerEnabled", "false");
        v1.put("contactListFontSize", "11");
        v1.put("contactListIconSize", "24");
        v1.put("chatRoomFontSize", "12");
        v1.put("language", "en");
        v1.put("showAvatar", "false");
        v1.put("showVCards", "true");
        v1.put("audioSystem", "wasapi");
        v1.put("audioDevice", "javasound://");
        v1.put("playbackDevice", "javasound://");
        v1.put("videoDevice", "");
        v1.put("MainWindowAlwaysOnTop", "false");
        v1.put("ChatWindowAlwaysOnTop", "false");
        v1.put("SelectedCodecs", "");
        v1.put("stunFallbackHost", "");
        v1.put("stunFallbackPort", "3478");
        v1.put("useTabForTransport", "false");
        v1.put("useTabForConference", "true");
        v1.put("AvailableCodecs", "");
        v1.put("DisableHostnameVerification", "false");
        v1.put("autoAcceptMucInvite", "false");
        v1.put("defaultBookmarkedConf", "");
        v1.put("HISTORY_SORT_DATEASC", "true");
        v1.put("HISTORY_SEARCH_PERIOD", "");
        v1.put("deactivatedPlugins", "");
        v1.put("useHostnameAsResource", "false");
        v1.put("useVersionAsResource", "false");
        v1.put("ccAccountsReg", "true");
        v1.put("ccAdvancedConfig", "true");
        v1.put("ccHostNameChange", "true");
        v1.put("ccInvisibleLogin", "true");
        v1.put("ccAnonymousLogin", "true");
        v1.put("ccPswdAutologin", "true");
        v1.put("acceptSelfSigned", "false");
        v1.put("acceptRevoked", "false");
        v1.put("acceptExpired", "false");
        v1.put("acceptNotValidYet", "false");
        v1.put("checkCRL", "true");
        v1.put("checkOCSP", "false");
        v1.put("allowSoftFail", "true");
        v1.put("allowClientSideAuthentication", "false");
        v1.put("fileTransferIbbOnly", "true");
        v1.put("fileTransferAutoAcceptPresence", "false");
        v1.put("currentHistoryMaxSize", "20");

        var v2 = Map.of(
            "showOfflineUsers", "true",
            "contactListIconSize", "32",
            "contactListFontSize", "14",
            "chatRoomFontSize", "16",
            "passwordSaved", "true"
        );
        return List.of(v1, v2);
    }
}
