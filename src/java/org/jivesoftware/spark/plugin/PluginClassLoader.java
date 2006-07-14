/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.spark.plugin;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * A simple classloader to extend the classpath to
 * include all jars in a lib directory.<p>
 * <p/>
 * The new classpath includes all <tt>*.jar files.
 *
 * @author Derek DeMoro
 */
public class PluginClassLoader extends URLClassLoader {

    /**
     * Constructs the classloader.
     *
     * @param parent the parent class loader (or null for none).
     * @param libDir the directory to load jar files from.
     * @throws java.net.MalformedURLException if the libDir path is not valid.
     */
    public PluginClassLoader(ClassLoader parent, File libDir) throws MalformedURLException {
        super(new URL[]{libDir.toURL()}, parent);
    }

    /**
     * Adds all archives in a plugin to the classpath.
     *
     * @param pluginDir the directory of the plugin.
     * @throws MalformedURLException the exception thrown if URL is not valid.
     */
    public void addPlugin(File pluginDir) throws MalformedURLException {
        File libDir = new File(pluginDir, "lib");

        File[] jars = libDir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                boolean accept = false;
                String smallName = name.toLowerCase();
                if (smallName.endsWith(".jar")) {
                    accept = true;
                }
                else if (smallName.endsWith(".zip")) {
                    accept = true;
                }
                return accept;
            }
        });

        // Do nothing if no jar or zip files were found
        if (jars == null) {
            return;
        }

        for (int i = 0; i < jars.length; i++) {
            if (jars[i].isFile()) {
                addURL(jars[i].toURL());
            }
        }
    }
}
