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
package net.java.sipmack.sip;

import gov.nist.javax.sip.header.CSeq;

import java.text.ParseException;
import java.util.ArrayList;

import javax.sip.ClientTransaction;
import javax.sip.Dialog;
import javax.sip.InvalidArgumentException;
import javax.sip.ServerTransaction;
import javax.sip.SipException;
import javax.sip.Transaction;
import javax.sip.TransactionUnavailableException;
import javax.sip.address.Address;
import javax.sip.address.SipURI;
import javax.sip.address.URI;
import javax.sip.header.AllowHeader;
import javax.sip.header.CSeqHeader;
import javax.sip.header.CallIdHeader;
import javax.sip.header.ContactHeader;
import javax.sip.header.ContentLengthHeader;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.MaxForwardsHeader;
import javax.sip.header.ToHeader;
import javax.sip.header.ViaHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;

import net.java.sipmack.common.Log;
import net.java.sipmack.sip.security.SipSecurityException;

import org.jivesoftware.spark.phone.PhoneManager;

/**
 * <p/>
 * Title: SIP COMMUNICATOR-1.1
 * </p>
 * <p/>
 * Description: JAIN-SIP-1.1 Audio/Video Phone Application
 * </p>
 * <p/>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p/>
 * Company: Organisation: LSIIT Laboratory (http://lsiit.u-strasbg.fr)
 * <p/>
 * </p>
 * Network Research Team (http://www-r2.u-strasbg.fr))
 * <p/>
 * </p>
 * Louis Pasteur University - Strasbourg - France
 * </p>
 *
 * @author Emil Ivov
 * @version 1.1
 */

public class CallProcessing {
    protected SipManager sipManCallback = null;

    protected CallDispatcher callDispatcher = new CallDispatcher();

    CallProcessing() {
    }

    CallProcessing(SipManager sipManCallback) {
        this.sipManCallback = sipManCallback;
    }

    void setSipManagerCallBack(SipManager sipManCallback) {
        this.sipManCallback = sipManCallback;
    }

    // ============================= Remotely Initiated Processing
    // ===================================
    // ----------------------------- Responses
    void processTrying(ClientTransaction clientTransaction, Response response) {
        // find the call
        Call call = callDispatcher.findCall(clientTransaction.getDialog());
        if (call == null) {
            sipManCallback.fireUnknownMessageReceived(response);
            return;
        }
        // change status
        if (!call.getState().equals(Call.MOVING_LOCALLY))
            call.setState(Call.DIALING);
    }

    void processRinging(ClientTransaction clientTransaction, Response response) {
        // find the call
        Call call = callDispatcher.findCall(clientTransaction.getDialog());
        String sdp = response.getRawContent() != null ? response
                .getRawContent().toString() : "";
        call.setRemoteSdpDescription(sdp);

        // change status
        call.setState(Call.RINGING);
    }

    void processRingingBack(ClientTransaction clientTransaction,
                            Response response) {
        // find the call
        Call call = callDispatcher.findCall(clientTransaction.getDialog());
        call.setRemoteSdpDescription(new String(response.getRawContent()));
        // change status
        call.setState(Call.RINGING);

    }

    /**
     * According to the RFC a UAC canceling a request cannot rely on receiving a
     * 487 (Request Terminated) response for the original request, as an RFC
     * 2543- compliant UAS will not generate such a response. So we are closing
     * the call when sending the cancel request and here we don't do anything.
     *
     * @param clientTransaction
     * @param response
     */
    void processRequestTerminated(ClientTransaction clientTransaction,
                                  Response response) {
        try {
            // add any additional code here
        }
        finally {

        }
    }

    void processByeOK(ClientTransaction clientTransaction, Response response) {
        try {
            // add any additional code here
        }
        finally {

        }
    }

    void processCancelOK(ClientTransaction clientTransaction, Response response) {
        try {
            // add any additional code here
        }
        finally {

        }
    }

    void processInviteOK(ClientTransaction clientTransaction, Response ok) {
        // find the call
        Call call = callDispatcher.findCall(clientTransaction.getDialog());
        if (call == null) {
            sipManCallback.fireUnknownMessageReceived(ok);
            return;
        }
        // Send ACK
        try {
            // Need to use dialog generated ACKs so that the remote UA core
            // sees them - Fixed by M.Ranganathan
            Request ackRequest = clientTransaction.getDialog()
					 			.createAck(((CSeqHeader)ok.getHeader(CSeqHeader.NAME)).getSeqNumber());

            clientTransaction.getDialog().sendAck(ackRequest);
            
        }
        catch (SipException ex) {
        	ex.printStackTrace();
            call.setState(Call.DISCONNECTED);
            sipManCallback
                    .fireCommunicationsError(new CommunicationsException(
                            "Failed to acknowledge call!", ex));
            return;
        } catch (InvalidArgumentException e) {
			e.printStackTrace();
			call.setState(Call.DISCONNECTED);
	            sipManCallback.fireCommunicationsError(new CommunicationsException(
	                            "Failed to create ack!", e));
	            return;
		}
        call.setRemoteSdpDescription(new String(ok.getRawContent()));
        // change status
        if (!call.getState().equals(Call.CONNECTED)) {
            call.setState(Call.CONNECTED);
        }
    }

