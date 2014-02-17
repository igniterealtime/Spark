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

import org.jivesoftware.resource.SparkRes;

import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * <code>JiveTreeNode</code> class is a better implementation than using the
 * DefaultMutableTree node. This allows better searching of children/parents as well
 * as handling of icons and drag and drop events.
 *
 * @author Derek DeMoro
 */

public class JiveTreeNode extends DefaultMutableTreeNode implements Transferable {
    private static final long serialVersionUID = 7643497519304035084L;
    private Icon closedImage = null;
    private Icon openImage = null;

    /**
     * Default Drag and Drop to use for Node detection.
     */
    public static final DataFlavor[] DATA_FLAVORS = {new DataFlavor(JiveTreeNode.class, "JiveTreeNodeFlavor")};
    private Object associatedObject;

    /*
    * Create node with closedImage.
    * @param userObject  Name to display
    * @param allowsChildren Specify if node allows children
    * @param img Specify closedImage to use.
    */
    public JiveTreeNode(TreeFolder folder) {
        super(folder.getDisplayName(), true);
        closedImage = SparkRes.getImageIcon(SparkRes.FOLDER_CLOSED);
        openImage = SparkRes.getImageIcon(SparkRes.FOLDER);
        associatedObject = folder;
    }

    /**
     * Create parent node.
     *
     * @param name           the name of the node.
     * @param allowsChildren true if the node allows children.
     */
    public JiveTreeNode(String name, boolean allowsChildren) {
        super(name, allowsChildren);
        if (allowsChildren) {
            closedImage = SparkRes.getImageIcon(SparkRes.FOLDER_CLOSED);
            openImage = SparkRes.getImageIcon(SparkRes.FOLDER);
        }
    }

    /**
     * Creates a new JiveTreeNode.
     *
     * @param o              the object to use.
     * @param allowsChildren true if it allows children.
     */
    public JiveTreeNode(Object o, boolean allowsChildren) {
        super(o, allowsChildren);
    }

    /**
     * Creates a new JiveTreeNode from a TreeItem.
     *
     * @param item the <code>TreeItem</code>
     */
    public JiveTreeNode(TreeItem item) {
        super(item.getDisplayName(), false);
        associatedObject = item;
    }

    /**
     * Creates a new JiveTreeNode from a TreeFolder.
     *
     * @param folder the <code>TreeFolder</code>.
     * @param img    the image to use in the node.
     */
    public JiveTreeNode(TreeFolder folder, Icon img) {
        this(folder);
        closedImage = img;
    }

    /**
     * Createa new JiveTreeNode from a TreeItem and Image.
     *
     * @param item the <code>TreeItem</code> to use.
     * @param img  the image to use in the node.
     */
    public JiveTreeNode(TreeItem item, Icon img) {
        this(item);
        closedImage = img;
    }

    /**
     * Creates a new JiveTreeNode.
     *
     * @param userobject the object to use in the node. Note: By default, the node
     *                   will not allow children.
     */
    public JiveTreeNode(String userobject) {
        super(userobject);
    }

    /**
     * Creates a new JiveTreeNode.
     *
     * @param userObject    the userObject to use.
     * @param allowChildren true if it allows children.
     * @param icon          the image to use in the node.
     */
    public JiveTreeNode(String userObject, boolean allowChildren, Icon icon) {
        super(userObject, allowChildren);
        closedImage = icon;
        openImage = icon;
    }

    /**
     * Returns the default image used.
     *
     * @return the default image used.
     */
    public Icon getIcon() {
        return closedImage;
    }

    /**
     * Return the icon that is displayed when the node is expanded.
     *
     * @return the open icon.
     */
    public Icon getOpenIcon() {
        return openImage;
    }

    /**
     * Returns the icon that is displayed when the node is collapsed.
     *
     * @return the closed icon.
     */
    public Icon getClosedIcon() {
        return closedImage;
    }

    /**
     * Sets the default icon.
     *
     * @param icon the icon.
     */
    public void setIcon(Icon icon) {
        closedImage = icon;
    }

    /**
     * Returns the associated object used. The associated object is used to store associated data objects
     * along with the node.
     *
     * @return the object.
     */
    public Object getAssociatedObject() {
        return associatedObject;
    }

    /**
     * Returns the associated object.
     *
     * @param o the associated object.
     */
    public void setAssociatedObject(Object o) {
        this.associatedObject = o;
    }

    /**
     * Returns true if a parent with the specified name is found.
     *
     * @param parentName the name of the parent.
     * @return true if parent found.
     */
    public final boolean hasParent(String parentName) {
        JiveTreeNode parent = (JiveTreeNode)getParent();
        while (true) {
            if (parent.getAssociatedObject() == null) {
                break;
            }
            final TreeFolder folder = (TreeFolder)parent.getAssociatedObject();
            if (folder.getDisplayName().equals(parentName)) {
                return true;
            }
            parent = (JiveTreeNode)parent.getParent();
        }
        return false;
    }


    /**
     * Transferable implementation
     */
    public DataFlavor[] getTransferDataFlavors() {
        return DATA_FLAVORS;
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor == DATA_FLAVORS[0];

    }

    public Object getTransferData(DataFlavor flavor)
            throws UnsupportedFlavorException, IOException {
        if (this.isDataFlavorSupported(flavor)) {
            return this;
        }

        throw new UnsupportedFlavorException(flavor);
    }


}



