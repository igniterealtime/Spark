/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkimpl.plugin.filetransfer.transfer;

import java.io.File;

import javax.swing.JFileChooser;

import org.jivesoftware.Spark;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.util.WindowsFileSystemView;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

public class Downloads {
    private static File downloadedDir;
    private static JFileChooser chooser;
    
    public static File getDownloadDirectory() {
		LocalPreferences pref = SettingsManager.getLocalPreferences();
		downloadedDir = new File(pref.getDownloadDir());
		downloadedDir.mkdirs();
    	
    	return downloadedDir;
    }
    
    public static JFileChooser getFileChooser() {
        if (chooser == null) {
            downloadedDir = new File(SparkManager.getUserDirectory(), "downloads");
            if (!downloadedDir.exists()) {
                downloadedDir.mkdirs();
            }
            chooser = new JFileChooser(downloadedDir);
            if (Spark.isWindows()) {
                chooser.setFileSystemView(new WindowsFileSystemView());
            }
        }
        return chooser;
    }
}

 
