/**
 * $RCSfile: ,v $
 * $Revision: $
 * $Date: $
 * 
 * Copyright (C) 2004-2010 Jive Software. All rights reserved.
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

 
