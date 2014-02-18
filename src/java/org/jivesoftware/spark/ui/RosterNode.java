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
package org.jivesoftware.spark.ui;

import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.packet.Presence;

import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 */
public class RosterNode extends DefaultMutableTreeNode {
	private static final long serialVersionUID = -3043224462615651820L;
	private String name;
    private boolean isGroup;

    private Icon openIcon;
    private Icon closedIcon;

    private Presence presence;
    private String fullJID;

    public RosterNode() {
        super("root");
    }

    public RosterNode(String name, boolean isGroup) {
        super(name, true);

        this.name = name;

        this.isGroup = isGroup;
        if (isGroup) {
            openIcon = SparkRes.getImageIcon(SparkRes.MINUS_SIGN);
            closedIcon = SparkRes.getImageIcon(SparkRes.PLUS_SIGN);
        }
    }

    public Object getUserObject() {
        return name + " " + getChildCount();
    }

    public RosterNode(String name, String fullJID) {
        super(name, false);
        this.name = name;
        this.fullJID = fullJID;
    }

    /**
     * Returns the default image used.
     *
     * @return the default image used.
     */
    public Icon getIcon() {
        return closedIcon;
    }

    /**
     * Return the icon that is displayed when the node is expanded.
     *
     * @return the open icon.
     */
    public Icon getOpenIcon() {
        return openIcon;
    }

    /**
     * Returns the icon that is displayed when the node is collapsed.
     *
     * @return the closed icon.
     */
    public Icon getClosedIcon() {
        return closedIcon;
    }

    /**
     * Sets the default icon.
     *
     * @param icon the icon.
     */
    public void setOpenIcon(Icon icon) {
        openIcon = icon;
    }

    public void setClosedIcon(Icon icon) {
        closedIcon = icon;
    }

    public boolean isContact() {
        return !isGroup;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public Presence getPresence() {
        return presence;
    }

    public void setPresence(Presence presence) {
        this.presence = presence;
    }

    public String getFullJID() {
        return fullJID;
    }

    public void setFullJID(String fullJID) {
        this.fullJID = fullJID;
    }

    public String getName() {
        return name;
    }
}
