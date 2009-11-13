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

/**
 * A single chat transcript mapped to one JID.
 *
 * @author Derek DeMoro
 */
public class ChatTranscript {

    private final List<HistoryMessage> messages = new ArrayList<HistoryMessage>();

    /**
     * Add a <code>HistoryMessage</code> to users chat transcript.
     *
     * @param entry the HistoryMessage to add.
     */
    public void addHistoryMessage(HistoryMessage entry) {
        messages.add(entry);
    }

    /**
     * Returns all messages.
     *
     * @return all messages.
     */
    public List<HistoryMessage> getMessages() {
        return messages;
    }

    /**
     * Returns a specified number of messages.
     *
     * @param number the number of messages to return.
     * @return the specified number of messages, or all messages if number is greater than the current amount.
     */
    public Collection<HistoryMessage> getNumberOfEntries(int number) {
        int listSize = messages.size();

        if (messages.size() <= number) {
            return messages;
        }
        else {
            int start = listSize - number;
            return messages.subList(start, listSize);
        }
    }

    /**
     * Returns messages that included search keywords.
     * 
     * @param text search keywords.If the search keywords is null, return all message.
     * @return the messages that included search keywords.
     */
    public List<HistoryMessage> getMessage(String text) {
    	if(text == null || "".equals(text)) {
    		return messages;
    	} else {
	    	List<HistoryMessage> searchResult = new ArrayList<HistoryMessage>();
	    	for(HistoryMessage message : messages) {
	    		// ignore keywords' case
	    		if(message.getBody().toLowerCase().indexOf(text.toLowerCase()) != -1) {
	    			searchResult.add(message);
	    		}
	    	}
	    	return searchResult;
    	}
    }
}