    void processBusyHere(ClientTransaction clientTransaction, Response busyHere) {
        // find the call
        Call call = callDispatcher.findCall(clientTransaction.getDialog());
        if (call == null) {
            sipManCallback.fireUnknownMessageReceived(busyHere);
            return;
        }
        // change status
        call.setState(Call.BUSY);
        // it is the stack that should be sending the ACK so don't do it
        // here
    }

    void processCallError(ClientTransaction clientTransaction,
                          Response notAcceptable) {
        // find the call
        Call call = callDispatcher.findCall(clientTransaction.getDialog());
        if (call == null) {
            sipManCallback.fireUnknownMessageReceived(notAcceptable);
            return;
        }
        // change status
        call.setState(Call.FAILED);
        sipManCallback.fireCommunicationsError(new CommunicationsException(
                "Remote party returned error response: "
                        + notAcceptable.getStatusCode() + " - "
                        + notAcceptable.getReasonPhrase()));
        return;

        // it is the stack that should be sending the ACK so don't do it
        // here
    }

    /**
     * Attempts to re-ogenerate the corresponding request with the proper
     * credentials and terminates the call if it fails.
     *
     * @param clientTransaction the corresponding transaction
     * @param response          the challenge
     */
    void processAuthenticationChallenge(ClientTransaction clientTransaction,
                                        Response response) {
        try {
            ClientTransaction retryTran = sipManCallback.sipSecurityManager
                    .handleChallenge(response, clientTransaction);
            // There is a new dialog that will be started with this request. Get
            // that dialog and record it into the Call objet for later use (by
            // Bye-s for example).

            Call call = callDispatcher.findCall(clientTransaction.getDialog());
            call.setDialog(retryTran.getDialog());
            call.setInitialRequest(retryTran.getRequest());
            retryTran.sendRequest();
        }
        catch (SipSecurityException exc) {
            callDispatcher.findCall(clientTransaction.getDialog()).setState(
                    Call.FAILED);
            sipManCallback.fireCommunicationsError(new CommunicationsException(
                    "Authorization failed!", exc));
        }
        catch (Exception exc) {
            callDispatcher.findCall(clientTransaction.getDialog()).setState(
                    Call.FAILED);
            sipManCallback.fireCommunicationsError(new CommunicationsException(
                    "Failed to resend a request "
                            + "after a security challenge!", exc));
        }
    }

    // -------------------------- Requests ---------------------------------
    void processInvite(ServerTransaction serverTransaction, Request invite) {
        Dialog dialog = serverTransaction.getDialog();

        if (!sipManCallback.isBusy() && !(PhoneManager.isUseStaticLocator()&&PhoneManager.isUsingMediaLocator())) {
            Call call = callDispatcher.createCall(dialog, invite);
            sipManCallback.fireCallReceived(call);
            // change status
            call.setState(Call.ALERTING);
            // sdp description may be in acks - bug report Laurent Michel
            ContentLengthHeader cl = invite.getContentLength();
            if (cl != null && cl.getContentLength() > 0) {
                call.setRemoteSdpDescription(new String(invite
                        .getRawContent()));
            }
            // Are we the one they are looking for?
            URI calleeURI = ((ToHeader) invite.getHeader(ToHeader.NAME))
                    .getAddress().getURI();
            /**
             * @todo We shoud rather ask the user what to do here as some
             *       would add prefixes or change user URIs
             */
            if (calleeURI.isSipURI()) {
                boolean assertUserMatch = SIPConfig
                        .isFailCallInUserMismatch();
                // user info is case sensitive according to rfc3261
                if (assertUserMatch) {
                    String calleeUser = ((SipURI) calleeURI).getUser();
                    String localUser = sipManCallback.getLocalUser();
                    if (calleeUser != null && !calleeUser.equals(localUser)) {
                        sipManCallback
                                .fireCallRejectedLocally(
                                        "The user specified by the caller did not match the local user!",
                                        invite, call);
                        call.setState(Call.DISCONNECTED);
                        Response notFound = null;
                        try {
                            notFound = sipManCallback.messageFactory
                                    .createResponse(Response.NOT_FOUND,
                                            invite);
                            sipManCallback.attachToTag(notFound, dialog);
                        }
                        catch (ParseException ex) {
                            call.setState(Call.DISCONNECTED);
                            sipManCallback
                                    .fireCommunicationsError(new CommunicationsException(
                                            "Failed to create a NOT_FOUND response to an INVITE request!",
                                            ex));
                            return;
                        }
                        try {
                            serverTransaction.sendResponse(notFound);

                        }
                        catch (SipException ex) {
                            call.setState(Call.DISCONNECTED);
                            sipManCallback
                                    .fireCommunicationsError(new CommunicationsException(
                                            "Failed to send a NOT_FOUND response to an INVITE request!",
                                            ex));
                            return;
                        } catch (InvalidArgumentException e) {
                            call.setState(Call.DISCONNECTED);
                            sipManCallback
                                    .fireCommunicationsError(new CommunicationsException(
                                            "Failed to send a NOT_FOUND response to an INVITE request!",
                                            e));
                            return;
                        }
                        return;
                    }
                }
            }

            // Send RINGING
            Response ringing = null;
            try {
                ringing = sipManCallback.messageFactory.createResponse(
                        Response.RINGING, invite);
                sipManCallback.attachToTag(ringing, dialog);
            }
            catch (ParseException ex) {
                call.setState(Call.DISCONNECTED);
                sipManCallback
                        .fireCommunicationsError(new CommunicationsException(
                                "Failed to create a RINGING response to an INVITE request!",
                                ex));
                return;
            }
            try {
                serverTransaction.sendResponse(ringing);

            }
            catch (SipException ex) {
                call.setState(Call.DISCONNECTED);
                sipManCallback
                        .fireCommunicationsError(new CommunicationsException(
                                "Failed to send a RINGING response to an INVITE request!",
                                ex));
                return;
            } catch (InvalidArgumentException e) {
                call.setState(Call.DISCONNECTED);
                sipManCallback
                        .fireCommunicationsError(new CommunicationsException(
                                "Failed to send a NOT_FOUND response to an INVITE request!",
                                e));
                return;
            }
        } else {
            // Send BUSY_HERE
            Response busy = null;
            try {
                busy = sipManCallback.messageFactory.createResponse(
                        Response.BUSY_HERE, invite);
                sipManCallback.attachToTag(busy, dialog);
            }
            catch (ParseException ex) {
                sipManCallback
                        .fireCommunicationsError(new CommunicationsException(
                                "Failed to create a RINGING response to an INVITE request!",
                                ex));
                return;
            }
            try {
                serverTransaction.sendResponse(busy);
            }
            catch (SipException ex) {
                sipManCallback
                        .fireCommunicationsError(new CommunicationsException(
                                "Failed to send a RINGING response to an INVITE request!",
                                ex));
                return;
            } catch (InvalidArgumentException e) {
                sipManCallback
                        .fireCommunicationsError(new CommunicationsException(
                                "Failed to send a RINGING response to an INVITE request!",
                                e));
                return;
            }
        }
    }

