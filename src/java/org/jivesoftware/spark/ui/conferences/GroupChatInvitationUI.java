/**
 * $Revision$
 * $Date$
 *
 * Copyright (C) 1999-2005 Jive Software. All rights reserved.
 * This software is the proprietary information of Jive Software. Use is subject to license terms.
 */

package org.jivesoftware.spark.ui.conferences;

import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.component.WrappedLabel;

import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.GridBagLayout;

/**
 *
 */
public class GroupChatInvitationUI extends JPanel {

    private JLabel iconLabel;
    private WrappedLabel titleLabel;
    private RolloverButton acceptButton;
    private RolloverButton rejectButton;

    public GroupChatInvitationUI() {
        setLayout(new GridBagLayout());

        setBackground(new Color(230, 239, 249));

        // Build the UI
        buildUI();
    }

    private void buildUI() {
        iconLabel = new JLabel(SparkRes.getImageIcon(SparkRes.CONFERENCE_IMAGE_48x48));

        titleLabel = new WrappedLabel();

        acceptButton = new RolloverButton("Accept", SparkRes.getImageIcon(SparkRes.ACCEPT_INVITE_IMAGE));
        acceptButton.setForeground(new Color(63, 158, 61));

        rejectButton = new RolloverButton("Reject", SparkRes.getImageIcon(SparkRes.REJECT_INVITE_IMAGE));
        rejectButton.setForeground(new Color(185, 33, 33));

        
    }


}
