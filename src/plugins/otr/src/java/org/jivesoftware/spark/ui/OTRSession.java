package org.jivesoftware.spark.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.component.RolloverButton;
import org.jivesoftware.spark.otrplug.OTREngineHost;
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
    private SessionID _remoteSession;
    private OtrEngineImpl _engine;

    public OTRSession(ChatRoomImpl chatroom, String myJID, String remoteJID) {
        _chatRoom = chatroom;
        _myJID = myJID;
        _remoteJID = remoteJID;
        _otrEngineHost = new OTREngineHost(new OtrPolicyImpl(OtrPolicy.ALLOW_V2 | OtrPolicy.ERROR_START_AKE), _chatRoom);
        _mySession = new SessionID(SparkManager.getConnection().getUser(), SparkManager.getConnection().getUser(), "Scytale");

        _engine = new OtrEngineImpl(_otrEngineHost);

        _chatRoom.addMessageEventListener(new MessageEventListener() {

            @Override
            public void sendingMessage(Message message) {
                System.out.println("SENDING MESSAGE");
                String oldmsg = message.getBody();
                if (_engine.getSessionStatus(_mySession).equals(SessionStatus.ENCRYPTED)) {
                    message.setBody(null);
                    String mesg = _engine.transformSending(_mySession, oldmsg);
                    message.setBody(mesg);
                    System.out.println("habs jetzt umgeschrieben!");
                }
            }

            @Override
            public void receivingMessage(Message message) {
                System.out.println("RECIECVING MESSAGE");

                if (message.getBody() != null && message.getBody().length() > 0) {
                    String old = message.getBody();
                    message.setBody(null);
                    String mesg = _engine.transformReceiving(_mySession, old);

                    if (_engine.getSessionStatus(_mySession).equals(SessionStatus.ENCRYPTED)) {

                        System.out.println("enc: " + mesg + " Recieved: " + old);
                        message.setBody(mesg);
                    } else {
                        System.out.println("not encrypted rec: " + old);
                        if (old.substring(0, 4).equals("?OTR"))
                        {
                            old = "otr verbindungsversuch";
                        }
                        message.setBody(old);
                    }
                }

            }
        });

        createButton();

    }

    private void createButton() {
        final ChatRoomButton _otrButton = new ChatRoomButton();

        final ClassLoader cl = getClass().getClassLoader();

        ImageIcon otricon = new ImageIcon(cl.getResource("otr_off.png"));
        _otrButton.setIcon(otricon);
        _otrButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (_engine.getSessionStatus(_mySession).equals(SessionStatus.ENCRYPTED)) {
                    stopSession();
                } else 
                {
                    startSession();
                }
               

            }
        });
        _chatRoom.getToolBar().addChatRoomButton(_otrButton);
        _engine.addOtrEngineListener(new OtrEngineListener() {

            @Override
            public void sessionStatusChanged(SessionID arg0) {
                if (_engine.getSessionStatus(_mySession).equals(SessionStatus.ENCRYPTED)) {
                    _otrButton.setIcon(new ImageIcon(cl.getResource("otr_on.png")));
                }

                if (!_engine.getSessionStatus(_mySession).equals(SessionStatus.ENCRYPTED)) {
                    _otrButton.setIcon(new ImageIcon(cl.getResource("otr_off.png")));
                }

            }
        });
    }

    private void startSession() {
        _engine.startSession(_mySession);
        SessionStatus status = _engine.getSessionStatus(_mySession);
        System.out.println(status.toString());
    }

    private void stopSession()
    {
        _engine.endSession(_mySession);
    }
    
}
