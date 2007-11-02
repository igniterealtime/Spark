/**
 * $Revision$
 * $Date$
 *
 * Copyright (C) 1999-2005 Jive Software. All rights reserved.
 * This software is the proprietary information of Jive Software. Use is subject to license terms.
 */

package org.jivesoftware.sparkimpl.plugin.gateways.transports;

import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;

import javax.swing.Icon;

/**
 *
 */
public class GTalkTransport implements Transport {

    private String serviceName;

    public GTalkTransport(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getTitle() {
        return Res.getString("title.gtalk.registration");
    }

    public String getInstructions() {
        return Res.getString("message.enter.gtalk");
    }

    public Icon getIcon() {
        return SparkRes.getImageIcon(SparkRes.GTALK_TRANSPORT_ACTIVE_IMAGE);
    }

    public Icon getInactiveIcon() {
        return SparkRes.getImageIcon(SparkRes.GTALK_TRANSPORT_INACTIVE_IMAGE);
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getName() {
        return "GTalk";
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
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
