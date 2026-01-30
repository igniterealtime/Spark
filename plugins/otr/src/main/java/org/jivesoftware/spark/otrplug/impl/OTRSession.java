package org.jivesoftware.spark.otrplug.impl;

import java.security.PublicKey;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import net.java.otr4j.*;
import net.java.otr4j.io.SerializationConstants;
import net.java.otr4j.session.Session;
import net.java.otr4j.session.SessionImpl;

import org.jivesoftware.smack.packet.MessageBuilder;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.otrplug.OTRManager;
import org.jivesoftware.spark.otrplug.ui.OTRConnectionPanel;
import org.jivesoftware.spark.otrplug.util.OTRProperties;
import org.jivesoftware.spark.otrplug.util.OTRResources;
import org.jivesoftware.spark.ui.ChatRoomButton;
import org.jivesoftware.spark.ui.MessageEventListener;
import org.jivesoftware.resource.Res;

import org.jivesoftware.spark.ui.rooms.ChatRoomImpl;
import org.jivesoftware.spark.util.UIComponentRegistry;

import net.java.otr4j.session.SessionID;
import net.java.otr4j.session.SessionStatus;
import org.jivesoftware.spark.util.log.Log;

/**
 * OTRSession are unique for every conversation.
 * It handles the otrEngine for the chat and controls if the chat is encrypted or not.
 * 
 * @author Bergunde Holger
 */
public class OTRSession {

    private static final OtrPolicyImpl OTR_POLICY = new OtrPolicyImpl(OtrPolicy.ALLOW_V2 | OtrPolicy.ALLOW_V3 | OtrPolicy.ERROR_START_AKE);
    private ChatRoomImpl _chatRoom;
    private String _myJID;
    private String _remoteJID;
    private OtrEngineHost _otrEngineHost;
    private SessionID _mySessionID;
    private Session _mySession;
    private OTRManager _manager = OTRManager.getInstance();
    final ChatRoomButton _otrButton = UIComponentRegistry.getButtonFactory().createOtrButton();
    private OTRConnectionPanel _conPanel;
    private MessageEventListener _msgEvnt;
    private boolean _OtrEnabled = false;
    private OtrEngineListener _otrListener;

    /**
     * OTRSession Constructor
     * 
     * @param chatroom chat room related to this OTR session
     * @param myJID my own JID
     * @param remoteJID the JID of the participant
     */
    public OTRSession(ChatRoomImpl chatroom, String myJID, String remoteJID) {
        _chatRoom = chatroom;
        _myJID = myJID;
        _remoteJID = remoteJID;
        _otrEngineHost = new OTREngineHost(OTR_POLICY, _chatRoom);
        _mySessionID = new SessionID( _myJID, _remoteJID, "Scytale");

        _mySession = new SessionImpl( _mySessionID, _otrEngineHost );

        setUpMessageListener();
        createButton();
        // Only initialize the actionListener once
        _otrButton.addActionListener(e -> {
            if (_mySession.getSessionStatus() == SessionStatus.ENCRYPTED) {
                stopSession();
            } else {
                startSession();
            }
        });
        _otrButton.setToolTipText(OTRResources.getString("otr.chat.button.tooltip"));
        _OtrEnabled = OTRProperties.getInstance().getIsOTREnabled();
    }

    /**
     * Maybe you want to update the chat room because it was reopened but the OTR
     * session is still alive.
     * 
     * @param chatroom the chat room related to this OTR session
     */
    public void updateChatRoom(ChatRoomImpl chatroom) {
        _OtrEnabled = OTRProperties.getInstance().getIsOTREnabled();
        _chatRoom = chatroom;
        setUpMessageListener();
        createButton();
    }

    private void setUpMessageListener() {
        _conPanel = new OTRConnectionPanel(_chatRoom);
        _chatRoom.removeMessageEventListener(_msgEvnt);
        _msgEvnt = new MessageEventListener() {

            @Override
            public void sendingMessage(MessageBuilder message) {
                String msgBody = message.getBody();
                if (msgBody == null) {
                    return;
                }
                if (_mySession.getSessionStatus() == SessionStatus.ENCRYPTED) {
                    try
                    {
                        String[] mesg = _mySession.transformSending(msgBody);
                        if (mesg != null) {
                            message.setBody(String.join("", mesg));
                        } else {
//                        message.setBody(""); //TODO clear message
                        }
                    }
                    catch ( OtrException e )
                    {
//                        message.setBody(""); //TODO clear message
                        Log.error( "An exception occurred while trying to send a message: " + msgBody, e );
                    }
                }
            }

            @Override
            public void receivingMessage(MessageBuilder message) {
                String msgBody = message.getBody();
                if (msgBody == null) {
                    return;
                }
                if (_OtrEnabled) {
                    if (_mySession.getSessionStatus() == SessionStatus.ENCRYPTED) {
                        try
                        {
                            String mesg = _mySession.transformReceiving(msgBody);
                            if (mesg != null) {
                                message.setBody(mesg);
                            }
                        }
                        catch ( OtrException e )
                        {
//                            message.setBody(""); //TODO clear message
                            _chatRoom.getTranscriptWindow().insertNotificationMessage(OTRResources.getString("otr.failed.to.decode"), ChatManager.ERROR_COLOR);
                            Log.error( "An exception occurred while receiving a message: " + msgBody, e );
                        }
                    } else {
                        if (msgBody.startsWith(SerializationConstants.HEAD)) {
                            try {
                                String mesg = _mySession.transformReceiving(msgBody);
                                if (mesg != null) {
                                    message.setBody(mesg);
                                }
                            } catch (OtrException e) {
                                Log.error("An exception occurred while receiving a message: " + msgBody, e);
                            }
                            _chatRoom.getTranscriptWindow().insertNotificationMessage(OTRResources.getString("otr.not.started"), ChatManager.ERROR_COLOR);
//                            message.setBody(""); //TODO clear message
                        }
                    }
                } else {
                    if (msgBody.startsWith(SerializationConstants.HEAD)) {
//                        message.setBody(""); //TODO clear message
                        _chatRoom.getTranscriptWindow().insertNotificationMessage(OTRResources.getString("otr.not.enabled"), ChatManager.NOTIFICATION_COLOR);
                    }
                }
            }
        };
        _chatRoom.addMessageEventListener(_msgEvnt);
    }

