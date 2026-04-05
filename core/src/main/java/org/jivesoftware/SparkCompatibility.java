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

package org.jivesoftware;

import org.apache.commons.lang3.SystemUtils;
import org.jivesoftware.spark.util.log.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;


public class SparkCompatibility {

    /**
     * Copies the old USER_SPARK_HOME to the new directory if it does not exist
     */
    public static void transferConfig(File newSparkHomeDir) {
        if (newSparkHomeDir.exists()) {
            return;
        }
        // Old Spark settings directory
        File oldSparkHomeDir = new File(System.getProperty("user.home"), SystemUtils.IS_OS_MAC ? "/Spark" : Spark.getUserConf());
        if (oldSparkHomeDir.exists()) {
            // Absolute paths to a collection of files or directories to skip
            Collection<String> skipFiles = List.of(
                new File(newSparkHomeDir, "plugins").getAbsolutePath()
            );
            try {
                copyDirectory(oldSparkHomeDir, newSparkHomeDir, skipFiles);
            } catch (IOException e) {
               Log.error("Unable to copy old Spark home directory", e);
            }
        }
    }

    /**
     * Copies the directories / files recursively
     *
     * @param src       The source dir/file to copy
     * @param dest      The destination dir/file to copy
     * @param skipFiles A collection of files to skip
     */
    private static void copyDirectory(File src, File dest, Collection<String> skipFiles) throws IOException {
        if (src.isDirectory()) {
            if (!dest.exists()) {
                dest.mkdir();
            }
            String[] children = src.list();
            children = children != null ? children : new String[]{};
            for (String child : children) {
                // Skip any directories / files which may need to be skipped.
                if (!skipFiles.contains((new File(dest, child).getAbsolutePath()))) {
                    copyDirectory(new File(src, child), new File(dest, child), new HashSet<>());
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
                    + src.getAbsolutePath() + "and" + dest.getAbsolutePath() + ".", e);
                wrapper.setStackTrace(e.getStackTrace());
                throw wrapper;
            } catch (SecurityException e) {
                IOException wrapper = new IOException("copyDirectory: access denied to copy file: "
                    + src.getAbsolutePath() + "and" + dest.getAbsolutePath() + ".", e);
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
                    + src.getAbsolutePath() + "to" + dest.getAbsolutePath() + ".", e);
                wrapper.setStackTrace(e.getStackTrace());
                throw wrapper;
            } finally {
                in.close();
                out.close();
            }
        }
    }

}
