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

import org.jivesoftware.spark.util.log.Log;


public class SparkCompatibility {
    private final String USER_SPARK_HOME = System.getProperties().getProperty("user.home") + "/" + getUserConf();
    
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
    public void transferConfig(String userSparkHome, Collection<String> skipFiles) throws IOException {
    	Log.debug("Transferring settings from: " + USER_SPARK_HOME);
    	Log.debug("To: " + userSparkHome);
    	File newSparkHomeDir = new File(userSparkHome);
    	File oldSparkHomeDir = new File(USER_SPARK_HOME);
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
	                        new File(dest, children[i]), new HashSet<String>());
            	}
            }
        } else {
        	InputStream in = null;
        	OutputStream out = null;
        	
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
    private String getUserConf() {
        if (Spark.isLinux()) {
            return ".Spark";
        }

        return "Spark";
    }
}
