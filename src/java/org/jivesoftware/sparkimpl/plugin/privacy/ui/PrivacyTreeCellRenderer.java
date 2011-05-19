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
package org.jivesoftware.sparkimpl.plugin.privacy.ui;

import java.awt.Color;
import java.awt.Component;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.Icon;

import javax.swing.JLabel;
import javax.swing.JPanel;

import javax.swing.JTree;

import javax.swing.tree.DefaultTreeCellRenderer;

import org.jivesoftware.resource.SparkRes;

/**
 * @author Bergunde Holger
 */
public class PrivacyTreeCellRenderer extends DefaultTreeCellRenderer {

    private static final long serialVersionUID = 5819051053144634773L;

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        final Component c = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        JPanel myPanel = new JPanel();
        myPanel.setBackground(Color.white);
        PrivacyTreeNode node = (PrivacyTreeNode) value;

        myPanel.setLayout(new GridBagLayout());

        if (leaf && node.isPrivacyItem()) {
            if (sel) {
                myPanel.setBackground(Color.LIGHT_GRAY);
            }

            Icon iq = node.getPrivacyItem().isFilterIQ() ? SparkRes.getImageIcon("PRIVACY_QUERY_DENY") : SparkRes.getImageIcon("PRIVACY_QUERY_ALLOW");
            Icon in = node.getPrivacyItem().isFilterPresence_in() ? SparkRes.getImageIcon("PRIVACY_PIN_DENY") : SparkRes.getImageIcon("PRIVACY_PIN_ALLOW");
            Icon out = node.getPrivacyItem().isFilterPresence_out() ? SparkRes.getImageIcon("PRIVACY_POUT_DENY") : SparkRes.getImageIcon("PRIVACY_POUT_ALLOW");
            Icon msg = node.getPrivacyItem().isFilterMessage() ? SparkRes.getImageIcon("PRIVACY_MSG_DENY") : SparkRes.getImageIcon("PRIVACY_MSG_ALLOW");

            myPanel.add(new JLabel(node.getPrivacyItem().getValue()), new GridBagConstraints(4, 0, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL,
                    new Insets(0, 10, 0, 0), 0, 0));
            myPanel.add(new JLabel(msg), new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
            myPanel.add(new JLabel(iq), new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
            myPanel.add(new JLabel(in), new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
            myPanel.add(new JLabel(out), new GridBagConstraints(3, 0, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

        } else {

            if (node.isRoot()) {
                setIcon(SparkRes.getImageIcon("CLIPBOARD"));
            }

            if (node.isGroupNode()) {

                setIcon(SparkRes.getImageIcon(SparkRes.SMALL_CURRENT_AGENTS));

            } else if (node.isContactGroup()) {
                setIcon(SparkRes.getImageIcon(SparkRes.AVAILABLE_USER));

            }

            if (node.isPrivacyList()) {
                String listName = node.getPrivacyList().getListName();
//                if (node.isActiveList()) {
//                    listName += " [" + Res.getString("privacy.label.list.is.active") + "]";
//                }
//                if (node.isDefaultList()) {
//                    listName += " [" + Res.getString("privacy.label.list.is.default") + "]";
//                }
                setText(listName);
                setIcon(SparkRes.getImageIcon(SparkRes.SMALL_ENTRY));

            }

            return c;

        }
        return myPanel;
    }
}
