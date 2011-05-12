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
package org.jivesoftware.sparkimpl.plugin.phone;

import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.resource.Res;
import org.jivesoftware.spark.component.TimeTrackingLabel;

import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Date;

public class OnPhone extends JPanel {
	private static final long serialVersionUID = -7344123390643812061L;
	private JLabel iconLabel;
    private TimeTrackingLabel timeLabel;

    public OnPhone() {
        setLayout(new BorderLayout());

        final JPanel imagePanel = new JPanel();
        imagePanel.setLayout(new GridBagLayout());


        imagePanel.setBackground(Color.white);

        // Handle Icon Label
        iconLabel = new JLabel(SparkRes.getImageIcon(SparkRes.TELEPHONE_24x24));
        iconLabel.setHorizontalAlignment(JLabel.CENTER);
        iconLabel.setVerticalTextPosition(JLabel.BOTTOM);
        iconLabel.setHorizontalTextPosition(JLabel.CENTER);
        iconLabel.setFont(new Font("Dialog", Font.BOLD, 14));
        iconLabel.setText(Res.getString("title.on.the.phone"));

        // Handle Time Tracker
        timeLabel = new TimeTrackingLabel(new Date(), this);
        timeLabel.setOpaque(false);
        timeLabel.setHorizontalAlignment(JLabel.CENTER);
        timeLabel.setHorizontalTextPosition(JLabel.CENTER);
        timeLabel.setFont(new Font("Dialog", Font.BOLD, 14));

        // Add Icon Label
        imagePanel.add(iconLabel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.7, GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

        // Add Time Label
        imagePanel.add(timeLabel, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.3, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

        add(imagePanel, BorderLayout.CENTER);
    }

    public void changeText(String text) {
        iconLabel.setText(text);
    }

    public void stopTimer() {
        timeLabel.stopTimer();
    }

}

