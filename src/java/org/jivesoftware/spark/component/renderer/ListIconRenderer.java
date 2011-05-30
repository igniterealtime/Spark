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
    private static final long serialVersionUID = -1440360116105487085L;

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

