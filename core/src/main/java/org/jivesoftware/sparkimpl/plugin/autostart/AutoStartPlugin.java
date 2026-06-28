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
package org.jivesoftware.sparkimpl.plugin.autostart;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;
import org.jivesoftware.Spark;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

import java.io.File;

public class AutoStartPlugin implements Plugin {

    @Override
    public void initialize() {
        SettingsManager.addPreferenceListener(this::updateAutoStart);
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

    private void updateAutoStart(LocalPreferences prefs) {
        boolean startOnStartup = prefs.isStartOnStartup();
        if (startOnStartup) {
            if (Spark.isWindows()) {
                addToAutostartWindows();
            }
        } else {
            if (Spark.isWindows()) {
                removeFromAutostartWindows();
            }
        }
    }

    private static void addToAutostartWindows() {
        try {
            // Persists autostart via Windows registry, if executable exists
            String PROGDIR = Spark.getBinDirectory().getParent();
            File exeFile = new File(PROGDIR + "\\" + SparkRes.getString(SparkRes.EXECUTABLE_NAME));
            if (exeFile.exists()) {
                Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER,
                    "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run");
                Advapi32Util.registrySetStringValue(
                    WinReg.HKEY_CURRENT_USER,
                    "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run",
                    SparkRes.getString(SparkRes.APP_NAME),
                    exeFile.getAbsolutePath());
            }
        } catch (Exception e) {
            Log.error("Error enabling start on reboot", e);
        }
    }

    private static void removeFromAutostartWindows() {
        if (!Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER,
            "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run",
            SparkRes.getString(SparkRes.APP_NAME))) {
            return;
        }
        try {
            Advapi32Util.registryDeleteValue(
                WinReg.HKEY_CURRENT_USER,
                "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run",
                SparkRes.getString(SparkRes.APP_NAME));
        } catch (Exception e) {
            Log.error("Can not delete registry entry", e);
        }
    }

}
