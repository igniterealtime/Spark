/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2009 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */
package net.java.sipmack.sip.security;

import java.util.Vector;

/**
 * Used to cache credentials through a call.
 *
 * @author Emil Ivov <emcho@dev.java.net>
 * @version 1.0
 */
class CredentialsCacheEntry {
    // The header that was used last time
    // AuthorizationHeader authorization = null;

    Credentials userCredentials = null;

    /**
     * The transactionHistory list contains transactions where the entry has
     * been and that had not yet been responded to (or at least the response has
     * not reached this class). The transactionHistory's elements are Strings
     * corresponding to branch ids.
     */
    private Vector<String> transactionHistory = new Vector<String>();

    /**
     * Adds the specified branch id to the transaction history list.
     *
     * @param requestBranchID the id to add to the list of uncofirmed transactions.
     */
    void processRequest(String requestBranchID) {
        transactionHistory.add(requestBranchID);
    }

    /**
     * Determines whether these credentials have been used for the specified
     * transaction. If yes - the transaction is removed and true is returned.
     * Otherwise we return false.
     *
     * @param branchID the branchi id of the response to process.
     * @return true if this entry hase been used for the transaction.
     */
    boolean processResponse(String responseBranchID) {
        return transactionHistory.remove(responseBranchID);
	}
}
