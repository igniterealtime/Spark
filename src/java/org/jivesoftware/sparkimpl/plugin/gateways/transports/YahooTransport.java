/**
 * $Revision$
 * $Date$
 *
 * Copyright (C) 1999-2005 Jive Software. All rights reserved.
 * This software is the proprietary information of Jive Software. Use is subject to license terms.
 */

package org.jivesoftware.sparkimpl.plugin.gateways.transports;

import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.resource.Res;

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
        return Res.getString("title.yahoo.registration");
    }

    public String getInstructions() {
        return Res.getString("message.enter.yahoo");
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

    public String getName(){
        return "Yahoo";
    }

    public Boolean requiresUsername() {
        return true;
    }

    public Boolean requiresPassword() {
        return true;
    }

    public Boolean requiresNickname() {
        return false;
    }
    
}
