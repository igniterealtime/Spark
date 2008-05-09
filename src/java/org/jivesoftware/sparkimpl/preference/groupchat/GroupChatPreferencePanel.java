/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkimpl.preference.groupchat;

import org.jivesoftware.resource.Res;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.VerticalFlowLayout;
import org.jivesoftware.spark.util.ResourceUtils;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * The Preference UI used to handle changing of Chat Preferences.
 */
public class GroupChatPreferencePanel extends JPanel {

    private JCheckBox highlightMyName = new JCheckBox();
    private JCheckBox highlightMyText = new JCheckBox();

    private JPanel gCPanel = new JPanel();
    /**
     * Constructor invokes UI setup.
     */
    public GroupChatPreferencePanel() {
        // Build the UI
        createUI();
    }

    private void createUI() {
        setLayout(new VerticalFlowLayout());

        ResourceUtils.resButton(highlightMyName, Res.getString("menuitem.add.groupchat.myname"));
        ResourceUtils.resButton(highlightMyText, Res.getString("menuitem.add.groupchat.mytext"));

        gCPanel.setBorder(BorderFactory.createTitledBorder(Res.getString("title.group.chat.settings")));

        add(gCPanel);

        gCPanel.setLayout(new GridBagLayout());
        
        gCPanel.add(highlightMyName, new GridBagConstraints(0, 0, 2, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        gCPanel.add(highlightMyText, new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    }

    public void setMucHighNameEnabled(boolean mucNHigh) {
        highlightMyName.setSelected(mucNHigh);
    }
    
    public void setMucHighTextEnabled(boolean mucTHigh) {
        highlightMyText.setSelected(mucTHigh);
    }
    
    public boolean isMucHighNameEnabled() {
        return highlightMyName.isSelected();
    }
    
    public boolean isMucHighTextEnabled() {
        return highlightMyText.isSelected();
    }

}
