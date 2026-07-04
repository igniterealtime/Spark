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

package org.jivesoftware;

import org.apache.commons.lang3.SystemUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Localpart;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;


public class SparkCompatibility {
    public static void migrateSettings() {
        transferConfig();
        migrateOldSettingsXml();
    }

    /**
     * Copies the old USER_SPARK_HOME to the new directory if it does not exist
     */
    private static void transferConfig() {
        File newSparkHomeDir = Spark.getSparkUserHome();
        if (newSparkHomeDir.exists()) {
            return;
        }
        // Old Spark settings directory
        File oldSparkHomeDir = new File(System.getProperty("user.home"), SystemUtils.IS_OS_MAC ? "/Spark" : Spark.getUserConf());
        if (oldSparkHomeDir.exists()) {
            // Absolute paths to a collection of files or directories to skip
            Collection<String> skipFiles = List.of(
                new File(newSparkHomeDir, "plugins").getAbsolutePath()
            );
            try {
                copyDirectory(oldSparkHomeDir, newSparkHomeDir, skipFiles);
            } catch (IOException e) {
               Log.error("Unable to copy old Spark home directory", e);
            }
        }
    }

    /**
     * Checks for historic Spark settings and upgrades the user.
     */
    private static void migrateOldSettingsXml() {
        File settingsXML = new File(Spark.getSparkUserHome(), "/settings.xml");
        if (!settingsXML.exists()) {
            return;
        }
        try {
            SAXReader saxReader = SAXReader.createDefault();
            Document pluginXML;
            try {
                pluginXML = saxReader.read(settingsXML);
            } catch (DocumentException e) {
                Log.error(e);
                return;
            }
            LocalPreferences pref = SettingsManager.getLocalPreferences();
            List<?> plugins = pluginXML.selectNodes("/settings");
            for (Object pluginEl : plugins) {
                if (!(pluginEl instanceof Element)) {
                    continue;
                }
                Element plugin = (Element) pluginEl;
                String username = plugin.selectSingleNode("username").getText();
                pref.setLastUsername(username);
                String server = plugin.selectSingleNode("server").getText();
                pref.setServer(server);
                String autoLogin = plugin.selectSingleNode("autoLogin").getText();
                pref.setAutoLogin(Boolean.parseBoolean(autoLogin));
                String savePassword = plugin.selectSingleNode("savePassword").getText();
                pref.setSavePassword(Boolean.parseBoolean(savePassword));
                String password = plugin.selectSingleNode("password").getText();
                Localpart usernamePart = Localpart.formUnescapedOrNull(username);
                DomainBareJid domainPart = JidCreate.domainBareFromOrNull(server);
                EntityBareJid bareJid = JidCreate.entityBareFrom(usernamePart, domainPart);
                pref.setPasswordForUser(bareJid, password);
            }
            // Delete settings File
            //noinspection ResultOfMethodCallIgnored
            settingsXML.delete();
        } catch (Exception e) {
            Log.error(e);
        }
    }

    /**
     * Copies the directories / files recursively
     *
     * @param src       The source dir/file to copy
     * @param dest      The destination dir/file to copy
     * @param skipFiles A collection of files to skip
     */
    private static void copyDirectory(File src, File dest, Collection<String> skipFiles) throws IOException {
        if (src.isDirectory()) {
            if (!dest.exists()) {
                dest.mkdir();
            }
            String[] children = src.list();
            children = children != null ? children : new String[]{};
            for (String child : children) {
                // Skip any directories / files which may need to be skipped.
                if (!skipFiles.contains((new File(dest, child).getAbsolutePath()))) {
                    copyDirectory(new File(src, child), new File(dest, child), new HashSet<>());
                }
            }
        } else {
            InputStream in;
            OutputStream out;

            try {
                in = new FileInputStream(src);
                out = new FileOutputStream(dest);
            } catch (FileNotFoundException e) {
                IOException wrapper = new IOException("copyDirectory: Unable to open handle on file: "
                    + src.getAbsolutePath() + "and" + dest.getAbsolutePath() + ".", e);
                wrapper.setStackTrace(e.getStackTrace());
                throw wrapper;
            } catch (SecurityException e) {
                IOException wrapper = new IOException("copyDirectory: access denied to copy file: "
                    + src.getAbsolutePath() + "and" + dest.getAbsolutePath() + ".", e);
                wrapper.setStackTrace(e.getStackTrace());
                throw wrapper;
            }
            try {
                // Copy the bits from instream to outstream
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } catch (IOException e) {
                IOException wrapper = new IOException("copyDirectory: Unable to copy file: "
                    + src.getAbsolutePath() + "to" + dest.getAbsolutePath() + ".", e);
                wrapper.setStackTrace(e.getStackTrace());
                throw wrapper;
            } finally {
                in.close();
                out.close();
            }
        }
    }

}
