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

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.Enumeration;

/**
 * Creates a new Tree UI to allow for easier manipulation of tree nodes.
 *
 * @see TreeFolder
 * @see TreeItem
 * @see JiveTreeNode
 */
public class Tree extends JTree implements TreeSelectionListener, MouseMotionListener {

    private static final long serialVersionUID = 295484232488085142L;

    /**
     * Creates the Tree from a root node.
     *
     * @param node - the root node to create a tree from.
     */
    public Tree(DefaultMutableTreeNode node) {
        super(node);
        addMouseMotionListener(this);
        putClientProperty("JTree.lineStyle", "Angled");
    }

    /**
     * Reacts to changing tree nodes.
     *
     * @param e - the TreeSelectionEvent notifying of a valueChange.
     */
    public void valueChanged(TreeSelectionEvent e) {
        JiveTreeNode node = (JiveTreeNode)getLastSelectedPathComponent();

        if (node == null) return;


        if (node.isLeaf()) {
            setTransferHandler(new TransferHandler("text"));
        }
    }

    /**
     * Returns the last selected node in the tree.
     *
     * @return the last selected node in the tree.
     */
    public JiveTreeNode getTreeNode() {
        return (JiveTreeNode)getLastSelectedPathComponent();
    }

    /**
     * Handles drag and drop.
     *
     * @param e - the mousedragged event to handle drag and drop from.
     */
    public void mouseDragged(MouseEvent e) {
        final JComponent c = (JComponent)e.getSource();
        JiveTreeNode node = (JiveTreeNode)getLastSelectedPathComponent();
        if (node == null) {
            return;
        }
        if (node.isLeaf()) {
            TransferHandler handler = c.getTransferHandler();
            handler.exportAsDrag(c, e, TransferHandler.COPY);
        }
    }

    /**
     * Finds the correct tree path.
     *
     * @param tree  the tree to search.
     * @param nodes the nodes to find in the tree.
     * @return the treepath.
     */
    public TreePath find(Tree tree, Object[] nodes) {
        TreeNode root = (TreeNode)tree.getModel().getRoot();
        return find2(tree, new TreePath(root), nodes, 0, false);
    }

    /**
     * Finds the path in tree as specified by the array of names. The names array is a
     * sequence of names where names[0] is the root and names[i] is a child of names[i-1].
     * Comparison is done using String.equals(). Returns null if not found.
     *
     * @param tree  the tree to search.
     * @param names a list of names to find.
     * @return the treepath found.
     */
    public TreePath findByName(Tree tree, String[] names) {
        TreeNode root = (TreeNode)tree.getModel().getRoot();
        return find2(tree, new TreePath(root), names, 0, true);
    }

    private TreePath find2(Tree tree, TreePath parent, Object[] nodes, int depth, boolean byName) {
        TreeNode node = (TreeNode)parent.getLastPathComponent();
        Object o = node;

        // If by name, convert node to a string
        if (byName) {
            o = o.toString();
        }

        // If equal, go down the branch
        if (o.equals(nodes[depth])) {
            // If at end, return match
            if (depth == nodes.length - 1) {
                return parent;
            }

            // Traverse children
            if (node.getChildCount() >= 0) {
                for (Enumeration<TreeNode> e = node.children(); e.hasMoreElements();) {
                    TreeNode n = e.nextElement();
                    TreePath path = parent.pathByAddingChild(n);
                    TreePath result = find2(tree, path, nodes, depth + 1, byName);
                    // Found a match
                    if (result != null) {
                        return result;
                    }
                }
            }
        }

        // No match at this branch
        return null;
    }

    public void mouseMoved(MouseEvent e) {
    }

    /**
     * Call to expand the entire tree.
     */
    public void expandTree() {
        for (int i = 0; i <= getRowCount(); i++) {
            expandPath(getPathForRow(i));
        }
    }
}