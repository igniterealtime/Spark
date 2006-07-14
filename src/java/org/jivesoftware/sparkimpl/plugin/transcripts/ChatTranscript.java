/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkimpl.plugin.transcripts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ChatTranscript {

    private final List messages = new ArrayList();

    public void addHistoryMessage(HistoryMessage entry) {
        messages.add(entry);
    }

    public Collection getMessages() {
        return messages;
    }

    public Collection getNumberOfEntries(int number) {
        int listSize = messages.size();

        if (messages.size() <= number) {
            return messages;
        }
        else {
            int start = listSize - number;
            return messages.subList(start, listSize);
        }
    }
}
