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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 * UI to show CheckBox trees.
 *
 * @author Derek DeMoro
 */
public class CheckTree extends JPanel {
    private static final long serialVersionUID = 8452107824029515678L;
    private JTree tree;

    /**
     * Constructs a new CheckBox tree.
     *
     * @param rootNode Node that is the root of this tree.
     */
    public CheckTree(CheckNode rootNode) {
        tree = new JTree(rootNode);
        tree.setCellRenderer(new CheckRenderer());
        tree.setRowHeight(18);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setToggleClickCount(1000);
        tree.putClientProperty("JTree.lineStyle", "Angled");
        tree.addMouseListener(new NodeSelectionListener(tree));

        setLayout(new BorderLayout());
        add(tree, BorderLayout.CENTER);
    }


    class NodeSelectionListener extends MouseAdapter {
        JTree tree;

        NodeSelectionListener(JTree tree) {
            this.tree = tree;
        }

        public void mouseClicked(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
            int row = tree.getRowForLocation(x, y);
            TreePath path = tree.getPathForRow(row);
            if (path != null) {
                CheckNode node = (CheckNode)path.getLastPathComponent();
                boolean isSelected = !node.isSelected();
                node.setSelected(isSelected);
                if (node.getSelectionMode() == CheckNode.DIG_IN_SELECTION) {
                    if (isSelected) {
                        //tree.expandPath(path);
                    }
                    else {
                        //tree.collapsePath(path);
                    }
                }
                ((DefaultTreeModel)tree.getModel()).nodeChanged(node);
                // I need revalidate if node is root.  but why?

                tree.revalidate();
                tree.repaint();

            }
        }
    }

    /**
     * Closes the CheckTree.
     */
    public void close() {
    }


    class ButtonActionListener implements ActionListener {
        CheckNode root;
        JTextArea textArea;

        ButtonActionListener(CheckNode root, JTextArea textArea) {
            this.root = root;
            this.textArea = textArea;
        }

        public void actionPerformed(ActionEvent e) {
            Enumeration<CheckNode> nodeEnum = root.breadthFirstEnumeration();
            while (nodeEnum.hasMoreElements()) {
                CheckNode node = nodeEnum.nextElement();
                if (node.isSelected()) {
                    TreeNode[] nodes = node.getPath();
                    textArea.append("\n" + nodes[0].toString());
                    for (int i = 1; i < nodes.length; i++) {
                        textArea.append("/" + nodes[i].toString());
                    }
                }
            }
        }
    }

    public JTree getTree() {
        return tree;
    }

    /**
     * Call to expand the entire tree.
     */
    public void expandTree() {
        for (int i = 0; i <= tree.getRowCount(); i++) {
            tree.expandPath(tree.getPathForRow(i));
        }
    }

}

