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
package org.jivesoftware.spark.ui.conferences;

import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.resource.Res;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.Affiliate;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.spark.component.renderer.ListIconRenderer;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.rooms.GroupChatRoom;
import org.jivesoftware.spark.util.log.Log;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;


/**
 * Handles unbanning banned users in Chat Rooms.
 */
public class BannedUsers extends JPanel {
    private static final long serialVersionUID = 6422162361752646645L;

    private MultiUserChat chat;

    private DefaultListModel listModel = new DefaultListModel();
    private JList list = new JList(listModel);
    private JMenuItem unBanMenuItem = new JMenuItem(Res.getString("menuitem.unban"));

    /**
     * Construct UI
     */
    public BannedUsers() {
        setLayout(new BorderLayout());
        list.setCellRenderer(new ListIconRenderer());
        add(list, BorderLayout.CENTER);
        // Respond to Double-Click in Agent List to start a chat
        list.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                if (evt.isPopupTrigger()) {
                    int index = list.locationToIndex(evt.getPoint());
                    list.setSelectedIndex(index);
                    ImageIcon icon = (ImageIcon)list.getModel().getElementAt(index);
                    String jid = icon.getDescription();
                    showPopup(evt, jid);
                }
            }

            public void mouseReleased(MouseEvent evt) {
                if (evt.isPopupTrigger()) {
                    int index = list.locationToIndex(evt.getPoint());
                    list.setSelectedIndex(index);
                    ImageIcon icon = (ImageIcon)list.getModel().getElementAt(index);
                    String jid = icon.getDescription();
                    showPopup(evt, jid);
                }
            }
        });

        unBanMenuItem.addActionListener( e -> {
            int index = list.getSelectedIndex();
            ImageIcon icon = (ImageIcon)list.getModel().getElementAt(index);
            String jid = icon.getDescription();
            try {
                chat.grantMembership(jid);
            }
            catch (XMPPException | SmackException memEx) {
                Log.error("Error granting membership", memEx);
            }
            listModel.removeElementAt(index);

        } );
    }

    /**
     * Binds a ChatRoom to listen to.
     *
     * @param cRoom the group chat room.
     */
    public void setChatRoom(ChatRoom cRoom) {
        GroupChatRoom chatRoom = (GroupChatRoom) cRoom;
        chat = chatRoom.getMultiUserChat();
    }

    /**
     * Loads all banned users in a ChatRoom.
     */
    public void loadAllBannedUsers() {
        // Clear all elements from model
        listModel.clear();

        Iterator<Affiliate> bannedUsers = null;
        try {
            bannedUsers = chat.getOutcasts().iterator();
        }
        catch (XMPPException | SmackException e) {
            Log.error("Error loading all banned users", e);
        }

        while (bannedUsers != null && bannedUsers.hasNext()) {
            Affiliate bannedUser = bannedUsers.next();
            ImageIcon icon = SparkRes.getImageIcon(SparkRes.STAR_RED_IMAGE);
            icon.setDescription(bannedUser.getJid());
            listModel.addElement(icon);
        }
    }

    /**
     * Responsible for popping up the menu items.
     *
     * @param e   the MouseEvent that triggered it.
     * @param jid the JID to handle.
     */
    private void showPopup(MouseEvent e, String jid) {
        final JPopupMenu popup = new JPopupMenu();
        popup.add(unBanMenuItem);
        popup.show(this, e.getX(), e.getY());
    }
}
