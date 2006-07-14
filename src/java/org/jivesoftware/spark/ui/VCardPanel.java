/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.spark.ui;

import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smackx.packet.Time;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.borders.PartialLineBorder;
import org.jivesoftware.spark.ui.rooms.ChatRoomImpl;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.sparkimpl.profile.VCardManager;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class VCardPanel extends JPanel {

    private ChatRoomImpl chatRoom;
    private JLabel avatarImage;

    public VCardPanel(final ChatRoomImpl chatRoom) {
        setLayout(new GridBagLayout());
        setOpaque(false);

        this.chatRoom = chatRoom;
        avatarImage = new JLabel();

        SwingWorker worker = new SwingWorker() {
            VCard vcard = null;

            public Object construct() {
                vcard = SparkManager.getVCardManager().getVCard(chatRoom.getParticipantJID());
                return vcard;
            }

            public void finished() {
                if (vcard == null) {
                    // Do nothing.
                    return;
                }

                byte[] bytes = vcard.getAvatar();
                if (bytes != null) {
                    try {
                        ImageIcon icon = new ImageIcon(bytes);
                        icon = VCardManager.scale(icon);
                        if (icon.getIconWidth() > 0) {
                            avatarImage.setIcon(icon);
                            avatarImage.setBorder(new PartialLineBorder(Color.LIGHT_GRAY, 1));
                        }
                        setupUI(vcard);
                    }
                    catch (Exception e) {
                    }
                }
            }
        };

        worker.start();
    }

    private void setupUI(VCard vcard) {
        add(avatarImage, new GridBagConstraints(0, 0, 1, 4, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

        String city = vcard.getField("CITY");
        String state = vcard.getField("STATE");
        String country = vcard.getField("COUNTRY");
        String firstName = vcard.getFirstName();
        if (firstName == null) {
            firstName = "";
        }

        String lastName = vcard.getLastName();
        if (lastName == null) {
            lastName = "";
        }

        String title = vcard.getField("TITLE");

        final JLabel usernameLabel = new JLabel();
        usernameLabel.setFont(new Font("Dialog", Font.BOLD, 12));

        usernameLabel.setForeground(Color.DARK_GRAY);
        if (ModelUtil.hasLength(firstName) && ModelUtil.hasLength(lastName)) {
            usernameLabel.setText(firstName + " " + lastName);
        }
        else {
            usernameLabel.setText(chatRoom.getTabTitle());
        }


        add(usernameLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 5, 2, 5), 0, 0));

        final JLabel locationLabel = new JLabel();

        if (ModelUtil.hasLength(city) && ModelUtil.hasLength(state) && ModelUtil.hasLength(country)) {
            locationLabel.setText(" - " + city + ", " + state + " " + country);
        }
        add(locationLabel, new GridBagConstraints(2, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 5, 2, 5), 0, 0));


        final JLabel titleLabel = new JLabel(title);
        if (ModelUtil.hasLength(title)) {
            add(titleLabel, new GridBagConstraints(1, 1, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 5, 2, 5), 0, 0));
        }

        String phone = vcard.getPhoneWork("VOICE");
        if (ModelUtil.hasLength(phone)) {
            final JLabel phoneNumber = new JLabel("Work: " + phone);
            add(phoneNumber, new GridBagConstraints(1, 2, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 5, 2, 5), 0, 0));
        }

        final JLabel localTime = new JLabel();
        add(localTime, new GridBagConstraints(1, 3, 2, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(2, 5, 2, 5), 0, 0));


        final Time time = new Time();
        time.setType(IQ.Type.GET);

        String fullJID = SparkManager.getUserManager().getFullJID(chatRoom.getParticipantJID());
        if (fullJID == null) {
            return;
        }
        time.setTo(fullJID);

        final PacketCollector packetCollector = SparkManager.getConnection().createPacketCollector(new PacketIDFilter(time.getPacketID()));

        SwingWorker timeThread = new SwingWorker() {
            IQ timeResult = null;

            public Object construct() {
                SparkManager.getConnection().sendPacket(time);
                timeResult = (IQ)packetCollector.nextResult();
                return timeResult;
            }

            public void finished() {
                // Wait up to 5 seconds for a result.

                if (timeResult != null && timeResult.getType() == IQ.Type.RESULT) {
                    Time t = (Time)timeResult;
                    localTime.setText("Local Time: " + t.getDisplay());
                }
            }
        };

        timeThread.start();
    }


}
