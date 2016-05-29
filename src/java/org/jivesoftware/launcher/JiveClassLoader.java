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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * A simple classloader to extend the classpath to
 * include all jars in a lib directory.<p>
 * <p/>
 * The new classpath includes all <tt>*.jar</tt> and <tt>*.zip</tt>
 * archives (zip is commonly used in packaging JDBC drivers). The extended
 * classpath is used for both the initial startup, as well as loading
 * plug-in support jars.
 *
 * @author Derek DeMoro
 */
class JiveClassLoader extends URLClassLoader {

    /**
     * Constructs the classloader.
     *
     * @param parent the parent class loader (or null for none).
     * @param libDir the directory to load jar files from.
     * @throws java.net.MalformedURLException if the libDir path is not valid.
     */
    JiveClassLoader(ClassLoader parent, File libDir) throws MalformedURLException {
        super(new URL[]{libDir.toURI().toURL()}, parent);

        File[] jars = libDir.listFiles( ( dir, name ) -> {
            boolean accept = false;
            String smallName = name.toLowerCase();
            if (smallName.endsWith(".jar")) {
                accept = true;
            }
            else if (smallName.endsWith(".zip")) {
                accept = true;
            }
            return accept;
        } );

        // Do nothing if no jar or zip files were found
        if (jars == null) {
            return;
        }

        for (File jar : jars) {
            if (jar.isFile()) {
                addURL(jar.toURI().toURL());
            }
        }
    }
}
