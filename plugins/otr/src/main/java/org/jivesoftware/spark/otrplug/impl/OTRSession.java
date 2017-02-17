package org.jivesoftware.spark.otrplug.impl;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.PublicKey;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import net.java.otr4j.*;
import net.java.otr4j.session.Session;
import net.java.otr4j.session.SessionImpl;
import org.jivesoftware.smack.packet.Message;
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
 * OTRSession are unique for every conversation. It handles the otrEngine for
 * the chat and controls if the chat is encrypted or not.
 * 
 * @author Bergunde Holger
 */
public class OTRSession {

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
     * @param chatroom
     *            chat room related to this OTR session
     * @param myJID
     *            my own JID
     * @param remoteJID
     *            the JID of the participant
     */
    public OTRSession(ChatRoomImpl chatroom, String myJID, String remoteJID) {
        _chatRoom = chatroom;
        _myJID = myJID;
        _remoteJID = remoteJID;
        _otrEngineHost = new OTREngineHost(new OtrPolicyImpl(OtrPolicy.ALLOW_V2 | OtrPolicy.ERROR_START_AKE), _chatRoom);
        _mySessionID = new SessionID( _myJID, _remoteJID, "Scytale");

        _mySession = new SessionImpl( _mySessionID, _otrEngineHost );

        setUpMessageListener();

        createButton();

        // Only initialize the actionListener once
        _otrButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (_mySession.getSessionStatus().equals(SessionStatus.ENCRYPTED)) {
                    try
                    {
                        stopSession();
                    }
                    catch ( OtrException ex )
                    {
                        Log.error( "An exception occurred while tyring to stop an OTR session.", ex );
                    }
                } else {
                    try
                    {
                        startSession();
                    }
                    catch ( OtrException ex )
                    {
                        Log.error( "An exception occurred while tyring to start an OTR session.", ex );
                    }
                }

            }
        });

        _otrButton.setToolTipText(OTRResources.getString("otr.chat.button.tooltip"));

        _OtrEnabled = OTRProperties.getInstance().getIsOTREnabled();
    }

    /**
     * Maybe you want to update the chat room because it was reopend but the OTR
     * session is still alive.
     * 
     * @param chatroom
     *            the chat room related to this OTR session
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
            public void sendingMessage(Message message) {
                String oldmsg = message.getBody();
                if (_mySession.equals( SessionStatus.ENCRYPTED)) {
                    message.setBody(null);
                    String[] mesg = null;
                    try
                    {
                        mesg = _mySession.transformSending(oldmsg);
                        message.setBody( String.join( "", mesg ) );
                    }
                    catch ( OtrException e )
                    {
                        Log.error( "An exception occurred while trying to send a message: " + oldmsg, e );
                    }
                }
            }

            @Override
            public void receivingMessage(Message message) {
                if (message.getBody() != null && _OtrEnabled) {
                    String old = message.getBody();
                    message.setBody(null);
                    String mesg = null;
                    if (old.length() > 2) {
                        try
                        {
                            mesg = _mySession.transformReceiving(old);
                        }
                        catch ( OtrException e )
                        {
                            Log.error( "An exception occurred while receiving a message: " + old, e );
                        }
                    }
                    if (_mySession.equals( SessionStatus.ENCRYPTED)) {
                        message.setBody(mesg);
                    } else {
                        if (old.length() > 3 && old.substring(0, 4).equals("?OTR")) {
                            old = null;
                        }
                        message.setBody(old);
                    }
                } else if (!_OtrEnabled) {
                    String old = message.getBody();
                    message.setBody(null);
                    if (old.length() > 3 && old.substring(0, 4).equals("?OTR")) {
                        _chatRoom.getTranscriptWindow().insertNotificationMessage(OTRResources.getString("otr.not.enabled"), Color.gray);
                    } else {
                        message.setBody(old);
                    }
                }

            }
        };
        _chatRoom.addMessageEventListener(_msgEvnt);
    }

    private void createButton() {

        if (OTRProperties.getInstance().getIsOTREnabled()) {
            final ClassLoader cl = getClass().getClassLoader();

            ImageIcon otricon = null;
            if (_mySession.getSessionStatus().equals( SessionStatus.ENCRYPTED)) {
                otricon = new ImageIcon(cl.getResource("otr_on.png"));
                _conPanel.successfullyCon();
            } else {
                otricon = new ImageIcon(cl.getResource("otr_off.png"));
            }

            _otrButton.setIcon(otricon);

            _mySession.removeOtrEngineListener(_otrListener);
            _chatRoom.getToolBar().addChatRoomButton(_otrButton);
            _otrListener = new OtrEngineListener() {

                @Override
                public void sessionStatusChanged(SessionID arg0) {
                	UIManager.put("OptionPane.yesButtonText", Res.getString("yes"));
                	UIManager.put("OptionPane.noButtonText", Res.getString("no"));
                	UIManager.put("OptionPane.cancelButtonText", Res.getString("cancel"));
                	
                    if (_mySession.getSessionStatus().equals( SessionStatus.ENCRYPTED)) {
                        _conPanel.successfullyCon();

                        String otrkey = _manager.getKeyManager().getRemoteFingerprint( _mySessionID );
                        if (otrkey == null) {
                            PublicKey pubkey = _mySession.getRemotePublicKey( _mySessionID );
                            _manager.getKeyManager().savePublicKey( _mySessionID, pubkey);
                            otrkey = _manager.getKeyManager().getRemoteFingerprint( _mySessionID );
                        }

                        if (!OTRManager.getInstance().getKeyManager().isVerified( _mySessionID )) {
                            final int n = JOptionPane.showConfirmDialog(_otrButton,
                                    OTRResources.getString("otr.start.session.with", _remoteJID) + "\n" + OTRResources.getString("otr.key.not.verified.text") + "\n" + otrkey
                                            + "\n" + OTRResources.getString("otr.question.verify"), OTRResources.getString("otr.key.not.verified.title"),
                                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                            if (n == JOptionPane.YES_OPTION) {
                                _manager.getKeyManager().verify( _mySessionID );
                            }

                        }
                        _otrButton.setIcon(new ImageIcon(cl.getResource("otr_on.png")));
                    } else if (_mySession.getSessionStatus().equals( SessionStatus.FINISHED) || _mySession.getSessionStatus().equals( SessionStatus.PLAINTEXT ) {
                        try
                        {
                            stopSession();
                            _otrButton.setIcon(new ImageIcon(cl.getResource("otr_off.png")));
                        }
                        catch ( OtrException e )
                        {
                            Log.error( "An exception occurred while stopping the OTR session.", e );
                        }
                    }

                }
            };
            _mySession.addOtrEngineListener(_otrListener);
        }
    }

    /**
     * Start the OTR session manually from outside
     */
    public void startSession() throws OtrException
    {
        _conPanel.tryToStart();
        _mySession.startSession();
    }

    /**
     * Stop the OTR session manually from outside
     */
    public void stopSession() throws OtrException
    {
        _conPanel.connectionClosed();
        if (_mySession.getSessionStatus().equals( SessionStatus.ENCRYPTED)) {
            final ClassLoader cl = getClass().getClassLoader();
            _otrButton.setIcon(new ImageIcon(cl.getResource("otr_off.png")));
            _mySession.endSession();
        }
    }

}
