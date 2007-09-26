/**
 * $Revision$
 * $Date$
 *
 * Copyright (C) 1999-2005 Jive Software. All rights reserved.
 * This software is the proprietary information of Jive Software. Use is subject to license terms.
 */
package org.jivesoftware.sparkimpl.profile;

import org.jivesoftware.smackx.packet.VCard;

/**
 * Users will want to implement this interface to listen for changes with their VCard.
 *
 * @author Derek DeMoro
 */
public interface VCardListener {

    void vcardChanged(VCard vcard);
}
