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

package org.jivesoftware;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashSet;



public class SparkCompatibility {

    /**
     * Old Spark settings directory
     */
    private final String OLD_USER_SPARK_HOME = System.getProperties().getProperty("user.home") + "/" + getUserConf();
    
    private final String OLD_USER_SPARK_HOME_MAC = System.getProperties().getProperty("user.home") + "/Spark";
    public SparkCompatibility() {
    }
    

    /**
     * Copies the old USER_SPARK_HOME to the new directory if it does not exist 
     * @param userSparkHome
     * 	The previous directory to copy from
     * @param skipFiles
     * 	The files to skip
     * @throws IOException
     */
    public void transferConfig(String userSparkHome, Collection<String> skipFiles)
	    throws IOException {
	File newSparkHomeDir = new File(userSparkHome);
	File oldSparkHomeDir;
	if (System.getProperty("os.name").toLowerCase().contains("mac")) {
	    oldSparkHomeDir = new File(OLD_USER_SPARK_HOME_MAC);
	} else {
	    oldSparkHomeDir = new File(OLD_USER_SPARK_HOME);

	}

	if (!newSparkHomeDir.exists() && oldSparkHomeDir.exists()) {
	    copyDirectory(oldSparkHomeDir, newSparkHomeDir, skipFiles);
	}
    }
    /**
     * Copies the directories / files recursively 
     * @param src
     * The source dir/file to copy
     * @param dest
     * The destination dir/file to copy
     * @param skipFiles
     * A colleciton of files to skip
     * @throws IOException
     */
    private void copyDirectory(File src , File dest, Collection<String> skipFiles) throws IOException {
        if (src.isDirectory()) {
            if (!dest.exists()) {
                dest.mkdir();
            }
            String[] children = src.list();
            for (int i=0; i<children.length; i++) {
            	// Skip any directories / files which may need to be skipped.  
            	if (!skipFiles.contains((new File(dest, children[i]).getAbsolutePath()))) {
	            	copyDirectory(new File(src, children[i]),
	                        new File(dest, children[i]), new HashSet<>());
            	}
            }
        } else {
        	InputStream in;
        	OutputStream out;
        	
        	try { 
        		in = new FileInputStream(src);
        		out = new FileOutputStream(dest);
        	} catch (FileNotFoundException e) {
        		IOException wrapper = new IOException("copyDirectory: Unable to open handle on file: "
        				+ src.getAbsolutePath() + "and" + dest.getAbsolutePath() + ".");
        				wrapper.initCause(e);
        				wrapper.setStackTrace(e.getStackTrace());
        				throw wrapper;
        	} catch (SecurityException e) {
        		IOException wrapper = new IOException("copyDirectory: access denied to copy file: "
        				+ src.getAbsolutePath() + "and" + dest.getAbsolutePath() + ".");
				wrapper.initCause(e);
				wrapper.setStackTrace(e.getStackTrace());
				throw wrapper;
        	}
        	try { 
	            // Copy the bits from instream to outstream
	            byte[] buf = new byte[1024];
	            int len;
	            while ((len = in.read(buf)) > 0) {
	                out.write(buf, 0, len);
	            }
        	} catch (IOException e) {
        		IOException wrapper = new IOException("copyDirectory: Unable to copy file: "
        				+ src.getAbsolutePath() + "to" + dest.getAbsolutePath() + ".");
        				wrapper.initCause(e);
        				wrapper.setStackTrace(e.getStackTrace());
        				throw wrapper;
        	} finally {
        		in.close();
        		out.close();
        	}
        }
    }

    
    
    /**
     * Keep track of the users configuration directory.
     *
     * @return Directory name depending on Operating System.
     */
    private static String getUserConf() {
	return Spark.getUserConf();
    }
}
