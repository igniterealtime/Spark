/**
 * $Revision$
 * $Date$
 *
 * Copyright (C) 1999-2005 Jive Software. All rights reserved.
 * This software is the proprietary information of Jive Software. Use is subject to license terms.
 */

package org.jivesoftware.spark.phone;

import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.plugin.ContextMenuListener;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatRoomButton;
import org.jivesoftware.spark.ui.ChatRoomListener;
import org.jivesoftware.spark.ui.ContactItem;
import org.jivesoftware.spark.ui.ContactList;
import org.jivesoftware.spark.ui.rooms.ChatRoomImpl;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.sparkimpl.plugin.phone.JMFInit;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;

/**
 * Handles general phone behavior in Spark. This allows for many different phone systems
 * to plug into Spark in a more elegant way.
 */
public class PhoneManager implements ChatRoomListener, ContextMenuListener {
    private static PhoneManager singleton;
    private static final Object LOCK = new Object();

    private List<Phone> phones = new CopyOnWriteArrayList<Phone>();

    /**
     * Returns the singleton instance of <CODE>PhoneManager</CODE>,
     * creating it if necessary.
     * <p/>
     *
     * @return the singleton instance of <Code>PhoneManager</CODE>
     */
    public static PhoneManager getInstance() {
        // Synchronize on LOCK to ensure that we don't end up creating
        // two singletons.
        synchronized (LOCK) {
            if (null == singleton) {
                PhoneManager controller = new PhoneManager();
                singleton = controller;
                return controller;
            }
        }
        return singleton;
    }

    private PhoneManager() {
        final SwingWorker worker = new SwingWorker() {
            public Object construct() {
                // Initialize JMF
                JMFInit.start(false);
                return true;
            }
        };

        worker.start();
    }

    private void addListeners() {
        // Handle ChatRooms.
        final ChatManager chatManager = SparkManager.getChatManager();
        chatManager.addChatRoomListener(this);

        // Handle ContextMenus.
        final ContactList contactList = SparkManager.getWorkspace().getContactList();
        contactList.addContextMenuListener(this);
    }

    public void addPhone(Phone phone) {
        if (phones.isEmpty()) {
            addListeners();
        }

        phones.add(phone);
    }

    public void removePhone(Phone phone) {
        phones.remove(phone);
    }


    public void chatRoomOpened(final ChatRoom room) {
        if (!phones.isEmpty() && room instanceof ChatRoomImpl) {
            final ChatRoomImpl chatRoomImpl = (ChatRoomImpl)room;
            final ChatRoomButton dialButton = new ChatRoomButton(SparkRes.getImageIcon(SparkRes.DIAL_PHONE_IMAGE_24x24));
            dialButton.setToolTipText("Place a phone call to this user.");

            final List<Action> actions = new ArrayList<Action>();
            SwingWorker actionWorker = new SwingWorker() {
                public Object construct() {
                    for (Phone phone : phones) {
                        final Collection<Action> phoneActions = phone.getPhoneActions(chatRoomImpl.getParticipantJID());
                        if (phoneActions != null) {
                            for (Action action : phoneActions) {
                                actions.add(action);
                            }
                        }
                    }
                    return actions;
                }

                public void finished() {
                    if (!actions.isEmpty()) {
                        room.getToolBar().addChatRoomButton(dialButton);
                        room.getToolBar().invalidate();
                        room.getToolBar().validate();
                        room.getToolBar().repaint();
                    }
                }
            };

            actionWorker.start();


            dialButton.addMouseListener(new MouseAdapter() {
                public void mousePressed(final MouseEvent e) {
                    SwingWorker worker = new SwingWorker() {
                        public Object construct() {
                            try {
                                Thread.sleep(50);
                            }
                            catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                            return true;
                        }

                        public void finished() {
                            // Handle actions.
                            if (actions.size() == 1) {
                                final Action action = actions.get(0);
                                action.actionPerformed(null);
                            }
                            else if (actions.size() > 1) {
                                // Display PopupMenu
                                final JPopupMenu menu = new JPopupMenu();
                                for (Action action : actions) {
                                    menu.add(action);
                                }

                                menu.show(dialButton, e.getX(), e.getY());
                            }
                        }
                    };
                    worker.start();


                }
            });
        }
    }

    public void chatRoomLeft(ChatRoom room) {
    }

    public void chatRoomClosed(ChatRoom room) {
    }

    public void chatRoomActivated(ChatRoom room) {
    }

    public void userHasJoined(ChatRoom room, String userid) {
    }

    public void userHasLeft(ChatRoom room, String userid) {
    }


    public void poppingUp(Object object, JPopupMenu popup) {
        if (!phones.isEmpty()) {
            if (object instanceof ContactItem) {
                ContactItem contactItem = (ContactItem)object;
                final List<Action> actions = new ArrayList<Action>();
                for (Phone phone : phones) {
                    final Collection<Action> itemActions = phone.getPhoneActions(contactItem.getJID());
                    for (Action action : itemActions) {
                        actions.add(action);
                    }
                }

                if (actions.size() == 1) {
                    Action action = actions.get(0);
                    action.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.DIAL_PHONE_IMAGE_16x16));
                    action.putValue(Action.NAME, "Dial");
                    popup.insert(action, 2);
                }
                else if (actions.size() > 1) {
                    final JMenu dialMenu = new JMenu("Dial");
                    dialMenu.setIcon(SparkRes.getImageIcon(SparkRes.DIAL_PHONE_IMAGE_16x16));

                    for (Action action : actions) {
                        dialMenu.add(action);
                    }

                    int count = popup.getComponentCount();
                    if (count > 2) {
                        popup.insert(dialMenu, 2);
                    }

                }
            }
        }
    }

    public void poppingDown(JPopupMenu popup) {
    }

    public boolean handleDefaultAction(MouseEvent e) {
        return false;
    }


}