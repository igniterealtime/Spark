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

/**
 * The class is used to store different kinds of URI instances.
 *
 * @author Emil Ivov
 * @version 1.0 Modified to be typesafe by Brian Burch
 */

public class GenericURI {

    public static final UriScheme SIP_SCHEME = new UriScheme("sip");

    public static final UriScheme SIPS_SCHEME = new UriScheme("sips");

    public static final UriScheme PRESENCE_SCHEME = new UriScheme("pres");

    public static final UriScheme MAIL_SCHEME = new UriScheme("mailto");

    public static final UriScheme TEL_SCHEME = new UriScheme("tel");

    /**
     * @uml.property name="aLL_SCHEMES"
     * @uml.associationEnd multiplicity="(0 -1)"
     */
    public static final UriScheme[] ALL_SCHEMES = {SIP_SCHEME, SIPS_SCHEME,
            PRESENCE_SCHEME, MAIL_SCHEME, TEL_SCHEME};

    /**
     * The scheme of this uri.
     */
    private UriScheme scheme;

    /**
     * only valid when scheme is SIP_SCHEME
     */
    private int port = 5060;

    /**
     * The address portiong of the Uri
     */
    private String address = null;

    /**
     * Any parameters found after the address part of the URI
     */
    private String uriParams = null;

    /**
     * Default constructor
     */
    public GenericURI() {
        this(SIP_SCHEME);
    }

    /**
     * Typesafe constructor
     */
    public GenericURI(UriScheme scheme) {
        setScheme(scheme);
    }

    /**
     * Sets the scheme of the URI.
     *
     * @param scheme the scheme of the URI.
     * @uml.property name="scheme"
     */
    public void setScheme(UriScheme scheme) {
        if (scheme == null)
            throw new NullPointerException("null UriScheme forbidden");
        this.scheme = scheme;
    }

    /**
     * Safely sets the scheme of the URI.
     *
     * @param schemeName the name of the scheme of the URI.
     */
    public void setScheme(String schemeName) {
        if (schemeName == null)
            throw new NullPointerException("null UriScheme name forbidden");
        this.scheme = null; // wipe any residual scheme
        String safeName = schemeName.toLowerCase();
        for (int i = 0; i < ALL_SCHEMES.length; i++) {
            if (safeName.equals(ALL_SCHEMES[i].toString()))
                setScheme(ALL_SCHEMES[i]);
        }
        if (this.scheme == null)
            throw new IllegalArgumentException("UriScheme called " + schemeName
                    + " is unknown");
    }

    /**
     * Returns the scheme of the URI.
     *
     * @param scheme the scheme of the URI.
     * @uml.property name="scheme"
     */
    public UriScheme getScheme() {
        return scheme;
    }

    /**
     * Sets the port of the URI. The port is only used with sip and pres schemes
     * and is ignored otherwise.
     *
     * @param port the port part of the uri.
     * @uml.property name="port"
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Returns the port used by the URI. This method would always return a
     * result even if the port field is not valid in a given context (e.g. tel
     * scheme)
     *
     * @return the port used by the URI
     * @uml.property name="port"
     */
    public int getPort() {
        return port;
    }

