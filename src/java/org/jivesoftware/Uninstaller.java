/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware;

import com.install4j.api.Context;
import com.install4j.api.ProgressInterface;
import com.install4j.api.UninstallAction;
import com.install4j.api.windows.WinRegistry;

import java.io.File;

/**
 * Performs registry operations on removal of the Spark client. This is a windows only function.
 *
 * @author Derek DeMoro
 */
public class Uninstaller extends UninstallAction {

    public int getPercentOfTotalInstallation() {
        return 0;
    }

    public boolean performAction(Context context, ProgressInterface progressInterface) {
        return super.performAction(context, progressInterface);    //To change body of overridden methods use File | Settings | File Templates.
    }

    /**
     * Removes Spark from the start up registration in the registry.
     *
     * @param dir the directory where Spark resides.
     */
    public void removeStartup(File dir) {
        WinRegistry.deleteValue(WinRegistry.HKEY_CURRENT_USER, "Software\\Microsoft\\Windows\\CurrentVersion\\Run", "Spark");
    }
}
