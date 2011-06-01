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
    final ChatRoomButton _otrButton = new ChatRoomButton();

    public OTRSession(ChatRoomImpl chatroom, String myJID, String remoteJID) {
        _chatRoom = chatroom;
        _myJID = myJID;
        _remoteJID = remoteJID;
        _otrEngineHost = new OTREngineHost(new OtrPolicyImpl(OtrPolicy.ALLOW_V2 | OtrPolicy.ERROR_START_AKE), _chatRoom);
        _mySession = new SessionID(_myJID, _remoteJID, "Scytale");

        _engine = new OtrEngineImpl(_otrEngineHost);

        setUpMessageListener();
        createButton();

    }

    public void updateChatRoom(ChatRoomImpl chatroom) {
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
                System.out.println("-------msg-----------");
                System.out.println(message.getBody());
                System.out.println(_engine.getSessionStatus(_mySession).toString());
                System.out.println("");
                if (message.getBody() != null) {
                    System.out.println("wird behandelt");
                    String old = message.getBody();
                    System.out.println("getmsg");
                    message.setBody(null);
                    String mesg = null;
                    if (old.length() > 2) {
                        mesg = _engine.transformReceiving(_mySession, old);
                    }
                    System.out.println("behandelt worden");
                    if (_engine.getSessionStatus(_mySession).equals(SessionStatus.ENCRYPTED)) {

                        System.out.println("to encrypt");
                        message.setBody(mesg);
                    } else {
                        System.out.println("dont decrypt");
                        if (old.length() > 3 && old.substring(0, 4).equals("?OTR")) {
                            old = null;
                        }
                        System.out.println("old is...");

                        System.out.println("setze: " + old);
                        message.setBody(old);
                    }
                }

            }
        });

    }

    private void createButton() {

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
                    String remkey;
                    if (OTRManager.getInstance().getKeyManager().isVerified(_mySession))
                    {
                        remkey ="yep";
                    } else
                    {
                        remkey = "no";
                      int n =  JOptionPane.showConfirmDialog(
                                null,
                                "Not verified!",
                                "Not verified!",
                                JOptionPane.YES_NO_OPTION);
                      if (n == JOptionPane.YES_OPTION)
                      {
                          OTRManager.getInstance().getKeyManager().verify(_mySession);
                      }
                    }
                    
                    
                 //   PublicKey remote = OTRManager.getInstance().getKeyManager().
                    remkey = OTRManager.getInstance().getKeyManager().getRemoteFingerprint(_mySession);
                    if (remkey == null)
                    {
                       PublicKey rempubkey =  _engine.getRemotePublicKey(_mySession);
                       OTRManager.getInstance().getKeyManager().savePublicKey(_mySession, rempubkey);
                       remkey =   OTRManager.getInstance().getKeyManager().getRemoteFingerprint(_mySession);
                    }
                    //String remkey = OTRManager.getInstance().getKeyManager().getRemoteFingerprint(_mySession);
                    _chatRoom.getTranscriptWindow().insertNotificationMessage("From now on, your conversation is encrypted Remotekey: "+remkey, Color.gray);
                    _otrButton.setIcon(new ImageIcon(cl.getResource("otr_on.png")));
                } else if (_engine.getSessionStatus(_mySession).equals(SessionStatus.FINISHED)) {
                    _chatRoom.getTranscriptWindow().insertNotificationMessage("From now on, your conversation is NOT encrypted", Color.gray);
                    _otrButton.setIcon(new ImageIcon(cl.getResource("otr_off.png")));
                    stopSession();
                } else if (_engine.getSessionStatus(_mySession).equals(SessionStatus.PLAINTEXT)) {
                    _chatRoom.getTranscriptWindow().insertNotificationMessage("From now on, your conversation is NOT encrypted", Color.gray);
                }

                System.out.println(_engine.getSessionStatus(_mySession));

            }
        });
    }

    private void startSession() {
        _engine.startSession(_mySession);
        _chatRoom.getTranscriptWindow().insertNotificationMessage("Trying to establish OTR Connection", Color.green);
        SessionStatus status = _engine.getSessionStatus(_mySession);
        System.out.println(status.toString());
    }

    private void stopSession() {
        final ClassLoader cl = getClass().getClassLoader();
        _otrButton.setIcon(new ImageIcon(cl.getResource("otr_off.png")));
        _engine.endSession(_mySession);
    }

}
