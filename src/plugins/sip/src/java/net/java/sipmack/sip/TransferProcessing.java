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
import net.java.sipmack.common.Log;

import javax.sip.*;
import javax.sip.address.URI;
import javax.sip.header.ContactHeader;
import javax.sip.header.ReferToHeader;
import javax.sip.header.ToHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.text.ParseException;

/**
 * Title: SIPark
 * Description:JAIN-SIP Audio/Video phone application
 *
 * @author Thiago Rocha Camargo (thiago@jivesoftware.com)
 * @version 1.0, 20/07/2006
 */

public class TransferProcessing {
    protected SipManager sipManCallback = null;
    protected CallProcessing callProcessing = null;

    TransferProcessing(SipManager sipManCallback, CallProcessing callProcessing) {
        this.sipManCallback = sipManCallback;
        this.callProcessing = callProcessing;
    }

    void setSipManagerCallBack(SipManager sipManCallback) {
        this.sipManCallback = sipManCallback;
    }

    void processRefer(ServerTransaction serverTransaction, Request request) {
        System.out.println("REFER ANSWER");
    }

    public void transfer(int callID, String callee)
            throws CommunicationsException {

        Call call = callProcessing.getCallDispatcher().getCall(callID);
        Request refer = null;
        refer = call.getDialog().getFirstTransaction().getRequest();

        try {
            refer = call.getDialog().createRequest(Request.REFER);

        } catch (SipException e) {
            Log.error("hold", e);
        }

        long cseq = ((CSeq) (refer.getHeader(CSeq.NAME)))
                .getSequenceNumber() + 1;
        refer.removeHeader(CSeq.NAME);
        try {
            refer.addHeader(sipManCallback.headerFactory.createCSeqHeader(
                    cseq, Request.REFER));
        }
        catch (Exception e) {

        }

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

        // Content
        ReferToHeader referHeader = sipManCallback.headerFactory
                .createReferToHeader(sipManCallback.addressFactory.createAddress(requestURI));

        refer.addHeader(referHeader);

        refer.removeContent();

        try {
            refer.setMethod(Request.REFER.toString());
        } catch (ParseException e) {
            Log.error("transfer", e);
        }

        // Transaction
        ClientTransaction referTransaction;
        try {

            referTransaction = sipManCallback.sipProvider.getNewClientTransaction(refer);

            call.getDialog().sendRequest(referTransaction);

            call.setLastRequest(refer);
        }
        catch (SipException ee) {
            Log.error("transfer", ee);
        }

        return;

    }

    public void sayOK(int callID)
            throws CommunicationsException {
        try {

            Call call = callProcessing.getCallDispatcher().getCall(callID);
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

                throw new CommunicationsException(
                        "Failed to send an OK response to an INVITE request",
                        e);
            }
        }
        finally {

        }

    } // answer call

}
