/**
 * $RCSfile: ,v $
 * $Revision: 1.0 $
 * $Date: 2005/05/25 04:20:03 $
 *
 * Copyright (C) 1999-2008 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.fastpath.workspace.panes;

import java.awt.Component;
import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.JList;
import javax.swing.BorderFactory;


/**
 * The <code>JPanelRenderer</code> is the an implementation of ListCellRenderer
 * to add an entire panel ui to lists.
 *
 * @author Derek DeMoro
 */
public class HistoryItemRenderer extends JPanel implements ListCellRenderer {
	private static final long serialVersionUID = 5290058516051285328L;

	/**
     * Construct Default JPanelRenderer.
     */
    public HistoryItemRenderer() {
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
            panel.setForeground(Color.black);
            panel.setBackground(new Color(217, 232, 250));
            panel.setBorder(BorderFactory.createLineBorder(new Color(187, 195, 215)));
        }
        else {
            panel.setBackground(list.getBackground());
            panel.setForeground(list.getForeground());
            panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        }

        return panel;
    }
}


