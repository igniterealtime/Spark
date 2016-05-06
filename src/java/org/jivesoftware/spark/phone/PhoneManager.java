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
package org.jivesoftware.spark.phone;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.media.CaptureDeviceInfo;
import javax.media.CaptureDeviceManager;
import javax.media.MediaLocator;
import javax.media.format.AudioFormat;
import javax.media.format.VideoFormat;
import javax.media.protocol.DataSource;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;

import org.jivesoftware.Spark;
import org.jivesoftware.resource.Res;
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

/**
 * Handles general phone behavior in Spark. This allows for many different phone systems
 * to plug into Spark in a more elegant way.
 */
public class PhoneManager implements ChatRoomListener, ContextMenuListener {
    private static PhoneManager singleton;
    private static final Object LOCK = new Object();

    private List<Phone> phones = new CopyOnWriteArrayList<>();
    private List<String> currentCalls = new ArrayList<>();
    // Static Media Locator
    static private MediaLocator mediaLocator = null;
    // Static Media Locator Preference
    static private boolean useStaticLocator = false;
    // Static using MediaLocator
    static private boolean usingMediaLocator = false;
    // Static DataSource
    static private DataSource ds;

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
        JMFInit.start(false);
        if (Spark.isVista() || Spark.isLinux()) {
            setUseStaticLocator(true);
        }
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
            final ChatRoomImpl chatRoomImpl = (ChatRoomImpl) room;
            final ChatRoomButton dialButton = new ChatRoomButton(SparkRes.getImageIcon(SparkRes.DIAL_PHONE_IMAGE_24x24));
            dialButton.setToolTipText(Res.getString("tooltip.place.voice.call"));

            final List<Action> actions = new ArrayList<>();
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
                        room.addChatRoomButton(dialButton, true);
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
                            if (actions.size() > 0) {
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

    public void poppingUp(Object object, final JPopupMenu popup) {
        if (!phones.isEmpty()) {
            if (object instanceof ContactItem) {
                final ContactItem contactItem = (ContactItem) object;
                final List<Action> actions = new ArrayList<>();

                SwingWorker worker = new SwingWorker() {
                    public Object construct() {
                        for (Phone phone : phones) {
                            final Collection<Action> itemActions = phone.getPhoneActions(contactItem.getJID());
                            for (Action action : itemActions) {
                                actions.add(action);
                            }
                        }
                        return null;
                    }

                    public void finished() {

                        if (actions.size() > 0) {
                            final JMenu dialMenu = new JMenu(Res.getString("title.dial.phone"));
                            dialMenu.setIcon(SparkRes.getImageIcon(SparkRes.DIAL_PHONE_IMAGE_16x16));

                            for (Action action : actions) {
                                dialMenu.add(action);
                            }

                            int count = popup.getComponentCount();
                            if (count > 2) {
                                popup.insert(dialMenu, 2);
                            }

                            popup.invalidate();
                            popup.validate();
                            popup.repaint();
                        }
                    }
                };

                worker.start();


            }
        }
    }

    public void poppingDown(JPopupMenu popup) {
    }

    public boolean handleDefaultAction(MouseEvent e) {
        return false;
    }

    public void addCurrentCall(String phoneNumber) {
        currentCalls.add(phoneNumber);
    }

    public void removeCurrentCall(String phoneNumber) {
        currentCalls.remove(phoneNumber);
    }

    public boolean containsCurrentCall(String phoneNumber) {
        return currentCalls.contains(phoneNumber);
    }

    public static String getNumbersFromPhone(String number) {
        if (number == null) {
            return null;
        }

        number = number.replace("-", "");
        number = number.replace("(", "");
        number = number.replace(")", "");
        number = number.replace(" ", "");
        if (number.startsWith("1")) {
            number = number.substring(1);
        }

        return number;
    }

    public static MediaLocator getMediaLocator(String locator) {
        MediaLocator auxLocator;

        System.out.println("--------------------------------");
        System.out.println("locator: " + locator);
        
    	Vector<CaptureDeviceInfo> vectorAudioDevices =  CaptureDeviceManager.getDeviceList(new AudioFormat(AudioFormat.LINEAR));
		for ( CaptureDeviceInfo infoCaptureDevice : vectorAudioDevices)
		{			     
			System.out.println(infoCaptureDevice.getLocator() + "-" + locator);
			if (infoCaptureDevice.getLocator().toString().equals(locator))
			{
				System.out.println("Found: " + locator);
				return infoCaptureDevice.getLocator();
			}
		}
		
		Vector<CaptureDeviceInfo> vectorVideoDevices = CaptureDeviceManager.getDeviceList(new VideoFormat(VideoFormat.RGB));
		for (  CaptureDeviceInfo infoCaptureDevice : vectorVideoDevices )
		{
			System.out.println(infoCaptureDevice.getLocator() + "-" + locator);
			if (infoCaptureDevice.getLocator().toString().equals(locator))
			{
				System.out.println("Found: " + locator);
				return infoCaptureDevice.getLocator();
			}
		}
		
        
        if (useStaticLocator) {
            if (mediaLocator == null) {
                mediaLocator = new MediaLocator(locator);
            }
            auxLocator = mediaLocator;
            //usingMediaLocator=true;
        } else {
            auxLocator = new MediaLocator(locator);
        }

        return auxLocator;
    }

    public static DataSource getDataSource(String locator) {

        if (ds == null) {

            try {
                ds = javax.media.Manager.createDataSource(getMediaLocator(locator));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }

        return ds;
    }

    public static DataSource getDataSource(MediaLocator locator) {

        if (ds == null) {

            try {
                ds = javax.media.Manager.createDataSource(locator);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }

        return ds;
    }

    public static boolean isUsingMediaLocator() {
        return usingMediaLocator;
    }

    public static void setUsingMediaLocator(boolean usingMediaLocator) {
        PhoneManager.usingMediaLocator = usingMediaLocator;
    }

    public static boolean isUseStaticLocator() {
        return useStaticLocator;
    }

    public static void setUseStaticLocator(boolean useStaticLocator) {
        PhoneManager.useStaticLocator = useStaticLocator;
    }
}