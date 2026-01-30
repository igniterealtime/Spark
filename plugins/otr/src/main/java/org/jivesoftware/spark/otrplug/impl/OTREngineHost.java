package org.jivesoftware.spark.otrplug.impl;

import static net.java.otr4j.session.FragmenterInstructions.UNLIMITED;

import java.awt.Color;
import java.security.KeyPair;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.StanzaBuilder;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.otrplug.OTRManager;
import org.jivesoftware.spark.ui.rooms.ChatRoomImpl;

import net.java.otr4j.OtrEngineHost;
import net.java.otr4j.OtrException;
import net.java.otr4j.OtrPolicy;
import net.java.otr4j.session.FragmenterInstructions;
import net.java.otr4j.session.InstanceTag;
import net.java.otr4j.session.SessionID;
import org.jivesoftware.spark.util.log.Log;

/**
 * Implementation of OtrEngineHost provided from otr4j. It handles the message
 * injection to specified chat window and handles key pair.
 * 
 * @author Bergunde Holger
 */
public class OTREngineHost implements OtrEngineHost {

    private final ChatRoomImpl _chatRoom;
    private final OtrPolicy _policy;

    public OTREngineHost(OtrPolicy policy, ChatRoomImpl chatroom) {
        _policy = policy;
        _chatRoom = chatroom;
    }

    @Override
    public KeyPair getLocalKeyPair(SessionID sessionID) {
        return OTRManager.getInstance().getKeyManager().loadLocalKeyPair(sessionID);
    }

    @Override
    public byte[] getLocalFingerprintRaw(SessionID sessionID) {
        return OTRManager.getInstance().getKeyManager().getLocalFingerprintRaw(sessionID);
    }

    @Override
    public void askForSecret(SessionID sessionID, InstanceTag receiverTag, String question) {
        Log.debug("Ask for secret from: " + sessionID + ", question: " + question);
    }

    @Override
    public void verify(SessionID sessionID, String fingerprint, boolean approved) {
        Log.debug("Session was verified: " + sessionID);
        if (!approved)
            Log.debug("Your answer for the question was verified."
                + "You should ask your opponent too or check shared secret.");
    }

    @Override
    public void unverify(SessionID sessionID, String fingerprint) {
        Log.warning("Session was not verified: " + sessionID);
    }

    @Override
    public String getReplyForUnreadableMessage(SessionID sessionID) {
        return "You sent me an unreadable encrypted message.";
    }

    @Override
    public String getFallbackMessage(SessionID sessionID) {
        return "Off-the-Record private conversation has been requested. However, you do not have a plugin to support that.";
    }

    @Override
    public void messageFromAnotherInstanceReceived(SessionID sessionID) {
        //TODO
    }

    @Override
    public void multipleInstancesDetected(SessionID sessionID) {
        //TODO
    }

    @Override
    public OtrPolicy getSessionPolicy(SessionID sessionID) {
        return _policy;
    }

    @Override
    public FragmenterInstructions getFragmenterInstructions(SessionID sessionID) {
        return  new FragmenterInstructions(UNLIMITED, UNLIMITED);
    }

    @Override
    public void injectMessage(SessionID sessionID, String msg) {
        String threadID = StringUtils.randomString(6);
        Message injection = StanzaBuilder.buildMessage()
            .ofType(Message.Type.chat)
            .setThread(threadID)
            .setBody(msg)
            .to(_chatRoom.getParticipantJID())
            .from(SparkManager.getSessionManager().getJID())
            .build();
        try
        {
            SparkManager.getConnection().sendStanza(injection);
        }
        catch (SmackException.NotConnectedException | InterruptedException e )
        {
            Log.warning( "Unable to send injection to " + injection.getTo(), e );
        }
    }

    @Override
    public void unreadableMessageReceived(SessionID sessionID) throws OtrException {
        Log.warning("Unreadable message received from: " + sessionID);
    }

    @Override
    public void unencryptedMessageReceived(SessionID sessionID, String msg) throws OtrException {
        Log.warning("Unencrypted message received: " + msg + " from " + sessionID);
    }

    @Override
    public void showError(SessionID sessionID, String error) {
        // shows error to user
        _chatRoom.getTranscriptWindow().insertNotificationMessage(error, Color.red);
    }

    @Override
    public void smpError(SessionID sessionID, int tlvType, boolean cheated) throws OtrException {
        // shows error to user
        _chatRoom.getTranscriptWindow().insertNotificationMessage("SM verification error with user: " + sessionID, Color.red);
    }

    @Override
    public void smpAborted(SessionID sessionID) throws OtrException {
        // shows error to user
        _chatRoom.getTranscriptWindow().insertNotificationMessage("SM verification has been aborted by user: " + sessionID, Color.red);
    }

    @Override
    public void finishedSessionMessage(SessionID sessionID, String msgText) throws OtrException {
        Log.warning("SM session was finished. You shouldn't send messages to: " + sessionID);
    }

    @Override
    public void requireEncryptedMessage(SessionID sessionID, String msgText) throws OtrException {
        Log.warning("Message can't be sent while encrypted session is not established: " + sessionID);
    }

}
