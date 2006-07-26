/**
 * $Revision$
 * $Date$
 *
 * Copyright (C) 1999-2005 Jive Software. All rights reserved.
 * This software is the proprietary information of Jive Software. Use is subject to license terms.
 */

package org.jivesoftware.sparkimpl.plugin.gateways.transports;

import org.jivesoftware.resource.SparkRes;

import javax.swing.Icon;

/**
 *
 */
public class YahooTransport implements Transport {

    private String serviceName;

    public YahooTransport(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getTitle() {
        return "Yahoo Registration";
    }

    public String getInstructions() {
        return "Enter your Screen Name and password to connect to Yahoo.";
    }

    public Icon getIcon() {
        return SparkRes.getImageIcon(SparkRes.YAHOO_TRANSPORT_ACTIVE_IMAGE);
    }

    public Icon getInactiveIcon() {
        return SparkRes.getImageIcon(SparkRes.YAHOO_TRANSPORT_INACTIVE_IMAGE);
    }

    public String getServiceName() {
        return serviceName;
    }
}
