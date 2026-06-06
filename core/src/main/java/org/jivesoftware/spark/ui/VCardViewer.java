/**
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
package org.jivesoftware.spark.ui;

import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.settings.Sizes;
import org.jxmpp.jid.BareJid;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.NORTHWEST;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.jivesoftware.spark.util.GraphicUtils.toHTMLColor;
import static org.jivesoftware.spark.util.XEP0392Utils.colorOfContact;

/**
 * UI to display VCard Information in Wizards, Dialogs, Chat Rooms and any other container.
 *
 * @author Derek DeMoro
 */
public class VCardViewer extends JPanel {
	private final Cursor DEFAULT_CURSOR = new Cursor(Cursor.DEFAULT_CURSOR);
    private final Cursor LINK_CURSOR = new Cursor(Cursor.HAND_CURSOR);

    private final BareJid jid;
    private final JLabel avatarImage;

    /**
     * Generate a VCard Panel using the specified jid.
     *
     * @param jid the jid to use when retrieving the vcard information.
     */
    public VCardViewer(final BareJid jid, VCard vcard) {
        setLayout(new GridBagLayout());
        setOpaque(false);

        this.jid = jid;
        avatarImage = new JLabel();
        add(avatarImage, new GridBagConstraints(0, 0, 1, 3, 0.0, 1.0, NORTHWEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 0), 0, 0));

