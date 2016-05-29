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
package org.jivesoftware.sparkimpl.plugin.filetransfer.transfer;

import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.JFileChooser;

import org.jivesoftware.Spark;
import org.jivesoftware.resource.Res;
import org.jivesoftware.spark.util.WindowsFileSystemView;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

/**
 * Provides Acces to the DownloadDirectory and a JFileChooser starting at the
 * DownloadDirectory
 * 
 */
public class Downloads {
    private static File downloadedDir;
    private static JFileChooser chooser;

	/**
     * Returns the Downloaddirectory
     * 
     * @return the download directory as <code>file</code>
     * @throws NullPointerException 
     * @throws SecurityException 
     * @throws FileNotFoundException 
     */
    public static File getDownloadDirectory() {
	LocalPreferences pref = SettingsManager.getLocalPreferences();
	downloadedDir = new File(pref.getDownloadDir());
	return downloadedDir;
    }

    /**
     * Check if the downloaddirectory is accessable and throws exceptions if not
     * 
     * @param downloadDir
     * @throws FileNotFoundException if the directory not exist
     * @throws SecurityException if user hasn't permissions to write to the directory
     * @throws NullPointerException if the directory is not set in preferences
     * @return true if the directory is ok
     */
    public static synchronized boolean checkDownloadDirectory() throws FileNotFoundException, SecurityException, NullPointerException{
    	// check the downloaddirectory
    	if (Downloads.getDownloadDirectory() == null ){
    		throw new NullPointerException(Res.getString("message.file.transfer.dirnull"));
    	}else if(!Downloads.getDownloadDirectory().exists()){
    		throw new FileNotFoundException(Res.getString("message.file.transfer.nodir"));
    	}else if (!(Downloads.getDownloadDirectory().canWrite() && Downloads.getDownloadDirectory().canExecute())){
    		throw new SecurityException(Res.getString("message.file.transfer.cantwritedir"));
    	}
    	//tro to create a file to check if we can write to the directory
    	try{
    		File x = File.createTempFile("dltemp", null,Downloads.getDownloadDirectory());
    		x.delete();
    	}catch (Exception cantWriteDir){
    		throw new SecurityException(Res.getString("message.file.transfer.cantwritedir"));
    	}
    	return true;
    }
    
    
    /**
     * Returns a {@link JFileChooser} starting at the DownloadDirectory
     * 
     * @return the filechooser
     */
    public static JFileChooser getFileChooser() {
	if (chooser == null) {

		LocalPreferences _localPreferences = SettingsManager.getLocalPreferences();
	    downloadedDir = new File( _localPreferences.getDownloadDir());

	    chooser = new JFileChooser(downloadedDir);
	    if (Spark.isWindows()) {
		chooser.setFileSystemView(new WindowsFileSystemView());
	    }
	}
	return chooser;
    }
}
