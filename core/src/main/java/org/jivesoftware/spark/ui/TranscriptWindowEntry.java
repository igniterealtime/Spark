/*
 * Copyright (C) 2017 Ignite Realtime Foundation. All rights reserved.
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
package org.jivesoftware.spark.ui;

import javax.swing.text.BadLocationException;
import java.time.ZonedDateTime;

/**
 * One entry in a transcript window (typically a line of text).
 *
 * This class and its descendants represent an entry of content displayed in a transcript window, including its
 * formatting.
 *
 * @author Guus der Kinderen, guus.der.kinderen@gmail.com.
 */
public abstract class TranscriptWindowEntry
{
    private final ZonedDateTime timestamp;
    protected final boolean isDelayed;

    /**
     * Constructs a new entry.
     *
     * When displayed in context of a conversation, chat entries are ordered by time. This is why the time component
     * of an entry is non-optional. Note that multiple entries can exist that have the same time component.
     *
     * @param timestamp The timestamp of the entry (cannot be null).
     */
    protected TranscriptWindowEntry( ZonedDateTime timestamp ){
    	this(timestamp, false);    	
    }
    /**
     * Constructs a new entry.
     *
     * When displayed in context of a conversation, chat entries are ordered by time. This is why the time component
     * of an entry is non-optional. Note that multiple entries can exist that have the same time component.
     *
     * @param timestamp The timestamp of the entry (cannot be null).
     * @param isDelayed Set true if contain delayed, historic timestamp.
     */
    protected TranscriptWindowEntry( ZonedDateTime timestamp, boolean isDelayed )
    {
        if ( timestamp == null )
        {
            throw new IllegalArgumentException( "Argument 'timestamp' cannot be null." );
        }
        this.timestamp = timestamp;
        this.isDelayed = isDelayed;
    }

    /**
     * The timestamp of the entry.
     *
     * @return A (zoned) timestamp (never null).
     */
    public ZonedDateTime getTimestamp()
    {
        return timestamp;
    }

    /**
     * Adds this entry to the provided chat area.
     *
     * This method is intended to be overridden by subclasses, which allows each subclass to decorate its content
     * appropriately.
     *
     * @param chatArea the ChatArea to which content is to be added (cannot be null).
     */
    protected abstract void addTo( ChatArea chatArea ) throws BadLocationException;

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        TranscriptWindowEntry that = (TranscriptWindowEntry) o;

        return timestamp.toInstant().equals( that.timestamp.toInstant() );
    }

    @Override
    public int hashCode()
    {
        return timestamp.toInstant().hashCode();
    }
   
    /**
     * Is it delayed message.
     * @return True if message contain historic, delayed delivery timestamp. 
     */
	public boolean isDelayed() {
		return isDelayed;
	}
}
