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
package org.jivesoftware.spark.component;

import org.jivesoftware.spark.plugin.ContextMenuListener;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class TreeFolder implements Serializable {
    private static final long serialVersionUID = 2692119297116147123L;
    private Set<TreeFolder> subFolders = new HashSet<TreeFolder>();
    private Set<TreeItem> paletteItems = new HashSet<TreeItem>();
    private String displayName;
    private String description;
    private String icon;
    private ContextMenuListener listener;

    public TreeFolder() {
        // Allow user the flexibilty to create
    }

    public TreeFolder(String displayName, String description, String icon) {
        this.displayName = displayName;
        this.description = description;
        this.icon = icon;
    }

    public void addSubFolder(TreeFolder folder) {
        subFolders.add(folder);
    }

    public void removeSubFolder(TreeFolder folder) {
        subFolders.remove(folder);
    }

    public Iterator<TreeFolder> getSubFolders() {
        return subFolders.iterator();
    }

    public void addPaletteItem(TreeItem item) {
        paletteItems.add(item);
    }

    public void removePaletteItem(TreeItem item) {
        paletteItems.remove(item);
    }

    public Iterator<TreeItem> getPaletteItems() {
        return paletteItems.iterator();
    }


    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }


    public String getDisplayName() {
        return displayName;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public String getDescription() {
        return description;
    }


    public void setIcon(String icon) {
        this.icon = icon;
    }


    public String getIcon() {
        return icon;
    }


    public void setListener(ContextMenuListener listener) {
        this.listener = listener;
    }


    public ContextMenuListener getListener() {
        return listener;
    }
}
