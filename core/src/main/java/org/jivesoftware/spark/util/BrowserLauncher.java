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
package org.jivesoftware.spark.util;

import org.jivesoftware.spark.util.log.Log;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;

public class BrowserLauncher {

    public static void openURL(String url) {
        try {
        if (url.startsWith("http") || url.startsWith("ftp") || url.startsWith("file") || url.startsWith("www")) {
            if (url.startsWith("file") && url.contains(" ")) {
                url = url.replace(" ", "%20");
            }
            if (url.startsWith("www")) {
                url = "http://" + url;
            }
            try {
                Desktop.getDesktop().browse(new URI(url));
            } catch (Exception ex) {
                // fallback on Linux
                Runtime.getRuntime().exec("xdg-open " + url);
            }
        } else {
            File f = new File(url);
            if (f.exists() && Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().open(f);
                } catch (Exception ex) {
                    if (!url.startsWith("//")) {
                        url = "//" + url;
                    }
                    Desktop.getDesktop().browse(new URI("http:" + url));
                }
            }
        }
        }
        catch (Exception e) {
            Log.error("Unable to open url " + url, e);
        }
    }

    public static void openFolder(File file) {
        if (!Desktop.isDesktopSupported())
            return;
        Desktop dt = Desktop.getDesktop();
        try {
            dt.open(file);
        } catch (IOException ex) {
            Log.error("Unable to open folder: " + file, ex);
        }
    }
}
