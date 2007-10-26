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

import java.util.StringTokenizer;

/**
 * Routing algorithms return a list of hops to which the request is routed.
 *
 * @author M. Ranganathan <mranga@nist.gov> <br/> <a href=" {@docRoot}
 *         /uncopyright.html">This code is in the public domain.</a> IPv6
 *         Support added by Emil Ivov (emil_ivov (at) yahoo (dot) com)<br/>
 *         Network Research Team (http://www-r2.u-strasbg.fr))<br/> Louis
 *         Pasteur University - Strasbourg - France<br/>
 * @version JAIN-SIP-1.1
 */
public class SipCommHop extends Object implements javax.sip.address.Hop {
    /**
     */
    protected String host;

    /**
     */
    protected int port;

    /**
     */
    protected String transport;

    /**
     */
    protected boolean explicitRoute; // this is generated from a ROUTE
    // header.

    /**
     */
    protected boolean defaultRoute; // This is generated from the proxy addr

    protected boolean uriRoute; // This is extracted from the requestURI.

    /**
     * Debugging println.
     */
    public String toString() {
        return host + ":" + port + "/" + transport;
    }

    /**
     * Create new hop given host, port and transport.
     *
     * @param hostName   hostname
     * @param portNumber port
     * @param trans      transport
     */
    public SipCommHop(String hostName, int portNumber, String trans) {
        host = hostName;
        port = portNumber;
        transport = trans;
    }

    /**
     * Creates new Hop
     *
     * @param hop is a hop string in the form of host:port/Transport
     * @throws IllegalArgument exception if string is not properly formatted or null.
     */
    public SipCommHop(String hop) throws IllegalArgumentException {
        if (hop == null) {
            Log.debug("SipCommHop", "Null arg: " + hop);
            throw new IllegalArgumentException("Null arg!");
        }
        StringTokenizer stringTokenizer = new StringTokenizer(hop + "/");
        String hostPort = stringTokenizer.nextToken("/").trim();
        transport = stringTokenizer.nextToken().trim();

        if (transport == null) {
            transport = "UDP";
        }
        else if (transport == "") {
            transport = "UDP";
        }
        if (transport.compareToIgnoreCase("UDP") != 0
                && transport.compareToIgnoreCase("TCP") != 0) {
            throw new IllegalArgumentException(hop);
        }
        String portString = null;
        // IPv6 hostport
        if (hostPort.charAt(0) == '[') {
            int rightSqBrackIndex = hostPort.indexOf(']');
            if (rightSqBrackIndex == -1) {
                throw new IllegalArgumentException("Bad IPv6 reference spec");
            }
            host = hostPort.substring(0, rightSqBrackIndex + 1);
            int portColon = hostPort.indexOf(':', rightSqBrackIndex);
            if (portColon != -1) {
                try {
                    portString = hostPort.substring(portColon + 1).trim();
                }
                catch (IndexOutOfBoundsException exc) {
                    // Do nothing - handled later
                }
            }
        }
        // IPv6 address and no port
        else if (hostPort.indexOf(':') != hostPort.lastIndexOf(":")) {
            host = '[' + hostPort + ']';
        }
        else { // no square brackets and a single or zero colons => IPv4
            // hostPort
            int portColon = hostPort.indexOf(':');
            if (portColon == -1) {
                host = hostPort;
            }
            else {
                host = hostPort.substring(0, portColon).trim();
                try {
                    portString = hostPort.substring(portColon + 1).trim();
                }
                catch (IndexOutOfBoundsException exc) {
                    // Do nothing - handled later
                }
            }
        }
        if (host == null || host.equals("")) {
            throw new IllegalArgumentException("no host!");
        }
        if (portString == null || portString.equals("")) {
            port = 5060;
        }
        else {
            try {
                port = Integer.parseInt(portString);
            }
            catch (NumberFormatException ex) {
                throw new IllegalArgumentException("Bad port spec");
            }
        }
    }

    /**
     * Retruns the host string.
     *
     * @return host String
     * @uml.property name="host"
     */
    public String getHost() {
        return host;
    }

    /**
     * Returns the port.
     *
     * @return port integer.
     * @uml.property name="port"
     */
    public int getPort() {
        return port;
    }

    /**
     * returns the transport string.
     *
     * @uml.property name="transport"
     */
    public String getTransport() {
        return transport;
    }

    /**
     * Return true if this is an explicit route (ie. extrcted from a ROUTE
     * Header)
     *
     * @uml.property name="explicitRoute"
     */
    public boolean isExplicitRoute() {
        return explicitRoute;
    }

    /**
     * Return true if this is a default route (ie. next hop proxy address)
     *
     * @uml.property name="defaultRoute"
     */
    public boolean isDefaultRoute() {
        return defaultRoute;
    }

    /**
     * Return true if this is uriRoute
     */
    public boolean isURIRoute() {
        return uriRoute;
    }

    /**
     * Set the URIRoute flag.
     */
    public void setURIRouteFlag() {
        uriRoute = true;
    }

    /**
     * Set the defaultRouteFlag.
     */
    public void setDefaultRouteFlag() {
        defaultRoute = true;
    }

    /**
     * Set the explicitRoute flag.
     */
    public void setExplicitRouteFlag() {
        explicitRoute = true;
	}
}
