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
package org.jivesoftware.sparkplugin.ui.call;

import net.java.sipmack.sip.Call;
import net.java.sipmack.sip.InterlocutorUI;
import net.java.sipmack.sip.event.CallListener;
import net.java.sipmack.sip.event.CallStateEvent;
import net.java.sipmack.softphone.SoftPhoneManager;
import net.java.sipmack.softphone.listeners.InterlocutorListener;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.tabbedPane.SparkTab;
import org.jivesoftware.spark.component.tabbedPane.SparkTabbedPaneListener;
import org.jivesoftware.spark.phone.PhoneManager;
import org.jivesoftware.spark.ui.ChatContainer;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.alerts.SparkToaster;

import javax.swing.JOptionPane;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import org.jivesoftware.spark.plugin.phone.resource.PhoneRes;

/**
 *
 */
public class CallManager implements InterlocutorListener {

    private static CallManager singleton;
    private static final Object LOCK = new Object();

    private SoftPhoneManager softPhone;

    private final Map<String, PhonePanel> calls = new HashMap<String, PhonePanel>();

    private Presence offPhonePresence;

    private Map<InterlocutorUI, SparkToaster> toasters = new HashMap<InterlocutorUI, SparkToaster>();

    /**
     * Returns the singleton instance of <CODE>CallManager</CODE>,
     * creating it if necessary.
     * <p/>
     *
     * @return the singleton instance of <Code>CallManager</CODE>
     */
    public static CallManager getInstance() {
        // Synchronize on LOCK to ensure that we don't end up creating
        // two singletons.
        synchronized (LOCK) {
            if (null == singleton) {
                CallManager controller = new CallManager();
                singleton = controller;
                return controller;
            }
        }
        return singleton;
    }


