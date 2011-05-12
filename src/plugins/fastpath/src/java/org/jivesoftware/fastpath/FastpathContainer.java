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
package org.jivesoftware.fastpath;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.lang.reflect.InvocationTargetException;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.jivesoftware.spark.component.tabbedPane.SparkTabbedPane;

/**
 * Acts as the container for all Fastpath components.
 */
public class FastpathContainer extends JPanel {
	private static final long serialVersionUID = 1651363083075622414L;
	private JPanel topPanel;
    private SparkTabbedPane mainPanel;

    public FastpathContainer() {

		  setLayout(new GridBagLayout());
		
		  topPanel = new JPanel();
		  mainPanel = new SparkTabbedPane();
		
		  add(topPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 0, 2), 0, 0));
		  add(mainPanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2, 2, 0, 2), 0, 0));
		
		  topPanel.setLayout(new BorderLayout());
		
		  mainPanel.setBackground(Color.white);
		  topPanel.setBackground(Color.white);
		
		  setBackground(Color.white);
		
		  mainPanel.getMainPanel().setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
		  mainPanel.getMainPanel().setBackground(Color.white);

    }

    public JPanel getTopPanel() {
        return topPanel;
    }

    public SparkTabbedPane getMainPanel() {
        return mainPanel;
    }

    public void setTitleForComponent(String title, Component component) {
        int size = mainPanel.getTabCount();
        for (int i = 0; i < size; i++) {
            Component c = mainPanel.getComponentAt(i);
            if (c == component) {
                mainPanel.getTabAt(i).setTabTitle(title);
                break;
            }
        }
    }


}
