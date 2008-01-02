/**
 * $RCSfile: ,v $
 * $Revision: 1.0 $
 * $Date: 2005/05/25 04:20:03 $
 *
 * Copyright (C) 1999-2005 Jive Software. All rights reserved.
 *
 * This software is the proprietary information of Jive Software. Use is
 subject to license terms.
 */

package org.jivesoftware.spark.plugin;


import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.spark.ui.ContactList;
import org.jivesoftware.spark.ui.ContactItem;
import org.jivesoftware.spark.ui.ChatRoomListenerAdapter;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.component.VerticalFlowLayout;
import org.jivesoftware.spark.component.panes.CollapsiblePane;
import org.jivesoftware.sparkimpl.profile.VCardManager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.text.BadLocationException;

public class GooglePlugin implements Plugin {
    public void initialize() {
        // Add "Find Reference to."
        ContactList contactList = SparkManager.getWorkspace().getContactList();
        contactList.addContextMenuListener(new ContextMenuListener() {
            public void poppingUp(Object object, JPopupMenu popup) {
                if (object instanceof ContactItem) {
                    final ContactItem item = (ContactItem)object;
                    final Action findReferenceAction = new AbstractAction() {
                        public void actionPerformed(ActionEvent actionEvent) {
                            findReferences(item);
                        }
                    };
                    ClassLoader cl = getClass().getClassLoader();
                    URL url = cl.getResource("images/google.gif");
                    ImageIcon icon = new ImageIcon(url);
                    findReferenceAction.putValue(Action.SMALL_ICON, icon);
                    findReferenceAction.putValue(Action.NAME, "Find documents relating to " + item.getNickname());
                    popup.add(findReferenceAction);
                }
            }

            public void poppingDown(JPopupMenu popup) {

            }

            public boolean handleDefaultAction(MouseEvent e) {
                return false;
            }
        });

        SparkManager.getSearchManager().addSearchService(new GoogleSearchable());


        SparkManager.getChatManager().addChatRoomListener(new ChatRoomListenerAdapter() {
            public void chatRoomOpened(final ChatRoom room) {
                ClassLoader cl = getClass().getClassLoader();
                URL url = cl.getResource("images/google.gif");
                ImageIcon icon = new ImageIcon(url);
                final RolloverButton searchButton = new RolloverButton(icon);

                JPanel buttonPanel = room.getEditorBar();
                buttonPanel.add(searchButton);

                searchButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        String text = room.getChatInputEditor().getSelectedText();
                        if(text == null){
                            text = room.getTranscriptWindow().getSelectedText();
                        }
                        if (ModelUtil.hasLength(text)) {
                            GoogleSearch search = new GoogleSearch();
                            List list = search.searchText(text, 4);
                            if(list.size() == 0){
                                return;
                            }

                            CollapsiblePane pane = new CollapsiblePane("Search Results");
                            JPanel panel = new JPanel();
                            pane.setContentPane(panel);

                            panel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP, 0, 0, true, false));


                            for (Object aList : list) {
                                GoogleSearchResult result = (GoogleSearchResult) aList;
                                GoogleDocument document = new GoogleDocument(room, result);
                                panel.add(document);
                            }

                            room.getTranscriptWindow().addComponent(pane);
                            try {
                                room.getTranscriptWindow().insertText("\n");
                            }
                            catch (BadLocationException e) {
                                Log.error(e);
                            }

                        }
                    }
                });


                room.getTranscriptWindow().addContextMenuListener(new ContextMenuListener() {
                    public void poppingUp(Object object, JPopupMenu popup) {
                        final Action searchConversationAction = new AbstractAction() {
                            public void actionPerformed(ActionEvent actionEvent) {
                                searchConversation(room);
                            }
                        };

                        final Action searchAction = new AbstractAction() {
                            public void actionPerformed(ActionEvent actionEvent) {
                                searchText(room.getTranscriptWindow().getSelectedText());
                            }
                        };

                        searchAction.putValue(Action.NAME, "Google Selected Text");

                        searchConversationAction.putValue(Action.NAME, "Google Conversation");

                        if (room.getTranscriptWindow().getSelectedText() != null) {
                            popup.add(searchAction);
                        }
                        else {
                            popup.add(searchConversationAction);
                        }
                    }

                    public void poppingDown(JPopupMenu popup) {

                    }

                    public boolean handleDefaultAction(MouseEvent e) {
                        return false;
                    }
                });


            }

            public void chatRoomClosed(ChatRoom room) {
            }
        });
    }

    private void findReferences(ContactItem item) {
        GoogleSearch search = new GoogleSearch();

        VCardManager vcardManager = SparkManager.getVCardManager();

        VCard vcard = vcardManager.getVCard(item.getJID());
        String emailHome = vcard.getEmailHome();
        String emailWork = vcard.getEmailWork();

        StringBuffer buf = new StringBuffer();
        if (emailHome != null) {
            buf.append(emailHome);
            buf.append(" ");
        }


        if (emailWork != null) {
            buf.append(emailWork);
        }

        if (buf.toString().trim().length() == 0) {
            JOptionPane.showMessageDialog(SparkManager.getMainWindow(), "No profile for " + item.getJID(), "No User Profile", JOptionPane.ERROR_MESSAGE);
            return;
        }


        search.search(buf.toString(), false);
    }

    private void searchConversation(ChatRoom room) {
        GoogleSearch search = new GoogleSearch();
        List transcripts = room.getTranscripts();
        Iterator iter = transcripts.iterator();

        StringBuffer buf = new StringBuffer();
        while (iter.hasNext()) {
            Message message = (Message)iter.next();
            buf.append(message.getBody());
        }

        search.search(buf.toString(), true);
    }

    private void searchText(String text) {
        GoogleSearch search = new GoogleSearch();
        search.search(text, true);
    }

    public void shutdown() {

    }

    public boolean canShutDown() {
        return false;
    }

    public void uninstall() {

    }


}
