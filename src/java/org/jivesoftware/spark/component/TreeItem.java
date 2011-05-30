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

import java.io.Serializable;

public class TreeItem implements Serializable {
    private static final long serialVersionUID = 4892011237317645034L;
    private String displayName;
    private String toolTip;
    private String description;
    private String editor;
    private String extraData;
    private int type;

    public TreeItem() {
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    public TreeItem(String displayName,
                    String tooltip,
                    String description,
                    String editor) {
        this.displayName = displayName;
        this.toolTip = tooltip;
        this.description = description;
        this.editor = editor;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }


    public String getDisplayName() {
        return this.displayName;
    }


    public void setToolTip(String toolTip) {
        this.toolTip = toolTip;
    }


    public String getToolTip() {
        return this.toolTip;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public String getDescription() {
        return this.description;
    }


    public void setEditor(String editor) {
        this.editor = editor;
    }


    public String getEditor() {
        return this.editor;
    }


    public void setExtraData(String extraData) {
        this.extraData = extraData;
    }


    public String getExtraData() {
        return this.extraData;
    }
}
