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
import org.jivesoftware.spark.util.ResourceUtils;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class DialPanel extends JPanel {
	private static final long serialVersionUID = 2460254947711336776L;
	private JButton dialButton;
    private JTextField dialField;
    final JLabel iconLabel;
    final JPanel dialPanel = new JPanel();

    public DialPanel() {
        setLayout(new GridBagLayout());

        JPanel imagePanel = new JPanel();
        imagePanel.setLayout(new BorderLayout());


        imagePanel.setBackground(Color.white);

        iconLabel = new JLabel(SparkRes.getImageIcon(SparkRes.TELEPHONE_24x24));
        iconLabel.setHorizontalAlignment(JLabel.CENTER);
        iconLabel.setVerticalTextPosition(JLabel.BOTTOM);
        iconLabel.setHorizontalTextPosition(JLabel.CENTER);
        imagePanel.add(iconLabel, BorderLayout.CENTER);
        iconLabel.setFont(new Font("Dialog", Font.BOLD, 14));


        dialPanel.setLayout(new GridBagLayout());

        JLabel dialLabel = new JLabel();
        dialPanel.add(dialLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.WEST, new Insets(5, 5, 5, 5), 0, 0));

        dialField = new JTextField();
        dialPanel.add(dialField, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

        dialButton = new JButton("");
        dialPanel.add(dialButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.WEST, new Insets(5, 5, 5, 5), 0, 0));

        add(imagePanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        add(dialPanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

        iconLabel.setText(Res.getString("title.waiting.to.call"));

        ResourceUtils.resButton(dialButton, Res.getString("label.dial"));
        ResourceUtils.resLabel(dialLabel, dialField, Res.getString("label.number") + ":");
    }

    public void changeToRinging() {
        remove(dialPanel);
        dialPanel.setVisible(false);
    }

    public void showDialog(String number) {
        iconLabel.setText(Res.getString("message.calling", number));
    }

    public void setText(String text) {
        iconLabel.setText(text);
    }

    public JButton getDialButton() {
        return dialButton;
    }

    public String getNumberToDial() {
        return dialField.getText();
    }

    public JTextField getDialField() {
        return dialField;
    }
}
