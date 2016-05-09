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
package org.jivesoftware.sparkplugin.ui;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;

import java.awt.Color;
import java.awt.Component;

/**
 * The <code>CallHistoryRenderer</code> is the an implementation of ListCellRenderer
 * to add an entire panel ui to lists.
 *
 * @author Derek DeMoro
 */
public class CallHistoryRenderer extends JPanel implements ListCellRenderer {
	private static final long serialVersionUID = 3217094193017887282L;

	/**
     * Construct Default CallHistoryRenderer.
     */
    public CallHistoryRenderer() {
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
            panel.setForeground(Color.white);
            panel.setBackground(new Color(51, 136, 238));
            panel.setBorder(BorderFactory.createLineBorder((Color)UIManager.get("List.selectionBorder")));
        }
        else {
            if (index % 2 == 0) {
                panel.setBackground((Color)UIManager.get("List.selectionBackground"));
            }
            else {
                panel.setBackground(list.getBackground());
            }
            panel.setForeground(list.getForeground());
            panel.setBorder(BorderFactory.createLineBorder((Color)UIManager.get("List.background")));
        }

        list.setBackground((Color)UIManager.get("List.background"));


        return panel;
    }
}

