/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkimpl.plugin.viewer;

import org.jivesoftware.resource.SparkRes;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;

import java.awt.Color;
import java.awt.Component;

/**
 * A swing renderer used to display labels within a table.
 */
public class PluginRenderer extends JLabel implements TableCellRenderer {
    Border unselectedBorder;
    Border selectedBorder;
    boolean isBordered = true;

    /**
     * PluginRenderer
     */
    public PluginRenderer() {
        setOpaque(true);
    }

    public Component getTableCellRendererComponent(JTable table, Object color, boolean isSelected, boolean hasFocus, int row, int column) {

        final Icon icon = SparkRes.getImageIcon(SparkRes.PLUGIN_IMAGE);
        setIcon(icon);

        if (isSelected) {
            setForeground(table.getSelectionForeground());
            setBackground(table.getSelectionBackground());
        }
        else {
            setForeground(Color.black);
            setBackground(Color.white);
            if (row % 2 == 0) {
                //setBackground( new Color( 156, 207, 255 ) );
            }
        }

        if (isBordered) {
            if (isSelected) {
                if (selectedBorder == null) {
                    selectedBorder = BorderFactory.createMatteBorder(2, 5, 2, 5,
                            table.getSelectionBackground());
                }
                setBorder(selectedBorder);
            }
            else {
                if (unselectedBorder == null) {
                    unselectedBorder = BorderFactory.createMatteBorder(2, 5, 2, 5,
                            table.getBackground());
                }
                setBorder(unselectedBorder);
            }
        }
        return this;
    }
}