    void processTimeout(Transaction transaction, Request request) {
        Call call = callDispatcher.findCall(transaction.getDialog());
        if (call == null) {
            return;
        }
        sipManCallback.fireCommunicationsError(new CommunicationsException(
                "The remote party has not replied!"
                        + "The call will be disconnected"));
        // change status
        call.setState(Call.DISCONNECTED);

    }

    void processBye(ServerTransaction serverTransaction, Request byeRequest) {
   	 
        try {
            // find the call
            Call call = callDispatcher.findCall(serverTransaction.getDialog());
            if (call == null) {
                Log.debug("No call find");
                sipManCallback.fireUnknownMessageReceived(byeRequest);
                return;
            }

            // Send OK
            Response ok = null;
            try {
                ok = sipManCallback.messageFactory.createResponse(Response.OK,
                        byeRequest);
                sipManCallback.attachToTag(ok, call.getDialog());
            }
            catch (ParseException ex) {
                sipManCallback
                        .fireCommunicationsError(new CommunicationsException(
                                "Failed to construct an OK response to a BYE request!",
                                ex));
                return;
            }
            try {
                serverTransaction.sendResponse(ok);
            }
            catch (SipException ex) {
                // This is not really a problem according to the RFC
                // so just dump to stdout should someone be interested
            }

            endCall(call.getID());

        }
        catch (Exception e) {
            Log.error("processBye", e);
        }
    }

    void processAck(ServerTransaction serverTransaction, Request ackRequest) {
        if (!serverTransaction.getDialog().getFirstTransaction()
                .getRequest().getMethod().equals(Request.INVITE)) {

            return;
        }
        // find the call
        Call call = callDispatcher.findCall(serverTransaction.getDialog());
        if (call == null) {
            // this is most probably the ack for a killed call - don't
            // signal it
            // sipManCallback.fireUnknownMessageReceived(ackRequest);

            return;
        }
        ContentLengthHeader cl = ackRequest.getContentLength();
        if (cl != null && cl.getContentLength() > 0) {
            call.setRemoteSdpDescription(new String(ackRequest
                    .getRawContent()));
        }
        // change status
        call.setState(Call.CONNECTED);
    }

