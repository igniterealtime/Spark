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
import java.util.Locale;

/**
 * Allows for changing of default languages within Spark.
 *
 * @author Derek DeMoro
 */
public class LanguagePlugin implements Plugin {

    private Locale[] locales;
    private JMenu languageMenu;

    @Override
	public void initialize() {
        // Register with action menu
        final JMenu actionsMenu = SparkManager.getMainWindow().getMenuByName(Res.getString("menuitem.actions"));

        languageMenu = new JMenu(Res.getString("menuitem.languages"));
        languageMenu.setIcon(SparkRes.getImageIcon("LANGUAGE_ICON"));

        locales = Locale.getAvailableLocales();

        // Load files
        for (final Locale locale : locales) {
            final String targetI18nFileName;
            if (locale.toString().equals("en")) {
                targetI18nFileName = "/i18n/spark_i18n.properties";
            } else {
                targetI18nFileName = "/i18n/spark_i18n_" + locale.toString() + ".properties";
            }

            // If we can find an translation file for this locale, we can support the language!
            if (getClass().getResource( targetI18nFileName ) != null) {
                addLanguage(locale);
            }
        }

        actionsMenu.add(languageMenu);
    }

    private void addLanguage(Locale locale) {
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
        String label = locale.getDisplayLanguage(locale);
        if (locale.getDisplayCountry(locale) != null &&
            locale.getDisplayCountry(locale).trim().length() > 0) {
            label = label + "-" + locale.getDisplayCountry(locale).trim();
        }
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
}
