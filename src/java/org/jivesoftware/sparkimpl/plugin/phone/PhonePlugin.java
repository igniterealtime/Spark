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
package org.jivesoftware.sparkimpl.plugin.phone;

import org.jivesoftware.phone.client.*;
import org.jivesoftware.phone.client.action.PhoneActionIQProvider;
import org.jivesoftware.phone.client.event.PhoneEventPacketExtensionProvider;
import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.phone.PhoneManager;
import org.jivesoftware.spark.plugin.ContextMenuListener;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.ui.*;
import org.jivesoftware.spark.ui.rooms.ChatRoomImpl;
import org.jivesoftware.spark.util.*;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.alerts.SparkToaster;
import org.jivesoftware.sparkimpl.plugin.idle.UserIdlePlugin;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;
import org.jxmpp.util.XmppStringUtils;

import javax.swing.*;
import java.awt.event.*;
import java.util.TimerTask;

public class PhonePlugin implements Plugin {
    public static PhoneClient phoneClient;
    private DialPanel dialPanel;
    //    private Alert incomingDialog;
    private JFrame dialDialog;

    public static Presence offPhonePresence;
    public static Presence onPhonePresence;
    public void initialize() {
        ProviderManager.addExtensionProvider("phone-event", "http://jivesoftware.com/xmlns/phone", new PhoneEventPacketExtensionProvider());
        ProviderManager.addIQProvider("phone-action", "http://jivesoftware.com/xmlns/phone", new PhoneActionIQProvider());

        final XMPPConnection con = SparkManager.getConnection();


        SwingWorker worker = new SwingWorker() {
            public Object construct() {
                try {
                    phoneClient = new PhoneClient(con);

                    // Add BaseListener
                    phoneClient.addEventListener(new PhoneListener());
                }
                catch (Exception e) {
                    // Ignore because the user does not have support.
                    //Log.debug(e);
                }
                return phoneClient;
            }

            public void finished() {
                if (phoneClient != null) {
                    setupPhoneSystem();
                }
            }
        };

        worker.start();
    }