    private CallManager() {
        softPhone = SoftPhoneManager.getInstance();
        softPhone.addInterlocutorListener(this);

        SparkManager.getChatManager().getChatContainer().addSparkTabbedPaneListener(new SparkTabbedPaneListener() {
            public void tabRemoved(SparkTab tab, Component component, int index) {
                removePhonePanel(component);
            }

            public void tabAdded(SparkTab tab, Component component, int index) {

            }

            public void tabSelected(SparkTab tab, Component component, int index) {
            }

            public void allTabsRemoved() {
            }


            public boolean canTabClose(SparkTab tab, Component component) {
                PhonePanel phonePanel = null;
                if (component instanceof PhonePanel) {
                    phonePanel = (PhonePanel) component;
                }

                if (phonePanel == null && component instanceof ChatRoom) {
                    final ChatRoom chatRoom = (ChatRoom) component;
                    Component rightComponent = chatRoom.getSplitPane().getRightComponent();
                    if (rightComponent instanceof PhonePanel) {
                        phonePanel = (PhonePanel) rightComponent;
                    }
                }

                if (phonePanel == null) {
                    return true;
                }

                if (component instanceof PhonePanel) {
                    final InterlocutorUI ic = phonePanel.getActiveCall();
                    if (ic.getCallState().equals(Call.CONNECTED)) {
                        // Prompt to close this window
                        int ok = JOptionPane.showConfirmDialog(component, PhoneRes.getIString("phone.closeconfirm"), PhoneRes.getIString("phone.confirmation"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                        boolean close = (ok == JOptionPane.YES_OPTION);
                        if (close) {
                            // End Call
                            softPhone.getDefaultGuiManager().hangup(ic);
                            return true;
                        } else {
                            return false;
                        }
                    }
                }

                return true;
            }
        });
    }


    public void busy() {

    }

    private void showIncomingCall(final InterlocutorUI ic) {
   	 
   	 try {
	   	 EventQueue.invokeAndWait(new Runnable(){
	   		 public void run()
	   		 {
	   			 final SparkToaster toasterManager = new SparkToaster();
	   			 toasters.put(ic, toasterManager);
	   			 final IncomingCallUI incomingCall = new IncomingCallUI(ic);
	   			 toasterManager.setToasterHeight(230);
	   			 toasterManager.setToasterWidth(300);
	   			 toasterManager.setDisplayTime(500000000);
	   			 
	   			 toasterManager.showToaster(PhoneRes.getIString("phone.incomingcall"), incomingCall);
	   			 toasterManager.hideTitle();
	   			 
	   			 toasterManager.setHidable(false);
	   			 incomingCall.getAcceptButton().addActionListener(new ActionListener() {
	   				 public void actionPerformed(ActionEvent e) {
	   					 SparkManager.getMainWindow().toFront();
	   					 closeToaster(ic);
	   					 SoftPhoneManager.getInstance().getDefaultGuiManager().answer();
	   					 
	   				 }
	   			 });
	   			 incomingCall.getRejectButton().addActionListener(new ActionListener() {
	   				 public void actionPerformed(ActionEvent e) {
	   					 closeToaster(ic);
	   					 SoftPhoneManager.getInstance().getDefaultGuiManager().hangup(ic);
	   				 }
	   			 });
	   		 }
	   	 });
   	 }
   	 catch(Exception e) {
   		 Log.error(e);
   	 }


    }

    private void showOutgoingCall(final InterlocutorUI ic) {
        final SparkToaster toasterManager = new SparkToaster();
        toasters.put(ic, toasterManager);
        final OutgoingCallUI incomingCall = new OutgoingCallUI(ic);
        toasterManager.setToasterHeight(230);
        toasterManager.setToasterWidth(300);
        toasterManager.setDisplayTime(500000000);
        toasterManager.setHidable(false);

        toasterManager.showToaster(PhoneRes.getIString("phone.incomingcall"), incomingCall);
        toasterManager.hideTitle();

        incomingCall.getRejectButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                closeToaster(ic);
                SoftPhoneManager.getInstance().getDefaultGuiManager().hangup(ic);
            }
        });
    }


    public void interlocutorAdded(final InterlocutorUI interlocutorUI) {

        Presence current = SparkManager.getWorkspace().getStatusBar().getPresence();

        // Set offline presence if necessary.
        if (offPhonePresence == null || !(current.getType().equals(Presence.Type.available) && current.getStatus().equals("On Phone") && current.getMode().equals(Presence.Mode.away))) {
            offPhonePresence = current;
        }

        // Send on phone presence
        Presence onPhonePresence = new Presence(Presence.Type.available, "On Phone", -1, Presence.Mode.away);
        SparkManager.getSessionManager().changePresence(onPhonePresence);

        interlocutorUI.getCall().addStateChangeListener(new CallListener() {
            public void callStateChanged(CallStateEvent evt) {
                final String callState = evt.getNewState();

                String callNumber = interlocutorUI.getCall().getNumber();
                callNumber = SoftPhoneManager.getNumbersFromPhone(callNumber);
                PhonePanel panel = calls.get(callNumber);

                if (callState.equals(Call.CONNECTED)) {
                    closeToaster(interlocutorUI);
                    showCallAnswered(interlocutorUI);
                } else if (callState.equals(Call.DISCONNECTED)) {
                    closeToaster(interlocutorUI);

                    if (panel != null) {
                        panel.callEnded();
                    }

                    PhoneManager.getInstance().removeCurrentCall(callNumber);
                } else if (callState.equals(Call.RINGING)) {

                } else if (callState.equals(Call.BUSY)) {
                    closeToaster(interlocutorUI);
                } else if (callState.equals(Call.ALERTING)) {
                    showIncomingCall(interlocutorUI);
                    PhoneManager.getInstance().addCurrentCall(callNumber);
                }
            }
        });

        final String callState = interlocutorUI.getCallState();
        if (callState != null && (callState.equals(Call.DIALING)) || callState.equals(Call.RINGING)) {
            showOutgoingCall(interlocutorUI);
        }
    }

    private void showCallAnswered(final InterlocutorUI interlocutorUI) {
        final ChatManager chatManager = SparkManager.getChatManager();
        try {
        
	        EventQueue.invokeAndWait(new Runnable(){
	      	  public void run()
	      	  {
			        String phoneNumber = interlocutorUI.getCall().getNumber();
			        phoneNumber = SoftPhoneManager.getNumbersFromPhone(phoneNumber);
			
			        PhonePanel panel = calls.get(phoneNumber);
			        ChatRoom chatRoom = null;
			        if (panel == null) {
			
			            // Let's check to see if the Contact Exists
			            final VCard vcard = SparkManager.getVCardManager().searchPhoneNumber(phoneNumber);
			            if (vcard != null) {
			                panel = new RosterMemberPanel();
			                panel.setInterlocutorUI(interlocutorUI);
			
			                String jid = vcard.getJabberId();
			                String nickname = SparkManager.getUserManager().getUserNicknameFromJID(jid);
			                chatRoom = chatManager.createChatRoom(jid, nickname, nickname);
			                chatRoom.getSplitPane().setRightComponent(panel);
			                chatRoom.getSplitPane().setResizeWeight(.80);
			                chatRoom.getSplitPane().setDividerSize(5);
			
			                chatManager.getChatContainer().activateChatRoom(chatRoom);
			            } else {
			                panel = new NonRosterPanel();
			                panel.setInterlocutorUI(interlocutorUI);
			                chatManager.getChatContainer().addContainerComponent(panel);
			                chatManager.getChatContainer().activateComponent(panel);
			            }
			
			            calls.put(phoneNumber, panel);
			        } else {
			            panel.setInterlocutorUI(interlocutorUI);
			        }
			
			        if (chatRoom != null) {
			            SoftPhoneManager.getInstance().addCallSession(chatRoom, SoftPhoneManager.CallRoomState.inCall);
			            SparkManager.getChatManager().notifySparkTabHandlers(chatRoom);
			        } else {
			            SoftPhoneManager.getInstance().addCallSession(panel, SoftPhoneManager.CallRoomState.inCall);
			            SparkManager.getChatManager().notifySparkTabHandlers(panel);
			        }
	      	  }
	        });
        }
        catch(Exception e){
      	  Log.error(e);
        }
    }

    public void interlocutorRemoved(final InterlocutorUI interlocutorUI) {
        if (softPhone.getInterlocutors().size() == 0) {

            if (offPhonePresence == null) {

                // Set user to available when all phone calls are hung up.
                Presence availablePresence = new Presence(Presence.Type.available, "Available", 1, Presence.Mode.available);
                SparkManager.getSessionManager().changePresence(availablePresence);

            } else {

                // Set user presence to last one choosed
                SparkManager.getSessionManager().changePresence(offPhonePresence);

            }

            offPhonePresence = null;

        }

        final SwingWorker delay = new SwingWorker() {
            public Object construct() {
                try {
                    Thread.sleep(2000);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return true;
            }

            public void finished() {
                String phoneNumber = interlocutorUI.getCall().getNumber();
                phoneNumber = SoftPhoneManager.getNumbersFromPhone(phoneNumber);

                final PhonePanel phonePanel = calls.get(phoneNumber);
                if (phonePanel == null) {
                    return;
                }

                for (ChatRoom room : SparkManager.getChatManager().getChatContainer().getChatRooms()) {
                    Component comp = room.getSplitPane().getRightComponent();
                    if (comp != null && comp instanceof PhonePanel) {
                        final PhonePanel cp = (PhonePanel) comp;
                        if (cp == phonePanel) {
                            room.getSplitPane().setRightComponent(null);
                            removePhonePanel(cp);

                            // Remove state mapping
                            softPhone.removeCallSession(room);

                            SparkManager.getChatManager().notifySparkTabHandlers(room);
                        }
                        return;
                    }
                }
            }
        };

        delay.start();
    }

    /**
     * Removes PhonePanel from ChatContainer or associated ChatRoom.
     *
     * @param component the PhonePanel or ChatRoom containing the PhonePanel.
     */
    private void removePhonePanel(Component component) {
        String phoneNumber = null;
        if (component instanceof PhonePanel) {
            final PhonePanel phonePanel = (PhonePanel) component;
            phoneNumber = phonePanel.getPhoneNumber();
            phoneNumber = SoftPhoneManager.getNumbersFromPhone(phoneNumber);
        } else if (component instanceof ChatRoom) {
            ChatRoom chatRoom = (ChatRoom) component;
            Component comp = chatRoom.getSplitPane().getRightComponent();
            if (comp != null && comp instanceof PhonePanel) {
                final PhonePanel phonePanel = (PhonePanel) comp;
                phoneNumber = phonePanel.getPhoneNumber();
                phoneNumber = SoftPhoneManager.getNumbersFromPhone(phoneNumber);
            }
        }

        if (phoneNumber != null) {
            calls.remove(phoneNumber);
        }
    }

    /**
     * Retrieves the <code>PhonePanel</code> object from the <code>ChatRoom</code>.
     * If no PhonePanel exists, then null is returned.
     *
     * @param chatRoom the ChatRoom.
     * @return the PhonePanel.
     */
    public PhonePanel getPhonePanel(ChatRoom chatRoom) {
        Component comp = chatRoom.getSplitPane().getRightComponent();
        if (comp != null && comp instanceof PhonePanel) {
            final PhonePanel phonePanel = (PhonePanel) comp;
            return phonePanel;
        }

        return null;
    }

    /**
     * Returns the associated Chat Room if one exists.
     *
     * @param phonePanel the <code>PhonePanel</code>.
     * @return the ChatRoom.
     */
    public ChatRoom getAssociatedChatRoom(PhonePanel phonePanel) {
        for (ChatRoom chatRoom : SparkManager.getChatManager().getChatContainer().getChatRooms()) {
            Component comp = chatRoom.getSplitPane().getRightComponent();
            if (phonePanel.equals(comp)) {
                return chatRoom;
            }
        }

        return null;
    }

    /**
     * Returns the <code>SparkTab</code> that acts as a container to this PhonePanel.
     *
     * @param phonePanel the <code>PhonePanel</code>
     * @return the SparkTab.
     */
    public SparkTab getSparkTab(PhonePanel phonePanel) {
        final ChatContainer chatContainer = SparkManager.getChatManager().getChatContainer();
        int tabCount = chatContainer.getTabCount();
        for (int i = 0; i < tabCount; i++) {
            SparkTab sparkTab = chatContainer.getTabAt(i);
            Component containerComponent = chatContainer.getComponentInTab(sparkTab);
            if (containerComponent.equals(phonePanel)) {
                return sparkTab;
            }

            if (containerComponent instanceof ChatRoom) {
                ChatRoom chatRoom = (ChatRoom) containerComponent;
                Component rightComponent = chatRoom.getSplitPane().getRightComponent();
                if (phonePanel.equals(rightComponent)) {
                    return sparkTab;
                }
            }
        }


        return null;
    }

    private void closeToaster(InterlocutorUI ic) {
        SparkToaster toaster = toasters.get(ic);
        if (toaster != null) {
            toaster.close();
            toasters.remove(ic);
        }
    }

}
