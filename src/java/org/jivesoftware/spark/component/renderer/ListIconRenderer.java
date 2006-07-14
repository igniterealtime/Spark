/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.spark.component.renderer;


import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;

import java.awt.Component;

/**
 * The <code>ListIconRenderer</code> is the an implementation of ListCellRenderer
 * to add icons w/ associated text in JComboBox and JList.
 *
 * @author Derek DeMoro
 */
public class ListIconRenderer extends JLabel implements ListCellRenderer {

    /**
     * Create a Default ListIconRenderer.
     */
    public ListIconRenderer() {
        setOpaque(true);
        setHorizontalAlignment(CENTER);
        setVerticalAlignment(CENTER);
    }

    public Component getListCellRendererComponent(JList list,
                                                  Object value,
                                                  int index,
                                                  boolean isSelected,
                                                  boolean cellHasFocus) {
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        }
        else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        setHorizontalAlignment(SwingConstants.LEFT);
        ImageIcon icon = (ImageIcon)value;
        setText(icon.getDescription());
        setIcon(icon);
        return this;
    }
}   

