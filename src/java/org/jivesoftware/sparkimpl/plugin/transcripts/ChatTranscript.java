/**
 * $RCSfile: ,v $
 * $Revision: $
 * $Date: $
 * 
 * Copyright (C) 2004-2011 Jive Software. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

    private List<HistoryMessage> messages = new ArrayList<>();

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
	    	List<HistoryMessage> searchResult = new ArrayList<>();
	    	for(HistoryMessage message : messages) {
	    		// ignore keywords' case
	    		if( message.getBody().toLowerCase().contains( text.toLowerCase() ) ) {
	    			searchResult.add(message);
	    		}
	    	}
	    	return searchResult;
    	}
    }
    
    /**
     * Clears the Message History if its not needed anymore
     */
    public void release() {
	messages.clear();
    }

    /**
     * Set the new List for the chat transcript
     * @param newList The list with the history messages
     */
    public void setList(List<HistoryMessage> newList){
    	this.messages = newList;
    }

    /**
     * Return the size of the message list
     * @return The size of the message list
     */
    public int size(){
    	return this.messages.size();
    }

    /**
     * Return the HistoryMessage by the given index
     * @param i the index of the history message
     * @return the history message, null if out of bounds
     */
    public HistoryMessage getMessage(int i){
    	HistoryMessage result = null;
    	if ((i > -1) && i < messages.size()){
    		result = messages.get(i);
    	}
    	return result;
    }

}
