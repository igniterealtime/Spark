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

public class PublicPlugin {
    private String name;
    private String pluginClass;
    private String version;
    private String author;
    private String email;
    private String description;
    private String homePage;
    private String downloadURL;
    private boolean changeLogAvailable;
    private boolean readMeAvailable;
    private boolean smallIconAvailable;
    private boolean largeIconAvailable;
    private String minVersion;

    private File pluginDir;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPluginClass() {
        return pluginClass;
    }

    public void setPluginClass(String pluginClass) {
        this.pluginClass = pluginClass;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHomePage() {
        return homePage;
    }

    public void setHomePage(String homePage) {
        this.homePage = homePage;
    }

    public File getPluginDir() {
        return pluginDir;
    }

    public void setPluginDir(File pluginDir) {
        this.pluginDir = pluginDir;
    }

    public String getDownloadURL() {
        return downloadURL;
    }

    public void setDownloadURL(String downloadURL) {
        this.downloadURL = downloadURL;
    }

    public boolean isChangeLogAvailable() {
        return changeLogAvailable;
    }

    public void setChangeLogAvailable(boolean changeLogAvailable) {
        this.changeLogAvailable = changeLogAvailable;
    }

    public boolean isReadMeAvailable() {
        return readMeAvailable;
    }

    public void setReadMeAvailable(boolean readMeAvailable) {
        this.readMeAvailable = readMeAvailable;
    }

    public boolean isSmallIconAvailable() {
        return smallIconAvailable;
    }

    public void setSmallIconAvailable(boolean smallIconAvailable) {
        this.smallIconAvailable = smallIconAvailable;
    }

    public boolean isLargeIconAvailable() {
        return largeIconAvailable;
    }

    public void setLargeIconAvailable(boolean largeIconAvailable) {
        this.largeIconAvailable = largeIconAvailable;
    }

    public String getMinVersion() {
        return minVersion;
    }

    public void setMinVersion(String minVersion) {
        this.minVersion = minVersion;
    }
}
