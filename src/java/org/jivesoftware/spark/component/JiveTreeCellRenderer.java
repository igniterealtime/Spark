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
    private static final long serialVersionUID = -6115204089076392745L;
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


