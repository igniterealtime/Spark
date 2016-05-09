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

