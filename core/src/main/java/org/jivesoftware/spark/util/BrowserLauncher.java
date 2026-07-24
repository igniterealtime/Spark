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

import org.jivesoftware.Spark;
import org.jivesoftware.spark.util.log.Log;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;

import static org.apache.commons.lang3.StringUtils.trimToNull;

@SuppressWarnings("HttpUrlsUsage")
public class BrowserLauncher {

    /**
     * Opens a URL, file path, Windows UNC path, or Samba share when clicked in a chat.
     * Supports: http(s), ftp, file, www, smb://, \\server\share, and local file paths.
     *
     * @param url the URL or path to open
     */
    public static void openURL(String url) {
        url = trimToNull(url);
        if (url == null) {
            Log.error("Cannot open empty or null URL");
            return;
        }
        try {
            // Handle web protocols
            if (isWebProtocol(url)) {
                openWebUrl(url);
                return;
            }
            // Handle Samba/SMB URLs
            if (url.startsWith("smb://") || url.startsWith("nfs://")) {
                openSambaUrl(url);
                return;
            }
            // Handle Windows UNC paths
            if (isWindowsUncPath(url)) {
                openInFileManager(url);
                return;
            }
            if (url.startsWith("webdav://") || url.startsWith("dav://") || url.startsWith("davs://")) {
                openWebDav(url);
                return;
            }
            if (url.startsWith("sftp://") ||
                url.startsWith("ftp://") || url.startsWith("ftps://")) {
                openInFileManager(url);
                return;
            }
            // Handle file:// with spaces
            if (url.startsWith("file://")) {
                url = url.substring(7).replace("%20", " ");
            }
            // Handle local file paths
            File f = new File(url);
            if (f.exists()) {
                openInFileManager(f.getAbsolutePath());
                return;
            }
            // Last resort: try as URL with https prefix
            openAsHttpsUrl(url);
        } catch (Exception e) {
            e.printStackTrace();
            Log.error("Unable to open url: " + url, e);
        }
    }

    /**
     * Checks if the URL uses a web protocol (http, https, ftp, file, www).
     */
    private static boolean isWebProtocol(String url) {
        return url.startsWith("https://") || url.startsWith("http://") || url.startsWith("www.");
    }

    /**
     * Checks if the path is a Windows UNC path (starts with \\ or //).
     */
    private static boolean isWindowsUncPath(String path) {
        return path.startsWith("\\\\") || path.startsWith("//");
    }

    /**
     * Opens a Samba URL (smb://server/share).
     * On Windows, converts to UNC path
     */
    private static void openSambaUrl(String url) throws IOException {
        if (Spark.isWindows()) {
            // Convert smb://server/share to \\server\share
            String uncPath = url.substring(6); // Remove "smb://" or "nfs://"
            uncPath = "\\\\" + uncPath.replace("/", "\\");
            openInFileManager(uncPath);
            return;
        }
        openInFileManager(url);
    }

    private static void openWebDav(String url) throws IOException {
        if (Spark.isWindows()) {
            // Convert davs://server/share to https://server/share
            String davUrl = url.replace("webdav://", "http://");
            davUrl = davUrl.replace("dav://", "http://");
            davUrl = davUrl.replace("davs://", "https://");
            openInFileManager(davUrl);
            return;
        }
        openInFileManager(url);
    }

    public static void openInFileManager(String path) {
        File file = new File(path);
        openInFileManager(file);
    }

    public static void openInFileManager(File file) {
        try {
            Desktop.getDesktop().open(file);
        } catch (IOException e) {
            Log.warning("Unable to open file directly, opening in edit mode: " + file + ": " + e);
            try {
                Desktop.getDesktop().edit(file);
            } catch (IOException e2) {
                Log.warning("Unable to open file directly, opening containing folder instead: " + file + ": " + e2);
                try {
                    openContainingFolder(file);
                } catch (IOException e3) {
                    Log.error("Unable to open folder: " + file + ": " + e3);
                }
            }
        }
    }

    /**
     * Opens a URL with a recognized web protocol.
     */
    private static void openWebUrl(String url) throws Exception {
        // Handle www. prefix
        if (url.startsWith("www.")) {
            url = "https://" + url;
        }
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception ex) {
            // Fallback for Linux systems
            Runtime.getRuntime().exec(new String[]{"xdg-open", url});
        }
    }

    /**
     * Attempts to open a URL by adding https:// prefix.
     */
    private static void openAsHttpsUrl(String url) throws Exception {
        if (!url.startsWith("//")) {
            url = "//" + url;
        }
        URI uri = new URI("https:" + url);
        Desktop.getDesktop().browse(uri);
    }

    private static void openContainingFolder(File file) throws IOException {
        if (Spark.isWindows()) {
            Runtime.getRuntime().exec(new String[]{
                "explorer.exe",
                "/select," + file.getAbsolutePath()
            });
            return;
        }

        File parent = file.getParentFile();
        if (parent != null) {
            Desktop.getDesktop().open(parent);
        } else {
            throw new IOException("Unable to determine parent folder for: " + file);
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

    public static void main(String[] args) throws InterruptedException {
//        openURL("C:\\Users\\Admin\\Desktop\\avatar.jpg");
//        openURL("C:\\Users\\Admin\\Desktop\\avatar.jpg");
        openURL("D:\\work\\Spark\\core\\src\\main\\resources\\images\\alert.png");
//        openURL("ftp://jkl.mn");
//        openURL("ftp://jkl.mn");
    }
}