    /**
     * Creates a GenericURI object after parsing the specified string.
     *
     * @param uriStr the uri string
     * @return the newly constructed URI.
     * @throws IllegalArgumentException if <code>uriStr</code> does not contain a valid URI
     */
    public static GenericURI parseURI(String uriStr)
            throws IllegalArgumentException {
        if (uriStr == null || uriStr.length() == 0)
            throw new IllegalArgumentException(
                    "The URI String must not be null or with 0 length");
        GenericURI uri = new GenericURI();

        int colonIndex = uriStr.indexOf(':');
        int bracketIndex = uriStr.indexOf('[');

        if (colonIndex == -1
                || (bracketIndex > -1 && colonIndex > bracketIndex))
            throw new IllegalArgumentException(
                    "No URI scheme found in the following uri: " + uriStr);

        uri.setScheme(uriStr.substring(0, colonIndex));

        int portColonIndex = -1;

        // move colon index forward in case we are dealing with an IPv6 address.
        // if(uriStr.charAt(colonIndex + 1) == '[') - bug fix by Ling, Fang-Yu
        if (uriStr.indexOf('[') != -1)
            portColonIndex = uriStr.indexOf(':', uriStr.indexOf(']'));
        else
            portColonIndex = uriStr.indexOf(':', colonIndex + 1);

        int semiColonIndex = uriStr.indexOf(';', colonIndex + 1);

        semiColonIndex = ((semiColonIndex == -1) ? uriStr.length()
                : semiColonIndex);

        // get the port
        if (portColonIndex != -1) {
            try {
                uri.port = Integer.parseInt(uriStr.substring(
                        portColonIndex + 1, semiColonIndex));
            }
            catch (NumberFormatException ex) {
                throw new IllegalArgumentException(
                        "Failed to parse the port part of the following uri: "
                                + uriStr);
            }
        }
        else
            portColonIndex = semiColonIndex;

        // get the uri value
        uri.address = uriStr.substring(colonIndex + 1, portColonIndex);

        if (semiColonIndex < uriStr.length())
            uri.uriParams = uriStr.substring(semiColonIndex + 1);

        return uri;
    }

    /**
     * Sets the address part (and only the address part) of this URI.
     */
    public void setAddressPart(String addressPart) {
        addressPart = addressPart.trim();
        if (addressPart.indexOf(':') != -1 && addressPart.charAt(0) != '[')
            // we have an IPv6 address
            this.address = "[" + addressPart + "]";
        else
            this.address = addressPart;

    }

    /**
     * Returns the address part of this URI.
     *
     * @return String
     */
    public String getAddressPart() {
        return address;
    }

    /**
     * Returns all parameters of this uri.
     *
     * @return a String containing all parameters of this uri.
     * @uml.property name="uriParams"
     */
    public String getUriParams() {
        return this.uriParams;
    }

    /**
     * Returns the String representation of this URI.
     *
     * @return the String representation of this URI.
     */
    public String toString() {
        StringBuffer buffer = new StringBuffer(getScheme().toString());

        buffer.append(':').append(getAddressPart());

        if (getPort() != 5060)
            buffer.append(':').append(String.valueOf(getPort()));

        if (getUriParams() != null && getUriParams().length() > 0)
            buffer.append(';').append(getUriParams());

        return buffer.toString();
    }

    /**
     * Creates and returns a copy of this uri.
     *
     * @return a clone of this instance.
     * @throws CloneNotSupportedException if the object's class does not support the
     *                                    <code>Cloneable</code> interface. Subclasses that override
     *                                    the <code>clone</code> method can also throw this exception
     *                                    to indicate that an instance cannot be cloned.
     * @todo Implement this java.lang.Object method
     */
    protected Object clone() {
        GenericURI clone = new GenericURI();

        clone.setScheme(getScheme()); // immutable singleton
        clone.setPort(getPort());
        clone.setAddressPart(new String(getAddressPart()));
        clone.setUriParams(getUriParams() == null ? null : new String(
                getUriParams()));

        return clone;
    }

    /**
     * Sets an parameter string for this URI.
     *
     * @param paramString a string of parameters for this uri.
     * @uml.property name="uriParams"
     */
    private void setUriParams(String paramString) {
        this.uriParams = paramString;
    }

    /**
     * Compares this GenericURI with <code>uri</code> ignoring all uri
     * parameters
     *
     * @param uri the uri to match this instance against.
     * @return true if uri represents the same entity as this instance (port,
     *         address and scheme fields are equal) and false otherwise.
     */
    public boolean matches(GenericURI uri) {
        return (uri.getScheme().equals(getScheme())
                && uri.getAddressPart().equals(getAddressPart()) && uri
                .getPort() == getPort());
    }

    // typesafe because constructor of inner class is private
    public static class UriScheme {
        private final String scheme;

        private UriScheme(String scheme) {
            if (scheme == null)
                throw new NullPointerException("null UriScheme forbidden");
            this.scheme = scheme.toLowerCase();
        }

        public String toString() {
			return scheme;
		}
	}
}
