/*
 * Copyright (C) 2004-2011 Jive Software. 2020 Ignite Realtime Foundation. All rights reserved.
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
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static java.util.Locale.ENGLISH;

/**
 * Allows for changing of default languages within Spark.
 *
 * @author Derek DeMoro
 */
public class LanguagePlugin implements Plugin {

    @Override
	public void initialize() {
        // Register with action menu
        final JMenu actionsMenu = SparkManager.getMainWindow().getMenuByName(Res.getString("menuitem.actions"));

        String languageMenuLabel = Res.getString("menuitem.languages");
        // For non-English locales append "Language" for those who accidentally changed language
        if (!Locale.getDefault().getLanguage().equals("en")) {
            languageMenuLabel += " (Language)";
        }
        JMenu languageMenu = new JMenu(languageMenuLabel);
        languageMenu.setIcon(SparkRes.getImageIcon("LANGUAGE_ICON"));

        addLanguage(ENGLISH, languageMenu);
        // If we have a translation file for this locale, we can support the language!
        List<String> i18nResources = getAllFilesFromResource("/i18n/");
        for (String localeFile : i18nResources) {
            Locale locale = Locale.forLanguageTag(localeFile.replace("_", "-"));
            addLanguage(locale, languageMenu);
        }

        actionsMenu.add(languageMenu);
    }

    private void addLanguage(Locale locale, JMenu languageMenu) {
        Action action = new AbstractAction() {
            private static final long serialVersionUID = -7093236616888591766L;

            @Override
            public void actionPerformed(ActionEvent e) {
                final LocalPreferences preferences = SettingsManager.getLocalPreferences();
                preferences.setLanguage(locale.toString());
                SettingsManager.saveSettings();

                UIManager.put("OptionPane.yesButtonText", Res.getString("yes"));
                UIManager.put("OptionPane.noButtonText", Res.getString("no"));

                int ok = JOptionPane.showConfirmDialog(SparkManager.getMainWindow(), Res.getString("message.restart.required"), Res.getString("title.confirmation"), JOptionPane.YES_NO_OPTION);
                if (ok == JOptionPane.YES_OPTION) {
                    SparkManager.getMainWindow().closeConnectionAndInvoke("Language Change");
                }
            }
        };
        String label = locale.getDisplayName(locale);
        action.putValue(Action.NAME, label);
        languageMenu.add(action);
    }

    @Override
	public void shutdown() {
    }

    @Override
	public boolean canShutDown() {
        return false;
    }


    @Override
	public void uninstall() {
    }

    private List<String> getAllFilesFromResource(String folder) {
        // dun walk the root path, we will walk all the classes
        URL resource = getClass().getResource(folder);
        if (resource == null) {
            return List.of();
        }
        try {
            List<String> collect = Files.list(Paths.get(resource.toURI()))
                .map(path -> path.getFileName().toString())
                .filter(name -> name.startsWith("spark_i18n_"))
                .map(name -> name.substring("spark_i18n_".length(), name.lastIndexOf(".")))
                .sorted()
                .collect(Collectors.toList());
            return collect;
        } catch (Exception e) {
            return List.of();
        }
    }
}
