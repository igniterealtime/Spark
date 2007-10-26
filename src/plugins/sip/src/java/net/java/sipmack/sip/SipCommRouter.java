/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 * Portions of this software are based upon public domain software
 * originally written at the National Center for Supercomputing Applications,
 * University of Illinois, Urbana-Champaign.
 */

package net.java.sipmack.sip;

import net.java.sipmack.common.Log;

import javax.sip.SipStack;
import javax.sip.SipException;
import javax.sip.address.Hop;
import javax.sip.address.Router;
import javax.sip.address.SipURI;
import javax.sip.address.URI;
import javax.sip.header.RouteHeader;
import javax.sip.message.Request;

import java.util.LinkedList;
import java.util.ListIterator;

/**
 * <p/>
 * Title: SIP COMMUNICATOR
 * </p>
 * <p/>
 * Description:JAIN-SIP Audio/Video phone application
 * </p>
 * <p/>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p/>
 * Organisation: LSIIT laboratory (http://lsiit.u-strasbg.fr)
 * </p>
 * <p/>
 * Network Research Team (http://www-r2.u-strasbg.fr))
 * </p>
 * <p/>
 * Louis Pasteur University - Strasbourg - France
 * </p>
 *
 * @author Emil Ivov (http://www.emcho.com)
 * @version 1.1
 */
public class SipCommRouter implements Router {
    protected SipStack myStack;

    protected SipCommHop outboundProxy = null;

    protected Router stackRouter = null;

    public SipCommRouter(SipStack sipStack, String outboundProxy) {
        outboundProxy = SIPConfig.getOutboundProxy();
        this.myStack = sipStack;
        if (outboundProxy != null && outboundProxy.length() > 0) {
            this.outboundProxy = new SipCommHop(outboundProxy);
        }
    }

    /**
     * Return the default address to forward the request to. The list is
     * organized in the following priority.
     * <p/>
     * If the outboung proxy has been specified, then it is used to construct
     * the first element of the list.
     * <p/>
     * If the requestURI refers directly to a host, the host and port
     * information are extracted from it and made the next hop on the list.
     *
     * @param sipRequest is the sip request to route.
     */
    public ListIterator getNextHops(Request sipRequest) {

        URI requestURI = sipRequest.getRequestURI();
        if (requestURI == null) {
            throw new IllegalArgumentException("Bad message: Null requestURI");
        }
        LinkedList hops = new LinkedList();
        if (outboundProxy != null) {
            hops.add(outboundProxy);
        }
        ListIterator routes = sipRequest.getHeaders(RouteHeader.NAME);
        if (routes != null && routes.hasNext()) {
            while (routes.hasNext()) {
                RouteHeader route = (RouteHeader) routes.next();
                SipURI uri = (SipURI) route.getAddress().getURI();
                int port = uri.getPort();
                port = (port == -1) ? 5060 : port;
                String host = uri.getHost();
                Log.debug("getNextHops", host);
                String transport = uri.getTransportParam();
                if (transport == null) {
                    transport = "udp";
                }
                Hop hop = new SipCommHop(host + ':' + port + '/' + transport);
                hops.add(hop);
            }
        } else if (requestURI instanceof SipURI
                && ((SipURI) requestURI).getMAddrParam() != null) {
            SipURI sipURI = ((SipURI) requestURI);
            String maddr = sipURI.getMAddrParam();
            String transport = sipURI.getTransportParam();
            if (transport == null) {
                transport = "udp";
            }
            int port = 5060;
            Hop hop = new SipCommHop(maddr, port, transport);
            hops.add(hop);
        } else if (requestURI instanceof SipURI) {
            SipURI sipURI = ((SipURI) requestURI);
            int port = sipURI.getPort();
            if (port == -1) {
                port = 5060;
            }
            String host = sipURI.getHost();
            String transport = sipURI.getTransportParam();
            if (transport == null) {
                transport = "UDP";
            }
            Hop hop = new SipCommHop(host + ":" + port + "/" + transport);
            hops.add(hop);
        } else {
            throw new IllegalArgumentException("Malformed requestURI");
        }
        return (hops.size() == 0) ? null : hops.listIterator();
    }

    /**
     * @return Returns the outboundProxy.
     */
    public Hop getOutboundProxy() {
        return this.outboundProxy;
    }

    protected void setOutboundProxy(String proxy) {
        if (SIPConfig.getOutboundProxy() != null
                && SIPConfig.getOutboundProxy().length() > 0) {
            this.outboundProxy = new SipCommHop(proxy);
        }
    }

    public Hop getNextHop(Request request) throws SipException {
        URI requestURI = request.getRequestURI();
        if (requestURI == null) {
            throw new IllegalArgumentException("Bad message: Null requestURI");
        }
        if (outboundProxy != null) {
            return outboundProxy;
        }
        ListIterator routes = request.getHeaders(RouteHeader.NAME);
        if (routes != null && routes.hasNext()) {
            while (routes.hasNext()) {
                RouteHeader route = (RouteHeader) routes.next();
                SipURI uri = (SipURI) route.getAddress().getURI();
                int port = uri.getPort();
                port = (port == -1) ? 5060 : port;
                String host = uri.getHost();
                Log.debug("getNextHops", host);
                String transport = uri.getTransportParam();
                if (transport == null) {
                    transport = "udp";
                }
                Hop hop = new SipCommHop(host + ':' + port + '/' + transport);
                return hop;
            }
        } else if (requestURI instanceof SipURI
                && ((SipURI) requestURI).getMAddrParam() != null) {
            SipURI sipURI = ((SipURI) requestURI);
            String maddr = sipURI.getMAddrParam();
            String transport = sipURI.getTransportParam();
            if (transport == null) {
                transport = "udp";
            }
            int port = 5060;
            Hop hop = new SipCommHop(maddr, port, transport);
            return hop;
        } else if (requestURI instanceof SipURI) {
            SipURI sipURI = ((SipURI) requestURI);
            int port = sipURI.getPort();
            if (port == -1) {
                port = 5060;
            }
            String host = sipURI.getHost();
            String transport = sipURI.getTransportParam();
            if (transport == null) {
                transport = "UDP";
            }
            Hop hop = new SipCommHop(host + ":" + port + "/" + transport);
            return hop;
        } else {
            throw new IllegalArgumentException("Malformed requestURI");
        }
        return null;
    }
}