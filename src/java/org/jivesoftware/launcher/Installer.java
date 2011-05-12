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
package org.jivesoftware.launcher;


import com.install4j.api.actions.InstallAction;
import com.install4j.api.context.Context;
import com.install4j.api.context.InstallerContext;
import com.install4j.api.context.UserCanceledException;
import com.install4j.api.windows.RegistryRoot;
import com.install4j.api.windows.WinRegistry;

import java.io.File;

/**
 * The installer class is used by the Install4j Installer to setup registry entries
 * during the setup process.
 */
public class Installer implements InstallAction {

    public int getPercentOfTotalInstallation() {
        return 0;
    }


    public void init(Context context) {
    }

    public boolean install(InstallerContext installerContext) throws UserCanceledException {
        final String osName = System.getProperty("os.name").toLowerCase();
        boolean isWindows = osName.startsWith("windows");

        if (!isWindows) {
            return true;
        }

        final File sparkDirectory;
        String sparkPath = "";
        try {
            sparkDirectory = new File(installerContext.getInstallationDirectory(), "Spark.exe");
            sparkPath = sparkDirectory.getCanonicalPath();

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        if (sparkPath != null && sparkPath.length() > 0) {
            // Add Spark to startup
            addSparkToStartup(sparkPath);

            // Add Spark XMPP:URI mapping
            setURI(sparkPath);
        }


        return true;
    }

    public void rollback(InstallerContext installerContext) {
        WinRegistry.deleteValue(RegistryRoot.HKEY_CURRENT_USER, "Software\\Microsoft\\Windows\\CurrentVersion\\Run", "Spark");
    }

    /**
     * Adds Spark to the users registry.
     *
     * @param sparkPath the canonical path to spark.
     */
    public void addSparkToStartup(String sparkPath) {
        try {
            WinRegistry.setValue(RegistryRoot.HKEY_CURRENT_USER, "Software\\Microsoft\\Windows\\CurrentVersion\\Run", "Spark", sparkPath);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds Spark to the users registry to allow for XMPP URI mapping.
     *
     * @param path the installation directory of spark.
     */
    private void setURI(String path) {
        boolean exists = WinRegistry.keyExists(RegistryRoot.HKEY_CLASSES_ROOT, "xmpp");
        if (exists) {
        }
        //   JOptionPane.showConfirmDialog(null, "Another application is currently registered to handle XMPP instant messaging. Make Spark the default XMPP instant messaging client?", "Confirmation",         }
        WinRegistry.deleteKey(RegistryRoot.HKEY_CLASSES_ROOT, "xmpp", true);

        WinRegistry.createKey(RegistryRoot.HKEY_CLASSES_ROOT, "xmpp");
        WinRegistry.setValue(RegistryRoot.HKEY_CLASSES_ROOT, "xmpp", "", "URL:XMPP Address");
        WinRegistry.setValue(RegistryRoot.HKEY_CLASSES_ROOT, "xmpp", "URL Protocol", "");

        WinRegistry.createKey(RegistryRoot.HKEY_CLASSES_ROOT, "xmpp\\shell\\open\\command");
        WinRegistry.setValue(RegistryRoot.HKEY_CLASSES_ROOT, "xmpp\\shell\\open\\command", "", path + " %1");
    }


}
