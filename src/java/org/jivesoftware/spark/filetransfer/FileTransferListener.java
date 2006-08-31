/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.spark.filetransfer;

import org.jivesoftware.smackx.filetransfer.FileTransferRequest;

/**
 * Implementation of the <code>TransferListener</code> interface allows for
 * handling of file transfers externally of the default behaviour.
 *
 * @author Derek DeMoro
 */
public interface FileTransferListener {

    /**
     * Returns true if the object wishes to handle the file transfer itself. Otherwise,
     * it will default.
     *
     * @param request the <code>FileTransferRequest</code>
     * @return true if object handles transfer request.
     */
    boolean handleTransfer(FileTransferRequest request);

}
