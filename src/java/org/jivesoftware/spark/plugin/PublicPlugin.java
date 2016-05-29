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
package org.jivesoftware.spark.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PublicPlugin {
    private String name;
    private String pluginClass;
    private String version;
    private String author;
    private String email;
    private String description;
    private String homePage;
    private String downloadURL;
    private String changeLogURL;
    private String readMeURL;
    private boolean smallIconAvailable;
    private boolean largeIconAvailable;
    private String minVersion;
    private File pluginDir;
    private List<PluginDependency> dependencies = new ArrayList<>();
    
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

    public String getChangeLog() {
        return changeLogURL;
    }

    public void setChangeLogURL(String changeLogURL) {
        this.changeLogURL = changeLogURL;
    }

    public String getReadMeURL() {
        return readMeURL;
    }

    public void setReadMeURL(String readMeURL) {
        this.readMeURL = readMeURL;
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
    
    public void addDependency(PluginDependency dependency) {
   	 dependencies.add(dependency);
    }
    
    public List<PluginDependency> getDependency() {
   	 return dependencies;
    }
}
