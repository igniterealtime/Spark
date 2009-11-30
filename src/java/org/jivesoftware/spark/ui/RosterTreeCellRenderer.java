/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.spark.ui;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;

/**
 *
 */
public class RosterTreeCellRenderer extends DefaultTreeCellRenderer {
	private static final long serialVersionUID = 4645070076041138834L;
	private Object value;
    private boolean isExpanded;

    /**
     * Empty Constructor.
     */
    public RosterTreeCellRenderer() {
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        this.value = value;

        final Component c = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

        isExpanded = expanded;

        setIcon(getCustomIcon());

        // Root Nodes are always bold
        RosterNode node = (RosterNode)value;
        if (node.isGroup()) {
            setFont(new Font("Dialog", Font.BOLD, 11));
            setText(node.getName() + " (" + node.getChildCount() + " online)");
            setForeground(new Color(64, 112, 196));
            setIcon(getCustomIcon());
        }

        if (node.isGroup()) {
            return c;
        }

        final JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));

        final JLabel label = new JLabel();
        label.setFont(new Font("Arial", Font.PLAIN, 11));
        label.setText(node.getName());
        panel.add(label);

        final JLabel descriptionLabel = new JLabel();
        descriptionLabel.setFont(new Font("Dialog", Font.PLAIN, 11));
        descriptionLabel.setForeground(new Color(178, 181, 182));
        descriptionLabel.setText(" - I'm just chilling.");

        panel.add(descriptionLabel);

        if (selected) {
            panel.setBackground(getBackgroundSelectionColor());
        }
        else {
            panel.setBackground(getBackgroundNonSelectionColor());
        }
        return panel;
    }

    private Icon getCustomIcon() {
        if (value instanceof RosterNode) {
            RosterNode node = (RosterNode)value;
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


