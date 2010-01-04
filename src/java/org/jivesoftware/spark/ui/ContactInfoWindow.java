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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JWindow;

import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.gateways.transports.Transport;
import org.jivesoftware.sparkimpl.plugin.gateways.transports.TransportUtils;

/**
 * Represents the UI for the "ToolTip" functionallity in the ContactList.
 *
 * @author Derek DeMoro
 */
public class ContactInfoWindow extends JPanel {
	private static final long serialVersionUID = 8409694344721276453L;
	private final JLabel nicknameLabel = new JLabel();
    private final JTextArea statusLabel = new JTextArea();
    private final JLabel fullJIDLabel = new JLabel();
    private final JLabel avatarLabel = new JLabel();
    private final JLabel iconLabel = new JLabel();
    private final JLabel titleLabel = new JLabel();

    private ContactItem contactItem;

    private JWindow window = new JWindow();

    private static ContactInfoWindow singleton;
    private static final Object LOCK = new Object();

    /**
     * Returns the singleton instance of <CODE>ContactInfoWindow</CODE>,
     * creating it if necessary.
     * <p/>
     *
     * @return the singleton instance of <Code>ContactInfoWindow</CODE>
     */
    public static ContactInfoWindow getInstance() {
        // Synchronize on LOCK to ensure that we don't end up creating
        // two singletons.
        synchronized (LOCK) {
            if (null == singleton) {
                ContactInfoWindow controller = new ContactInfoWindow();
                singleton = controller;
                return controller;
            }
        }
        return singleton;
    }

