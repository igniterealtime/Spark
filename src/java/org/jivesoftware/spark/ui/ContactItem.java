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

import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.packet.DefaultPacketExtension;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.RosterPacket;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.PresenceManager;
import org.jivesoftware.spark.ui.status.StatusItem;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.profile.VCardManager;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class ContactItem extends JPanel {
    private JLabel imageLabel;
    private JLabel nicknameLabel;
    private JLabel descriptionLabel;
    private String nickname;
    private String fullJID;
    private Icon icon;

    private String status;
    private String groupName;

    boolean available;

    private Presence presence;

    private String hash = "";

    private File contactsDir;

    private JLabel sideIcon;


    public ContactItem(String nickname, String fullJID) {
        setLayout(new GridBagLayout());

        // Set default presence
        presence = new Presence(Presence.Type.unavailable);

        contactsDir = new File(SparkManager.getUserDirectory(), "contacts");

        nicknameLabel = new JLabel();
        descriptionLabel = new JLabel();
        imageLabel = new JLabel();
        sideIcon = new JLabel();

        nicknameLabel.setHorizontalTextPosition(JLabel.LEFT);
        nicknameLabel.setHorizontalAlignment(JLabel.LEFT);
        nicknameLabel.setText(nickname);


        descriptionLabel.setFont(new Font("Dialog", Font.PLAIN, 11));
        descriptionLabel.setForeground((Color)UIManager.get("ContactItemDescription.foreground"));
        descriptionLabel.setHorizontalTextPosition(JLabel.LEFT);
        descriptionLabel.setHorizontalAlignment(JLabel.LEFT);


        this.setOpaque(true);

        add(imageLabel, new GridBagConstraints(0, 0, 1, 2, 0.0, 0.0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 0), 0, 0));
        add(nicknameLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));
        add(descriptionLabel, new GridBagConstraints(2, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 2, 0), 0, 0));
        add(sideIcon, new GridBagConstraints(3, 0, 1, 2, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));

        setNickname(nickname);
        setFullJID(fullJID);
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
        nicknameLabel.setText(nickname);
    }

    public String getFullJID() {
        return fullJID;
    }

    public void setFullJID(String fullJID) {
        this.fullJID = fullJID;
    }

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
        imageLabel.setIcon(icon);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getContactJID() {
        return fullJID;
    }


    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public JLabel getNicknameLabel() {
        return nicknameLabel;
    }

    public JLabel getDescriptionLabel() {
        return descriptionLabel;
    }

    public Presence getPresence() {
        return presence;
    }

    /**
     * Sets the current presence on this contact item.
     *
     * @param presence
     */
    public void setPresence(Presence presence) {

        this.presence = presence;

        final PacketExtension packetExtension = presence.getExtension("x", "vcard-temp:x:update");

        // Handle vCard update packet.
        if (packetExtension != null) {
            DefaultPacketExtension o = (DefaultPacketExtension)packetExtension;
            String hash = o.getValue("photo");
            if (hash != null) {
                this.hash = hash;

                if (!hashExists(hash)) {
                    updateAvatar(hash);
                }
            }
        }


        updatePresenceIcon(presence);
    }

    private boolean hashExists(String hash) {
        contactsDir.mkdirs();

        final File imageFile = new File(contactsDir, hash);
        return imageFile.exists();
    }

    public URL getAvatarURL() throws MalformedURLException {
        contactsDir.mkdirs();

        if (ModelUtil.hasLength(hash)) {
            final File imageFile = new File(contactsDir, hash);
            if (imageFile.exists()) {
                return imageFile.toURL();
            }
        }

        return null;
    }

    private void updateAvatar(final String hash) {
        Thread updateAvatarThread = new Thread(new Runnable() {
            public void run() {
                contactsDir.mkdirs();

                final File imageFile = new File(contactsDir, hash);

                VCard vcard = SparkManager.getVCardManager().getVCard(getFullJID(), false);

                try {
                    byte[] bytes = vcard.getAvatar();
                    if (bytes != null) {
                        ImageIcon icon = new ImageIcon(bytes);
                        icon = VCardManager.scale(icon);
                        if (icon != null && icon.getIconWidth() != -1) {
                            BufferedImage image = GraphicUtils.convert(icon.getImage());
                            ImageIO.write(image, "PNG", imageFile);
                        }
                    }

                    SparkManager.getVCardManager().addVCard(getFullJID(), vcard);
                }
                catch (Exception e) {
                    Log.error("Unable to update avatar in Contact Item.", e);
                }
            }
        });

        updateAvatarThread.start();
    }

    public String toString() {
        return nicknameLabel.getText();
    }


    public void updatePresenceIcon(Presence presence) {
        ChatManager chatManager = SparkManager.getChatManager();
        boolean handled = chatManager.fireContactItemPresenceChanged(this, presence);
        if (handled) {
            return;
        }

        String status = presence.getStatus();
        Icon statusIcon = SparkRes.getImageIcon(SparkRes.GREEN_BALL);
        boolean isAvailable = false;
        if (status == null && presence.isAvailable()) {
            Presence.Mode mode = presence.getMode();
            if (mode == Presence.Mode.available) {
                status = "Available";
                isAvailable = true;
            }
            else if (mode == Presence.Mode.away) {
                status = "I'm away";
                statusIcon = SparkRes.getImageIcon(SparkRes.IM_AWAY);
            }
            else if (mode == Presence.Mode.chat) {
                status = "I'm free to chat";
            }
            else if (mode == Presence.Mode.dnd) {
                status = "Do not disturb";
                statusIcon = SparkRes.getImageIcon(SparkRes.IM_AWAY);
            }
            else if (mode == Presence.Mode.xa) {
                status = "Extended away";
                statusIcon = SparkRes.getImageIcon(SparkRes.IM_AWAY);
            }
        }

        if (presence.isAvailable() && (presence.getMode() == Presence.Mode.dnd || presence.getMode() == Presence.Mode.away || presence.getMode() == Presence.Mode.xa)) {
            statusIcon = SparkRes.getImageIcon(SparkRes.IM_AWAY);
        }
        else if (presence.isAvailable()) {
            isAvailable = true;
        }
        else if (!presence.isAvailable()) {
            getNicknameLabel().setFont(new Font("Dialog", Font.PLAIN, 11));
            getNicknameLabel().setForeground((Color)UIManager.get("ContactItemOffline.color"));

            RosterEntry entry = SparkManager.getConnection().getRoster().getEntry(getFullJID());
            if (entry != null && (entry.getType() == RosterPacket.ItemType.none || entry.getType() == RosterPacket.ItemType.from)
                    && RosterPacket.ItemStatus.SUBSCRIPTION_PENDING == entry.getStatus()) {
                // Do not move out of group.
                getNicknameLabel().setFont(new Font("Dialog", Font.PLAIN, 11));
                setStatusText("Pending");
            }
            else {
                setIcon(null);
                setFont(new Font("Dialog", Font.PLAIN, 11));
                getNicknameLabel().setFont(new Font("Dialog", Font.PLAIN, 11));
                setAvailable(false);

                String itemStatus = presence.getStatus();
                if (itemStatus == null) {
                    setStatusText("");
                }
                else {
                    setStatusText(itemStatus);
                }
            }

            setAvailable(false);
            return;
        }
        else if (!presence.isAvailable()) {
            getNicknameLabel().setFont(new Font("Dialog", Font.PLAIN, 11));
            getNicknameLabel().setForeground((Color)UIManager.get("ContactItemOffline.color"));

            RosterEntry entry = SparkManager.getConnection().getRoster().getEntry(getFullJID());
            if (entry != null && (entry.getType() == RosterPacket.ItemType.none || entry.getType() == RosterPacket.ItemType.from)
                    && RosterPacket.ItemStatus.SUBSCRIPTION_PENDING == entry.getStatus()) {
                // Do not move out of group.
                setIcon(SparkRes.getImageIcon(SparkRes.SMALL_QUESTION));
                getNicknameLabel().setFont(new Font("Dialog", Font.PLAIN, 11));
                setStatusText("Pending");
            }
            else {
                setIcon(null);
                setFont(new Font("Dialog", Font.PLAIN, 11));
                getNicknameLabel().setFont(new Font("Dialog", Font.PLAIN, 11));
                setAvailable(false);
                setStatusText("");
            }

            sideIcon.setIcon(null);
            setAvailable(false);
            return;
        }

        Icon sIcon = PresenceManager.getIconFromPresence(presence);
        if (sIcon != null) {
            setIcon(sIcon);
        }
        else {
            setIcon(statusIcon);
        }
        if (status != null) {
            setStatus(status);
        }

        if (status != null && status.toLowerCase().indexOf("phone") != -1) {
            statusIcon = SparkRes.getImageIcon(SparkRes.ON_PHONE_IMAGE);
            setIcon(statusIcon);
        }

        // Always change nickname label to black.
        getNicknameLabel().setForeground((Color)UIManager.get("ContactItemNickname.foreground"));


        if (isAvailable) {
            getNicknameLabel().setFont(new Font("Dialog", Font.PLAIN, 11));
            if ("Online".equals(status) || Res.getString("available").equalsIgnoreCase(status)) {
                setStatusText("");
            }
            else {
                setStatusText(status);
            }
        }
        else if (presence.isAvailable()) {
            getNicknameLabel().setFont(new Font("Dialog", Font.ITALIC, 11));
            getNicknameLabel().setForeground(Color.gray);
            if (status != null) {
                setStatusText(status);
            }
        }

        setAvailable(true);
    }

    public void setStatusText(String status) {
        setStatus(status);

        if (ModelUtil.hasLength(status)) {
            getDescriptionLabel().setText(" - " + status);
        }
        else {
            getDescriptionLabel().setText("");
        }
    }

    public void setSideIcon(Icon icon) {
        sideIcon.setIcon(icon);
    }

    public void showUserComingOnline() {
        // Change Font
        getNicknameLabel().setFont(new Font("Dialog", Font.BOLD, 11));
        getNicknameLabel().setForeground(new Color(255, 128, 0));
    }

    public void showUserGoingOfflineOnline() {
        // Change Font
        getNicknameLabel().setFont(new Font("Dialog", Font.BOLD, 11));
        getNicknameLabel().setForeground(Color.red);
    }


}
