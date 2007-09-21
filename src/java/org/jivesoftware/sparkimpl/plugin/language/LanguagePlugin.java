/**
 * $Revision$
 * $Date$
 *
 * Copyright (C) 1999-2005 Jive Software. All rights reserved.
 * This software is the proprietary information of Jive Software. Use is subject to license terms.
 */

package org.jivesoftware.sparkimpl.plugin.language;

import org.jivesoftware.resource.Res;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.util.URLFileSystem;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JOptionPane;

import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.Enumeration;
import java.util.Locale;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipFile;

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

        locales = Locale.getAvailableLocales();

        // Load files
        URL sparkJar = getClass().getClassLoader().getResource("spark.jar");

        try {
            ZipFile zipFile = new JarFile(URLFileSystem.url2File(sparkJar));
            for (Enumeration e = zipFile.entries(); e.hasMoreElements();) {
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
            zipFile = null;
        }
        catch (Throwable e) {
            Log.error("Error unzipping plugin", e);
        }

        actionsMenu.add(languageMenu);
    }

    private void addLanguage(String language) {
        for (int i = 0; i < locales.length; i++) {
            final Locale locale = locales[i];
            if (locale.getLanguage().equals(language)) {
                Action action = new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        final LocalPreferences preferences = SettingsManager.getLocalPreferences();
                        preferences.setLanguage(locale.getLanguage());

                        int ok = JOptionPane.showConfirmDialog(SparkManager.getMainWindow(), Res.getString("message.restart.required"), Res.getString("title.confirmation"), JOptionPane.YES_NO_OPTION);
                        if (ok == JOptionPane.YES_OPTION) {
                            SparkManager.getMainWindow().shutdown();
                        }
                    }
                };

                action.putValue(Action.NAME, locale.getDisplayLanguage(locale));
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
