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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200;

/**
 *
 */
public class Startup {

    /**
     * Default to this location if one has not been specified
     */
    private static final String DEFAULT_LIB_DIR = "../lib";

    public static void main(String[] args) {
        new Startup().start(args);
    }

    /**
     * Starts the server by loading and instantiating the bootstrap
     * container. Once the start method is called, the server is
     * started and the server starter should not be used again.
     *
     * @param args the arguments passed into this initial instance of Spark.
     */
    private void start(String[] args) {
        // Setup the classpath using JiveClassLoader
        try {
            // Load up the bootstrap container
            final ClassLoader parent = findParentClassLoader();

            File libDir;
            final String workingDirectory = System.getProperty("appdir");
            if (workingDirectory == null) {
                libDir = new File(DEFAULT_LIB_DIR);
            }
            else {
                libDir = new File(new File(workingDirectory), "lib");
            }
            
            File pluginDir = new File(libDir.getParentFile(), "plugins");

            // Unpack any pack files in lib.
            unpackArchives(libDir, true);

            // Unpack plugins.
            unpackArchives(pluginDir, true);

            // Load them into the classloader
            final ClassLoader loader = new JiveClassLoader(parent, libDir);

            Thread.currentThread().setContextClassLoader(loader);

            // Get class
            Class<?> sparkClass = loader.loadClass("org.jivesoftware.Spark");
            Object instanceOfSpark = sparkClass.newInstance();

            // Handle arguments
            if (args.length > 0) {
                String argument = args[0];
                Method setArgument = sparkClass.getMethod("setArgument", String.class);
                setArgument.invoke(instanceOfSpark, argument);
            }

            Method startupMethod = sparkClass.getMethod("startup");
            startupMethod.invoke(instanceOfSpark);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Locates the best class loader based on context (see class description).
     *
     * @return The best parent classloader to use
     */
    private ClassLoader findParentClassLoader() {
        ClassLoader parent = Thread.currentThread().getContextClassLoader();
        if (parent == null) {
            parent = this.getClass().getClassLoader();
            if (parent == null) {
                parent = ClassLoader.getSystemClassLoader();
            }
        }
        return parent;
    }

    /**
     * Converts any pack files in a directory into standard JAR files. Each
     * pack file will be deleted after being converted to a JAR. If no
     * pack files are found, this method does nothing.
     *
     * @param libDir      the directory containing pack files.
     * @param printStatus true if status ellipses should be printed when unpacking.
     */
    private void unpackArchives(File libDir, boolean printStatus) {
        // Get a list of all packed files in the lib directory.
        File[] packedFiles = libDir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".pack");
            }
        });

        if (packedFiles == null) {
            // Do nothing since no .pack files were found
            return;
        }

        // Unpack each.
        boolean unpacked = false;
        for (File packedFile : packedFiles) {
            try {
                String jarName = packedFile.getName().substring(0,
                    packedFile.getName().length() - ".pack".length());
                // Delete JAR file with same name if it exists (could be due to upgrade
                // from old Wildfire release).
                File jarFile = new File(libDir, jarName);
                if (jarFile.exists()) {
                    jarFile.delete();
                }

                InputStream in = new BufferedInputStream(new FileInputStream(packedFile));
                JarOutputStream out = new JarOutputStream(new BufferedOutputStream(
                    new FileOutputStream(new File(libDir, jarName))));
                Pack200.Unpacker unpacker = Pack200.newUnpacker();
                // Print something so the user knows something is happening.
                if (printStatus) {
                    System.out.print(".");
                }
                // Call the unpacker
                unpacker.unpack(in, out);

                in.close();
                out.close();
                packedFile.delete();
                unpacked = true;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        // Print newline if unpacking happened.
        if (unpacked && printStatus) {
            System.out.println();
        }
    }
}     