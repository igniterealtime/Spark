/**
 * $RCSfile: ,v $
 * $Revision: $
 * $Date: $
 * 
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
package org.jivesoftware.sparkimpl.plugin.language;

import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.Locale;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.nio.charset.Charset;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JOptionPane;

/**
 * Allows for changing of default languages within Spark.
 *
 * @author Derek DeMoro
 */
public class LanguagePlugin implements Plugin {

    private Locale[] locales;
    private JMenu languageMenu;

    public void initialize() {
        // Register with action menu
        final JMenu actionsMenu = SparkManager.getMainWindow().getMenuByName(Res.getString("menuitem.actions"));

        languageMenu = new JMenu(Res.getString("menuitem.languages"));
        languageMenu.setIcon(SparkRes.getImageIcon("LANGUAGE_ICON"));

        locales = Locale.getAvailableLocales();

        // Load files
        URL sparkJar = getClass().getClassLoader().getResource("spark.jar");
        if (sparkJar == null) {
            sparkJar =  getClass().getProtectionDomain().getCodeSource().getLocation();
            if (sparkJar == null) return;
        }

        try {
            String url = URLDecoder.decode(sparkJar.getPath(), Charset.defaultCharset().toString());
            ZipFile zipFile = new JarFile(new File(url));
            for (Enumeration<? extends ZipEntry> e = zipFile.entries(); e.hasMoreElements();) {
                JarEntry entry = (JarEntry)e.nextElement();
                String propertiesName = entry.getName();
                // Ignore any manifest.mf entries.
                if (propertiesName.endsWith(".properties")) {
                    int lastIndex = propertiesName.lastIndexOf("i18n_");
                    int period = propertiesName.lastIndexOf(".");
                    if (lastIndex == -1 && propertiesName.contains("spark_i18n")) {
                        addLanguage("en");
                    }
                    else {
                        String language = propertiesName.substring(lastIndex + 5, period);
                        addLanguage(language);
                    }
                }
            }
            zipFile.close();
        }
        catch (Throwable e) {
            Log.error("Error unzipping plugin", e);
        }

        actionsMenu.add(languageMenu);
    }

    private void addLanguage(String language) {
        for (final Locale locale : locales) {
            if (locale.toString().equals(language)) {
                Action action = new AbstractAction() {
		    private static final long serialVersionUID = -7093236616888591766L;

		    public void actionPerformed(ActionEvent e) {
                        final LocalPreferences preferences = SettingsManager.getLocalPreferences();
                        preferences.setLanguage(locale.toString());
                        SettingsManager.saveSettings();

                        int ok = JOptionPane.showConfirmDialog(SparkManager.getMainWindow(), Res.getString("message.restart.required"), Res. getString("title.confirmation"), JOptionPane.YES_NO_OPTION);
                        if (ok == JOptionPane.YES_OPTION) {
                            SparkManager.getMainWindow().closeConnectionAndInvoke("Language Change");
                        }
                    }
                };
                String label = locale.getDisplayLanguage(locale);
                if (locale.getDisplayCountry(locale) != null &&
                    locale.getDisplayCountry(locale).trim().length() > 0) {
                    label = label + "-" + locale.getDisplayCountry(locale).trim();
                }
                action.putValue(Action.NAME, label);
                languageMenu.add(action);
                break;
            }
        }
    }

    public void shutdown() {
    }

    public boolean canShutDown() {
        return false;
    }


    public void uninstall() {
    }
}
