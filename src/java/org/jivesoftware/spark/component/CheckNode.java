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

import java.util.Enumeration;

import javax.swing.Icon;

/**
 * Creates one tree node with a check box.
 */
public class CheckNode extends JiveTreeNode {
    /**
     * Mode to use if the node should not expand when selected.
     */
    public static final int SINGLE_SELECTION = 0;

    /**
     * Mode to use if the node should be expaned if selected and if possible.
     */
    public static final int DIG_IN_SELECTION = 4;

    private int selectionMode;
    private boolean isSelected;
    private String fullName;
    private Object associatedObject;

    /**
     * Construct an empty node.
     */
    public CheckNode() {
        this(null);
    }

    /**
     * Creates a new CheckNode with the specified name.
     *
     * @param userObject the name to use.
     */
    public CheckNode(Object userObject) {
        this(userObject, true, false);
    }

    /**
     * Constructs a new CheckNode.
     *
     * @param userObject     the name to use.
     * @param allowsChildren true if it allows children.
     * @param isSelected     true if it is to be selected.
     */
    public CheckNode(Object userObject, boolean allowsChildren, boolean isSelected) {
        super(userObject, allowsChildren);
        this.isSelected = isSelected;
        setSelectionMode(DIG_IN_SELECTION);
    }

     /**
     * Constructs a new CheckNode.
     *
     * @param userObject     the name to use.
     * @param allowsChildren true if it allows children.
     * @param icon the icon to use.
     */
    public CheckNode(String userObject, boolean allowsChildren, Icon icon){
        super(userObject, allowsChildren, icon);
        setSelectionMode(DIG_IN_SELECTION);
    }

    /**
     * Constructs a new CheckNode.
     *
     * @param userObject     the name to use.
     * @param allowsChildren true if it allows children.
     * @param isSelected     true if it is selected.
     * @param name           the identifier name.
     */
    public CheckNode(Object userObject, boolean allowsChildren, boolean isSelected, String name) {
        super(userObject, allowsChildren);
        this.isSelected = isSelected;
        setSelectionMode(DIG_IN_SELECTION);
        fullName = name;
    }

    /**
     * Returns the full name of the node.
     *
     * @return the full name of the node.
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Sets the selection mode.
     *
     * @param mode the selection mode to use.
     */
    public void setSelectionMode(int mode) {
        selectionMode = mode;
    }

    /**
     * Returns the selection mode.
     *
     * @return the selection mode.
     */
    public int getSelectionMode() {
        return selectionMode;
    }

    /**
     * Selects or deselects node.
     *
     * @param isSelected true if the node should be selected, false otherwise.
     */
    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;

        if (selectionMode == DIG_IN_SELECTION
                && children != null) {
            Enumeration nodeEnum = children.elements();
            while (nodeEnum.hasMoreElements()) {
                CheckNode node = (CheckNode)nodeEnum.nextElement();
                node.setSelected(isSelected);
            }
        }
    }

    /**
     * Returns true if the node is selected.
     *
     * @return true if the node is selected.
     */
    public boolean isSelected() {
        return isSelected;
    }

    /**
     * Returns the associated object of this node.
     *
     * @return the associated object.
     */
    public Object getAssociatedObject() {
        return associatedObject;
    }

    /**
     * Sets an assoicated object for this node.
     *
     * @param associatedObject the associated object set.
     */
    public void setAssociatedObject(Object associatedObject) {
        this.associatedObject = associatedObject;
    }
}