    void processCancel(ServerTransaction serverTransaction,
                       Request cancelRequest) {
        if (!serverTransaction.getDialog().getFirstTransaction()
                .getRequest().getMethod().equals(Request.INVITE)) {
            // For someone else

            return;
        }
        // find the call
        Call call = callDispatcher.findCall(serverTransaction.getDialog());
        if (call == null) {
            sipManCallback.fireUnknownMessageReceived(cancelRequest);
            return;
        }
        // change status
        call.setState(Call.DISCONNECTED);
        // Cancels should be OK-ed and the initial transaction - terminated
        // (report and fix by Ranga)
        try {
            Response ok = sipManCallback.messageFactory.createResponse(
                    Response.OK, cancelRequest);
            sipManCallback.attachToTag(ok, call.getDialog());
            serverTransaction.sendResponse(ok);

        }
        catch (ParseException ex) {
            sipManCallback
                    .fireCommunicationsError(new CommunicationsException(
                            "Failed to create an OK Response to an CANCEL request.",
                            ex));
        }
        catch (SipException ex) {
            sipManCallback
                    .fireCommunicationsError(new CommunicationsException(
                            "Failed to send an OK Response to an CANCEL request.",
                            ex));
        } catch (InvalidArgumentException e) {
            sipManCallback
                    .fireCommunicationsError(new CommunicationsException(
                            "Failed to send a NOT_FOUND response to an INVITE request!",
                            e));

        }
        try {
            // stop the invite transaction as well
            Transaction tran = call.getDialog().getFirstTransaction();
            // should be server transaction and misplaced cancels should be
            // filtered by the stack but it doesn't hurt checking anyway
            if (!(tran instanceof ServerTransaction)) {
                sipManCallback
                        .fireCommunicationsError(new CommunicationsException(
                                "Received a misplaced CANCEL request!"));
                return;
            }
            ServerTransaction inviteTran = (ServerTransaction) tran;
            Request invite = call.getDialog().getFirstTransaction()
                    .getRequest();
            Response requestTerminated = sipManCallback.messageFactory
                    .createResponse(Response.REQUEST_TERMINATED, invite);
            sipManCallback.attachToTag(requestTerminated, call.getDialog());
            inviteTran.sendResponse(requestTerminated);

        }
        catch (ParseException ex) {
            sipManCallback
                    .fireCommunicationsError(new CommunicationsException(
                            "Failed to create a REQUEST_TERMINATED Response to an INVITE request.",
                            ex));
        }
        catch (SipException ex) {
            sipManCallback
                    .fireCommunicationsError(new CommunicationsException(
                            "Failed to send an REQUEST_TERMINATED Response to an INVITE request.",
                            ex));
        } catch (InvalidArgumentException e) {
            sipManCallback
                    .fireCommunicationsError(new CommunicationsException(
                            "Failed to send a NOT_FOUND response to an INVITE request!",
                            e));
        }
    }

    // ----------------- Responses --------------------------
    // NOT FOUND
    void processNotFound(ClientTransaction clientTransaction, Response response) {
        if (!clientTransaction.getDialog().getFirstTransaction()
                .getRequest().getMethod().equals(Request.INVITE)) {
            // Not for us

            return;
        }
        // find the call
        Call call = callDispatcher.findCall(clientTransaction.getDialog());
        if (call != null)
            call.setState(Call.DISCONNECTED);
        sipManCallback.fireCallRejectedRemotely(
                "Number NOT found at the server.", response, call);

    }

    void processNotImplemented(ClientTransaction clientTransaction,
                               Response response) {
        if (!clientTransaction.getDialog().getFirstTransaction()
                .getRequest().getMethod().equals(Request.INVITE)) {
            // Not for us

            return;
        }
        // find the call
        Call call = callDispatcher.findCall(clientTransaction.getDialog());
        call.setState(Call.DISCONNECTED);
        sipManCallback.fireCallRejectedRemotely(
                "Server cannot dial this number.", response, call);

    }

