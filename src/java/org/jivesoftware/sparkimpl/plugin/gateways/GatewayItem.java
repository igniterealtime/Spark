/**
 * 
 */
package org.jivesoftware.sparkimpl.plugin.gateways;

/**
 * @author holger.bergunde
 *
 */
public interface GatewayItem {

    public void signedIn(boolean signedIn);
       
    public boolean isLoggedIn();

    
}
