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
package org.jivesoftware.sparkimpl.plugin.gateways;

import org.jivesoftware.sparkimpl.plugin.gateways.transports.Transport;
import org.jivesoftware.resource.Res;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 */
public class TransportItem extends JPanel {
	private static final long serialVersionUID = 6321253573105576262L;
	private JLabel iconLabel;
    private JLabel titleLabel;
    private JLabel descriptionLabel;
    private JLabel activeLabel;

    private String transportAddress;

    private Transport transport;

    public TransportItem(Transport transport, boolean active, String address) {
        setLayout(new GridBagLayout());

        iconLabel = new JLabel();
        titleLabel = new JLabel();
        descriptionLabel = new JLabel();
        activeLabel = new JLabel();

        iconLabel.setIcon(transport.getIcon());

        titleLabel.setText(transport.getTitle());
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 11));

        descriptionLabel.setText(transport.getName());
        descriptionLabel.setFont(new Font("Dialog", Font.PLAIN, 11));
        descriptionLabel.setForeground(Color.lightGray);
        descriptionLabel.setHorizontalTextPosition(JLabel.LEFT);
        descriptionLabel.setHorizontalAlignment(JLabel.LEFT);

        activeLabel.setFont(new Font("Dialog", Font.PLAIN, 10));

        if (active) {
            activeLabel.setText(Res.getString("active"));
            activeLabel.setForeground(Color.green);
        }
        else {
            activeLabel.setText(Res.getString("not.registered"));
            activeLabel.setForeground(Color.GRAY);
        }

        this.transportAddress = address;

        this.transport = transport;

        // Build UI
        buildUI();
    }

    private void buildUI() {
        add(iconLabel, new GridBagConstraints(0, 0, 1, 2, 0.0, 0.0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 0), 0, 0));
        add(titleLabel, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));
        add(descriptionLabel, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 2, 0), 0, 0));
        add(activeLabel, new GridBagConstraints(1, 2, 1, 2, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));
    }

    public String getTransportAddress() {
        return transportAddress;
    }

    public void setTransportAddress(String transportAddress) {
        this.transportAddress = transportAddress;
    }

    public Transport getTransport(){
        return transport;
    }
}
