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
package com.jivesoftware.spark.plugin.growl;

import org.jivesoftware.Spark;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.plugin.Plugin;

import java.io.*;

/**
 * Provides support for Growl Notifications on Mac OS X. Growl can be acquired
 * at http://growl.info
 * 
 * 
 * @author Wolf.Posdorfer
 */
public class GrowlPlugin implements Plugin {

    private GrowlMessageListener growlListener;

    public void initialize() {
	boolean b = placeLibs();
	try {
	    Thread.sleep(1500);
	} catch (InterruptedException e) {
	}

	if (!b) {
	    placeLibs();
	    try {
		Thread.sleep(1500);
	    } catch (InterruptedException e) {
	    }
	}

	GrowlPreference preference = new GrowlPreference();
	// SparkManager.getPreferenceManager().addPreference(preference);
	growlListener = new GrowlMessageListener();
	SparkManager.getChatManager().addGlobalMessageListener(growlListener);
    }

    public void shutdown() {
	SparkManager.getChatManager().removeGlobalMessageListener(growlListener);
    }

    public boolean canShutDown() {
	return true;
    }

    public void uninstall() {
	SparkManager.getChatManager().removeGlobalMessageListener(growlListener);
    }

    /**
     * Places the libgrowl.jnilib into /Library/Java/Extensions
     * 
     * @return
     */
    private boolean placeLibs() {

	boolean result;

	File f = new File(Spark.getUserHome() + "/Library/Java/Extensions/libgrowl.jnilib");

	if (f.exists()) {
	    return true;
	}

	String home = Spark.getSparkUserHome() + "/plugins/growl/lib/";
	File library = new File(home + "libgrowl.jnilib");

	System.out.println(f.exists() + " " + library.exists());

	try {
	    InputStream in = new FileInputStream(library);
	    OutputStream out = new FileOutputStream(f);

	    // Transfer bytes from in to out
	    byte[] buf = new byte[1024];
	    int len;
	    while ((len = in.read(buf)) > 0) {
		out.write(buf, 0, len);
	    }
	    in.close();
	    out.close();
	} catch (IOException e) {
	    e.printStackTrace();
	}

	result = f.exists();

	return result;

    }

}
