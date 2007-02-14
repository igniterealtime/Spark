/**
 * $Revision$
 * $Date$
 *
 * Copyright (C) 1999-2005 Jive Software. All rights reserved.
 * This software is the proprietary information of Jive Software. Use is subject to license terms.
 */

package org.jivesoftware.spark.ui;

import org.jivesoftware.smack.packet.Message;

/**
 * Allows users to intercept messages before they are inserted into the TranscriptWindow.
 *
 * @see TranscriptWindow
 * @see org.jivesoftware.spark.ChatManager
 */
public interface TranscriptWindowInterceptor {

    /**
     * Is called before a message by this user is inserted into the TranscriptWindow.
     *
     * @param window  the TranscriptWindow.
     * @param userid  the userid.
     * @param message the message to be inserted.
     * @return true if it should be handled by a custom interceptor.
     */
    boolean isMessageIntercepted(TranscriptWindow window, String userid, Message message);

}
