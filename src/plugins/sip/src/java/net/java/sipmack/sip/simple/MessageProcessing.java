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
package net.java.sipmack.sip.simple;

import java.text.ParseException;
import java.util.ArrayList;

import javax.sip.ClientTransaction;
import javax.sip.InvalidArgumentException;
import javax.sip.ServerTransaction;
import javax.sip.SipException;
import javax.sip.TransactionUnavailableException;
import javax.sip.address.Address;
import javax.sip.address.URI;
import javax.sip.header.CSeqHeader;
import javax.sip.header.CallIdHeader;
import javax.sip.header.ContentLengthHeader;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.MaxForwardsHeader;
import javax.sip.header.ToHeader;
import javax.sip.header.ViaHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;

import net.java.sipmack.common.Log;
import net.java.sipmack.sip.CommunicationsException;
import net.java.sipmack.sip.SIPConfig;
import net.java.sipmack.sip.SipManager;
import net.java.sipmack.sip.security.SipSecurityException;


public class MessageProcessing {
    protected SipManager sipManCallback = null;

    public MessageProcessing() {
    }

    public MessageProcessing(SipManager sipManCallback) {
        this.sipManCallback = sipManCallback;
    }

    public void setSipManagerCallBack(SipManager sipManCallback) {
        this.sipManCallback = sipManCallback;
    }

    /**
     * Attempts to re-generate the corresponding request with the proper
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

            retryTran.sendRequest();
        }
        catch (SipSecurityException exc) {
            sipManCallback.fireCommunicationsError(new CommunicationsException(
                    "Authorization failed!", exc));
        }
        catch (Exception exc) {
            sipManCallback.fireCommunicationsError(new CommunicationsException(
                    "Failed to resend a request "
                            + "after a security challenge!", exc));
        }
        finally {

        }
    }

    /**
     * Process MESSAGE requests and send OK response.
     *
     * @param serverTransaction
     * @param request
     */
    public void processMessageRequest(ServerTransaction serverTransaction,
                                      Request request) {
        try {

            // Send OK
            Response ok = null;
            try {
                ok = sipManCallback.messageFactory.createResponse(Response.OK,
                        request);
                // sipManCallback.attachToTag(ok,
                // serverTransaction.getDialog());
            }
            catch (ParseException ex) {
                sipManCallback
                        .fireCommunicationsError(new CommunicationsException(
                                "Failed to construct an OK response to a MESSAGE request!",
                                ex));
                return;
            }
            try {
                serverTransaction.sendResponse(ok);

            }
            catch (SipException ex) {
                // This is not really a problem according to the RFC
                // so just dump to stdout should someone be interested
            } catch (InvalidArgumentException e) {

            }
        }
        finally {

        }
    }

