/**
 * $Revision$
 * $Date$
 *
 * Copyright (C) 1999-2005 Jive Software. All rights reserved.
 * This software is the proprietary information of Jive Software. Use is subject to license terms.
 */
package org.jivesoftware.sparkimpl.plugin.gateways.transports;

import javax.swing.Icon;

/**
 *
 */
public interface Transport {

    String getTitle();

    String getInstructions();

    Icon getIcon();

    Icon getInactiveIcon();

    String getServiceName();

    String getName();

    Boolean requiresUsername();

    Boolean requiresPassword();

    Boolean requiresNickname();

}
