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

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import java.awt.Component;
import java.awt.Font;

/**
 * <code>JiveTreeCellRenderer</code> class is a bare bone
 * TreeCellRenderer to easily add icons and not much else.
 *
 * @author Derek DeMoro
 */
public class JiveTreeCellRenderer extends DefaultTreeCellRenderer {
    private Object value;
    private boolean isExpanded;

    /**
     * Empty Constructor.
     */
    public JiveTreeCellRenderer() {
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        this.value = value;

        final Component c = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

        isExpanded = expanded;

        setIcon(getCustomIcon());

        // Root Nodes are always bold
        JiveTreeNode node = (JiveTreeNode)value;
        if (node.getAllowsChildren()) {
            setFont(new Font("Arial", Font.BOLD, 11));
        }
        else {
            setFont(new Font("Arial", Font.PLAIN, 11));
        }


        return c;
    }

    private Icon getCustomIcon() {
        if (value instanceof JiveTreeNode) {
            JiveTreeNode node = (JiveTreeNode)value;
            if (isExpanded) {
                return node.getOpenIcon();
            }
            return node.getClosedIcon();
        }
        return null;
    }

    public Icon getClosedIcon() {
        return getCustomIcon();
    }

    public Icon getDefaultClosedIcon() {
        return getCustomIcon();
    }

    public Icon getDefaultLeafIcon() {
        return getCustomIcon();
    }

    public Icon getDefaultOpenIcon() {
        return getCustomIcon();
    }

    public Icon getLeafIcon() {
        return getCustomIcon();
    }

    public Icon getOpenIcon() {
        return getCustomIcon();
    }

}