        try {
                    if (vcard == null) {
                        // Do nothing.
                        return;
                    }

                    ImageIcon icon = null;

                    byte[] bytes = vcard.getAvatar();
                    if (bytes != null && bytes.length > 0) {
                        try {
                            icon = new ImageIcon(bytes);
                            Image aImage = icon.getImage();
                            aImage = aImage.getScaledInstance(-1, Sizes.Avatar.VCARD, Image.SCALE_SMOOTH);
                            icon = new ImageIcon(aImage);
                        } catch (Exception e) {
                            Log.warning("Unable to get scaled avatar from vcard.", e);
                        }
                    } else {
                        icon = SparkRes.getImageIcon(SparkRes.Icon.DEFAULT_AVATAR_64x64_IMAGE);
                    }

                    if (icon != null && icon.getIconWidth() > 0) {
                        avatarImage.setIcon(icon);
                        avatarImage.setBorder(BorderFactory.createLineBorder(Color.lightGray, 1, true));
                    }
                    buildUI(vcard);
        } catch (Exception e) {
            Log.warning("Unable to get avatar from vcard.", e);
        }
    }

    private void buildUI(final VCard vcard) {
        final JLabel usernameLabel = new JLabel();
        usernameLabel.setHorizontalTextPosition(JLabel.LEFT);
        usernameLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 15));
        usernameLabel.setForeground(colorOfContact(jid));
        String fullName = vcard.getField("FN");
        if (!isEmpty(fullName)) {
            usernameLabel.setText(fullName);
        }
        else {
            String nickname = SparkManager.getUserManager().getUserNicknameFromJID(jid);
            usernameLabel.setText(nickname);
        }

        final Icon icon = SparkManager.getChatManager().getIconForContactHandler(jid);
        if (icon != null) {
            usernameLabel.setIcon(icon);
        }

        int row = 0;
        add(usernameLabel, new GridBagConstraints(1, row++, 1, 1, 0, 0, NORTHWEST, HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));

        String title = vcard.getField("TITLE");
        if (!isEmpty(title)) {
            final JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(new Font(Font.DIALOG, Font.PLAIN, 11));
            add(titleLabel, new GridBagConstraints(1, row++, 1, 1, 0, 0, NORTHWEST, HORIZONTAL, new Insets(0, 7, 0, 0), 0, 0));
        }

        Color linkColor = new Color(49, 89, 151);
        String emailAddress = vcard.getEmailHome();
        if (!isEmpty(emailAddress)) {
            String unselectedText = "<html><body><font color=" + toHTMLColor(linkColor) + "><u>" + emailAddress + "</u></font></body></html>";
            String hoverText = "<html><body><font color=red><u>" + emailAddress + "</u></font></body></html>";
            JLabel emailTime = new JLabel(unselectedText);
            emailTime.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    startEmailClient(emailAddress);
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    emailTime.setText(hoverText);
                    setCursor(LINK_CURSOR);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    emailTime.setText(unselectedText);
                    setCursor(DEFAULT_CURSOR);
                }
            });

            add(emailTime, new GridBagConstraints(1, row++, 1, 1, 1, 0, NORTHWEST, HORIZONTAL, new Insets(0, 7, 10, 0), 0, 0));
        }

        // Add JID Label
        final JLabel jidLabel = new JLabel("<html><body>JID: <font color=" + toHTMLColor(linkColor) + "><u>" + jid + "</u></font></body></html>");
        jidLabel.setToolTipText("Click to copy jid to clipboard.");
        jidLabel.addMouseListener(new MouseAdapter() {
            @Override
			public void mouseEntered(MouseEvent mouseEvent) {
                jidLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
			public void mouseExited(MouseEvent mouseEvent) {
                jidLabel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }

            @Override
			public void mouseClicked(MouseEvent mouseEvent) {
                SparkManager.setClipboard("xmpp:" + jid.toString());
            }
        });

        Insets insets = new Insets(0, 7, 2, 0);
        add(jidLabel, new GridBagConstraints(1, row++, 1, 1, 1, 0, NORTHWEST, HORIZONTAL, insets, 0, 0));

        // Add Home Phone
        String homeNumber = vcard.getPhoneHome("VOICE");
        if (!isEmpty(homeNumber)) {
            JLabel homePhoneLabel = new JLabel(Res.getString("label.home").replace("&", "") + ": " + homeNumber);
            add(homePhoneLabel, new GridBagConstraints(1, row++, 1, 1, 1, 0, NORTHWEST, HORIZONTAL, insets, 0, 0));
        }

        // Add Work Phone
        String workNumber = vcard.getPhoneWork("VOICE");
        if (!isEmpty(workNumber)) {
            JLabel workPhoneLabel = new JLabel(Res.getString("label.work").replace("&", "") + ": " + workNumber);
            add(workPhoneLabel, new GridBagConstraints(1, row++, 1, 1, 1, 0, NORTHWEST, HORIZONTAL, insets, 0, 0));
        }

        // Add Cell Phone
        String cellNumber = vcard.getPhoneWork("CELL");
        if (!isEmpty(cellNumber)) {
            JLabel cellPhoneLabel = new JLabel(Res.getString("label.cell").replace("&", "") + ": " + cellNumber);
            add(cellPhoneLabel, new GridBagConstraints(1, row++, 1, 1, 1, 0, NORTHWEST, HORIZONTAL, insets, 0, 0));
        }

        // Add Company
        String company = vcard.getOrganization();
        if (!isEmpty(company)) {
            JLabel orgLabel = new JLabel(Res.getString("label.company").replace("&", "") + ": " + company);
            add(orgLabel, new GridBagConstraints(1, row++, 1, 1, 1, 0, NORTHWEST, HORIZONTAL, insets, 0, 0));
        }
        // Birthday
        String bdayField = vcard.getField("BDAY");
        if (!isEmpty(bdayField)) {
            JLabel bdayLabel = new JLabel(Res.getString("label.birthday") + ": " + bdayField);
            add(bdayLabel, new GridBagConstraints(1, row++, 1, 1, 1, 0, NORTHWEST, HORIZONTAL, insets, 0, 0));
        }
        // Gender
        String genderField = vcard.getField("GENDER");
        if (!isEmpty(genderField)) {
            String genderTitle;
            switch (genderField) {
                case "M":
                    genderTitle = Res.getString("label.gender.male");
                    break;
                case "F":
                    genderTitle = Res.getString("label.gender.female");
                    break;
                default:
                    genderTitle = genderField;
            }
            JLabel genderLabel = new JLabel(Res.getString("label.gender") + ": " + genderTitle);
            add(genderLabel, new GridBagConstraints(1, row++, 1, 1, 1, 0, NORTHWEST, HORIZONTAL, insets, 0, 0));
        }
        // About me
        String descField = vcard.getField("DESC");
        if (!isEmpty(descField)) {
            JLabel descLabel = new JLabel(Res.getString("label.description") + ": " + descField);
            add(descLabel, new GridBagConstraints(1, row++, 1, 2, 1, 0, NORTHWEST, HORIZONTAL, insets, 0, 0));
        }
    }

    private void startEmailClient(String emailAddress) {
   		try {
			Desktop.getDesktop().mail(new URI("mailto:" + emailAddress));
		} catch (IOException e) {
			Log.error("Can't open Mailer", e);
		} catch (URISyntaxException e) {
			Log.error("URI Wrong", e);
		}
    }

}