    // -------------------------------- User Initiated processing
    // ---------------------------------
    Call invite(String callee, String sdpContent)
            throws CommunicationsException {
        callee = callee.trim();
        // Remove excessive characters from phone numbers such as '
        // ','(',')','-'
        String excessiveChars = SIPConfig.getExcessiveURIChar();

        if (excessiveChars != null) {
            StringBuffer calleeBuff = new StringBuffer(callee);
            for (int i = 0; i < excessiveChars.length(); i++) {
                String charToDeleteStr = excessiveChars.substring(i, i + 1);

                int charIndex = -1;
                while ((charIndex = calleeBuff.indexOf(charToDeleteStr)) != -1)
                    calleeBuff.delete(charIndex, charIndex + 1);
            }
            callee = calleeBuff.toString();
        }

        // Handle default domain name (i.e. transform 1234 -> 1234@sip.com

        String defaultDomainName = SIPConfig.getDefaultDomain();
        if (defaultDomainName != null // no sip scheme
                && !callee.trim().startsWith("tel:")
                && callee.indexOf('@') == -1 // most probably a sip uri
                ) {
            callee = callee + "@" + defaultDomainName;
        }

        // Let's be uri fault tolerant
        if (callee.toLowerCase().indexOf("sip:") == -1 // no sip scheme
                && callee.indexOf('@') != -1 // most probably a sip uri
                ) {
            callee = "sip:" + callee;

        }
        // Request URI
        URI requestURI;
        try {
            requestURI = sipManCallback.addressFactory.createURI(callee);
        }
        catch (ParseException ex) {

            throw new CommunicationsException(callee
                    + " is not a legal SIP uri!", ex);
        }
        // Call ID
        CallIdHeader callIdHeader = sipManCallback.sipProvider
                .getNewCallId();
        // CSeq
        CSeqHeader cSeqHeader;
        try {
            cSeqHeader = sipManCallback.headerFactory.createCSeqHeader(1L,
                    Request.INVITE);
        }
        catch (ParseException ex) {
            // Shouldn't happen

            throw new CommunicationsException(
                    "An unexpected erro occurred while"
                            + "constructing the CSeqHeadder", ex);
        }
        catch (InvalidArgumentException ex) {
            // Shouldn't happen
            throw new CommunicationsException(
                    "An unexpected erro occurred while"
                            + "constructing the CSeqHeadder", ex);
        }
        // FromHeader
        FromHeader fromHeader = sipManCallback.getFromHeader();
        // ToHeader
        Address toAddress = sipManCallback.addressFactory
                .createAddress(requestURI);
        ToHeader toHeader;
        try {
            toHeader = sipManCallback.headerFactory.createToHeader(
                    toAddress, null);
        }
        catch (ParseException ex) {
            // Shouldn't happen
            throw new CommunicationsException(
                    "Null is not an allowed tag for the to header!", ex);
        }
        // ViaHeaders
        ArrayList<ViaHeader> viaHeaders = sipManCallback.getLocalViaHeaders();
        // MaxForwards
        MaxForwardsHeader maxForwards = sipManCallback
                .getMaxForwardsHeader();
        // Contact
        ContactHeader contactHeader = sipManCallback.getContactHeader();
        Request invite = null;
        try {
            invite = sipManCallback.messageFactory.createRequest(
                    requestURI, Request.INVITE, callIdHeader, cSeqHeader,
                    fromHeader, toHeader, viaHeaders, maxForwards);
        }
        catch (ParseException ex) {
            throw new CommunicationsException(
                    "Failed to create invite Request!", ex);
        }
        //
        invite.addHeader(contactHeader);

        AllowHeader allow = null;
        try {
            allow = sipManCallback.headerFactory.createAllowHeader("INVITE, ACK, CANCEL, OPTIONS, BYE, REFER, NOTIFY, MESSAGE, SUBSCRIBE, INFO");
            invite.addHeader(allow);
        } catch (ParseException e) {
            Log.error(e);
        }

        // Content
        ContentTypeHeader contentTypeHeader = null;
        try {
            // content type should be application/sdp (not applications)
            // reported by Oleg Shevchenko (Miratech)
            contentTypeHeader = sipManCallback.headerFactory
                    .createContentTypeHeader("application", "sdp");
        }
        catch (ParseException ex) {
            // Shouldn't happen
            throw new CommunicationsException(
                    "Failed to create a content type header for the INVITE request",
                    ex);
        }
        try {
            invite.setContent(sdpContent, contentTypeHeader);
        }
        catch (ParseException ex) {
            throw new CommunicationsException(
                    "Failed to parse sdp data while creating invite request!",
                    ex);
        }
        // Transaction
        ClientTransaction inviteTransaction;
        try {
            inviteTransaction = sipManCallback.sipProvider
                    .getNewClientTransaction(invite);
        }
        catch (TransactionUnavailableException ex) {
            throw new CommunicationsException(
                    "Failed to create inviteTransaction.\n"
                            + "This is most probably a network connection error.",
                    ex);
        }
        try {
            inviteTransaction.sendRequest();
        }
        catch (SipException ex) {
            throw new CommunicationsException(
                    "An error occurred while sending invite request", ex);
        }
        Call call = callDispatcher.createCall(
                inviteTransaction.getDialog(), invite);
        call.setState(Call.DIALING);
        return call;
    }

    // Hold

    public void hold(int callID, String sdpContent)
            throws CommunicationsException {
        Call call = callDispatcher.getCall(callID);
        Request invite = null;

        invite = call.getDialog().getFirstTransaction().getRequest();

        try {
            invite = call.getDialog().createRequest(Request.INVITE);

        } catch (SipException e) {
            Log.error("hold", e);
        }

        long cseq = ((CSeq) (invite.getHeader(CSeq.NAME)))
                .getSequenceNumber() + 1;
        invite.removeHeader(CSeq.NAME);
        try {
            invite.addHeader(sipManCallback.headerFactory.createCSeqHeader(cseq, Request.INVITE));
        }
        catch (Exception e) {
            Log.error("hold", e);
        }

        invite.removeHeader(ViaHeader.NAME);

        for (ViaHeader via : sipManCallback.getLocalViaHeaders())
            invite.addHeader(via);

        ContentTypeHeader contentTypeHeader = null;
        try {
            // content type should be application/sdp (not applications)
            // reported by Oleg Shevchenko (Miratech)
            contentTypeHeader = sipManCallback.headerFactory
                    .createContentTypeHeader("application", "sdp");
        }
        catch (ParseException ex) {
            // Shouldn't happen
            throw new CommunicationsException(
                    "Failed to create a content type header for the INVITE request",
                    ex);
        }
        try {
            invite.setContent(sdpContent, contentTypeHeader);
        }
        catch (ParseException ex) {
            throw new CommunicationsException(
                    "Failed to parse sdp data while creating invite request!",
                    ex);
        }

        // Transaction
        ClientTransaction inviteTransaction;
        try {

            inviteTransaction = sipManCallback.sipProvider.getNewClientTransaction(invite);

            call.getDialog().sendRequest(inviteTransaction);

            call.setLastRequest(invite);
        }
        catch (SipException ee) {
            Log.error("hold", ee);
        }

        return;
    }

