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


import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;

import java.awt.Color;
import java.awt.Component;

/**
 * The <code>JPanelRenderer</code> is the an implementation of ListCellRenderer
 * to add an entire panel ui to lists.
 *
 * @author Derek DeMoro
 */
public class JPanelRenderer extends JPanel implements ListCellRenderer {

    /**
     * Construct Default JPanelRenderer.
     */
    public JPanelRenderer() {
        setOpaque(true);
    }

    public Component getListCellRendererComponent(JList list,
                                                  Object value,
                                                  int index,
                                                  boolean isSelected,
                                                  boolean cellHasFocus) {
        JPanel panel = (JPanel)value;
        panel.setFocusable(false);

        if (isSelected) {
            panel.setForeground((Color)UIManager.get("List.selectionForeground"));
            panel.setBackground((Color)UIManager.get("List.selectionBackground"));
            panel.setBorder(BorderFactory.createLineBorder((Color)UIManager.get("List.selectionBorder")));
        }
        else {
            panel.setBackground(list.getBackground());
            panel.setForeground(list.getForeground());
            panel.setBorder(BorderFactory.createLineBorder((Color)UIManager.get("ContactItem.background")));
        }

        list.setBackground((Color)UIManager.get("ContactItem.background"));


        return panel;
    }
}