    private void setupPhoneSystem() {
        // Add Dial Menu
        final JMenu viewMenu = SparkManager.getMainWindow().getMenuByName(Res.getString("menuitem.actions"));
        JMenuItem dialNumberMenu = new JMenuItem(SparkRes.getImageIcon(SparkRes.ON_PHONE_IMAGE));
        ResourceUtils.resButton(dialNumberMenu, Res.getString("button.dial.number"));

        // Add Listener
        dialNumberMenu.addActionListener( e -> {
            dialPanel = new DialPanel();
            dialPanel.getDialButton().addActionListener( e1 -> {
                String number = dialPanel.getNumberToDial();
                if (ModelUtil.hasLength(number)) {
                    dialPanel.setText(Res.getString("message.calling", number));
                    dialPanel.changeToRinging();
                    callExtension(number);

                }

            } );

            dialDialog = PhoneDialog.invoke(dialPanel, Res.getString("title.dial.phone"), Res.getString("message.number.to.call"), null);
            dialPanel.getDialField().requestFocusInWindow();

            dialPanel.getDialField().addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                        try {
                            String number = dialPanel.getNumberToDial();
                            if (ModelUtil.hasLength(number)) {
                                dialPanel.setText(Res.getString("message.calling", number));
                                dialPanel.changeToRinging();
                                callExtension(number);

                            }
                            e.consume();
                        }
                        catch (Exception ex) {
                            Log.error(ex);
                        }
                    }
                }
            });
        } );
        viewMenu.add(dialNumberMenu);

        // Add ChatRoomListener to call users based on JID
        SparkManager.getChatManager().addChatRoomListener(new ChatRoomListenerAdapter() {
            public void chatRoomOpened(final ChatRoom room) {
                if (room instanceof ChatRoomImpl) {
                    final ChatRoomButton callButton = new ChatRoomButton("", SparkRes.getImageIcon(SparkRes.TELEPHONE_24x24));
                    callButton.setToolTipText(Res.getString("tooltip.place.a.call"));
                    final ChatRoomImpl chatRoom = (ChatRoomImpl)room;
                    boolean phoneEnabled = false;
                    try {
                        phoneEnabled = phoneClient.isPhoneEnabled( XmppStringUtils.parseBareJid(chatRoom.getParticipantJID()));
                    }
                    catch (Exception e) {
                        Log.error(e);
                    }

                    if (phoneEnabled) {
                        room.addChatRoomButton(callButton);
                        callButton.addActionListener( e -> callJID(chatRoom.getParticipantJID()) );
                    }
                }
            }
        });

        ContactList contactList = SparkManager.getWorkspace().getContactList();
        contactList.addContextMenuListener(new ContextMenuListener() {
            public void poppingUp(Object object, final JPopupMenu popup) {
                if (object instanceof ContactItem) {
                    final ContactItem item = (ContactItem)object;

                    boolean phoneEnabled = false;


                    try {
                        phoneEnabled = phoneClient.isPhoneEnabled(item.getJID());
                    }
                    catch (Exception e) {
                        Log.error("There was an error retrieving phone information.", e);
                    }

                    if (phoneEnabled) {
                        Action callAction = new AbstractAction() {
							private static final long serialVersionUID = 7221741748743018431L;

							public void actionPerformed(ActionEvent e) {
                                callJID(item.getJID());
                            }
                        };

                        callAction.putValue(Action.NAME, "Call");
                        callAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.ON_PHONE_IMAGE));
                        popup.add(callAction);
                    }
                }
            }

            public void poppingDown(JPopupMenu popup) {

            }

            public boolean handleDefaultAction(MouseEvent e) {
                return false;
            }
        });
    }

    private class PhoneListener extends BasePhoneEventListener {

        public void handleOnPhone(OnPhoneEvent event) {
            if (dialDialog != null) {
                dialDialog.setVisible(false);
            }

            // Get current presence if necessary.
            offPhonePresence = SparkManager.getWorkspace().getStatusBar().getPresence();


            // Send "on the phone" presence
            onPhonePresence = new Presence(Presence.Type.available, "On the phone", 1, Presence.Mode.away);
            SparkManager.getSessionManager().changePresence(onPhonePresence);
        }


        public void handleHangUp(HangUpEvent event) {
            onPhonePresence = null;
            if (dialDialog != null) {
                dialDialog.setVisible(false);
            }

            if (offPhonePresence != null) {

                if (((offPhonePresence.getMode().equals(Presence.Mode.away)) && (!UserIdlePlugin.getDesktopLockStatus())
                        && (UserIdlePlugin.latestPresence != null)) && (!UserIdlePlugin.latestPresence.getStatus().contentEquals("On the Phone"))) {
                    SparkManager.getSessionManager().changePresence(UserIdlePlugin.latestPresence);
                    Log.debug("PhonePlugin: Setting presence from UserIdlePlugin");

                } else if ((UserIdlePlugin.getDesktopLockStatus()) && ((offPhonePresence.getStatus().equals("Online"))
                        || (offPhonePresence.getStatus().equals("Free to chat")))) {
                    Presence presence = new Presence(Presence.Type.available, UserIdlePlugin.pref.getIdleMessage(), 1, Presence.Mode.away);
                    SparkManager.getSessionManager().changePresence(presence);
                    Log.debug("PhonePlugin: Desktop is Locked - Setting presence from pref.idle message");

                } else if (UserIdlePlugin.getDesktopLockStatus() && (!offPhonePresence.isAway())) {
                    Presence presence = new Presence(Presence.Type.available, offPhonePresence.getStatus(), 1, Presence.Mode.away);
                    SparkManager.getSessionManager().changePresence(presence);
                    Log.debug("PhonePlugin: Desktop is Locked - Setting presence from user defined presence");

                } else {
                    // Set user to previous presence state when all phone calls are hung up.
                    SparkManager.getSessionManager().changePresence(offPhonePresence);
                    Log.debug("PhonePlugin: Setting Presence from PhonePlugin.");
                }


            } else {
                // If no previous state available, set status to Available
                Presence availablePresence = new Presence(Presence.Type.available, "Online", 1, Presence.Mode.available);

                SparkManager.getSessionManager().changePresence(availablePresence);
                Log.debug("no previous state available from Phone Plugin..setting to Online");

            }
        }

        public void handleRing(final RingEvent event) {
            final TimerTask task = new SwingTimerTask() {
                public void doRun() {
                    String callerID = event.getCallerID();
                    if (ModelUtil.hasLength(callerID)) {
                        String number = PhoneManager.getNumbersFromPhone(callerID);
                        if (PhoneManager.getInstance().containsCurrentCall(number)) {
                            return;
                        }
                    }
                    displayRingUI(event);
                }
            };

            TaskEngine.getInstance().schedule(task, 1000);
        }
    }

    private void displayRingUI(RingEvent event) {
        IncomingCall incomingCall = new IncomingCall();
        boolean idExists = false;
        if (ModelUtil.hasLength(event.getCallerIDName())) {
            incomingCall.setCallerName(event.getCallerIDName());
            idExists = true;
        }

        if (ModelUtil.hasLength(event.getCallerID())) {
            incomingCall.setCallerNumber(event.getCallerID());
            idExists = true;
        }

        if (!idExists) {
            incomingCall.setCallerName(Res.getString("message.no.caller.id"));
        }

        if (!SettingsManager.getLocalPreferences().getDisableAsteriskToasterPopup()) {
            SparkToaster toasterManager = new SparkToaster();
            toasterManager.setTitle("Incoming Phone Call");
            toasterManager.setDisplayTime(15000);
            toasterManager.showToaster(SparkRes.getImageIcon(SparkRes.ON_PHONE_IMAGE));
            toasterManager.setComponent(incomingCall);

        }
    }


    public void callExtension(final String number) {
        final Runnable caller = () -> {
            try {
                phoneClient.dialByExtension(number);
            }
            catch (PhoneActionException e) {
                Log.error(e);
            }
        };

        TaskEngine.getInstance().submit(caller);
    }

    public void callJID(final String jid) {
        final Runnable caller = () -> {
            try {
                phoneClient.dialByJID(jid);
            }
            catch (PhoneActionException e) {
                Log.error(e);
            }
        };

        TaskEngine.getInstance().submit(caller);
    }


    public static PhoneClient getPhoneClient() {
        return phoneClient;
    }

    public void shutdown() {

    }

    public boolean canShutDown() {
        return true;
    }

    public void uninstall() {
        // Do nothing.
    }


}
