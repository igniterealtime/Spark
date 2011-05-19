package org.jivesoftware.sparkimpl.plugin.privacy.list;

/**
 *
 * @author Bergunde Holger
 */

public interface SparkPrivacyListListener {
    
    
    /**
     * Gets triggered, when a list has been activated 
     * @param listname
     */
    void listActivated(String listname);
    
    /**
     * Gets triggered, when a list has been deactivated 
     * @param listname
     */
    void listDeActivated(String listname);
    
    /**
     * Gets triggered, when a list has been set as default
     * @param listname
     */
    void listSetAsDefault(String listname);
    
    /**
     * Gets triggered, when a list has been removed as default list
     * @param listname
     */
    void listRemovedAsDefault(String listname);

}
