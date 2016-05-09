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

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import net.java.sipmack.common.Log;

/**
 * Title: Spark Phone
 * Description:JAIN-SIP Audio/Video phone application
 * New features: NAT / STUN
 *
 * @author Thiago Rocha Camargo (thiago@jivesoftware.com)
 */

public class NetworkAddressManager {
    private static NetworkAddressManager manager = null;

    private static InetAddress cachedLocalhost = null;

    private static int index = 0;

    private static List<InetAddressWrapper> addresses = new ArrayList<InetAddressWrapper>();

    private NetworkAddressManager() {
    }

    private void init() {
        setProperties();
    }

    /**
     * Initializes the address manager.
     */
    public static void start() {
        cacheLocalhost(null);
        index = 0;
        if (manager != null)
            return;
        manager = new NetworkAddressManager();
        manager.init();
        // Detect and output network configuration (Firewall and NAT type)

        // only used for debugging currently.
    }

    /**
     * Shuts down the address manager and the underlying stun lib and deletes
     * the manager.
     */
    public static void shutDown() {
        if (manager == null)
            return;
        manager = null;
    }

    /**
     * Returns an InetAddress instance representing the local host or null if no
     * IP address for the host could be found
     *
     * @return an InetAddress instance representing the local host or null if no
     *         IP address for the host could be found
     */
    public static InetAddress getLocalHost() {
        return getLocalHost(false);
    }