    public void sendKeepAlive() throws CommunicationsException {

        String to = "";
        byte[] messageBody = "".getBytes();

        try {

            to = to.trim();
            // Handle default domain name (i.e. transform 1234 -> 1234@sip.com
            String defaultDomainName = SIPConfig.getDefaultDomain();
            if (defaultDomainName != null // no sip scheme
                    && !to.trim().startsWith("tel:") && to.indexOf('@') == -1) {
                to = to + "@" + defaultDomainName;
            }

            // Let's be uri fault tolerant
            if (to.toLowerCase().indexOf("sip:") == -1 // no sip scheme
                    && to.indexOf('@') != -1 // most probably a sip uri
                    ) {
                to = "sip:" + to;
            }

            // Request URI
            URI requestURI;
            try {
                requestURI = sipManCallback.addressFactory.createURI(to);
            }
            catch (ParseException ex) {

                throw new CommunicationsException(to
                        + " is not a legal SIP uri!", ex);
            }
            // Call ID
            CallIdHeader callIdHeader = sipManCallback.sipProvider
                    .getNewCallId();
            // CSeq
            CSeqHeader cSeqHeader;
            try {
                cSeqHeader = sipManCallback.headerFactory.createCSeqHeader(1L,
                        Request.MESSAGE);
            }
            catch (Exception ex) {
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

            ContentLengthHeader contentLengthHeader = null;
            try {
                contentLengthHeader = sipManCallback.headerFactory
                        .createContentLengthHeader(messageBody.length);
            }
            catch (InvalidArgumentException ex) {
                throw new CommunicationsException(
                        "Cseq Header must contain a integer value!", ex);
            }

            //ExpiresHeader expiresHeader = null;
            try {
                sipManCallback.headerFactory.createExpiresHeader(30);
            }
            catch (InvalidArgumentException ex) {
                throw new CommunicationsException(
                        "Expires Header must be an integer!", ex);
            }

            String contentType = "text/plain";
            ContentTypeHeader contentTypeHeader = null;
            try {
                String[] contentTypeTab = contentType.split("/");
                contentTypeHeader = sipManCallback.headerFactory
                        .createContentTypeHeader(contentTypeTab[0],
                                contentTypeTab[1]);
            }
            catch (ParseException ex) {
                throw new CommunicationsException(
                        "ContentType Header must look like type/subtype!", ex);
            }

            // ViaHeaders
            ArrayList<ViaHeader> viaHeaders = sipManCallback.getLocalViaHeaders();
            // MaxForwards
            MaxForwardsHeader maxForwards = sipManCallback
                    .getMaxForwardsHeader();
            Request message = null;
            try {
                message = sipManCallback.messageFactory.createRequest(
                        requestURI, Request.MESSAGE, callIdHeader, cSeqHeader,
                        fromHeader, toHeader, viaHeaders, maxForwards);
                message.setContentLength(contentLengthHeader);
                message.setContent(messageBody, contentTypeHeader);
                // message.addHeader(eventHeader);
            }
            catch (Exception e) {
                Log.error("sendKeepAlive", e);
            }

            //ClientTransaction messageTransaction = null;
            //String subscriber = sipManCallback.getFromHeader().getAddress()
            //        .getURI().toString();

            try {
                sipManCallback.sipProvider.getNewClientTransaction(message);
            } catch (TransactionUnavailableException e) {
                e.printStackTrace();
            }

            try {
            	// TODO : Anpassen
                //((SIPClientTransaction) messageTransaction).sendRequest("\0".getBytes(), InetAddress.getByName(SIPConfig.getDefaultDomain()), SIPConfig.getRegistrarPort());
            }
            catch (Exception e) {
                Log.error("sendKeepAlive", e);
            }

        }
        finally {
            try {
                this.finalize();
            }
            catch (Throwable e) {
            }
        }

    }

    /**
     * Sends an instant message in pager-mode using a SIMPLE/SIP MESSAGE
     * request. In pager-mode, each message is independent of any other
     * messages. An instant message will be the body of the MESSAGE request to
     * be sent, therefore, its format must conform to the values in the
     * "Content-Type" and "Content-Encoding" header fields. Refer to Message for
     * details.
     *
     * @param to              the address of receiver.
     * @param messageBody     the message to be sent. The messageBody will be the body of
     *                        the MESSAGE request to be sent and its format must conform to
     *                        the values in the parameters contentType and contentEncoding.
     *                        Please refer to the setBody method for details.
     * @param contentType     the Internet media type of the messageBody. Please refer to
     *                        the Message.setBody method for details.
     * @param contentEncoding the encodings that have been applied to the messageBody in
     *                        addition to those specified by contentType. Please refer to
     *                        the Message.setBody method for details.
     * @return the transaction ID associated with the MESSAGE request sent by
     *         this method.
     * @throws CommunicationsException
     */
    public java.lang.String sendMessage(java.lang.String to,
                                        byte[] messageBody, java.lang.String contentType,
                                        java.lang.String contentEncoding) throws CommunicationsException {
        try {

            to = to.trim();
            // Handle default domain name (i.e. transform 1234 -> 1234@sip.com
            String defaultDomainName = SIPConfig.getDefaultDomain();

            if (defaultDomainName != null // no sip scheme
                    && !to.trim().startsWith("tel:") && to.indexOf('@') == -1 // most
                // probably
                // a
                // sip
                // uri
                    ) {
                to = to + "@" + defaultDomainName;
            }

            // Let's be uri fault tolerant
            if (to.toLowerCase().indexOf("sip:") == -1 // no sip scheme
                    && to.indexOf('@') != -1 // most probably a sip uri
                    ) {
                to = "sip:" + to;
            }

            // Request URI
            URI requestURI;
            try {
                requestURI = sipManCallback.addressFactory.createURI(to);
            }
            catch (ParseException ex) {

                throw new CommunicationsException(to
                        + " is not a legal SIP uri!", ex);
            }
            // Call ID
            CallIdHeader callIdHeader = sipManCallback.sipProvider
                    .getNewCallId();
            // CSeq
            CSeqHeader cSeqHeader;
            try {
                cSeqHeader = sipManCallback.headerFactory.createCSeqHeader(1L,
                        Request.MESSAGE);
            }
            catch (Exception ex) {
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

            ContentTypeHeader contentTypeHeader = null;
            try {
                String[] contentTypeTab = contentType.split("/");
                contentTypeHeader = sipManCallback.headerFactory
                        .createContentTypeHeader(contentTypeTab[0],
                                contentTypeTab[1]);
            }
            catch (ParseException ex) {
                throw new CommunicationsException(
                        "ContentType Header must look like type/subtype!", ex);
            }

            ContentLengthHeader contentLengthHeader = null;
            try {
                contentLengthHeader = sipManCallback.headerFactory
                        .createContentLengthHeader(messageBody.length);
            }
            catch (InvalidArgumentException ex) {
                throw new CommunicationsException(
                        "Cseq Header must contain a integer value!", ex);
            }

            /*
                * EventHeader eventHeader = null; try { eventHeader =
                * sipManCallback.headerFactory.createEventHeader("presence"); }
                * catch (ParseException ex) { //Shouldn't happen console.error(
                * "Unable to create event header!", ex); throw new
                * CommunicationsException( "Unable to create event header!", ex); }
                */

            //ExpiresHeader expiresHeader = null;
            try {
                sipManCallback.headerFactory.createExpiresHeader(30);
            }
            catch (InvalidArgumentException ex) {
                throw new CommunicationsException(
                        "Expires Header must be an integer!", ex);
            }

            // ViaHeaders
            ArrayList<ViaHeader> viaHeaders = sipManCallback.getLocalViaHeaders();
            // MaxForwards
            MaxForwardsHeader maxForwards = sipManCallback
                    .getMaxForwardsHeader();
            Request message = null;
            try {
                message = sipManCallback.messageFactory.createRequest(
                        requestURI, Request.MESSAGE, callIdHeader, cSeqHeader,
                        fromHeader, toHeader, viaHeaders, maxForwards);
                message.setContent(messageBody, contentTypeHeader);
                message.setContentLength(contentLengthHeader);
                // message.addHeader(eventHeader);
            }
            catch (ParseException ex) {
                throw new CommunicationsException(
                        "Failed to create message Request!", ex);
            }

            ClientTransaction messageTransaction = null;
            //String subscriber = sipManCallback.getFromHeader().getAddress()
            //        .getURI().toString();

            try {
                messageTransaction = sipManCallback.sipProvider
                        .getNewClientTransaction(message);
                //
            }
            catch (TransactionUnavailableException ex) {
                throw new CommunicationsException(
                        "Failed to create messageTransaction.", ex);
            }

            try {
                messageTransaction.sendRequest();

            }
            catch (SipException ex) {
                throw new CommunicationsException(
                        "An error occurred while sending message request", ex);
            }

            return messageTransaction.toString();

        }
        finally {

        }
    }

}
