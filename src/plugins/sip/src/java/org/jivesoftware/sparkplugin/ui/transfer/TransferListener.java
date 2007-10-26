/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2007 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkplugin.ui.transfer;

/**
 * TransferListener is notified whenever a user chooses a number to transfer to.
 */
public interface TransferListener {

    /**
     * Is called whenver a user selects a number within a TransferGroup.
     *
     * @param number the number selected.
     */
    void numberSelected(String number);
}
