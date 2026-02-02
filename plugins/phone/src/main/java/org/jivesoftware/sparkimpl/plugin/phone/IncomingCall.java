/**
 * Copyright (C) 2004-2011 Jive Software. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jivesoftware.sparkimpl.plugin.phone;

import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.resource.Res;

import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/**
 * UI to display incoming call information
 */
public class IncomingCall extends JPanel {
    private static final long serialVersionUID = -5840942759253687771L;
    private final JLabel callerNameLabel;
    private final JLabel callerNumberLabel;

    public IncomingCall() {
        setLayout(new GridBagLayout());
        setBackground(Color.white);

        callerNameLabel = new JLabel();
        callerNameLabel.setFont(new Font("Dialog", Font.BOLD, 13));
        callerNameLabel.setHorizontalAlignment(JLabel.CENTER);

        callerNumberLabel = new JLabel();
        callerNumberLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
        callerNumberLabel.setHorizontalAlignment(JLabel.CENTER);

        final JLabel phoneImage = new JLabel(SparkRes.getImageIcon(SparkRes.TELEPHONE_24x24));
        phoneImage.setHorizontalAlignment(JLabel.CENTER);
        phoneImage.setVerticalTextPosition(JLabel.BOTTOM);
        phoneImage.setHorizontalTextPosition(JLabel.CENTER);
        phoneImage.setText(Res.getString("title.incoming.call"));
        phoneImage.setFont(new Font("Dialog", Font.BOLD, 16));
        add(phoneImage, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 10, 0, 10), 0, 0));
        add(callerNameLabel, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 0, 0), 0, 0));
        add(callerNumberLabel, new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 10, 0), 0, 0));
    }

    public void setCallerName(String user) {
        callerNameLabel.setText(user);
    }

    public void setCallerNumber(String number) {
        String buf = "";
        if (number == null) {
            return;
        }

        if (number.trim().length() == 10) {
            buf += "(";
            String areaCode = number.substring(0, 3);
            buf += areaCode;
            buf += ") ";

            String nextThree = number.substring(3, 6);
            buf += " ";
            buf += nextThree;
            buf += "-";

            String lastThree = number.substring(6, 10);
            buf += lastThree;
        }

        callerNumberLabel.setText(buf);
    }
}