    // send DTMF
    void sendDTMF(int callID, String digit) {
        try {
            Call call = callDispatcher.getCall(callID);

            Dialog dialog = call.getDialog();
            sendNumDTMF(dialog, digit);
            //
        }
        catch (CommunicationsException e) {

        }

    } // send DTMF

    // end call
    void endCall(int callID) throws CommunicationsException {
        Call call = callDispatcher.getCall(callID);
        if (call == null) {
            throw new CommunicationsException(
                    "Could not find call with id=" + callID);
        } else {
            endCall(call);
        }

    } // end call

    void endCall(Call call) throws CommunicationsException {
        if (call == null) {
            throw new CommunicationsException(
                    "Could not find call");
        }
        Dialog dialog = call.getDialog();
        if (call.getState().equals(Call.CONNECTED)
                || call.getState().equals(Call.RECONNECTED)) {
            call.setState(Call.DISCONNECTED);
            sayBye(dialog);
        } else if (call.getState().equals(Call.DIALING)
                || call.getState().equals(Call.RINGING)) {
            if (dialog.getFirstTransaction() != null) {
                try {
                    // Someone knows about us. Let's be polite and say we
                    // are leaving
                    sayCancel(dialog);
                }
                catch (CommunicationsException ex) {
                    // something went wrong let's just tell the others
                    sipManCallback
                            .fireCommunicationsError(new CommunicationsException(
                                    "Could not send the CANCEL request! "
                                            + "Remote party won't know we're leaving!",
                                    ex));
                }
            }
            call.setState(Call.DISCONNECTED);
        } else if (call.getState().equals(Call.ALERTING)) {
            call.setState(Call.DISCONNECTED);
            sayBusyHere(dialog);
        }
        // For FAILED and BUSY we only need to update CALL_STATUS
        else if (call.getState().equals(Call.BUSY)) {
            call.setState(Call.DISCONNECTED);
        } else if (call.getState().equals(Call.FAILED)) {
            call.setState(Call.DISCONNECTED);
        } else {
            call.setState(Call.DISCONNECTED);
            throw new CommunicationsException(
                    "Could not determine call state!");
        }
    } // end call

    // send dtmf

    private void sendNumDTMF(Dialog dialog, String digit)
            throws CommunicationsException {

        Request info = null;
        String body = "Signal=" + digit + "\nDuration=160";
        String contentType = "application/dtmf-relay";
        String[] contentTypeTab = contentType.split("/");
        ContentTypeHeader contentTypeHeader = null;
        try {
            info = dialog.createRequest(Request.INFO);
            try {
                contentTypeHeader = sipManCallback.headerFactory
                        .createContentTypeHeader(contentTypeTab[0],
                                contentTypeTab[1]);
                info.setContent(body, contentTypeHeader);
            }
            catch (ParseException ex) {
                throw new CommunicationsException(
                        "ContentType Header must look like type/subtype!",
                        ex);
            }

        }
        catch (SipException ex) {
            throw new CommunicationsException(
                    "Failed to create bye request!", ex);
        }
        ClientTransaction clientTransaction = null;
        try {
            clientTransaction = sipManCallback.sipProvider
                    .getNewClientTransaction(info);
        }
        catch (TransactionUnavailableException ex) {
            throw new CommunicationsException(
                    "Failed to construct a client transaction from the INFO request",
                    ex);
        }
        try {
            dialog.sendRequest(clientTransaction);

        }
        catch (SipException ex1) {
            throw new CommunicationsException(
                    "Failed to send the INFO request");
        }
    }

    // send message
//    private void sendInfoMessage(Dialog dialog, String body)
//            throws CommunicationsException {
//        Request request = dialog.getFirstTransaction().getRequest();
//        Request info = null;
//        String contentType = "application/dtmd-relay";
//        String[] contentTypeTab = contentType.split("/");
//        ContentTypeHeader contentTypeHeader = null;
//        try {
//            info = dialog.createRequest(Request.INFO);
//            try {
//                contentTypeHeader = sipManCallback.headerFactory
//                        .createContentTypeHeader(contentTypeTab[0],
//                                contentTypeTab[1]);
//                info.setContent(body, contentTypeHeader);
//            }
//            catch (ParseException ex) {
//
//                throw new CommunicationsException(
//                        "ContentType Header must look like type/subtype!",
//                        ex);
//            }
//        }
//        catch (SipException ex) {
//
//            throw new CommunicationsException(
//                    "Failed to create bye request!", ex);
//        }
//        ClientTransaction clientTransaction = null;
//        try {
//            clientTransaction = sipManCallback.sipProvider
//                    .getNewClientTransaction(info);
//        }
//        catch (TransactionUnavailableException ex) {
//
//            throw new CommunicationsException(
//                    "Failed to construct a client transaction from the INFO request",
//                    ex);
//        }
//        try {
//            dialog.sendRequest(clientTransaction);
//        }
//        catch (SipException ex1) {
//            throw new CommunicationsException(
//                    "Failed to send the INFO request");
//        }
//
//    } // send message

