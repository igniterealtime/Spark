/**
 * $Revision$
 * $Date$
 *
 * Copyright (C) 1999-2008 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.fastpath.workspace.panes;

import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.JList;
import javax.swing.UIManager;
import javax.swing.BorderFactory;

import java.awt.Component;
import java.awt.Color;

public class FastpathPanelRenderer extends JPanel implements ListCellRenderer {
	private static final long serialVersionUID = 1964407022568150717L;

	/**
     * Construct Default JPanelRenderer.
     */
    public FastpathPanelRenderer() {
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
            panel.setBorder(BorderFactory.createMatteBorder(0,0,1,0, Color.lightGray));
        }

        list.setBackground((Color)UIManager.get("List.background"));


        return panel;
    }
}

