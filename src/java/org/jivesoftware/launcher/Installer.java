/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.launcher;

import com.install4j.api.InstallAction;
import com.install4j.api.InstallerWizardContext;
import com.install4j.api.ProgressInterface;
import com.install4j.api.UserCanceledException;
import com.install4j.api.windows.WinRegistry;

import java.io.File;
import java.io.IOException;

/**
 * The installer class is used by the Install4j Installer to setup registry entries
 * during the setup process.
 */
public class Installer extends InstallAction {

    private InstallerWizardContext context;

    public int getPercentOfTotalInstallation() {
        return 0;
    }


    public boolean performAction(InstallerWizardContext installerWizardContext, ProgressInterface progressInterface) throws UserCanceledException {
        context = installerWizardContext;

        final String osName = System.getProperty("os.name").toLowerCase();
        boolean isWindows = osName.startsWith("windows");

        if (!isWindows) {
            return true;
        }

        addSparkToStartup(installerWizardContext.getInstallationDirectory());
        return true;
    }

    /**
     * Adds Spark to the users registry.
     *
     * @param dir the installation directory of Spark.
     */
    public void addSparkToStartup(File dir) {
        final File sparkDirectory = new File(dir, "Spark.exe");
        try {
            final String sparkPath = sparkDirectory.getCanonicalPath();
            WinRegistry.setValue(WinRegistry.HKEY_CURRENT_USER, "Software\\Microsoft\\Windows\\CurrentVersion\\Run", "Spark", sparkPath);

            setURI(sparkPath);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds Spark to the users registry to allow for XMPP URI mapping.
     *
     * @param path the installation directory of spark.
     */
    private void setURI(String path) {
        boolean exists = WinRegistry.keyExists(WinRegistry.HKEY_CLASSES_ROOT, "xmpp");
        if (exists) {
        }
        //   JOptionPane.showConfirmDialog(null, "Another application is currently registered to handle XMPP instant messaging. Make Spark the default XMPP instant messaging client?", "Confirmation",         }
        WinRegistry.deleteKey(WinRegistry.HKEY_CLASSES_ROOT, "xmpp", true);

        WinRegistry.createKey(WinRegistry.HKEY_CLASSES_ROOT, "xmpp");
        WinRegistry.setValue(WinRegistry.HKEY_CLASSES_ROOT, "xmpp", "", "URL:XMPP Address");
        WinRegistry.setValue(WinRegistry.HKEY_CLASSES_ROOT, "xmpp", "URL Protocol", "");

        WinRegistry.createKey(WinRegistry.HKEY_CLASSES_ROOT, "xmpp\\shell\\open\\command");
        WinRegistry.setValue(WinRegistry.HKEY_CLASSES_ROOT, "xmpp\\shell\\open\\command", "", path + " %1");
    }


}
