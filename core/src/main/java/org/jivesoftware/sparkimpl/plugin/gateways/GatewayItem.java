/**
 */
package org.jivesoftware.sparkimpl.plugin.gateways;

/**
 * @author holger.bergunde
 *
 */
public interface GatewayItem {
    void signedIn(boolean signedIn);
    boolean isLoggedIn();
}
