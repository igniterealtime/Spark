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
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.profile.VCardManager;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * UI to display VCard Information in Wizards, Dialogs, Chat Rooms and any other container.
 *
 * @author Derek DeMoro
 */
public class VCardPanel extends JPanel {

    private final String jid;
    private final JLabel avatarImage;

    private static SimpleDateFormat utcFormat = new SimpleDateFormat("yyyyMMdd'T'HH:mm:ss");

    /**
     * Generate a VCard Panel using the specified jid.
     *
     * @param jid the jid to use when retrieving the vcard information.
     */
    public VCardPanel(final String jid) {
        setLayout(new GridBagLayout());
        setOpaque(false);

        this.jid = jid;
        avatarImage = new JLabel();

        SwingWorker worker = new SwingWorker() {
            VCard vcard = null;

            public Object construct() {
                vcard = SparkManager.getVCardManager().getVCard(jid);
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
                        Image aImage = icon.getImage();
                        if (icon.getIconHeight() > 32 || icon.getIconWidth() > 32) {
                            aImage = aImage.getScaledInstance(-1, 32, Image.SCALE_SMOOTH);
                        }
                        icon = new ImageIcon(aImage);

                        if (icon.getIconWidth() > 0) {
                            avatarImage.setIcon(icon);
                            avatarImage.setBorder(BorderFactory.createBevelBorder(0, Color.white, Color.lightGray));
                        }

                    }
                    catch (Exception e) {
                        Log.error(e);
                    }
                }

                vcard.setJabberId(jid);
                setupUI(vcard);
            }
        };

        worker.start();
    }

    public VCardPanel(VCard vcard, String jid) {
        setLayout(new GridBagLayout());
        setOpaque(false);

        this.jid = jid;
        avatarImage = new JLabel();

        byte[] bytes = vcard.getAvatar();
        if (bytes != null) {
            try {
                ImageIcon icon = new ImageIcon(bytes);
                icon = VCardManager.scale(icon);
                if (icon.getIconWidth() > 0) {
                    avatarImage.setIcon(icon);
                    avatarImage.setBorder(BorderFactory.createBevelBorder(0, Color.white, Color.lightGray));
                }
                setupUI(vcard);
            }
            catch (Exception e) {
                Log.error(e);
            }
        }
    }

    private void setupUI(final VCard vcard) {
        add(avatarImage, new GridBagConstraints(0, 0, 1, 4, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));

        avatarImage.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent mouseEvent) {
                if(mouseEvent.getClickCount() == 2){
                    SparkManager.getVCardManager().viewProfile(vcard.getJabberId(), avatarImage);
                }
            }
        });

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
        usernameLabel.setFont(new Font("Dialog", Font.BOLD, 11));

        usernameLabel.setForeground(Color.DARK_GRAY);
        if (ModelUtil.hasLength(firstName) && ModelUtil.hasLength(lastName)) {
            usernameLabel.setText(firstName + " " + lastName);
        }
        else {
            usernameLabel.setText(jid);
        }


        add(usernameLabel, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 2, 5), 0, 0));

        /*
        final JLabel locationLabel = new JLabel();

        if (ModelUtil.hasLength(city) && ModelUtil.hasLength(state) && ModelUtil.hasLength(country)) {
            locationLabel.setText(" - " + city + ", " + state + " " + country);
        }
        add(locationLabel, new GridBagConstraints(2, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 5, 2, 5), 0, 0));
        */

        final JLabel titleLabel = new JLabel(title);
        if (ModelUtil.hasLength(title)) {
            add(titleLabel, new GridBagConstraints(1, 1, 2, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0));
        }

        /*
              String phone = vcard.getPhoneWork("VOICE");
              if (ModelUtil.hasLength(phone)) {
                  final JLabel phoneNumber = new JLabel("Work: " + phone);
                  add(phoneNumber, new GridBagConstraints(1, 2, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 5, 2, 5), 0, 0));
              }
        */

        final JLabel localTime = new JLabel();
        add(localTime, new GridBagConstraints(1, 3, 2, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0));

        final Time time = new Time();
        time.setType(IQ.Type.GET);

        String fullJID = SparkManager.getUserManager().getFullJID(jid);
        if (fullJID == null) {
            return;
        }
        time.setTo(fullJID);

        final PacketCollector packetCollector = SparkManager.getConnection().createPacketCollector(new PacketIDFilter(time.getPacketID()));

        final VCardPanel panel = this;
        SwingWorker timeThread = new SwingWorker() {
            IQ timeResult;

            public Object construct() {
                SparkManager.getConnection().sendPacket(time);
                timeResult = (IQ)packetCollector.nextResult();
                return timeResult;
            }

            public void finished() {
                // Wait up to 5 seconds for a result.

                if (timeResult != null && timeResult.getType() == IQ.Type.RESULT) {
                    try {
                        Time t = (Time)timeResult;
                        Calendar cal = Calendar.getInstance();
                        cal.setTimeZone(TimeZone.getTimeZone(t.getTz()));
                        // Convert the UTC time to local time.
                        cal.setTime(new Date(utcFormat.parse(t.getUtc()).getTime() +
                                cal.getTimeZone().getOffset(cal.getTimeInMillis())));
                        Date date = cal.getTime();

                        final SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");


                        localTime.setText("Time: " + formatter.format(date));
                    }
                    catch (ParseException e) {
                        Log.error(e);
                    }
                }
            }
        };

        timeThread.start();
    }


}
