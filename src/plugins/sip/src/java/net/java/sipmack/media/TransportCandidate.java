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
package net.java.sipmack.media;

import java.net.SocketException;
import java.net.UnknownHostException;

import org.jivesoftware.smack.XMPPConnection;

/**
 * Transport candidate.
 * <p/>
 * A candidate represents the possible transport for data interchange between
 * the two endpoints.
 *
 * @author Thiago Camargo
 */
public abstract class TransportCandidate {

    private String name;

    private String ip; // IP address

    private int port; // Port to use, or 0 for any port

    private String localIp;

    private int generation;

    protected String password;

    private String sessionId;

    private XMPPConnection connection;

    private TransportCandidate symmetric;

    public void addCandidateEcho() throws SocketException, UnknownHostException {
    }

    public String getIp() {
        return ip;
    }

    /**
     * Set the IP address.
     *
     * @param ip the IP address
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * Get local IP to bind to this candidate
     *
     * @return
     */
    public String getLocalIp() {
        return localIp == null ? ip : localIp;
    }

    /**
     * Set local IP to bind to this candidate
     *
     * @param localIp
     */
    public void setLocalIp(String localIp) {
        this.localIp = localIp;
    }

    /**
     * Get the symetric candidate for this candidate if it exists.
     *
     * @return
     */
    public TransportCandidate getSymmetric() {
        return symmetric;
    }

    /**
     * Set the symetric candidate for this candidate.
     *
     * @param symetric
     */
    public void setSymmetric(TransportCandidate symetric) {
        this.symmetric = symetric;
    }

    /**
     * Get the password used by ICE or relayed candidate
     *
     * @return a password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set the password used by ICE or relayed candidate
     *
     * @param password a password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Get the XMPPConnection use to send or receive this candidate
     *
     * @return
     */
    public XMPPConnection getConnection() {
        return connection;
    }

    /**
     * Set the XMPPConnection use to send or receive this candidate
     *
     * @param connection
     */
    public void setConnection(XMPPConnection connection) {
        this.connection = connection;
    }

    /**
     * Get the jingle�s sessionId that is using this candidate
     *
     * @return
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * Set the jingle�s sessionId that is using this candidate
     *
     * @param sessionId
     */
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * Empty constructor
     */
    public TransportCandidate() {
        this(null, 0, 0);
    }

    /**
     * Constructor with IP address and port
     *
     * @param ip   The IP address.
     * @param port The port number.
     */
    public TransportCandidate(String ip, int port) {
        this(ip, port, 0);
    }

    /**
     * Constructor with IP address and port
     *
     * @param ip         The IP address.
     * @param port       The port number.
     * @param generation The generation
     */
    public TransportCandidate(String ip, int port, int generation) {
        this.ip = ip;
        this.port = port;
        this.generation = generation;
    }

    /**
     * Return true if the candidate is not valid.
     *
     * @return true if the candidate is null.
     */
    public boolean isNull() {
        if (ip == null) {
            return true;
        }
        else if (ip.length() == 0) {
            return true;
        }
        else if (port < 0) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Get the port, or 0 for any port.
     *
     * @return the port or 0
     */
    public int getPort() {
        return port;
    }

    /**
     * Set the port, using 0 for any port
     *
     * @param port the port
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Get the generation for a transportElement definition
     *
     * @return the generation
     */
    public int getGeneration() {
        return generation;
    }

    /**
     * Set the generation for a transportElement definition.
     *
     * @param generation the generation number
     */
    public void setGeneration(int generation) {
        this.generation = generation;
    }

    /**
     * Get the name used for identifying this transportElement method (optional)
     *
     * @return a name used for identifying this transportElement (ie,
     *         "myrtpvoice1")
     */
    public String getName() {
        return name;
    }

    /**
     * Set a name for identifying this transportElement.
     *
     * @param name the name used for the transportElement
     */
    public void setName(String name) {
        this.name = name;
    }

    /*
      * (non-Javadoc)
      *
      * @see java.lang.Object#equals(java.lang.Object)
      */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TransportCandidate other = (TransportCandidate) obj;
        if (generation != other.generation) {
            return false;
        }
        if (getIp() == null) {
            if (other.getIp() != null) {
                return false;
            }
        }
        else if (!getIp().equals(other.getIp())) {
            return false;
        }
        if (getName() == null) {
            if (other.getName() != null) {
                return false;
            }
        }
        else if (!getName().equals(other.getName())) {
            return false;
        }
        if (getPort() != other.getPort()) {
            return false;
        }
        return true;
    }

