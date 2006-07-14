/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.spark.component;

import java.io.Serializable;

public class TreeItem implements Serializable {
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
