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
package net.java.sipmack.softphone;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.sdp.MediaDescription;
import javax.sdp.SessionDescription;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;

import net.java.sipmack.common.DialSoundManager;
import net.java.sipmack.common.Log;
import net.java.sipmack.events.UserActionListener;
import net.java.sipmack.media.AudioMediaSession;
import net.java.sipmack.media.AudioReceiverChannel;
import net.java.sipmack.media.JmfMediaManager;
import net.java.sipmack.media.MediaException;
import net.java.sipmack.media.VideoMediaSession;
import net.java.sipmack.sip.Call;
import net.java.sipmack.sip.CommunicationsException;
import net.java.sipmack.sip.Interlocutor;
import net.java.sipmack.sip.InterlocutorUI;
import net.java.sipmack.sip.NetworkAddressManager;
import net.java.sipmack.sip.SIPConfig;
import net.java.sipmack.sip.SipManager;
import net.java.sipmack.sip.SipRegisterStatus;
import net.java.sipmack.sip.event.CallEvent;
import net.java.sipmack.sip.event.CallListener;
import net.java.sipmack.sip.event.CallRejectedEvent;
import net.java.sipmack.sip.event.CallStateEvent;
import net.java.sipmack.sip.event.CommunicationsErrorEvent;
import net.java.sipmack.sip.event.CommunicationsListener;
import net.java.sipmack.sip.event.MessageEvent;
import net.java.sipmack.sip.event.RegistrationEvent;
import net.java.sipmack.sip.event.UnknownMessageEvent;
import net.java.sipmack.softphone.gui.DefaultGuiManager;
import net.java.sipmack.softphone.gui.GuiManager;
import net.java.sipmack.softphone.listeners.InterlocutorListener;
import net.java.sipmack.softphone.listeners.RegisterEvent;
import net.java.sipmack.softphone.listeners.SoftPhoneListener;

import org.jivesoftware.resource.Res;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.phone.PhoneManager;
import org.jivesoftware.spark.plugin.phone.resource.PhoneRes;
import org.jivesoftware.spark.preference.PreferenceManager;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;
import org.jivesoftware.sparkplugin.calllog.LogManager;
import org.jivesoftware.sparkplugin.calllog.LogManagerImpl;
import org.jivesoftware.sparkplugin.calllog.LogPacket;
import org.jivesoftware.sparkplugin.preferences.SipPreference;
import org.jivesoftware.sparkplugin.preferences.SipPreferences;
import org.jivesoftware.sparkplugin.sipaccount.SipAccount;
import org.jivesoftware.sparkplugin.sipaccount.SipAccountPacket;
import org.jivesoftware.sparkplugin.ui.call.MissedCalls;

/**
 * Title: SIPark
 * Description:JAIN-SIP Audio/Video phone application
 *
 * @author Thiago Rocha Camargo (thiago@jivesoftware.com) The
 *         <code>SoftPhoneManager</code> class that Manage SIP Stack, Sessions
 *         and Calls
 *         Use getInstance() to get a SoftPhone instance to use
 * @version 1.0, 20/07/2006
 */

public class SoftPhoneManager implements CommunicationsListener, CallListener, UserActionListener, SoftPhone {

	
    private static SoftPhoneManager singleton;
    private static final Object LOCK = new Object();
    private SipAccount saccount;

    private final List<SoftPhoneListener> softPhoneListeners = new CopyOnWriteArrayList<SoftPhoneListener>();

    // TODO REMOVE
    @SuppressWarnings("unused")
    private SoftPhoneMedia softPhoneMedia = null;

    private SoftPhoneSecurity softPhoneSecurity = null;

    private SipManager sipManager = null;

    private JmfMediaManager mediaManager = null;

    private GuiManager guiManager = null;

    private String authUserName = "";

    private String username = "";

    private String password = null;// new char[10];

    public String server = "";

    protected String msgBuffer = "";

    protected Integer unregistrationLock = Integer.valueOf(0);

    private SipRegisterStatus status = SipRegisterStatus.Unregistered;

