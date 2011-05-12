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
package net.java.sipmack.softphone;

import net.java.sipmack.sip.event.MessageEvent;
import org.jivesoftware.resource.SparkRes;

import javax.sip.message.Request;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class VoiceMail {

    Request request = null;

    private String read = "";

    private String unread = "";

    public VoiceMail(MessageEvent evt) {

        String body = evt.getBody();

        String match = "Voice-Message:";

        int a = body.indexOf(match);
        if (a > 0) {

            a += match.length();
            int b = body.indexOf("\n", a);

            String msgs[] = body.substring(a, b - 1).trim().split("\\(")[0]
                    .split("/");

            unread = msgs[0].trim();
            read = msgs[1].trim();

        }

    }

    public String getRead() {
        return read;
    }

    public String getUnread() {
        return unread;
    }

    public JPanel getToaster() {

        JPanel toaster = new JPanel();

        toaster.setLayout(new GridBagLayout());
        toaster.setBackground(Color.white);

        JLabel newVoiceMail = new JLabel();
        JLabel oldVoiceMail = new JLabel();

        newVoiceMail.setFont(new Font("Dialog", Font.BOLD, 15));
        newVoiceMail.setHorizontalAlignment(JLabel.CENTER);
        newVoiceMail.setText("New: " + this.getUnread());

        oldVoiceMail.setFont(new Font("Dialog", Font.PLAIN, 15));
        oldVoiceMail.setHorizontalAlignment(JLabel.CENTER);
        oldVoiceMail.setText("Old: " + this.getRead());

        final JLabel phoneImage = new JLabel(SparkRes
                .getImageIcon(SparkRes.MAIL_IMAGE_32x32));
        phoneImage.setHorizontalAlignment(JLabel.CENTER);
        phoneImage.setVerticalTextPosition(JLabel.BOTTOM);
        phoneImage.setHorizontalTextPosition(JLabel.CENTER);
        phoneImage.setText("Voice Mails");
        phoneImage.setFont(new Font("Dialog", Font.BOLD, 16));

        toaster.add(phoneImage, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 10, 0, 10), 0, 0));
        toaster.add(newVoiceMail, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(2, 0, 0, 0), 0, 0));
        toaster.add(oldVoiceMail, new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(2, 0, 10, 0), 0, 0));

        return toaster;

    }

}
