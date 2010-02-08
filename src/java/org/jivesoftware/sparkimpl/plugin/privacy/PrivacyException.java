/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2010 Ignite Realtime. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkimpl.plugin.privacy;

import org.jivesoftware.smack.XMPPException;

/**
 *
 * @author Zolotarev Konstantin
 */
public class PrivacyException extends XMPPException {

    public PrivacyException() {
        super();
    }

    public PrivacyException(String message) {
        super(message);
    }

}
