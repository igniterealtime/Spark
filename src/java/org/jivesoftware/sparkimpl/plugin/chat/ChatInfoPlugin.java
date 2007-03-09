/**
 * $Revision$
 * $Date$
 *
 * Copyright (C) 1999-2005 Jive Software. All rights reserved.
 * This software is the proprietary information of Jive Software. Use is subject to license terms.
 */

package org.jivesoftware.sparkimpl.plugin.chat;

import org.jdesktop.jdic.desktop.Desktop;
import org.jdesktop.jdic.desktop.DesktopException;
import org.jdesktop.jdic.desktop.Message;
import org.jivesoftware.Spark;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.ui.ChatRoomButton;
import org.jivesoftware.spark.ui.ContactInfoHandler;
import org.jivesoftware.spark.ui.ContactInfoWindow;
import org.jivesoftware.spark.ui.ContactItem;
import org.jivesoftware.spark.util.SwingWorker;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Allows a plugin implementor to add specific operations to the users ContactInfoWindow. The <code>ContactInfoWindow</code> is used
 * to display information about a particular user within the <code>ContactList</code>. Some operations can be adding buttons to start
 * conversations or send emails.
 *
 * @author Derek DeMoro
 * @see ContactInfoWindow
 */
public class ChatInfoPlugin implements Plugin, ContactInfoHandler {

    private final ChatManager chatManager = SparkManager.getChatManager();

    // The active ContactInfoWindow.
    private ContactInfoWindow contactInfoWindow;

    public void initialize() {
        // Add this as a contact info handler.
        chatManager.addContactInfoHandler(this);
    }

    public void shutdown() {
    }

    public boolean canShutDown() {
        return true;
    }

    public void uninstall() {
    }

    /**
     * Called when a user hovers over a ContactItem.
     *
     * @param contactInfo the ContactInfoWindow.
     */
    public void handleContactInfo(final ContactInfoWindow contactInfo) {
        this.contactInfoWindow = contactInfo;

        final ChatRoomButton chatButton = new ChatRoomButton(SparkRes.getImageIcon(SparkRes.SMALL_MESSAGE_IMAGE));
        chatButton.setToolTipText("Start a conversation.");
        chatButton.setText("Chat");

        contactInfo.addChatRoomButton(chatButton);

        chatButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startConversation(contactInfo.getContactItem());
            }
        });

        lookupEmailAddress(contactInfoWindow.getContactItem().getJID());
    }


    private void lookupEmailAddress(final String jid) {
        final VCard vcard = SparkManager.getVCardManager().getVCardFromMemory(jid);
        if (contactInfoWindow.getContactItem() == null || !contactInfoWindow.getContactItem().getJID().equals(jid)) {
            return;
        }

        if (vcard != null && vcard.getEmailHome() != null) {
            final ChatRoomButton emailButton = new ChatRoomButton(SparkRes.getImageIcon(SparkRes.SEND_MAIL_IMAGE_16x16));
            emailButton.setText("Email");
            emailButton.setToolTipText("Send an email");
            contactInfoWindow.addChatRoomButton(emailButton);
            contactInfoWindow.getToolbar().invalidate();
            contactInfoWindow.getToolbar().validate();
            contactInfoWindow.getToolbar().repaint();
            emailButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    sendEmail(contactInfoWindow.getContactItem());
                }
            });
        }
    }

    /**
     * Start a conversation with a user based on their ContactItem reference.
     *
     * @param item the ContactItem.
     */
    private void startConversation(ContactItem item) {
        contactInfoWindow.dispose();
        chatManager.activateChat(item.getJID(), item.getNickname());
    }

    /**
     * Starts up an email client with the users email address.
     *
     * @param item the ContactItem.
     */
    private void sendEmail(ContactItem item) {
        contactInfoWindow.dispose();

        final VCard vcard = SparkManager.getVCardManager().getVCard(item.getJID());

        String emailHome = vcard.getEmailHome();

        if (emailHome != null && Spark.isWindows()) {
            final Message message = new Message();

            final List<String> list = new ArrayList<String>();
            list.add(emailHome);

            message.setToAddrs(list);

            SwingWorker worker = new SwingWorker() {
                public Object construct() {
                    try {
                        Desktop.mail(message);
                    }
                    catch (DesktopException e) {
                        e.printStackTrace();
                    }
                    return true;
                }
            };

            worker.start();

        }
    }
}