    private ContactInfoWindow() {
   	 
        setLayout(new GridBagLayout());
       
        setBackground(Color.white);

        add(avatarLabel, new GridBagConstraints(0, 1, 1, 3, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
        add(iconLabel, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 0, 2), 0, 0));
        add(nicknameLabel, new GridBagConstraints(2, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 0, 2), 0, 0));
        add(statusLabel, new GridBagConstraints(2, 2, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 2), 0, 0));
        add(titleLabel, new GridBagConstraints(2, 3, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 2, 2), 0, 0));
        add(fullJIDLabel, new GridBagConstraints(0, 5, 4, 1, 1.0, 1.0, GridBagConstraints.SOUTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 2, 2, 2), 0, 0));


        nicknameLabel.setFont(new Font("Dialog", Font.BOLD, 12));
        statusLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
        statusLabel.setForeground(Color.gray);
        statusLabel.setLineWrap(true);
        statusLabel.setWrapStyleWord(true);
        statusLabel.setEditable(false);
        statusLabel.setBorder(null);
        fullJIDLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
        fullJIDLabel.setForeground(Color.gray);
        titleLabel.setFont(new Font("Dialog", Font.PLAIN, 11));
        titleLabel.setForeground(Color.gray);


        fullJIDLabel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.gray));

        setBorder(BorderFactory.createLineBorder(Color.gray, 1));

        window.getContentPane().add(this);

        final ContactList contactList = SparkManager.getWorkspace().getContactList();
        contactList.addContactListListener(new ContactListListener() {
            public void contactItemAdded(ContactItem item) {
            }

            public void contactItemRemoved(ContactItem item) {
            }

            public void contactGroupAdded(ContactGroup group) {
            }

            public void contactGroupRemoved(ContactGroup group) {
            }

            public void contactItemClicked(ContactItem item) {
                if (window != null) {
                    window.dispose();
                }
            }

            public void contactItemDoubleClicked(ContactItem item) {
                if (window != null) {
                    window.dispose();
                }
            }
        });
    }

    public void display(ContactGroup group, MouseEvent e) {
        int loc = group.getList().locationToIndex(e.getPoint());


        ContactItem item = (ContactItem)group.getList().getModel().getElementAt(loc);
        if (item == null || item.getJID() == null) {
            return;
        }

        if (getContactItem() != null && getContactItem() == item) {
            return;
        }

        iconLabel.setIcon(item.getIcon());

        Point point = group.getList().indexToLocation(loc);

        window.setFocusableWindowState(false);
        setContactItem(item);
        window.pack();


        Point mainWindowLocation = SparkManager.getMainWindow().getLocationOnScreen();
        Point listLocation = group.getList().getLocationOnScreen();

        int x = (int)mainWindowLocation.getX() + SparkManager.getMainWindow().getWidth();

        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        if ((int)screenSize.getWidth() - getPreferredSize().getWidth() >= x) {
            int y = (int)listLocation.getY() + (int)point.getY();
            y = y - 5;
            window.setLocation(x, y);
            if (!window.isVisible()) {
                window.setVisible(true);
            }
        }
        else {
            int y = (int)listLocation.getY() + (int)point.getY();
            y = y - 5;
            window.setLocation((int)mainWindowLocation.getX() - (int)getPreferredSize().getWidth(), y);
            if (!window.isVisible()) {
                window.setVisible(true);
            }
        }
    }

    public void setContactItem(ContactItem contactItem) {
        this.contactItem = contactItem;
        if (contactItem == null) {
            return;
        }

        nicknameLabel.setText(contactItem.getDisplayName());

        String status = contactItem.getStatus();
        if (!ModelUtil.hasLength(status)) {
            if (contactItem.getPresence() == null || contactItem.getPresence().getType() == Presence.Type.unavailable) {
                status = Res.getString("offline");
            }
            else {
                status = Res.getString("online");
            }
        }
        statusLabel.setText(status);

        Transport transport = TransportUtils.getTransport(StringUtils.parseServer(contactItem.getJID()));
        if (transport != null) {
            fullJIDLabel.setIcon(transport.getIcon());
            String name = StringUtils.parseName(contactItem.getJID());
            name = StringUtils.unescapeNode(name);
            fullJIDLabel.setText(transport.getName() + " - " + name);
        }
        else {
            String name = StringUtils.unescapeNode(contactItem.getJID());
            fullJIDLabel.setText(name);
            fullJIDLabel.setIcon(null);
        }

        avatarLabel.setBorder(null);

        try {
            URL avatarURL = contactItem.getAvatarURL();
            ImageIcon icon = null;
            if (avatarURL != null) {
                icon = new ImageIcon(avatarURL);
            }

            if (icon != null && icon.getIconHeight() > 1) {
                icon = GraphicUtils.scaleImageIcon(icon, 96, 96);
                avatarLabel.setIcon(icon);
            }
            else {
                icon = SparkRes.getImageIcon(SparkRes.DEFAULT_AVATAR_64x64_IMAGE);
                avatarLabel.setIcon(icon);
            }
            avatarLabel.setBorder(BorderFactory.createBevelBorder(0, Color.white, Color.lightGray));
        }
        catch (MalformedURLException e) {
            Log.error(e);
        }

        // Get VCard from memory (if available)
        String title = "";
        VCard vcard = SparkManager.getVCardManager().getVCardFromMemory(StringUtils.parseBareAddress(contactItem.getJID()));
        if (vcard != null) {
            title = vcard.getField("TITLE");
            if (!ModelUtil.hasLength(title)) {
                title = "";
            }
        }

        titleLabel.setText(title);
    }

    public ContactItem getContactItem() {
        return contactItem;
    }

    public void dispose() {
        window.setVisible(false);
        contactItem = null;
        window.dispose();
    }


    public Dimension getPreferredSize() {
        final Dimension size = super.getPreferredSize();
        size.width = 300;
        size.height = 125;
        return size;
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
        Point point = e.getPoint();

        Dimension dim = window.getSize();

        int x = (int)point.getX();
        int y = (int)point.getY();

        boolean close = false;

        if (x < 0 || x >= dim.getWidth()) {
            close = true;
        }

        if (y < 0 || y >= dim.getHeight()) {
            close = true;
        }

        if (close) {
            window.setVisible(false);
            contactItem = null;
        }
    }

}