    // Bye
    private void sayBye(Dialog dialog) throws CommunicationsException {
        Request bye = null;

        bye = dialog.getFirstTransaction().getRequest();

        try {
            bye = dialog.createRequest(Request.BYE);

        } catch (SipException e) {
            Log.error("bye", e);
        }

        long cseq = ((CSeq) (bye.getHeader(CSeq.NAME)))
                .getSequenceNumber() + 1;
        bye.removeHeader(CSeq.NAME);
        try {
            bye.addHeader(sipManCallback.headerFactory.createCSeqHeader(cseq, Request.BYE));
        }
        catch (Exception e) {
            Log.error("bye", e);
        }

        bye.removeHeader(ViaHeader.NAME);

        for (ViaHeader via : sipManCallback.getLocalViaHeaders())
            bye.addHeader(via);

        ContactHeader contactHeader = sipManCallback.getContactHeader();
        bye.addHeader(contactHeader);

        AllowHeader allow = null;
        try {
            allow = sipManCallback.headerFactory.createAllowHeader("INVITE, ACK, CANCEL, OPTIONS, BYE, REFER, NOTIFY, MESSAGE, SUBSCRIBE, INFO");
            bye.addHeader(allow);
        } catch (ParseException e) {
            Log.error(e);
        }

        // Transaction
        ClientTransaction inviteTransaction;
        try {

            inviteTransaction = sipManCallback.sipProvider.getNewClientTransaction(bye);

            dialog.sendRequest(inviteTransaction);

        }
        catch (SipException ee) {
            Log.error("bye", ee);
        }

        return;
    } // bye

    // cancel
    private void sayCancel(Dialog dialog) throws CommunicationsException {
        if (dialog.isServer()) {

            throw new CommunicationsException(
                    "Cannot cancel a server transaction");
        }
        ClientTransaction clientTransaction = (ClientTransaction) dialog
                .getFirstTransaction();
        try {
            Request cancel = clientTransaction.createCancel();
            ClientTransaction cancelTransaction = sipManCallback.sipProvider
                    .getNewClientTransaction(cancel);
            cancelTransaction.sendRequest();
        }
        catch (SipException ex) {

            throw new CommunicationsException(
                    "Failed to send the CANCEL request", ex);
        }
    } // cancel

    // busy here
    private void sayBusyHere(Dialog dialog) throws CommunicationsException {
        Request request = dialog.getFirstTransaction().getRequest();
        Response busyHere = null;
        try {
            busyHere = sipManCallback.messageFactory.createResponse(
                    Response.BUSY_HERE, request);
            sipManCallback.attachToTag(busyHere, dialog);
        }
        catch (ParseException ex) {

            throw new CommunicationsException(
                    "Failed to create the BUSY_HERE response!", ex);
        }
        if (!dialog.isServer()) {

            throw new CommunicationsException(
                    "Cannot send BUSY_HERE in a client transaction");
        }
        ServerTransaction serverTransaction = (ServerTransaction) dialog
                .getFirstTransaction();
        try {
            serverTransaction.sendResponse(busyHere);

        }
        catch (SipException ex) {

            throw new CommunicationsException(
                    "Failed to send the BUSY_HERE response", ex);
        } catch (InvalidArgumentException e) {
            throw new CommunicationsException(
                    "Failed to send the BUSY_HERE response", e);
        }

    } // busy here

