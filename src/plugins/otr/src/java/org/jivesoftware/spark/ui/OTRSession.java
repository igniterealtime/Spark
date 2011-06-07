package org.jivesoftware.spark.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.PublicKey;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.otrplug.OTREngineHost;
import org.jivesoftware.spark.otrplug.OTRManager;
import org.jivesoftware.spark.otrplug.OTRProperties;
import org.jivesoftware.spark.otrplug.OTRResources;
import org.jivesoftware.spark.ui.rooms.ChatRoomImpl;

import net.java.otr4j.OtrEngineHost;
import net.java.otr4j.OtrEngineImpl;
import net.java.otr4j.OtrEngineListener;
import net.java.otr4j.OtrPolicy;
import net.java.otr4j.OtrPolicyImpl;
import net.java.otr4j.session.SessionID;
import net.java.otr4j.session.SessionStatus;

public class OTRSession {

    private ChatRoomImpl _chatRoom;
    private String _myJID;
    private String _remoteJID;
    private OtrEngineHost _otrEngineHost;
    private SessionID _mySession;
    private OtrEngineImpl _engine;
    private OTRManager _manager = OTRManager.getInstance();
    final ChatRoomButton _otrButton = new ChatRoomButton();
    private boolean _OtrEnabled = false;

    public OTRSession(ChatRoomImpl chatroom, String myJID, String remoteJID) {
        _chatRoom = chatroom;
        _myJID = myJID;
        _remoteJID = remoteJID;
        _otrEngineHost = new OTREngineHost(new OtrPolicyImpl(OtrPolicy.ALLOW_V2 | OtrPolicy.ERROR_START_AKE), _chatRoom);
        _mySession = new SessionID(_myJID, _remoteJID, "Scytale");

        _engine = new OtrEngineImpl(_otrEngineHost);

        setUpMessageListener();

        createButton();
        _OtrEnabled = OTRProperties.getInstance().getIsOTREnabled();
    }

    public void updateChatRoom(ChatRoomImpl chatroom) {
        _OtrEnabled = OTRProperties.getInstance().getIsOTREnabled();
        _chatRoom = chatroom;
        setUpMessageListener();
        createButton();
    }

    private void setUpMessageListener() {
        _chatRoom.addMessageEventListener(new MessageEventListener() {

            @Override
            public void sendingMessage(Message message) {
                String oldmsg = message.getBody();
                if (_engine.getSessionStatus(_mySession).equals(SessionStatus.ENCRYPTED)) {
                    message.setBody(null);
                    String mesg = _engine.transformSending(_mySession, oldmsg);
                    message.setBody(mesg);

                }
            }

            @Override
            public void receivingMessage(Message message) {
                if (message.getBody() != null && _OtrEnabled) {
                    String old = message.getBody();
                    message.setBody(null);
                    String mesg = null;
                    if (old.length() > 2) {
                        mesg = _engine.transformReceiving(_mySession, old);
                    }
                    if (_engine.getSessionStatus(_mySession).equals(SessionStatus.ENCRYPTED)) {

                        message.setBody(mesg);
                    } else {
                        if (old.length() > 3 && old.substring(0, 4).equals("?OTR")) {
                            old = null;
                        }
                        message.setBody(old);
                    }
                } else if (!_OtrEnabled)
                {
                    System.out.println("bin drin");
                    String old = message.getBody();
                    message.setBody(null);
                    if (old.length() > 3 && old.substring(0, 4).equals("?OTR")) {
                        _chatRoom.getTranscriptWindow().insertNotificationMessage(OTRResources.getString("otr.not.enabled"), Color.gray);
                    } else {
                    message.setBody(old);
                    }
                }

            }
        });

    }

    private void createButton() {

        if (OTRProperties.getInstance().getIsOTREnabled()) {
            final ClassLoader cl = getClass().getClassLoader();

            ImageIcon otricon = null;
            if (_engine.getSessionStatus(_mySession).equals(SessionStatus.ENCRYPTED)) {
                otricon = new ImageIcon(cl.getResource("otr_on.png"));
                _chatRoom.getTranscriptWindow().insertNotificationMessage("From now on, your conversation is encrypted", Color.gray);
            } else {
                otricon = new ImageIcon(cl.getResource("otr_off.png"));
            }

            _otrButton.setIcon(otricon);

            _otrButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (_engine.getSessionStatus(_mySession).equals(SessionStatus.ENCRYPTED)) {
                        stopSession();
                    } else {
                        startSession();
                    }

                }
            });

            _chatRoom.getToolBar().addChatRoomButton(_otrButton);
            _engine.addOtrEngineListener(new OtrEngineListener() {

                @Override
                public void sessionStatusChanged(SessionID arg0) {
                    if (_engine.getSessionStatus(_mySession).equals(SessionStatus.ENCRYPTED)) {

                        String otrkey = _manager.getKeyManager().getRemoteFingerprint(_mySession);
                        if (otrkey == null) {
                            PublicKey pubkey = _engine.getRemotePublicKey(_mySession);
                            _manager.getKeyManager().savePublicKey(_mySession, pubkey);
                            otrkey = _manager.getKeyManager().getRemoteFingerprint(_mySession);
                        }

                        if (!OTRManager.getInstance().getKeyManager().isVerified(_mySession)) {
                            final int n = JOptionPane.showConfirmDialog(_otrButton,
                                    OTRResources.getString("otr.start.session.with", _remoteJID) + "\n" + OTRResources.getString("otr.key.not.verified.text") + "\n" + otrkey
                                            + "\n" + OTRResources.getString("otr.question.verify"), OTRResources.getString("otr.key.not.verified.title"),
                                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                            if (n == JOptionPane.YES_OPTION) {
                                _manager.getKeyManager().verify(_mySession);
                            }

                        }

                        _chatRoom.getTranscriptWindow().insertNotificationMessage("From now on, your conversation is encrypted Remotekey: ", Color.gray);
                        _otrButton.setIcon(new ImageIcon(cl.getResource("otr_on.png")));
                    } else if (_engine.getSessionStatus(_mySession).equals(SessionStatus.FINISHED)) {
                        _chatRoom.getTranscriptWindow().insertNotificationMessage("From now on, your conversation is NOT encrypted", Color.gray);
                        _otrButton.setIcon(new ImageIcon(cl.getResource("otr_off.png")));
                        stopSession();
                    } else if (_engine.getSessionStatus(_mySession).equals(SessionStatus.PLAINTEXT)) {
                        _chatRoom.getTranscriptWindow().insertNotificationMessage("From now on, your conversation is NOT encrypted", Color.gray);
                    }

                }
            });
        }
    }

    public void startSession() {
        _engine.startSession(_mySession);
        _chatRoom.getTranscriptWindow().insertNotificationMessage("Trying to establish OTR Connection", Color.green);
    }

    public void stopSession() {
        if (_engine.getSessionStatus(_mySession).equals(SessionStatus.ENCRYPTED)) {
            final ClassLoader cl = getClass().getClassLoader();
            _otrButton.setIcon(new ImageIcon(cl.getResource("otr_off.png")));
            _engine.endSession(_mySession);
        }
    }

}
