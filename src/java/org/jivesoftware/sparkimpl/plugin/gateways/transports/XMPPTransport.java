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
public class XMPPTransport implements Transport {

    private String serviceName;

    public XMPPTransport(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getTitle() {
        return Res.getString("title.xmpp.registration");
    }

    public String getInstructions() {
        return Res.getString("message.enter.xmpp");
    }

    public Icon getIcon() {
        return SparkRes.getImageIcon(SparkRes.XMPP_TRANSPORT_ACTIVE_IMAGE);
    }

    public Icon getInactiveIcon() {
        return SparkRes.getImageIcon(SparkRes.XMPP_TRANSPORT_INACTIVE_IMAGE);
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getName() {
        return "XMPP";
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}
