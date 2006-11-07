/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkimpl.plugin.emoticons;

import org.jivesoftware.Spark;
import org.jivesoftware.spark.plugin.PluginClassLoader;

import java.io.File;

/**
 * Responsible for the handling of all Emoticon packs.
 *
 * @author Derek DeMoro
 */
public class EmoticonManager {

    private static EmoticonManager singleton;
    private static final Object LOCK = new Object();

    /**
     * The root Plugins Directory.
     */
    public static File EMOTICON_DIRECTORY = new File(Spark.getBinDirectory().getParent(), "emoticons").getAbsoluteFile();




    /**
     * Returns the singleton instance of <CODE>EmoticonManager</CODE>,
     * creating it if necessary.
     * <p/>
     *
     * @return the singleton instance of <Code>EmoticonManager</CODE>
     */
    public static EmoticonManager getInstance() {
        // Synchronize on LOCK to ensure that we don't end up creating
        // two singletons.
        synchronized (LOCK) {
            if (null == singleton) {
                EmoticonManager controller = new EmoticonManager();
                singleton = controller;
                return controller;
            }
        }
        return singleton;
    }

    private EmoticonManager() {

    }
}
