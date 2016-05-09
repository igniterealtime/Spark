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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.sip.ClientTransaction;
import javax.sip.InvalidArgumentException;
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
import javax.sip.header.EventHeader;
import javax.sip.header.ExpiresHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.MaxForwardsHeader;
import javax.sip.header.ToHeader;
import javax.sip.header.UserAgentHeader;
import javax.sip.header.ViaHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;

import net.java.sipmack.common.Log;
import net.java.sipmack.sip.event.RegistrationEvent;
import net.java.sipmack.sip.security.SipSecurityException;
import net.java.sipmack.softphone.SoftPhoneManager;

/**
 * Title: SIPark
 * Description:JAIN-SIP Audio/Video phone application
 *
 * @author Thiago Rocha Camargo (thiago@jivesoftware.com)
 */

class RegisterProcessing {

    private SipManager sipManCallback = null;

    /**
     */
    private Request registerRequest = null;

    /**
     */
    private boolean isRegistered = false;

    private boolean isUnregistering = false;

    private Timer reRegisterTimer = null;

    // Thiago Rocha Camargo
    // Keep-Alive
    private Timer keepAliveTimer = null;

    RegisterProcessing(SipManager sipManCallback) {
        this.sipManCallback = sipManCallback;
    }

    void setSipManagerCallBack(SipManager sipManCallback) {
        this.sipManCallback = sipManCallback;
    }

    void processOK(ClientTransaction clientTransatcion, Response response) {
        isRegistered = true;
        FromHeader fromHeader = ((FromHeader) response
                .getHeader(FromHeader.NAME));
        Address address = fromHeader.getAddress();

        int expires = 0;
        if (!isUnregistering) {
            ContactHeader contactHeader = (ContactHeader) response
                    .getHeader(ContactHeader.NAME);
            // TODO check if the registrar created the contact address
            if (contactHeader != null) {
                expires = contactHeader.getExpires();
            } else {
                ExpiresHeader expiresHeader = response.getExpires();
                if (expiresHeader != null) {
                    expires = expiresHeader.getExpires();
                }
            }
        }
        // expires may be null
        // fix by Luca Bincoletto <Luca.Bincoletto@tilab.com>
        if (expires == 0) {
            isUnregistering = false;
            sipManCallback.fireUnregistered(address.toString());
        } else {
            if (reRegisterTimer != null)
                reRegisterTimer.cancel();
            if (keepAliveTimer != null)
                keepAliveTimer.cancel();

            reRegisterTimer = new Timer();
            keepAliveTimer = new Timer();
            // if (expires > 0 && expires < 60) {
            // [issue 2] Schedule re registrations
            // bug reported by LynlvL@netscape.com
            // use the value returned by the server to reschedule
            // registration
            SipURI uri = (SipURI) address.getURI();
            scheduleReRegistration(uri.getHost(), uri.getPort(), uri
                    .getTransportParam(), expires);
            scheduleKeepAlive(SIPConfig.getKeepAliveDelay());
            // }
            /*
            * else{ SipURI uri = (SipURI) address.getURI();
            * scheduleReRegistration(uri.getHost(), uri.getPort(),
            * uri.getTransportParam(), expires); }
            */
            sipManCallback.fireRegistered(address.toString());

        }
    }

    void processTimeout(Transaction transaction, Request request) {
        System.err.println("TIME ERROR");
        isRegistered = true;
        FromHeader fromHeader = ((FromHeader) request.getHeader(FromHeader.NAME));
        Address address = fromHeader.getAddress();
        sipManCallback.fireRegistrationFailed("Unable to register with " + address.toString(), RegistrationEvent.Type.TimeOut);
    }

