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
package org.jivesoftware.spark.plugin;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smack.provider.IqProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.xml.SmackXmlParser;
import org.jivesoftware.smack.xml.XmlPullParser;
import org.jivesoftware.spark.util.URLFileSystem;
import org.jivesoftware.spark.util.log.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static java.nio.charset.StandardCharsets.UTF_8;

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
        super(new URL[]{libDir.toURI().toURL()}, parent);
    }

    /**
     * Adds all archives in a plugin to the classpath.
     *
     * @param pluginDir the directory of the plugin.
     * @throws MalformedURLException the exception thrown if URL is not valid.
     */
    public void addPlugin(File pluginDir) throws MalformedURLException {
        File libDir = new File(pluginDir, "lib");

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
                final URL url = jar.toURI().toURL();
                addURL(url);
            }
        }
    }

}
