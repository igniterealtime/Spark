package org.jivesoftware.spark.otrplug;

import java.awt.Color;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.rooms.ChatRoomImpl;

import net.java.otr4j.OtrEngineHost;
import net.java.otr4j.OtrPolicy;
import net.java.otr4j.session.SessionID;

public class OTREngineHost implements OtrEngineHost {

    private ChatRoomImpl _chatRoom;
    private OtrPolicy _policy;
    public OTREngineHost(OtrPolicy policy, ChatRoomImpl chatroom)
    {
        _policy = policy;
        _chatRoom = chatroom;
    }
    
    @Override
    public KeyPair getKeyPair(SessionID arg0) {
        KeyPairGenerator kg;
        try {
            kg = KeyPairGenerator.getInstance("DSA");

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }

        return kg.genKeyPair();
    }

    @Override
    public OtrPolicy getSessionPolicy(SessionID arg0) {
        return _policy;
    }

    @Override
    public void injectMessage(SessionID arg0, String arg1) {
        System.err.println("Send msg via injection "+arg1);     
      //  _chatRoom.sendMessage(arg1);
        Message injection = new Message();
        injection.setType(Message.Type.chat);
        injection.setTo(_chatRoom.getParticipantJID());
        injection.setFrom(SparkManager.getSessionManager().getJID()); 
        String threadID = StringUtils.randomString(6);
        injection.setThread(threadID);
        // Set the body of the message using typedMessage
        injection.setBody(arg1);
        SparkManager.getConnection().sendPacket(injection);
    }

    @Override
    public void showError(SessionID arg0, String arg1) {
        _chatRoom.getTranscriptWindow().insertNotificationMessage(arg1, Color.red);

    }

    @Override
    public void showWarning(SessionID arg0, String arg1) {
        _chatRoom.getTranscriptWindow().insertNotificationMessage(arg1, Color.red);

    }

}