    void processNotImplemented(ClientTransaction transatcion, Response response) {
        isRegistered = true;
        FromHeader fromHeader = ((FromHeader) response
                .getHeader(FromHeader.NAME));
        Address address = fromHeader.getAddress();
        sipManCallback.fireUnregistered("Server returned NOT_IMPLEMENTED. "
                + address.toString());
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

            return;
        }
        catch (SipSecurityException exc) {
            // tell the others we couldn't register
            sipManCallback.fireUnregistered(((FromHeader) clientTransaction
                    .getRequest().getHeader(FromHeader.NAME)).getAddress()
                    .toString());
            sipManCallback.fireCommunicationsError(new CommunicationsException(
                    "Authorization failed!", exc));
        }
        catch (Exception exc) {
            // tell the others we couldn't register
            sipManCallback.fireUnregistered(((FromHeader) clientTransaction
                    .getRequest().getHeader(FromHeader.NAME)).getAddress()
                    .toString());
            sipManCallback.fireCommunicationsError(new CommunicationsException(
                    "Failed to resend a request "
                            + "after a security challenge!", exc));
        }
    }

    synchronized void register(String registrarAddress, int registrarPort, String registrarTransport, int expires) throws CommunicationsException {
        try {
            isUnregistering = false;
            // From
            FromHeader fromHeader = sipManCallback.getFromHeader(true);
            Address fromAddress = fromHeader.getAddress();
            sipManCallback.fireRegistering(fromAddress.toString());
            // Request URI
            SipURI requestURI = null;
            try {
                requestURI = sipManCallback.addressFactory.createSipURI(null,
                        registrarAddress);
            }
            catch (ParseException ex) {

                throw new CommunicationsException("Bad registrar address:"
                        + registrarAddress, ex);
            }
            catch (NullPointerException ex) {
                // Do not throw an exc, we should rather silently notify the
                // user
                // throw new CommunicationsException("A registrar address was
                // not specified!", ex);
                sipManCallback.fireUnregistered(fromAddress.getURI().toString()
                        + " (registrar not specified)");
                return;
            }
            /*
            requestURI.setPort(registrarPort);
            try {
                requestURI.setTransportParam(registrarTransport);
            }
            catch (ParseException ex) {
                throw new CommunicationsException(registrarTransport
                        + " is not a valid transport!", ex);
            }
            */
            // Call ID Header
            CallIdHeader callIdHeader = sipManCallback.sipProvider
                    .getNewCallId();
            // CSeq Header
            CSeqHeader cSeqHeader = null;
            try {
                cSeqHeader = sipManCallback.headerFactory.createCSeqHeader((long) 1,
                        Request.REGISTER);
            }
            catch (ParseException ex) {
                // Should never happen

                Log.error("register", ex);

            }
            catch (InvalidArgumentException ex) {
                // Should never happen

                Log.error("register", ex);

            }
            // To Header
            ToHeader toHeader = null;
            try {
                toHeader = sipManCallback.headerFactory.createToHeader(
                        fromAddress, null);
            }
            catch (ParseException ex) {
                // throw was missing - reported by Eero Vaarnas
                throw new CommunicationsException(
                        "Could not create a To header " + "for address:"
                                + fromHeader.getAddress(), ex);
            }
            // User Agent Header
            UserAgentHeader uaHeader = null;
            ArrayList<String> userAgentList = new ArrayList<String>();
            userAgentList.add(SoftPhoneManager.userAgent);

            try {
                uaHeader = sipManCallback.headerFactory
                        .createUserAgentHeader(userAgentList);
            }
            catch (ParseException ex) {
                // throw was missing - reported by Eero Vaarnas
                throw new CommunicationsException(
                        "Could not create a To header " + "for address:"
                                + fromHeader.getAddress(), ex);
            }

            // Via Headers
            ArrayList<ViaHeader> viaHeaders = sipManCallback.getLocalViaHeaders();
            // MaxForwardsHeader
            MaxForwardsHeader maxForwardsHeader = sipManCallback
                    .getMaxForwardsHeader();
            // Request
            Request request = null;
            try {
                request = sipManCallback.messageFactory.createRequest(
                        requestURI, Request.REGISTER, callIdHeader, cSeqHeader,
                        fromHeader, toHeader, viaHeaders, maxForwardsHeader);
                request.setHeader(uaHeader);
            }
            catch (ParseException ex) {

                // throw was missing - reported by Eero Vaarnas
                throw new CommunicationsException(
                        "Could not create the register request!", ex);
            }
            // Expires Header
            ExpiresHeader expHeader = null;
            for (int retry = 0; retry < 2; retry++) {
                try {
                    expHeader = sipManCallback.headerFactory
                            .createExpiresHeader(expires);
                }
                catch (InvalidArgumentException ex) {
                    if (retry == 0) {
                        expires = 3600;
                        continue;
                    }
                    throw new CommunicationsException(
                            "Invalid registrations expiration parameter - "
                                    + expires, ex);
                }
            }
            request.addHeader(expHeader);
            // Contact Header should contain IP - bug report - Eero Vaarnas
            ContactHeader contactHeader = sipManCallback
                    .getRegistrationContactHeader();
            request.addHeader(contactHeader);
            AllowHeader allow = sipManCallback.headerFactory.createAllowHeader("INVITE, ACK, CANCEL, OPTIONS, BYE, REFER, NOTIFY, MESSAGE, SUBSCRIBE, INFO");
            request.addHeader(allow);
            // Transaction
            ClientTransaction regTrans = null;
            try {
                regTrans = sipManCallback.sipProvider
                        .getNewClientTransaction(request);
            }
            catch (TransactionUnavailableException ex) {
                throw new CommunicationsException(
                        "Could not create a register transaction!\n"
                                + "Check that the Registrar address is correct!");
            }
            try {
                regTrans.sendRequest();
            }
            // we sometimes get a null pointer exception here so catch them all
            catch (Exception ex) {

                // throw was missing - reported by Eero Vaarnas
                throw new CommunicationsException(
                        "Could not send out the register request!", ex);
            }
            this.registerRequest = request;
        }
        catch (Exception e) {
            Log.error("register", e);
            sipManCallback.fireRegistrationFailed(registrarAddress == null ? "" : registrarAddress, RegistrationEvent.Type.TimeOut);
        }
    }

    /**
     * Synchronize because of timer tasks
     *
     * @throws CommunicationsException
     */
    synchronized void unregister() throws CommunicationsException {
        try {

            Log.debug("UNREGISTER");

            if (!isRegistered) {
                return;
            }

            cancelPendingRegistrations();
            isRegistered = false;
            isUnregistering = true;

            Request registerRequest = getRegisterRequest();
            if (this.registerRequest == null) {

                throw new CommunicationsException(
                        "Couldn't find the initial register request");
            }
            
            
            Request unregisterRequest = (Request) registerRequest.clone();
            try {
            	 CallIdHeader callIdHeader = sipManCallback.sipProvider.getNewCallId(); 
            	 ((CallIdHeader)unregisterRequest.getHeader(CallIdHeader.NAME)).setCallId(callIdHeader.getCallId());
            	 
                unregisterRequest.getExpires().setExpires(0);
                CSeqHeader cSeqHeader = (CSeqHeader) unregisterRequest
                        .getHeader(CSeqHeader.NAME);
                // [issue 1] - increment registration cseq number
                // reported by - Roberto Tealdi <roby.tea@tin.it>
                cSeqHeader
                        .setSeqNumber(cSeqHeader.getSeqNumber() + 1);

            }
            catch (InvalidArgumentException ex) {

                // Shouldn't happen
                throw new CommunicationsException(
                        "Unable to set Expires Header", ex);
            }
            ClientTransaction unregisterTransaction = null;
            try {
                unregisterTransaction = sipManCallback.sipProvider
                        .getNewClientTransaction(unregisterRequest);
            }
            catch (TransactionUnavailableException ex) {
                throw new CommunicationsException(
                        "Unable to create a unregister transaction", ex);
            }
            try {
                sipManCallback
                        .fireUnregistering(sipManCallback.currentlyUsedURI);
                unregisterTransaction.sendRequest();
            }
            catch (SipException ex) {
                throw new CommunicationsException(
                        "Failed to send unregister request", ex);
            }
        }
        catch (Exception e) {
            Log.error("unregister", e);
        }
    }

    /**
     * Synchronize because of timer tasks
     *
     * @throws CommunicationsException
     */
    synchronized void subscribe(String registrarAddress, int registrarPort, String registrarTransport) throws CommunicationsException {
        FromHeader fromHeader = sipManCallback.getFromHeader(true);
        Address fromAddress = fromHeader.getAddress();
        // Request URI
        URI requestURI = null;
        try {
            requestURI = sipManCallback.addressFactory.createSipURI(sipManCallback.getCurrentUsername(), registrarAddress);
        } catch (ParseException e) {
            Log.error(e);
        }

        // Call ID Header
        CallIdHeader callIdHeader = sipManCallback.sipProvider
                .getNewCallId();
        // CSeq Header
        CSeqHeader cSeqHeader = null;
        try {
            cSeqHeader = sipManCallback.headerFactory.createCSeqHeader((long) 1,
                    Request.SUBSCRIBE);
        }
        catch (ParseException ex) {
            // Should never happen

            Log.error("register", ex);

        }
        catch (InvalidArgumentException ex) {
            // Should never happen

            Log.error("register", ex);

        }
        // To Header
        ToHeader toHeader = null;
        try {
            toHeader = sipManCallback.headerFactory.createToHeader(
                    fromAddress, null);
        }
        catch (ParseException ex) {
            // throw was missing - reported by Eero Vaarnas
            throw new CommunicationsException(
                    "Could not create a To header " + "for address:"
                            + fromHeader.getAddress(), ex);
        }
        // User Agent Header
        UserAgentHeader uaHeader = null;
        ArrayList<String> userAgentList = new ArrayList<String>();
        userAgentList.add(SoftPhoneManager.userAgent);

        try {
            uaHeader = sipManCallback.headerFactory
                    .createUserAgentHeader(userAgentList);
        }
        catch (ParseException ex) {
            // throw was missing - reported by Eero Vaarnas
            throw new CommunicationsException(
                    "Could not create a To header " + "for address:"
                            + fromHeader.getAddress(), ex);
        }

        // Via Headers
        ArrayList<ViaHeader> viaHeaders = sipManCallback.getLocalViaHeaders();
        // MaxForwardsHeader
        MaxForwardsHeader maxForwardsHeader = sipManCallback
                .getMaxForwardsHeader();
        // Request
        Request request = null;
        try {
            request = sipManCallback.messageFactory.createRequest(
                    requestURI, Request.SUBSCRIBE, callIdHeader, cSeqHeader,
                    fromHeader, toHeader, viaHeaders, maxForwardsHeader);
            request.setHeader(uaHeader);
        }
        catch (ParseException ex) {
            throw new CommunicationsException(
                    "Could not create the register request!", ex);
        }
        // Expires Header
        ExpiresHeader expHeader = null;
        try {
            expHeader = sipManCallback.headerFactory.createExpiresHeader(3000);
        }
        catch (InvalidArgumentException ex) {
        }
        request.addHeader(expHeader);

        try {
            EventHeader event = sipManCallback.headerFactory.createEventHeader("message-summary");
            request.addHeader(event);
        } catch (ParseException e) {
            Log.error(e);
        }

        // Contact Header should contain IP - bug report - Eero Vaarnas
        ContactHeader contactHeader = sipManCallback
                .getRegistrationContactHeader();
        request.addHeader(contactHeader);
        AllowHeader allow = null;
        try {
            allow = sipManCallback.headerFactory.createAllowHeader("INVITE, ACK, CANCEL, OPTIONS, BYE, REFER, NOTIFY, MESSAGE, SUBSCRIBE, INFO");
            request.addHeader(allow);
        } catch (ParseException e) {
            Log.error(e);
        }

        // Transaction
        ClientTransaction regTrans = null;
        try {
            regTrans = sipManCallback.sipProvider
                    .getNewClientTransaction(request);
        }
        catch (TransactionUnavailableException ex) {
            throw new CommunicationsException(
                    "Could not create a register transaction!\n"
                            + "Check that the Registrar address is correct!");
        }
        try {
            regTrans.sendRequest();
        }
        // we sometimes get a null pointer exception here so catch them all
        catch (Exception ex) {

            // throw was missing - reported by Eero Vaarnas
            throw new CommunicationsException(
                    "Could not send out the register request!", ex);
        }
    }

    public void cancelSchedules() {

        if (reRegisterTimer != null)
            reRegisterTimer.cancel();
        if (keepAliveTimer != null)
            keepAliveTimer.cancel();

        reRegisterTimer = null;
        keepAliveTimer = null;

    }

    /**
     * @return
     */
    boolean isRegistered() {
        return isRegistered;
    }

    /**
     * @return Returns the registerRequest.
     */
    private Request getRegisterRequest() {
        return registerRequest;
    }

    private class ReRegisterTask extends TimerTask {
        String registrarAddress = null;

        int registrarPort = -1;

        String transport = null;

        int expires = 0;

        public ReRegisterTask(String registrarAddress, int registrarPort,
                              String registrarTransport, int expires) {
            this.registrarAddress = registrarAddress;
            this.registrarPort = registrarPort;

            // don't do this.transport = transport ;)
            // bug report and fix by Willem Romijn (romijn at lucent.com)
            this.transport = registrarTransport;
            this.expires = expires;
        }

        public void run() {
            try {
                if (isRegistered())
                    register(registrarAddress, registrarPort, transport,
                            expires);
            }
            catch (CommunicationsException ex) {
                sipManCallback
                        .fireCommunicationsError(new CommunicationsException(
                                "Failed to reRegister", ex));
            }
        }
    }

    private class KeepAliveTask extends TimerTask {

        public KeepAliveTask(int s) {
        }
       
        public void run() {
            try {
                if (sipManCallback.isRegistered()) {
                    try {
                        sipManCallback.messageProcessing.sendKeepAlive();// .sendMessage("","0".getBytes(),"text/plain",null);
                    }
                    catch (Exception e) {
                        Log.error("KeepAliveTask", e);
                    }
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
    }

    private void cancelPendingRegistrations() {
        try {

            if (reRegisterTimer != null)
                reRegisterTimer.cancel();
            if (keepAliveTimer != null)
                keepAliveTimer.cancel();

            reRegisterTimer = null;
            keepAliveTimer = null;

        }
        catch (Exception e) {
            Log.error("cancelPendingRegistrations", e);
        }
    }

    private void scheduleKeepAlive(int sec) {
        KeepAliveTask kat = new KeepAliveTask(sec);
        if (keepAliveTimer != null)
            keepAliveTimer.schedule(kat, sec * 1000, sec * 1000);
    }

    private void scheduleReRegistration(String registrarAddress,
                                        int registrarPort, String registrarTransport, int expires) {

        ReRegisterTask reRegisterTask = new ReRegisterTask(
                registrarAddress, registrarPort, registrarTransport,
                expires);

        // java.util.Timer thinks in miliseconds
        // bug report and fix by Willem Romijn (romijn at lucent.com)
        // We keep a margin of 10% when sending re-registrations (1000
        // becomes 900)
        expires = expires * 900;

        reRegisterTimer.schedule(reRegisterTask, expires);

    }
}