    // ------------------ say ok
    public void sayOK(int callID, String sdpContent)
            throws CommunicationsException {
        Call call = callDispatcher.getCall(callID);
        if (call == null) {
            throw new CommunicationsException(
                    "Failed to find call with id=" + callID);
        }

        if (!call.isIncoming()) return;

        Dialog dialog = call.getDialog();
        if (dialog == null) {
            call.setState(Call.DISCONNECTED);
            throw new CommunicationsException(
                    "Failed to extract call's associated dialog! Ending Call!");
        }
        Transaction transaction = dialog.getFirstTransaction();
        if (transaction == null || !dialog.isServer()) {
            call.setState(Call.DISCONNECTED);
            throw new CommunicationsException(
                    "Failed to extract a ServerTransaction "
                            + "from the call's associated dialog!");
        }
        ServerTransaction serverTransaction = (ServerTransaction) transaction;
        Response ok = null;
        try {
            ok = sipManCallback.messageFactory.createResponse(Response.OK,
                    dialog.getFirstTransaction().getRequest());
            sipManCallback.attachToTag(ok, dialog);
        }
        catch (ParseException ex) {
            call.setState(Call.DISCONNECTED);
            throw new CommunicationsException(
                    "Failed to construct an OK response to an INVITE request",
                    ex);
        }
        // Content
        ContentTypeHeader contentTypeHeader = null;
        try {
            // content type should be application/sdp (not applications)
            // reported by Oleg Shevchenko (Miratech)
            contentTypeHeader = sipManCallback.headerFactory
                    .createContentTypeHeader("application", "sdp");
        }
        catch (ParseException ex) {
            // Shouldn't happen
            call.setState(Call.DISCONNECTED);
            throw new CommunicationsException(
                    "Failed to create a content type header for the OK request",
                    ex);
        }
        try {
            ok.setContent(sdpContent, contentTypeHeader);
        }
        catch (NullPointerException ex) {
            call.setState(Call.DISCONNECTED);
            throw new CommunicationsException(
                    "No sdp data was provided for the ok response to an INVITE request!",
                    ex);
        }
        catch (ParseException ex) {
            call.setState(Call.DISCONNECTED);
            throw new CommunicationsException(
                    "Failed to parse sdp data while creating invite request!",
                    ex);
        }
        // TODO This is here provisionally as my remote user agent that I am
        // using for
        // testing is not doing it. It is not correct from the protocol
        // point of view
        // and should probably be removed
        if (((ToHeader) ok.getHeader(ToHeader.NAME)).getTag() == null) {
            try {
                ((ToHeader) ok.getHeader(ToHeader.NAME)).setTag(Integer
                        .toString(dialog.hashCode()));
            }
            catch (ParseException ex) {
                call.setState(Call.DISCONNECTED);
                throw new CommunicationsException("Unable to set to tag",
                        ex);
            }
        }
        ContactHeader contactHeader = sipManCallback.getContactHeader();
        ok.addHeader(contactHeader);
        try {
            serverTransaction.sendResponse(ok);

        }
        catch (SipException ex) {
            call.setState(Call.DISCONNECTED);

            throw new CommunicationsException(
                    "Failed to send an OK response to an INVITE request",
                    ex);
        } catch (InvalidArgumentException e) {
            call.setState(Call.DISCONNECTED);
            sipManCallback
                    .fireCommunicationsError(new CommunicationsException(
                            "Failed to send a NOT_FOUND response to an INVITE request!",
                            e));
        }

    } // answer call

    // ------------------ Internal Error
    void sayInternalError(int callID) throws CommunicationsException {
        Call call = callDispatcher.getCall(callID);
        if (call == null) {

            throw new CommunicationsException(
                    "Failed to find call with id=" + callID);
        }
        Dialog dialog = call.getDialog();
        if (dialog == null) {
            call.setState(Call.DISCONNECTED);
            throw new CommunicationsException(
                    "Failed to extract call's associated dialog! Ending Call!");
        }
        Transaction transaction = dialog.getFirstTransaction();
        if (transaction == null || !dialog.isServer()) {
            call.setState(Call.DISCONNECTED);
            throw new CommunicationsException(
                    "Failed to extract a transaction from the call's associated dialog!");
        }
        ServerTransaction serverTransaction = (ServerTransaction) transaction;
        Response internalError = null;
        try {
            internalError = sipManCallback.messageFactory.createResponse(
                    Response.SERVER_INTERNAL_ERROR, dialog
                    .getFirstTransaction().getRequest());
            sipManCallback.attachToTag(internalError, dialog);
        }
        catch (ParseException ex) {
            call.setState(Call.DISCONNECTED);
            throw new CommunicationsException(
                    "Failed to construct an OK response to an INVITE request",
                    ex);
        }
        ContactHeader contactHeader = sipManCallback.getContactHeader();
        internalError.addHeader(contactHeader);
        try {
            serverTransaction.sendResponse(internalError);

        }
        catch (SipException ex) {
            call.setState(Call.DISCONNECTED);
            throw new CommunicationsException(
                    "Failed to send an OK response to an INVITE request",
                    ex);
        } catch (InvalidArgumentException e) {
            call.setState(Call.DISCONNECTED);
            throw new CommunicationsException(
                    "Failed to send an OK response to an INVITE request",
                    e);
        }

    } // internal error

    /**
     * @return Returns the callDispatcher.
     * @uml.property name="callDispatcher"
     */
    CallDispatcher getCallDispatcher() {
        return callDispatcher;
    }

    // The following method is currently being implemented and tested
    protected void processReInvite(ServerTransaction serverTransaction,
                                   Request invite) {
        sipManCallback.setBusy(false);

        Log.debug("REINVITE DETECTED");

        Call call = callDispatcher.findCall(serverTransaction.getDialog());
        if (call == null) {
            call = callDispatcher.createCall(serverTransaction.getDialog(),
                    invite);
        }

        call.setRemoteSdpDescription(new String(invite.getRawContent()));

        Log.debug("CALL CONNECT EVENT");

        try {
            sayOK(call.getID(), call.getLocalSdpDescription().toString());
        } catch (CommunicationsException e) {
            Log.error("Re-Invite", e);
        }

        call.setState(Call.MOVING_REMOTELY);
        call.setState(Call.CONNECTED);

    }
}
