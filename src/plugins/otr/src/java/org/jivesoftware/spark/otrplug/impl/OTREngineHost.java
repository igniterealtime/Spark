package org.jivesoftware.spark.otrplug.impl;

import java.awt.Color;
import java.security.KeyPair;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.otrplug.OTRManager;
import org.jivesoftware.spark.ui.rooms.ChatRoomImpl;

import net.java.otr4j.OtrEngineHost;
import net.java.otr4j.OtrPolicy;
import net.java.otr4j.session.SessionID;
import org.jivesoftware.spark.util.log.Log;

/**
 * 
 * Implementation of OtrEngineHost provided from otr4j. It handles the message
 * injection to specified chat window and handles key pair.
 * 
 * @author Bergunde Holger
 */
public class OTREngineHost implements OtrEngineHost {

    private ChatRoomImpl _chatRoom;
    private OtrPolicy _policy;

    public OTREngineHost(OtrPolicy policy, ChatRoomImpl chatroom) {
        _policy = policy;
        _chatRoom = chatroom;
    }

    @Override
    public KeyPair getKeyPair(SessionID arg0) {
        return OTRManager.getInstance().getKeyManager().loadLocalKeyPair(arg0);
    }

    @Override
    public OtrPolicy getSessionPolicy(SessionID arg0) {
        return _policy;
    }

    @Override
    public void injectMessage(SessionID arg0, String arg1) {
        Message injection = new Message();
        injection.setType(Message.Type.chat);
        injection.setTo(_chatRoom.getParticipantJID());
        injection.setFrom(SparkManager.getSessionManager().getJID());
        String threadID = StringUtils.randomString(6);
        injection.setThread(threadID);
        injection.setBody(arg1);
        try
        {
            SparkManager.getConnection().sendStanza(injection);
        }
        catch ( SmackException.NotConnectedException e )
        {
            Log.warning( "Unable to send injection to " + injection.getTo(), e );
        }
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
