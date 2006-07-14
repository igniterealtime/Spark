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


import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import java.awt.Component;

/**
 * The <code>JLabelIconRenderer</code> is the an implementation of ListCellRenderer
 * to add icons w/ associated text in JComboBox and JList.
 *
 * @author Derek DeMoro
 */
public class JLabelIconRenderer extends JLabel implements ListCellRenderer {

    /**
     * Construct Default JLabelIconRenderer.
     */
    public JLabelIconRenderer() {
        setOpaque(true);
        this.setVerticalTextPosition(JLabel.BOTTOM);
        this.setHorizontalTextPosition(JLabel.CENTER);
        this.setHorizontalAlignment(JLabel.CENTER);
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

        this.setVerticalTextPosition(JLabel.BOTTOM);
        this.setHorizontalTextPosition(JLabel.CENTER);

        JLabel label = (JLabel)value;
        setText(label.getText());
        setIcon(label.getIcon());
        return this;
    }
}   

