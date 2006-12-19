/**
 * $Revision$
 * $Date$
 *
 * Copyright (C) 1999-2005 Jive Software. All rights reserved.
 * This software is the proprietary information of Jive Software. Use is subject to license terms.
 */

package org.jivesoftware.spark.ui;

/**
 * Allows a plugin to add and/or remove components to the ContactInfo UI.
 *
 * @author Derek DeMoro
 */
public interface ContactInfoHandler {

    /**
     * Called everytime a <code>ContactInfo</code> is about to appear.
     * @param contactInfo the contactInfo object.
     */
    void handleContactInfo(ContactInfoWindow contactInfo);
}
