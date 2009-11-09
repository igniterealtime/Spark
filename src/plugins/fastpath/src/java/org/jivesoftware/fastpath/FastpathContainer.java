/**
 * $Revision$
 * $Date$
 *
 * Copyright (C) 1999-2008 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.fastpath;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/**
 * Acts as the container for all Fastpath components.
 */
public class FastpathContainer extends JPanel {
    private JPanel topPanel;
    private JTabbedPane mainPanel;

    public FastpathContainer() {
        setLayout(new GridBagLayout());

        topPanel = new JPanel();
        mainPanel = new JTabbedPane();

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

    public JTabbedPane getMainPanel() {
        return mainPanel;
    }

    public void setTitleForComponent(String title, Component component) {
        int size = mainPanel.getTabCount();
        for (int i = 0; i < size; i++) {
            Component c = mainPanel.getComponentAt(i);
            if (c == component) {
                mainPanel.getTabAt(i).getTitleLabel().setText(title);
                break;
            }
        }
    }


}
