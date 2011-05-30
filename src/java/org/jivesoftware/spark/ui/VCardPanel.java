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
package org.jivesoftware.spark.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.Timer;

import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.UserManager;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.log.Log;

/**
 * UI to display VCard Information in Wizards, Dialogs, Chat Rooms and any other container.
 *
 * @author Derek DeMoro
 */
public class VCardPanel extends JPanel {

	private static final long serialVersionUID = -5810110243694315630L;
	private Cursor DEFAULT_CURSOR = new Cursor(Cursor.DEFAULT_CURSOR);
    private Cursor LINK_CURSOR = new Cursor(Cursor.HAND_CURSOR);

    private JWindow _avatarHoverWindow = new JWindow();
    private final String jid;
    private final JLabel avatarImage;

    private String emailAddress = "";

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
        add(avatarImage, new GridBagConstraints(0, 0, 1, 3, 0.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 0), 0, 0));
        buildAvatarHover();

        Image aImage = SparkRes.getImageIcon(SparkRes.BLANK_24x24).getImage();
        aImage = aImage.getScaledInstance(-1, 64, Image.SCALE_SMOOTH);
        ImageIcon ico = new ImageIcon(aImage);

        avatarImage.setIcon(ico);


        VCard vcard = SparkManager.getVCardManager().getVCard(jid);


        if (vcard == null) {
            // Do nothing.
            return;
        }

        ImageIcon icon = null;

        byte[] bytes = vcard.getAvatar();
        if (bytes != null && bytes.length > 0) {
            try {
                icon = new ImageIcon(bytes);
                Image newImage = icon.getImage();
                if (icon.getIconHeight() > 48 || icon.getIconWidth() > 48) {
                newImage = newImage.getScaledInstance(-1, 48, Image.SCALE_SMOOTH);
                }
                icon = new ImageIcon(newImage);
            }
            catch (Exception e) {
                Log.error(e);
            }
        }
        else {
            icon = SparkRes.getImageIcon(SparkRes.DEFAULT_AVATAR_32x32_IMAGE);
        }

        if (icon != null && icon.getIconWidth() > 0) {
            avatarImage.setIcon(icon);
            avatarImage.setBorder(BorderFactory.createBevelBorder(0, Color.white, Color.lightGray));
        }

        vcard.setJabberId(jid);
        buildUI(vcard);
    }
    
    
    private void buildAvatarHover() {
        _avatarHoverWindow.addMouseListener(new MouseAdapter() {
            public void mouseExited(MouseEvent e) {
                showAvatarBig(false, null);

            };
        });

    }

    private void showAvatarBig(boolean bool, VCard vcard) {
        if (bool && !_avatarHoverWindow.isVisible()) {

            _avatarHoverWindow.setLocation(avatarImage.getLocationOnScreen().x+2,avatarImage.getLocationOnScreen().y+2);
            _avatarHoverWindow.setVisible(true);
            ImageIcon icon = null;
            JLabel label = new JLabel();
            _avatarHoverWindow.add(label);
            byte[] bytes = vcard.getAvatar();
            Image newImage = null;
            if (bytes != null && bytes.length > 0) {
                try {
                    icon = new ImageIcon(bytes);
                    newImage = icon.getImage();
                    if (icon.getIconHeight() > 128 || icon.getIconWidth() > 128) {
                    newImage = newImage.getScaledInstance(-1, 128, Image.SCALE_SMOOTH);
                    }
                    icon = new ImageIcon(newImage);

                } catch (Exception e1) {
                    Log.error(e1);
                }

            } else {
                icon = SparkRes.getImageIcon(SparkRes.DEFAULT_AVATAR_32x32_IMAGE);
            }

            label.setIcon(icon);
            _avatarHoverWindow.pack();
            _avatarHoverWindow.invalidate();
            _avatarHoverWindow.validate();
            _avatarHoverWindow.repaint();

        } else {

            _avatarHoverWindow.setVisible(false);
        }

    }

    private void buildUI(final VCard vcard) {

        avatarImage.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 2) {
                    SparkManager.getVCardManager().viewProfile(vcard.getJabberId(), avatarImage);
                }
            }

            final Timer timer = new Timer(500, new ActionListener() {
                public void actionPerformed(ActionEvent actionEvent) {
                    showAvatarBig(true, vcard);
                }
            });

            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                timer.start();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                timer.stop();
            }

        });

        String firstName = vcard.getFirstName();
        if (firstName == null) {
            firstName = "";
        }

        String lastName = vcard.getLastName();
        if (lastName == null) {
            lastName = "";
        }


        final JLabel usernameLabel = new JLabel();
        usernameLabel.setHorizontalTextPosition(JLabel.LEFT);
        usernameLabel.setFont(new Font("Dialog", Font.BOLD, 15));

        usernameLabel.setForeground(Color.GRAY);
        if (ModelUtil.hasLength(firstName) && ModelUtil.hasLength(lastName)) {
            usernameLabel.setText(firstName + " " + lastName);
        }
        else {
            String nickname = SparkManager.getUserManager().getUserNicknameFromJID(jid);
            usernameLabel.setText(UserManager.unescapeJID(nickname));
        }


        final Icon icon = SparkManager.getChatManager().getIconForContactHandler(vcard.getJabberId());
        if (icon != null) {
            usernameLabel.setIcon(icon);
        }


        add(usernameLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));

        String title = vcard.getField("TITLE");
        if (ModelUtil.hasLength(title)) {
            final JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(new Font("Dialog", Font.PLAIN, 11));
            add(titleLabel, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 7, 0, 0), 0, 0));
        }

        if (ModelUtil.hasLength(vcard.getEmailHome())) {
            emailAddress = vcard.getEmailHome();
        }

        final Color linkColor = new Color(49, 89, 151);
        final String unselectedText = "<html><body><font color=" + GraphicUtils.toHTMLColor(linkColor) + "><u>" + emailAddress + "</u></font></body></html>";
        final String hoverText = "<html><body><font color=red><u>" + emailAddress + "</u></font></body></html>";
        final JLabel emailTime = new JLabel(unselectedText);
        emailTime.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                startEmailClient(emailAddress);
            }

            public void mouseEntered(MouseEvent e) {
                emailTime.setText(hoverText);
                setCursor(LINK_CURSOR);

            }

            public void mouseExited(MouseEvent e) {
                emailTime.setText(unselectedText);
                setCursor(DEFAULT_CURSOR);
            }
        });


        add(emailTime, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 7, 5, 0), 0, 0));
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
