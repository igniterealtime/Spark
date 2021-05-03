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
package org.jivesoftware.launcher;

import java.io.File;
import java.lang.reflect.Method;

/**
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

}     
