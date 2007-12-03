/**
 * $Revision$
 * $Date$
 *
 * Copyright (C) 1999-2005 Jive Software. All rights reserved.
 * This software is the proprietary information of Jive Software. Use is subject to license terms.
 */

package org.jivesoftware.spark.phone;

import javax.swing.Action;

import java.util.Collection;

/**
 *
 */
public interface Phone {

    /**
     * Return all actions for this <code>ChatRoom</code>.
     *
     * @param jid the jid of the user.
     * @return a collection of actions.
     */
    Collection<Action> getPhoneActions(String jid);

}