    /**
     * Fixed transport candidate
     */
    public static class Fixed extends TransportCandidate {

        public Fixed() {
            super();
        }

        /**
         * Constructor with IP address and port
         *
         * @param ip   The IP address.
         * @param port The port number.
         */
        public Fixed(String ip, int port) {
            super(ip, port);
        }

        /**
         * Constructor with IP address and port
         *
         * @param ip         The IP address.
         * @param port       The port number.
         * @param generation The generation
         */
        public Fixed(String ip, int port, int generation) {
            super(ip, port, generation);
        }
    }

    /**
     * Type-safe enum for the transportElement protocol
     */
    public static class Protocol {

        public static final Protocol UDP = new Protocol("udp");

        public static final Protocol TCP = new Protocol("tcp");

        public static final Protocol TCPACT = new Protocol("tcp-act");

        public static final Protocol TCPPASS = new Protocol("tcp-pass");

        public static final Protocol SSLTCP = new Protocol("ssltcp");

        private String value;

        public Protocol(String value) {
            this.value = value;
        }

        public String toString() {
            return value;
        }

        /**
         * Returns the Protocol constant associated with the String value.
         */
        public static Protocol fromString(String value) {
            if (value == null) {
                return UDP;
            }
            value = value.toLowerCase();
            if (value.equals("udp")) {
                return UDP;
            }
            else if (value.equals("tcp")) {
                return TCP;
            }
            else if (value.equals("tcp-act")) {
                return TCPACT;
            }
            else if (value.equals("tcp-pass")) {
                return TCPPASS;
            }
            else if (value.equals("ssltcp")) {
                return SSLTCP;
            }
            else {
                return UDP;
            }
        }

        /*
           * (non-Javadoc)
           *
           * @see java.lang.Object#equals(java.lang.Object)
           */
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Protocol other = (Protocol) obj;
            if (value == null) {
                if (other.value != null) {
                    return false;
                }
            }
            else if (!value.equals(other.value)) {
                return false;
            }
            return true;
        }

        /**
         * Return true if the protocol is not valid.
         *
         * @return true if the protocol is null
         */
        public boolean isNull() {
            if (value == null) {
                return true;
            }
            else if (value.length() == 0) {
                return true;
            }
            else {
                return false;
            }
        }
    }

    /**
     * Type-safe enum for the transportElement channel
     */
    public static class Channel {

        public static final Channel MYRTPVOICE = new Channel("myrtpvoice");

        public static final Channel MYRTCPVOICE = new Channel("myrtcpvoice");

        private String value;

        public Channel(String value) {
            this.value = value;
        }

        public String toString() {
            return value;
        }

        /**
         * Returns the MediaChannel constant associated with the String value.
         */
        public static Channel fromString(String value) {
            if (value == null) {
                return MYRTPVOICE;
            }
            value = value.toLowerCase();
            if (value.equals("myrtpvoice")) {
                return MYRTPVOICE;
            }
            else if (value.equals("tcp")) {
                return MYRTCPVOICE;
            }
            else {
                return MYRTPVOICE;
            }
        }

        /*
           * (non-Javadoc)
           *
           * @see java.lang.Object#equals(java.lang.Object)
           */
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Channel other = (Channel) obj;
            if (value == null) {
                if (other.value != null) {
                    return false;
                }
            }
            else if (!value.equals(other.value)) {
                return false;
            }
            return true;
        }

        /**
         * Return true if the channel is not valid.
         *
         * @return true if the channel is null
         */
        public boolean isNull() {
            if (value == null) {
                return true;
            }
            else if (value.length() == 0) {
                return true;
            }
            else {
                return false;
            }
        }
    }

}
