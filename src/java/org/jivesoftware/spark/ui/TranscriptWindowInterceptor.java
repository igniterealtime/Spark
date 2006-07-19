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
 *
 */
public interface TranscriptWindowInterceptor {

    boolean handleInsertMessage(String userid, Message message);

    boolean handleOtherMessage(String userid, Message message);
    
}
