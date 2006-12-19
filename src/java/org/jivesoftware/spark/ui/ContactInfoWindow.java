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

import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.BackgroundPanel;
import org.jivesoftware.spark.component.JMultilineLabel;
import org.jivesoftware.spark.component.borders.PartialLineBorder;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.gateways.transports.Transport;
import org.jivesoftware.sparkimpl.plugin.gateways.transports.TransportUtils;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;

/**
 * Represents the UI for the "ToolTip" functionallity in the ContactList.
 *
 * @author Derek DeMoro
 */
public class ContactInfoWindow extends JPanel {
    private final JLabel nicknameLabel = new JLabel();
    private final JMultilineLabel statusLabel = new JMultilineLabel();
    private final JLabel fullJIDLabel = new JLabel();
    private final JLabel imageLabel = new JLabel();

    private ContactItem contactItem;

    private JPanel toolbar;

    private JWindow window = new JWindow();

    private boolean inWindow;

    private ChatManager chatManager;

    public ContactInfoWindow() {
        setLayout(new GridBagLayout());

        this.chatManager = SparkManager.getChatManager();

        setBackground(Color.white);

        toolbar = new BackgroundPanel();
        toolbar.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        toolbar.setOpaque(false);

        add(toolbar, new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

        add(nicknameLabel, new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0));
        add(statusLabel, new GridBagConstraints(0, 3, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 8, 5, 5), 0, 0));

        add(fullJIDLabel, new GridBagConstraints(0, 4, 2, 1, 1.0, 1.0, GridBagConstraints.SOUTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 5), 0, 0));
        add(imageLabel, new GridBagConstraints(1, 1, 1, 3, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 5), 0, 0));


        nicknameLabel.setFont(new Font("Dialog", Font.BOLD, 12));
        statusLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
        statusLabel.setForeground(Color.gray);
        fullJIDLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
        fullJIDLabel.setForeground(Color.gray);


        nicknameLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.gray));
        fullJIDLabel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.gray));

        setBorder(BorderFactory.createLineBorder(Color.gray, 1));

        window.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                inWindow = true;
            }

            public void mouseExited(MouseEvent e) {
                Point point = e.getLocationOnScreen();
                Point windowPoint = window.getLocationOnScreen();

                int newX = (int)point.getX();
                int newY = (int)point.getY();

                int x = (int)windowPoint.getX();
                int y = (int)windowPoint.getY();

                boolean close = false;

                if (newX < x || newX > x + window.getWidth()) {
                    close = true;
                }

                if (newY < y || newY > y + window.getHeight()) {
                    close = true;
                }

                if (close) {
                    inWindow = false;
                    checkWindow();
                }
            }
        });

        window.getContentPane().add(this);

    }

    public void checkWindow() {
        final SwingWorker worker = new SwingWorker() {
            public Object construct() {
                try {
                    Thread.sleep(100);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return true;
            }

            public void finished() {
                if (!inWindow) {
                    window.setVisible(false);
                    contactItem = null;
                }
            }
        };

        worker.start();
    }

    public void display(ContactGroup group, MouseEvent e) {
        int loc = group.getList().locationToIndex(e.getPoint());


        ContactItem item = (ContactItem)group.getList().getModel().getElementAt(loc);
        if (item == null || item.getFullJID() == null) {
            return;
        }

        if (getContactItem() != null && getContactItem() == item) {
            return;
        }

        nicknameLabel.setIcon(item.getIcon());

        Point point = group.getList().indexToLocation(loc);

        window.setFocusableWindowState(false);
        setContactItem(item);
        chatManager.notifyContactInfoHandlers(this);
        window.pack();


        Point mainWindowLocation = SparkManager.getMainWindow().getLocationOnScreen();
        Point listLocation = group.getList().getLocationOnScreen();

        int x = (int)mainWindowLocation.getX() + SparkManager.getMainWindow().getWidth();

        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        if ((int)screenSize.getWidth() - getPreferredSize().getWidth() >= x) {
            window.setLocation(x, (int)listLocation.getY() + (int)point.getY());
            if (!window.isVisible())
                window.setVisible(true);
        }
        else {
            window.setLocation((int)mainWindowLocation.getX() - (int)getPreferredSize().getWidth(), (int)listLocation.getY() + (int)point.getY());
            if (!window.isVisible())
                window.setVisible(true);
        }
    }

    public void setContactItem(ContactItem contactItem) {
        this.contactItem = contactItem;
        if (contactItem == null) {
            return;
        }

        nicknameLabel.setText(contactItem.getNickname());
        statusLabel.setText(contactItem.getStatus());

        Transport transport = TransportUtils.getTransport(StringUtils.parseServer(contactItem.getFullJID()));
        if (transport != null) {
            fullJIDLabel.setIcon(transport.getIcon());
            String name = StringUtils.parseName(contactItem.getFullJID());
            fullJIDLabel.setText(transport.getName() + " - " + name);
        }
        else {
            fullJIDLabel.setText(contactItem.getFullJID());
            fullJIDLabel.setIcon(null);
        }

        imageLabel.setBorder(null);

        try {
            URL avatarURL = contactItem.getAvatarURL();
            ImageIcon icon = null;
            if (avatarURL != null) {
                icon = new ImageIcon(avatarURL);
            }

            if (icon != null && icon.getIconHeight() > 1) {
                icon = GraphicUtils.scaleImageIcon(icon, 96, 96);
                imageLabel.setIcon(icon);

                imageLabel.setBorder(new PartialLineBorder(Color.gray, 1));
            }
            else {
                icon = new ImageIcon(SparkRes.getImageIcon(SparkRes.BLANK_24x24).getImage().getScaledInstance(1, 64, Image.SCALE_SMOOTH));
                imageLabel.setIcon(icon);
            }
        }
        catch (MalformedURLException e) {
            Log.error(e);
        }

        toolbar.removeAll();
    }

    public ContactItem getContactItem() {
        return contactItem;
    }

    public void addChatRoomButton(ChatRoomButton button) {
        toolbar.add(button);
    }

    public JPanel getToolbar() {
        return toolbar;
    }


    public void dispose() {
        window.dispose();
    }


    public Dimension getPreferredSize() {
        final Dimension size = super.getPreferredSize();
        size.width = 330;
        size.height = 160;
        return size;
    }
}