    /**
     * Returns a localhostAddress.
     *
     * @param anyAddressIsAccepted is 0.0.0.0 accepted as a return value.
     * @return the address that was detected the address of the localhost.
     */
    public static InetAddress getLocalHost(boolean anyAddressIsAccepted) {

        Log.debug("NETWORK DETECTION");

        if (cachedLocalhost != null) {
            Log.debug("SELECTED IP: " + cachedLocalhost);
            return cachedLocalhost;
        }

        Enumeration<NetworkInterface> ifaces = null;
        int i = 0;

        // Try to get the Preferred IP Address

        if (SIPConfig.getPreferredNetworkAddress() != null && !SIPConfig.getPreferredNetworkAddress().equals("")) {
            //Preferred is local host

            try {
                SIPConfig.setPreferredNetworkAddress(InetAddress.getLocalHost().getHostAddress());
            } catch (UnknownHostException e) {
                Log.error(e);
            }

        }

        if (SIPConfig.getPreferredNetworkAddress() != null && !SIPConfig.getPreferredNetworkAddress().equals("")) {

            try {
                ifaces = NetworkInterface.getNetworkInterfaces();
            } catch (SocketException e) {
                e.printStackTrace();
            }

            while (ifaces.hasMoreElements()) {

                NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
                Enumeration<InetAddress> iaddresses = iface.getInetAddresses();

                while (iaddresses.hasMoreElements()) {
                    InetAddress iaddress = iaddresses.nextElement();
                    if (iaddress.getHostAddress().equals(SIPConfig.getPreferredNetworkAddress())) {
                        addresses.add(new InetAddressWrapper(iaddress, i++));
                        break;
                    }
                }
            }

        }

        try {
            ifaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        // Try to get best IP ( not loopback, not linklocal and not sitelocal )

        while (ifaces.hasMoreElements()) {

            NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
            Enumeration<InetAddress> iaddresses = iface.getInetAddresses();

            while (iaddresses.hasMoreElements()) {
                InetAddress iaddress = (InetAddress) iaddresses.nextElement();
                if (!iaddress.isLoopbackAddress() && !iaddress.isLinkLocalAddress() && !iaddress.isSiteLocalAddress() && !(iaddress instanceof java.net.Inet6Address)) {
                    addresses.add(new InetAddressWrapper(iaddress, i++));
                }
            }
        }

        try {
            ifaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        // Try to get best IP ( not loopback and not linklocal )

        while (ifaces.hasMoreElements()) {

            NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
            Enumeration<InetAddress> iaddresses = iface.getInetAddresses();

            while (iaddresses.hasMoreElements()) {
                InetAddress iaddress = (InetAddress) iaddresses.nextElement();
                if (!iaddress.isLoopbackAddress() && !iaddress.isLinkLocalAddress() && !(iaddress instanceof java.net.Inet6Address)) {
                    addresses.add(new InetAddressWrapper(iaddress, i++));
                }
            }
        }

        // If Address list is empty, return localhost

        if (addresses.isEmpty())
            try {
                return InetAddress.getLocalHost();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

        Collections.sort(addresses);

        cacheLocalhost(addresses.get(index).getInetAddress());
        setProperties();

        return addresses.get(addresses.size() - 1).getInetAddress();
    }

    /**
     * Caches localhost to make address discovery faster
     *
     * @param localhost localhost
     */
    public static boolean cacheLocalhost(InetAddress localhost) {
        cachedLocalhost = localhost;
        return true;
    }

    // EMIL: I don't see where this method is used. Should probbaly Take it away

    /**
     * Tries to obtain a mapped/public address for the specified address and
     * port. If the STUN lib fails, tries to retrieve localhost, if that fails
     * too, returns null.
     *
     * @param address the address to resolve
     * @param port    the port whose mapping we are interested in.
     * @return a public address corresponding to the specified port or null if
     *         all attempts to retrieve such an address have failed.
     */
    public static InetSocketAddress getPublicAddressFor(String address, int port) {
        return new InetSocketAddress(address, port);
    }

    /**
     * Tries to obtain a mapped/public address for the specified port. If the
     * STUN lib fails, tries to retrieve localhost, if that fails too, returns
     * null.
     *
     * @param port the port whose mapping we are interested in.
     * @return a public address corresponding to the specified port or null if
     *         all attempts to retrieve such an address have failed.
     */
    public static InetSocketAddress getPublicAddressFor(int port) {
        return new InetSocketAddress(getLocalHost(), port);
    }

    /**
     * Determines whether the address is the result of windows auto
     * configuration. (i.e. One that is in the 169.254.0.0 network)
     *
     * @param add the address to inspect
     * @return true if the address is autoconfigured by windows, false
     *         otherwise.
     */
    public static boolean isWindowsAutoConfiguredIPv4Address(InetAddress add) {
        return (add.getAddress()[0] & 0xFF) == 169
                && (add.getAddress()[1] & 0xFF) == 254;
    }

    /**
     * Determines whether the address is an IPv4 link local address. IPv4 link
     * local addresses are those in the following networks:
     * <p/>
     * 10.0.0.0 to 10.255.255.255 172.16.0.0 to 172.31.255.255 192.168.0.0 to
     * 192.168.255.255
     *
     * @param add the address to inspect
     * @return true if add is a link local ipv4 address and false if not.
     */
    public static boolean isLinkLocalIPv4Address(InetAddress add) {
        byte address[] = add.getAddress();
        if ((address[0] & 0xFF) == 10)
            return true;
        if ((address[0] & 0xFF) == 172 && (address[1] & 0xFF) >= 16
                && address[1] <= 31)
            return true;
        if ((address[0] & 0xFF) == 192 && (address[1] & 0xFF) == 168)
            return true;
        return false;
    }

    /**
     * Determines whether the address could be used in a VoIP session.
     * Attention, routable address as determined by this method are not only
     * globally routable addresses in the general sense of the term. Link local
     * addresses such as 192.168.x.x or fe80::xxxx are also considered usable.
     *
     * @param address the address to test.
     * @return true if the address could be used in a VoIP session.
     */
    public static boolean isRoutable(InetAddress address) {
        if (address instanceof Inet6Address) {
            return !address.isLoopbackAddress();
        } else {
            return (!address.isLoopbackAddress())
                    && (!isWindowsAutoConfiguredIPv4Address(address));
        }
    }

    private static void setProperties() {

        InetAddress selectedAddress = getLocalHost();

        NetworkAddressManager.cacheLocalhost(selectedAddress);

        SIPConfig.setPreferredNetworkAddress(selectedAddress.getHostAddress());
        SIPConfig.setIPAddress(selectedAddress.getHostAddress());
        SIPConfig.setPublicAddress(selectedAddress.getHostAddress());
        SIPConfig.setSystemProperties();

    }

    public static void resetIndex() {
        index = 0;
        cachedLocalhost = null;
    }

    public static boolean nextIndex() {
        if (index < addresses.size() - 1) {
            index++;
            cacheLocalhost(addresses.get(index).getInetAddress());
            setProperties();
            return true;
        }
        return false;
    }

    public static List<InetAddressWrapper> getInetAddresses() {
        return addresses;
    }

    public static class InetAddressWrapper implements Comparable<InetAddressWrapper> {

        private int value = 0;
        private InetAddress inetAddress = null;

        public InetAddressWrapper(InetAddress inetAddress, int value) {
            this.inetAddress = inetAddress;
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public InetAddress getInetAddress() {
            return inetAddress;
        }

        public int compareTo(InetAddressWrapper o) {

            InetAddressWrapper other = o;

            if (this.value < other.getValue()) return -1;
            else if (this.value == other.getValue()) return 0;
            else return 1;
        }
    }

    public static void main(String args[]) {

        NetworkAddressManager.start();

        boolean next = true;

        while (next) {
            System.out.println(NetworkAddressManager.getLocalHost());
            next = NetworkAddressManager.nextIndex();
        }

    }

}