    public static final String userAgent = "SIPSpark";

    public String callTo = "";

    private LogManager logManager;

    private SipPreferences preferences;

    private SipPreference preference;

//    private int lines = 1;

    private DialSoundManager dtmfSounds;

    private MissedCalls missedCalls;

    // Define UIs
    private JCheckBoxMenuItem registerMenu;

    private Map<Component, CallRoomState> callRooms = new HashMap<Component, CallRoomState>();

    /**
     * Private constructor of the class.
     */
    private SoftPhoneManager() {
    	mediaManager = new JmfMediaManager();
    }

    /**
     * Initializes the core phone objects.
     */
    private void initializePhone() {
       // Load Preferences
       loadPreferences();

       if (preferences == null) {
           return;
       }


       guiManager = new GuiManager();
       guiManager.addUserActionListener(this);
       logManager = new LogManagerImpl(this);

       this.getLogManager().setRemoteLogging(true);

    	 try {
				EventQueue.invokeAndWait(new Runnable() {
		 				@Override
		 				public void run() {
		 					registerMenu = new JCheckBoxMenuItem(PhoneRes.getIString("phone.enabled"));
		 				}
		 			});
	    }
	    catch(Exception e){
	   	 Log.error(e);
	    }
        
        registerMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                if (getStatus() == SipRegisterStatus.Unregistered ||
                        getStatus() == SipRegisterStatus.RegistrationFailed) {

                    register();
                } else {
                    handleUnregisterRequest();
                }
            }
        });


        SIPConfig.setPreferredNetworkAddress(preferences.getPreferredAddress());
        NetworkAddressManager.start();

     	 try {
				EventQueue.invokeAndWait(new Runnable() {
		 				@Override
		 				public void run() {
		 					 // Initialize Missed calls
		 					 missedCalls = new MissedCalls();
		 				}
		 			});
	    }
	    catch(Exception e){
	   	 Log.error(e);
	    }
       

        final JMenu actionsMenu = SparkManager.getMainWindow().getMenuByName(Res.getString("menuitem.actions"));
        actionsMenu.add(registerMenu);
    }


    /**
     * Type of states a jingle call can be in.
     */
    public static enum CallRoomState {
        /**
         * The component is in a call.
         */
        inCall,

        /**
         * The components call has ended.
         */
        callWasEnded,

        /**
         * The call is muted.
         */
        muted,

        /**
         * The call is on hold.
         */
        onHold
    }

    /**
     * Returns the singleton instance of <CODE>SoftPhoneManager</CODE>,
     * creating it if necessary.
     * <p/>
     *
     * @return the singleton instance of <Code>SoftPhoneManager</CODE>
     */
    public static SoftPhoneManager getInstance() {
        // Synchronize on LOCK to ensure that we don't end up creating
        // two singletons.
        synchronized (LOCK) {
            if (null == singleton) {
                
	 				 SoftPhoneManager controller = new SoftPhoneManager();
	             singleton = controller;
	 				 controller.initializePhone();
                
	        	    return controller;
            }
        }
        return singleton;
    }

    /**
     * Add SoftPhoneListener
     *
     * @param softPhoneListener the listener.
     */
    public void addSoftPhoneListener(SoftPhoneListener softPhoneListener) {
        softPhoneListeners.add(softPhoneListener);
    }

    /**
     * Add InterlocutorListener
     *
     * @param interlocutorListener the listener.
     */
    public void addInterlocutorListener(InterlocutorListener interlocutorListener) {
        guiManager.addInterlocutorListener(interlocutorListener);
    }

    /**
     * Remove InterlocutorListener
     *
     * @param interlocutorListener the listener
     */
    public void removeInterlocutorListener(InterlocutorListener interlocutorListener) {
        guiManager.removeInterlocutorListener(interlocutorListener);
    }

    /**
     * Return the DefaultGuiManager
     */
    public DefaultGuiManager getDefaultGuiManager() {
        return guiManager;
    }

    /**
     * Create the softphone handlers and stack
     */
    public void createSoftPhone(String server) throws MediaException {

        this.server = server;
        SIPConfig.setServer(server);

        if (sipManager != null) {
            destroySoftPhone();
        }

        sipManager = new SipManager();
        softPhoneMedia = new SoftPhoneMedia();
        softPhoneSecurity = new SoftPhoneSecurity();

        sipManager.addCommunicationsListener(this);
        sipManager.setSecurityAuthority(softPhoneSecurity);

        try {
            // put in a seperate thread
            sipManager.start();
            if (sipManager.isStarted()) {
                Log.debug("createSoftPhone", "SIP STARTED");
            }
        }
        catch (CommunicationsException exc) {
            Log.error("createSoftPhone", exc);
        }

    }

    /**
     * Destroys the softphone handlers and stack
     */
    public void destroySoftPhone() {

        try {
            sipManager.stop();
        }
        catch (Exception exc) {
            Log.error("destroySoftPhone", exc);
        }
    }

    /**
     * Return the current SIP connection status
     *
     * @return status
     */
    public SipRegisterStatus getStatus() {
        return status;
    }

    /**
     * Gets the current username
     *
     * @return The current connection username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the current server
     *
     * @return The current connection server
     */
    public String getServer() {
        return server;
    }

    /**
     * Handle an UnregisterRequest
     */
    public void handleUnregisterRequest() {
        if (sipManager != null) {
            try {
                sipManager.endAllCalls();
                sipManager.unregister();
            }
            catch (Exception e) {
                Log.error("handleUnregisterRequest", e);
            }
        }
    }

    /**
     * Handle a Re-Register Request.
     * This method will only have effect if the user has successfully registered beforeat least once before.
     */
    public void handleReRegisterRequest() {
        if (this.password != null && !this.username.equals("")) {
            try {
                sipManager.startRegisterProcess(username, authUserName, password);
            }
            catch (CommunicationsException exc) {
                Log.error("handleRegisterRequest", exc);
            }
        }
    }

    /**
     * Handle an RegisterRequest
     *
     * @param username username
     * @param password password
     */
    public void handleRegisterRequest(String username, String password) {
        handleRegisterRequest(username, null, password);
    }

    /**
     * Handle an RegisterRequest
     *
     * @param username     username
     * @param authUserName Authorization username
     * @param password     password
     */
    public void handleRegisterRequest(String username, String authUserName, String password) {

        this.authUserName = authUserName;
        this.username = username;
        this.password = password;

        try {
            sipManager.startRegisterProcess(username, authUserName, password);
        }
        catch (CommunicationsException exc) {
            Log.error("handleRegisterRequest", exc);
        }
    }

    /**
     * Add a CommunicationsListener
     *
     * @param communicationsListener CommunicationsListener
     */
    public void addCommunicationsListener(CommunicationsListener communicationsListener) {
        sipManager.addCommunicationsListener(communicationsListener);
    }

    /**
     * Fired when a call ends
     *
     * @param evt CallEvent
     */
    public void callEnded(CallEvent evt) {
        AudioMediaSession audioMediaSession = evt.getSourceCall().getAudioMediaSession();
        if (audioMediaSession != null) {
            audioMediaSession.close();
        }
    }

    /**
     * Fired when a call is received
     *
     * @param evt CallEvent
     */
    public void callReceived(CallEvent evt) {
        try {
            Call call = evt.getSourceCall();
            Interlocutor interlocutor = new Interlocutor();
            interlocutor.setCall(call);
            guiManager.addInterlocutor(interlocutor);
            call.addStateChangeListener(this);
            // handleAnswerRequest(interlocutor);
        }
        catch (Exception e) {
            Log.error("callReceived", e);
        }
    }

    /**
     * Fired when a message is received
     *
     * @param evt MessageEvent
     */
    public void messageReceived(MessageEvent evt) {
        for (SoftPhoneListener sfl : softPhoneListeners) {
            sfl.messageReceived(evt);
        }
    }

    /**
     * Fired when a call is rejected locally
     *
     * @param evt CallRejectedEvent
     */
    public void callRejectedLocally(CallRejectedEvent evt) {
//        String reason = evt.getReason();
//        String detailedReason = evt.getDetailedReason();
    }

    /**
     * Fired when a call is rejected remotly
     *
     * @param evt CallRejectedEvent
     */
    public void callRejectedRemotely(CallRejectedEvent evt) {
        for (SoftPhoneListener softPhoneListener : softPhoneListeners) {
            softPhoneListener.callRejectedRemotely(evt);
        }
    }

    /**
     * Fired when softphone is register sucessfully
     *
     * @param evt RegistrationEvent
     */
    public void registered(RegistrationEvent evt) {
        status = SipRegisterStatus.Registered;

        preferences.setPreferredAddress(NetworkAddressManager.getLocalHost().getHostAddress());
        //preference.commit();

        registerStatusChanged(new RegisterEvent(this, SipRegisterStatus.Registered, evt.getReason()));

        if (callTo.length() > 2) {
            handleDialRequest(callTo);
        }
        registerMenu.setSelected(true);
    }

    public void registrationFailed(RegistrationEvent evt) {
        status = SipRegisterStatus.RegistrationFailed;
        registerStatusChanged(new RegisterEvent(this, SipRegisterStatus.RegistrationFailed, evt.getReason()));
        registerMenu.setSelected(false);
    }

    /**
     * Fired when softphone is registering
     *
     * @param evt RegistrationEvent
     */
    public void registering(RegistrationEvent evt) {
        status = SipRegisterStatus.Registering;
        registerStatusChanged(new RegisterEvent(this, SipRegisterStatus.Registering, evt.getReason()));
    }

    /**
     * Fired when softphone is unregistered sucessfully
     *
     * @param evt RegistrationEvent
     */
    public void unregistered(RegistrationEvent evt) {
        try {
            status = SipRegisterStatus.Unregistered;

            registerStatusChanged(new RegisterEvent(this, SipRegisterStatus.Unregistered, evt.getReason()));

            // we could now exit
            synchronized (unregistrationLock) {
                unregistrationLock.notifyAll();
            }

            destroySoftPhone();
            registerMenu.setSelected(false);
        }
        catch (Exception e) {
            Log.error("unregistered", e);
        }
    }

    /**
     * Fired when softphone is unregistering
     *
     * @param evt RegistrationEvent
     */
    public void unregistering(RegistrationEvent evt) {
        status = SipRegisterStatus.Unregistered;

        registerStatusChanged(new RegisterEvent(this, SipRegisterStatus.Unregistering, evt.getReason()));

        int waitUnreg = SIPConfig.getWaitUnregistration();
        if (waitUnreg != -1) {
            try {
                int delay = waitUnreg;
                // we get here through a _synchronous_ call from shutdown so
                // let's try
                // and wait for unregistrations confirmation in case the
                // registrar has requested authorization
                // before conriming unregistration
                if (delay > 0)
                    synchronized (unregistrationLock) {
                        unregistrationLock.wait(delay);
                    }
            }
            catch (InterruptedException ex) {
                Log.error("unregistering", ex);
            }
            catch (NumberFormatException ex) {
                Log.error("unregistering", ex);
            }

        }
    }

    private void registerStatusChanged(RegisterEvent evt) {
        for (SoftPhoneListener sfl : softPhoneListeners) {
            sfl.registerStatusChanged(evt);
        }
    }

    /**
     * Returns the current interlocutors
     *
     * @return List<InterlocutorUI>
     */
    public List<InterlocutorUI> getInterlocutors() {
        return guiManager.getInterlocutors();
    }

    /**
     * Fired when softphone receive a unknown sip message
     *
     * @param evt RegistrationEvent
     */
    public void receivedUnknownMessage(UnknownMessageEvent evt) {
        // TODO Do something with the error.
    }

    /**
     * Fired when communications Errors Occurred
     *
     * @param evt CommunicationsErrorEvent
     */
    public void communicationsErrorOccurred(CommunicationsErrorEvent evt) {

    }

    /**
     * Fired when call state changes
     *
     * @param evt CallStateEvent
     */
    public void callStateChanged(CallStateEvent evt) {
        try {

            for (SoftPhoneListener sfl : softPhoneListeners) {
                sfl.callStateChanged(evt);
            }

            Call call = evt.getSourceCall();
            Log.debug("callStateChanged", evt.getOldState() + " -> "
                    + evt.getNewState());
            if (evt.getNewState() == Call.CONNECTED) {
                //sipManager.setBusy(true);

                if (call.getAudioReceiverChannel() != null)
                    call.getAudioReceiverChannel().stop();

                if (evt.getOldState() == Call.MOVING_REMOTELY) {
                    AudioMediaSession audioMediaSession = evt.getSourceCall().getAudioMediaSession();
                    if (call.getAudioReceiverChannel() != null)
                        call.getAudioReceiverChannel().stop();

                    if (audioMediaSession != null) {
                        audioMediaSession.stopTrasmit();
                        audioMediaSession.stopReceive();
                    }
                    PhoneManager.setUsingMediaLocator(false);
                }

                int localAudioPort = -1;
                int localVideoPort = -1;
                
                Vector<MediaDescription> mediaDescriptions = call.getLocalSdpDescription().getMediaDescriptions(true);
                for (MediaDescription mediaDescription : mediaDescriptions)
                {
                	if (mediaDescription.getMedia().getMediaType().equals("audio"))
                		localAudioPort = mediaDescription.getMedia().getMediaPort();
                	else if (mediaDescription.getMedia().getMediaType().equals("video"))
                		localVideoPort = mediaDescription.getMedia().getMediaPort();
                }
                
                AudioMediaSession audioMediaSession = mediaManager.createAudioMediaSession(call.getRemoteSdpDescription().toString(), localAudioPort);                
                call.setAudioMediaSession(audioMediaSession);

                if (audioMediaSession != null) {
                    audioMediaSession.startTrasmit();
                    audioMediaSession.startReceive();
                }
                
                // If remote client have video
                if (localVideoPort > 0)
                {
                	if (SettingsManager.getLocalPreferences().getVideoDevice() != null && !"".equals(SettingsManager.getLocalPreferences().getVideoDevice()))
                	{
		                VideoMediaSession videoMediaSession = mediaManager.createVideoMediaSession(call.getRemoteSdpDescription().toString(), localVideoPort);
		                if (videoMediaSession != null) {
		                	videoMediaSession.startTrasmit();
		                	videoMediaSession.startReceive();
		                }
                	}
                }

                
                evt.getSourceCall().start();
                
                Log.debug("MEDIA STREAMS OPENED");

            } else if (evt.getNewState() == Call.RINGING) {

                if (call.getRemoteSdpDescription() != null
                        && !call.getRemoteSdpDescription().equals("")) {

                    Log.debug("STATE", call.getRemoteSdpDescription().toString());

                    int localPort = ((MediaDescription) (call.getLocalSdpDescription().getMediaDescriptions(true).get(0))).getMedia().getMediaPort();
                    int destPort = ((MediaDescription) (call.getRemoteSdpDescription().getMediaDescriptions(true).get(0))).getMedia().getMediaPort();
                    String destIp = call.getRemoteSdpDescription().getConnection().getAddress();

                    AudioReceiverChannel audioReceiverChannel = mediaManager.createAudioReceiverChannel(localPort, destIp, destPort);
                    call.setAudioReceiverChannel(audioReceiverChannel);

                    if (audioReceiverChannel != null)
                        audioReceiverChannel.start();

                }

            } else if (evt.getNewState() == Call.DISCONNECTED) {
                sipManager.setBusy(false);

                AudioMediaSession audioMediaSession = evt.getSourceCall().getAudioMediaSession();
                if (audioMediaSession != null) {
                    audioMediaSession.stopTrasmit();
                    audioMediaSession.stopReceive();
                }
                if (call.getAudioReceiverChannel() != null)
                    call.getAudioReceiverChannel().stop();

                PhoneManager.setUsingMediaLocator(false);

            } else if (evt.getNewState() == Call.FAILED) {
                call.setState(Call.DISCONNECTED);
                if (call.getAudioReceiverChannel() != null)
                    call.getAudioReceiverChannel().stop();

                CallRejectedEvent rejectEvt = new CallRejectedEvent("Disconnected", call.getLastRequest(), call);

                for (SoftPhoneListener softPhoneListener : softPhoneListeners) {
                    softPhoneListener.callRejectedRemotely(rejectEvt);
                }

                PhoneManager.setUsingMediaLocator(false);

            }
        }
        catch (Exception e) {
            Log.error("callStateChanged", e);
        }

    }

    /**
     * Handle a exit request
     */
    public void handleExitRequest() {
        if (mediaManager != null) {
        }
        // SIP unregister
        if (sipManager != null) {
            try {
                sipManager.endAllCalls();
            }
            catch (CommunicationsException exc) {
                Log.error("handleExitRequest", exc);
            }
            catch (Throwable exc) {
                Log.error("handleExitRequest", exc);
            }
            try {
                sipManager.unregister();
            }
            catch (CommunicationsException exc) {
                Log.error("handleExitRequest", exc);
            }
            catch (Throwable exc) {
                Log.error("handleExitRequest", exc);
            }
            try {
                sipManager.stop();
            }
            catch (Exception exc) {
                Log.error("handleExitRequest", exc);
            }
        }
        NetworkAddressManager.shutDown();
    }

    /**
     * Handle a Hold request
     *
     * @param iui the InterlocutorUI
     * @param mic true to place on hold.
     * @param cam true to place camera on hold.
     */
    public void handleHold(InterlocutorUI iui, boolean mic, boolean cam) {

        try {
            sipManager.hold(iui.getID(), mediaManager.generateHoldSdpDescription(mic, mic, iui.getCall()), mic, cam);
        }
        catch (Exception e) {
            Log.error("handleHold", e);
        }

    }

    public void handleTransfer(int callID, String callee) {

        sipManager.transfer(callID, callee);

    }

    /**
     * Handle a Mute request
     *
     * @param iui the InterlocutorUI
     * @param mic true to place on mute.
     */
    public void handleMute(InterlocutorUI iui, boolean mic) {
        try {
            AudioMediaSession audioMediaSession = iui.getCall().getAudioMediaSession();
            if (audioMediaSession != null) audioMediaSession.setTrasmit(mic);
        }
        catch (Exception e) {
            Log.error("handleHold", e);
        }
    }

    /**
     * Handle when users press a dtmf button
     *
     * @param iui the InterlocutorUI
     */
    public void handleDTMF(InterlocutorUI iui, String digit) {
        try {
            sendDTMFDigit(iui.getID(), digit);
        }
        catch (Exception e) {
            Log.error("sendDTMFDigit", e);
        }
    }

    /**
     * Send the dtmf digit to the sip server.
     *
     * @param callID the caller id
     * @param digit  the digit typed.
     */
    void sendDTMFDigit(int callID, String digit) {
        try {
            sipManager.sendDTMF(callID, digit);
        }
        catch (CommunicationsException exc) {
            Log.error("sendDTMFDigit", exc);
        }
    }

    /**
     * Handle a answer request
     */
    public boolean handleAnswerRequest(Interlocutor interlocutor) {

        // cancel call request if no Media Locator
        if (PhoneManager.isUseStaticLocator() && PhoneManager.isUsingMediaLocator()) {
            return false;
        }

        PhoneManager.setUsingMediaLocator(true);

        SessionDescription sdpData = null;
        try {
            sdpData = mediaManager.generateSdpDescription();
            interlocutor.getCall().setLocalSdpDescription(sdpData);
        }
        catch (MediaException ex) {
            try {
                sipManager.sendServerInternalError(interlocutor.getID());
            }
            catch (CommunicationsException ex1) {
                Log.error("handleAnswerRequest", ex1);
            }
            return false;
        }
        try {
            sipManager.answerCall(interlocutor.getID(), sdpData.toString());
        }
        catch (CommunicationsException exc) {
            Log.error("handleAnswerRequest", exc);
            return false;
        }
        return true;
    }

    /**
     * Handle a dial request
     */
    public void handleDialRequest(String phoneNumber) {
        try {

            System.err.println("Audio Static:" + PhoneManager.isUseStaticLocator() + " Using:" + PhoneManager.isUsingMediaLocator());

            // cancel call request if no Media Locator
            if (PhoneManager.isUseStaticLocator() && PhoneManager.isUsingMediaLocator()) {
                return;
            }

            PhoneManager.setUsingMediaLocator(true);

            SessionDescription sdpData = mediaManager.generateSdpDescription();

            Call call = sipManager.establishCall(phoneNumber, sdpData.toString());

            if (call == null) return;

            call.setLocalSdpDescription(sdpData);

            call.addStateChangeListener(this);
            Interlocutor interlocutor = new Interlocutor();
            interlocutor.setCall(call);

            guiManager.addInterlocutor(interlocutor);
        }
        catch (Exception e) {
            Log.error("handleDialRequest", e);
        }
    }

    /**
     * Handle a hangup request
     */
    public boolean handleHangupRequest(Interlocutor interlocutor) {
        boolean hangupOk = true;
        try {
            sipManager.endCall(interlocutor.getID());
        }
        catch (CommunicationsException exc) {
            guiManager.remove(interlocutor);
            hangupOk = false;
            Log.error("handleHangupRequest", exc);
        }
        return hangupOk;
    }

    /**
     * Register the softPhone with the Spark preferrence settings
     */
    public void register() {
        Thread registerThread = new Thread(new Runnable() {
            public void run() {
                if (preferences != null) {
                    String user = preferences.getUserName();
                    String authUser = preferences.getAuthUserName();
                    String server = preferences.getServer();
                    String pass = preferences.getPassword();

                    try {
                        createSoftPhone(server);
                    }
                    catch (MediaException e) {
                        Log.error("Media Exception", e);

                    }

                    handleRegisterRequest(user, authUser, pass);
                }
            }
        });

        registerThread.start();
    }

    /**
     * Loads Preferences, either from server or locally.
     */
    private void loadPreferences() {
        boolean serverPluginInstalled = SipAccountPacket.isSoftPhonePluginInstalled(SparkManager.getConnection());
        if (serverPluginInstalled) {
            setupRemotePreferences(SparkManager.getConnection());
        }
    }

    /**
     * Returns the LogManager associated with this SoftPhone.
     *
     * @return the LogManager.
     */
    public LogManager getLogManager() {
        return logManager;
    }
    
    /**
     * Set the LogManager associated with this SoftPhone.
     */
    public void setLogManager(LogManager logmanager) {
   	  // removes the existing listener 
   	  softPhoneListeners.remove(this.logManager);
        logManager = logmanager;
    }

    /**
     * Setup the Preferences according to remote server
     *
     * @param con XMPPConnection connection
     */
    private void setupRemotePreferences(XMPPConnection con) {

        try {
            ProviderManager.getInstance().addIQProvider(SipAccountPacket.ELEMENT_NAME, SipAccountPacket.NAMESPACE, new SipAccountPacket.Provider());

            ProviderManager.getInstance().addIQProvider(LogPacket.ELEMENT_NAME, LogPacket.NAMESPACE, new LogPacket.Provider());

            SipAccountPacket sp = SipAccountPacket.getSipSettings(con);

            if (sp != null) {

                SipAccount sipAccount = sp.getSipAccount();

                if (sipAccount != null) {
                    this.saccount = sipAccount;
                    preference = new SipPreference();
                    preference.setShowGUI(false);
                    preference.setCommitSettings(false);

                    preferences = new SipPreferences();

                    preferences.setUserName(sipAccount.getSipUsername());
                    preferences.setAuthUserName(sipAccount.getAuthUsername());
                    preferences.setServer(sipAccount.getServer());
                    preferences.setPassword(sipAccount.getPassword());
                    preferences.setRegisterAtStart(true);

                    preferences.setStunServer(sipAccount.getStunServer());
                    preferences.setStunPort(sipAccount.getStunPort());
                    preferences.setUseStun(sipAccount.isUseStun());
                    preferences.setVoicemail(sipAccount.getVoiceMailNumber());
                    preferences.setOutboundproxy(sipAccount.getOutboundproxy());
                    preferences.setPromptCredentials(sipAccount.isPromptCredentials());

                    SIPConfig.setUseStun(preferences.isUseStun());
                    SIPConfig.setStunServer(preferences.getStunServer());
                    SIPConfig.setStunPort(preferences.getStunPort());
                    SIPConfig.setOutboundProxy(preferences.getOutboundproxy());

                    preference.setData(preferences);
                    SIPConfig.setPreferredNetworkAddress(NetworkAddressManager.getLocalHost(false).getHostAddress());

                    if (preferences.isRegisterAtStart()) {
                        register();
                    }

                    if (preferences.isPromptCredentials()) {
                        loadLocalPreferences();
                    }

                } else {

                }
            }

        }
        catch (Exception e) {
            Log.error("setupRemotePreferences", e);
        }

    }

    private void loadLocalPreferences() {
        preference = new SipPreference();

        PreferenceManager pm = SparkManager.getPreferenceManager();
        pm.addPreference(preference);

        preferences = (SipPreferences) preference.getData();

        SIPConfig.setUseStun(preferences.isUseStun());
        SIPConfig.setStunServer(preferences.getStunServer());
        SIPConfig.setStunPort(preferences.getStunPort());
        SIPConfig.setPreferredNetworkAddress(preferences.getPreferredAddress());

        preference.setCommitSettings(true);

        if (preferences.isRegisterAtStart()) {
            register();
        }
    }


    /**
     * Calls an individual user by their VCard information.
     *
     * @param jid the users JID.
     */
    public void callByJID(String jid) {
        if (getStatus() == SipRegisterStatus.Registered) {
            final VCard vcard = SparkManager.getVCardManager().getVCard(StringUtils.parseBareAddress(jid));

            if (vcard != null) {
                String number = vcard.getPhoneWork("VOICE");
                if (!ModelUtil.hasLength(number)) {
                    number = vcard.getPhoneHome("VOICE");
                }

                if (ModelUtil.hasLength(number)) {
                    getDefaultGuiManager().dial(number);
                }
            }
        }
    }

    /**
     * Returns the SoundManager for Dial Tones.
     *
     * @return the SoundManager.
     */
    public DialSoundManager getDTMFSounds() {
        if (dtmfSounds == null) {
            dtmfSounds = new DialSoundManager();
        }
        return dtmfSounds;
    }


    public SipPreference getPreference() {
        return preference;
    }

    public MissedCalls getMissedCalls() {
        return missedCalls;
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

    public SipAccount getSipAccount() {
        return saccount;
    }

    /**
     * Adds a new CallRoomState.
     *
     * @param component the component where the call is taking place.
     * @param state     the state of the call.
     */
    public void addCallSession(Component component, CallRoomState state) {
        callRooms.put(component, state);
    }

    /**
     * Removes a CallRoomState.
     *
     * @param component the component where the call is taking place.
     */
    public void removeCallSession(Component component) {
        callRooms.remove(component);
    }

    /**
     * Returns the state of a <code>Component</code>. If no call is taking place,
     * this method will return null.
     *
     * @param component the <code>Component</code>.
     * @return the CallRoomState.
     */
    public CallRoomState getCallRoomState(Component component) {
        return callRooms.get(component);
    }

    /**
     * Returns true if the user is connected.
     *
     * @return true if the user is connected.
     */
    public boolean isPhoneEnabled() {
        return getPreference() != null;
    }
    
    public JmfMediaManager getJmfMediaManager() {
   	 return mediaManager;
    }
}
