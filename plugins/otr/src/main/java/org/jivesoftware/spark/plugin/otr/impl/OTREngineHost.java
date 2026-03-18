package org.jivesoftware.spark.plugin.otr.impl;

import static net.java.otr4j.session.FragmenterInstructions.UNLIMITED;

import java.awt.*;
import java.security.KeyPair;
import java.util.List;

import net.java.otr4j.crypto.OtrCryptoEngine;
import net.java.otr4j.session.*;
import org.jivesoftware.resource.Res;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.StanzaBuilder;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.plugin.otr.OTRManager;
import org.jivesoftware.spark.plugin.otr.util.OTRResources;
import org.jivesoftware.spark.ui.rooms.ChatRoomImpl;

import net.java.otr4j.OtrEngineHost;
import net.java.otr4j.OtrException;
import net.java.otr4j.OtrPolicy;
import org.jivesoftware.spark.util.log.Log;

import javax.net.ssl.HandshakeCompletedEvent;
import javax.swing.*;

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
        String authReqMsg = OTRResources.getString("otr.authentication.request", _chatRoom.getParticipantNickname());
        _chatRoom.getTranscriptWindow().insertNotificationMessage(authReqMsg, Color.orange);
        OTRSession otrSession = OTRManager.getInstance().getOtrSession(sessionID.getUserID());
        EventQueue.invokeLater(() -> {
            Session mySession = otrSession.getMySession();
            String dlgMsg = authReqMsg + "\n" + OTRResources.getString("otr.authentication.giveAnswer");
            if (question != null) {
                dlgMsg += ": " + question;
            }
            String secret = JOptionPane.showInputDialog(_chatRoom, dlgMsg, authReqMsg, JOptionPane.QUESTION_MESSAGE);
            try {
                if (secret == null) {
                    mySession.abortSmp();
                    return;
                }
                mySession.respondSmp(receiverTag, question, secret);
            } catch (OtrException e) {
                Log.error(e);
                _chatRoom.getTranscriptWindow().insertNotificationMessage(e.getMessage(), Color.red);
            }
        });
    }

    @Override
    public void verify(SessionID sessionID, String fingerprint, boolean approved) {
        Log.debug("Session authentification finished " + sessionID + " approved: " + approved);
        if (approved) {
            OTRManager.getInstance().getKeyManager().verify(sessionID);
            String msg = OTRResources.getString("otr.authorization.completed");
            _chatRoom.getTranscriptWindow().insertNotificationMessage(msg, Color.green);
        }
    }

    @Override
    public void unverify(SessionID sessionID, String fingerprint) {
        String msg = "Session was not verified: " + sessionID + " fingerprint: " + fingerprint;
        Log.warning(msg);
        _chatRoom.getTranscriptWindow().insertNotificationMessage(msg, Color.red);
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
        String msg = "Received message from another instances: " + sessionID;
        Log.warning(msg);
        _chatRoom.getTranscriptWindow().insertNotificationMessage(msg, Color.red);
    }

    @Override
    public void multipleInstancesDetected(SessionID sessionID) {
        Log.warning("Detected multiple instances: " + sessionID);
    }

    @Override
    public OtrPolicy getSessionPolicy(SessionID sessionID) {
        return _policy;
    }

    @Override
    public FragmenterInstructions getFragmenterInstructions(SessionID sessionID) {
        return new FragmenterInstructions(UNLIMITED, 64*1024);
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