    private void createButton() {
        if (!OTRProperties.getInstance().getIsOTREnabled()) {
            return;
        }
        ClassLoader cl = getClass().getClassLoader();

        ImageIcon otrIcon;
        if (_mySession.getSessionStatus() == SessionStatus.ENCRYPTED) {
            otrIcon = new ImageIcon(cl.getResource("otr_on.png"));
            _conPanel.successfullyCon();
        } else {
            otrIcon = new ImageIcon(cl.getResource("otr_off.png"));
        }

        _otrButton.setIcon(otrIcon);

        _mySession.removeOtrEngineListener(_otrListener);
        _chatRoom.getToolBar().addChatRoomButton(_otrButton);
        _otrListener = new OtrEngineListener() {

            @Override
            public void sessionStatusChanged(SessionID sessionID) {
                UIManager.put("OptionPane.yesButtonText", Res.getString("yes"));
                UIManager.put("OptionPane.noButtonText", Res.getString("no"));
                UIManager.put("OptionPane.cancelButtonText", Res.getString("cancel"));

                if (_mySession.getSessionStatus() == SessionStatus.ENCRYPTED) {
                    _conPanel.successfullyCon();
                    String otrkey = _manager.getKeyManager().getRemoteFingerprint( _mySessionID );
                    if (otrkey == null) {
                        PublicKey pubkey = _mySession.getRemotePublicKey( _mySession.getReceiverInstanceTag() );
                        _manager.getKeyManager().savePublicKey( _mySessionID, pubkey);
                        otrkey = _manager.getKeyManager().getRemoteFingerprint( _mySessionID );
                    }

                    if (!OTRManager.getInstance().getKeyManager().isVerified( _mySessionID )) {
                        String dialogTitle = OTRResources.getString("otr.key.not.verified.title");
                        String dialogMessage = OTRResources.getString("otr.start.session.with", _remoteJID) + "\n" +
                            OTRResources.getString("otr.key.not.verified.text") + "\n" +
                            otrkey + "\n" +
                            OTRResources.getString("otr.question.verify");
                        int n = JOptionPane.showConfirmDialog(_otrButton, dialogMessage, dialogTitle,
                                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                        if (n == JOptionPane.YES_OPTION) {
                            _manager.getKeyManager().verify( _mySessionID );
                        }
                    }
                    _otrButton.setIcon(new ImageIcon(cl.getResource("otr_on.png")));
                } else if (_mySession.getSessionStatus() == SessionStatus.FINISHED || _mySession.getSessionStatus() == SessionStatus.PLAINTEXT) {
                        stopSession();
                        _otrButton.setIcon(new ImageIcon(cl.getResource("otr_off.png")));
                }
            }

            @Override
            public void multipleInstancesDetected(SessionID sessionID) {
                Log.warning("multipleInstancesDetected for OTR session " + sessionID);
            }

            @Override
            public void outgoingSessionChanged(SessionID sessionID) {
                Log.warning("outgoingSessionChanged for OTR session " + sessionID);
            }
        };
        _mySession.addOtrEngineListener(_otrListener);
    }

    /**
     * Start the OTR session manually from outside
     */
    public void startSession() {
        _conPanel.tryToStart();
        try {
            _mySession.startSession();
        } catch (OtrException e) {
            Log.error("An exception occurred while starting an OTR session.", e);
        }
    }

    /**
     * Stop the OTR session manually from outside
     */
    public void stopSession()
    {
        _conPanel.connectionClosed();
        if (_mySession.getSessionStatus() == SessionStatus.ENCRYPTED) {
            final ClassLoader cl = getClass().getClassLoader();
            _otrButton.setIcon(new ImageIcon(cl.getResource("otr_off.png")));
            try {
                _mySession.endSession();
            } catch (OtrException e) {
                Log.error( "An exception occurred while stopping the OTR session.", e);
            }
        }
    }

}
